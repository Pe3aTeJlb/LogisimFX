package LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab;

import LogisimFX.IconsManager;
import LogisimFX.Main;
import LogisimFX.circuit.WireSet;
import LogisimFX.fpga.designrulecheck.SimpleDrcContainer;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.proj.Project;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.util.UndoUtils;
import org.fxmisc.undo.UndoManager;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Terminal extends VBox {

	private Project proj;
/*
	private PtyProcess process;
	private Path terminalPath;
	private String[] termCommand;
	private LinkedBlockingQueue<String> commandQueue;

	private ObjectProperty<Reader> inputReaderProperty;
	private ObjectProperty<Reader> errorReaderProperty;
	private ObjectProperty<Writer> outputWriterProperty;

	private String windowsTerminalStarter = "cmd.exe";
	private String unixTerminalStarter = "/bin/bash -i";
*/

	private TerminalHandler editHandler;
	private TerminalEditMenu menu;

	private VirtualizedScrollPane<?> virtualizedScrollPane;
	private ScaledVirtualized<StyleClassedTextArea> scaleVirtualized;
	private StyleClassedTextArea terminalArea;

	//Find bar
	private ToolBar findBar;
	private TextField findTxtFld;
	private SimpleStringProperty currFindIndex, totalFindIndex;
	private ArrayList<ArrayList<Integer>> coordinateList = new ArrayList<>();
	private AtomicInteger currWordIndex = new AtomicInteger(0);

	private String backgroundStyleClass = "terminal-background";
	private String infoStyleClass = "terminal-info";
	private String warningStyleClass = "terminal-warning";
	private String errorStyleClass = "terminal-error";

	public Terminal(Project project){

		super();

		this.proj = project;

		initFindBar();
		initTerminalArea();

		this.getChildren().addAll(findBar, virtualizedScrollPane);

		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				findBar.setVisible(false);

				findBar.setMinHeight(0);
				findBar.setMaxHeight(0);

				removeHighlightedTxt(coordinateList, terminalArea, currWordIndex);
				/*
				 * After closing the popup dialog the color of keywords is also changed.
				 * So in order to re-highlight the syntax I am appending and deleting the text to fire plaintext change event.
				 * I think, there are some better way, but until that I am going with this one.
				 */
				terminalArea.appendText(" ");
				int len = terminalArea.getText().length();
				terminalArea.deleteText(len-1, len);

			}
		});

		editHandler = new TerminalHandler(this);
		menu = new TerminalEditMenu(this);

		try {
			initializeProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void initTerminalArea(){

		terminalArea = new StyleClassedTextArea();
		scaleVirtualized = new ScaledVirtualized<>(terminalArea);

		virtualizedScrollPane = new VirtualizedScrollPane<>(scaleVirtualized);

		UndoManager<List<PlainTextChange>> um = UndoUtils.plainTextUndoManager(terminalArea);
		terminalArea.setUndoManager(um);

		VBox.setVgrow(virtualizedScrollPane, Priority.ALWAYS);

		terminalArea.addEventFilter(ScrollEvent.ANY, e -> {
			if (e.isControlDown()) {
				zoom(e.getDeltaY());
			}
		});

		terminalArea.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			Event.fireEvent(this, event.copyFor(event.getSource(), this));
		});

		IntFunction<Node> graphicFactory = line -> {

			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER_LEFT);

			Button button = new Button();
			button.setPrefSize(15,15);
			button.setMinSize(15,15);
			button.setMaxSize(15,15);
			button.getStyleClass().add(backgroundStyleClass);

			if (!drcContainers.isEmpty() && drcContainers.containsKey(line) && drcContainers.get(line).hasCircuit()){
				SimpleDrcContainer drc = drcContainers.get(line);
				button.setOnAction(event -> {
					if (curDRCContainer != null){
						if (!curDRCContainer.getDrcComponents().isEmpty()) {
							layoutEditor.getLayoutCanvas().setHighlightedComponent(null);
						}
						if (!curDRCContainer.getDrcWires().isEmpty()) {
							layoutEditor.getLayoutCanvas().setHighlightedWires(WireSet.EMPTY);
						}
					}
					generateDrcTrace(drc);
					curDRCContainer = drc;
				});
				button.setGraphic(IconsManager.getIcon("drclink.png"));
			}

			hbox.getChildren().add(button);

			return hbox;
		};
		terminalArea.setParagraphGraphicFactory(graphicFactory);

		terminalArea.setEditable(false);
		terminalArea.requestFocus();

		this.getStylesheets().add("/LogisimFX/resources/css/default.css");
		terminalArea.getStyleClass().add(backgroundStyleClass);

	}

	private void initFindBar(){

		currFindIndex = new SimpleStringProperty("0");
		totalFindIndex = new SimpleStringProperty("0");

		findBar = new ToolBar();
		findBar.setVisible(false);

		findTxtFld = new TextField();
		findTxtFld.textProperty().addListener(change -> find());

		Label findResultLbl = new Label();
		findResultLbl.textProperty().bind(
				Bindings.concat(
						currFindIndex,
						"/",
						totalFindIndex
				)
		);

		Button prevWordBt = new Button();
		prevWordBt.setGraphic(IconsManager.getImageView("arrowup.gif"));
		prevWordBt.setOnAction(event -> prevWord());

		Button nextWordBtn = new Button();
		nextWordBtn.setGraphic(IconsManager.getImageView("arrowdown.gif"));
		nextWordBtn.setOnAction(event -> nextWord());

		findBar.getItems().addAll(findTxtFld, findResultLbl, prevWordBt, nextWordBtn);

		findBar.setMinHeight(0);
		findBar.setMaxHeight(0);

	}

	public void find() {
		if (findTxtFld.getText().isEmpty()) return;
		highlightText(findTxtFld, coordinateList, currWordIndex, terminalArea);
		if (coordinateList.size()==0) return;
		totalFindIndex.set(String.valueOf(coordinateList.size()));
		currFindIndex.set(String.valueOf(currWordIndex.get()+1));
	}

	public void nextWord() {
		if (coordinateList.size()==0) return;
		gotoNextWord(coordinateList, currWordIndex, terminalArea);
		currFindIndex.set(String.valueOf(currWordIndex.get()+1));
	}

	private void gotoNextWord(ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea codeArea) {
		if (currWordIndex.get() >= (coordinateList.size()-1) && coordinateList.size()!=0) return;
		currWordIndex.incrementAndGet();
		int index = currWordIndex.get();
		codeArea.getCaretSelectionBind().moveTo(coordinateList.get(currWordIndex.get()).get(0));
		codeArea.setStyleClass(coordinateList.get(index).get(0), coordinateList.get(index).get(1), "findActive");
		removeHighlightedTxtInRange(coordinateList.get(index-1).get(0), coordinateList.get(index-1).get(1), codeArea);
	}

	public void prevWord() {
		if (coordinateList.size()==0) return;
		gotoPrevWord(coordinateList, currWordIndex, terminalArea);
		currFindIndex.set(String.valueOf(currWordIndex.get()+1));
	}

	private void gotoPrevWord(ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea codeArea) {
		if (currWordIndex.get() <= 0 && coordinateList.size()!=0) return;
		currWordIndex.decrementAndGet();
		int index = currWordIndex.get();
		codeArea.setStyleClass(coordinateList.get(index).get(0), coordinateList.get(index).get(1), "findActive");
		codeArea.getCaretSelectionBind().moveTo(coordinateList.get(currWordIndex.get()).get(0));
		removeHighlightedTxtInRange(coordinateList.get(index+1).get(0), coordinateList.get(index+1).get(1), codeArea);
	}


	//Text highlight

	public void removeHighlightedTxt(ArrayList<ArrayList<Integer>> coordinateList, StyleClassedTextArea currCodeArea, AtomicInteger currWordIndex) {
		if (coordinateList.size()!=0) {
			for (ArrayList<Integer> arrayList : coordinateList) {
				currCodeArea.setStyleClass(arrayList.get(0), arrayList.get(1), "");
			}
			coordinateList.clear();
			currWordIndex.set(0);
		}
	}

	public void removeHighlightedTxtInRange(int start, int end, StyleClassedTextArea currCodeArea) {
		currCodeArea.setStyleClass(start, end, "find");
	}

	public void highlightText(TextField textField, ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea currCodeArea) {
		removeHighlightedTxt(coordinateList, currCodeArea, currWordIndex);
		Pattern pattern = Pattern.compile("\\b("+textField.getText()+")\\b");
		Matcher matcher = pattern.matcher(currCodeArea.getText());
		while (matcher.find()) {
			currCodeArea.setStyleClass(matcher.start(), matcher.end(), "find");
			coordinateList.add(new ArrayList<>(Arrays.asList(matcher.start(), matcher.end())));
		}
		if (coordinateList.size()!=0) currCodeArea.getCaretSelectionBind().moveTo(coordinateList.get(0).get(0));
		currCodeArea.requestFollowCaret();
	}


	//Accelerator events

	public void cut() {
		if (terminalArea.getSelectedText().length()==0) terminalArea.selectLine();
		terminalArea.cut();
	}

	public void copy() {
		if (terminalArea.getSelectedText().length()==0) {
			terminalArea.selectLine();
			terminalArea.copy();
			return;
		}
		terminalArea.copy();
	}

	public void paste(){
		if (terminalArea.getSelectedText().length()==0) terminalArea.selectLine();
		terminalArea.paste();
	}

	public void openFindBar(){

		findBar.setVisible(true);
		if (terminalArea.getSelectedText() != null) findTxtFld.setText(terminalArea.getSelectedText());

		findBar.setMinHeight(-1);
		findBar.setMaxHeight(-1);

	}


	private void zoom(double delta){

		double scaleAmount = 0.9;
		Scale zoom = scaleVirtualized.getZoom();

		if (delta != 0) {

			double zoomVal =  delta > 0 ? zoom.getY() / scaleAmount : zoom.getY() * scaleAmount;
			if (zoomVal > 3) zoomVal = 3;
			if (zoomVal < 0.5) zoomVal = 0.5;
			zoom.setY(zoomVal);
			zoom.setX(zoomVal);

		}

	}

	public void zoomIn(){
		zoom(40);
	}

	public void zoomOut(){
		zoom(-40);
	}

	public void toDefaultZoom(){
		Scale zoom = scaleVirtualized.getZoom();
		zoom.setY(1);
		zoom.setX(1);
	}



	public StyleClassedTextArea getTerminalArea(){
		return terminalArea;
	}

	public List<MenuItem> getEditMenuItems(){
		return menu.getMenuItems();
	}

	public EditHandler getEditHandler(){
		return editHandler;
	}



	private void initializeProcess() throws Exception {

/*
		terminalPath = Paths.get(getJarPath());

		outputWriterProperty = new SimpleObjectProperty<>();
		commandQueue = new LinkedBlockingQueue<>();

		inputReaderProperty = new SimpleObjectProperty<>();
		errorReaderProperty = new SimpleObjectProperty<>();
		outputWriterProperty = new SimpleObjectProperty<>();

		inputReaderProperty.addListener((observable, oldValue, newValue) -> {
			ThreadHelper.start(() -> {
				printReader(newValue);
			});
		});

		errorReaderProperty.addListener((observable, oldValue, newValue) -> {
			ThreadHelper.start(() -> {
				printReader(newValue);
			});
		});

		if (SystemUtils.IS_OS_WINDOWS) {
			this.termCommand = windowsTerminalStarter.split("\\s+");
		} else {
			this.termCommand = unixTerminalStarter.split("\\s+");
		}

		final Map<String, String> envs = new HashMap<>(System.getenv());
		envs.put("TERM", "xterm");

		if (Objects.nonNull(terminalPath) && Files.exists(terminalPath)) {
			this.process = new PtyProcessBuilder().setCommand(termCommand).setEnvironment(envs).setDirectory(terminalPath.toString()).start();
		} else {
			this.process = new PtyProcessBuilder().setCommand(termCommand).setEnvironment(envs).start();
		}

		String defaultCharEncoding = System.getProperty("file.encoding");

		setInputReader(new BufferedReader(new InputStreamReader(process.getInputStream(), defaultCharEncoding)));
		setErrorReader(new BufferedReader(new InputStreamReader(process.getErrorStream(), defaultCharEncoding)));
		setOutputWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), defaultCharEncoding)));
*/
	}

/*
	public void execute(String command) {
		try {
			commandQueue.put(command);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		ThreadHelper.start(() -> {
			try {
				final String commandToExecute = commandQueue.poll();
				getOutputWriter().write(commandToExecute);
				getOutputWriter().flush();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void silentExecute(String command) {
		try {
			commandQueue.put(command);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		ThreadHelper.start(() -> {
			try {
				final String commandToExecute = commandQueue.poll();
				getOutputWriter().write(commandToExecute);
				getOutputWriter().flush();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});

		private void printReader(Reader bufferedReader) {
		try {
			int nRead;
			final char[] data = new char[1 * 1024];

			while ((nRead = bufferedReader.read(data, 0, data.length)) != -1) {
				final StringBuilder builder = new StringBuilder(nRead);
				builder.append(data, 0, nRead);
				System.out.println(builder.toString());
				print(builder.toString());
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected void print(String text) {
		ThreadHelper.runActionLater(() -> {
			this.getTerminalArea().insertText(getTerminalArea().getCaretPosition(), text);
		});
	}

	}*/

	public void execute(String command) {

		try {

			terminalArea.append(getJarPath()+">"+command+"\n", infoStyleClass);
			Process process = Runtime.getRuntime().exec(command);

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			input.lines().forEach(string -> terminalArea.append(string+"\n", infoStyleClass));
			errors.lines().forEach(string -> terminalArea.append(string+"\n", errorStyleClass));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Process silentExecute(String command) {
		try {
			return Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String getJarPath() {

		try {
			return Main.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath().substring(1).replace("/", File.separator).replace("\\", File.separator);
		} catch (Exception e) {

		}

		return "";

	}


	HashMap<Integer, SimpleDrcContainer> drcContainers = new HashMap<>();
	LayoutEditor layoutEditor;
	SimpleDrcContainer curDRCContainer = null;

	public void printError(Exception e){
		printError(e.getMessage());
	}

	public void printError(SimpleDrcContainer error){
		drcContainers.put(terminalArea.getCurrentParagraph(), error);
		printError(error.toString());
	}

	public void printError(String error){
		terminalArea.append(error+"\n", errorStyleClass);
	}

	public void printWarning(SimpleDrcContainer warning){
		drcContainers.put(terminalArea.getCurrentParagraph(), warning);
		printWarning(warning.toString());
	}

	public void printWarning(String warning){
		terminalArea.append(warning+"\n", warningStyleClass);
	}

	public void printInfo(SimpleDrcContainer info){
		drcContainers.put(terminalArea.getCurrentParagraph(), info);
		printInfo(info.toString());
	}

	public void printInfo(String info){
		terminalArea.append(info+"\n", infoStyleClass);
	}

	public void clearTerminal(){
		terminalArea.clear();
		drcContainers.clear();
	}

	private void generateDrcTrace(SimpleDrcContainer dc) {

		if (dc.hasCircuit()) {

			proj.getFrameController().selectCircLayoutEditor(dc.getCircuit());
			layoutEditor = (LayoutEditor) proj.getFrameController().getEditor();

			if (dc.getDrcComponents() != null && !dc.getDrcComponents().isEmpty()) {
				layoutEditor.getLayoutCanvas().setHighlightedComponents(new ArrayList<>(dc.getDrcComponents()));
			}

			if (dc.getDrcWires() != null && !dc.getDrcWires().isEmpty()) {
				layoutEditor.getLayoutCanvas().setHighlightedWires(new WireSet(dc.getDrcWires()));
			}

		}

	}

/*
	public ObjectProperty<Reader> inputReaderProperty() {
		return inputReaderProperty;
	}

	public Reader getInputReader() {
		return inputReaderProperty.get();
	}

	public void setInputReader(Reader reader) {
		inputReaderProperty.set(reader);
	}


	public ObjectProperty<Reader> errorReaderProperty() {
		return errorReaderProperty;
	}

	public Reader getErrorReader() {
		return errorReaderProperty.get();
	}

	public void setErrorReader(Reader reader) {
		errorReaderProperty.set(reader);
	}


	public ObjectProperty<Writer> outputWriterProperty() {
		return outputWriterProperty;
	}

	public Writer getOutputWriter() {
		return outputWriterProperty.get();
	}

	public void setOutputWriter(Writer writer) {
		outputWriterProperty.set(writer);
	}
*/
/*
	@Override
	public void copyAccelerators(){
		if (this.getScene() != proj.getFrameController().getStage().getScene()){
			this.getScene().getAccelerators().putAll(
					proj.getFrameController().getStage().getScene().getAccelerators()
			);
		}
	}
*/

	public void terminateListeners(){
		/*
		if (process != null) {
			System.out.println(process.isAlive());
			try {
				process.getErrorStream().close();
				process.getInputStream().close();
				process.getOutputStream().close();
				process.destroy();
				System.out.println(process.waitFor(1, TimeUnit.SECONDS));
				System.out.println(process.isAlive() + " ");
				System.out.println(process.exitValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			process = null;
		}*/
	}

}

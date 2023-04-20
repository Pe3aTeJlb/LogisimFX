/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentEvent;
import LogisimFX.comp.ComponentListener;
import LogisimFX.comp.EndData;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeEvent;
import LogisimFX.data.Location;
import LogisimFX.file.LogisimFile;
import LogisimFX.fpga.file.FileWriter;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.AutoCompletion.AutoCompletion;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.AutoCompletion.AutoCompletionWords;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.proj.Project;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.util.UndoUtils;
import org.fxmisc.undo.UndoManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends EditorBase {

    private CodeEditHandler editHandler;
    private CodeEditorEditMenu menu;

    private VirtualizedScrollPane<?> virtualizedScrollPane;
    private ScaledVirtualized<StyleClassedTextArea> scaleVirtualized;
    private StyleClassedTextArea codeArea;

    private Popup autoCompletionPopup;
    private ListView<String> autoCompletionList;


    //Find&Searc bar
    private InvalidationListener sceneListener;
    private ToolBar codeEditorToolBar;
    private ToolBar findBar, replaceBar;
    private TextField findTxtFld, replaceTxtFld;
    private SimpleStringProperty currFindIndex, totalFindIndex;
    private ArrayList<ArrayList<Integer>> coordinateList = new ArrayList<>();


    //Bottom info bar
    private HBox footBar;
    private Label selectTextLabel;
    private SimpleStringProperty lineNum, colNum, selectedTextNum;
    private AtomicInteger currWordIndex = new AtomicInteger(0);

    private Component comp;
    private File file;

    public CodeEditor(Project project, Circuit circ, Component comp){

        super(project, circ);

        this.comp = comp;

        initFindReplaceBar();
        initCodeArea("v");
        initFootBar();

        codeEditorToolBar = new CodeEditorToolBar();
        codeEditorToolBar.setOnMousePressed(event -> Event.fireEvent(this, event.copyFor(event.getSource(), this)));

        this.getChildren().addAll(codeEditorToolBar, findBar, replaceBar, virtualizedScrollPane, footBar);

        proj.getFrameController().editorProperty().addListener((observableValue, editorBase, t1) -> {
            if (this.isSelected()){
                recalculateAccelerators();
            }
        });

        //this.sceneProperty().addListener(sceneListener = (change) -> this.recalculateAccelerators());

        editHandler = new CodeEditHandler(this);
        menu = new CodeEditorEditMenu(this);

        codeArea.requestFocus();


        StringBuilder builder = new StringBuilder();

        for(String s: comp.getFactory().getHDLGenerator(comp.getAttributeSet()).getArchitecture(
                circ.getNetList(), comp.getAttributeSet(), comp.getFactory().getHDLName(comp.getAttributeSet()))) {
            builder.append(s).append("\n");
        }

        codeArea.insertText(0, builder.toString());

        codeArea.setEditable(false);

    }

    public CodeEditor(Project project, Circuit circ, File file){

        super(project, circ);

        this.file = file;

        circ.registerProject(proj);

        initFindReplaceBar();
        initCodeArea(file.getName().split("\\.")[1]);
        initFootBar();

        codeEditorToolBar = new CodeEditorToolBar();
        codeEditorToolBar.setOnMousePressed(event -> Event.fireEvent(this, event.copyFor(event.getSource(), this)));

        this.getChildren().addAll(codeEditorToolBar, findBar, replaceBar, virtualizedScrollPane, footBar);

        proj.getFrameController().editorProperty().addListener((observableValue, editorBase, t1) -> {
            if (this.isSelected()){
                recalculateAccelerators();
            }
        });

        //this.sceneProperty().addListener(sceneListener = (change) -> this.recalculateAccelerators());

        editHandler = new CodeEditHandler(this);
        menu = new CodeEditorEditMenu(this);

        codeArea.requestFocus();

        if (file.length() > 0) {

            try {
                codeArea.insertText(0, Files.readString(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            StringBuilder builder = new StringBuilder();
            SubcircuitFactory factory = circ.getSubcircuitFactory();
            for (String s : ((CircuitHdlGeneratorFactory) factory.getHDLGenerator(circ.getStaticAttributes())).getModuleFunctionality(
                    circ.getNetList(), circ.getStaticAttributes()
            ).get()) {
                builder.append(s).append("\n");
            }

            codeArea.insertText(0, builder.toString());

        }

    }

    public CodeEditor(Project project, File file){

        super(project, null);

        this.file = file;

        initFindReplaceBar();
        initCodeArea("");
        initFootBar();

        codeEditorToolBar = new CodeEditorToolBar();
        codeEditorToolBar.setOnMousePressed(event -> Event.fireEvent(this, event.copyFor(event.getSource(), this)));

        this.getChildren().addAll(codeEditorToolBar, findBar, replaceBar, virtualizedScrollPane, footBar);

        proj.getFrameController().editorProperty().addListener((observableValue, editorBase, t1) -> {
            if (this.isSelected()){
                recalculateAccelerators();
            }
        });

        //this.sceneProperty().addListener(sceneListener = (change) -> this.recalculateAccelerators());

        editHandler = new CodeEditHandler(this);
        menu = new CodeEditorEditMenu(this);

        codeArea.requestFocus();


        if (file.length() > 0) {
            try {
                codeArea.insertText(0, Files.readString(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getEditorDescriptor(){

        if (comp != null){
            return circ.getName() + " " + comp.getFactory().getName() + " " + comp.getLocation().toString();
        } else if (circ != null){
            return circ.getHDLFile(proj, file.getName()).toString().split(proj.getLogisimFile().getProjectDir().getFileName().toString())[1].substring(1);
        } else {
            return file.toString().split(proj.getLogisimFile().getProjectDir().getFileName().toString())[1].substring(1);
        }

    }

    public void doSave(){

        FileOutputStream writer;

        if (comp == null && circ != null){
            try {
                File f = circ.getHDLFile(proj, file.getName());
                if (!f.exists()){
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
                writer = new FileOutputStream(f);
                writer.write(codeArea.getText().getBytes());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                DialogManager.createStackTraceDialog("Error!", "Error during saving code editor content " + file.getName(), e);
                e.printStackTrace();
            }
        } else {
            try {
                if (!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                writer = new FileOutputStream(file);
                writer.write(codeArea.getText().getBytes());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                DialogManager.createStackTraceDialog("Error!", "Error during saving code editor content " + file.getName(), e);
                e.printStackTrace();
            }
        }

    }

    private void initCodeArea(String ext){

        autoCompletionPopup = new Popup();
        autoCompletionPopup.setAutoHide(true);
        autoCompletionPopup.setHideOnEscape(true);

        codeArea = new StyleClassedTextArea();
        scaleVirtualized = new ScaledVirtualized<>(codeArea);
        virtualizedScrollPane = new VirtualizedScrollPane<>(scaleVirtualized);
        IntFunction<Node> noFactory = LineNumberFactory.get(codeArea);
        IntFunction<Node> graphicFactory = line -> {
            HBox lineBox = new HBox(noFactory.apply(line));
            lineBox.getStyleClass().add("lineno-box");
            lineBox.setAlignment(Pos.CENTER_LEFT);
            return lineBox;
        };
        UndoManager<List<PlainTextChange>> um = UndoUtils.plainTextUndoManager(codeArea);
        codeArea.setUndoManager(um);

        new SyntaxHighlighter(codeArea).start(ext);
        autoIndent(codeArea);  // auto-indent: insert previous line's indents on enter
        configureAllShortcuts(codeArea);
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(x -> {
            autoCompletion(getCurrWord(codeArea), codeArea);
            //fileModified(tab, codeArea);
        });

        setSelectionListener(codeArea);
        codeArea.caretPositionProperty().addListener((observableValue, oldPos, newPos) -> updateCaret(codeArea));
        codeArea.setParagraphGraphicFactory(graphicFactory);

        VBox.setVgrow(virtualizedScrollPane, Priority.ALWAYS);

        codeArea.getStyleClass().add("styled-text-area");

        codeArea.addEventFilter(ScrollEvent.ANY, e -> {
            if (e.isControlDown()) {
                zoom(e.getDeltaY());
            }
        });

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



    public Component getComp(){
        return comp;
    }

    public File getOpenedFile(){
        return file;
    }


    private void autoIndent(StyleClassedTextArea area) {
        area.addEventHandler( KeyEvent.KEY_PRESSED, KE -> {
            if (KE.getCode() == KeyCode.ENTER && !KE.isControlDown()) indent(area);
        });
    }

    private void configureAllShortcuts(StyleClassedTextArea codeArea) {
        final KeyCombination showCompletionComb = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlEnterComb = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        codeArea.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (showCompletionComb.match(event)) {
                autoCompletion(getCurrWord(codeArea), codeArea);
            } else if (ctrlEnterComb.match(event)) {
                codeArea.selectLine();
                IndexRange range = codeArea.getCaretSelectionBind().getRange();
                codeArea.insertText(range.getEnd(), "\n");
                indent(codeArea);
            }
        });
    }

    private void setSelectionListener(StyleClassedTextArea area) {
        area.selectedTextProperty().addListener((observableValue, s, t1) -> {
            selectTextLabel.setVisible(true);
            selectedTextNum.set(String.valueOf(t1.length()));
            if (t1.length()==0) selectTextLabel.setVisible(false);
        });
    }

    private void indent(StyleClassedTextArea area) {
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        int caretPosition = area.getCaretPosition();
        int currentParagraph = area.getCurrentParagraph();
        Matcher m0 = whiteSpace.matcher(area.getParagraph(currentParagraph-1).getSegments().get(0));
        if (m0.find()) area.insertText(caretPosition, m0.group());
    }



    private void autoCompletion(String toSearch, StyleClassedTextArea currCodeArea) {
        if (toSearch.length()==0) {
            autoCompletionPopup.hide();
            return;
        }
        List<String> words = new AutoCompletionWords().getWords("verilog");
        if (words==null) return;
        AutoCompletion completion = new AutoCompletion(words);
        showCompletion(completion.suggest(toSearch), currCodeArea, toSearch.length());
    }

    public void insertCompletion(String s, StyleClassedTextArea currCodeArea, int wordLen) {
        currCodeArea.replaceText(currCodeArea.getCaretPosition()-wordLen, currCodeArea.getCaretPosition(), s);
        autoCompletionPopup.hide();
    }

    private void showCompletion(List<String> list, StyleClassedTextArea currCodeArea, int wordLen) {

        if (list.size()>0) {

            autoCompletionList = new ListView<>();
            autoCompletionList.setPrefWidth(360);
            autoCompletionList.getItems().addAll(list);
            autoCompletionList.prefHeightProperty().bind(Bindings.size(autoCompletionList.getItems()).multiply(31));
            autoCompletionList.getSelectionModel().selectFirst();

            // set listeners
            autoCompletionList.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) insertCompletion(autoCompletionList.getSelectionModel().getSelectedItem(), currCodeArea, wordLen);
            });
            autoCompletionList.setOnMouseClicked(event -> {
                if (autoCompletionList.getSelectionModel().getSelectedItem()!=null) insertCompletion(autoCompletionList.getSelectionModel().getSelectedItem(), currCodeArea, wordLen);
            });

            // replacing old data with new one.
            autoCompletionPopup.getContent().clear();
            autoCompletionPopup.getContent().add(autoCompletionList);
            autoCompletionPopup.show(proj.getFrameController().getStage(), currCodeArea.getCaretBounds().get().getMaxX(), currCodeArea.getCaretBounds().get().getMaxY());

        } else {

            if (autoCompletionPopup != null) {
                autoCompletionPopup.getContent().clear();
                autoCompletionPopup.hide();
            }

        }

    }



    private void updateCaret(StyleClassedTextArea codeArea) {
        lineNum.set(Integer.toString(codeArea.getCaretSelectionBind().getParagraphIndex()+1));
        colNum.set(Integer.toString(codeArea.getCaretSelectionBind().getColumnPosition()+1));
    }

    private String getCurrWord(StyleClassedTextArea currCodeArea) {
        Set<Character> charSet = new HashSet<>();
        charSet.add('}');
        charSet.add('{');
        charSet.add(']');
        charSet.add('[');
        charSet.add(')');
        charSet.add('(');
        StringBuilder word = new StringBuilder();
        for (int i = currCodeArea.getCaretPosition(); i>0; i--) {
            char ch = currCodeArea.getText().charAt(i-1);
            if (ch == ' ' || ch == '\n' || charSet.contains(ch)) break;
            else word.append(ch);
        }

        return word.reverse().toString();
    }



    private void initFootBar(){

        lineNum = new SimpleStringProperty("1");
        colNum = new SimpleStringProperty("1");
        selectedTextNum = new SimpleStringProperty("");

        footBar = new HBox();
        footBar.setAlignment(Pos.CENTER_RIGHT);
        footBar.setSpacing(5);

        selectTextLabel = new Label();
        HBox.setHgrow(selectTextLabel, Priority.ALWAYS);
        selectTextLabel.setVisible(false);
        selectTextLabel.textProperty().bind(selectedTextNum.concat(" ").concat(LC.createStringBinding("codeAreaSelectedTextInfo")));

        Label lineColInfo = new Label();
        HBox.setHgrow(lineColInfo, Priority.ALWAYS);
        lineColInfo.textProperty().bind(lineNum.concat(":").concat(colNum));
        
        Label tabInfo = new Label();
        HBox.setHgrow(tabInfo, Priority.ALWAYS);
        tabInfo.textProperty().bind(LC.createComplexStringBinding("codeAreaIndentInfo", Integer.toString(4)));

        footBar.getChildren().addAll(selectTextLabel, lineColInfo, tabInfo);

    }



    private void initFindReplaceBar(){

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
        //prevWordBt.disableProperty().bind(coordinateList.s);
        prevWordBt.setGraphic(IconsManager.getImageView("projup.gif"));
        prevWordBt.setOnAction(event -> prevWord());

        Button nextWordBtn = new Button();
        //nextWordBtn.disableProperty().bind();
        nextWordBtn.setGraphic(IconsManager.getImageView("projdown.gif"));
        nextWordBtn.setOnAction(event -> nextWord());

        findBar.getItems().addAll(findTxtFld, findResultLbl, prevWordBt, nextWordBtn);


        replaceBar = new ToolBar();
        replaceBar.setVisible(false);

        replaceTxtFld = new TextField();

        Button replaceBtn = new Button();
        replaceBtn.textProperty().bind(LC.createStringBinding("codeAreaReplaceBtn"));
        replaceBtn.setOnAction(event -> replace());

        Button replaceAllBtn = new Button();
        replaceAllBtn.textProperty().bind(LC.createStringBinding("codeAreaReplaceAllBtn"));
        replaceAllBtn.setOnAction(event -> replaceAll());

        findBar.setMinHeight(0);
        findBar.setMaxHeight(0);
        replaceBar.setMinHeight(0);
        replaceBar.setMaxHeight(0);

        replaceBar.getItems().addAll(replaceTxtFld, replaceBtn, replaceAllBtn);

    }

    private void find() {
        if (findTxtFld.getText().isEmpty()) return;
        highlightText(findTxtFld, coordinateList, currWordIndex, codeArea);
        if (coordinateList.size()==0) return;
        totalFindIndex.set(String.valueOf(coordinateList.size()));
        currFindIndex.set(String.valueOf(currWordIndex.get()+1));
    }


    private void nextWord() {
        if (coordinateList.size()==0) return;
        gotoNextWord(coordinateList, currWordIndex, codeArea);
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


    private void prevWord() {
        if (coordinateList.size()==0) return;
        gotoPrevWord(coordinateList, currWordIndex, codeArea);
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


    private void replace() {
        if (coordinateList.size()==0) return;
        codeArea.replaceText(coordinateList.get(currWordIndex.get()).get(0), coordinateList.get(currWordIndex.get()).get(1), replaceTxtFld.getText());
        highlightText(findTxtFld, coordinateList, currWordIndex, codeArea);
        totalFindIndex.set(String.valueOf(coordinateList.size()));
    }

    private void replaceAll() {
        if (coordinateList.size()==0) return;
        codeArea.replaceText(codeArea.getText().replaceAll("\\b(" + findTxtFld.getText() + ")\\b", replaceTxtFld.getText()));
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

    public void undo() {
        codeArea.undo();
    }

    public void redo() {
        codeArea.redo();
    }

    void cut() {
        if (codeArea.getSelectedText().length()==0) codeArea.selectLine();
        codeArea.cut();
    }

    void copy() {
        if (codeArea.getSelectedText().length()==0) {
            codeArea.selectLine();
            codeArea.copy();
            return;
        }
        codeArea.copy();
    }

    void paste(){
        if (codeArea.getSelectedText().length()==0) codeArea.selectLine();
        codeArea.paste();
    }

    void delete(){
        codeArea.deleteText(codeArea.getSelection());
    }

    void duplicate(){
        if (codeArea.getSelectedText().length()==0) return;
        codeArea.insertText(codeArea.getCaretPosition(), codeArea.getSelectedText());
    }

    void selectAll(){
        codeArea.selectAll();
    }

    void openFindBar(){

        findBar.setVisible(true);
        if (codeArea.getSelectedText() != null) findTxtFld.setText(codeArea.getSelectedText());

        replaceBar.setVisible(false);
        findBar.setMinHeight(-1);
        findBar.setMaxHeight(-1);
        replaceBar.setMinHeight(0);
        replaceBar.setMaxHeight(0);

    }

    void openReplaceBar(){

        findBar.setVisible(true);
        if (codeArea.getSelectedText() != null && findTxtFld.getText().equals("")){
            findTxtFld.setText(codeArea.getSelectedText());
        }

        replaceBar.setVisible(true);
        findBar.setMinHeight(-1);
        findBar.setMaxHeight(-1);
        replaceBar.setMinHeight(-1);
        replaceBar.setMaxHeight(-1);

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


    public StyleClassedTextArea getCodeArea(){
        return codeArea;
    }

    public List<MenuItem> getEditMenuItems(){
        return menu.getMenuItems();
    }

    public EditHandler getEditHandler(){
        return editHandler;
    }

    public void recalculateAccelerators(){

        if (this.getScene() == null) return;

        this.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE),
                new Runnable() {
                    @FXML
                    public void run() {
                        findBar.setVisible(false);
                        replaceBar.setVisible(false);

                        findBar.setMinHeight(0);
                        findBar.setMaxHeight(0);
                        replaceBar.setMinHeight(0);
                        replaceBar.setMaxHeight(0);

                        removeHighlightedTxt(coordinateList, codeArea, currWordIndex);
                        /*
                         * After closing the popup dialog the color of keywords is also changed.
                         * So in order to re-highlight the syntax I am appending and deleting the text to fire plaintext change event.
                         * I think, there are some better way, but until that I am going with this one.
                         */
                        codeArea.appendText(" ");
                        int len = codeArea.getText().length();
                        codeArea.deleteText(len-1, len);

                    }
                }
        );

    }

    @Override
    public void copyAccelerators(){
        if (this.getScene() != proj.getFrameController().getStage().getScene()){
            this.getScene().getAccelerators().putAll(
                    proj.getFrameController().getStage().getScene().getAccelerators()
            );
        }
        recalculateAccelerators();
    }

}

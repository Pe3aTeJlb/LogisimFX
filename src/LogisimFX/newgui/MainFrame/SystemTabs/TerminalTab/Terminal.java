package LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab;

import LogisimFX.Main;
import LogisimFX.circuit.WireSet;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.proj.Project;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.paint.Color;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLIFrameElement;
import terminalfx.config.TerminalConfig;

import java.io.*;
import java.util.*;

public class Terminal extends terminalfx.Terminal {

	private Project proj;

	private String infoStyleClass = "terminal-info";
	private String warningStyleClass = "terminal-warning";
	private String errorStyleClass = "terminal-error";

	private Document doc;
	private Document iframeDoc;
	private Element terminalContentRoot;
	private JSBridge jsBridge;

	public Terminal(Project project) {

		super();
		this.proj = project;

		jsBridge = new JSBridge();

		TerminalConfig darkConfig = new TerminalConfig();
		darkConfig.setBackgroundColor(Color.rgb(16, 16, 16));
		darkConfig.setForegroundColor(Color.rgb(240, 240, 240));
		darkConfig.setCursorColor(Color.rgb(255, 0, 0, 0.5));

		this.setTerminalConfig(darkConfig);
/*
		webEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				System.out.println("succeded");
				jsBridge = new JSBridge();
				getTerminal().setMember("JSBridge", jsBridge);
			}
		});

 */

	}

	public Project getProj() {
		return proj;
	}



	//non-PTY stream print methods

	ArrayList<TerminalMessageContainer> drcContainers = new ArrayList<>();
	LayoutEditor layoutEditor;
	TerminalMessageContainer curDRCContainer = null;

	public void printError(Exception e) {
		printError(e.getMessage());
	}

	public void printError(TerminalMessageContainer error) {
		terminalAppendDRC(error, errorStyleClass);
	}

	public void printError(String error) {
		terminalAppendString(error, errorStyleClass);
	}

	public void printWarning(TerminalMessageContainer warning) {
		terminalAppendDRC(warning, warningStyleClass);
	}

	public void printWarning(String warning) {
		terminalAppendString(warning, warningStyleClass);
	}

	public void printInfo(TerminalMessageContainer info) {
		terminalAppendDRC(info, infoStyleClass);
	}

	public void printInfo(String info) {
		terminalAppendString(info, infoStyleClass);
	}

	private void terminalAppendString(String msg, String style){

		if (terminalContentRoot != null){
			terminalfx.helper.ThreadHelper.awaitLatch(countDownLatch);
			terminalfx.helper.ThreadHelper.runActionLater(() -> {
				getTerminalIO().call("printlnStyled", msg.trim(), style);
			});
		} else {
			initTerminalDOMPointers();
			terminalAppendString(msg, style);
		}

	}

	private void terminalAppendDRC(TerminalMessageContainer drc, String style){

		if (terminalContentRoot != null){
			if (drc.hasCircuit() || drc.hasFile()){
				drcContainers.add(drc);
				terminalfx.helper.ThreadHelper.awaitLatch(countDownLatch);
				terminalfx.helper.ThreadHelper.runActionLater(() -> {
					getTerminalIO().call("printlnDrc", drc.toString().trim(), style, drcContainers.size()-1);
				});
			} else {
				terminalAppendString(drc.toString(), style);
			}
		} else {
			initTerminalDOMPointers();
			terminalAppendDRC(drc, style);
		}

	}

	public void clearTerminal() {
		terminalfx.helper.ThreadHelper.awaitLatch(countDownLatch);
		terminalfx.helper.ThreadHelper.runActionLater(() -> {
			getTerminal().call("clearHome");
		});
		drcContainers.clear();
	}





	private void initTerminalDOMPointers(){

		doc = webEngine().getDocument();
		HTMLIFrameElement iframe = (HTMLIFrameElement) doc.getElementsByTagName("iframe").item(0);
		iframeDoc = iframe.getContentDocument();

		terminalContentRoot = iframeDoc.getElementById("hterm:row-nodes");

		getWindow().setMember("JSBridge", jsBridge);

	}

	public class JSBridge{

		public void generateDrcTrace(int drc) {

			TerminalMessageContainer dc = drcContainers.get(drc);

			if (curDRCContainer != null){
				if (curDRCContainer.getDrcComponents() != null && !curDRCContainer.getDrcComponents().isEmpty()) {
					layoutEditor.getLayoutCanvas().setHighlightedComponent(null);
				}
				if (curDRCContainer.getDrcWires() != null && !curDRCContainer.getDrcWires().isEmpty()) {
					layoutEditor.getLayoutCanvas().setHighlightedWires(WireSet.EMPTY);
				}
			}

			curDRCContainer = dc;

			if (dc.hasCircuit()) {

				proj.getFrameController().addCircLayoutEditor(dc.getCircuit());
				layoutEditor = (LayoutEditor) proj.getFrameController().getEditor();

				if (dc.getDrcComponents() != null && !dc.getDrcComponents().isEmpty()) {
					layoutEditor.getLayoutCanvas().setHighlightedComponents(new ArrayList<>(dc.getDrcComponents()));
				}

				if (dc.getDrcWires() != null && !dc.getDrcWires().isEmpty()) {
					layoutEditor.getLayoutCanvas().setHighlightedWires(new WireSet(dc.getDrcWires()));
				}

			}

			if (dc.hasFile()) {
				System.out.println("code");
				proj.getFrameController().addCodeEditor(dc.getFile());
			}

		}

	}


	public void execute(String command) {
		System.out.println("command " + command);
		byte[] enter = new byte[1];
		enter[0] = getProcess().getEnterKeyCode();
		command += new String(enter);
		String finalCommand = command;
		ThreadHelper.start(() -> {
			try {
				getOutputWriter().write(finalCommand);
				getOutputWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void executeAsNonPty(String command) {

		try {

			Process process = Runtime.getRuntime().exec(command);

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));

/*
			terminalArea.append(getJarPath() + ">" + command + "\n", infoStyleClass);
			input.lines().forEach(string -> terminalArea.append(string + "\n", infoStyleClass));
			errors.lines().forEach(string -> terminalArea.append(string + "\n", errorStyleClass));
*/
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Process silentExecuteAsNonPty(String command) {
		try {
			return Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	//Working Directory

	public static String getJarPath() {

		try {
			return Main.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath().substring(1).replace("/", File.separator).replace("\\", File.separator);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";

	}

}

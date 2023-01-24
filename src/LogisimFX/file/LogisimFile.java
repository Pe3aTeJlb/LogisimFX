/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.newgui.FrameManager;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.util.EventSourceWeakSupport;
import LogisimFX.util.ListUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class LogisimFile extends Library implements LibraryEventSource {

	private static class WritingThread extends Thread {

		OutputStream out;
		LogisimFile file;

		WritingThread(OutputStream out, LogisimFile file) {
			this.out = out;
			this.file = file;
		}

		@Override
		public void run() {

			try {
				file.write(out, file.loader);
			} catch (IOException e) {
				file.loader.showError(LC.getFormatted("fileDuplicateError", e.toString()));
			}
			try {
				out.close();
			} catch (IOException e) {
				file.loader.showError(LC.getFormatted("fileDuplicateError", e.toString()));
			}

		}

	}

	private EventSourceWeakSupport<LibraryListener> listeners = new EventSourceWeakSupport<>();
	private Loader loader;
	private LinkedList<String> messages = new LinkedList<>();
	private Options options = new Options();
	private LinkedList<AddTool> tools = new LinkedList<>();
	private LinkedList<Library> libraries = new LinkedList<>();
	private Circuit main = null;
	private Circuit current = null;
	private String name;
	private boolean dirty = false;

	public SimpleStringProperty obsPos = new SimpleStringProperty("undefined2");
	public SimpleBooleanProperty isMain = new SimpleBooleanProperty(false);

	LogisimFile(Loader loader) {

		this.loader = loader;

		name = LC.get("defaultProjectName");
		if (FrameManager.windowNamed(name)) {
			for (int i = 2; true; i++) {
				if (!FrameManager.windowNamed(name + " " + i)) {
					name += " " + i;
					break;
				}
			}
		}

	}

	//
	// access methods
	//
	@Override
	public String getName() { return name; }

	@Override
	public boolean isDirty() { return dirty; }

	public String getMessage() {
		if (messages.size() == 0) return null;
		return messages.removeFirst();
	}

	public Loader getLoader() {
		return loader;
	}

	public Options getOptions() {
		return options;
	}

	@Override
	public List<AddTool> getTools() {
		return tools;
	}

	@Override
	public List<Library> getLibraries() {
		return libraries;
	}

	@Override
	public List<?> getElements() {
		return ListUtil.joinImmutableLists(tools, libraries);
	}

	public Circuit getCircuit(String name) {
		if (name == null) return null;
		for (AddTool tool : tools) {
			SubcircuitFactory factory = (SubcircuitFactory) tool.getFactory();
			if (name.equals(factory.getName())) return factory.getSubcircuit();
		}
		return null;
	}

	public boolean contains(Circuit circ) {
		for (AddTool tool : tools) {
			SubcircuitFactory factory = (SubcircuitFactory) tool.getFactory();
			if (factory.getSubcircuit() == circ) return true;
		}
		return false;
	}

	public List<Circuit> getCircuits() {

		List<Circuit> ret = new ArrayList<>(tools.size());
		for (AddTool tool : tools) {
			SubcircuitFactory factory = (SubcircuitFactory) tool.getFactory();
			ret.add(factory.getSubcircuit());
		}
		return ret;

	}

	public AddTool getAddTool(Circuit circ) {

		for (AddTool tool : tools) {
			SubcircuitFactory factory = (SubcircuitFactory) tool.getFactory();
			if (factory.getSubcircuit() == circ) {
				return tool;
			}
		}
		return null;

	}

	public Circuit getMainCircuit() {
		return main;
	}

	public int getCircuitCount() {
		return tools.size();
	}

	//
	// listener methods
	//
	public void addLibraryListener(LibraryListener what) {
		listeners.add(what);
	}

	public void removeLibraryListener(LibraryListener what) {
		listeners.remove(what);
	}

	private void fireEvent(int action, Object data) {
		LibraryEvent e = new LibraryEvent(this, action, data);
		for (LibraryListener l : listeners) {
			l.libraryChanged(e);
		}
	}


	//
	// modification actions
	//
	public void addMessage(String msg) {
		messages.addLast(msg);
	}

	public void setDirty(boolean value) {

		if (dirty != value) {
			dirty = value;
			fireEvent(LibraryEvent.DIRTY_STATE, value ? Boolean.TRUE : Boolean.FALSE);
		}

	}

	public void setName(String name) {

		this.name = name;
		fireEvent(LibraryEvent.SET_NAME, name);

	}

	public void addCircuit(Circuit circuit) {
		addCircuit(circuit, tools.size());
	}

	public void addCircuit(Circuit circuit, int index) {

		AddTool tool = new AddTool(circuit.getSubcircuitFactory());
		tools.add(index, tool);
		if (tools.size() == 1) setMainCircuit(circuit);
		fireEvent(LibraryEvent.ADD_TOOL, tool);

		updateCircuitPos();

	}

	public void removeCircuit(Circuit circuit) {

		if (tools.size() <= 1) {
			throw new RuntimeException("Cannot remove last circuit");
		}

		int index = getCircuits().indexOf(circuit);
		if (index >= 0) {
			Tool circuitTool = tools.remove(index);

			if (main == circuit) {
				AddTool dflt_tool = tools.get(0);
				SubcircuitFactory factory = (SubcircuitFactory) dflt_tool.getFactory();
				setMainCircuit(factory.getSubcircuit());
			}
			fireEvent(LibraryEvent.REMOVE_TOOL, circuitTool);
		}

		updateCircuitPos();

	}

	public void moveCircuit(AddTool tool, int index) {

		int oldIndex = tools.indexOf(tool);

		if (oldIndex < 0) {
			tools.add(index, tool);
			fireEvent(LibraryEvent.ADD_TOOL, tool);
		} else {
			AddTool value = tools.remove(oldIndex);
			tools.add(index, value);
			fireEvent(LibraryEvent.MOVE_TOOL, tool);
		}

		updateCircuitPos();

	}

	public void addLibrary(Library lib) {

		libraries.add(lib);
		fireEvent(LibraryEvent.ADD_LIBRARY, lib);

	}

	public void removeLibrary(Library lib) {

		libraries.remove(lib);
		fireEvent(LibraryEvent.REMOVE_LIBRARY, lib);

	}

	public String getUnloadLibraryMessage(Library lib) {

		HashSet<ComponentFactory> factories = new HashSet<>();
		for (Tool tool : lib.getTools()) {
			if (tool instanceof AddTool) {
				factories.add(((AddTool) tool).getFactory());
			}
		}

		for (Circuit circuit : getCircuits()) {
			for (Component comp : circuit.getNonWires()) {
				if (factories.contains(comp.getFactory())) {
					return LC.getFormatted("unloadUsedError",
							circuit.getName());
				}
			}
		}

		ToolbarData tb = options.getToolbarData();
		MouseMappings mm = options.getMouseMappings();
		for (Tool t : lib.getTools()) {
			if (tb.usesToolFromSource(t)) {
				return LC.get("unloadToolbarError");
			}
			if (mm.usesToolFromSource(t)) {
				return LC.get("unloadMappingError");
			}
		}

		return null;
	}

	public void setMainCircuit(Circuit circuit) {

		if (circuit == null) return;
		this.main = circuit;
		fireEvent(LibraryEvent.SET_MAIN, circuit);

		isMain.setValue(true);
		updateCircuitPos();

	}

	public void setCurrent(Circuit circ){

		current = circ;

		if(current != main){
			isMain.setValue(false);
		}else{
			isMain.setValue(true);
		}

		updateCircuitPos();

	}

	//
	// other methods
	//
	void write(OutputStream out, LibraryLoader loader) throws IOException {

		try {
			XmlWriter.write(this, out, loader);
		} catch (TransformerConfigurationException e) {
			loader.showError("internal error configuring transformer");
		} catch (ParserConfigurationException e) {
			loader.showError("internal error configuring parser");
		} catch (TransformerException e) {
			String msg = e.getMessage();
			String err = LC.get("xmlConversionError");
			if (msg == null) err += ": " + msg;
			loader.showError(err);
		}

	}

	public LogisimFile cloneLogisimFile(Loader newloader) {
		PipedInputStream reader = new PipedInputStream();
		PipedOutputStream writer = new PipedOutputStream();
		try {
			reader.connect(writer);
		} catch (IOException e) {
			newloader.showError(LC.getFormatted("fileDuplicateError", e.toString()));
			return null;
		}
		new WritingThread(writer, this).start();
		try {
			return LogisimFile.load(reader, newloader);
		} catch (IOException e) {
			newloader.showError(LC.getFormatted("fileDuplicateError", e.toString()));
			return null;
		}
	}

	Tool findTool(Tool query) {
		for (Library lib : getLibraries()) {
			Tool ret = findTool(lib, query);
			if (ret != null) return ret;
		}
		return null;
	}

	private Tool findTool(Library lib, Tool query) {
		for (Tool tool : lib.getTools()) {
			if (tool.equals(query)) return tool;
		}
		return null;
	}

	public void updateCircuitPos(){

		//System.out.println("tool size: "+tools.size());

		if(tools.size()==1){
			obsPos.setValue("first&last");
		}
		else if(getCircuits().indexOf(current)==0){
			obsPos.setValue("first");
		}else if(getCircuits().indexOf(current)==getCircuits().size()-1){
			obsPos.setValue("last");
		}else{
			obsPos.setValue("undefined");
		}
		//System.out.println(obsPos.getValue());

	}

	//
	// creation methods
	//
	public static LogisimFile createNew(Loader loader) {

		LogisimFile ret = new LogisimFile(loader);
		ret.main = new Circuit("main");
		// The name will be changed in LogisimPreferences
		ret.tools.add(new AddTool(ret.main.getSubcircuitFactory()));
		return ret;

	}

	public static LogisimFile load(File file, Loader loader)
			throws IOException {

		InputStream in = new FileInputStream(file);
		SAXException firstExcept = null;

		try {
			return loadSub(in, loader);
		} catch (SAXException e) {
			firstExcept = e;
		} finally {
			in.close();
		}

		if (firstExcept != null) {
			// We'll now try to do it using a reader. This is to work around
			// Logisim versions prior to 2.5.1, when files were not saved using
			// UTF-8 as the encoding (though the XML file reported otherwise).
			try {
				in = new ReaderInputStream(new FileReader(file), "UTF8");
				return loadSub(in, loader);
			} catch (Throwable t) {
				loader.showError(LC.getFormatted("xmlFormatError", firstExcept.toString()));
			} finally {
				try {
					in.close();
				} catch (Throwable t) { }
			}
		}

		return null;

	}

	public static LogisimFile load(InputStream in, Loader loader)
			throws IOException {

		try {
			return loadSub(in, loader);
		} catch (SAXException e) {
			loader.showError(LC.getFormatted("xmlFormatError", e.toString()));
			return null;
		}

	}

	public static LogisimFile loadSub(InputStream in, Loader loader)
			throws IOException, SAXException {

		// fetch first line and then reset
		BufferedInputStream inBuffered = new BufferedInputStream(in);
		String firstLine = getFirstLine(inBuffered);

		if (firstLine == null) {
			throw new IOException("File is empty");
		} else if (firstLine.equals("Logisim v1.0")) {
			// if this is a 1.0 file, then set up a pipe to translate to
			// 2.0 and then interpret as a 2.0 file
			throw new IOException("Version 1.0 files no longer supported");
		}

		XmlReader xmlReader = new XmlReader(loader);
		LogisimFile ret = xmlReader.readLibrary(inBuffered);
		ret.loader = loader;
		return ret;

	}

	private static String getFirstLine(BufferedInputStream in)
			throws IOException {

		byte[] first = new byte[512];
		in.mark(first.length - 1);
		in.read(first);
		in.reset();
		
		int lineBreak = first.length;
		for (int i = 0; i < lineBreak; i++) {
			if (first[i] == '\n') {
				lineBreak = i;
			}
		}
		return new String(first, 0, lineBreak, "UTF-8");

	}

}

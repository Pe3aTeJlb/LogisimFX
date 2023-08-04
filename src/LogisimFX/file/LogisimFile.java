/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.file;

import LogisimFX.lang.python.PythonConnector;
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

import LogisimFX.util.ZipUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
	private LinkedList<AddTool> libtools = new LinkedList<>();
	private LinkedList<Library> libraries = new LinkedList<>();
	private Circuit main = null;
	private Circuit current = null;
	private boolean dirty = false;

	public SimpleStringProperty obsPos = new SimpleStringProperty("undefined2");
	public SimpleBooleanProperty isMain = new SimpleBooleanProperty(false);

	//Пути к temp директориям
	public static Path LOGISIMFX_TEMP_DIR;
	public static Path LOGISIMFX_TEMP_RUNTIME;
	public static Path LOGISIMFX_RUNTIME; //Путь к runtime директории в user.home
	private Path projectDir;
	private Path circuitDir;
	private Path fpgaDir;
	private Path libDir;
	private Path otherDir;

	public static String CIRCUIT = "circuit";
	public static String FPGA_BUILD = "fpgaBuild";
	public static String LIB = "lib";
	public static String OTHERS = "other";

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					if (Files.exists(LOGISIMFX_TEMP_DIR)) {
						FileUtils.deleteDirectory(LOGISIMFX_TEMP_DIR.toFile());
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	LogisimFile(Loader loader, boolean isLib) {

		this.loader = loader;

		nameProperty().set(LC.get("defaultProjectName"));
		if (FrameManager.windowNamed(name.get())) {
			for (int i = 2; true; i++) {
				if (!FrameManager.windowNamed(name.get() + " " + i)) {
					name.set(name.get() + " " + i);
					break;
				}
			}
		}

		try {

			if (LOGISIMFX_TEMP_DIR == null) {
				LOGISIMFX_TEMP_DIR = Files.createTempDirectory("LogisimFX-");
				LOGISIMFX_TEMP_RUNTIME = Files.createDirectories(Paths.get(LOGISIMFX_TEMP_DIR+File.separator+"runtime"));
			}

			if (!isLib) {

				projectDir = Files.createTempDirectory(LOGISIMFX_TEMP_DIR, name.get() + "-");

				circuitDir = new File(projectDir + File.separator + CIRCUIT).toPath();
				circuitDir.toFile().mkdirs();

				fpgaDir = new File(projectDir + File.separator + FPGA_BUILD).toPath();
				fpgaDir.toFile().mkdirs();

				libDir = new File(projectDir + File.separator + LIB).toPath();
				libDir.toFile().mkdirs();

				otherDir = new File(projectDir + File.separator + OTHERS).toPath();
				otherDir.toFile().mkdirs();

			}

			LOGISIMFX_RUNTIME = Paths.get(System.getProperty("user.home") + File.separator + "logisimfx_runtime");
			if (!LOGISIMFX_RUNTIME.toFile().exists()){
				LOGISIMFX_RUNTIME = Files.createDirectory(
						Paths.get(System.getProperty("user.home") + File.separator + "logisimfx_runtime")
				);
			}

			if (FileUtils.isEmptyDirectory(LOGISIMFX_RUNTIME.toFile())){
				PythonConnector.unpackVenv();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private SimpleStringProperty name;

	public SimpleStringProperty nameProperty() {
		if (name == null) {
			name = new SimpleStringProperty(LC.get("defaultProjectName")) {
				@Override
				protected void invalidated() {
				}

				@Override
				public Object getBean() {
					return LogisimFile.this;
				}

				@Override
				public String getName() {
					return "name";
				}
			};
		}
		return name;
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	//
	// access methods
	//


	//public Path getProjectDir(){
	//	return projectDir;
	//}

	@Override
	public boolean isDirty() {
		return dirty;
	}

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
	public AddTool getTool(String name) {
		for (AddTool tool : getTools()) {
			if (tool.getName().equals(name)) {
				return tool;
			}
		}
		return null;
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
		for (AddTool tool : libtools) {
			SubcircuitFactory factory = (SubcircuitFactory) tool.getFactory();
			if (name.equals(factory.getName())) return factory.getSubcircuit();
		}
		return null;
	}

	public boolean contains(Circuit circ) {
		//return true;
		for (AddTool tool : tools) {
			SubcircuitFactory factory = (SubcircuitFactory) tool.getFactory();
			if (factory.getSubcircuit() == circ) return true;
		}
		for (AddTool tool : libtools) {
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
		for (AddTool tool : libtools) {
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

	public Path getProjectDir(){
		return projectDir;
	}

	public Path getCircuitDir(){
		return circuitDir;
	}

	public Path getFpgaDir(){
		return fpgaDir;
	}

	public Path getLibDir(){
		return libDir;
	}

	public Path getOtherDir(){
		return otherDir;
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
		nameProperty().set(name);
		fireEvent(LibraryEvent.SET_NAME, name);
	}

	public void addCircuit(Circuit circuit) {
		addCircuit(circuit, tools.size());
	}

	public void addLibCircuit(Circuit circuit) {
		addLibCircuit(circuit, libtools.size());
	}

	public void addCircuit(Circuit circuit, int index) {

		AddTool tool = new AddTool(circuit.getSubcircuitFactory());
		tools.add(index, tool);
		if (tools.size() == 1) setMainCircuit(circuit);
		fireEvent(LibraryEvent.ADD_TOOL, tool);

		updateCircuitPos();

	}

	public void addLibCircuit(Circuit circuit, int index) {
		AddTool tool = new AddTool(circuit.getSubcircuitFactory());
		libtools.add(index, tool);
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

	@Override
	public boolean removeLibrary(String name) {
		int index = -1;
		for (final var lib : libraries)
			if (lib.getName().equals(name))
				index = libraries.indexOf(lib);
		if (index < 0) return false;
		libraries.remove(index);
		return true;
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

	public void setCurrent(Circuit circ) {

		current = circ;

		if (current != main) {
			isMain.setValue(false);
		} else {
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
			return LogisimFile.load(reader, newloader, false, null);
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

	public void updateCircuitPos() {

		if (tools.size() == 1) {
			obsPos.setValue("first&last");
		} else if (getCircuits().indexOf(current) == 0) {
			obsPos.setValue("first");
		} else if (getCircuits().indexOf(current) == getCircuits().size() - 1) {
			obsPos.setValue("last");
		} else {
			obsPos.setValue("undefined");
		}

	}

	//
	// creation methods
	//
	public static LogisimFile createNew(Loader loader) {

		LogisimFile ret = new LogisimFile(loader, false);
		ret.main = new Circuit("main");
		// The name will be changed in LogisimPreferences
		ret.tools.add(new AddTool(ret.main.getSubcircuitFactory()));
		return ret;

	}

	public static LogisimFile loadZip(File file, Loader loader, boolean isLib, LogisimFile baseLogisimFile){

		LogisimFile logisimFile = new LogisimFile(loader, false);

		try {

			ZipUtils.unzipFolder(file.toPath(), logisimFile.getProjectDir());
			FileFilter fileFilter = new WildcardFileFilter("*.proj");
			File[] projFile = logisimFile.getProjectDir().toFile().listFiles(fileFilter);

			InputStream in = new FileInputStream(projFile[0]);
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
			xmlReader.readLibraryTo(logisimFile, inBuffered, false, null);
			logisimFile.loader = loader;

			return logisimFile;

		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}

		return logisimFile;

	}

	public static LogisimFile load(File file, Loader loader, boolean isLib, LogisimFile baseLogisimFile)
			throws IOException {

		InputStream in = new FileInputStream(file);
		SAXException firstExcept = null;

		try {
			return loadSub(in, loader, isLib, baseLogisimFile);
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
				return loadSub(in, loader, isLib, baseLogisimFile);
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

	public static LogisimFile load(InputStream in, Loader loader, boolean isLib, LogisimFile baseLogisimFile)
			throws IOException {

		try {
			return loadSub(in, loader, isLib, baseLogisimFile);
		} catch (SAXException e) {
			loader.showError(LC.getFormatted("xmlFormatError", e.toString()));
			return null;
		}

	}

	public static LogisimFile loadSub(InputStream in, Loader loader, boolean isLib, LogisimFile baseLogisimFile)
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
		LogisimFile ret = xmlReader.readLibrary(inBuffered, isLib, baseLogisimFile);
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

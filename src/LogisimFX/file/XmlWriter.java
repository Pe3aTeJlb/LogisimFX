/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.circuit.CircuitMapInfo;
import LogisimFX.draw.model.AbstractCanvasObject;
import LogisimFX.LogisimVersion;
import LogisimFX.Main;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.Wire;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeDefaultProvider;
import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.data.MapComponent;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.util.InputEventUtil;
import LogisimFX.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class XmlWriter {
	static void write(LogisimFile file, OutputStream out, LibraryLoader loader)
			throws ParserConfigurationException,
				TransformerConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		XmlWriter context = new XmlWriter(file, doc, loader);
		context.fromLogisimFile();

		TransformerFactory tfFactory = TransformerFactory.newInstance();
		try {
			tfFactory.setAttribute("indent-number", Integer.valueOf(2));
		} catch (IllegalArgumentException e) { }
		Transformer tf = tfFactory.newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"2");
		} catch (IllegalArgumentException e) { }

		Source src = new DOMSource(doc);
		Result dest = new StreamResult(out);
		tf.transform(src, dest);
	}

	private LogisimFile file;
	private Document doc;
	private LibraryLoader loader;
	private HashMap<Library,String> libs = new HashMap<Library,String>();

	private XmlWriter(LogisimFile file, Document doc, LibraryLoader loader) {
		this.file = file;
		this.doc = doc;
		this.loader = loader;
	}

	Element fromLogisimFile() {
		Element ret = doc.createElement("project");
		doc.appendChild(ret);
		ret.appendChild(doc.createTextNode("\nThis file is intended to be "
				+ "loaded by LogisimFX (https://sites.google.com/view/pplosstudio).\n"));
		ret.setAttribute("version", "1.0");
		ret.setAttribute("source", Main.VERSION_NAME);

		for (Library lib : file.getLibraries()) {
			Element elt = fromLibrary(lib);
			if (elt != null) ret.appendChild(elt);
		}

		if (file.getMainCircuit() != null) {
			Element mainElt = doc.createElement("main");
			mainElt.setAttribute("name", file.getMainCircuit().getName());
			ret.appendChild(mainElt);
		}

		ret.appendChild(fromOptions());
		ret.appendChild(fromMouseMappings());
		ret.appendChild(fromToolbarData());

		/*
		for (Circuit circ : file.getCircuits()) {
			ret.appendChild(fromCircuit(circ));
		}
		 */
		for (Circuit circuit: file.getCircuits()){

			Element circ = doc.createElement("circuit");
			circ.setAttribute("name", circuit.getName());
			circ.setAttribute("sch", circuit.getSchematicsRelativePath().toString());

			if (!circuit.getAppearance().isDefaultAppearance()){
				circ.setAttribute("app", circuit.getAppearanceRelativePath().toString());
			}

			if (circuit.haveBoardMapNamestoSave()){
				circ.setAttribute("iomap", circuit.getIOMapRelativePath().toString());
			}

			ret.appendChild(circ);

			exportCircuit(circuit);

		}

		ret.appendChild(fromMainFrameLayout());

		ret.appendChild(fromFPGAToolchainOrchestrator());

		return ret;
	}

	Element fromLibrary(Library lib) {
		Element ret = doc.createElement("lib");
		if (libs.containsKey(lib)) return null;
		String name = "" + libs.size();
		String desc = loader.getDescriptor(lib);
		if (desc == null) {
			loader.showError("library location unknown: "
				+ lib.getName());
			return null;
		}
		libs.put(lib, name);
		ret.setAttribute("name", name);
		ret.setAttribute("desc", desc);
		for (Tool t : lib.getTools()) {
			AttributeSet attrs = t.getAttributeSet();
			if (attrs != null) {
				Element toAdd = doc.createElement("tool");
				toAdd.setAttribute("name", t.getName());
				addAttributeSetContent(doc, toAdd, attrs, t);
				if (toAdd.getChildNodes().getLength() > 0) {
					ret.appendChild(toAdd);
				}
			}
		}
		return ret;
	}

	Element fromOptions() {
		Element elt = doc.createElement("options");
		addAttributeSetContent(doc, elt, file.getOptions().getAttributeSet(), null);
		return elt;
	}

	Element fromMouseMappings() {
		Element elt = doc.createElement("mappings");
		MouseMappings map = file.getOptions().getMouseMappings();
		for (Map.Entry<Integer,Tool> entry : map.getMappings().entrySet()) {
			Integer mods = entry.getKey();
			Tool tool = entry.getValue();
			Element toolElt = fromTool(tool);
			String mapValue = InputEventUtil.toString(mods.intValue());
			toolElt.setAttribute("map", mapValue);
			elt.appendChild(toolElt);
		}
		return elt;
	}

	Element fromToolbarData() {
		Element elt = doc.createElement("toolbar");
		ToolbarData toolbar = file.getOptions().getToolbarData();
		for (Tool tool : toolbar.getContents()) {
			if (tool == null) {
				elt.appendChild(doc.createElement("sep"));
			} else {
				elt.appendChild(fromTool(tool));
			}
		}
		return elt;
	}

	Element fromMainFrameLayout(){
		return file.getOptions().getMainFrameLayout().getLayout(doc);
	}

	Element fromTool(Tool tool) {
		Library lib = findLibrary(tool);
		String lib_name;
		if (lib == null) {
			loader.showError(StringUtil.format("tool `%s' not found",
				tool.getDisplayName().getValue()));
			return null;
		} else if (lib == file) {
			lib_name = null;
		} else {
			lib_name = libs.get(lib);
			if (lib_name == null) {
				loader.showError("unknown library within file");
				return null;
			}
		}

		Element elt = doc.createElement("tool");
		if (lib_name != null) elt.setAttribute("lib", lib_name);
		elt.setAttribute("name", tool.getName());
		addAttributeSetContent(doc, elt, tool.getAttributeSet(), tool);
		return elt;
	}

	Element fromCircuit(Circuit circuit) {
		Element ret = doc.createElement("circuit");
		ret.setAttribute("name", circuit.getName());
		addAttributeSetContent(doc, ret, circuit.getStaticAttributes(), null);
		if (!circuit.getAppearance().isDefaultAppearance()) {
			Element appear = doc.createElement("appear");
			for (Object o : circuit.getAppearance().getObjectsFromBottom()) {
				if (o instanceof AbstractCanvasObject) {
					Element elt = ((AbstractCanvasObject) o).toSvgElement(doc);
					if (elt != null) {
						appear.appendChild(elt);
					}
				}
			}
			ret.appendChild(appear);
		}
		for (Wire w : circuit.getWires()) {
			ret.appendChild(fromWire(w));
		}
		for (Component comp : circuit.getNonWires()) {
			Element elt = fromComponent(doc, comp);
			if (elt != null) ret.appendChild(elt);
		}
		return ret;
	}

	Element fromComponent(Document doc, Component comp) {
		ComponentFactory source = comp.getFactory();
		Library lib = findLibrary(source);
		String lib_name;
		if (lib == null) {
			loader.showError(source.getName() + " component not found");
			return null;
		} else if (lib == file) {
			lib_name = null;
		} else {
			lib_name = libs.get(lib);
			if (lib_name == null) {
				loader.showError("unknown library within file");
				return null;
			}
		}

		Element ret = doc.createElement("comp");
		if (lib_name != null) ret.setAttribute("lib", lib_name);
		ret.setAttribute("name", source.getName());
		ret.setAttribute("loc", comp.getLocation().toString());
		addAttributeSetContent(doc, ret, comp.getAttributeSet(), comp.getFactory());
		return ret;
	}

	Element fromWire(Wire w) {
		Element ret = doc.createElement("wire");
		ret.setAttribute("from", w.getEnd0().toString());
		ret.setAttribute("to", w.getEnd1().toString());
		return ret;
	}

	Element fromFPGAToolchainOrchestrator(){
		return file.getOptions().getFPGAToolchainOrchestratorData().getData(doc);
	}

	void addAttributeSetContent(Document doc, Element elt, AttributeSet attrs,
			AttributeDefaultProvider source) {
		if (attrs == null) return;
		LogisimVersion ver = Main.VERSION;
		if (source != null && source.isAllDefaultValues(attrs, ver)) return;
		for (Attribute<?> attrBase : attrs.getAttributes()) {
			@SuppressWarnings("unchecked")
			Attribute<Object> attr = (Attribute<Object>) attrBase;
			Object val = attrs.getValue(attr);
			if (attrs.isToSave(attr) && val != null) {
				Object dflt = source == null ? null : source.getDefaultAttributeValue(attr, ver);
				if (dflt == null || !dflt.equals(val)) {
					Element a = doc.createElement("a");
					a.setAttribute("name", attr.getName());
					String value = attr.toStandardString(val);
					if (value.indexOf("\n") >= 0) {
						a.appendChild(doc.createTextNode(value));
					} else {
						a.setAttribute("val", attr.toStandardString(val));
					}
					elt.appendChild(a);
				}
			}
		}
	}



	private void exportCircuit(Circuit circuit){

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element circ = doc.createElement("circuit");
			doc.appendChild(circ);

			addAttributeSetContent(doc, circ, circuit.getStaticAttributes(), null);

			for (Wire w : circuit.getWires()) {
				circ.appendChild(fromWire(w));
			}
			for (Component comp : circuit.getNonWires()) {
				Element elt = fromComponent(doc, comp);
				if (elt != null) circ.appendChild(elt);
			}

			exportToFile(doc, Paths.get(file.getProjectDir() + File.separator + circuit.getSchematicsRelativePath()).toFile());

			if (!circuit.getAppearance().isDefaultAppearance()){
				exportAppearance(circuit);
			}

			if (circuit.haveBoardMapNamestoSave()){
				exportIOMap(circuit);
			}

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

	}

	private void exportAppearance(Circuit circuit){

		try{

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element appear = doc.createElement("appear");
			doc.appendChild(appear);

			for (Object o : circuit.getAppearance().getObjectsFromBottom()) {
				if (o instanceof AbstractCanvasObject) {
					Element elt = ((AbstractCanvasObject) o).toSvgElement(doc);
					if (elt != null) {
						appear.appendChild(elt);
					}
				}
			}

			exportToFile(doc, Paths.get(file.getProjectDir() + File.separator + circuit.getAppearanceRelativePath()).toFile());

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

	}

	private void exportIOMap(Circuit circuit){

		try{

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			for (String boardName : circuit.getBoardMapNamestoSave()) {

				Element ret = doc.createElement("boardmap");
				ret.setAttribute("boardname", boardName);
				for (String key : circuit.getMapInfo(boardName).keySet()) {
					Element map = doc.createElement("mc");
					CircuitMapInfo mapInfo = circuit.getMapInfo(boardName).get(key);
					MapComponent nmap = mapInfo.getMap();
					if (nmap != null) {
						nmap.getMapElement(map);
					} else {
						map.setAttribute("key", key);
						MapComponent.getComplexMap(map, mapInfo);
					}
					ret.appendChild(map);
				}
				doc.appendChild(ret);

			}

			exportToFile(doc, Paths.get(file.getProjectDir() + File.separator + circuit.getIOMapRelativePath()).toFile());

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

	}

	private void exportToFile(Document doc, File out) throws TransformerException {

		if (!out.exists()){
			try {
				out.getParentFile().mkdirs();
				out.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		TransformerFactory tfFactory = TransformerFactory.newInstance();
		try {
			tfFactory.setAttribute("indent-number", Integer.valueOf(2));
		} catch (IllegalArgumentException e) { }
		Transformer tf = tfFactory.newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"2");
		} catch (IllegalArgumentException e) { }

		Source src = new DOMSource(doc);
		Result dest = new StreamResult(out);
		tf.transform(src, dest);

	}


	Library findLibrary(Tool tool) {
		if (libraryContains(file, tool)) {
			return file;
		}
		for (Library lib : file.getLibraries()) {
			if (libraryContains(lib, tool)) return lib;
		}
		return null;
	}

	Library findLibrary(ComponentFactory source) {
		if (file.contains(source)) {
			return file;
		}
		for (Library lib : file.getLibraries()) {
			if (lib.contains(source)) return lib;
		}
		return null;
	}

	boolean libraryContains(Library lib, Tool query) {
		for (Tool tool : lib.getTools()) {
			if (tool.sharesSource(query)) return true;
		}
		return false;
	}
}

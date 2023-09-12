package LogisimFX.yosys;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.BitWidth;
import LogisimFX.data.Direction;
import LogisimFX.data.Location;
import LogisimFX.file.LogisimFileActions;
import LogisimFX.instance.InstanceComponent;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Project;
import LogisimFX.std.wiring.Pin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkGraphFactory;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.elk.graph.impl.ElkNodeImpl;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

public class YosysRTLParser {

	private static ElkGraphFactory elkFactory = ElkGraphFactory.eINSTANCE;
	private static ElkNode graphRoot;

	private static Project proj;
	private static ArrayList<String> verilogDesc;
	private static ArrayList<ElkNode> elkNodes;

	private static class Node extends ElkNodeImpl {

		private InstanceComponent comp;

		public Node(Component cmp){

			this.comp = (InstanceComponent) cmp;

			setDimensions(comp.getBounds().getWidth(), comp.getBounds().getHeight());
			setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);

			for (Port port : comp.getPorts()){

				ElkPort p = elkFactory.createElkPort();
				this.getPorts().add(p);
				p.setLocation(port.getRelX(), port.getRelY());
				p.setDimensions(1, 1);

			}
		}

		public Component getComp(){
			return comp;
		}

	}

	public static void parse(Project project, File vfile, File json){

		proj = project;
		verilogDesc = new ArrayList<>();
		elkNodes = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(Paths.get("D:\\demo.v").toFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				verilogDesc.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			Reader reader = new InputStreamReader(new FileInputStream(Paths.get("D:\\demo.json").toFile()));
			JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
			JsonObject modules = parser.getAsJsonObject("modules");

			for (String module : modules.keySet()){
				processModule(module, modules.getAsJsonObject(module));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void processModule(String moduleName, JsonObject module){

		System.out.println(moduleName + " " + module);

		graphRoot = elkFactory.createElkNode();
		graphRoot.setProperty(CoreOptions.ALGORITHM, "org.eclipse.elk.layered");

		Circuit circ = processCircuit(moduleName, module.getAsJsonObject("attributes"));
		CircuitMutation cm =  new CircuitMutation(circ);
		cm.clear();

		//Process default params
		if (module.has("parameter_default_values")) {
			JsonObject parameter_default_values = module.getAsJsonObject("parameter_default_values");
			for (String param : parameter_default_values.keySet()) {
				processModuleParam(param, parameter_default_values.getAsJsonObject(param));
			}
		}

		//Process ports
		if (module.has("ports")) {
			JsonObject ports = module.getAsJsonObject("ports");
			for (String port : ports.keySet()) {
				graphRoot.getChildren().add(processModulePort(port, ports.getAsJsonObject(port)));
			}
		}

		//Process cells
		if (module.has("cells")) {
			JsonObject cells = module.getAsJsonObject("cells");
			for (String cell : cells.keySet()) {
				graphRoot.getChildren().add(processModuleCell(cell, cells.getAsJsonObject(cell)));
			}
		}

		//Process memory cells
		if (module.has("memories")) {
			JsonObject memories = module.getAsJsonObject("memories");
			for (String mem : memories.keySet()) {
				processModuleMemory(mem, memories.getAsJsonObject(mem));
			}
		}

		//Create layout

		//create graph edges


		//Process graph layout
		RecursiveGraphLayoutEngine engine = new RecursiveGraphLayoutEngine();
		engine.layout(graphRoot, new BasicProgressMonitor());

		//Apply layout to logisimfx components
		for (ElkNode n : graphRoot.getChildren()){
			Node node = (Node)n;
			node.getComp().getLocation().translate((int)node.getX(), (int)node.getY());
			cm.add(node.getComp());
		}

		//create logisimfx wires

		cm.execute();
	}

	private static Circuit processCircuit(String circName, JsonObject attrs){

		if (proj.getLogisimFile().getTool(circName) != null){
			return ((SubcircuitFactory)proj.getLogisimFile().getTool(circName).getFactory()).getSubcircuit();
		} else {

			Circuit circuit = new Circuit(circName);

			//"demo.v:1.1-12.10"
			String src = attrs.get("src").getAsString().split(":")[1];
			int startLine = Integer.parseInt(src.split("-")[0].split("\\.")[0]) - 1;
			int startColumn = Integer.parseInt(src.split("-")[0].split("\\.")[1]) - 1;
			int endLine = Integer.parseInt(src.split("-")[1].split("\\.")[0]) - 1;
			int endColumn = Integer.parseInt(src.split("-")[1].split("\\.")[1]) - 1;

			PrintWriter writer;
			try {

				File f = circuit.getVerilogModel(proj);
				if (!f.exists()) {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}
				writer = new PrintWriter(f);

				writer.println(verilogDesc.get(startLine).substring(startColumn));
				for (int i = startLine + 1; i < endLine; i++) {
					writer.println(verilogDesc.get(i));
				}
				writer.println(verilogDesc.get(endLine).substring(0, endColumn));
				writer.flush();
				writer.close();

			} catch (IOException e) {
				DialogManager.createStackTraceDialog("Error!", "Error during processing " + circName, e);
				e.printStackTrace();
			}

			proj.doAction(LogisimFileActions.addCircuit(circuit));
			proj.setCurrentCircuit(circuit);

			return circuit;

		}

	}

	private static void processModuleParam(String paramName, JsonObject param){
		System.out.println(paramName + " " + param);
	}

	private static Node processModulePort(String portName, JsonObject port){

		System.out.println(portName + " " + port);

		String direction = port.get("direction").getAsString();
		int width = port.get("bits").getAsJsonArray().size();

		ComponentFactory factory = Pin.FACTORY;
		AttributeSet attrs = factory.createAttributeSet();
		attrs.setValue(StdAttr.FACING, Direction.WEST);
		attrs.setValue(StdAttr.WIDTH, BitWidth.create(width));
		if (direction.equals("input")){
			attrs.setValue(Pin.ATTR_TYPE, Pin.INPUT);
		} else if (direction.equals("output")){
			attrs.setValue(Pin.ATTR_TYPE, Pin.OUTPUT);
		} else {
			attrs.setValue(Pin.ATTR_TYPE, Pin.INOUT);
		}
		attrs.setValue(StdAttr.LABEL, portName);
		attrs.setValue(Pin.ATTR_LABEL_LOC, Direction.NORTH);

		return new Node(factory.createComponent(Location.create(0,0), attrs));

	}








	private static Node processModuleCell(String cellName, JsonObject cell){
		System.out.println(cellName + " " + cell);

		switch (cell.get("type").getAsString()){
			case "$not": return $not(cellName, cell);
			case "$pos": return $pos(cellName, cell);
			case "$neg": return $neg(cellName, cell);
			case "$reduce_and": return $reduce_and(cellName, cell);
			case "$reduce_or": return $reduce_or(cellName, cell);
			case "$reduce_xor": return $reduce_xor(cellName, cell);
			case "$reduce_xnor": return $reduce_xnor(cellName, cell);
			case "$reduce_bool": return $reduce_bool(cellName, cell);
			case "$logic_not": return $logic_not(cellName, cell);
			case "$and": return $and(cellName, cell);
			case "$lt": return $lt(cellName, cell);
			case "$or": return $or(cellName, cell);
			case "$le": return $le(cellName, cell);
			case "$xor": return $xor(cellName, cell);
			case "$eq": return $eq(cellName, cell);
			case "$xnor": return $xnor(cellName, cell);
			case "$ne": return $ne(cellName, cell);
			case "$shl": return $shl(cellName, cell);
			case "$ge": return $ge(cellName, cell);
			case "$shr": return $shr(cellName, cell);
			case "$gt": return $gt(cellName, cell);
			case "$sshl": return $sshl(cellName, cell);
			case "$add": return $add(cellName, cell);
			case "$sshr": return $sshr(cellName, cell);
			case "$sub": return $sub(cellName, cell);
			case "$logic_and": return $logic_and(cellName, cell);
			case "$mul": return $mul(cellName, cell);
			case "$logic_or": return $logic_or(cellName, cell);
			case "$div": return $div(cellName, cell);
			case "$eqx": return $eqx(cellName, cell);
			case "$mod": return $mod(cellName, cell);
			case "$nex": return $nex(cellName, cell);
			case "$divfloor": return $divfloor(cellName, cell);
			case "$pow": return $pow(cellName, cell);
			case "$modfoor": return $modfoor(cellName, cell);
			case "$mux": return $mux(cellName, cell);
			case "$pmux": return $pmux(cellName, cell);
			case "$tribuf": return $tribuf(cellName, cell);
			case "$sr": return $sr(cellName, cell);
			case "$dff": return $dff(cellName, cell);
			case "$dffe": return $dffe(cellName, cell);
			case "$adffe": return $adffe(cellName, cell);
			case "$aldffe": return $aldffe(cellName, cell);
			case "$dffsre": return $dffsre(cellName, cell);
			case "$sdffe": return $sdffe(cellName, cell);
			case "$sdffce": return $sdffce(cellName, cell);
			case "$adff": return $adff(cellName, cell);
			case "$aldff": return $aldff(cellName, cell);
			case "$dffsr": return $dffsr(cellName, cell);
			case "$sdff": return $sdff(cellName, cell);
			case "$dlatch": return $dlatch(cellName, cell);
			case "$adlatch": return $adlatch(cellName, cell);
			case "$dlatchsr": return $dlatchsr(cellName, cell);
			default: return null;
		}

	}

	private static Node $not(String cellName, JsonObject cell){return null;}
	private static Node $pos(String cellName, JsonObject cell){return null;}
	private static Node $neg(String cellName, JsonObject cell){return null;}
	private static Node $reduce_and(String cellName, JsonObject cell){return null;}
	private static Node $reduce_or(String cellName, JsonObject cell){return null;}
	private static Node $reduce_xor(String cellName, JsonObject cell){return null;}
	private static Node $reduce_xnor(String cellName, JsonObject cell){return null;}
	private static Node $reduce_bool(String cellName, JsonObject cell){return null;}
	private static Node $logic_not(String cellName, JsonObject cell){return null;}
	private static Node $and(String cellName, JsonObject cell){return null;}
	private static Node $lt(String cellName, JsonObject cell){return null;}
	private static Node $or(String cellName, JsonObject cell){return null;}
	private static Node $le(String cellName, JsonObject cell){return null;}
	private static Node $xor(String cellName, JsonObject cell){return null;}
	private static Node $eq(String cellName, JsonObject cell){return null;}
	private static Node $xnor(String cellName, JsonObject cell){return null;}
	private static Node $ne(String cellName, JsonObject cell){return null;}
	private static Node $shl(String cellName, JsonObject cell){return null;}
	private static Node $ge(String cellName, JsonObject cell){return null;}
	private static Node $shr(String cellName, JsonObject cell){return null;}
	private static Node $gt(String cellName, JsonObject cell){return null;}
	private static Node $sshl(String cellName, JsonObject cell){return null;}
	private static Node $add(String cellName, JsonObject cell){

		String direction = port.get("direction").getAsString();
		int width = cell.get("bits").getAsJsonArray().size();

		NodeFactory factory = proj.getLogisimFile().getTool("Adder").getFactory();

		AttributeSet attrs = factory.createAttributeSet();
		attrs.setValue(StdAttr.FACING, Direction.WEST);
		attrs.setValue(StdAttr.WIDTH, BitWidth.create(width));
		attrs.setValue(StdAttr.LABEL, cellName);
		attrs.setValue(Pin.ATTR_LABEL_LOC, Direction.NORTH);

		return factory.createNode(Location.create(0,0), attrs);

	}
	private static Node $sshr(String cellName, JsonObject cell){return null;}
	private static Node $sub(String cellName, JsonObject cell){return null;}
	private static Node $logic_and(String cellName, JsonObject cell){return null;}
	private static Node $mul(String cellName, JsonObject cell){return null;}
	private static Node $logic_or(String cellName, JsonObject cell){return null;}
	private static Node $div(String cellName, JsonObject cell){return null;}
	private static Node $eqx(String cellName, JsonObject cell){return null;}
	private static Node $mod(String cellName, JsonObject cell){return null;}
	private static Node $nex(String cellName, JsonObject cell){return null;}
	private static Node $divfloor(String cellName, JsonObject cell){return null;}
	private static Node $pow(String cellName, JsonObject cell){return null;}
	private static Node $modfoor(String cellName, JsonObject cell){return null;}
	private static Node $mux(String cellName, JsonObject cell){return null;}
	private static Node $pmux(String cellName, JsonObject cell){return null;}
	private static Node $tribuf(String cellName, JsonObject cell){return null;}
	private static Node $sr(String cellName, JsonObject cell){return null;}
	private static Node $dff(String cellName, JsonObject cell){return null;}
	private static Node $dffe(String cellName, JsonObject cell){return null;}
	private static Node $adffe(String cellName, JsonObject cell){return null;}
	private static Node $aldffe(String cellName, JsonObject cell){return null;}
	private static Node $dffsre(String cellName, JsonObject cell){return null;}
	private static Node $sdffe(String cellName, JsonObject cell){return null;}
	private static Node $sdffce(String cellName, JsonObject cell){return null;}
	private static Node $adff(String cellName, JsonObject cell){return null;}
	private static Node $aldff(String cellName, JsonObject cell){return null;}
	private static Node $dffsr(String cellName, JsonObject cell){return null;}
	private static Node $sdff(String cellName, JsonObject cell){return null;}
	private static Node $dlatch(String cellName, JsonObject cell){return null;}
	private static Node $adlatch(String cellName, JsonObject cell){return null;}
	private static Node $dlatchsr(String cellName, JsonObject cell){return null;}

	private static Node processModuleMemory(String memName, JsonObject mem){
		System.out.println(memName + " " + mem);
		return null;
	}

}

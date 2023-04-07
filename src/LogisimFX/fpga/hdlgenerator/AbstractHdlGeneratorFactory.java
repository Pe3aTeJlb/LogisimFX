/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.hdlgenerator;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.Reporter;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.file.FileWriter;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.std.wiring.ClockHdlGeneratorFactory;
import LogisimFX.util.LineBuffer;
import LogisimFX.util.StringUtil;

import java.io.File;
import java.util.*;

public class AbstractHdlGeneratorFactory implements HdlGeneratorFactory {

	private final String subDirectoryName;
	protected final HdlParameters myParametersList = new HdlParameters();
	protected final HdlWires myWires = new HdlWires();
	protected final HdlPorts myPorts = new HdlPorts();
	protected final HdlTypes myTypedWires = new HdlTypes();
	protected boolean getWiresPortsDuringHDLWriting = false;

	public AbstractHdlGeneratorFactory() {
		final var className = getClass().toString().replace('.', ':').replace(' ', ':');
		final var parts = className.split(":");
		if (parts.length < 2) throw new ExceptionInInitializerError("Cannot read class path!");
		subDirectoryName = parts[parts.length - 2];
	}

	public AbstractHdlGeneratorFactory(String subDirectory) {
		subDirectoryName = subDirectory;
	}

	// Handle to get the wires and ports during generation time
	public void getGenerationTimeWiresPorts(Netlist theNetlist, AttributeSet attrs) {
	}

	/* Here the common predefined methods are defined */
	@Override
	public boolean generateAllHDLDescriptions(
			Set<String> handledComponents,
			String workingDirectory,
			List<String> hierarchy) {
		return true;
	}

	@Override
	public List<String> getArchitecture(Netlist theNetlist, AttributeSet attrs, String componentName) {
		final var contents = LineBuffer.getHdlBuffer();
		if (getWiresPortsDuringHDLWriting) {
			myWires.removeWires();
			myTypedWires.clear();
			myPorts.removePorts();
			getGenerationTimeWiresPorts(theNetlist, attrs);
		}
		contents.add(FileWriter.getGenerateRemark(componentName));

		final var preamble = String.format("module %s( ", componentName);
		final var indenting = " ".repeat(preamble.length());
		final var body = LineBuffer.getHdlBuffer();
		if (myPorts.isEmpty()) {
			contents.add(preamble + " );");
		} else {
			final var ports = new TreeSet<>(myPorts.keySet());
			for (final var port : myPorts.keySet())
				if (myPorts.isClock(port)) ports.add(myPorts.getTickName(port));
			var first = true;
			var maxNrOfPorts = ports.size();
			for (final var port : ports) {
				maxNrOfPorts--;
				final var end = maxNrOfPorts == 0 ? " );" : ",";
				contents.add("{{1}}{{2}}{{3}}", first ? preamble : indenting, port, end);
				first = false;
			}
		}
		if (!myParametersList.isEmpty(attrs)) {
			body.empty().addRemarkBlock("Here all module parameters are defined with a dummy value");
			final var parameters = new TreeSet<String>();
			for (final var paramId : myParametersList.keySet(attrs)) {
				// For verilog we specify a maximum vector, this seems the best way to do it
				final var paramName = myParametersList.isPresentedByInteger(paramId, attrs)
						? myParametersList.get(paramId, attrs) : String.format("[64:0] %s", myParametersList.get(paramId, attrs));
				parameters.add(paramName);
			}
			for (final var param : parameters)
				body.add(String.format("parameter %s = 1;", param));
		}
		if (myTypedWires.getNrOfTypes() > 0) {
			body.empty()
					.addRemarkBlock("Here all private types are defined")
					.add(myTypedWires.getTypeDefinitions());
		}
		final var inputs = myPorts.keySet(Port.INPUT);
		for (final var input : myPorts.keySet(Port.INPUT)) {
			if (myPorts.isClock(input))
				inputs.add(myPorts.getTickName(input));
		}
		if (!inputs.isEmpty()) {
			body.empty().addRemarkBlock("The inputs are defined here");
			if (!getVerilogSignalSet("input", inputs, attrs, true, body)) return null;
		}
		final var outputs = myPorts.keySet(Port.OUTPUT);
		if (!outputs.isEmpty()) {
			body.empty().addRemarkBlock("The outputs are defined here");
			if (!getVerilogSignalSet("output", outputs, attrs, true, body)) return null;
		}
		final var inouts = myPorts.keySet(Port.INOUT);
		if (!inouts.isEmpty()) {
			body.empty().addRemarkBlock("The inouts are defined here");
			if (!getVerilogSignalSet("inout", inouts, attrs, true, body)) return null;
		}
		final var wires = myWires.wireKeySet();
		if (!wires.isEmpty()) {
			body.empty().addRemarkBlock("The wires are defined here");
			if (!getVerilogSignalSet("wire", wires, attrs, false, body)) return null;
		}
		final var regs = myWires.registerKeySet();
		if (!regs.isEmpty()) {
			body.empty().addRemarkBlock("The registers are defined here");
			if (!getVerilogSignalSet("reg", regs, attrs, false, body)) return null;
		}
		final var typedWires = myTypedWires.getTypedWires();
		if (!typedWires.isEmpty()) {
			body.empty().addRemarkBlock("The type defined signals are defined here");
			final var sortedWires = new TreeSet<>(typedWires.keySet());
			var maxNameLength = 0;
			for (final var wire : sortedWires)
				maxNameLength = Math.max(maxNameLength, typedWires.get(wire).length());
			for (final var wire : sortedWires) {
				final var typeName = typedWires.get(wire);
				body.add(LineBuffer.format("{{1}}{{2}} {{3}};", typeName, " ".repeat(maxNameLength - typeName.length()), wire));
			}
		}
		body.empty()
				.addRemarkBlock("The module functionality is described here")
				.add(getModuleFunctionality(theNetlist, attrs));
		contents.add(body.getWithIndent()).add("endmodule");

		return contents.get();
	}

	@Override
	public LineBuffer getComponentMap(Netlist nets, Long componentId, Object componentInfo, String name) {
		final var contents = LineBuffer.getHdlBuffer();
		final var parameterMap = new TreeMap<String, String>();
		final var portMap = getPortMap(nets, componentInfo);
		final var componentHdlName =
				(componentInfo instanceof netlistComponent)
						? ((netlistComponent)componentInfo).getComponent().getFactory().getHDLName(((netlistComponent) componentInfo).getComponent().getAttributeSet())
						: name;
		final var compName = StringUtil.isNotEmpty(name) ? name : componentHdlName;
		final var thisInstanceIdentifier = getInstanceIdentifier(componentInfo, componentId);
		final var oneLine = new StringBuilder();
		if (componentInfo == null) parameterMap.putAll(myParametersList.getMaps(null));
		else if (componentInfo instanceof netlistComponent) {
			netlistComponent comp = (netlistComponent) componentInfo;
			final var attrs = comp.getComponent().getAttributeSet();
			parameterMap.putAll(myParametersList.getMaps(attrs));
		}
		var tabLength = 0;
		var first = true;

		oneLine.append(compName);
		if (!parameterMap.isEmpty()) {
			oneLine.append(" #(");
			tabLength = oneLine.length();
			first = true;
			for (var parameter : parameterMap.keySet()) {
				if (!first) {
					oneLine.append(",");
					contents.add(oneLine.toString());
					oneLine.setLength(0);
					oneLine.append(" ".repeat(tabLength));
				} else first = false;
				oneLine.append(".").append(parameter).append("(").append(parameterMap.get(parameter)).append(")");
			}
			oneLine.append(")");
			contents.add(oneLine.toString());
			oneLine.setLength(0);
		}
		oneLine.append("   ").append(thisInstanceIdentifier).append(" (");
		if (!portMap.isEmpty()) {
			tabLength = oneLine.length();
			first = true;
			for (var port : portMap.keySet()) {
				if (!first) {
					oneLine.append(",");
					contents.add(oneLine.toString());
					oneLine.setLength(0);
					oneLine.append(" ".repeat(tabLength));
				} else first = false;
				oneLine.append(".").append(port).append("(");
				final var MappedSignal = portMap.get(port);
				if (!MappedSignal.contains(",")) {
					oneLine.append(MappedSignal);
				} else {
					final var vectorList = MappedSignal.split(",");
					oneLine.append("{");
					var tabSize = oneLine.length();
					for (var vectorEntries = 0; vectorEntries < vectorList.length; vectorEntries++) {
						oneLine.append(vectorList[vectorEntries].replace("}", "").replace("{", ""));
						if (vectorEntries < vectorList.length - 1) {
							contents.add(oneLine + ",");
							oneLine.setLength(0);
							oneLine.append(" ".repeat(tabSize));
						} else {
							oneLine.append("}");
						}
					}
				}
				oneLine.append(")");
			}
		}
		oneLine.append(");");
		contents.add(oneLine.toString());

		return contents;
	}

	private String getInstanceIdentifier(Object componentInfo, Long componentId) {
		if (componentInfo instanceof netlistComponent) {
			netlistComponent comp = (netlistComponent) componentInfo;
			final var attrs = comp.getComponent().getAttributeSet();
			if (attrs.containsAttribute(StdAttr.LABEL)) {
				final var label = attrs.getValue(StdAttr.LABEL);
				if (StringUtil.isNotEmpty(label)) {
					return CorrectLabel.getCorrectLabel(label);
				}
			}
		}
		return LineBuffer.format("{{1}}_{{2}}", subDirectoryName.toUpperCase(), componentId.toString());
	}

	/* Here all public entries for HDL generation are defined */
	@Override
	public LineBuffer getInlinedCode(Netlist nets, Long componentId, netlistComponent componentInfo, String circuitName) {
		throw new IllegalAccessError("BUG: Inline code not supported");
	}

	public LineBuffer getModuleFunctionality(Netlist netlist, AttributeSet attrs) {
		/*
		 * In this method the functionality of the black-box is described. It is
		 * used for both VHDL and VERILOG.
		 */
		return LineBuffer.getHdlBuffer();
	}

	public Map<String, String> getPortMap(Netlist nets, Object mapInfo) {
		final var result = new TreeMap<String, String>();
		if ((mapInfo instanceof netlistComponent) && !myPorts.isEmpty()) {
			netlistComponent componentInfo = (netlistComponent) mapInfo;
			final var compName = componentInfo.getComponent().getFactory().getDisplayName();
			final var attrs = componentInfo.getComponent().getAttributeSet();
			if (getWiresPortsDuringHDLWriting) {
				myWires.removeWires();
				myTypedWires.clear();
				myPorts.removePorts();
				getGenerationTimeWiresPorts(nets, componentInfo.getComponent().getAttributeSet());
			}
			for (var port : myPorts.keySet()) {
				if (myPorts.isClock(port)) {
					var gatedClock = false;
					var hasClock = true;
					var clockAttr = attrs.containsAttribute(StdAttr.EDGE_TRIGGER)
							? attrs.getValue(StdAttr.EDGE_TRIGGER) : attrs.getValue(StdAttr.TRIGGER);
					if (clockAttr == null)
						clockAttr = StdAttr.TRIG_RISING; // default case if no other specified (for TTL library)
					final var activeLow = StdAttr.TRIG_LOW.equals(clockAttr) || StdAttr.TRIG_FALLING.equals(clockAttr);
					final var compPinId = myPorts.getComponentPortId(port);
					if (!componentInfo.isEndConnected(compPinId)) {
						// FIXME hard coded string
						Reporter.report.addSevereWarning(
								String.format("Component \"%s\" in circuit \"%s\" has no clock connection!", compName, nets.getCircuitName()));
						hasClock = false;
					}
					final var clockNetName = Hdl.getClockNetName(componentInfo, compPinId, nets);
					if (StringUtil.isNullOrEmpty(clockNetName)) {
						// FIXME hard coded string
						Reporter.report.addSevereWarning(
								String.format("Component \"%s\" in circuit \"%s\" has a gated clock connection!", compName, nets.getCircuitName()));
						gatedClock = true;
					}
					if (hasClock && !gatedClock && Netlist.isFlipFlop(attrs)) {
						if (nets.requiresGlobalClockConnection()) {
							result.put(myPorts.getTickName(port), LineBuffer
									.formatHdl("{{1}}{{<}}{{2}}{{>}}", clockNetName, ClockHdlGeneratorFactory.GLOBAL_CLOCK_INDEX));
						} else {
							final var clockIndex = activeLow ? ClockHdlGeneratorFactory.NEGATIVE_EDGE_TICK_INDEX : ClockHdlGeneratorFactory.POSITIVE_EDGE_TICK_INDEX;
							result.put(myPorts.getTickName(port), LineBuffer.formatHdl("{{1}}{{<}}{{2}}{{>}}", clockNetName, clockIndex));
						}
						result.put(HdlPorts.CLOCK, LineBuffer
								.formatHdl("{{1}}{{<}}{{2}}{{>}}", clockNetName, ClockHdlGeneratorFactory.GLOBAL_CLOCK_INDEX));
					} else if (!hasClock) {
						result.put(myPorts.getTickName(port), Hdl.zeroBit());
						result.put(HdlPorts.CLOCK, Hdl.zeroBit());
					} else {
						result.put(myPorts.getTickName(port), Hdl.oneBit());
						if (!gatedClock) {
							final var clockIndex = activeLow ? ClockHdlGeneratorFactory.INVERTED_DERIVED_CLOCK_INDEX : ClockHdlGeneratorFactory.DERIVED_CLOCK_INDEX;
							result.put(HdlPorts.CLOCK, LineBuffer.formatHdl("{{1}}{{<}}{{2}}{{>}}", clockNetName, clockIndex));
						} else {
							result.put(HdlPorts.CLOCK, Hdl.getNetName(componentInfo, compPinId, true, nets));
						}
					}
				} else if (myPorts.isFixedMapped(port)) {
					final var fixedMap = myPorts.getFixedMap(port);
					if (HdlPorts.PULL_DOWN.equals(fixedMap))
						result.put(port, Hdl.getConstantVector(0, myPorts.get(port, attrs)));
					else if (HdlPorts.PULL_UP.equals(fixedMap))
						result.put(port, Hdl.getConstantVector(0xFFFFFFFFFFFFFFFFL, myPorts.get(port, attrs)));
					else
						result.put(port, fixedMap);
				} else {
					result.putAll(Hdl.getNetMap(port, myPorts.doPullDownOnFloat(port), componentInfo, myPorts.getComponentPortId(port), nets));
				}
			}
		}
		return result;
	}

	@Override
	public String getRelativeDirectory() {
		final StringBuilder directoryName = new StringBuilder();
		if (!subDirectoryName.isEmpty()) {
			directoryName.append(subDirectoryName);
			if (!subDirectoryName.endsWith(File.separator)) directoryName.append(File.separator);
		}
		return directoryName.toString();
	}

	private boolean addPortEntry(LineBuffer contents, boolean firstEntry, int nrOfEntries, int currentEntry,
								 String name, String direction, String type, int maxLength) {
		final var fmt = firstEntry
				? "   {{port}} ( {{1}}{{2}}: {{3}} {{4}}{{5}};"
				: "          {{1}}{{2}}: {{3}} {{4}}{{5}};";
		contents.add(fmt, name, " ".repeat(maxLength - name.length()), direction, type, currentEntry == (nrOfEntries - 1) ? " )" : "");

		// FIXME: refactor code that uses this retval, because as it's a const, then the logic using it can probably be simplified.
		return false;
	}

	private boolean getVerilogSignalSet(String preamble, List<String> signals, AttributeSet attrs, boolean isPort, LineBuffer contents) {
		if (signals.isEmpty()) return true;
		final var signalSet = new HashMap<String, String>();
		for (final var input : signals) {
			// this we have to check for the tick
			final var nrOfBits = isPort ? myPorts.contains(input) ? myPorts.get(input, attrs) : 1 : myWires.get(input);
			if (nrOfBits < 0) {
				if (myParametersList.containsKey(nrOfBits, attrs)) {
					signalSet.put(input, String.format("%s [%s-1:0]", preamble, myParametersList.get(nrOfBits, attrs)));
				} else {
					// FIXME: hard coded String
					Reporter.report.addFatalError("Internal Error, Parameter not present in HDL generation, your HDL code will not work!");
					return false;
				}
			} else if (nrOfBits == 0) {
				signalSet.put(input, String.format("%s [0:0]", preamble));
			} else if (nrOfBits > 1) {
				signalSet.put(input, String.format("%s [%d:0]", preamble, nrOfBits - 1));
			} else {
				signalSet.put(input, preamble);
			}
		}
		final var sortedSignals = new TreeSet<>(signalSet.keySet());
		var maxNameLength = 0;
		for (final var signal : sortedSignals)
			maxNameLength = Math.max(maxNameLength, signalSet.get(signal).length());
		for (final var signal : sortedSignals) {
			final var type = signalSet.get(signal);
			contents.add(LineBuffer.format("{{1}}{{2}} {{3}};", type, " ".repeat(maxNameLength - type.length()), signal));
		}
		return true;
	}

	@Override
	public boolean isHdlSupportedTarget(AttributeSet attrs) {
		return true;
	}

	@Override
	public boolean isOnlyInlined() {
		return false;
	}
}

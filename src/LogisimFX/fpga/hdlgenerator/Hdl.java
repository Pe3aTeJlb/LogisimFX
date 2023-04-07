/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.hdlgenerator;

import LogisimFX.fpga.Reporter;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.file.FileWriter;
import LogisimFX.util.CollectionUtil;
import LogisimFX.util.LineBuffer;

import java.util.*;

public class Hdl {

	public static final String NET_NAME = "s_logisimfxNet";
	public static final String BUS_NAME = "s_logisimfxBus";

	/**
	 * Length of remark block special sequences (block open/close, line open/close).
	 */
	public static final int REMARK_MARKER_LENGTH = 3;

	private Hdl() {
		throw new IllegalStateException("Utility class. No instantiation allowed.");
	}

	public static String bracketOpen() {
		return "[";
	}

	public static String bracketClose() {
		return "]";
	}

	public static String getRemarkChar() {
		return "*";
	}

	/**
	 * Comment block opening sequence. Must be REMARK_BLOCK_SEQ_LEN long.
	 */
	public static String getRemarkBlockStart() {
		return "/**";
	}

	/**
	 * Comment block closing sequence. Must be REMARK_BLOCK_SEQ_LEN long.
	 */
	public static String getRemarkBlockEnd() {
		return "**/";
	}

	/**
	 * Comment block line (mid block) opening sequence. Must be REMARK_BLOCK_SEQ_LEN long.
	 */
	public static String getRemarkBlockLineStart() {
		return "** ";
	}

	/**
	 * Comment block line (mid block) closing sequence. Must be REMARK_BLOCK_SEQ_LEN long.
	 */
	public static String getRemarkBlockLineEnd() {
		return " **";
	}

	public static String getLineCommentStart() {
		return "// ";
	}

	public static String startIf(String condition) {
		return LineBuffer.formatHdl("if ({{1}}) begin", condition);
	}

	public static String elseStatement() {
		return "end else begin";
	}

	public static String elseIf(String condition) {
		return LineBuffer.formatHdl("end else if ({{1}}) begin", condition);
	}

	public static String endIf() {
		return "end";
	}

	public static String assignPreamble() {
		return "assign ";
	}

	public static String assignOperator() {
		return " = ";
	}

	public static String equalOperator() {
		return "==";
	}

	public static String notEqualOperator() {
		return "!=";
	}

	private static String typecast(String signal, boolean signed) {
		return (signed ? "$signed(" + signal + ")" : signal);
	}

	public static String greaterOperator(String signalOne, String signalTwo, boolean signed, boolean equal) {
		return LineBuffer.formatHdl("{{1}} >{{2}} {{3}}", typecast(signalOne, signed), equal ? "=" : "", typecast(signalTwo, signed));
	}

	public static String lessOperator(String signalOne, String signalTwo, boolean signed, boolean equal) {
		return LineBuffer.formatHdl("{{1}} <{{2}} {{3}}", typecast(signalOne, signed), equal ? "=" : "", typecast(signalTwo, signed));
	}

	public static String leqOperator(String signalOne, String signalTwo, boolean signed) {
		return lessOperator(signalOne, signalTwo, signed, true);
	}

	public static String geqOperator(String signalOne, String signalTwo, boolean signed) {
		return greaterOperator(signalOne, signalTwo, signed, true);
	}

	public static String risingEdge(String signal) {
		return "posedge " + signal;
	}

	public static String notOperator() {
		return "~";
	}

	public static String andOperator() {
		return "&";
	}

	public static String orOperator() {
		return "|";
	}

	public static String xorOperator() {
		return "^";
	}

	public static String addOperator(String signalOne, String signalTwo, boolean signed) {
		return ("")
				+ typecast(signalOne, signed)
				+ " + "
				+ typecast(signalTwo, signed)
				+ ("");
	}

	public static String subOperator(String signalOne, String signalTwo, boolean signed) {
		return ("")
				+ typecast(signalOne, signed)
				+ " - "
				+ typecast(signalTwo, signed)
				+ ("");
	}

	public static String shiftlOperator(String signal, int width, int distance, boolean arithmetic) {
		if (distance == 0) return signal;
		return LineBuffer.formatHdl("{{{1}}{{2}},{{{3}}{1'b0}}}", signal, splitVector(width - 1 - distance, 0), distance);
	}

	public static String shiftrOperator(String signal, int width, int distance, boolean arithmetic) {
		if (distance == 0) return signal;
		if (arithmetic) {
			return LineBuffer.formatHdl("{ {{{1}}{{{2}}[{{1}}-1]}},{{2}}{{3}}}", width, signal, splitVector(width - 1, width - distance));
		} else {
			return LineBuffer.formatHdl("{ {{{1}}{1'b0}},{{2}}{{3}}}", width, signal, splitVector(width - 1, width - distance));
		}
	}

	public static String sllOperator(String signal, int width, int distance) {
		return shiftlOperator(signal, width, distance, false);
	}

	public static String slaOperator(String signal, int width, int distance) {
		return shiftlOperator(signal, width, distance, true);
	}

	public static String srlOperator(String signal, int width, int distance) {
		return shiftrOperator(signal, width, distance, false);
	}

	public static String sraOperator(String signal, int width, int distance) {
		return shiftrOperator(signal, width, distance, true);
	}

	public static String rolOperator(String signal, int width, int distance) {
		return LineBuffer.formatHdl("{{1}}{{2}}{{3}}{{1}}{{4}}", signal, splitVector(width - 1 - distance, 0), (","), splitVector(width - 1, width - distance));
	}

	public static String rorOperator(String signal, int width, int distance) {
		return LineBuffer.formatHdl("{{1}}{{2}}{{3}}{{1}}{{4}}", signal, splitVector(distance, 0), (","), splitVector(width - 1, distance));
	}

	public static String zeroBit() {
		return "1'b0";
	}

	public static String oneBit() {
		return "1'b1";
	}

	public static String unconnected(boolean empty) {
		return empty ? "" : "'bz";
	}

	public static String vectorLoopId() {
		return ":";
	}

	public static String splitVector(int start, int end) {
		if (start == end) return LineBuffer.formatHdl("{{<}}{{2}}{{>}}", start);
		return LineBuffer.formatHdl("[{{1}}:{{2}}]", start, end);
	}

	public static String getZeroVector(int nrOfBits, boolean floatingPinTiedToGround) {
		final var contents = new StringBuilder();
		contents.append(nrOfBits).append("'d");
		contents.append(floatingPinTiedToGround ? "0" : "-1");
		return contents.toString();
	}

	public static String getConstantVector(long value, int nrOfBits) {
		final var nrHexDigits = nrOfBits / 4;
		final var nrSingleBits = nrOfBits % 4;
		final var hexDigits = new String[nrHexDigits];
		final var singleBits = new StringBuilder();
		var shiftValue = value;
		for (var hexIndex = nrHexDigits - 1; hexIndex >= 0; hexIndex--) {
			var hexValue = shiftValue & 0xFL;
			shiftValue >>= 4L;
			hexDigits[hexIndex] = String.format("%1X", hexValue);
		}
		final var hexValue = new StringBuilder();
		for (var hexIndex = 0; hexIndex < nrHexDigits; hexIndex++) {
			hexValue.append(hexDigits[hexIndex]);
		}
		var mask = (nrSingleBits == 0) ? 0 : 1L << (nrSingleBits - 1);
		while (mask > 0) {
			singleBits.append((shiftValue & mask) == 0 ? "0" : "1");
			mask >>= 1L;
		}
		// first case, we have to concatinate
		if ((nrHexDigits > 0) && (nrSingleBits > 0)) {
			return LineBuffer.format("{{{1}}'b{{2}}, {{3}}'h{{4}}}", nrSingleBits, singleBits.toString(),
					nrHexDigits * 4, hexValue.toString());
		}
		// second case, we have only hex digits
		if (nrHexDigits > 0) {
			return LineBuffer.format("{{1}}'h{{2}}", nrHexDigits * 4, hexValue.toString());
		}

		return LineBuffer.format("{{1}}'b{{2}}", nrSingleBits, singleBits.toString());
	}

	public static String getNetName(netlistComponent comp, int endIndex, boolean floatingNetTiedToGround, Netlist myNetlist) {
		var netName = "";
		if ((endIndex >= 0) && (endIndex < comp.nrOfEnds())) {
			final var floatingValue = floatingNetTiedToGround ? zeroBit() : oneBit();
			final var thisEnd = comp.getEnd(endIndex);
			final var isOutput = thisEnd.isOutputEnd();

			if (thisEnd.getNrOfBits() == 1) {
				final var solderPoint = thisEnd.get((byte) 0);
				if (solderPoint.getParentNet() == null) {
					// The net is not connected
					netName = LineBuffer.formatHdl(isOutput ? unconnected(true) : floatingValue);
				} else {
					// The net is connected, we have to find out if the connection
					// is to a bus or to a normal net.
					netName = (solderPoint.getParentNet().getBitWidth() == 1)
							? LineBuffer.formatHdl("{{1}}{{2}}", NET_NAME, myNetlist.getNetId(solderPoint.getParentNet()))
							: LineBuffer.formatHdl("{{1}}{{2}}{{<}}{{3}}{{>}}", BUS_NAME,
							myNetlist.getNetId(solderPoint.getParentNet()), solderPoint.getParentNetBitIndex());
				}
			}
		}
		return netName;
	}

	public static String getBusEntryName(netlistComponent comp, int endIndex, boolean floatingNetTiedToGround, int bitindex, Netlist theNets) {
		var busName = "";
		if ((endIndex >= 0) && (endIndex < comp.nrOfEnds())) {
			final var thisEnd = comp.getEnd(endIndex);
			final var isOutput = thisEnd.isOutputEnd();
			final var nrOfBits = thisEnd.getNrOfBits();
			if ((nrOfBits > 1) && (bitindex >= 0) && (bitindex < nrOfBits)) {
				if (thisEnd.get((byte) bitindex).getParentNet() == null) {
					// The net is not connected
					busName = LineBuffer.formatHdl(isOutput ? unconnected(false) : getZeroVector(1, floatingNetTiedToGround));
				} else {
					final var connectedNet = thisEnd.get((byte) bitindex).getParentNet();
					final var connectedNetBitIndex = thisEnd.get((byte) bitindex).getParentNetBitIndex();
					// The net is connected, we have to find out if the connection
					// is to a bus or to a normal net.
					busName =
							!connectedNet.isBus()
									? LineBuffer.formatHdl("{{1}}{{2}}", NET_NAME, theNets.getNetId(connectedNet))
									: LineBuffer.formatHdl("{{1}}{{2}}{{<}}{{3}}{{>}}", BUS_NAME, theNets.getNetId(connectedNet), connectedNetBitIndex);
				}
			}
		}
		return busName;
	}

	public static String getBusNameContinues(netlistComponent comp, int endIndex, Netlist theNets) {
		if ((endIndex < 0) || (endIndex >= comp.nrOfEnds())) return null;
		final var connectionInformation = comp.getEnd(endIndex);
		final var nrOfBits = connectionInformation.getNrOfBits();
		if (nrOfBits == 1) return getNetName(comp, endIndex, true, theNets);
		if (!theNets.isContinuesBus(comp, endIndex)) return null;
		final var connectedNet = connectionInformation.get((byte) 0).getParentNet();
		return LineBuffer.formatHdl("{{1}}{{2}}{{3}}",
				BUS_NAME,
				theNets.getNetId(connectedNet),
				splitVector(connectionInformation.get((byte) (connectionInformation.getNrOfBits() - 1)).getParentNetBitIndex(),
						connectionInformation.get((byte) (0)).getParentNetBitIndex()));
	}

	public static String getBusName(netlistComponent comp, int endIndex, Netlist theNets) {
		if ((endIndex < 0) || (endIndex >= comp.nrOfEnds())) return null;
		final var connectionInformation = comp.getEnd(endIndex);
		final var nrOfBits = connectionInformation.getNrOfBits();
		if (nrOfBits == 1) return getNetName(comp, endIndex, true, theNets);
		if (!theNets.isContinuesBus(comp, endIndex)) return null;
		final var connectedNet = connectionInformation.get((byte) 0).getParentNet();
		if (connectedNet.getBitWidth() != nrOfBits) return getBusNameContinues(comp, endIndex, theNets);
		return LineBuffer.format("{{1}}{{2}}", BUS_NAME, theNets.getNetId(connectedNet));
	}

	public static String getClockNetName(netlistComponent comp, int endIndex, Netlist theNets) {
		var contents = new StringBuilder();
		if ((theNets.getCurrentHierarchyLevel() != null) && (endIndex >= 0) && (endIndex < comp.nrOfEnds())) {
			final var endData = comp.getEnd(endIndex);
			if (endData.getNrOfBits() == 1) {
				final var connectedNet = endData.get((byte) 0).getParentNet();
				final var ConnectedNetBitIndex = endData.get((byte) 0).getParentNetBitIndex();
				/* Here we search for a clock net Match */
				final var clocksourceid = theNets.getClockSourceId(
						theNets.getCurrentHierarchyLevel(), connectedNet, ConnectedNetBitIndex);
				if (clocksourceid >= 0) {
					contents.append(HdlGeneratorFactory.CLOCK_TREE_NAME).append(clocksourceid);
				}
			}
		}
		return contents.toString();
	}

	public static boolean writeArchitecture(String targetDirectory, List<String> contents, String componentName) {
		if (CollectionUtil.isNullOrEmpty(contents)) {
			// FIXME: hardcoded string
			Reporter.report.addFatalErrorFmt("INTERNAL ERROR: Empty behavior description for Component '%s' received!", componentName);
			return false;
		}
		final var outFile = FileWriter.getFilePointer(targetDirectory, componentName);
		if (outFile == null) return false;
		return FileWriter.writeContents(outFile, contents);
	}

	public static Map<String, String> getNetMap(String sourceName, boolean floatingPinTiedToGround,
												netlistComponent comp, int endIndex, Netlist theNets) {
		final var netMap = new HashMap<String, String>();
		if ((endIndex < 0) || (endIndex >= comp.nrOfEnds())) {
			Reporter.report.addFatalError("INTERNAL ERROR: Component tried to index non-existing SolderPoint");
			return netMap;
		}
		final var connectionInformation = comp.getEnd(endIndex);
		final var isOutput = connectionInformation.isOutputEnd();
		final var nrOfBits = connectionInformation.getNrOfBits();
		if (nrOfBits == 1) {
			/* Here we have the easy case, just a single bit net */
			netMap.put(sourceName, getNetName(comp, endIndex, floatingPinTiedToGround, theNets));
		} else {
			/*
			 * Here we have the more difficult case, it is a bus that needs to
			 * be mapped
			 */
			/* First we check if the bus has a connection */
			var connected = false;
			for (var bit = 0; bit < nrOfBits; bit++) {
				if (connectionInformation.get((byte) bit).getParentNet() != null)
					connected = true;
			}
			if (!connected) {
				/* Here is the easy case, the bus is unconnected */
				netMap.put(sourceName, isOutput ? unconnected(true) : getZeroVector(nrOfBits, floatingPinTiedToGround));
			} else {
				/*
				 * There are connections, we detect if it is a continues bus
				 * connection
				 */
				if (theNets.isContinuesBus(comp, endIndex)) {
					/* Another easy case, the continues bus connection */
					netMap.put(sourceName, getBusNameContinues(comp, endIndex, theNets));
				} else {
					/* The last case, we have to enumerate through each bit */
					final var seperateSignals = new ArrayList<String>();
					/*
					 * First we build an array with all the signals that
					 * need to be concatenated
					 */
					for (var bit = 0; bit < nrOfBits; bit++) {
						final var solderPoint = connectionInformation.get((byte) bit);
						if (solderPoint.getParentNet() == null) {
							/* this entry is not connected */
							seperateSignals.add(isOutput ? "1'bZ" : getZeroVector(1, floatingPinTiedToGround));
						} else {
							/*
							 * The net is connected, we have to find out if
							 * the connection is to a bus or to a normal net
							 */
							if (solderPoint.getParentNet().getBitWidth() == 1) {
								/* The connection is to a Net */
								seperateSignals.add(String.format("%s%d", NET_NAME,
										theNets.getNetId(solderPoint.getParentNet())));
							} else {
								/* The connection is to an entry of a bus */
								seperateSignals.add(String.format("%s%d[%d]", BUS_NAME,
										theNets.getNetId(solderPoint.getParentNet()), solderPoint.getParentNetBitIndex()));
							}
						}
					}
					/* Finally we can put all together */
					final var vector = new StringBuilder();
					vector.append("{");
					for (var bit = nrOfBits; bit > 0; bit--) {
						vector.append(seperateSignals.get(bit - 1));
						if (bit != 1) {
							vector.append(",");
						}
					}
					vector.append("}");
					netMap.put(sourceName, vector.toString());

				}
			}
		}
		return netMap;
	}

	public static void addAllWiresSorted(LineBuffer contents, Map<String, String> wires) {
		var maxNameLength = 0;
		for (var wire : wires.keySet())
			maxNameLength = Math.max(maxNameLength, wire.length());
		final var sortedWires = new TreeSet<>(wires.keySet());
		for (var wire : sortedWires)
			contents.add("{{assign}}{{1}}{{2}}{{=}}{{3}};", wire, " ".repeat(maxNameLength - wire.length()), wires.get(wire));
		wires.clear();
	}

}

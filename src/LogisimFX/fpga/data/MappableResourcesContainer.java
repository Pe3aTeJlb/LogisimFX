/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.data;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitMapInfo;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;

public class MappableResourcesContainer {

	private final Circuit myCircuit;
	private final BoardInformation currentUsedBoard;
	protected Map<ArrayList<String>, MapComponent> myMappableResources;
	private final List<FpgaIoInformationContainer> myIOComponents;

	/*
	 * We differentiate two notation for each component, namely: 1) The display
	 * name: "LED: /LED1". This name can be augmented with alternates, e.g. a
	 * 7-segment display could either be: "SEVENSEGMENT: /DS1" or
	 * "SEVENSEGMENT: /DS1#Segment_A",etc.
	 *
	 * 2) The map name: "FPGA4U:/LED1" or "FPGA4U:/DS1#Segment_A", etc.
	 *
	 * The MappedList keeps track of the display names.
	 */
	public MappableResourcesContainer(BoardInformation CurrentBoard, Circuit circ) {
		currentUsedBoard = CurrentBoard;
		myCircuit = circ;
		var BoardId = new ArrayList<String>();
		BoardId.add(CurrentBoard.getBoardName());
		myIOComponents = new ArrayList<>();
		for (var io : currentUsedBoard.getAllComponents()) {
			try {
				FpgaIoInformationContainer clone = (FpgaIoInformationContainer) io.clone();
				clone.setMapMode();
				myIOComponents.add(clone);
			} catch (CloneNotSupportedException e) {
			}
		}
		updateMapableComponents();
		circ.setBoardMap(CurrentBoard.getBoardName(), this);
	}

	/*
	 * Here we define the new structure of MappableResourcesContainer that allows for more features
	 * and has less complexity; being compatible with the old version
	 */

	public Map<ArrayList<String>, MapComponent> getMappableResources() {
		return myMappableResources;
	}

	public String getToplevelName() {
		return myCircuit.getName();
	}

	public BoardInformation getBoardInformation() {
		return currentUsedBoard;
	}

	public void updateMapableComponents() {
		var cur = new HashSet<ArrayList<String>>();
		if (myMappableResources == null){
			myMappableResources = new HashMap<>();
		} else {
			cur.addAll(myMappableResources.keySet());
		}
		var BoardId = new ArrayList<String>();
		BoardId.add(currentUsedBoard.getBoardName());
		var newMappableResources = myCircuit.getNetList().getMappableResources(BoardId, true);
		for (var key : newMappableResources.keySet()) {
			if (cur.contains(key)) {
				var comp = myMappableResources.get(key);
				if (!comp.equalsType(newMappableResources.get(key))) {
					comp.unmap();
					myMappableResources.put(key, new MapComponent(key, newMappableResources.get(key)));
				} else {
					var newMap = new MapComponent(key, newMappableResources.get(key));
					newMap.copyMapFrom(comp);
					myMappableResources.put(key, newMap);
				}
				cur.remove(key);
			} else {
				myMappableResources.put(key, new MapComponent(key, newMappableResources.get(key)));
			}
		}
		for (var key : cur) {
			myMappableResources.get(key).unmap();
			myMappableResources.remove(key);
		}
	}

	public void tryMap(String mapKey, CircuitMapInfo cmap) {
		var key = getHierarchyName(mapKey);
		if (!myMappableResources.containsKey(key)) return;
		if (mapKey.contains("#")) myMappableResources.get(key).tryMap(mapKey, cmap, myIOComponents);
		else myMappableResources.get(key).tryMap(cmap, myIOComponents);
	}

	public void tryMap(String mapKey, BoardRectangle rect) {
		tryMap(mapKey, new CircuitMapInfo(rect));
	}

	public Map<String, CircuitMapInfo> getCircuitMap() {
		var id = 0;
		var result = new HashMap<String, CircuitMapInfo>();
		for (var key : myMappableResources.keySet()) {
			result.put(Integer.toString(id++), new CircuitMapInfo(myMappableResources.get(key)));
		}
		return result;
	}

	public void unMapAll() {
		for (var key : myMappableResources.keySet()) myMappableResources.get(key).unmap();
	}

	private ArrayList<String> getHierarchyName(String mapKey) {
		final var split1 = mapKey.split(" ");
		final var hier = split1[split1.length - 1];
		final var split2 = hier.split("#");
		var result = new ArrayList<String>();
		result.add(currentUsedBoard.getBoardName());
		for (var key : split2[0].split("/")) if (!key.isEmpty()) result.add(key);
		return result;
	}

	public boolean isCompletelyMapped() {
		var result = true;
		for (var key : myMappableResources.keySet()) {
			var map = myMappableResources.get(key);
			for (var i = 0; i < map.getNrOfPins(); i++) result &= map.isMapped(i);
		}
		return result;
	}

	public List<String> getMappedIoPinNames() {
		final var result = new ArrayList<String>();
		for (final var key : myMappableResources.keySet()) {
			final var map = myMappableResources.get(key);
			for (var i = 0; i < map.getNrOfPins(); i++) {
				if (!map.isIo(i) || map.isInternalMapped(i)) continue;
				if (map.isBoardMapped(i)) {
					final var sb = new StringBuilder();
					if (map.isExternalInverted(i)) sb.append("n_");
					sb.append(map.getHdlString(i));
					result.add(sb.toString());
				}
			}
		}
		return result;
	}

	public List<String> getMappedInputPinNames() {
		final var result = new ArrayList<String>();
		for (final var key : myMappableResources.keySet()) {
			final var map = myMappableResources.get(key);
			for (var i = 0; i < map.getNrOfPins(); i++) {
				if (!map.isInput(i) || map.isInternalMapped(i)) continue;
				if (map.isBoardMapped(i)) {
					final var sb = new StringBuilder();
					if (map.isExternalInverted(i)) sb.append("n_");
					sb.append(map.getHdlString(i));
					result.add(sb.toString());
				}
			}
		}
		return result;
	}

	public List<String> getMappedOutputPinNames() {
		final var result = new ArrayList<String>();
		for (final var key : myMappableResources.keySet()) {
			final var map = myMappableResources.get(key);
			for (var i = 0; i < map.getNrOfPins(); i++) {
				if (!map.isOutput(i) || map.isInternalMapped(i)) continue;
				if (map.isBoardMapped(i)) {
					final var sb = new StringBuilder();
					if (map.isExternalInverted(i)) sb.append("n_");
					sb.append(map.getHdlString(i));
					result.add(sb.toString());
				}
			}
		}
		return result;
	}

	public List<FpgaIoInformationContainer> getIOComponents(){
		return myIOComponents;
	}

}

/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.data;

import LogisimFX.fpga.file.BoardReaderClass;
import LogisimFX.newgui.IOMapper.IOMapperController;
import LogisimFX.std.io.DipSwitch;
import LogisimFX.std.io.DotMatrix;
import LogisimFX.std.io.LedBar;
import LogisimFX.std.io.RgbLed;
import LogisimFX.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.*;

public class FpgaIoInformationContainer implements Cloneable {

	private static class mapType {
		private LogisimFX.fpga.data.MapComponent map;
		private final int pin;

		public mapType(LogisimFX.fpga.data.MapComponent map, int pin) {
			this.map = map;
			this.pin = pin;
		}

		public void unmap() {
			map.unmap(pin);
		}

		public void update(LogisimFX.fpga.data.MapComponent map) {
			this.map = map;
		}

		public LogisimFX.fpga.data.MapComponent getMap() {
			return map;
		}
	}

	public static class MapResultClass {
		public boolean mapResult;
		public int pinId;
	}

	public static List<String> getComponentTypes() {
		final var result = new LinkedList<String>();
		for (final var comp : IoComponentTypes.KNOWN_COMPONENT_SET) {
			result.add(comp.toString());
		}
		return result;
	}

	static final Logger logger = LoggerFactory.getLogger(FpgaIoInformationContainer.class);

	private IoComponentTypes myType;
	protected BoardRectangle myRectangle;
	protected int myRotation = IoComponentTypes.ROTATION_ZERO;
	private Map<Integer, String> myPinLocations;
	private Set<Integer> myInputPins;
	private Set<Integer> myOutputPins;
	private Set<Integer> myIoPins;
	private Integer[][] partialMapArray;
	private Integer nrOfPins;
	private Integer nrOfExternalPins = 0;
	private Integer myArrayId = -1;
	private char myPullBehavior;
	private char myActivityLevel;
	private char myIoStandard;
	private char myDriveStrength;
	private String myLabel;
	private boolean toBeDeleted = false;
	private ArrayList<mapType> pinIsMapped;
	private boolean mapMode = false;
	private int nrOfRows = 4;
	private int nrOfColumns = 4;
	private char driving = LedArrayDriving.LED_DEFAULT;
	protected boolean selectable = false;
	protected int selectedPin = -1;
	protected IOMapperController.MapInfo selComp = null;

	public FpgaIoInformationContainer() {
		myType = IoComponentTypes.Unknown;
		myRectangle = null;
		myPinLocations = new HashMap<>();
		setNrOfPins(0);
		myPullBehavior = LogisimFX.fpga.data.PullBehaviors.UNKNOWN;
		myActivityLevel = PinActivity.Unknown;
		myIoStandard = LogisimFX.fpga.data.IoStandards.UNKNOWN;
		myDriveStrength = LogisimFX.fpga.data.DriveStrength.UNKNOWN;
		myLabel = null;
	}

	public FpgaIoInformationContainer(Node DocumentInfo) {
		/*
		 * This constructor is used to create an element during the reading of a
		 * board information xml file
		 */
		myType = IoComponentTypes.Unknown;
		myRectangle = null;
		myPinLocations = new HashMap<>();
		setNrOfPins(0);
		myPullBehavior = LogisimFX.fpga.data.PullBehaviors.UNKNOWN;
		myActivityLevel = PinActivity.Unknown;
		myIoStandard = LogisimFX.fpga.data.IoStandards.UNKNOWN;
		myDriveStrength = LogisimFX.fpga.data.DriveStrength.UNKNOWN;
		myLabel = null;
		ArrayList<String> InputLocs = new ArrayList<>();
		ArrayList<String> OutputLocs = new ArrayList<>();
		ArrayList<String> IOLocs = new ArrayList<>();
		IoComponentTypes SetId = IoComponentTypes.getEnumFromString(DocumentInfo.getNodeName());
		if (IoComponentTypes.KNOWN_COMPONENT_SET.contains(SetId)) {
			myType = SetId;
		} else {
			return;
		}
		var attrs = DocumentInfo.getAttributes();
		int x = -1, y = -1, width = -1, height = -1;
		for (var attributeIndex = 0; attributeIndex < attrs.getLength(); attributeIndex++) {
			final var thisAttr = attrs.item(attributeIndex);
			if (thisAttr.getNodeName().equals(BoardReaderClass.MAP_ROTATION)) {
				myRotation = Integer.parseInt(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.LOCATION_X_STRING)) {
				x = Integer.parseInt(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.LOCATION_Y_STRING)) {
				y = Integer.parseInt(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.WIDTH_STRING)) {
				width = Integer.parseInt(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.HEIGHT_STRING)) {
				height = Integer.parseInt(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.RECT_SET_STRING)) {
				final var vals = thisAttr.getNodeValue().split(",");
				if (vals.length == 4) {
					try {
						x = Integer.parseUnsignedInt(vals[0]);
						y = Integer.parseUnsignedInt(vals[1]);
						width = Integer.parseUnsignedInt(vals[2]);
						height = Integer.parseUnsignedInt(vals[3]);
					} catch (NumberFormatException e) {
						x = y = width = height = -1;
					}
				}
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.LED_ARRAY_INFO_STRING)) {
				final var vals = thisAttr.getNodeValue().split(",");
				if (vals.length == 3) {
					try {
						nrOfRows = Integer.parseUnsignedInt(vals[0]);
						nrOfColumns = Integer.parseUnsignedInt(vals[1]);
						driving = LedArrayDriving.getId(vals[2]);
					} catch (NumberFormatException e) {
						nrOfRows = nrOfColumns = 4;
						driving = LedArrayDriving.LED_DEFAULT;
					}
				}
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.PIN_LOCATION_STRING)) {
				setNrOfPins(1);
				myPinLocations.put(0, thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.MULTI_PIN_INFORMATION_STRING)) {
				setNrOfPins(Integer.parseInt(thisAttr.getNodeValue()));
			}
			if (thisAttr.getNodeName().startsWith(BoardReaderClass.MULTI_PIN_PREFIX_STRING)) {
				String Id =
						thisAttr.getNodeName().substring(BoardReaderClass.MULTI_PIN_PREFIX_STRING.length());
				myPinLocations.put(Integer.parseInt(Id), thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(BoardReaderClass.LABEL_STRING)) {
				myLabel = thisAttr.getNodeValue();
			}
			if (thisAttr.getNodeName().equals(LogisimFX.fpga.data.DriveStrength.DRIVE_ATTRIBUTE_STRING)) {
				myDriveStrength = LogisimFX.fpga.data.DriveStrength.getId(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(LogisimFX.fpga.data.PullBehaviors.PULL_ATTRIBUTE_STRING)) {
				myPullBehavior = LogisimFX.fpga.data.PullBehaviors.getId(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(LogisimFX.fpga.data.IoStandards.IO_ATTRIBUTE_STRING)) {
				myIoStandard = LogisimFX.fpga.data.IoStandards.getId(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().equals(PinActivity.ACTIVITY_ATTRIBUTE_STRING)) {
				myActivityLevel = PinActivity.getId(thisAttr.getNodeValue());
			}
			if (thisAttr.getNodeName().contentEquals(BoardReaderClass.INPUT_SET_STRING)) {
				InputLocs.addAll(Arrays.asList(thisAttr.getNodeValue().split(",")));
			}
			if (thisAttr.getNodeName().contentEquals(BoardReaderClass.OUTPUT_SET_STRING)) {
				OutputLocs.addAll(Arrays.asList(thisAttr.getNodeValue().split(",")));
			}
			if (thisAttr.getNodeName().contentEquals(BoardReaderClass.IO_SET_STRING)) {
				IOLocs.addAll(Arrays.asList(thisAttr.getNodeValue().split(",")));
			}
		}
		if ((x < 0) || (y < 0) || (width < 1) || (height < 1)) {
			myType = IoComponentTypes.Unknown;
			return;
		}
		var idx = 0;
		for (var loc : InputLocs) {
			myPinLocations.put(idx, loc);
			if (myInputPins == null) myInputPins = new HashSet<>();
			myInputPins.add(idx++);
		}
		for (var loc : OutputLocs) {
			myPinLocations.put(idx, loc);
			if (myOutputPins == null) myOutputPins = new HashSet<>();
			myOutputPins.add(idx++);
		}
		for (var loc : IOLocs) {
			myPinLocations.put(idx, loc);
			if (myIoPins == null) myIoPins = new HashSet<>();
			myIoPins.add(idx++);
		}
		if (idx != 0) setNrOfPins(idx);
		var PinsComplete = true;
		for (var i = 0; i < nrOfPins; i++) {
			if (!myPinLocations.containsKey(i)) {
				logger.warn("Bizar missing pin {} of component!", i);
				PinsComplete = false;
			}
		}
		if (!PinsComplete) {
			myType = IoComponentTypes.Unknown;
			return;
		}
		/* This code is for backward compatibility */
		if (myInputPins == null && myOutputPins == null && myIoPins == null) {
			var NrInpPins = IoComponentTypes.getFpgaInputRequirement(myType);
			var NrOutpPins = IoComponentTypes.getFpgaOutputRequirement(myType);
			for (var i = 0; i < nrOfPins; i++) {
				if (i < NrInpPins) {
					if (myInputPins == null) myInputPins = new HashSet<>();
					myInputPins.add(i);
				} else if (i < (NrInpPins + NrOutpPins)) {
					if (myOutputPins == null) myOutputPins = new HashSet<>();
					myOutputPins.add(i);
				} else {
					if (myIoPins == null) myIoPins = new HashSet<>();
					myIoPins.add(i);
				}
			}
		}
		/* End backward compatibility */
		if (myType.equals(IoComponentTypes.Pin)) myActivityLevel = PinActivity.ACTIVE_HIGH;
		myRectangle = new BoardRectangle(this, x, y, width, height);
		if (myType.equals(IoComponentTypes.LedArray)) {
			nrOfExternalPins = nrOfPins;
			nrOfPins = nrOfRows * nrOfColumns;
			setNrOfPins(nrOfPins);
			myOutputPins.clear();
			for (var i = 0; i < nrOfPins; i++)
				myOutputPins.add(i);
		}
	}

	public void setArrayId(int val) {
		myArrayId = val;
	}

	public int getArrayId() {
		return myArrayId;
	}

	public void setMapRotation(int val) {
		if ((val == IoComponentTypes.ROTATION_CW_90)
				|| (val == IoComponentTypes.ROTATION_CCW_90)
				|| (val == IoComponentTypes.ROTATION_ZERO))
			myRotation = val;
	}

	public int getMapRotation() {
		return myRotation;
	}

	public int getExternalPinCount() {
		return nrOfExternalPins;
	}

	public boolean hasMap() {
		var ret = false;
		for (var i = 0; i < nrOfPins; i++) {
			ret |= isPinMapped(i);
		}
		return ret;
	}

	public int getNrOfInputPins() {
		return (myInputPins == null) ? 0 : myInputPins.size();
	}

	public int getNrOfOutputPins() {
		return (myOutputPins == null) ? 0 : myOutputPins.size();
	}

	public int getNrOfRows() {
		return nrOfRows;
	}

	public int getNrOfColumns() {
		return nrOfColumns;
	}

	public char getArrayDriveMode() {
		return driving;
	}

	public void setNrOfRows(int value) {
		nrOfRows = value;
	}

	public void setNrOfColumns(int value) {
		nrOfColumns = value;
	}

	public void setArrayDriveMode(char value) {
		driving = value;
	}

	public void setMapMode() {
		mapMode = true;
	}

	public void setToBeDeleted() {
		toBeDeleted = true;
	}

	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	public char getActivityLevel() {
		return myActivityLevel;
	}

	public void setActivityLevel(char activity) {
		myActivityLevel = activity;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		var clone = new FpgaIoInformationContainer();
		clone.myType = myType;
		clone.myRectangle = myRectangle;
		clone.myRectangle.updateFpgaIoInformationContainer(clone);
		clone.myRotation = myRotation;
		clone.myPinLocations = myPinLocations;
		clone.myInputPins = myInputPins;
		clone.myOutputPins = myOutputPins;
		clone.myIoPins = myIoPins;
		clone.nrOfPins = nrOfPins;
		clone.nrOfExternalPins = nrOfExternalPins;
		clone.myArrayId = myArrayId;
		clone.myPullBehavior = myPullBehavior;
		clone.myActivityLevel = myActivityLevel;
		clone.myIoStandard = myIoStandard;
		clone.myDriveStrength = myDriveStrength;
		clone.myLabel = myLabel;
		clone.driving = driving;
		clone.nrOfRows = nrOfRows;
		clone.nrOfColumns = nrOfColumns;
		for (var pinId = 0; pinId < nrOfPins; pinId++) {
			clone.pinIsMapped.add(null);
		}
		return clone;
	}

	public String getPinLocation(int index) {
		return myPinLocations.getOrDefault(index, "");
	}

	public void setInputPinLocation(int index, String value) {
		if (myOutputPins != null) myOutputPins.remove(index);
		if (myIoPins != null) myIoPins.remove(index);
		if (myInputPins == null) myInputPins = new HashSet<>();
		myInputPins.add(index);
		myPinLocations.put(index, value);
	}

	public void setOutputPinLocation(int index, String value) {
		if (myInputPins != null) myInputPins.remove(index);
		if (myIoPins != null) myIoPins.remove(index);
		if (myOutputPins == null) myOutputPins = new HashSet<>();
		myOutputPins.add(index);
		myPinLocations.put(index, value);
	}

	public void setIOPinLocation(int index, String value) {
		if (myInputPins != null) myInputPins.remove(index);
		if (myOutputPins != null) myOutputPins.remove(index);
		if (myIoPins == null) myIoPins = new HashSet<>();
		myIoPins.add(index);
		myPinLocations.put(index, value);
	}

	public Element getDocumentElement(Document doc) {
		if (myType.equals(IoComponentTypes.Unknown)) return null;
		try {
			var result = doc.createElement(myType.toString());
			result.setAttribute(
					BoardReaderClass.RECT_SET_STRING,
					myRectangle.getX()
							+ ","
							+ myRectangle.getY()
							+ ","
							+ myRectangle.getWidth()
							+ ","
							+ myRectangle.getHeight());
			if (myLabel != null) {
				var label = doc.createAttribute(BoardReaderClass.LABEL_STRING);
				label.setValue(myLabel);
				result.setAttributeNode(label);
			}
			if (myType.equals(IoComponentTypes.LedArray)) {
				result.setAttribute(
						BoardReaderClass.LED_ARRAY_INFO_STRING,
						nrOfRows
								+ ","
								+ nrOfColumns
								+ ","
								+ LedArrayDriving.getStrings().get(driving));
			}
			if (IoComponentTypes.hasRotationAttribute(myType)) {
				if (myRotation == IoComponentTypes.ROTATION_CW_90 || myRotation == IoComponentTypes.ROTATION_CCW_90){
					result.setAttribute(BoardReaderClass.MAP_ROTATION, Integer.toString(myRotation));
				}
			}
			if (CollectionUtil.isNotEmpty(myInputPins)) {
				final var attrSet = doc.createAttribute(BoardReaderClass.INPUT_SET_STRING);
				final var sb = new StringBuilder();
				var first = true;
				for (var i = 0; i < nrOfPins; i++)
					if (myInputPins.contains(i)) {
						if (first) first = false;
						else sb.append(",");
						sb.append(myPinLocations.get(i));
					}
				attrSet.setValue(sb.toString());
				result.setAttributeNode(attrSet);
			}
			if (CollectionUtil.isNotEmpty(myOutputPins)) {
				final var attrSet = doc.createAttribute(BoardReaderClass.OUTPUT_SET_STRING);
				final var sb = new StringBuilder();
				var first = true;
				for (var i = 0; i < nrOfPins; i++)
					if (myOutputPins.contains(i)) {
						if (first) first = false;
						else sb.append(",");
						sb.append(myPinLocations.get(i));
					}
				attrSet.setValue(sb.toString());
				result.setAttributeNode(attrSet);
			}
			if (CollectionUtil.isNotEmpty(myIoPins)) {
				final var attrSet = doc.createAttribute(BoardReaderClass.IO_SET_STRING);
				final var sb = new StringBuilder();
				var first = true;
				for (var i = 0; i < nrOfPins; i++)
					if (myIoPins.contains(i)) {
						if (first) first = false;
						else sb.append(",");
						sb.append(myPinLocations.get(i));
					}
				attrSet.setValue(sb.toString());
				result.setAttributeNode(attrSet);
			}
			if (myDriveStrength != LogisimFX.fpga.data.DriveStrength.UNKNOWN && myDriveStrength != LogisimFX.fpga.data.DriveStrength.DEFAULT_STENGTH) {
				final var drive = doc.createAttribute(LogisimFX.fpga.data.DriveStrength.DRIVE_ATTRIBUTE_STRING);
				drive.setValue(LogisimFX.fpga.data.DriveStrength.BEHAVIOR_STRINGS[myDriveStrength]);
				result.setAttributeNode(drive);
			}
			if (myPullBehavior != LogisimFX.fpga.data.PullBehaviors.UNKNOWN && myPullBehavior != LogisimFX.fpga.data.PullBehaviors.FLOAT) {
				final var pull = doc.createAttribute(LogisimFX.fpga.data.PullBehaviors.PULL_ATTRIBUTE_STRING);
				pull.setValue(LogisimFX.fpga.data.PullBehaviors.BEHAVIOR_STRINGS[myPullBehavior]);
				result.setAttributeNode(pull);
			}
			if (myIoStandard != LogisimFX.fpga.data.IoStandards.UNKNOWN && myIoStandard != LogisimFX.fpga.data.IoStandards.DEFAULT_STANDARD) {
				final var stand = doc.createAttribute(LogisimFX.fpga.data.IoStandards.IO_ATTRIBUTE_STRING);
				stand.setValue(LogisimFX.fpga.data.IoStandards.BEHAVIOR_STRINGS[myIoStandard]);
				result.setAttributeNode(stand);
			}
			if (myActivityLevel != PinActivity.Unknown && myActivityLevel != PinActivity.ACTIVE_HIGH) {
				final var act = doc.createAttribute(PinActivity.ACTIVITY_ATTRIBUTE_STRING);
				act.setValue(PinActivity.BEHAVIOR_STRINGS[myActivityLevel]);
				result.setAttributeNode(act);
			}
			return result;
		} catch (Exception e) {
			/* TODO: handle exceptions */
			logger.error(
					"Exceptions not handled yet in GetDocumentElement(), but got an exception: {}",
					e.getMessage());
		}
		return null;
	}

	public String getLabel() {
		return myLabel;
	}

	public String getDisplayString() {
		return myLabel == null ? myType.name() : myLabel;
	}

	public void setLabel(String label) {
		myLabel = label;
	}

	public char getDrive() {
		return myDriveStrength;
	}

	public void setDrive(char drive) {
		myDriveStrength = drive;
	}

	public char getIoStandard() {
		return myIoStandard;
	}

	public void setIOStandard(char IoStandard) {
		myIoStandard = IoStandard;
	}

	public int getNrOfPins() {
		return nrOfPins;
	}

	public char getPullBehavior() {
		return myPullBehavior;
	}

	public void setPullBehavior(char pull) {
		myPullBehavior = pull;
	}

	public BoardRectangle getRectangle() {
		return myRectangle;
	}

	public IoComponentTypes getType() {
		return myType;
	}

	public void setType(IoComponentTypes type) {
		myType = type;
	}

	public boolean isInput() {
		return IoComponentTypes.INPUT_COMPONENT_SET.contains(myType);
	}

	public boolean isInputOutput() {
		return IoComponentTypes.IN_OUT_COMPONENT_SET.contains(myType);
	}

	public boolean isKnownComponent() {
		return IoComponentTypes.KNOWN_COMPONENT_SET.contains(myType);
	}

	public boolean isOutput() {
		return IoComponentTypes.OUTPUT_COMPONENT_SET.contains(myType);
	}

	public boolean isPinMapped(int index) {
		if (index < 0 || index >= nrOfPins) return true;
		return pinIsMapped.get(index) != null;
	}

	public LogisimFX.fpga.data.MapComponent getPinMap(int index) {
		if (index < 0 || index >= nrOfPins) return null;
		return pinIsMapped.get(index).getMap();
	}

	public int getMapPin(int index) {
		if (index < 0 || index >= nrOfPins) return -1;
		return pinIsMapped.get(index).pin;
	}

	public void setNrOfPins(int count) {
		if (pinIsMapped == null) pinIsMapped = new ArrayList<>();
		nrOfPins = count;
		if (count > pinIsMapped.size()) {
			for (var i = pinIsMapped.size(); i < count; i++)
				pinIsMapped.add(null);
		} else if (count < pinIsMapped.size()) {
			for (var i = pinIsMapped.size() - 1; i >= count; i--) {
				var map = pinIsMapped.get(i);
				if (map != null) map.unmap();
				pinIsMapped.remove(i);
			}
		}
	}

	public void unmap(int pin) {
		if (pin < 0 || pin >= pinIsMapped.size()) return;
		var map = pinIsMapped.get(pin);
		pinIsMapped.set(pin, null);
		if (map != null) map.unmap();
	}

	public MapResultClass tryInputMap(LogisimFX.fpga.data.MapComponent comp, int compPin, int inpPin) {
		var result = new MapResultClass();
		result.mapResult = false;
		result.pinId = inpPin;
		if (myInputPins == null || !myInputPins.contains(result.pinId))
			return this.tryIOMap(comp, compPin, inpPin);
		unmap(result.pinId);
		var map = new mapType(comp, compPin);
		pinIsMapped.set(result.pinId, map);
		result.mapResult = true;
		return result;
	}

	public MapResultClass tryOutputMap(LogisimFX.fpga.data.MapComponent comp, int compPin, int outpPin) {
		var result = new MapResultClass();
		result.mapResult = false;
		result.pinId = outpPin + (myInputPins == null ? 0 : myInputPins.size());
		if (myOutputPins == null || !myOutputPins.contains(result.pinId))
			return this.tryIOMap(comp, compPin, outpPin);
		unmap(result.pinId);
		var map = new mapType(comp, compPin);
		pinIsMapped.set(result.pinId, map);
		result.mapResult = true;
		return result;
	}

	public MapResultClass tryIOMap(LogisimFX.fpga.data.MapComponent comp, int compPin, int ioPin) {
		var result = new MapResultClass();
		result.mapResult = false;
		result.pinId =
				ioPin
						+ (myInputPins == null ? 0 : myInputPins.size())
						+ (myOutputPins == null ? 0 : myOutputPins.size());
		if (myIoPins == null || !myIoPins.contains(result.pinId)) return result;
		unmap(result.pinId);
		var map = new mapType(comp, compPin);
		pinIsMapped.set(result.pinId, map);
		result.mapResult = true;
		return result;
	}

	public boolean tryMap(LogisimFX.fpga.data.MapComponent comp, int compPin, int myPin) {
		if (myPin < 0 || myPin >= nrOfPins) return false;
		unmap(myPin);
		var map = new mapType(comp, compPin);
		pinIsMapped.set(myPin, map);
		return true;
	}

	public boolean updateMap(int pin, LogisimFX.fpga.data.MapComponent comp) {
		if (pin < 0 || pin >= pinIsMapped.size()) return false;
		var map = pinIsMapped.get(pin);
		if (map == null) return false;
		map.update(comp);
		return true;
	}

	public boolean isCompletelyMappedBy(LogisimFX.fpga.data.MapComponent comp) {
		for (var i = 0; i < nrOfPins; i++)
			if (pinIsMapped.get(i) != null) {
				if (!pinIsMapped.get(i).map.equals(comp)) return false;
			} else return false;
		return true;
	}

	private int getNrOfMaps() {
		int res = 0;
		for (var i = 0; i < nrOfPins; i++)
			if (pinIsMapped.get(i) != null)
				res++;
		return res;
	}

	public boolean hasInputs() {
		return CollectionUtil.isNotEmpty(myInputPins);
	}

	public boolean hasOutputs() {
		return CollectionUtil.isNotEmpty(myOutputPins);
	}

	public boolean hasIoPins() {
		return CollectionUtil.isNotEmpty(myIoPins);
	}

	public int nrInputs() {
		return myInputPins == null ? 0 : myInputPins.size();
	}

	public int nrOutputs() {
		return myOutputPins == null ? 0 : myOutputPins.size();
	}

	public int getNrOfIoPins() {
		return myIoPins == null ? 0 : myIoPins.size();
	}

	public Set<Integer> getInputs() {
		return myInputPins;
	}

	public Set<Integer> getOutputs() {
		return myOutputPins;
	}

	public Set<Integer> getIos() {
		return myIoPins;
	}

	public String getPinName(int index) {
		if (myInputPins != null && myInputPins.contains(index)) {
			return IoComponentTypes.getInputLabel(nrOfPins, index, myType);
		}
		if (myOutputPins != null && myOutputPins.contains(index)) {
			return IoComponentTypes.getOutputLabel(nrOfPins, nrOfRows, nrOfColumns, index, myType);
		}
		if (myIoPins != null && myIoPins.contains(index)) {
			return IoComponentTypes.getIoLabel(nrOfPins, index, myType);
		}
		return "" + index;
	}

	public boolean setSelectable(IOMapperController.MapInfo comp) {
		selComp = comp;
		var map = comp.getMap();
		var connect = comp.getPin();
		selectedPin = -1;
		selectable = false;
		if (connect < 0) {
			if (map.hasInputs() && (hasIoPins() || hasInputs())) selectable = true;
			if (map.hasOutputs() && (hasIoPins() || hasOutputs())) selectable = true;
			if (map.hasIos() && hasIoPins()) selectable = true;
		} else {
			if (map.isInput(connect) && (hasIoPins() || hasInputs())) selectable = true;
			if (map.isOutput(connect) && (hasIoPins() || hasOutputs())) selectable = true;
			if (map.isIo(connect) && hasIoPins()) selectable = true;
		}
		return selectable;
	}

	public boolean removeSelectable() {
		var ret = selectable;
		selComp = null;
		selectable = false;
		selectedPin = -1;
		return ret;
	}

	private boolean containsMap() {
		if (selComp == null) return false;
		var com = selComp.getMap();
		for (var i = 0; i < nrOfPins; i++) {
			if (pinIsMapped.get(i) != null && pinIsMapped.get(i).map.equals(com)) return true;
		}
		return false;
	}

	public boolean isCompleteMap() {
		if (selComp == null) return true;
		var map = selComp.getMap();
		if (selComp.getPin() >= 0 && nrOfPins == 1) {
			/* single pin only */
			return true;
		}
		if (map.nrInputs() == nrInputs()
				&& map.nrOutputs() == nrOutputs()
				&& map.nrIOs() == getNrOfIoPins()
				&& selComp.getPin() < 0) {
			return true;
		}
		if (nrInputs() == 0
				&& nrOutputs() == 0
				&& map.nrIOs() == 0
				&& map.nrInputs() == getNrOfIoPins()
				&& map.nrOutputs() == 0
				&& selComp.getPin() < 0) {
			return true;
		}
		if (nrInputs() == 0
				&& nrOutputs() == 0
				&& map.nrIOs() == 0
				&& map.nrOutputs() == getNrOfIoPins()
				&& map.nrInputs() == 0
				&& selComp.getPin() < 0) {
			return true;
		}
		return false;
	}

	public boolean tryLedArrayMap() {
		var map = selComp.getMap();
		if (selComp.getPin() >= 0 && selectedPin >= 0) {
			/* single pin on a selected Pin */
			map.unmap(selComp.getPin());
			return map.tryMap(selComp.getPin(), this, selectedPin);
		}
		/* okay, the map component has more than one pin, then we treat first the RGB-LED,
		 * DotMatrix, and LedBar, all others will be handled by a partialmapdialog
		 */
		var fact = map.getComponentFactory();
		if (fact instanceof DotMatrix) {
			var nrOfMatrixRows = map.getAttributeSet().getValue(DotMatrix.ATTR_MATRIX_ROWS).getWidth();
			var nrOfMatrixColumns = map.getAttributeSet().getValue(DotMatrix.ATTR_MATRIX_COLS).getWidth();
			var startRow = selectedPin / nrOfColumns;
			var startColumn = selectedPin % nrOfColumns;
			if (((nrOfMatrixRows + startRow) <= nrOfRows) && ((nrOfMatrixColumns + startColumn) <= nrOfColumns)) {
				var canMap = true;
				/* we can map the matrix here */
				map.unmap(); // Remove all previous maps
				for (var row = 0; row < nrOfMatrixRows; row++) {
					for (var column = 0; column < nrOfMatrixColumns; column++) {
						var SourcePin = row * nrOfMatrixColumns + column;
						var MapPin = (row + startRow) * nrOfColumns + column + startColumn;
						canMap &= map.tryMap(SourcePin, this, MapPin);
					}
				}
				if (!canMap) map.unmap();
				return canMap;
			}
		}
		if (fact instanceof LedBar) {
			var nrOfSegs = map.getAttributeSet().getValue(LedBar.ATTR_MATRIX_COLS).getWidth();
			var selCol = selectedPin % nrOfColumns;
			if ((selCol + nrOfSegs) <= nrOfColumns) {
				/* we can completely map the ledbar in this row */
				map.unmap(); /* remove all old maps */
				var canBeMapped = true;
				for (var i = 0; i < nrOfSegs; i++) {
					canBeMapped &= map.tryMap(nrOfSegs - i - 1, this, selectedPin + i);
				}
				if (!canBeMapped) map.unmap();
				return canBeMapped;
			}
		}
		if (fact instanceof RgbLed) {
			if (driving == LedArrayDriving.RGB_COLUMN_SCANNING
					|| driving == LedArrayDriving.RGB_DEFAULT
					|| driving == LedArrayDriving.RGB_ROW_SCANNING) {
				/* only if we have an RGB-array we are going to do something special */
				map.unmap(); /* remove all previous maps */
				return map.tryCompleteMap(this, selectedPin);
			}
		}
		return false;
	}

	public boolean tryMap() {
		if (!selectable) {
			return false;
		}
		if (selComp == null) {
			return false;
		}
		if (myType.equals(IoComponentTypes.LedArray))
			return tryLedArrayMap();
		var map = selComp.getMap();
		if (selComp.getPin() >= 0 && nrOfPins == 1) {
			/* single pin only */
			map.unmap(selComp.getPin());
			return map.tryMap(selComp.getPin(), this, 0);
		}
		if (selComp.getPin() >= 0 && selectedPin >= 0) {
			/* single pin on a selected Pin */
			map.unmap(selComp.getPin());
			return map.tryMap(selComp.getPin(), this, selectedPin);
		}
		if (isCompleteMap()) {
			/* complete map */
			map.unmap();
			return map.tryMap(this);
		}
		/* in case of a dipswitch on dipswitch we are doing some more intelligent approach */
		if (myType.equals(IoComponentTypes.DIPSwitch) && (map.getComponentFactory() instanceof DipSwitch)) {
			var nrOfSwitches = map.getAttributeSet().getValue(DipSwitch.ATTR_SIZE).getWidth();
			if ((nrOfSwitches + selectedPin) <= nrOfPins) {
				map.unmap();
				var canMap = true;
				for (var i = 0; i < nrOfSwitches; i++)
					canMap &= map.tryMap(i, this, i + selectedPin);
				if (!canMap) map.unmap();
				return canMap;
			}
		}
		return false;
	}



}

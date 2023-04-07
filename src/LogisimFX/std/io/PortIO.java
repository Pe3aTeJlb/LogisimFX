/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.io;

import LogisimFX.KeyEvents;
import LogisimFX.data.*;
import LogisimFX.fpga.data.ComponentMapInformationContainer;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.DirectionConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

public class PortIO extends InstanceFactory {

	public static List<String> getLabels(int size) {
		List<String> labelNames = new ArrayList<>();
		for (var i = 0; i < size; i++) {
			labelNames.add("pin_" + (i + 1));
		}
		return labelNames;
	}

	private static class PortState implements InstanceData, Cloneable {

		/* each pin has it's own Value, where there are 3 entries in the value:
		 * 1) The state of the input
		 * 2) The state of the poke value
		 * 3) The state of the enable pin
		 */

		private final BitWidth BIT_WIDTH = BitWidth.create(1);
		private final ArrayList<Value> inputState;
		private final ArrayList<Value> pokeState;
		private final ArrayList<Value> enableState;
		private int size;

		public PortState(int size) {
			this.size = size;
			inputState = new ArrayList<>();
			pokeState = new ArrayList<>();
			enableState = new ArrayList<>();
			for (var pin = 0; pin < size; pin++) {
				inputState.add(Value.createUnknown(BIT_WIDTH));
				pokeState.add(Value.createUnknown(BIT_WIDTH));
				enableState.add(Value.createKnown(BIT_WIDTH, 0));
			}
		}

		public void resize(int newSize) {
			if (newSize == size) return;
			if (newSize > size) {
				for (var newPin = size; newPin < newSize; newPin++) {
					inputState.add(Value.createUnknown(BIT_WIDTH));
					pokeState.add(Value.createUnknown(BIT_WIDTH));
					enableState.add(Value.createKnown(BIT_WIDTH, 0));
				}
			} else {
				while (inputState.size() > newSize) {
					inputState.remove(inputState.size() - 1);
					pokeState.remove(inputState.size() - 1);
					enableState.remove(inputState.size() - 1);
				}
			}
			size = newSize;
		}

		public void togglePokeValue(int pinIndex) {
			if (pinIndex < 0 || pinIndex > size) return;
			final var pokeValue = pokeState.get(pinIndex).get(0);
			if (pokeValue.equals(Value.UNKNOWN))
				pokeState.set(pinIndex, Value.createKnown(BIT_WIDTH, 0));
			else if (pokeValue.equals(Value.FALSE))
				pokeState.set(pinIndex, Value.createKnown(BIT_WIDTH, 1));
			else pokeState.set(pinIndex, Value.createUnknown(BIT_WIDTH));
		}

		public void setInputValue(int pinIndex, Value value) {
			if ((pinIndex < 0) || (pinIndex > size)) return;
			final var newValue = new Value[1];
			newValue[0] = value;
			inputState.set(pinIndex, Value.create(newValue));
		}

		public Value getPinValue(int pinIndex, AttributeOption directionAttribute) {
			if ((pinIndex < 0) || (pinIndex > size) || (directionAttribute == null)) return Value.ERROR;
			if (directionAttribute.equals(OUTPUT)) {
				return inputState.get(pinIndex);
			}
			if (directionAttribute.equals(INPUT)) {
				return pokeState.get(pinIndex);
			}
			final var inputValue = inputState.get(pinIndex);
			final var pokeValue = pokeState.get(pinIndex);
			final var enableValue = enableState.get(pinIndex);
			final var resultValue =
					(pokeValue.equals(Value.UNKNOWN) || pokeValue.equals(inputValue))
							? inputValue
							: Value.ERROR;
			if (enableValue.equals(Value.UNKNOWN)) return Value.ERROR;
			return enableValue.equals(Value.TRUE) ? resultValue : pokeValue;
		}

		public void setEnableValue(int pinIndex, Value value) {
			if ((pinIndex < 0) || (pinIndex > size)) return;
			enableState.set(pinIndex, value);
		}

		public Paint getPinColor(int pinIndex, AttributeOption directionAttribute) {
			final var pinValue = getPinValue(pinIndex, directionAttribute);
			return pinValue.equals(Value.UNKNOWN) ? Color.LIGHTGRAY : pinValue.getColor();
		}

		@Override
		public Object clone() {
			final var other = new PortState(size);
			for (int pinIndex = 0; pinIndex < size; pinIndex++) {
				other.inputState.set(pinIndex, inputState.get(pinIndex));
				other.enableState.set(pinIndex, enableState.get(pinIndex));
				other.pokeState.set(pinIndex, pokeState.get(pinIndex));
			}
			return other;
		}
	}

	public static class PortPoker extends InstancePoker {
		@Override
		public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {
			final var loc = state.getInstance().getLocation();
			final var cx = e.localX - loc.getX() - 7 + 2;
			final var cy = e.localY - loc.getY() - 25 + 2;
			if (cx < 0 || cy < 0) return;
			final var i = cx / 10;
			final var j = cy / 10;
			if (j > 1) return;
			final var n = 2 * i + j;
			final var data = getState(state);
			if (n < 0 || n >= data.size) return;
			data.togglePokeValue(n);
			state.fireInvalidated();
		}
	}

	public static final int MAX_IO = 128;
	public static final int MIN_IO = 2;
	private static final int INITPORTSIZE = 8;
	public static final Attribute<BitWidth> ATTR_SIZE =
			Attributes.forBitWidth("number", LC.createStringBinding("pioNumber"), MIN_IO, MAX_IO);

	public static final AttributeOption INPUT =
			new AttributeOption("onlyinput", LC.createStringBinding("pioInput"));
	public static final AttributeOption OUTPUT =
			new AttributeOption("onlyOutput", LC.createStringBinding("pioOutput"));
	public static final AttributeOption INOUTSE =
			new AttributeOption("IOSingleEnable", LC.createStringBinding("pioIOSingle"));
	public static final AttributeOption INOUTME =
			new AttributeOption("IOMultiEnable", LC.createStringBinding("pioIOMultiple"));

	public static final Attribute<AttributeOption> ATTR_DIR =
			Attributes.forOption(
					"direction",
					LC.createStringBinding("pioDirection"),
					new AttributeOption[]{INPUT, OUTPUT, INOUTSE, INOUTME});

	protected static final int DELAY = 1;

	public PortIO() {
		super("PortIO", LC.createStringBinding("pioComponent"), new PortHdlGeneratorFactory(), true);
		setAttributes(
				new Attribute[]{
						StdAttr.FPGA_SUPPORTED,
						StdAttr.FACING,
						StdAttr.LABEL,
						StdAttr.LABEL_LOC,
						StdAttr.LABEL_FONT,
						StdAttr.LABEL_VISIBILITY,
						ATTR_SIZE,
						ATTR_DIR,
						StdAttr.MAPINFO
				},
				new Object[]{
						Boolean.FALSE,
						Direction.EAST,
						"",
						Direction.EAST,
						StdAttr.DEFAULT_LABEL_FONT,
						Boolean.FALSE,
						BitWidth.create(INITPORTSIZE),
						INOUTSE,
						new ComponentMapInformationContainer(
								0, 0, INITPORTSIZE, null, null, getLabels(INITPORTSIZE))
				});
		setFacingAttribute(StdAttr.FACING);
		setIcon("portio.gif");
		setKeyConfigurator(
				JoinedConfigurator.create(
						new BitWidthConfigurator(ATTR_SIZE, MIN_IO, MAX_IO, KeyEvents.ALT_DOWN),
						new DirectionConfigurator(StdAttr.LABEL_LOC, KeyEvents.ALT_DOWN)));
		setInstancePoker(PortPoker.class);
	}

	@Override
	protected void configureNewInstance(Instance instance) {
		instance.addAttributeListener();
		updatePorts(instance);
		instance.computeLabelTextField(Instance.AVOID_BOTTOM);
		ComponentMapInformationContainer map = instance.getAttributeSet().getValue(StdAttr.MAPINFO);
		if (map == null) {
			map =
					new ComponentMapInformationContainer(
							0, 0, INITPORTSIZE, null, null, getLabels(INITPORTSIZE));
			instance.getAttributeSet().setValue(ATTR_SIZE, BitWidth.create(INITPORTSIZE));
			instance.getAttributeSet().setValue(ATTR_DIR, INOUTSE);
		}
		instance.getAttributeSet().setValue(StdAttr.MAPINFO, map.clone());
	}

	private void updatePorts(Instance instance) {
		final var facing = instance.getAttributeValue(StdAttr.FACING);
		final var dir = instance.getAttributeValue(ATTR_DIR);
		final var size = instance.getAttributeValue(ATTR_SIZE).getWidth();
		// logisim max bus size is BitWidth.MAXWIDTH, so use multiple buses if needed
		final var nBus = (((size - 1) / BitWidth.MAXWIDTH) + 1);
		var nPorts = -1;
		if (dir == INPUT || dir == OUTPUT) nPorts = nBus;
		else if (dir == INOUTME) nPorts = 3 * nBus;
		else if (dir == INOUTSE) nPorts = 2 * nBus + 1;
		Port[] ps = new Port[nPorts];
		var p = 0;

		var x = 0;
		var y = 0;
		var dx = 0;
		var dy = 0;
		if (facing == Direction.NORTH) dy = -10;
		else if (facing == Direction.SOUTH) dy = 10;
		else if (facing == Direction.WEST) dx = -10;
		else dx = 10;
		if (dir == INPUT || dir == OUTPUT) {
			x += dx;
			y += dy;
		}
		if (dir == INOUTSE) {
			ps[p] = new Port(x - dy, y + dx, Port.INPUT, 1);
			ps[p].setToolTip(LC.createStringBinding("pioOutEnable"));
			p++;
			x += dx;
			y += dy;
		}
		var n = size;
		var i = 0;
		while (n > 0) {
			final var e = Math.min(n, BitWidth.MAXWIDTH);
			final var range = "[" + i + "..." + (i + e - 1) + "]";
			if (dir == INOUTME) {
				ps[p] = new Port(x - dy, y + dx, Port.INPUT, e);
				ps[p].setToolTip(LC.createComplexStringBinding("pioOutEnables", range));
				p++;
				x += dx;
				y += dy;
			}
			if (dir == INPUT || dir == INOUTSE || dir == INOUTME) {
				ps[p] = new Port(x, y, Port.INPUT, e);
				ps[p].setToolTip(LC.createComplexStringBinding("pioOutputs", range));
				p++;
				x += dx;
				y += dy;
			}
			i += BitWidth.MAXWIDTH;
			n -= e;
		}
		n = size;
		i = 0;
		while (n > 0) {
			final var e = Math.min(n, BitWidth.MAXWIDTH);
			String range = "[" + i + "..." + (i + e - 1) + "]";
			if (dir == OUTPUT || dir == INOUTSE || dir == INOUTME) {
				ps[p] = new Port(x, y, Port.OUTPUT, e);
				ps[p].setToolTip(LC.createComplexStringBinding("pioInputs", range));
				p++;
				x += dx;
				y += dy;
			}
			i += BitWidth.MAXWIDTH;
			n -= e;
		}
		instance.setPorts(ps);
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {
		final var facing = attrs.getValue(StdAttr.FACING);
		var n = attrs.getValue(ATTR_SIZE).getWidth();
		if (n < 8) n = 8;
		return Bounds.create(0, 0, 10 + (n + 1) / 2 * 10, 50).rotate(Direction.EAST, facing, 0, 0);
	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			updatePorts(instance);
			instance.computeLabelTextField(Instance.AVOID_BOTTOM);
		} else if (attr == StdAttr.LABEL_LOC) {
			instance.computeLabelTextField(Instance.AVOID_BOTTOM);
		} else if (attr == ATTR_SIZE || attr == ATTR_DIR) {
			instance.recomputeBounds();
			updatePorts(instance);
			instance.computeLabelTextField(Instance.AVOID_BOTTOM);
			ComponentMapInformationContainer map = instance.getAttributeValue(StdAttr.MAPINFO);
			if (map != null) {
				final var nrPins = instance.getAttributeValue(ATTR_SIZE).getWidth();
				var inputs = 0;
				var outputs = 0;
				var ios = 0;
				final var labels = getLabels(nrPins);
				if (instance.getAttributeValue(ATTR_DIR) == INPUT) {
					inputs = nrPins;
				} else if (instance.getAttributeValue(ATTR_DIR) == OUTPUT) {
					outputs = nrPins;
				} else {
					ios = nrPins;
				}
				map.setNrOfInports(inputs, labels);
				map.setNrOfOutports(outputs, labels);
				map.setNrOfInOutports(ios, labels);
			}
			if (attr == ATTR_DIR) {
				/*
				// we have to reset simulatio, as otherwise strange things can happen.
				final var stateImpl = instance.getComponent().getInstanceStateImpl();
				if (stateImpl == null) return;
				final var circuitState = stateImpl.getCircuitState();
				if (circuitState == null) return;
				final var circuit = circuitState.getCircuit();
				if (circuit == null) return;
				final var project = circuit.getProject();
				if (project == null) return;
				final var simulator = project.getSimulator();
				if (simulator == null) return;
				simulator.reset();*/
			}
		}
	}

	@Override
	public void paintInstance(InstancePainter painter) {
		final var facing = painter.getAttributeValue(StdAttr.FACING);

		final var bds = painter.getBounds().rotate(Direction.EAST, facing, 0, 0);
		final var w = bds.getWidth();
		final var h = bds.getHeight();
		final var x = painter.getLocation().getX();
		final var y = painter.getLocation().getY();
		final var g = painter.getGraphics();
		g.c.translate(x, y);
		var rotate = 0.0;
		if (facing != Direction.EAST) {
			rotate = -facing.toRadians();
			g.rotate(rotate);
		}

		g.setLineWidth(2);
		g.setColor(Color.DARKGRAY);
		double[] bx = {1, 1, 5, w - 6, w - 2, w - 2, 1};
		double[] by = {20, h - 8, h - 4, h - 4, h - 8, 20, 20};
		g.c.fillPolygon(bx, by, 6);
		g.setColor(Color.BLACK);
		g.setLineWidth(1);

		g.c.strokePolyline(bx, by, 7);

		final var size = painter.getAttributeValue(ATTR_SIZE).getWidth();
		final var nBus = (((size - 1) / BitWidth.MAXWIDTH) + 1);
		if (!painter.getShowState()) {
			g.setColor(Color.LIGHTGRAY);
			for (var i = 0; i < size; i++) g.c.fillRect(7 + ((i / 2) * 10), 25 + (i % 2) * 10, 6, 6);
		} else {
			PortState data = getState(painter);
			for (var i = 0; i < size; i++) {
				g.setColor(data.getPinColor(i, painter.getAttributeValue(ATTR_DIR)));
				g.c.fillRect(7 + ((i / 2) * 10), 25 + (i % 2) * 10, 6, 6);
			}
		}
		g.setColor(Color.BLACK);
		AttributeOption dir = painter.getAttributeValue(ATTR_DIR);
		var px = ((dir == INOUTSE || dir == INOUTME) ? 0 : 10);
		final var py = 0;
		for (var p = 0; p < nBus; p++) {
			if (dir == INOUTSE) {
				g.setLineWidth(3);
				if (p == 0) {
					g.c.strokeLine(px, py + 10, px + 6, py + 10);
					px += 10;
				} else {
					g.c.strokeLine(px - 6, py + 10, px - 4, py + 10);
				}
			}
			if (dir == INOUTME) {
				g.setLineWidth(3);
				g.c.strokeLine(px, py + 10, px + 6, py + 10);
				px += 10;
			}
			if (dir == OUTPUT || dir == INOUTSE || dir == INOUTME) {
				g.setLineWidth(3);
				g.c.strokeLine(px, py, px, py + 4);
				g.c.strokeLine(px, py + 15, px, py + 20);
				g.setLineWidth(2);
				double[] xp = {px, px - 4, px + 4, px};
				double[] yp = {py + 15, py + 5, py + 5, py + 15};
				g.c.strokePolyline(xp, yp, 4);
				px += 10;
			}
		}

		for (var p = 0; p < nBus; p++) {
			if (dir == INPUT || dir == INOUTSE || dir == INOUTME) {
				g.setLineWidth(3);
				g.c.strokeLine(px, py, px, py + 5);
				g.c.strokeLine(px, py + 16, px, py + 20);
				g.setLineWidth(2);
				double[] xp = {px, px - 4, px + 4, px};
				double[] yp = {py + 6, py + 16, py + 16, py + 6};
				g.c.strokePolyline(xp, yp, 4);
				px += 10;
			}
		}

		g.setLineWidth(1);
		g.rotate(-rotate);
		g.c.translate(-x, -y);

		painter.drawPorts();
		painter.drawLabel();

		g.toDefault();

	}

	private static PortState getState(InstanceState state) {
		final var size = state.getAttributeValue(ATTR_SIZE).getWidth();
		var data = (PortState) state.getData();
		if (data == null) {
			data = new PortState(size);
			state.setData(data);
			return data;
		}
		if (data.size != size) data.resize(size);
		return data;
	}

	@Override
	public void propagate(InstanceState state) {
		final var portType = state.getAttributeValue(ATTR_DIR);
		final var nrOfPins = state.getAttributeValue(ATTR_SIZE).getWidth();
		final var stateData = getState(state);

		var currentPortIndex = 0;
		// first we update the state data
		if (portType.equals(INOUTSE) || portType.equals(INOUTME) || portType.equals(OUTPUT)) {
			var enableValue = state.getPortValue(currentPortIndex);
			if (portType.equals(INOUTSE) || portType.equals(INOUTME)) currentPortIndex++;
			var inputValue = state.getPortValue(currentPortIndex);
			var pinIndexCorrection = -BitWidth.MAXWIDTH;
			for (var pinIndex = 0; pinIndex < nrOfPins; pinIndex++) {
				if ((pinIndex % BitWidth.MAXWIDTH) == 0) {
					if ((portType.equals(INOUTME)) && (pinIndex > 0))
						enableValue = state.getPortValue(currentPortIndex++);
					inputValue = state.getPortValue(currentPortIndex++);
					pinIndexCorrection += BitWidth.MAXWIDTH;
				}
				if (!portType.equals(OUTPUT)) {
					final var enableIndex = portType.equals(INOUTSE) ? 0 : pinIndex - pinIndexCorrection;
					stateData.setEnableValue(pinIndex, enableValue.get(enableIndex));
				}
				stateData.setInputValue(pinIndex, inputValue.get(pinIndex - pinIndexCorrection));
			}
		}
		// now we force the outputs
		if (!portType.equals(OUTPUT)) {
			var nrOfRemainingPins = nrOfPins;
			var nrOfPinsInCurrentBus = Math.min(nrOfRemainingPins, BitWidth.MAXWIDTH);
			nrOfRemainingPins -= nrOfPinsInCurrentBus;
			var outputValue = new Value[nrOfPinsInCurrentBus];
			var pinIndexCorrection = 0;
			for (var pinIndex = 0; pinIndex < nrOfPins; pinIndex++) {
				if ((pinIndex > 0) && ((pinIndex % BitWidth.MAXWIDTH) == 0)) {
					state.setPort(currentPortIndex++, Value.create(outputValue), DELAY);
					nrOfPinsInCurrentBus = Math.min(nrOfRemainingPins, BitWidth.MAXWIDTH);
					nrOfRemainingPins -= nrOfPinsInCurrentBus;
					outputValue = new Value[nrOfPinsInCurrentBus];
					pinIndexCorrection += BitWidth.MAXWIDTH;
				}
				outputValue[pinIndex - pinIndexCorrection] = stateData.getPinValue(pinIndex, portType);
			}
			state.setPort(currentPortIndex++, Value.create(outputValue), DELAY);
		}
	}
}

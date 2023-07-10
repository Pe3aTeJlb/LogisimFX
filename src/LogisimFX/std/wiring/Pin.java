/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.std.wiring;

import LogisimFX.IconsManager;
import LogisimFX.KeyEvents;
import LogisimFX.circuit.Wire;
import LogisimFX.comp.EndData;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.DirectionConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.util.GraphicsUtil;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.RadixOption;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.awt.event.MouseEvent;
import java.math.BigInteger;

public class Pin extends InstanceFactory {

	public static final Attribute<Boolean> ATTR_TRISTATE
			= Attributes.forBoolean("tristate", LC.createStringBinding("pinThreeStateAttr"));
	public static final Attribute<Boolean> ATTR_TYPE
			= Attributes.forBoolean("output", LC.createStringBinding("pinOutputAttr"));
	public static final Attribute<Direction> ATTR_LABEL_LOC
			= Attributes.forDirection("labelloc", LC.createStringBinding("pinLabelLocAttr"));

	public static final AttributeOption PULL_NONE
			= new AttributeOption("none", LC.createStringBinding("pinPullNoneOption"));
	public static final AttributeOption PULL_UP
			= new AttributeOption("up", LC.createStringBinding("pinPullUpOption"));
	public static final AttributeOption PULL_DOWN
			= new AttributeOption("down", LC.createStringBinding("pinPullDownOption"));
	public static final Attribute<AttributeOption> ATTR_PULL
			= Attributes.forOption("pull", LC.createStringBinding("pinPullAttr"),
			new AttributeOption[]{PULL_NONE, PULL_UP, PULL_DOWN});

	public static final Pin FACTORY = new Pin();

	private static final ImageView ICON_IN = IconsManager.getIcon("pinInput.gif");
	private static final ImageView ICON_OUT = IconsManager.getIcon("pinOutput.gif");
	private static final Font ICON_WIDTH_FONT = Font.font("SansSerif", FontWeight.BOLD, FontPosture.REGULAR, 9);
	public static final Font DEFAULT_FONT = Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12);
	private static final Color ICON_WIDTH_COLOR = Value.WIDTH_ERROR_COLOR.darker();
	public static final int DIGIT_WIDTH = 8;

	public Pin() {

		super("Pin", LC.createStringBinding("pinComponent"));
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(JoinedConfigurator.create(
				new BitWidthConfigurator(StdAttr.WIDTH),
				new DirectionConfigurator(ATTR_LABEL_LOC, KeyEvents.ALT_DOWN)));
		setInstanceLogger(PinLogger.class);
		setInstancePoker(PinPoker.class);

	}

	@Override
	public AttributeSet createAttributeSet() {
		return new PinAttributes();
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction dir = attrs.getValue(StdAttr.FACING);
		BitWidth width = attrs.getValue(StdAttr.WIDTH);
		RadixOption radix = attrs.getValue(RadixOption.ATTRIBUTE);
		//return Probe.getOffsetBounds(facing, width, RadixOption.RADIX_2);

		int len =
				radix == null || radix == RadixOption.RADIX_2
						? width.getWidth()
						: radix.getMaxLength(width);
		int bwidth, bheight, x, y;
		if (radix == RadixOption.RADIX_2) {
			int maxBitsPerRow = 8;
			int maxRows = 8;
			int rows = len / maxBitsPerRow;
			if (len > rows * maxBitsPerRow) rows++;
			bwidth = (len < 2) ? 20 : (len >= maxBitsPerRow) ? maxBitsPerRow * 10 : len * 10;
			bheight = (rows < 2) ? 20 : (rows >= maxRows) ? maxRows * 20 : rows * 20;
		} else {
			if (len < 2) bwidth = 20;
			else bwidth = len * Pin.DIGIT_WIDTH;
			bheight = 20;
		}
		bwidth += (len == 1) ? 20 : 25;
		bwidth = ((bwidth + 9) / 10) * 10;
		if (dir == Direction.EAST) {
			x = -bwidth;
			y = -(bheight / 2);
		} else if (dir == Direction.WEST) {
			x = 0;
			y = -(bheight / 2);
		} else if (dir == Direction.SOUTH) {

			x = bwidth;
			bwidth = bheight;
			bheight = x;

			x = -(bwidth / 2);
			y = -bheight;
		} else {

			x = bwidth;
			bwidth = bheight;
			bheight = x;

			x = -(bwidth / 2);
			y = 0;
		}
		return Bounds.create(x, y, bwidth, bheight);

	}

	//
	// graphics methods
	//

	@Override
	public ImageView getIcon() {
		return ICON_IN;
	}

	private void drawNewStyleValue(
			InstancePainter painter, int width, int height, boolean isOutput, boolean isGhost) {
		/* Note: we are here in an translated environment the point (0,0) presents the pin location*/
		if (isGhost) return;
		Value value = getState(painter).sending;
		Graphics g = painter.getGraphics();
		g.setFont(Pin.DEFAULT_FONT);
		RadixOption radix = painter.getAttributeValue(RadixOption.ATTRIBUTE);
		Direction dir = painter.getAttributeSet().getValue(StdAttr.FACING);
		int westTranslate = (isOutput) ? width : width + 10;
		if (dir == Direction.WEST) {
			g.rotate(-180);
			g.c.translate(westTranslate, 0);
		}
		if (!painter.getShowState()) {
			g.setColor(Color.BLACK);
			GraphicsUtil.drawCenteredText(
					g,
					"x" + ((PinAttributes) painter.getAttributeSet()).width.getWidth(),
					-15 - (width - 15) / 2,
					0);
		} else {
			int labelYPos = height / 2 - 2;
			int LabelValueXOffset = (isOutput) ? -15 : -20;
			g.setColor(Color.BLUE);
			g.c.scale(0.7, 0.7);
			g.c.fillText(
					radix.getIndexChar(),
					(int) ((double) LabelValueXOffset / 0.7),
					(int) ((double) labelYPos / 0.7));
			g.c.scale(1.0 / 0.7, 1.0 / 0.7);
			g.setColor(Color.BLACK);
			if (radix == null || radix == RadixOption.RADIX_2) {
				int wid = value.getWidth();
				if (wid == 0) {
					g.setLineWidth(2);
					int x = -15 - (width - 15) / 2;
					g.c.strokeLine(x - 4, 0, x + 4, 0);
					if (dir == Direction.WEST) {
						g.c.translate(-westTranslate, 0);
						g.rotate(180);
					}
					return;
				}
				int x0 = (isOutput) ? -20 : -25;
				int cx = x0;
				int cy = height / 2 - 12;
				int cur = 0;
				for (int k = 0; k < wid; k++) {
					if (radix == RadixOption.RADIX_2 && !isOutput) {
						g.setColor(value.get(k).getColor());
						g.c.fillOval(cx - 4, cy - 5, 9, 14);
						g.setColor(Color.WHITE);
					}
					GraphicsUtil.drawCenteredText(g, value.get(k).toDisplayString(), cx, cy);
					if (radix == RadixOption.RADIX_2 && !isOutput) g.setColor(Color.BLACK);
					++cur;
					if (cur == 8) {
						cur = 0;
						cx = x0;
						cy -= 20;
					} else {
						cx -= 10;
					}
				}
			} else {
				String text = radix.toString(value);
				int cx = (isOutput) ? -15 : -20;
				for (int k = text.length() - 1; k >= 0; k--) {
					GraphicsUtil.drawText(
							g, text.substring(k, k + 1), cx, -2, GraphicsUtil.H_RIGHT, GraphicsUtil.H_CENTER);
					cx -= Pin.DIGIT_WIDTH;
				}
			}
		}
		if (dir == Direction.WEST) {
			g.c.translate(-westTranslate, 0);
			g.rotate(180);
		}
	}

	private void drawInputShape(
			InstancePainter painter,
			int x,
			int y,
			int width,
			int height,
			Paint LineColor,
			boolean isGhost) {

		PinAttributes attrs = (PinAttributes) painter.getAttributeSet();
		boolean isBus = attrs.getValue(StdAttr.WIDTH).getWidth() > 1;
		Direction dir = attrs.getValue(StdAttr.FACING);

		Graphics g = painter.getGraphics();
		int xpos = x + width;
		int ypos = y + height / 2;
		int rwidth = width;
		int rheight = height;
		double rotation = 0;
		if (dir == Direction.NORTH) {
			rotation = -90;
			xpos = x + width / 2;
			ypos = y;
			rwidth = height;
			rheight = width;
		} else if (dir == Direction.SOUTH) {
			rotation = 90;
			xpos = x + width / 2;
			ypos = y + height;
			rwidth = height;
			rheight = width;
		} else if (dir == Direction.WEST) {
			rotation = 180;
			xpos = x;
			ypos = y + height / 2;
		}
		g.c.translate(xpos, ypos);
		g.rotate(rotation);
		if (isBus) {
			g.setLineWidth(Wire.WIDTH);
			g.c.strokeLine(Wire.WIDTH / 2 - 5, 0, 0, 0);
			g.setLineWidth(2);
		} else {
			Color col = g.getColor();
			if (painter.getShowState())
				g.setColor(LineColor);
			g.setLineWidth(Wire.WIDTH);
			g.c.strokeLine(-5, 0, 0, 0);
			g.setLineWidth(2);
			g.setColor(col);
		}
		g.c.strokeLine(-15, -rheight / 2, -5, 0);
		g.c.strokeLine(-15, rheight / 2, -5, 0);
		g.c.strokeLine(-rwidth, -rheight / 2, -rwidth, rheight / 2);
		g.c.strokeLine(-rwidth, -rheight / 2, -15, -rheight / 2);
		g.c.strokeLine(-rwidth, rheight / 2, -15, rheight / 2);
		drawNewStyleValue(painter, rwidth, rheight, false, isGhost);
		g.rotate(-rotation);
		g.c.translate(-xpos, -ypos);

	}

	private void drawOutputShape(
			InstancePainter painter,
			int x,
			int y,
			int width,
			int height,
			Paint LineColor,
			boolean isGhost) {
		PinAttributes attrs = (PinAttributes) painter.getAttributeSet();
		boolean isBus = attrs.getValue(StdAttr.WIDTH).getWidth() > 1;
		Direction dir = attrs.getValue(StdAttr.FACING);

		Graphics g = painter.getGraphics();
		int xpos = x + width;
		int ypos = y + height / 2;
		int rwidth = width;
		int rheight = height;
		double rotation = 0;
		if (dir == Direction.NORTH) {
			rotation = -90;
			xpos = x + width / 2;
			ypos = y;
			rwidth = height;
			rheight = width;
		} else if (dir == Direction.SOUTH) {
			rotation = 90;
			xpos = x + width / 2;
			ypos = y + height;
			rwidth = height;
			rheight = width;
		} else if (dir == Direction.WEST) {
			rotation = 180;
			xpos = x;
			ypos = y + height / 2;
		}
		g.c.translate(xpos, ypos);
		g.rotate(rotation);
		if (isBus) {
			g.setLineWidth(Wire.WIDTH);
			g.c.strokeLine(-3, 0, -Wire.WIDTH / 2, 0);
			g.setLineWidth(2);
		} else {
			Color col = g.getColor();
			if (painter.getShowState())
				g.setColor(LineColor);
			g.setLineWidth(Wire.WIDTH);
			g.c.strokeLine(-3, 0, 0, 0);
			g.setLineWidth(2);
			g.setColor(col);
		}
		g.c.strokeLine(10 - rwidth, -rheight / 2, -rwidth, 0);
		g.c.strokeLine(10 - rwidth, rheight / 2, -rwidth, 0);
		g.c.strokeLine(-5, -rheight / 2, -5, rheight / 2);
		g.c.strokeLine(-5, -rheight / 2, 10 - rwidth, -rheight / 2);
		g.c.strokeLine(-5, rheight / 2, 10 - rwidth, rheight / 2);
		drawNewStyleValue(painter, rwidth, rheight, true, isGhost);
		g.rotate(-rotation);
		g.c.translate(-xpos, -ypos);

	}

	@Override
	public void paintGhost(InstancePainter painter) {
/*
		PinAttributes attrs = (PinAttributes) painter.getAttributeSet();
		Location loc = painter.getLocation();
		Bounds bds = painter.getOffsetBounds();
		int x = loc.getX();
		int y = loc.getY();
		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		boolean output = attrs.isOutput();
		if (output) {
			BitWidth width = attrs.getValue(StdAttr.WIDTH);
			if (width == BitWidth.ONE) {
				g.c.strokeOval(x + bds.getX() + 1, y + bds.getY() + 1,
						bds.getWidth() - 1, bds.getHeight() - 1);
			} else {
				g.c.strokeRoundRect(x + bds.getX() + 1, y + bds.getY() + 1,
						bds.getWidth() - 1, bds.getHeight() - 1, 6, 6);
			}
		} else {
			g.c.strokeRect(x + bds.getX() + 1, y + bds.getY() + 1,
					bds.getWidth() - 1, bds.getHeight() - 1);
		}
*/
		PinAttributes attrs = (PinAttributes) painter.getAttributeSet();
		Location loc = painter.getLocation();
		Bounds bds = painter.getOffsetBounds();
		int x = loc.getX();
		int y = loc.getY();
		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		if (attrs.isOutput()) {
			drawOutputShape(
					painter,
					x + bds.getX(),
					y + bds.getY(),
					bds.getWidth(),
					bds.getHeight(),
					Color.GRAY,
					true);
		} else {
			drawInputShape(
					painter,
					x + bds.getX(),
					y + bds.getY(),
					bds.getWidth(),
					bds.getHeight(),
					Color.GRAY,
					true);
		}

	}

	@Override
	public void paintInstance(InstancePainter painter) {
/*
		PinAttributes attrs = (PinAttributes) painter.getAttributeSet();
		Graphics g = painter.getGraphics();
		Bounds bds = painter.getInstance().getBounds(); // intentionally with no graphics object - we don't want label included
		int x = bds.getX();
		int y = bds.getY();
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		if (attrs.type == EndData.OUTPUT_ONLY) {
			if (attrs.width.getWidth() == 1) {
				g.c.strokeOval(x + 1, y + 1,
						bds.getWidth() - 1, bds.getHeight() - 1);
			} else {
				g.c.strokeRoundRect(x + 1, y + 1,
						bds.getWidth() - 1, bds.getHeight() - 1, 6, 6);
			}
		} else {
			g.c.strokeRect(x + 1, y + 1,
					bds.getWidth() - 1, bds.getHeight() - 1);
		}

		painter.drawLabel();

		if (!painter.getShowState()) {
			g.setColor(Color.BLACK);
			GraphicsUtil.drawCenteredText(g, "x" + attrs.width.getWidth(),
					bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
		} else {
			PinState state = getState(painter);
			if (attrs.width.getWidth() <= 1) {
				Value receiving = state.receiving;
				g.setColor(receiving.getColor());
				g.c.fillOval(x + 4, y + 4, 13, 13);

				if (attrs.width.getWidth() == 1) {
					g.setColor(Color.WHITE);
					GraphicsUtil.drawCenteredText(g,
							state.sending.toDisplayString(), x + 11, y + 9);
				}
			} else {
				Probe.paintValue(painter, state.sending);
			}
		}

		painter.drawPorts();
		g.toDefault();
*/
		PinAttributes attrs = (PinAttributes) painter.getAttributeSet();
		Graphics g = painter.getGraphics();
		Bounds bds = painter.getInstance().getBounds(); // intentionally with no graphics object - we don't want label included
		boolean IsOutput = attrs.type == EndData.OUTPUT_ONLY;
		PinState state = getState(painter);
		Value found = state.receiving;
		int x = bds.getX();
		int y = bds.getY();
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		if (IsOutput) {
			drawOutputShape(
					painter, x + 1, y + 1, bds.getWidth() - 1, bds.getHeight() - 1, found.getColor(), false);
		} else {
			drawInputShape(
					painter, x + 1, y + 1, bds.getWidth() - 1, bds.getHeight() - 1, found.getColor(), false);
		}
		painter.drawLabel();
		painter.drawPorts();
		g.toDefault();

	}

	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		PinAttributes attrs = (PinAttributes) instance.getAttributeSet();
		instance.addAttributeListener();
		configurePorts(instance);
		Probe.configureLabel(instance, attrs.labelloc, attrs.facing);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == ATTR_TYPE) {
			configurePorts(instance);
		} else if (attr == StdAttr.WIDTH || attr == StdAttr.FACING
				|| attr == Pin.ATTR_LABEL_LOC || attr == RadixOption.ATTRIBUTE) {
			instance.recomputeBounds();
			PinAttributes attrs = (PinAttributes) instance.getAttributeSet();
			Probe.configureLabel(instance, attrs.labelloc, attrs.facing);
		}

	}

	private void configurePorts(Instance instance) {

		PinAttributes attrs = (PinAttributes) instance.getAttributeSet();
		String endType = attrs.isOutput() ? Port.INPUT : Port.OUTPUT;
		Port port = new Port(0, 0, endType, StdAttr.WIDTH);
		if (attrs.isOutput()) {
			port.setToolTip(LC.createStringBinding("pinOutputToolTip"));
		} else {
			port.setToolTip(LC.createStringBinding("pinInputToolTip"));
		}
		instance.setPorts(new Port[]{port});

	}

	@Override
	public void propagate(InstanceState state) {

		PinAttributes attrs = (PinAttributes) state.getAttributeSet();
		Value val = state.getPortValue(0);

		PinState q = getState(state);
		if (attrs.type == EndData.OUTPUT_ONLY) {
			q.sending = val;
			q.receiving = val;
			state.setPort(0, Value.createUnknown(attrs.width), 1);
		} else {
			if (!val.isFullyDefined() && !attrs.threeState
					&& state.isCircuitRoot()) {
				q.sending = pull2(q.sending, attrs.width);
				q.receiving = pull2(val, attrs.width);
				state.setPort(0, q.sending, 1);
			} else {
				q.receiving = val;
				if (!val.equals(q.sending)) { // ignore if no change
					state.setPort(0, q.sending, 1);
				}
			}
		}

	}

	private static Value pull2(Value mod, BitWidth expectedWidth) {

		if (mod.getWidth() == expectedWidth.getWidth()) {
			Value[] vs = mod.getAll();
			for (int i = 0; i < vs.length; i++) {
				if (vs[i] == Value.UNKNOWN) vs[i] = Value.FALSE;
			}
			return Value.create(vs);
		} else {
			return Value.createKnown(expectedWidth, 0);
		}

	}

	//
	// basic information methods
	//
	public BitWidth getWidth(Instance instance) {

		PinAttributes attrs = (PinAttributes) instance.getAttributeSet();
		return attrs.width;

	}

	public int getType(Instance instance) {

		PinAttributes attrs = (PinAttributes) instance.getAttributeSet();
		return attrs.type;

	}

	public boolean isInputPin(Instance instance) {

		PinAttributes attrs = (PinAttributes) instance.getAttributeSet();
		return attrs.type != EndData.OUTPUT_ONLY;

	}

	//
	// state information methods
	//
	public Value getValue(InstanceState state) {
		return getState(state).sending;
	}

	public void setValue(InstanceState state, Value value) {

		PinAttributes attrs = (PinAttributes) state.getAttributeSet();
		Object pull = attrs.pull;
		if (pull != PULL_NONE && pull != null && !value.isFullyDefined()) {
			Value[] bits = value.getAll();
			if (pull == PULL_UP) {
				for (int i = 0; i < bits.length; i++) {
					if (bits[i] != Value.FALSE) bits[i] = Value.TRUE;
				}
			} else if (pull == PULL_DOWN) {
				for (int i = 0; i < bits.length; i++) {
					if (bits[i] != Value.TRUE) bits[i] = Value.FALSE;
				}
			}
			value = Value.create(bits);
		}

		PinState myState = getState(state);
		if (value == Value.NIL) {
			myState.sending = Value.createUnknown(attrs.width);
		} else {
			myState.sending = value;
		}

	}

	private static PinState getState(InstanceState state) {

		PinAttributes attrs = (PinAttributes) state.getAttributeSet();
		BitWidth width = attrs.width;
		PinState ret = (PinState) state.getData();
		if (ret == null) {
			Value val = attrs.threeState ? Value.UNKNOWN : Value.FALSE;
			if (width.getWidth() > 1) {
				Value[] arr = new Value[width.getWidth()];
				java.util.Arrays.fill(arr, val);
				val = Value.create(arr);
			}
			ret = new PinState(val, val);
			state.setData(ret);
		}
		if (ret.sending.getWidth() != width.getWidth()) {
			ret.sending = ret.sending.extendWidth(width.getWidth(),
					attrs.threeState ? Value.UNKNOWN : Value.FALSE);
		}
		if (ret.receiving.getWidth() != width.getWidth()) {
			ret.receiving = ret.receiving.extendWidth(width.getWidth(), Value.UNKNOWN);
		}
		return ret;

	}

	private static class PinState implements InstanceData, Cloneable {

		Value sending;
		Value receiving;

		public PinState(Value sending, Value receiving) {
			this.sending = sending;
			this.receiving = receiving;
		}

		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

	}

	public static class PinPoker extends InstancePoker {

		int bitPressed = -1;
		int bitCaret = -1;

		int bitWidth;
		PinState pinState;
		RadixOption radix;
		boolean tristate;

		@Override
		public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
			bitPressed = getBit(state, e);
		}

		@Override
		public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {
/*
			int bit = getBit(state, e);
			if (bit == bitPressed && bit >= 0) {
				handleBitPress(state, bit, e);
			}
			bitPressed = -1;

			if (!((PinAttributes) state.getAttributeSet()).isInput()) {
				bitPressed = -1;
				bitCaret = -1;
				return;
			}*/

			radix = state.getAttributeValue(RadixOption.ATTRIBUTE);
			pinState = getState(state);
			Value value = pinState.sending;
			bitWidth = value.getWidth();
			PinAttributes attrs = (PinAttributes) state.getAttributeSet();
			tristate = (attrs.threeState && attrs.pull == PULL_NONE);

			RadixOption radix = state.getAttributeValue(RadixOption.ATTRIBUTE);
			if (radix == RadixOption.RADIX_10_SIGNED || radix == RadixOption.RADIX_10_UNSIGNED) {
				String s = DialogManager.createInputDialog("", "");
				acceptDecimal(state, s);
			} else if (radix == RadixOption.RADIX_FLOAT) {
				String s = DialogManager.createInputDialog("", "");
				acceptFloat(state, s);
			} else {
				int bit = getBit(state, e);
				if (bit == bitPressed && bit >= 0) {
					bitCaret = bit;
					handleBitPress(state, bit, radix, e, (char) 0);
				}
				if (bitCaret < 0) {
					BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
					int r = (radix == RadixOption.RADIX_16 ? 4 : (radix == RadixOption.RADIX_8 ? 3 : 1));
					bitCaret = ((width.getWidth() - 1) / r) * r;
				}
			}
			bitPressed = -1;

		}

		private void handleBitPress(InstanceState state, int bit, RadixOption radix, LayoutCanvas.CME e, char ch) {

			PinAttributes attrs = (PinAttributes) state.getAttributeSet();
			if (!attrs.isInput()) return;

			Object sourceComp = e.event.getSource();
			if (sourceComp instanceof LayoutCanvas && !state.isCircuitRoot()) {
				LayoutCanvas canvas = (LayoutCanvas) e.event.getSource();
				CircuitState circState = canvas.getCircuitState();

				int choice = DialogManager.createConfirmWarningDialog(
						LC.get("pinFrozenTitle"),
						LC.get("pinFrozenQuestion")
				);

				if (choice == 1) {
					circState = circState.cloneState();
					canvas.getProject().setCircuitState(circState);
					state = circState.getInstanceState(state.getInstance());
				} else {
					return;
				}
			}
/*
			PinState pinState = getState(state);
			Value val = pinState.sending.get(bit);
			if (val == Value.FALSE) {
				val = Value.TRUE;
			} else if (val == Value.TRUE) {
				val = attrs.threeState ? Value.UNKNOWN : Value.FALSE;
			} else {
				val = Value.FALSE;
			}
			pinState.sending = pinState.sending.set(bit, val);
			state.fireInvalidated();
*/

			BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
			PinState pinState = getState(state);
			int r = (radix == RadixOption.RADIX_16 ? 4 : (radix == RadixOption.RADIX_8 ? 3 : 1));
			if (bit + r > width.getWidth()) r = width.getWidth() - bit;
			Value[] val = pinState.sending.getAll();
			boolean tristate = (attrs.threeState && attrs.pull == PULL_NONE);
			if (ch == 0) {
				boolean ones = true, defined = true;
				for (int b = bit; b < bit + r; b++) {
					if (val[b] == Value.FALSE) ones = false;
					else if (val[b] != Value.TRUE) defined = false;
				}
				if (!defined || (ones && !tristate)) {
					for (int b = bit; b < bit + r; b++) val[b] = Value.FALSE;
				} else if (ones && tristate) {
					for (int b = bit; b < bit + r; b++) val[b] = Value.UNKNOWN;
				} else {
					int carry = 1;
					Value[] v = new Value[] {Value.FALSE, Value.TRUE};
					for (int b = bit; b < bit + r; b++) {
						int s = (val[b] == Value.TRUE ? 1 : 0) + carry;
						val[b] = v[(s % 2)];
						carry = s / 2;
					}
				}
			} else {
				int d;
				if ('0' <= ch && ch <= '9') d = ch - '0';
				else if ('a' <= ch && ch <= 'f') d = 0xa + (ch - 'a');
				else if ('A' <= ch && ch <= 'F') d = 0xA + (ch - 'A');
				else return;
				if (d >= 1 << r) return;
				for (int i = 0; i < r; i++)
					val[bit + i] = (((d & (1 << i)) != 0) ? Value.TRUE : Value.FALSE);
			}
			for (int b = bit; b < bit + r; b++)
				pinState.sending = pinState.sending.set(b, val[b]);
			state.fireInvalidated();

		}

		private int getBit(InstanceState state, LayoutCanvas.CME e) {

			RadixOption radix = state.getAttributeValue(RadixOption.ATTRIBUTE);
			BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
			int r;
			if (radix == RadixOption.RADIX_16) {
				r = 4;
			} else if (radix == RadixOption.RADIX_8) {
				r = 3;
			} else if (radix == RadixOption.RADIX_2) {
				r = 1;
			} else {
				return -1;
			}
			if (width.getWidth() <= r) {
				return 0;
			} else {
				Bounds bds = state.getInstance().getBounds();
				int i, j;
				i = getColumn(state, e, r == 1);
				j = getRow(state, e);
				int bit = (r == 1) ? 8 * j + i : i * r;
				if (bit < 0 || bit >= width.getWidth()) {
					return -1;
				} else {
					return bit;
				}
			}

		}

		private int getRow(InstanceState state, LayoutCanvas.CME e) {
			int row = 0;
			Direction dir = state.getAttributeValue(StdAttr.FACING);
			Bounds bds = state.getInstance().getBounds();
			if (dir == Direction.EAST || dir == Direction.WEST)
				row = (bds.getY() + bds.getHeight() - e.localY) / 20;
			else if (dir == Direction.NORTH) row = (bds.getX() + bds.getWidth() - e.localX) / 20;
			else row = (e.localX - bds.getX()) / 20;
			return row;
		}

		private int getColumn(InstanceState state, LayoutCanvas.CME e, boolean isBinair) {
			int col = 0;
			int distance = isBinair ? 10 : DIGIT_WIDTH;
			Direction dir = state.getAttributeValue(StdAttr.FACING);
			Bounds bds = state.getInstance().getBounds();
			if (dir == Direction.EAST || dir == Direction.WEST) {
				int offset = dir == Direction.EAST ? 20 : 10;
				col = (bds.getX() + bds.getWidth() - e.localX - offset) / distance;
			} else if (dir == Direction.NORTH) col = (e.localY - bds.getY() - 20) / distance;
			else col = (bds.getY() + bds.getHeight() - e.localY - 20) / distance;

			return col;
		}

		public void acceptDecimal(InstanceState state, String s) {
			if (isEditDecimalValid(s)) {
				Value newVal;
				if (s.equals(Character.toString(Value.UNKNOWNCHAR).toLowerCase())
						|| s.equals(Character.toString(Value.UNKNOWNCHAR).toUpperCase())
						|| s.equals("???")) {
					newVal = Value.createUnknown(BitWidth.create(bitWidth));
				} else {
					try {
						BigInteger n = new BigInteger(s);
						BigInteger signedMax = new BigInteger("1").shiftLeft(bitWidth - 1);
						if (radix == RadixOption.RADIX_10_SIGNED || n.compareTo(signedMax) < 0) {
							newVal = Value.createKnown(BitWidth.create(bitWidth), n.intValue());
						} else {
							BigInteger max = new BigInteger("1").shiftLeft(bitWidth);
							BigInteger newValue = n.subtract(max);
							newVal = Value.createKnown(BitWidth.create(bitWidth), newValue.intValue());
						}
					} catch (NumberFormatException exception) {
						return;
					}
				}
				pinState.sending = newVal;
				state.fireInvalidated();
			}
		}

		boolean isEditDecimalValid(String s) {
			if (s == null) return false;
			s = s.trim();
			if (s.equals("")) return false;
			if (tristate
					&& (s.equals(Character.toString(Value.UNKNOWNCHAR).toLowerCase())
					|| s.equals(Character.toString(Value.UNKNOWNCHAR).toUpperCase())
					|| s.equals("???"))) return true;
			try {
				BigInteger n = new BigInteger(s);
				if (radix == RadixOption.RADIX_10_SIGNED) {
					BigInteger min = new BigInteger("-1").shiftLeft(bitWidth - 1);
					BigInteger max = new BigInteger("1").shiftLeft(bitWidth - 1);
					return (n.compareTo(min) >= 0) && (n.compareTo(max) < 0);
				} else {
					BigInteger max = new BigInteger("1").shiftLeft(bitWidth);
					return (n.compareTo(BigInteger.ZERO) >= 0) && (n.compareTo(max) < 0);
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}

		public void acceptFloat(InstanceState state, String s) {
			if (isEditFloatValid(s)) {
				Value newVal;
				if (s.equals(Character.toString(Value.UNKNOWNCHAR).toLowerCase())
						|| s.equals(Character.toString(Value.UNKNOWNCHAR).toUpperCase())
						|| s.equals("???")) {
					newVal = Value.createUnknown(BitWidth.create(bitWidth));
				} else {
					double val;
					if (s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("+inf")) val = Double.POSITIVE_INFINITY;
					else if (s.equalsIgnoreCase("-inf")) val = Double.NEGATIVE_INFINITY;
					else if (s.equalsIgnoreCase("nan")) val = Double.NaN;
					else val = Double.parseDouble(s);
					//newVal = bitWidth == 64 ? Value.createKnown(BitWidth.create(32), (int)val) : Value.createKnown(BitWidth.create(32), (int)val);
						newVal = Value.createKnown(BitWidth.create(32), Float.floatToIntBits((float) val));
				}
				pinState.sending = newVal;
				state.fireInvalidated();
			}
		}

		boolean isEditFloatValid(String s) {
			if (s == null) return false;
			s = s.trim();
			if (s.equals("")) return false;
			if (tristate
					&& (s.equals(Character.toString(Value.UNKNOWNCHAR).toLowerCase())
					|| s.equals(Character.toString(Value.UNKNOWNCHAR).toUpperCase())
					|| s.equals("???"))) return true;
			if (s.equalsIgnoreCase("nan")
					|| s.equalsIgnoreCase("inf")
					|| s.equalsIgnoreCase("+inf")
					|| s.equalsIgnoreCase("-inf")) return true;

			try {
				Double.parseDouble(s);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

	}

	public static class PinLogger extends InstanceLogger {

		@Override
		public String getLogName(InstanceState state, Object option) {

			PinAttributes attrs = (PinAttributes) state.getAttributeSet();
			String ret = attrs.label;
			if (ret == null || ret.equals("")) {
				String type = attrs.type == EndData.INPUT_ONLY
						? LC.get("pinInputName") : LC.get("pinOutputName");
				return type + state.getInstance().getLocation();
			} else {
				return ret;
			}

		}

		@Override
		public Value getLogValue(InstanceState state, Object option) {

			PinState s = getState(state);
			return s.sending;

		}

	}


	@Override
	public boolean isHDLSupportedComponent(AttributeSet attrs) {
		return true;
	}

	@Override
	public boolean hasThreeStateDrivers(AttributeSet attrs) {
		/*
		 * We ignore for the moment the three-state property of the pin, as it
		 * is not an active component, just wiring
		 */
		return false;
	}

	@Override
	public boolean requiresNonZeroLabel() {
		return true;
	}

}
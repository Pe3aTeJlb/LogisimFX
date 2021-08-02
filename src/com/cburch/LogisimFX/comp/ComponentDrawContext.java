/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.comp;

import com.cburch.LogisimFX.data.AttributeSet;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.data.Direction;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.util.GraphicsUtil;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.WireSet;
import com.cburch.LogisimFX.prefs.AppPreferences;

import com.sun.javafx.tk.FontMetrics;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ComponentDrawContext {
	private static final int PIN_OFFS = 2;
	private static final int PIN_RAD = 4;

	private Circuit circuit;
	private CircuitState circuitState;
	private Graphics g;
	private boolean showState;
	private boolean showColor;
	private boolean printView;
	private WireSet highlightedWires;
	private InstancePainter instancePainter;

	public ComponentDrawContext(
			Circuit circuit, CircuitState circuitState,
			Graphics cvcontext, boolean printView) {
		this.circuit = circuit;
		this.circuitState = circuitState;
		this.g = cvcontext;
		this.showState = true;
		this.showColor = true;
		this.printView = printView;
		this.highlightedWires = WireSet.EMPTY;
		this.instancePainter = new InstancePainter(this, null);
	}

	public ComponentDrawContext(
			Circuit circuit, CircuitState circuitState,
			Graphics cvcontext) {
		this(circuit, circuitState, cvcontext, false);
	}
	
	public void setShowState(boolean value) {
		showState = value;
	}
	
	public void setShowColor(boolean value) {
		showColor = value;
	}
	
	public InstancePainter getInstancePainter() {
		return instancePainter;
	}
	
	public void setHighlightedWires(WireSet value) {
		this.highlightedWires = value == null ? WireSet.EMPTY : value;
	}
	
	public WireSet getHighlightedWires() {
		return highlightedWires;
	}

	public boolean getShowState() {
		return !printView && showState;
	}

	public boolean isPrintView() {
		return printView;
	}

	public boolean shouldDrawColor() {
		return !printView && showColor;
	}

	public Graphics getGraphics() {
		return g;
	}

	public GraphicsContext getGraphicsContext(){
		return g.c;
	}

	public Circuit getCircuit() {
		return circuit;
	}

	public CircuitState getCircuitState() {
		return circuitState;
	}

	public void setGraphics(Graphics g) {
		this.g = g;
	}

	public void setGraphicsContext(GraphicsContext g) {
		this.g.c = g;
	}

	public Object getGateShape() {
		return AppPreferences.GATE_SHAPE.get();
	}

	//
	// helper methods
	//
	public void drawBounds(Component comp) {
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		Bounds bds = comp.getBounds();
		g.c.strokeRect(bds.getX(), bds.getY(),
				bds.getWidth(), bds.getHeight());
	}

	public void drawRectangle(Component comp) {
		drawRectangle(comp, "");
	}

	public void drawRectangle(Component comp, String label) {
		Bounds bds = comp.getBounds(g);
		drawRectangle(bds.getX(), bds.getY(), bds.getWidth(),
			bds.getHeight(), label);
	}

	public void drawRectangle(int x, int y,
			int width, int height, String label) {
		g.setLineWidth(2);
		g.c.strokeRect(x, y, width, height);
		if (label != null && !label.equals("")) {
			FontMetrics fm = g.getFontMetrics();
			int lwid = (int)fm.computeStringWidth(label);
			if (height > 20) { // centered at top edge
				g.c.fillText(label, x + (width - lwid) / 2,
					y + 2 + fm.getAscent());
			} else { // centered overall
				g.c.fillText(label, x + (width - lwid) / 2,
					y + (height + fm.getAscent()) / 2 - 1);
			}
		}
	}

	public void drawRectangle(ComponentFactory source, int x, int y,
                              AttributeSet attrs, String label) {
		Bounds bds = source.getOffsetBounds(attrs);
		drawRectangle(source, x + bds.getX(), y + bds.getY(), bds.getWidth(),
			bds.getHeight(), label);
	}

	public void drawRectangle(ComponentFactory source, int x, int y,
                              int width, int height, String label) {

		g.setLineWidth(2);
		g.c.strokeRect(x + 1, y + 1, width - 1, height - 1);
		if (label != null && !label.equals("")) {
			FontMetrics fm = g.getFontMetrics();
			int lwid = (int) fm.computeStringWidth(label);
			if (height > 20) { // centered at top edge
				g.c.fillText(label, x + (width - lwid) / 2,
					y + 2 + fm.getAscent());
			} else { // centered overall
				g.c.fillText(label, x + (width - lwid) / 2,
					y + (height + fm.getAscent()) / 2 - 1);
			}
		}
	}

	public void drawDongle(int x, int y) {
		g.setLineWidth(2);
		g.c.strokeOval(x - 4, y - 4, 9, 9);
	}

	public void drawPin(Component comp, int i,
                        String label, Direction dir) {
		Color curColor = g.getColor();
		if (i < 0 || i >= comp.getEnds().size()) return;
		EndData e = comp.getEnd(i);
		Location pt = e.getLocation();
		int x = pt.getX();
		int y = pt.getY();
		if (getShowState()) {
			CircuitState state = getCircuitState();
			g.setColor(state.getValue(pt).getColor());
		} else {
			g.setColor(Color.BLACK);
		}
		g.c.fillOval(x - PIN_OFFS, y - PIN_OFFS, PIN_RAD, PIN_RAD);
		g.setColor(curColor);
		if (dir == Direction.EAST) {
			GraphicsUtil.drawText(g, label, x + 3, y,
					GraphicsUtil.H_LEFT, GraphicsUtil.V_CENTER);
		} else if (dir == Direction.WEST) {
			GraphicsUtil.drawText(g, label, x - 3, y,
					GraphicsUtil.H_RIGHT, GraphicsUtil.V_CENTER);
		} else if (dir == Direction.SOUTH) {
			GraphicsUtil.drawText(g, label, x, y - 3,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
		} else if (dir == Direction.NORTH) {
			GraphicsUtil.drawText(g, label, x, y + 3,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
		}

	}

	public void drawPin(Component comp, int i) {
		EndData e = comp.getEnd(i);
		Location pt = e.getLocation();
		Color curColor = g.getColor();
		if (getShowState()) {
			CircuitState state = getCircuitState();
			g.setColor(state.getValue(pt).getColor());
		} else {
			g.setColor(Color.BLACK);
		}
		g.c.fillOval(pt.getX() - PIN_OFFS, pt.getY() - PIN_OFFS, PIN_RAD, PIN_RAD);

		g.setColor(curColor);
	}

	public void drawPins(Component comp) {
		Color curColor = g.getColor();
		for (EndData e : comp.getEnds()) {
			Location pt = e.getLocation();
			if (getShowState()) {
				CircuitState state = getCircuitState();
				g.setColor(state.getValue(pt).getColor());
			} else {
				g.setColor(Color.BLACK);
			}
			g.c.fillOval(pt.getX() - PIN_OFFS, pt.getY() - PIN_OFFS, PIN_RAD, PIN_RAD);
		}

		g.setColor(curColor);

	}

	public void drawClock(Component comp, int i,
                          Direction dir) {
		Color curColor = g.getColor();
		g.setColor(Color.BLACK);
		g.setLineWidth(2);

		EndData e = comp.getEnd(i);
		Location pt = e.getLocation();
		int x = pt.getX();
		int y = pt.getY();
		final int CLK_SZ = 4;
		final int CLK_SZD = CLK_SZ - 1;
		if (dir == Direction.NORTH) {
			g.c.strokeLine(x - CLK_SZD, y - 1, x, y - CLK_SZ);
			g.c.strokeLine(x + CLK_SZD, y - 1, x, y - CLK_SZ);
		} else if (dir == Direction.SOUTH) {
			g.c.strokeLine(x - CLK_SZD, y + 1, x, y + CLK_SZ);
			g.c.strokeLine(x + CLK_SZD, y + 1, x, y + CLK_SZ);
		} else if (dir == Direction.EAST) {
			g.c.strokeLine(x + 1, y - CLK_SZD, x + CLK_SZ, y);
			g.c.strokeLine(x + 1, y + CLK_SZD, x + CLK_SZ, y);
		} else if (dir == Direction.WEST) {
			g.c.strokeLine(x - 1, y - CLK_SZD, x - CLK_SZ, y);
			g.c.strokeLine(x - 1, y + CLK_SZD, x - CLK_SZ, y);
		}

		g.setColor(curColor);

	}

	public void drawHandles(Component comp) {
		Bounds b = comp.getBounds(g);
		int left = b.getX();
		int right = left + b.getWidth();
		int top = b.getY();
		int bot = top + b.getHeight();
		drawHandle(right, top);
		drawHandle(left,  bot);
		drawHandle(right, bot);
		drawHandle(left,  top);
	}
	
	public void drawHandle(Location loc) {
		drawHandle(loc.getX(), loc.getY());
	}

	public void drawHandle(int x, int y) {
		g.setColor(Color.WHITE);
		g.c.fillRect(x - 3, y - 3, 7, 7);
		g.setColor(Color.BLACK);
		g.c.strokeRect(x - 3, y - 3, 7, 7);
	}

}

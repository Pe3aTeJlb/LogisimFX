/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.draw.actions.ModelAddAction;
import com.cburch.LogisimFX.draw.canvas.AppearanceCanvas;
import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.draw.shapes.Curve;
import com.cburch.LogisimFX.draw.shapes.CurveUtil;
import com.cburch.LogisimFX.draw.shapes.DrawAttr;
import com.cburch.LogisimFX.draw.shapes.LineUtil;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.Location;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

import java.util.List;

public class CurveTool extends AbstractTool {

	private static final int BEFORE_CREATION = 0;
	private static final int ENDPOINT_DRAG = 1;
	private static final int CONTROL_DRAG = 2;
	
	private DrawingAttributeSet attrs;
	private int state;
	private Location end0;
	private Location end1;
	private Curve curCurve;
	private boolean mouseDown;
	private int lastMouseX;
	private int lastMouseY;
	
	public CurveTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
		state = BEFORE_CREATION;
		mouseDown = false;
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("drawcurv.gif");
	}

	@Override
	public Cursor getCursor() {
		return Cursor.CROSSHAIR;
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		state = BEFORE_CREATION;
		repaintArea(canvas);
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		int mx = e.getX();
		int my = e.getY();
		lastMouseX = mx;
		lastMouseY = my;
		mouseDown = true;
		int mods = e.getModifiersEx();
		if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) {
			mx = canvas.snapX(mx);
			my = canvas.snapY(my);
		}
		
		switch (state) {
		case BEFORE_CREATION:
		case CONTROL_DRAG:
			end0 = Location.create(mx, my);
			end1 = end0;
			state = ENDPOINT_DRAG;
			break;
		case ENDPOINT_DRAG:
			curCurve = new Curve(end0, end1, Location.create(mx, my));
			state = CONTROL_DRAG;
			break;
		}
		repaintArea(canvas);
	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		updateMouse(canvas, e.getX(), e.getY(), e.getModifiersEx());
		repaintArea(canvas);
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		Curve c = updateMouse(canvas, e.getX(), e.getY(), e.getModifiersEx());
		mouseDown = false;
		if (state == CONTROL_DRAG) {
			if (c != null) {
				attrs.applyTo(c);
				CanvasModel model = canvas.getModel();
				canvas.doAction(new ModelAddAction(model, c));
				canvas.toolGestureComplete(this, c);
			}
			state = BEFORE_CREATION;
		}
		repaintArea(canvas);
	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {
		int code = e.getKeyCode();
		if (mouseDown && (code == KeyEvent.VK_SHIFT
				|| code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_ALT)) {
			updateMouse(canvas, lastMouseX, lastMouseY, e.getModifiersEx());
			repaintArea(canvas);
		}
	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}

	@Override
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) {
		char ch = e.getKeyChar();
		if (ch == '\u001b') { // escape key
			state = BEFORE_CREATION;
			repaintArea(canvas);
			canvas.toolGestureComplete(this, null);
		}
	}
	
	private Curve updateMouse(AppearanceCanvas canvas, int mx, int my, int mods) {
		lastMouseX = mx;
		lastMouseY = my;
		
		boolean shiftDown = (mods & MouseEvent.SHIFT_DOWN_MASK) != 0;
		boolean ctrlDown = (mods & MouseEvent.CTRL_DOWN_MASK) != 0;
		boolean altDown = (mods & MouseEvent.ALT_DOWN_MASK) != 0;
		Curve ret = null;
		switch (state) {
		case ENDPOINT_DRAG:
			if (mouseDown) {
				if (shiftDown) {
					Location p = LineUtil.snapTo8Cardinals(end0, mx, my);
					mx = p.getX();
					my = p.getY();
				}
				if (ctrlDown) {
					mx = canvas.snapX(mx);
					my = canvas.snapY(my);
				}
				end1 = Location.create(mx, my);
			}
			break;
		case CONTROL_DRAG:
			if (mouseDown) {
				int cx = mx;
				int cy = my;
				if (ctrlDown) {
					cx = canvas.snapX(cx);
					cy = canvas.snapY(cy);
				}
				if (shiftDown) {
					double x0 = end0.getX();
					double y0 = end0.getY();
					double x1 = end1.getX();
					double y1 = end1.getY();
					double midx = (x0 + x1) / 2;
					double midy = (y0 + y1) / 2;
					double dx = x1 - x0;
					double dy = y1 - y0;
					double[] p = LineUtil.nearestPointInfinite(cx, cy,
							midx, midy, midx - dy, midy + dx);
					cx = (int) Math.round(p[0]);
					cy = (int) Math.round(p[1]);
				}
				if (altDown) {
					double[] e0 = { end0.getX(), end0.getY() };
					double[] e1 = { end1.getX(), end1.getY() };
					double[] mid = { cx, cy };
					double[] ct = CurveUtil.interpolate(e0, e1, mid);
					cx = (int) Math.round(ct[0]);
					cy = (int) Math.round(ct[1]);
				}
				ret = new Curve(end0, end1, Location.create(cx, cy));
				curCurve = ret;
			}
			break;
		}
		return ret;
	}

	private void repaintArea(AppearanceCanvas canvas) {
		canvas.repaint();
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getFillAttributes(attrs.getValue(DrawAttr.PAINT_TYPE));
	}

	@Override
	public void draw(AppearanceCanvas canvas) {
		g.setColor(Color.GRAY);
		switch (state) {
		case ENDPOINT_DRAG:
			g.drawLine(end0.getX(), end0.getY(), end1.getX(), end1.getY());
			break;
		case CONTROL_DRAG:
			((Graphics2D) g).draw(curCurve.getCurve2D());
			break;
		}
	}

}

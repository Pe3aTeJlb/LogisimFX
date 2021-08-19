/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.draw.actions.ModelAddAction;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

abstract class RectangularTool extends AbstractTool {

	private boolean active;
	private Location dragStart;
	private int lastMouseX;
	private int lastMouseY;
	private Bounds currentBounds;
	
	public RectangularTool() {
		active = false;
		currentBounds = Bounds.EMPTY_BOUNDS;
	}
	
	public abstract CanvasObject createShape(int x, int y, int w, int h);
	public abstract void drawShape(Graphics g, int x, int y, int w, int h);
	public abstract void fillShape(Graphics g, int x, int y, int w, int h);
	
	@Override
	public Cursor getCursor() {
		return Cursor.CROSSHAIR;
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		Bounds bds = currentBounds;
		active = false;
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		Location loc = Location.create(e.localX, e.localY);
		Bounds bds = Bounds.create(loc);
		dragStart = loc;
		lastMouseX = loc.getX();
		lastMouseY = loc.getY();
		active = canvas.getModel() != null;

	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		updateMouse(canvas, e.localX, e.localY, e);
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		if (active) {
			Bounds bds = computeBounds(canvas, e.localX, e.localY, e);
			currentBounds = Bounds.EMPTY_BOUNDS;
			active = false;
			CanvasObject add = null;
			if (bds.getWidth() != 0 && bds.getHeight() != 0) {
				CanvasModel model = canvas.getModel();
				add = createShape(bds.getX(), bds.getY(),
						bds.getWidth(), bds.getHeight());
				canvas.doAction(new ModelAddAction(model, add));
			}
			canvas.toolGestureComplete(this, add);
		}

	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {

		KeyCode code = e.getCode();
		if (active && (code == KeyCode.SHIFT || code == KeyCode.ALT || code == KeyCode.CONTROL)) {
			updateMouse(canvas, lastMouseX, lastMouseY, null);
		}

	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}
	
	private void updateMouse(AppearanceCanvas canvas, int mx, int my, AppearanceCanvas.CME e) {

		Bounds oldBounds = currentBounds;
		Bounds bds = computeBounds(canvas, mx, my, e);
		if (!bds.equals(oldBounds)) {
			currentBounds = bds;
		}

	}
	
	private Bounds computeBounds(AppearanceCanvas canvas, int mx, int my, AppearanceCanvas.CME e) {

		lastMouseX = mx;
		lastMouseY = my;
		if (!active) {
			return Bounds.EMPTY_BOUNDS;
		} else {
			Location start = dragStart;
			int x0 = start.getX();
			int y0 = start.getY();
			int x1 = mx;
			int y1 = my;
			if (x0 == x1 && y0 == y1) {
				return Bounds.EMPTY_BOUNDS;
			}

			boolean ctrlDown = e.event.isControlDown();
			if (ctrlDown) {
				x0 = AppearanceCanvas.snapXToGrid(x0);
				y0 = AppearanceCanvas.snapYToGrid(y0);
				x1 = AppearanceCanvas.snapXToGrid(x1);
				y1 = AppearanceCanvas.snapYToGrid(y1);
			}
			
			boolean altDown = e.event.isAltDown();
			boolean shiftDown = e.event.isShiftDown();
			if (altDown) {
				if (shiftDown) {
					int r = Math.min(Math.abs(x0 - x1), Math.abs(y0 - y1));
					x1 = x0 + r;
					y1 = y0 + r;
					x0 -= r;
					y0 -= r;
				} else {
					x0 = x0 - (x1 - x0);
					y0 = y0 - (y1 - y0);
				}
			} else {
				if (shiftDown) {
					int r = Math.min(Math.abs(x0 - x1), Math.abs(y0 - y1));
					y1 = y1 < y0 ? y0 - r : y0 + r;
					x1 = x1 < x0 ? x0 - r : x0 + r;
				}
			}
			
			int x = x0;
			int y = y0;
			int w = x1 - x0;
			int h = y1 - y0;
			if (w < 0) {
				x = x1;
				w = -w;
			}
			if (h < 0) {
				y = y1;
				h = -h;
			}
			return Bounds.create(x, y, w, h);
		}

	}
	
	@Override
	public void draw(AppearanceCanvas canvas) {

		Graphics g = canvas.getGraphics();

		Bounds bds = currentBounds;
		if (active && bds != null && bds != Bounds.EMPTY_BOUNDS) {
			g.setColor(Color.GRAY);
			drawShape(g, bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
		}

		g.toDefault();

	}

}

/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.draw.actions.ModelAddAction;
import com.cburch.LogisimFX.draw.canvas.AppearanceCanvas;
import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.draw.shapes.DrawAttr;
import com.cburch.LogisimFX.draw.shapes.LineUtil;
import com.cburch.LogisimFX.draw.shapes.Poly;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.Location;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class PolyTool extends AbstractTool {

	// how close we need to be to the start point to count as "closing the loop"
	private static final int CLOSE_TOLERANCE = 2;
	
	private boolean closed; // whether we are drawing polygons or polylines
	private DrawingAttributeSet attrs;
	private boolean active;
	private ArrayList<Location> locations;
	private boolean mouseDown;
	private int lastMouseX;
	private int lastMouseY;
	
	public PolyTool(boolean closed, DrawingAttributeSet attrs) {
		this.closed = closed;
		this.attrs = attrs;
		active = false;
		locations = new ArrayList<Location>();
	}
	
	@Override
	public ImageView getIcon() {
		if (closed) {
			return IconsManager.getIcon("drawpoly.gif");
		} else {
			return IconsManager.getIcon("drawplin.gif");
		}
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getFillAttributes(attrs.getValue(DrawAttr.PAINT_TYPE));
	}

	@Override
	public Cursor getCursor(AppearanceCanvas canvas) {
		return Cursor.CROSSHAIR);
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		CanvasObject add = commit(canvas);
		canvas.toolGestureComplete(this, add);
		repaintArea(canvas);
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		int mx = e.getX();
		int my = e.getY();
		lastMouseX = mx;
		lastMouseY = my;
		int mods = e.getModifiersEx();
		if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) {
			mx = canvas.snapX(mx);
			my = canvas.snapY(my);
		}

		if (active && e.getClickCount() > 1) {
			CanvasObject add = commit(canvas);
			canvas.toolGestureComplete(this, add);
			return;
		}

		Location loc = Location.create(mx, my);
		ArrayList<Location> locs = locations;
		if (!active) { locs.clear(); locs.add(loc); }
		locs.add(loc);

		mouseDown = true;
		active = canvas.getModel() != null;
		repaintArea(canvas);
	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		updateMouse(canvas, e.getX(), e.getY(), e.getModifiersEx());
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		if (active) {
			updateMouse(canvas, e.getX(), e.getY(), e.getModifiersEx());
			mouseDown = false;
			int size = locations.size();
			if (size >= 3) {
				Location first = locations.get(0);
				Location last = locations.get(size - 1);
				if (first.manhattanDistanceTo(last) <= CLOSE_TOLERANCE) {
					locations.remove(size - 1);
					CanvasObject add = commit(canvas);
					canvas.toolGestureComplete(this, add);
				}
			}
		}
	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {
		int code = e.getKeyCode();
		if (active && mouseDown
				&& (code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL)) {
			updateMouse(canvas, lastMouseX, lastMouseY, e.getModifiersEx());
		}
	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}

	@Override
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) {
		if (active) {
			char ch = e.getKeyChar();
			if (ch == '\u001b') { // escape key
				active = false;
				locations.clear();
				repaintArea(canvas);
				canvas.toolGestureComplete(this, null);
			} else if (ch == '\n') { // enter key
				CanvasObject add = commit(canvas);
				canvas.toolGestureComplete(this, add);
			}
		}
	}
	
	private CanvasObject commit(AppearanceCanvas canvas) {
		if (!active) return null;
		CanvasObject add = null;
		active = false;
		ArrayList<Location> locs = locations;
		for(int i = locs.size() - 2; i >= 0; i--) {
			if (locs.get(i).equals(locs.get(i + 1))) locs.remove(i);
		}
		if (locs.size() > 1) {
			CanvasModel model = canvas.getModel();
			add = new Poly(closed, locs);
			canvas.doAction(new ModelAddAction(model, add));
			repaintArea(canvas);
		}
		locs.clear();
		return add;
	}
	
	private void updateMouse(AppearanceCanvas canvas, int mx, int my, int mods) {
		lastMouseX = mx;
		lastMouseY = my;
		if (active) {
			int index = locations.size() - 1;
			Location last = locations.get(index);
			Location newLast;
			if ((mods & MouseEvent.SHIFT_DOWN_MASK) != 0 && index > 0) {
				Location nextLast = locations.get(index - 1);
				newLast = LineUtil.snapTo8Cardinals(nextLast, mx, my);
			} else {
				newLast = Location.create(mx, my);
			}
			if ((mods & MouseEvent.CTRL_DOWN_MASK) != 0) {
				int lastX = newLast.getX();
				int lastY = newLast.getY();
				lastX = canvas.snapX(lastX);
				lastY = canvas.snapY(lastY);
				newLast = Location.create(lastX, lastY);
			}
			
			if (!newLast.equals(last)) {
				locations.set(index, newLast);
				repaintArea(canvas);
			}
		}
	}

	private void repaintArea(AppearanceCanvas canvas) {
		canvas.repaint();
	}
	
	@Override
	public void draw(AppearanceCanvas canvas) {
		if (active) {
			g.setColor(Color.GRAY);
			int size = locations.size();
			int[] xs = new int[size];
			int[] ys = new int[size];
			for(int i = 0; i < size; i++) {
				Location loc = locations.get(i);
				xs[i] = loc.getX();
				ys[i] = loc.getY();
			}
			g.drawPolyline(xs, ys, size);
			int lastX = xs[xs.length - 1];
			int lastY = ys[ys.length - 1];
			g.fillOval(lastX - 2, lastY - 2, 4, 4);
		}
	}
}

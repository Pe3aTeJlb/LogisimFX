/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.tools;

import LogisimFX.IconsManager;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.circuit.Wire;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Action;

import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class WiringTool extends Tool {

	private static Cursor cursor = Cursor.CROSSHAIR;

	private static final ImageView icon = IconsManager.getIcon("wiring.gif");

	private static final int HORIZONTAL = 1;
	private static final int VERTICAL = 2;
	private static final int DIAGONAL = 3;

	private boolean exists = false;
	private boolean inCanvas = false;
	private Location start = Location.create(0, 0);
	private Location cur = Location.create(0, 0);
	private boolean hasDragged = false;
	private boolean startShortening = false;
	private Wire shortening = null;
	private Action lastAction = null;
	private int direction = 0;

	public WiringTool() {
		super.select(null);
	}

	@Override
	public void select(LayoutCanvas canvas) {
		super.select(canvas);
		lastAction = null;
		reset();
	}

	private void reset() {
		exists = false;
		inCanvas = false;
		start = Location.create(0, 0);
		cur = Location.create(0, 0);
		startShortening = false;
		shortening = null;
		direction = 0;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof WiringTool;
	}

	@Override
	public int hashCode() {
		return WiringTool.class.hashCode();
	}

	@Override
	public String getName() {
		return "Wiring Tool";
	}

	@Override
	public StringBinding getDisplayName() {
		return LC.createStringBinding("wiringTool");
	}

	@Override
	public StringBinding getDescription() {
		return LC.createStringBinding("wiringToolDesc");
	}

	private boolean computeMove(int newX, int newY) {

		if (cur.getX() == newX && cur.getY() == newY) return false;

		boolean isDiagonal = Math.abs(newX - start.getX()) == Math.abs(newY - start.getY());

		Location start = this.start;
		if (direction == 0) {
			if (newX != start.getX()) direction = HORIZONTAL;
			else if (newY != start.getY()) direction = VERTICAL;
		} else if (direction == HORIZONTAL && newX == start.getX()) {
			if (newY == start.getY()) direction = 0;
			else direction = VERTICAL;
		} else if (direction == VERTICAL && newY == start.getY()) {
			if (newX == start.getX()) direction = 0;
			else direction = HORIZONTAL;
		} else if((direction == HORIZONTAL || direction == VERTICAL) &&
				newX != start.getX() && newY != start.getY() && isDiagonal){
			direction = DIAGONAL;
		} else if (direction == DIAGONAL && !isDiagonal){
			if (newX == start.getX() && newY == start.getY()) direction = 0;
			else direction = HORIZONTAL;
			//if (newY == start.getY()) direction = HORIZONTAL;
		}

		return true;

	}

	@Override
	public Set<Component> getHiddenComponents(LayoutCanvas canvas) {
		Component shorten = willShorten(start, cur);
		if (shorten != null) {
			return Collections.singleton(shorten);
		} else {
			return null;
		}
	}

	@Override
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {

		Graphics g = context.getGraphics();
		if (exists) {
			Location e0 = start;
			Location e1 = cur;
			Wire shortenBefore = willShorten(start, cur);
			if (shortenBefore != null) {
				Wire shorten = getShortenResult(shortenBefore, start, cur);
				if (shorten == null) {
					return;
				} else {
					e0 = shorten.getEnd0();
					e1 = shorten.getEnd1();
				}
			}
			int x0 = e0.getX();
			int y0 = e0.getY();
			int x1 = e1.getX();
			int y1 = e1.getY();

			g.setColor(Color.BLACK);
			g.setLineWidth(3);
			g.setLineExtras(StrokeLineCap.ROUND);
			if (direction == HORIZONTAL) {
				if (x0 != x1) g.c.strokeLine(x0, y0, x1, y0);
				if (y0 != y1) g.c.strokeLine(x1, y0, x1, y1);
			} else if (direction == VERTICAL) {
				if (y0 != y1) g.c.strokeLine(x0, y0, x0, y1);
				if (x0 != x1) g.c.strokeLine(x0, y1, x1, y1);
			} else if(direction == DIAGONAL){
				if (x0 != x1 && y0 != y1)g.c.strokeLine(x0, y0, x1, y1);
			}
		} else if (AppPreferences.ADD_SHOW_GHOSTS.getBoolean() && inCanvas) {
			g.setColor(Color.GRAY);
			g.c.fillOval(cur.getX() - 2, cur.getY() - 2, 5, 5);
		}
		g.toDefault();
	}

	@Override
	public void mouseEntered(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		inCanvas = true;
	}

	@Override
	public void mouseExited(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		inCanvas = false;
	}

	@Override
	public void mouseMoved(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (exists) {
			mouseDragged(canvas, g, e);
		} else {
			inCanvas = true;
			int curX = e.snappedX;
			int curY = e.snappedY;
			if (cur.getX() != curX || cur.getY() != curY) {
				cur = Location.create(curX, curY);
			}
		}
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		if (!canvas.getProject().getLogisimFile().contains(canvas.getCircuit())) {
			exists = false;
			canvas.setErrorMessage(LC.createStringBinding("cannotModifyError"),null);
			return;
		}

		if (exists) {
			mouseDragged(canvas, g, e);
		} else {

			int curX = e.snappedX;
			int curY = e.snappedY;

			start = Location.create(curX, curY);
			cur = start;
			exists = true;
			hasDragged = false;

			startShortening = !canvas.getCircuit().getWires(start).isEmpty();
			shortening = null;

			super.mousePressed(canvas, g, e);

		}

	}

	@Override
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		if (exists) {

			int curX = e.snappedX;
			int curY = e.snappedY;
			if (!computeMove(curX, curY)) return;
			hasDragged = true;

			/*
			Rectangle rect = new Rectangle();
			rect.
			rect.add(start.getX(), start.getY());
			rect.add(cur.getX(), cur.getY());
			rect.add(curX, curY);
			rect.grow(3, 3);
			 */

			cur = Location.create(curX, curY);
			super.mouseDragged(canvas, g, e);

			Wire shorten = null;
			if (startShortening) {
				for (Wire w : canvas.getCircuit().getWires(start)) {
					if (w.contains(cur)) { shorten = w; break; }
				}
			}
			if (shorten == null) {
				for (Wire w : canvas.getCircuit().getWires(cur)) {
					if (w.contains(start)) { shorten = w; break; }
				}
			}
			shortening = shorten;

		}

	}

	void resetClick() {
		exists = false;
	}

	@Override
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		if (!exists) return;

		int curX = e.snappedX;
		int curY = e.snappedY;
		if (computeMove(curX, curY)) {
			cur = Location.create(curX, curY);
		}
		if (hasDragged) {
			exists = false;
			super.mouseReleased(canvas, g, e);

			ArrayList<Wire> ws = new ArrayList<Wire>(2);
			if (cur.getY() == start.getY() || cur.getX() == start.getX()) {

				Wire w = Wire.create(cur, start);
				w = checkForRepairs(canvas, w, w.getEnd0());
				w = checkForRepairs(canvas, w, w.getEnd1());
				if (performShortening(canvas, start, cur)) {
					return;
				}
				if (w.getLength() > 0) ws.add(w);

			} else {

				if (direction == HORIZONTAL || direction == VERTICAL) {

					Location m;

					if (direction == HORIZONTAL) {
						m = Location.create(cur.getX(), start.getY());
					} else {
						m = Location.create(start.getX(), cur.getY());
					}

					Wire w0 = Wire.create(start, m);
					Wire w1 = Wire.create(m, cur);
					w0 = checkForRepairs(canvas, w0, start);
					w1 = checkForRepairs(canvas, w1, cur);
					if (w0.getLength() > 0) ws.add(w0);
					if (w1.getLength() > 0) ws.add(w1);

				} else if (direction == DIAGONAL) {

					Wire w = Wire.create(start, cur);
					w = checkForRepairs(canvas, w, w.getEnd0());
					w = checkForRepairs(canvas, w, w.getEnd1());
					if (performShortening(canvas, start, cur)) {
						return;
					}
					if (w.getLength() > 0) ws.add(w);

				}

			}
			if (ws.size() > 0) {
				CircuitMutation mutation = new CircuitMutation(canvas.getCircuit());
				mutation.addAll(ws);
				StringBinding desc;
				if (ws.size() == 1) desc = LC.createStringBinding("addWireAction");
				else desc = LC.createStringBinding("addWiresAction");
				Action act = mutation.toAction(desc);
				canvas.getProject().doAction(act);
				lastAction = act;
			}
		}
	}

	private Wire checkForRepairs(LayoutCanvas canvas, Wire w, Location end) {

		//ошибка тут

		if(w.getRotation() == 0) {
			if (w.getLength() <= 10) return w; // don't repair a short wire to nothing
		}else{
			if (w.getLength() <= 14) return w; // don't repair a short wire to nothing
		}
		if (!canvas.getCircuit().getNonWires(end).isEmpty()) return w;

		int delta = (end.equals(w.getEnd0()) ? 10 : -10);
		Location cand;
		if(w.isDiagonal()){
			cand = Location.create(end.getX() + delta, end.getY() + delta);
		} else if (w.isVertical()) {
			cand = Location.create(end.getX(), end.getY() + delta);
		} else {
			cand = Location.create(end.getX() + delta, end.getY());
		}

		for (Component comp : canvas.getCircuit().getNonWires(cand)) {
			if (comp.getBounds().contains(end)) {
				WireRepair repair = (WireRepair) comp.getFeature(WireRepair.class);
				if (repair != null && repair.shouldRepairWire(new WireRepairData(w, cand))) {
					w = Wire.create(w.getOtherEnd(end), cand);
					//canvas.repaint(end.getX() - 13, end.getY() - 13, 26, 26);
					return w;
				}
			}
		}

		return w;

	}

	private Wire willShorten(Location drag0, Location drag1) {
		Wire shorten = shortening;
		if (shorten == null) {
			return null;
		} else if (shorten.endsAt(drag0) || shorten.endsAt(drag1)) {
			return shorten;
		} else {
			return null;
		}
	}

	private Wire getShortenResult(Wire shorten, Location drag0, Location drag1) {
		if (shorten == null) {
			return null;
		} else {
			Location e0;
			Location e1;
			if (shorten.endsAt(drag0)) {
				e0 = drag1;
				e1 = shorten.getOtherEnd(drag0);
			} else if (shorten.endsAt(drag1)) {
				e0 = drag0;
				e1 = shorten.getOtherEnd(drag1);
			} else {
				return null;
			}
			return e0.equals(e1) ? null : Wire.create(e0, e1);
		}
	}

	private boolean performShortening(LayoutCanvas canvas, Location drag0, Location drag1) {

		Wire shorten = willShorten(drag0, drag1);
		if (shorten == null) {
			return false;
		} else {
			CircuitMutation xn = new CircuitMutation(canvas.getCircuit());
			StringBinding actName;
			Wire result = getShortenResult(shorten, drag0, drag1);
			if (result == null) {
				xn.remove(shorten);
				actName = LC.createComplexStringBinding("removeComponentAction",
						shorten.getFactory().getDisplayGetter().getValue());
			} else {
				xn.replace(shorten, result);
				actName = LC.createComplexStringBinding("shortenWireAction");
			}
			canvas.getProject().doAction(xn.toAction(actName));
			return true;
		}

	}

	@Override
	public void keyPressed(LayoutCanvas canvas, KeyEvent event) {

		switch (event.getCode()) {
			case BACK_SPACE:
				if (lastAction != null && canvas.getProject().getLastAction() == lastAction) {
					canvas.getProject().undoAction();
					lastAction = null;
				}
		}

	}

	@Override
	public ImageView getIcon(){
		return icon;
	}

	@Override
	public Cursor getCursor() { return cursor; }

}

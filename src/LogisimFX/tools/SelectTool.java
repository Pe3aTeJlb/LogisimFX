/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.tools;

import LogisimFX.IconsManager;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.Selection;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.SelectionActions;
import LogisimFX.tools.key.KeyConfigurationEvent;
import LogisimFX.tools.key.KeyConfigurationResult;
import LogisimFX.tools.key.KeyConfigurator;
import LogisimFX.tools.move.MoveGesture;
import LogisimFX.tools.move.MoveRequestListener;
import LogisimFX.tools.move.MoveResult;
import LogisimFX.LogisimVersion;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.ReplacementMap;
import LogisimFX.circuit.Wire;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SelectTool extends Tool {

	private static final Cursor selectCursor = Cursor.DEFAULT;
	private static final Cursor rectSelectCursor = Cursor.CROSSHAIR;
	private static final Cursor moveCursor = Cursor.MOVE;

	private static final int IDLE = 0;
	private static final int MOVING = 1;
	private static final int RECT_SELECT = 2;
	private static final ImageView icon = IconsManager.getIcon("select.gif");

	private static final Color COLOR_UNMATCHED = Color.color(0.753, 0, 0);
	private static final Color COLOR_COMPUTING = Color.color(0.376, 0.753, 0.376);
	private static final Color COLOR_RECT_SELECT = Color.color(0, 0.251, 0.502, 1);
	private static final Color BACKGROUND_RECT_SELECT = Color.color(0.753, 0.753, 1, 0.753);
	
	private static class MoveRequestHandler implements MoveRequestListener {
		private LayoutCanvas canvas;
		
		MoveRequestHandler(LayoutCanvas canvas) {
			this.canvas = canvas;
		}
		
		public void requestSatisfied(MoveGesture gesture, int dx, int dy) {
			canvas.clearErrorMessage();
		}
	}

	private Location start;
	private int state;
	private int curDx;
	private int curDy;
	private boolean drawConnections;
	private MoveGesture moveGesture;
	private HashMap<Component, KeyConfigurator> keyHandlers;
	private HashSet<Selection> selectionsAdded;

	public SelectTool() {
		start = null;
		state = IDLE;
		selectionsAdded = new HashSet<>();
		keyHandlers = null;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof SelectTool;
	}
	
	@Override
	public int hashCode() {
		return SelectTool.class.hashCode();
	}

	@Override
	public String getName() {
		return "Select Tool";
	}

	@Override
	public StringBinding getDisplayName() {
		return LC.createStringBinding("selectTool");
	}

	@Override
	public StringBinding getDescription() {
		return LC.createStringBinding("selectToolDesc");
	}

	@Override
	public AttributeSet getAttributeSet(LayoutCanvas canvas) {
		return canvas.getSelection().getAttributeSet();
	}

	@Override
	public boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver) {
		return true;
	}

	@Override
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {

		int dx = curDx;
		int dy = curDy;
		if (state == MOVING) {

			canvas.getSelection().drawGhostsShifted(context, dx, dy);

			MoveGesture gesture = moveGesture;
			if (gesture != null && drawConnections && (dx != 0 || dy != 0)) {
				MoveResult result = gesture.findResult(dx, dy);
				if (result != null) {
					Collection<Wire> wiresToAdd = result.getWiresToAdd();
					Graphics g = context.getGraphics();
					g.setLineWidth(3);
					g.setColor(Color.GRAY);
					for (Wire w : wiresToAdd) {
						Location loc0 = w.getEnd0();
						Location loc1 = w.getEnd1();
						g.c.strokeLine(loc0.getX(), loc0.getY(),
								loc1.getX(), loc1.getY());
					}
					g.setLineWidth(1);
					g.setColor(COLOR_UNMATCHED);
					for (Location conn : result.getUnconnectedLocations()) {
						int connX = conn.getX();
						int connY = conn.getY();
						g.c.fillOval(connX - 3, connY - 3, 6, 6);
						g.c.fillOval(connX + dx - 3, connY + dy - 3, 6, 6);
					}
					g.toDefault();
				}
			}

		} else if (state == RECT_SELECT) {

			int left = start.getX();
			int right = left + dx;
			if (left > right) { int i = left; left = right; right = i; }
			int top = start.getY();
			int bot = top + dy;
			if (top > bot) { int i = top; top = bot; bot = i; }
			
			Graphics gBase = context.getGraphics();
			int w = right - left - 1;
			int h = bot - top - 1;
			if (w > 2 && h > 2) {
				gBase.setColor(BACKGROUND_RECT_SELECT);
				gBase.c.fillRect(left + 1, top + 1, w - 1, h - 1);
			}
			
			Circuit circ = canvas.getCircuit();
			Bounds bds = Bounds.create(left, top, right - left, bot - top);
			for (Component c : circ.getAllWithin(bds)) {
				Location cloc = c.getLocation();
				c.getFactory().drawGhost(context, COLOR_RECT_SELECT,
						cloc.getX(), cloc.getY(), c.getAttributeSet());
				context.getGraphics().toDefault();
			}

			gBase.setColor(COLOR_RECT_SELECT);
			gBase.setLineWidth(2);
			if (w < 0) w = 0;
			if (h < 0) h = 0;
			gBase.c.strokeRect(left, top, w, h);
			gBase.toDefault();
		}

	}
	
	@Override
	public void select(LayoutCanvas canvas) {
	}
	
	@Override
	public void deselect(LayoutCanvas canvas) {
		moveGesture = null;
	}
	
	@Override
	public void mouseEntered(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		canvas.requestFocus();
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		Circuit circuit = canvas.getCircuit();
		start = Location.create(e.localX, e.localY);
		curDx = 0;
		curDy = 0;
		moveGesture = null;

		// if the user clicks into the selection,
		// selection is being modified
		Collection<Component> in_sel = sel.getComponentsContaining(start, g);
		if (!in_sel.isEmpty()) {
			if (!e.event.isShiftDown()) {
				setState(canvas, MOVING);
				return;
			} else {
				Action act = SelectionActions.drop(sel, in_sel);
				if (act != null) {
					proj.doAction(act);
				}
			}
		}

		// if the user clicks into a component outside selection, user
		// wants to add/reset selection
		Collection<Component> clicked = circuit.getAllContaining(start, g);
		if (!clicked.isEmpty()) {
			if (!e.event.isShiftDown()) {
				//canvas.getProject().getFrameController().setAttributeTable();
				if (sel.getComponentsContaining(start).isEmpty()) {
					Action act = SelectionActions.dropAll(sel);
					if (act != null) {
						proj.doAction(act);
					}
				}
			}
			for (Component comp : clicked) {
				if (!in_sel.contains(comp)) {
					sel.add(comp);
				}
			}
			setState(canvas, MOVING);
			return;
		}

		// The user clicked on the background. This is a rectangular
		// selection (maybe with the shift key down).
		if (!e.event.isShiftDown()) {
			Action act = SelectionActions.dropAll(sel);
			if (act != null) {
				proj.doAction(act);
			}
		}
		setState(canvas, RECT_SELECT);

	}

	@Override
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		if (state == MOVING) {
			computeDxDy(canvas, e, g);
			handleMoveDrag(canvas, curDx, curDy, null);
		} else if (state == RECT_SELECT) {
			curDx = e.localX - start.getX();
			curDy = e.localY - start.getY();
		}

	}
	
	private void handleMoveDrag(LayoutCanvas canvas, int dx, int dy, KeyEvent e) {
		boolean connect = shouldConnect(e);
		drawConnections = connect;
		if (connect) {
			MoveGesture gesture = moveGesture;
			if (gesture == null) {
				gesture = new MoveGesture(new MoveRequestHandler(canvas),
					canvas.getCircuit(), canvas.getSelection().getAnchoredComponents());
				moveGesture = gesture;
			}
			if (dx != 0 || dy != 0) {
				boolean queued = gesture.enqueueRequest(dx, dy);
				if (queued) {
					canvas.setErrorMessage(LC.createStringBinding("moveWorkingMsg"), COLOR_COMPUTING);
					// maybe CPU scheduled led the request to be satisfied
					// just before the "if(queued)" statement. In any case, it
					// doesn't hurt to check to ensure the message belongs.
					if (gesture.findResult(dx, dy) != null) {
						LayoutCanvas.clearErrorMessage();
					}
				}
			}
		}

	}

	private boolean shouldConnect(KeyEvent e) {

		boolean shiftReleased = true;
		if(e!=null)
		shiftReleased = !e.isShiftDown();
		boolean dflt = AppPreferences.MOVE_KEEP_CONNECT.getBoolean();
		if (shiftReleased) {
			return dflt;
		} else {
			return !dflt;
		}
	}

	@Override
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		Project proj = canvas.getProject();
		if (state == MOVING) {

			setState(canvas, IDLE);
			computeDxDy(canvas, e, g);
			int dx = curDx;
			int dy = curDy;
			if (dx != 0 || dy != 0) {
				if (!proj.getLogisimFile().contains(canvas.getCircuit())) {
					canvas.setErrorMessage(LC.createStringBinding("cannotModifyError"),null);
				} else if (canvas.getSelection().hasConflictWhenMoved(dx, dy)) {
					canvas.setErrorMessage(LC.createStringBinding("exclusiveError"),null);
				} else {
					boolean connect = shouldConnect(null);
					drawConnections = false;
					ReplacementMap repl;
					if (connect) {
						MoveGesture gesture = moveGesture;
						if (gesture == null) {
							gesture = new MoveGesture(new MoveRequestHandler(canvas),
									canvas.getCircuit(), canvas.getSelection().getAnchoredComponents());
						}
						canvas.setErrorMessage(LC.createStringBinding("moveWorkingMsg"), COLOR_COMPUTING);
						MoveResult result = gesture.forceRequest(dx, dy);
						repl = result.getReplacementMap();
					} else {
						repl = null;
					}
					Selection sel = canvas.getSelection();
					proj.doAction(SelectionActions.translate(sel, dx, dy, repl));
				}
			}
			moveGesture = null;

		} else if (state == RECT_SELECT) {

			Bounds bds = Bounds.create(start).add(start.getX() + curDx,
				start.getY() + curDy);
			Circuit circuit = canvas.getCircuit();
			Selection sel = canvas.getSelection();
			Collection<Component> in_sel = sel.getComponentsWithin(bds, g);
			for (Component comp : circuit.getAllWithin(bds, g)) {
				if (!in_sel.contains(comp)) sel.add(comp);
			}
			Action act = SelectionActions.drop(sel, in_sel);
			if (act != null) {
				proj.doAction(act);
			}
			setState(canvas, IDLE);

		}

	}
	
	@Override
	public void keyPressed(LayoutCanvas canvas, KeyEvent e) {
		if (state == MOVING && e.getCode() == KeyCode.SHIFT) {
			handleMoveDrag(canvas, curDx, curDy, e);
		} else {
			switch (e.getCode()) {
			case BACK_SPACE:
			case DELETE:
				if (!canvas.getSelection().isEmpty()) {
					Action act = SelectionActions.clear(canvas.getSelection());
					canvas.getProject().doAction(act);
					e.consume();
				}
				break;
			default:
				processKeyEvent(canvas, e, KeyConfigurationEvent.KEY_PRESSED);
			}
		}
	}
	
	@Override
	public void keyReleased(LayoutCanvas canvas, KeyEvent e) {
		if (state == MOVING && e.getCode() == KeyCode.SHIFT) {
			handleMoveDrag(canvas, curDx, curDy, e);
		} else {
			processKeyEvent(canvas, e, KeyConfigurationEvent.KEY_RELEASED);
		}
	}
	
	@Override
	public void keyTyped(LayoutCanvas canvas, KeyEvent e) {
		processKeyEvent(canvas, e, KeyConfigurationEvent.KEY_TYPED);
	}
	
	private void processKeyEvent(LayoutCanvas canvas, KeyEvent e, int type) {
		HashMap<Component, KeyConfigurator> handlers = keyHandlers;
		if (handlers == null) {
			handlers = new HashMap<>();
			Selection sel = canvas.getSelection();
			for (Component comp : sel.getComponents()) {
				ComponentFactory factory = comp.getFactory();
				AttributeSet attrs = comp.getAttributeSet();
				Object handler = factory.getFeature(KeyConfigurator.class, attrs);
				if (handler != null) {
					KeyConfigurator base = (KeyConfigurator) handler;
					handlers.put(comp, base.clone());
				}
			}
			keyHandlers = handlers;
		}

		if (!handlers.isEmpty()) {
			boolean consume = false;
			ArrayList<KeyConfigurationResult> results;
			results = new ArrayList<>();
			for (Map.Entry<Component, KeyConfigurator> entry : handlers.entrySet()) {
				Component comp = entry.getKey();
				KeyConfigurator handler = entry.getValue();
				KeyConfigurationEvent event = new KeyConfigurationEvent(type,
						comp.getAttributeSet(), e, comp);
				KeyConfigurationResult result = handler.keyEventReceived(event);
				consume |= event.isConsumed();
				if (result != null) {
					results.add(result);
				}
			}
			if (consume) {
				e.consume();
			}
			if (!results.isEmpty()) {
				SetAttributeAction act = new SetAttributeAction(canvas.getCircuit(),
						LC.createStringBinding("changeComponentAttributesAction"));
				for (KeyConfigurationResult result : results) {
					Component comp = (Component) result.getEvent().getData();
					Map<Attribute<?>,Object> newValues = result.getAttributeValues();
					for (Map.Entry<Attribute<?>,Object> entry : newValues.entrySet()) {
						act.set(comp, entry.getKey(), entry.getValue());
					}
				}
				if (!act.isEmpty()) {
					canvas.getProject().doAction(act);
				}
			}
		}
	}

	private void computeDxDy(LayoutCanvas canvas, LayoutCanvas.CME e, Graphics g) {

		Bounds bds = canvas.getSelection().getBounds(g);

		int dx;
		int dy;
		if (bds == Bounds.EMPTY_BOUNDS) {
			dx = e.localX - start.getX();
			dy = e.localY - start.getY();
		} else {
			dx = Math.max(e.localX - start.getX(), -bds.getX());
			dy = Math.max(e.localY - start.getY(), -bds.getY());
		}

		Selection sel = canvas.getSelection();
		if (sel.shouldSnap()) {
			dx = LayoutCanvas.snapXToGrid(dx);
			dy = LayoutCanvas.snapYToGrid(dy);
		}
		curDx = dx;
		curDy = dy;

	}

	@Override
	public ImageView getIcon(){
		return icon;
	}

	@Override
	public Cursor getCursor() {
		return state == IDLE ? selectCursor :
			(state == RECT_SELECT ? rectSelectCursor : moveCursor);
	}
	
	@Override
	public Set<Component> getHiddenComponents(LayoutCanvas canvas) {
		if (state == MOVING) {
			int dx = curDx;
			int dy = curDy;
			if (dx == 0 && dy == 0) {
				return null;
			}

			Set<Component> sel = canvas.getSelection().getComponents();
			MoveGesture gesture = moveGesture;
			if (gesture != null && drawConnections) {
				MoveResult result = gesture.findResult(dx, dy);
				if (result != null) {
					HashSet<Component> ret = new HashSet<>(sel);
					ret.addAll(result.getReplacementMap().getRemovals());
					return ret;
				}
			}
			return sel;
		} else {
			return null;
		}
	}

	private void setState(LayoutCanvas canvas, int new_state) {

		if (state == new_state) return; // do nothing if state not new
		state = new_state;
		canvas.setCursor(getCursor());

	}

}

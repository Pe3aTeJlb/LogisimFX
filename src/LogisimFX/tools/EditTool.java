/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.IconsManager;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.Selection;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.SelectionActions;
import LogisimFX.LogisimVersion;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.Wire;
import LogisimFX.proj.Action;

import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.*;

public class EditTool extends Tool {

	private static final int CACHE_MAX_SIZE = 32;

	private static final Location NULL_LOCATION
		= Location.create(Integer.MIN_VALUE, Integer.MIN_VALUE);

	private static final ImageView icon = IconsManager.getIcon("select.gif");

	private SelectTool select;
	private WiringTool wiring;
	private Tool current;
	private LinkedHashMap<Location,Boolean> cache;
	private LayoutCanvas lastCanvas;
	private int lastRawX;
	private int lastRawY;
	private int lastX; // last coordinates where wiring was computed
	private int lastY;
	private LayoutCanvas.CME lastMods; // last modifiers for mouse event
	private Location wireLoc; // coordinates where to draw wiring indicator, if
	private int pressX; // last coordinate where mouse was pressed
	private int pressY; // (used to determine when a short wire has been clicked)
	
	public EditTool(SelectTool select, WiringTool wiring) {
		this.select = select;
		this.wiring = wiring;
		this.current = select;
		this.cache = new LinkedHashMap<Location,Boolean>();
		this.lastX = -1;
		this.wireLoc = NULL_LOCATION;
		this.pressX = -1;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof EditTool;
	}
	
	@Override
	public int hashCode() {
		return EditTool.class.hashCode();
	}

	@Override
	public String getName() {
		return "Edit Tool";
	}
	
	@Override
	public StringBinding getDisplayName() {
		return LC.createStringBinding("editTool");
	}
	
	@Override
	public StringBinding getDescription() {
		return LC.createStringBinding("editToolDesc");
	}

	@Override
	public ImageView getIcon(){
		return icon;
	}

	@Override
	public AttributeSet getAttributeSet() {
		return select.getAttributeSet();
	}
	
	@Override
	public void setAttributeSet(AttributeSet attrs) {
		select.setAttributeSet(attrs);
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
	public Set<Component> getHiddenComponents(LayoutCanvas canvas) {
		return current.getHiddenComponents(canvas);
	}
		
	@Override
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {

		Location loc = wireLoc;
		if (loc != NULL_LOCATION && current != wiring) {
			int x = loc.getX();
			int y = loc.getY();
			Graphics g = context.getGraphics();
			g.setColor(Value.TRUE_COLOR);
			g.setLineWidth(2);
			g.c.strokeOval(x - 5, y - 5, 10, 10);

			g.toDefault();
		}

		current.draw(canvas, context);

	}
	
	@Override
	public void select(LayoutCanvas canvas) {
		current = select;
		lastCanvas = canvas;
		cache.clear();
		select.select(canvas);
	}
	
	@Override
	public void deselect(LayoutCanvas canvas) {
		current = select;
		canvas.getSelection().setSuppressHandles(null);
		cache.clear();
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		boolean wire = updateLocation(canvas, e);
		Location oldWireLoc = wireLoc;
		wireLoc = NULL_LOCATION;
		lastX = Integer.MIN_VALUE;
		if (wire) {
			current = wiring;
			Selection sel = canvas.getSelection();
			Circuit circ = canvas.getCircuit();
			Collection<Component> selected = sel.getAnchoredComponents();
			ArrayList<Component> suppress = null;
			for (Wire w : circ.getWires()) {
				if (selected.contains(w)) {
					if (w.contains(oldWireLoc)) {
						if (suppress == null) suppress = new ArrayList<Component>();
						suppress.add(w);
					}
				}
			}
			sel.setSuppressHandles(suppress);
		} else {
			current = select;
		}
		pressX = e.localX;
		pressY = e.localY;
		current.mousePressed(canvas, g, e);

	}
	
	@Override
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		isClick(e);
		current.mouseDragged(canvas, g, e);

	}
	
	@Override
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		boolean click = isClick(e) && current == wiring;
		canvas.getSelection().setSuppressHandles(null);
		current.mouseReleased(canvas, g, e);
		if (click) {
			wiring.resetClick();
			select.mousePressed(canvas, g, e);
			select.mouseReleased(canvas, g, e);
		}
		current = select;
		cache.clear();
		updateLocation(canvas, e);

	}
	
	@Override
	public void mouseEntered(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		pressX = -1;
		current.mouseEntered(canvas, g, e);
		canvas.requestFocus();

	}
	
	@Override
	public void mouseExited(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		pressX = -1;
		current.mouseExited(canvas, g, e);


	}
	
	@Override
	public void mouseMoved(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		updateLocation(canvas, e);
		select.mouseMoved(canvas, g, e);

	}
	
	private boolean isClick(LayoutCanvas.CME e) {

		int px = pressX;
		if (px < 0) {
			return false;
		} else {
			int dx = e.localX - px;
			int dy = e.localY - pressY;
			if (dx * dx + dy * dy <= 4) {
				return true;
			} else {
				pressX = -1;
				return false;
			}
		}

	}
	
	private boolean updateLocation(LayoutCanvas canvas, LayoutCanvas.CME e) {

		return updateLocation(canvas, e.localX, e.localY, e);

	}
	
	private boolean updateLocation(LayoutCanvas canvas, int mx, int my, LayoutCanvas.CME e) {

		int snapx = LayoutCanvas.snapXToGrid(mx);
		int snapy = LayoutCanvas.snapYToGrid(my);
		int dx = mx - snapx;
		int dy = my - snapy;
		boolean isEligible = dx * dx + dy * dy < 36;
		if (e != null && e.event.isAltDown()) isEligible = true;
		if (!isEligible) {
			snapx = -1;
			snapy = -1;
		}
		boolean modsSame = lastMods == e;
		lastCanvas = canvas;
		lastRawX = mx;
		lastRawY = my;
		lastMods = e;
		if (lastX == snapx && lastY == snapy && modsSame) { // already computed
			return wireLoc != NULL_LOCATION;
		} else {
			Location snap = Location.create(snapx, snapy);
			if (modsSame) {
				Object o = cache.get(snap);
				if (o != null) {
					lastX = snapx;
					lastY = snapy;
					boolean ret = ((Boolean) o).booleanValue();
					wireLoc = ret ? snap : NULL_LOCATION;
					return ret;
				}
			} else {
				cache.clear();
			}

			boolean ret = isEligible && isWiringPoint(canvas, snap, e);
			wireLoc = ret ? snap : NULL_LOCATION;
			cache.put(snap, Boolean.valueOf(ret));
			int toRemove = cache.size() - CACHE_MAX_SIZE;
			Iterator<Location> it = cache.keySet().iterator();
			while (it.hasNext() && toRemove > 0) {
				it.next();
				it.remove();
				toRemove--;
			}

			lastX = snapx;
			lastY = snapy;
			return ret;

		}

	}
	
	private boolean isWiringPoint(LayoutCanvas canvas, Location loc, LayoutCanvas.CME e) {

		boolean wiring = true;
		if(e!=null)
		wiring = !e.event.isAltDown();
		boolean select = !wiring;
		
		if (canvas != null && canvas.getSelection() != null) {
			Collection<Component> sel = canvas.getSelection().getComponents();
			if (sel != null) {
				for (Component c : sel) {
					if (c instanceof Wire) {
						Wire w = (Wire) c;
						if (w.contains(loc) && !w.endsAt(loc)) return select;
					}
				}
			}
		}
		
		Circuit circ = canvas.getCircuit();
		Collection<? extends Component> at = circ.getComponents(loc);
		if (at != null && at.size() > 0) return wiring;
		
		for (Wire w : circ.getWires()) {
			if (w.contains(loc)) { return wiring; }
		}

		return select;

	}

	@Override
	public void keyTyped(LayoutCanvas canvas, KeyEvent e) {
		select.keyTyped(canvas, e);
	}
	
	@Override
	public void keyPressed(LayoutCanvas canvas, KeyEvent e) {

		switch (e.getCode()) {
		case BACK_SPACE:
		case DELETE:
			if (!canvas.getSelection().isEmpty()) {
				Action act = SelectionActions.clear(canvas.getSelection());
				canvas.getProject().doAction(act);
				e.consume();
			} else {
				wiring.keyPressed(canvas, e);
			}
			break;
		case INSERT:
			Action act = SelectionActions.duplicate(canvas.getSelection());
			canvas.getProject().doAction(act);
			e.consume();
			break;
		case UP:
			if (!e.isShortcutDown()) attemptReface(canvas, Direction.NORTH, e);
			else                         select.keyPressed(canvas, e);
			break;
		case DOWN:
			if (!e.isShortcutDown()) attemptReface(canvas, Direction.SOUTH, e);
			else                         select.keyPressed(canvas, e);
			break;
		case LEFT:
			if (!e.isShortcutDown()) attemptReface(canvas, Direction.WEST, e);
			else                         select.keyPressed(canvas, e);
			break;
		case RIGHT:
			if (!e.isShortcutDown()) attemptReface(canvas, Direction.EAST, e);
			else                         select.keyPressed(canvas, e);
			break;
		//case ALT:   updateLocation(canvas, e); e.consume(); break;
		default:
			select.keyPressed(canvas, e);
		}

	}
	
	@Override
	public void keyReleased(LayoutCanvas canvas, KeyEvent e) {

		if (e.getCode() != KeyCode.ALT) {
			select.keyReleased(canvas, e);
		}

	}
	
	private void attemptReface(LayoutCanvas canvas, final Direction facing, KeyEvent e) {

		if (!e.isShortcutDown()) {
			final Circuit circuit = canvas.getCircuit();
			final Selection sel = canvas.getSelection();
			SetAttributeAction act = new SetAttributeAction(circuit,
					LC.createStringBinding("selectionRefaceAction"));
			for (Component comp : sel.getComponents()) {
				if (!(comp instanceof Wire)) {
					Attribute<Direction> attr = getFacingAttribute(comp);
					if (attr != null) {
						act.set(comp, attr, facing);
					}
				}
			}
			if (!act.isEmpty()) {
				canvas.getProject().doAction(act);
				e.consume();
			}
		}

	}
	
	private Attribute<Direction> getFacingAttribute(Component comp) {

		AttributeSet attrs = comp.getAttributeSet();
		Object key = ComponentFactory.FACING_ATTRIBUTE_KEY;
		Attribute<?> a = (Attribute<?>) comp.getFactory().getFeature(key, attrs);
		@SuppressWarnings("unchecked")
        Attribute<Direction> ret = (Attribute<Direction>) a;
		return ret;

	}
	
	@Override
	public Cursor getCursor() {

		return select.getCursor();

	}

}

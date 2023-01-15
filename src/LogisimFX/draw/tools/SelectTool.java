/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.tools;

import LogisimFX.IconsManager;
import LogisimFX.draw.actions.ModelMoveHandleAction;
import LogisimFX.draw.actions.ModelRemoveAction;
import LogisimFX.draw.actions.ModelTranslateAction;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.Selection;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.model.Handle;
import LogisimFX.draw.model.HandleGesture;
import LogisimFX.data.Attribute;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SelectTool extends AbstractTool {

	private static final int IDLE = 0;
	private static final int MOVE_ALL = 1;
	private static final int RECT_SELECT = 2;
	private static final int RECT_TOGGLE = 3;
	private static final int MOVE_HANDLE = 4;
	
	private static final int DRAG_TOLERANCE = 2;
	private static final int HANDLE_SIZE = 8;
	
	private static final Color RECT_SELECT_BACKGROUND = Color.color(0, 0, 0, 0.125);
	
	private int curAction;
	private List<CanvasObject> beforePressSelection;
	private Handle beforePressHandle;
	private Location dragStart;
	private Location dragEnd;
	private boolean dragEffective;
	private int lastMouseX;
	private int lastMouseY;
	private HandleGesture curGesture;
	
	public SelectTool() {
		curAction = IDLE;
		dragStart = Location.create(0, 0);
		dragEnd = dragStart;
		dragEffective = false;
	}

	@Override
	public String getName(){
		return null;
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("select.gif");
	}

	@Override
	public Cursor getCursor() {
		return Cursor.DEFAULT;
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return Collections.emptyList();
	}

	@Override
	public void toolSelected(AppearanceCanvas canvas) {
		curAction = IDLE;
		canvas.getSelection().clearSelected();
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		curAction = IDLE;
		canvas.getSelection().clearSelected();
	}
	
	private int getHandleSize(AppearanceCanvas canvas) {
		double zoom = canvas.getZoom();
		return (int) Math.ceil(HANDLE_SIZE / Math.sqrt(zoom));
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		beforePressSelection = new ArrayList<CanvasObject>(canvas.getSelection().getSelected());
		beforePressHandle = canvas.getSelection().getSelectedHandle();
		int mx = e.localX;
		int my = e.localY;
		boolean shift = e.event.isShiftDown();
		dragStart = Location.create(mx, my);
		dragEffective = false;
		dragEnd = dragStart;
		lastMouseX = mx;
		lastMouseY = my;
		Selection selection = canvas.getSelection();
		selection.setHandleSelected(null);
		
		// see whether user is pressing within an existing handle
		int halfSize = getHandleSize(canvas) / 2;
		CanvasObject clicked = null;
		for (CanvasObject shape : selection.getSelected()) {
			List<Handle> handles = shape.getHandles(null);
			for (Handle han : handles) {
				int dx = han.getX() - mx;
				int dy = han.getY() - my;
				if (dx >= -halfSize && dx <= halfSize
						&& dy >= -halfSize && dy <= halfSize) {
					if (shape.canMoveHandle(han)) {
						curAction = MOVE_HANDLE;
						curGesture = new HandleGesture(han, 0, 0, e);
						return;
					} else if (clicked == null) {
						clicked = shape;
					}
				}
			}
		}

		// see whether the user is clicking within a shape
		if (clicked == null) {
			clicked = getObjectAt(canvas.getModel(), e.localX, e.localY, false);
		}
		if (clicked != null) {
			if (shift && selection.isSelected(clicked)) {
				selection.setSelected(clicked, false);
				curAction = IDLE;
			} else {
				if (!shift && !selection.isSelected(clicked)) {
					selection.clearSelected();
				}
				selection.setSelected(clicked, true);
				selection.setMovingShapes(selection.getSelected(), 0, 0);
				curAction = MOVE_ALL;
			}
			return;
		}
		
		clicked = getObjectAt(canvas.getModel(), e.localX, e.localY, true);
		if (clicked != null && selection.isSelected(clicked)) {
			if (shift) {
				selection.setSelected(clicked, false);
				curAction = IDLE;
			} else {
				selection.setMovingShapes(selection.getSelected(), 0, 0);
				curAction = MOVE_ALL;
			}
			return;
		}

		if (shift) {
			curAction = RECT_TOGGLE;
		} else {
			selection.clearSelected();
			curAction = RECT_SELECT;
		}

	}
	
	@Override
	public void cancelMousePress(AppearanceCanvas canvas) {

		List<CanvasObject> before = beforePressSelection;
		Handle handle = beforePressHandle;
		beforePressSelection = null;
		beforePressHandle = null;
		if (before != null) {
			curAction = IDLE;
			Selection sel = canvas.getSelection();
			sel.clearDrawsSuppressed();
			sel.setMovingShapes(Collections.<CanvasObject>emptySet(), 0, 0);
			sel.clearSelected();
			sel.setSelected(before, true);
			sel.setHandleSelected(handle);
		}

	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		setMouse(canvas, e.localX, e.localY, e);
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		beforePressSelection = null;
		beforePressHandle = null;
		setMouse(canvas, e.localX, e.localY, e);
		
		CanvasModel model = canvas.getModel();
		Selection selection = canvas.getSelection();
		Set<CanvasObject> selected = selection.getSelected();
		int action = curAction;
		curAction = IDLE;
		
		if (!dragEffective) {
			Location loc = dragEnd;
			CanvasObject o = getObjectAt(model, loc.getX(), loc.getY(), false);
			if (o != null) {
				Handle han = o.canDeleteHandle(loc);
				if (han != null) {
					selection.setHandleSelected(han);
				} else {
					han = o.canInsertHandle(loc);
					if (han != null) {
						selection.setHandleSelected(han);
					}
				}
			}
		}
		
		Location start = dragStart;
		int x1 = e.localX;
		int y1 = e.localY;
		switch (action) {
		case MOVE_ALL:
			Location moveDelta = selection.getMovingDelta();
			if (dragEffective && !moveDelta.equals(Location.create(0, 0))) {
				canvas.doAction(new ModelTranslateAction(model, selected,
						moveDelta.getX(), moveDelta.getY()));
			}
			break;
		case MOVE_HANDLE:
			HandleGesture gesture = curGesture;
			curGesture = null;
			if (dragEffective && gesture != null) {
				ModelMoveHandleAction act;
				act = new ModelMoveHandleAction(model, gesture);
				canvas.doAction(act);
				Handle result = act.getNewHandle();
				if (result != null) {
					Handle h = result.getObject().canDeleteHandle(result.getLocation());
					selection.setHandleSelected(h);
				}
			}
			break;
		case RECT_SELECT:
			if (dragEffective) {
				Bounds bds = Bounds.create(start).add(x1, y1);
				selection.setSelected(canvas.getModel().getObjectsIn(bds), true);
			} else {
				CanvasObject clicked;
				clicked = getObjectAt(model, start.getX(), start.getY(), true);
				if (clicked != null) {
					selection.clearSelected();
					selection.setSelected(clicked, true);
				}
			}
			break;
		case RECT_TOGGLE:
			if (dragEffective) {
				Bounds bds = Bounds.create(start).add(x1, y1);
				selection.toggleSelected(canvas.getModel().getObjectsIn(bds));
			} else {
				CanvasObject clicked;
				clicked = getObjectAt(model, start.getX(), start.getY(), true);
				selection.setSelected(clicked, !selected.contains(clicked));
			}
			break;
		}
		selection.clearDrawsSuppressed();

	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {
		KeyCode code = e.getCode();
		if ((code == KeyCode.SHIFT || code == KeyCode.CONTROL
				|| code == KeyCode.ALT) && curAction != IDLE) {
			setMouse(canvas, lastMouseX, lastMouseY, null);
		}
	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}
	
	@Override
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) {

		KeyCode code = e.getCode();
		Selection selected = canvas.getSelection();
		if (code == KeyCode.DELETE && !selected.isEmpty()) {
			ArrayList<CanvasObject> toRemove = new ArrayList<CanvasObject>();
			for (CanvasObject shape : selected.getSelected()) {
				if (shape.canRemove()) {
					toRemove.add(shape);
				}
			}
			if (!toRemove.isEmpty()) {
				e.consume();
				CanvasModel model = canvas.getModel();
				canvas.doAction(new ModelRemoveAction(model, toRemove));
				selected.clearSelected();
			}
		} else if (code == KeyCode.ESCAPE && !selected.isEmpty()) {
			selected.clearSelected();
		}

	}
	
	
	private void setMouse(AppearanceCanvas canvas, int mx, int my, AppearanceCanvas.CME e) {

		lastMouseX = mx;
		lastMouseY = my;
		boolean shift = e.event.isShiftDown();
		boolean ctrl = e.event.isControlDown();
		Location newEnd = Location.create(mx, my);
		dragEnd = newEnd;

		Location start = dragStart;
		int dx = newEnd.getX() - start.getX();
		int dy = newEnd.getY() - start.getY();
		if (!dragEffective) {
			if (Math.abs(dx) + Math.abs(dy) > DRAG_TOLERANCE) {
				dragEffective = true;
			} else {
				return;
			}
		}

		switch (curAction) {
		case MOVE_HANDLE:
			HandleGesture gesture = curGesture;
			if (ctrl) {
				Handle h = gesture.getHandle();
				dx = AppearanceCanvas.snapXToGrid(h.getX() + dx) - h.getX();
				dy = AppearanceCanvas.snapYToGrid(h.getY() + dy) - h.getY();
			}
			curGesture = new HandleGesture(gesture.getHandle(), dx, dy, e);
			canvas.getSelection().setHandleGesture(curGesture);
			break;
		case MOVE_ALL:
			if (ctrl) {
				int minX = Integer.MAX_VALUE;
				int minY = Integer.MAX_VALUE;
				for (CanvasObject o : canvas.getSelection().getSelected()) {
					for (Handle handle : o.getHandles(null)) {
						int x = handle.getX();
						int y = handle.getY();
						if (x < minX) minX = x;
						if (y < minY) minY = y;
					}
				}
				dx = AppearanceCanvas.snapXToGrid(minX + dx) - minX;
				dy = AppearanceCanvas.snapYToGrid(minY + dy) - minY;
			}
			if (shift) {
				if (Math.abs(dx) > Math.abs(dy)) {
					dy = 0;
				} else {
					dx = 0;
				}
			}
			canvas.getSelection().setMovingDelta(dx, dy);
			break;
		}

	}
	
	@Override
	public void draw(AppearanceCanvas canvas) {

		Graphics g = canvas.getGraphics();
		Selection selection = canvas.getSelection();
		int action = curAction;

		Location start = dragStart;
		Location end = dragEnd;
		HandleGesture gesture = null;
		boolean drawHandles;
		switch (action) {
		case MOVE_ALL:
			drawHandles = !dragEffective;
			break;
		case MOVE_HANDLE:
			drawHandles = !dragEffective;
			if (dragEffective) gesture = curGesture;
			break;
		default:
			drawHandles = true;
		}

		CanvasObject moveHandleObj = null;
		if (gesture != null) moveHandleObj = gesture.getHandle().getObject();
		if (drawHandles) {

			g.setLineWidth(1);

			int size = (int) Math.ceil(HANDLE_SIZE);
			int offs = size / 2;
			for (CanvasObject obj : selection.getSelected()) {
				List<Handle> handles;
				if (action == MOVE_HANDLE && obj == moveHandleObj) {
					handles = obj.getHandles(gesture);
				} else {
					handles = obj.getHandles(null);
				}
				for (Handle han : handles) {
					int x = han.getX();
					int y = han.getY();
					if (action == MOVE_ALL && dragEffective) {
						Location delta = selection.getMovingDelta();
						x += delta.getX();
						y += delta.getY();
					}
					x = Math.round(x);
					y = Math.round(y);
					g.c.clearRect(x - offs, y - offs, size, size);
					g.c.strokeRect(x - offs, y - offs, size, size);
				}
			}
			Handle selHandle = selection.getSelectedHandle();
			if (selHandle != null) {
				int x = selHandle.getX();
				int y = selHandle.getY();
				if (action == MOVE_ALL && dragEffective) {
					Location delta = selection.getMovingDelta();
					x += delta.getX();
					y += delta.getY();
				}
				x = Math.round(x);
				y = Math.round(y);
				double[] xs = { x - offs, x, x + offs, x };
				double[] ys = { y, y - offs, y, y + offs };
				g.setColor(Color.WHITE);
				g.c.fillPolygon(xs, ys, 4);
				g.setColor(Color.BLACK);
				g.c.strokePolygon(xs, ys, 4);
				g.toDefault();
			}
		}
		
		switch (action) {
		case RECT_SELECT:
		case RECT_TOGGLE:
			if (dragEffective) {
				// find rectangle currently to show
				int x0 = start.getX();
				int y0 = start.getY();
				int x1 = end.getX();
				int y1 = end.getY();
				if (x1 < x0) { int t = x0; x0 = x1; x1 = t; }
				if (y1 < y0) { int t = y0; y0 = y1; y1 = t; }

				// make the region that's not being selected darker

				int w = canvas.inverseTransformX(canvas.getWidth());
				int h = canvas.inverseTransformY(canvas.getHeight());
				g.setColor(RECT_SELECT_BACKGROUND);
				g.c.fillRect(0, 0, w, y0);
				g.c.fillRect(0, y0, x0, y1 - y0);
				g.c.fillRect(x1, y0, w - x1, y1 - y0);
				g.c.fillRect(0, y1, w, h - y1);

				// now draw the rectangle
				g.setColor(Color.GRAY);
				g.c.strokeRect(x0, y0, x1 - x0, y1 - y0);
				g.toDefault();
			}
			break;
		}
	}

	private static CanvasObject getObjectAt(CanvasModel model, int x, int y,
                                            boolean assumeFilled) {
		Location loc = Location.create(x, y);
		for (CanvasObject o : model.getObjectsFromTop()) {
			if (o.contains(loc, assumeFilled)) return o;
		}

		return null;

	}

}

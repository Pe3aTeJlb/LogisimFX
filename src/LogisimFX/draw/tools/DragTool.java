/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.draw.tools;

import LogisimFX.IconsManager;
import LogisimFX.data.Attribute;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.draw.actions.ModelRemoveAction;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.model.Handle;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.Selection;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragTool extends AbstractTool{

    private static final ImageView icon = IconsManager.getIcon("poke.gif");

    private static final Cursor cursor = Cursor.HAND;

    private static final int IDLE = 0;
    private static final int RECT_SELECT = 2;

    private static final int DRAG_TOLERANCE = 2;
    private static final int HANDLE_SIZE = 8;


    private int curAction;
    private List<CanvasObject> beforePressSelection;
    private Handle beforePressHandle;
    private Location dragStart;
    private Location dragEnd;
    private boolean dragEffective;
    private int lastMouseX;
    private int lastMouseY;

    @Override
    public ImageView getIcon() {
        return icon;
    }

    @Override
    public List<Attribute<?>> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Cursor getCursor() {
        return cursor;
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

        beforePressSelection = new ArrayList<>(canvas.getSelection().getSelected());
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
                    if (clicked == null) {
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
            }
            return;
        }

        clicked = getObjectAt(canvas.getModel(), e.localX, e.localY, true);
        if (clicked != null && selection.isSelected(clicked)) {
            if (shift) {
                selection.setSelected(clicked, false);
                curAction = IDLE;
            }
            return;
        }

        if (!shift) {
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
            sel.setMovingShapes(Collections.emptySet(), 0, 0);
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
        if (action == RECT_SELECT) {
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
            ArrayList<CanvasObject> toRemove = new ArrayList<>();
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
        Location newEnd = Location.create(mx, my);
        dragEnd = newEnd;

        Location start = dragStart;
        int dx = newEnd.getX() - start.getX();
        int dy = newEnd.getY() - start.getY();
        if (!dragEffective) {
            if (Math.abs(dx) + Math.abs(dy) > DRAG_TOLERANCE) {
                dragEffective = true;
            }
        }

    }

    @Override
    public void draw(AppearanceCanvas canvas) {

        Graphics g = canvas.getGraphics();
        Selection selection = canvas.getSelection();

        g.setLineWidth(1);

        int size = (int) Math.ceil(HANDLE_SIZE);
        int offs = size / 2;
        for (CanvasObject obj : selection.getSelected()) {
            List<Handle> handles;
            handles = obj.getHandles(null);
            for (Handle han : handles) {
                int x = han.getX();
                int y = han.getY();
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

    private static CanvasObject getObjectAt(CanvasModel model, int x, int y,
                                            boolean assumeFilled) {
        Location loc = Location.create(x, y);
        for (CanvasObject o : model.getObjectsFromTop()) {
            if (o.contains(loc, assumeFilled)) return o;
        }

        return null;

    }

}

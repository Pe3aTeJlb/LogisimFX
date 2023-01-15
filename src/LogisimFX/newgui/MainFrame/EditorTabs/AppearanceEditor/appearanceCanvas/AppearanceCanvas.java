/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.appear.AppearanceElement;
import LogisimFX.data.Location;
import LogisimFX.draw.actions.ModelAddAction;
import LogisimFX.draw.actions.ModelReorderAction;
import LogisimFX.draw.model.*;
import LogisimFX.draw.tools.DragTool;
import LogisimFX.draw.tools.SelectTool;
import LogisimFX.draw.undo.Action;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;

import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class AppearanceCanvas extends Canvas {

    private AnchorPane root;

    private Project proj;

    //public Canvas;
    private Graphics g;
    private double width, height;

    private static final double MIN_ZOOM = 5;
    private static final double MAX_ZOOM = 0.5;
    private double zoom;
    private double dragScreenX, dragScreenY;
    private double[] transform;

    private AnimationTimer update;

    //Grid
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color GRID_DOT = Color.gray(0.18,1);
    private static final Color GRID_DOT_QUARTER = Color.gray(0.10,1);

    private static final double SPACING_X = 10;
    private static final double SPACING_Y = 10;

    private CanvasModel model;
    private CanvasTool tool;
    private SelectTool selectTool;
    private Selection selection;
    private CircuitState circuitState;

    private Circuit circ;

    private ContextMenu contextMenu;

    private AppearanceCanvas canvas;
    private AppearanceEditHandler appearanceEditHandler;

    private MyListener listener = new MyListener();

    private class MyListener implements CanvasModelListener, ProjectListener {

        public void modelChanged(CanvasModelEvent event) {
            getSelection().modelChanged(event);
        }

        @Override
        public void projectChanged(ProjectEvent event) {

            int act = event.getAction();

            if (act == ProjectEvent.ACTION_SET_TOOL) {

                CanvasTool t = event.getAbstractTool();

                if (t != null) {
                    canvas.setCursor(t.getCursor());
                } else {
                    canvas.setCursor(Cursor.DEFAULT);
                }

            }

        }

    }

    public AppearanceCanvas(AnchorPane rt, Project project, Circuit circ){

        super(rt.getWidth(),rt.getHeight());

        this.circ = circ;

        canvas = this;

        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        this.setFocusTraversable(true);

        root = rt;

        proj = project;
        proj.addProjectListener(listener);

        g = new Graphics(this.getGraphicsContext2D());
        g.toDefault();

        setCanvasEvents();

        appearanceEditHandler = new AppearanceEditHandler(this);

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        model = proj.getCurrentCircuit().getAppearance();
        model.addCanvasModelListener(listener);

        selection = new Selection();

        selectTool = new SelectTool();

        update = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };

        setModel(circ.getAppearance());

        //update.start();

    }

    public void updateStop(){
        update.stop();
    }

    public void updateResume(){
        update.start();
    }

    //Unity Hie!
    private void Update(){

        //System.out.println("appearance canvas draw");

        updateCanvasSize();

        clearRect40K(transform[4], transform[5]);

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        model = proj.getCurrentCircuit().getAppearance();

        drawGrid();

        //setCircuit(proj, proj.getCircuitState());

        tool = proj.getAbstractTool();

        if (model != null) {
            model.paint(g, selection);
            g.toDefault();
        }

        if (tool != null) {
            tool.draw(this);
            g.toDefault();
        }

    }

    public void updateCanvasSize(){

        width = root.getWidth();
        height = root.getHeight();

        this.setWidth(width);
        this.setHeight(height);

    }

    private void drawGrid(){

        for (int x = inverseSnapXToGrid(0);
             x < inverseSnapXToGrid((int)this.getWidth()); x += SPACING_X) {

            for (int y = inverseSnapYToGrid(0);
                 y < inverseSnapYToGrid((int)this.getHeight()); y += SPACING_Y) {

                if(zoom < 0.8f && (float)x % 50 == 0 && (float)y % 50 == 0){
                    g.c.setFill(GRID_DOT_QUARTER);
                    g.c.fillRect(x,y,2,2);
                }else{
                    g.c.setFill(GRID_DOT);
                    g.c.fillRect(x,y,1,1);
                }

            }

        }

    }

    private void setCanvasEvents(){

       // this.addEventFilter(MouseEvent.ANY, (e) -> this.requestFocus());
      //  this.addEventFilter(KeyEvent.ANY, (e) -> this.requestFocus());

        this.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            this.requestFocus();

            if(event.getButton() != MouseButton.SECONDARY &&
                    contextMenu != null &&
                    contextMenu.isShowing() &&
                    !event.getTarget().equals(contextMenu)){
                    contextMenu.hide();
            }

            if (event.getButton() == MouseButton.SECONDARY) {
                handlePopupTrigger(new AppearanceCanvas.CME(event));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                if (tool != null) tool.mousePressed(this, new AppearanceCanvas.CME(event));
            }

        });

        this.setOnMouseDragged(event -> {

            if(event.getButton() == MouseButton.MIDDLE || (event.getButton() == MouseButton.PRIMARY && tool instanceof DragTool)) {
                double dx = event.getX() - dragScreenX;
                double dy = event.getY() - dragScreenY;

                if (dx == 0 && dy == 0) {
                    return;
                }

                if(transform[4] + dx > 0){
                    dx = 0;
                    transform[4] = 0;
                }

                if(transform[5] + dy > 0){
                    dy = 0;
                    transform[5] = 0;
                }

                clearRect40K(transform[4], transform[5]);

                transform[4] += dx;
                transform[5] += dy;

                dragScreenX = event.getX();
                dragScreenY = event.getY();
            }

            if (tool != null) {
                tool.mouseDragged(this, new AppearanceCanvas.CME(event));
            }

        });

        this.setOnScroll(event -> {

            clearRect40K(transform[4], transform[5]);

            double newScale;
            double oldScale = transform[0];
            double val = event.getDeltaY()*.005;

            newScale = Math.max(oldScale+val, MAX_ZOOM);
            newScale = Math.min(newScale, MIN_ZOOM);

            int cx = inverseTransformX(width / 2);
            int cy = inverseTransformY(height / 2);

            transform[0] = newScale;
            transform[3] = newScale;

            // adjust translation to keep center of screen constant
            // inverse transform = (x-t4)/t0

            zoom = newScale;

            transform[4] = width / 2 - cx * newScale;
            transform[5] = height / 2 - cy * newScale;

            if(transform[4] > 0) transform[4] = 0;
            if(transform[5] > 0) transform[5] = 0;

        });

        this.setOnMouseMoved(event -> {

            if (tool != null) tool.mouseMoved(this, new AppearanceCanvas.CME(event));

        });

        this.setOnMouseReleased(event -> {

            if (event.getButton() == MouseButton.PRIMARY) {
                if (tool != null) tool.mouseReleased(this, new AppearanceCanvas.CME(event));
            }

        });

        this.setOnMouseEntered(event -> {

            if (tool != null) tool.mouseEntered(this, new AppearanceCanvas.CME(event));
            this.requestFocus();

        });

        this.setOnMouseExited(event -> {

            if (tool != null) tool.mouseExited(this, new AppearanceCanvas.CME(event));
            this.requestFocus();

        });

        this.setOnKeyPressed(event -> {

            if (tool != null) tool.keyPressed(this, event);
            if(event.getCode().isArrowKey())event.consume();

        });

        this.setOnKeyReleased(event -> {

            if (tool != null) tool.keyReleased(this, event);
            event.consume();

        });

        this.setOnKeyTyped(event -> {

            if (tool != null) tool.keyTyped(this, event);
            event.consume();

        });

    }

    private void handlePopupTrigger(AppearanceCanvas.CME e) {

        Location loc = Location.create(e.localX, e.localY);
        List<CanvasObject> objects = this.getModel().getObjectsFromTop();
        CanvasObject clicked = null;

        for (CanvasObject o : objects) {
            if (o.contains(loc, false)) {
                clicked = o;
                break;
            }
        }

        if (clicked == null) {
            for (CanvasObject o : objects) {
                if (o.contains(loc, true)) {
                    clicked = o;
                    break;
                }
            }
        }

        contextMenu = ContextMenuManager.AppearanceEditContextMenu(this);
        contextMenu.show(this,e.event.getScreenX(),e.event.getScreenY());

    }

    public void doAction(Action canvasAction) {

        Circuit circuit = circuitState.getCircuit();
        if (!proj.getLogisimFile().contains(circuit)) {
            return;
        }

        if (canvasAction instanceof ModelReorderAction) {
            int max = getMaxIndex(getModel());
            ModelReorderAction reorder = (ModelReorderAction) canvasAction;
            List<ReorderRequest> rs = reorder.getReorderRequests();
            List<ReorderRequest> mod = new ArrayList<ReorderRequest>(rs.size());
            boolean changed = false;
            boolean movedToMax = false;
            for (ReorderRequest r : rs) {
                CanvasObject o = r.getObject();
                if (o instanceof AppearanceElement) {
                    changed = true;
                } else {
                    if (r.getToIndex() > max) {
                        int from = r.getFromIndex();
                        changed = true;
                        movedToMax = true;
                        if (from == max && !movedToMax) {
                            ; // this change is ineffective - don't add it
                        } else {
                            mod.add(new ReorderRequest(o, from, max));
                        }
                    } else {
                        if (r.getToIndex() == max) movedToMax = true;
                        mod.add(r);
                    }
                }
            }
            if (changed) {
                if (mod.isEmpty()) {
                    return;
                }
                canvasAction = new ModelReorderAction(getModel(), mod);
            }
        }

        if (canvasAction instanceof ModelAddAction) {
            ModelAddAction addAction = (ModelAddAction) canvasAction;
            int cur = addAction.getDestinationIndex();
            int max = getMaxIndex(getModel());
            if (cur > max) {
                canvasAction = new ModelAddAction(getModel(),
                        addAction.getObjects(), max + 1);
            }
        }

        proj.doAction(new CanvasActionAdapter(circuit, canvasAction));

    }

    public void toolGestureComplete(CanvasTool tool, CanvasObject created) {

        if (tool == this.tool && tool != selectTool) {
            if (created != null) {
                proj.setAbstractTool(selectTool);
                getSelection().clearSelected();
                getSelection().setSelected(created, true);
            }
        }

    }

    //Tools

    // convert screen coordinates to grid coordinates by inverting circuit transform
    public int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    public int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
    }

    public int inverseSnapXToGrid(int x) {

        x = (int) ((x-transform[4])/transform[0]);;

        if (x < 0) {
            return -((-x + 5) / 10) * 10;
        } else {
            return ((x + 5) / 10) * 10;
        }

    }

    public int inverseSnapYToGrid(int y) {

        y = (int) ((y-transform[5])/transform[3]);

        if (y < 0) {
            return -((-y + 5) / 10) * 10;
        } else {
            return ((y + 5) / 10) * 10;
        }

    }

    public static int snapXToGrid(int x) {

        if (x < 0) {
            return -((-x + 5) / 10) * 10;
        } else {
            return ((x + 5) / 10) * 10;
        }

    }

    public static int snapYToGrid(int y) {

        if (y < 0) {
            return -((-y + 5) / 10) * 10;
        } else {
            return ((y + 5) / 10) * 10;
        }

    }

    private void clearRect40K() {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(0,0,(this.getWidth()/transform[0])*2,(this.getHeight()/transform[0])*2);

    }

    private void clearRect40K(double prevX, double prevY) {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(-prevX/transform[0],-prevY/transform[0],this.getWidth()/transform[0],
                this.getHeight()/transform[0]);

    }

    static int getMaxIndex(CanvasModel model) {

        List<CanvasObject> objects = model.getObjectsFromBottom();
        for (int i = objects.size() - 1; i >= 0; i--) {
            if (!(objects.get(i) instanceof AppearanceElement)) {
                return i;
            }
        }

        return -1;

    }



    public Project getProject() {
        return proj;
    }

    public Circuit getCircuit(){
        return proj.getCurrentCircuit();
    }

    public CanvasModel getModel(){
        return model;
    }

    public Graphics getGraphics(){
        return g;
    }

    public Selection getSelection(){
        return selection;
    }

    public AppearanceEditHandler getEditHandler(){
        return appearanceEditHandler;
    }


    public void setCircuit(Project proj, CircuitState circuitState) {

        this.proj = proj;
        this.circuitState = circuitState;
        Circuit circuit = circuitState.getCircuit();
        setModel(circuit.getAppearance());

    }

    public void setModel(CanvasModel value) {
        model.removeCanvasModelListener(listener);
        if (value != null) {
            model = value;
            model.addCanvasModelListener(listener);
        }
    }

    public double getZoom(){
        return transform[0];
    }



    public class CME{

        public int globalX, globalY;
        public int localX, localY;
        public int snappedX, snappedY;

        public MouseEvent event;

        public CME(MouseEvent event){

            this.event = event;

            globalX = (int)event.getX();
            globalY = (int)event.getY();

            localX = inverseTransformX(event.getX());
            localY = inverseTransformY(event.getY());

            snappedX = inverseSnapXToGrid((int)event.getX());
            snappedY = inverseSnapYToGrid((int)event.getY());

            //System.out.println("Global " + globalX + " " + globalY);
            // System.out.println("Local " + localX + " " + localY);
            // System.out.println("Snapped " + snappedX + " " + snappedY);

        }

    }


    public void terminateCanvas(){

        update.stop();

    }

}

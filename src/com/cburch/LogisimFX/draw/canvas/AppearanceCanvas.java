package com.cburch.LogisimFX.draw.canvas;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.newgui.MainFrame.LayoutCanvas;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.MenuTool;
import com.cburch.LogisimFX.tools.Tool;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.StringBinding;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

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
    private static double[] transform;

    private AnimationTimer update;

    //Grid
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color GRID_DOT = Color.gray(0.18,1);
    private static final Color GRID_DOT_QUARTER = Color.gray(0.10,1);

    private static final double SPACING_X = 10;
    private static final double SPACING_Y = 10;

    private CanvasModel model;
    private Selection selection;

    public AppearanceCanvas(AnchorPane rt, Project project){

        super(rt.getWidth(),rt.getHeight());

        root = rt;

        proj = project;

        this.setFocusTraversable(true);

        g = new Graphics(this.getGraphicsContext2D());
        g.toDefault();

        setCanvasEvents();

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        model = null;
        selection = new Selection();

        update = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };

        update.start();

    }

    //Unity Hie!
    private void Update(){

        updateCanvasSize();

        clearRect40K(transform[4], transform[5]);

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        drawGrid();

        g.setColor(Color.GREEN);
        g.c.fillOval(0,0,10,10);

        /*
        CanvasModel model = this.model;
        CanvasTool tool = listener.getTool();
        if (model != null) {
            java.awt.Graphics dup = g.create();
            model.paint(g, selection);
            dup.dispose();
        }
        if (tool != null) {
            java.awt.Graphics dup = g.create();
            tool.draw(this, dup);
            dup.dispose();
        }

         */

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

        this.addEventFilter(MouseEvent.ANY, (e) -> this.requestFocus());

        this.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            /*
            dragTool = proj.getTool();
            if (dragTool != null) {
                dragTool.mousePressed(this, getGraphics(), new LayoutCanvas.CME(event));
                proj.getSimulator().requestPropagate();
            }

            if(event.getButton() == MouseButton.SECONDARY){
                dragTool = new MenuTool();
                if (dragTool != null) {
                    dragTool.mousePressed(this, getGraphics(), new LayoutCanvas.CME(event));
                }
            }

             */

        });



        this.setOnMouseDragged(event -> {

            if(event.getButton() == MouseButton.MIDDLE) {
                double dx = event.getX() - dragScreenX;
                double dy = event.getY() - dragScreenY;
                if (dx == 0 && dy == 0) {
                    return;
                }

                clearRect40K(transform[4], transform[5]);

                transform[4] += dx;
                transform[5] += dy;

                dragScreenX = event.getX();
                dragScreenY = event.getY();
            }

            //if (dragTool != null) {
           //     dragTool.mouseDragged(this, getGraphics(), new LayoutCanvas.CME(event));
           // }

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
            System.out.println("Zoom " + zoom);

            transform[4] = width / 2 - cx * newScale;
            transform[5] = height / 2 - cy * newScale;

        });
/*
        this.setOnMouseMoved(event -> {

            Tool tool = proj.getTool();
            if (tool != null) {
                tool.mouseMoved(this, g, new LayoutCanvas.CME(event));
            }

        });

        this.setOnMouseReleased(event -> {

            if (dragTool != null) {
                dragTool.mouseReleased(this, getGraphics(), new LayoutCanvas.CME(event));
                dragTool = null;
            }

            Tool tool = proj.getTool();
            if (tool != null) {
                tool.mouseMoved(this, getGraphics(), new LayoutCanvas.CME(event));
            }

        });

        this.setOnMouseEntered(event -> {

            if (dragTool != null) {
                dragTool.mouseEntered(this, getGraphics(), new LayoutCanvas.CME(event));
            } else {
                Tool tool = proj.getTool();
                if (tool != null) {
                    tool.mouseEntered(this, getGraphics(), new LayoutCanvas.CME(event));
                }
            }

        });

        this.setOnMouseExited(event -> {

            if (dragTool != null) {
                dragTool.mouseExited(this, getGraphics(), new LayoutCanvas.CME(event));
            } else {
                Tool tool = proj.getTool();
                if (tool != null) {
                    tool.mouseExited(this, getGraphics(), new LayoutCanvas.CME(event));
                }
            }

        });

        this.setOnKeyPressed(event -> {
            Tool tool = proj.getTool();
            if (tool != null) tool.keyPressed(this, event);

        });

        this.setOnKeyReleased(event -> {
            Tool tool = proj.getTool();
            if (tool != null) tool.keyReleased(this, event);

        });

        this.setOnKeyTyped(event -> {
            Tool tool = proj.getTool();
            if (tool != null) tool.keyTyped(this, event);

        });
        */

    }


    //Tools

    // convert screen coordinates to grid coordinates by inverting circuit transform
    public static int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    public static int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
    }

    public static int inverseSnapXToGrid(int x) {

        x = (int) ((x-transform[4])/transform[0]);;

        if (x < 0) {
            return -((-x + 5) / 10) * 10;
        } else {
            return ((x + 5) / 10) * 10;
        }

    }

    public static int inverseSnapYToGrid(int y) {

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



    public static class CME{

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

}

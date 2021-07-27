package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.WidthIncompatibilityData;
import com.cburch.LogisimFX.circuit.WireSet;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.data.BitWidth;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.proj.Project;

import com.cburch.LogisimFX.util.GraphicsUtil;

import com.sun.javafx.tk.FontMetrics;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Set;

public class CustomCanvas extends Canvas {

    private AnchorPane root;

    public Canvas cv;
    private Graphics g;
    private double width, height;

    private static final double MIN_ZOOM = 5;
    private static final double MAX_ZOOM = 0.5;
    private double zoom;
    private double dragScreenX, dragScreenY;
    private double[] transform;


    //Grid
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color GRID_DOT = Color.gray(0.18,1);
    private static final Color GRID_DOT_QUARTER = Color.gray(0.10,1);

    private static final double SPACING_X = 10;
    private static final double SPACING_Y = 10;

    private AnimationTimer update;

    private Project proj;

    private WireSet highlightedWires = WireSet.EMPTY;
    private static final Set<Component> NO_COMPONENTS = Collections.emptySet();
    private ComponentDrawContext context, ptContext;

    public CustomCanvas(AnchorPane rt, Project project){

        root = rt;

        proj = project;

        cv = new Canvas(root.getWidth(),root.getHeight());
        g = new Graphics(cv.getGraphicsContext2D());
        g.toDefault();

        root.getChildren().add(cv);

        setCanvasEvents();

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

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

        g.setColor(Color.RED);
        g.c.fillOval(0,0,10,10);


        drawWithUserState();

        drawWidthIncompatibilityData();

        Circuit circ = proj.getCurrentCircuit();
        CircuitState circState = proj.getCircuitState();

        ComponentDrawContext ptContext = new ComponentDrawContext(circ, circState, g);
        ptContext.setHighlightedWires(highlightedWires);

        g.setColor(Color.RED);

        circState.drawOscillatingPoints(ptContext);

        g.setColor(Color.BLUE);

        proj.getSimulator().drawStepPoints(ptContext);

    }

    public void updateCanvasSize(){

        width = root.getWidth();
        height = root.getHeight();

        cv.setWidth(width);
        cv.setHeight(height);

    }

    private void drawGrid(){

            for (int x = snapXToGrid(inverseTransformX(0)); x < snapXToGrid(inverseTransformX(cv.getWidth())); x += SPACING_X) {

                for (int y = snapYToGrid(inverseTransformY(0)); y < snapYToGrid(inverseTransformY(cv.getHeight())); y += SPACING_Y) {

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

    private void drawWithUserState() {

        Circuit circ = proj.getCurrentCircuit();
        //Selection sel = proj.getSelection();
        Set<Component> hidden = NO_COMPONENTS;

        /*
        Tool dragTool = canvas.getDragTool();
        if (dragTool == null) {
            hidden = NO_COMPONENTS;
        } else {
            hidden = dragTool.getHiddenComponents(canvas);
            if (hidden == null) hidden = NO_COMPONENTS;
        }

         */
/*
        // draw halo around component whose attributes we are viewing
        boolean showHalo = AppPreferences.ATTRIBUTE_HALO.getBoolean();
        if (showHalo && haloedComponent != null && haloedCircuit == circ
                && !hidden.contains(haloedComponent)) {
            GraphicsUtil.switchToWidth(g, 3);
            g.setColor(com.cburch.logisim.gui.main.Canvas.HALO_COLOR);
            Bounds bds = haloedComponent.getBounds(g).expand(5);
            int w = bds.getWidth();
            int h = bds.getHeight();
            double a = com.cburch.logisim.gui.main.Canvas.SQRT_2 * w;
            double b = com.cburch.logisim.gui.main.Canvas.SQRT_2 * h;
            cvcontext.fillOval().drawOval((int) Math.round(bds.getX() + w/2.0 - a/2.0),
                    (int) Math.round(bds.getY() + h/2.0 - b/2.0),
                    (int) Math.round(a), (int) Math.round(b));
            GraphicsUtil.switchToWidth(g, 1);
            g.setColor(java.awt.Color.BLACK);
        }


 */
        // draw circuit and selection
        CircuitState circState = proj.getCircuitState();
        boolean printerView = AppPreferences.PRINTER_VIEW.getBoolean();
        context = new ComponentDrawContext(circ, circState, g, printerView);
        context.setHighlightedWires(highlightedWires);
        circ.draw(context, hidden);
        //sel.draw(context, hidden);
/*
        // draw tool
        Tool tool = dragTool != null ? dragTool : proj.getTool();
        if (tool != null && !canvas.isPopupMenuUp()) {
            Graphics gCopy = g.create();
            context.setGraphics(gCopy);
            tool.draw(canvas, context);
            gCopy.dispose();
        }
 */

    }

    private void drawWidthIncompatibilityData() {

        Set<WidthIncompatibilityData> exceptions;
        exceptions = proj.getCurrentCircuit().getWidthIncompatibilityData();
        if (exceptions == null || exceptions.size() == 0) return;

        g.setColor(Value.WIDTH_ERROR_COLOR);

        g.setLineWidth(2);

        FontMetrics fm = g.getFontMetrics();
        for (WidthIncompatibilityData ex : exceptions) {
            for (int i = 0; i < ex.size(); i++) {
                Location p = ex.getPoint(i);
                BitWidth w = ex.getBitWidth(i);

                // ensure it hasn't already been drawn
                boolean drawn = false;
                for (int j = 0; j < i; j++) {
                    if (ex.getPoint(j).equals(p)) { drawn = true; break; }
                }
                if (drawn) continue;

                // compute the caption combining all similar points
                String caption = "" + w.getWidth();
                for (int j = i + 1; j < ex.size(); j++) {
                    if (ex.getPoint(j).equals(p)) { caption += "/" + ex.getBitWidth(j); break; }
                }
                g.c.strokeOval(p.getX() - 4, p.getY() - 4, 8, 8);
                g.c.strokeText(caption, p.getX() + 5, p.getY() + 2 + fm.getAscent());
            }
        }

        g.toDefault();

    }



    //Tools

    // convert screen coordinates to grid coordinates by inverting circuit transform
    private int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    private int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
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
    public static void snapToGrid(MouseEvent e) {
        int old_x = e.getX();
        int old_y = e.getY();
        int new_x = snapXToGrid(old_x);
        int new_y = snapYToGrid(old_y);
        e.translatePoint(new_x - old_x, new_y - old_y);
    }

    private void setCanvasEvents(){

        cv.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            System.out.println("Point " + inverseTransformX(event.getX()) + " " + inverseTransformY(event.getY()));
            System.out.println("0 Point " + inverseTransformX(0) + " " + inverseTransformY(0));

        });

        cv.setOnMouseDragged(event -> {

            double dx = event.getX()-dragScreenX;
            double dy = event.getY()-dragScreenY;
            if (dx == 0 && dy == 0) {return;}

            clearRect40K(transform[4],transform[5]);

            transform[4] += dx;
            transform[5] += dy;

            dragScreenX = event.getX();
            dragScreenY = event.getY();

        });

        cv.setOnScroll(event -> {


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

    }



    //Canvas trail cleaner

    private void clearRect40K() {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(0,0,(cv.getWidth()/transform[0])*2,(cv.getHeight()/transform[0])*2);

    }

    private void clearRect40K(double prevX, double prevY) {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(-prevX/transform[0],-prevY/transform[0],cv.getWidth()/transform[0],cv.getHeight()/transform[0]);

    }

    public Canvas getCanvas(){return cv;}

    public Image getPrintImage(){

        //return cv.snapshot(0,new WritableImage());
        return null;
        // return cv.snapshot();
    }

}

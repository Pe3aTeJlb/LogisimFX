package com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.WidthIncompatibilityData;
import com.cburch.LogisimFX.circuit.WireSet;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.data.BitWidth;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;
import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.MenuTool;
import com.cburch.LogisimFX.tools.Tool;

import com.sun.javafx.tk.FontMetrics;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.StringBinding;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Collections;
import java.util.Set;

public class LayoutCanvas extends Canvas {

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


    // don't bother to update the size if it hasn't changed more than this
    static final double SQRT_2 = Math.sqrt(2.0);

    //Error string
    private static final Color DEFAULT_ERROR_COLOR = Color.color(0.753, 0, 0);
    private static final Font DEFAULT_ERROR_FONT = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 14);
    private Color errorColor;
    private static StringBinding errorMessage;

    //Tick rate
    private static final Color TICK_RATE_COLOR = Color.color(0, 0, 0.361, 0.361);
    private static final Font TICK_RATE_FONT = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 12);
    private TickCounter tickCounter;

    static final Color HALO_COLOR = Color.color(0.753, 1, 1);
    private Component haloedComponent = null;
    private Circuit haloedCircuit = null;
    private WireSet highlightedWires = WireSet.EMPTY;

    private static final Set<Component> NO_COMPONENTS = Collections.emptySet();
    private ComponentDrawContext context, ptContext;

    private Tool dragTool;
    private Selection selection;

    private ContextMenu contextMenu;

    private LayoutEditHandler layoutEditHandler;

    public LayoutCanvas(AnchorPane rt, Project project){

        super(rt.getWidth(),rt.getHeight());

        root = rt;

        proj = project;

        this.setFocusTraversable(true);

        g = new Graphics(this.getGraphicsContext2D());
        g.toDefault();

        setCanvasEvents();

        layoutEditHandler = new LayoutEditHandler(this);

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        this.selection = new Selection(proj, this);
        this.tickCounter = new TickCounter();

        proj.getSimulator().addSimulatorListener(tickCounter);

        update = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };

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

        //System.out.println("layout draw");

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

        ptContext = new ComponentDrawContext(circ, circState, g);
        ptContext.setHighlightedWires(highlightedWires);

        g.setColor(Color.RED);

        circState.drawOscillatingPoints(ptContext);

        g.setColor(Color.BLUE);

        proj.getSimulator().drawStepPoints(ptContext);

        StringBinding message = errorMessage;
        if (message != null) {
            g.setColor(errorColor);
            drawString(g, message.get());
            return;
        }

        if (proj.getSimulator().isOscillating()) {
            g.setColor(DEFAULT_ERROR_COLOR);
            drawString(g, LC_gui.getInstance().get("canvasOscillationError"));
            return;
        }

        if (proj.getSimulator().isExceptionEncountered()) {
            g.setColor(DEFAULT_ERROR_COLOR);
            drawString(g, LC_gui.getInstance().get("canvasExceptionError"));
            return;
        }

        drawErrorMessage();

        drawTickRate();

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

    private void drawWithUserState() {

        Circuit circ = proj.getCurrentCircuit();
        Set<Component> hidden = NO_COMPONENTS;

        if (dragTool == null) {
            hidden = NO_COMPONENTS;
        } else {
            hidden = dragTool.getHiddenComponents(this);
            if (hidden == null) hidden = NO_COMPONENTS;
        }

        // draw halo around component whose attributes we are viewing
        boolean showHalo = AppPreferences.ATTRIBUTE_HALO.getBoolean();
        if (showHalo && haloedComponent != null && haloedCircuit == circ
                && !hidden.contains(haloedComponent)) {

            g.setLineWidth(3);
            g.setColor(HALO_COLOR);
            Bounds bds = haloedComponent.getBounds(g).expand(5);
            int w = bds.getWidth();
            int h = bds.getHeight();
            double a = SQRT_2 * w;
            double b = SQRT_2 * h;
            g.c.strokeOval((int) Math.round(bds.getX() + w/2.0 - a/2.0),
                    (int) Math.round(bds.getY() + h/2.0 - b/2.0),
                    (int) Math.round(a), (int) Math.round(b));

            g.toDefault();

        }

        // draw circuit and selection
        CircuitState circState = proj.getCircuitState();
        boolean printerView = AppPreferences.PRINTER_VIEW.getBoolean();
        context = new ComponentDrawContext(circ, circState, g, printerView);
        context.setHighlightedWires(highlightedWires);
        circ.draw(context, hidden);
        selection.draw(context, hidden);


        // draw tool
       Tool tool = dragTool != null ? dragTool : proj.getTool();
        //if (tool != null && !canvas.isPopupMenuUp()) {
        if (tool != null) {
            tool.draw(this, context);
            g.toDefault();
        }

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

    private void drawErrorMessage(){

        if(errorMessage != null) {
            g.setFont(DEFAULT_ERROR_FONT);
            g.setColor(errorColor);
            g.c.fillText(errorMessage.getValue(), inverseTransformX(this.getWidth() / 2),
                   inverseTransformY(3 * this.getHeight() / 4));

            g.toDefault();
        }

    }

    private void drawString(Graphics g, String msg){

        g.setFont(DEFAULT_ERROR_FONT);
        FontMetrics fm = g.getFontMetrics();
        int x = inverseTransformX((getWidth() - fm.computeStringWidth(msg)) / 2);
        if (x < 0) x = 0;
        g.c.fillText(msg, x, inverseTransformY(getHeight() - 23));

    }

    private void drawTickRate(){

        if (AppPreferences.SHOW_TICK_RATE.getBoolean()) {

            String hz = tickCounter.getTickRate();

            if (hz != null && !hz.equals("")) {
                g.setColor(TICK_RATE_COLOR);
                g.setFont(TICK_RATE_FONT);
                FontMetrics fm = g.getFontMetrics();
                int x = inverseTransformX(getWidth() - fm.computeStringWidth(hz) - 5);
                int y = inverseTransformY(fm.getAscent() + 5);
                g.c.fillText(hz, x, y);
            }

            g.toDefault();

        }

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

    private void setCanvasEvents(){

        this.addEventFilter(MouseEvent.ANY, (e) -> this.requestFocus());
        this.addEventFilter(KeyEvent.ANY, (e) -> {this.requestFocus();});

        this.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            //new CME(event).print();

            if( ((!(dragTool instanceof MenuTool) && event.getButton() != MouseButton.SECONDARY) ||
                    ((dragTool instanceof MenuTool) && event.getButton() == MouseButton.PRIMARY)) &&
                    contextMenu != null &&
                    contextMenu.isShowing() &&
                    !event.getTarget().equals(contextMenu)){
                contextMenu.hide();
            }

            if(event.getButton() == MouseButton.PRIMARY) {
                dragTool = proj.getTool();
                if (dragTool != null) {
                    dragTool.mousePressed(this, getGraphics(), new CME(event));
                    proj.getSimulator().requestPropagate();
                }
            }

            if(event.getButton() == MouseButton.SECONDARY){
                dragTool = new MenuTool();
                dragTool.mousePressed(this, getGraphics(), new CME(event));
            }

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

            if (dragTool != null) {
                dragTool.mouseDragged(this, getGraphics(), new CME(event));
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
            System.out.println("Zoom " + zoom);

            transform[4] = width / 2 - cx * newScale;
            transform[5] = height / 2 - cy * newScale;

        });

        this.setOnMouseMoved(event -> {

            Tool tool = proj.getTool();
            if (tool != null) {
                tool.mouseMoved(this, g, new CME(event));
            }

        });

        this.setOnMouseReleased(event -> {

            if (dragTool != null) {
                dragTool.mouseReleased(this, getGraphics(), new CME(event));
                dragTool = null;
            }

            Tool tool = proj.getTool();
            if (tool != null) {
                tool.mouseMoved(this, getGraphics(), new CME(event));
            }

        });

        this.setOnMouseEntered(event -> {

            this.requestFocus();

            if (dragTool != null) {
                dragTool.mouseEntered(this, getGraphics(), new CME(event));
            } else {
                Tool tool = proj.getTool();
                if (tool != null) {
                    tool.mouseEntered(this, getGraphics(), new CME(event));
                }
            }

        });

        this.setOnMouseExited(event -> {

            if (dragTool != null) {
                dragTool.mouseExited(this, getGraphics(), new CME(event));
            } else {
                Tool tool = proj.getTool();
                if (tool != null) {
                    tool.mouseExited(this, getGraphics(), new CME(event));
                }
            }

        });

        this.setOnKeyPressed(event -> {

            Tool tool = proj.getTool();
            if (tool != null) tool.keyPressed(this, event);

            //to avoid focus traversable that binded on arrow keys in javafx
            if(event.getCode().isArrowKey())event.consume();

        });

        this.setOnKeyReleased(event -> {

            Tool tool = proj.getTool();
            if (tool != null) tool.keyReleased(this, event);

            event.consume();

        });

        this.setOnKeyTyped(event -> {

            Tool tool = proj.getTool();
            if (tool != null) tool.keyTyped(this, event);

            event.consume();

        });

    }

    public void showContextMenu(ContextMenu menu, double x, double y){

        if(contextMenu != null)contextMenu.hide();

        contextMenu = menu;
        contextMenu.show(this,x,y);

    }



    //Canvas trail cleaner

    private void clearRect40K() {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(0,0,(this.getWidth()/transform[0])*2,(this.getHeight()/transform[0])*2);

    }

    private void clearRect40K(double prevX, double prevY) {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(-prevX/transform[0],-prevY/transform[0],this.getWidth()/transform[0],
                this.getHeight()/transform[0]);

    }



    public void setErrorMessage(StringBinding msg, Color color) {

        if (errorMessage != msg) {
            errorMessage = msg;
            errorColor = color == null ? DEFAULT_ERROR_COLOR : color;
        }

    }

    public static void clearErrorMessage(){
        errorMessage = null;
    }


    public void setHighlightedWires(WireSet value) {
        highlightedWires = value == null ? WireSet.EMPTY : value;
    }

    public void setHaloedComponent(Circuit circ, Component comp) {
        if (comp == haloedComponent) return;
        haloedCircuit = circ;
        haloedComponent = comp;
    }

    public Selection getSelection() {
        return selection;
    }

    public Circuit getCircuit(){
        return proj.getCurrentCircuit();
    }

    public CircuitState getCircuitState(){
        return proj.getCircuitState();
    }

    public Project getProject(){
        return proj;
    }

    public Graphics getGraphics(){
        return g;
    }

    public Canvas getCanvas(){return this;}

    public LayoutEditHandler getEditHandler(){
        return layoutEditHandler;
    }



    //ReadLike CanvasMouseEvent
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

        }

        public void print(){

            System.out.println("Global " + globalX + " " + globalY);
            System.out.println("Local " + localX + " " + localY);
            System.out.println("Snapped " + snappedX + " " + snappedY);

        }

    }

}

/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas;

import LogisimFX.OldFontmetrics;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.comp.ComponentUserEvent;
import LogisimFX.data.*;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.*;

import com.sun.javafx.tk.FontMetrics;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.*;

public class LayoutCanvas extends Canvas {

    private AnchorPane root;

    private LayoutEditor layoutEditor;
    private Project proj;

    //public Canvas;
    private Graphics g;
    private double width, height;

    private static final double MIN_ZOOM = 5;
    private static final double MAX_ZOOM = 0.5;
    private double zoom = 1;
    private double dragScreenX, dragScreenY;
    private double[] transform;

    public SimpleStringProperty mouseXProperty;
    public SimpleStringProperty mouseYProperty;
    public SimpleStringProperty zoomProperty;


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
    private static final Font DEBUG_FONT = Font.font("serif", FontWeight.LIGHT, FontPosture.REGULAR, 6);
    private Color errorColor;
    private StringBinding errorMessage;


    //Tick rate
    private static final Color TICK_RATE_COLOR = Color.color(0, 0, 0.361, 0.361);
    private static final Font TICK_RATE_FONT = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 12);
    private TickCounter tickCounter;


    //Haloed components
    public static final Color HALO_COLOR = Color.color(0, 1, 1);
    private ArrayList<Component> highlightedComponents = new ArrayList<>();
    private WireSet highlightedWires = WireSet.EMPTY;

    private static final Set<Component> NO_COMPONENTS = Collections.emptySet();
    private ComponentDrawContext context, ptContext;

    private Tool dragTool;
    private Selection selection;
    private boolean mouseOver = false;

    //Objects of canvas mouse events
    private ContextMenu contextMenu;

    private Tooltip tooltip = new Tooltip();
    private PauseTransition pauseTransition;

    private Circuit circ;
    private CircuitState circState;

    private MyProjectListener myProjectListener = new MyProjectListener();

    private class MyProjectListener
            implements ProjectListener, LibraryListener, CircuitListener{

        public void projectChanged(ProjectEvent event) {

            int act = event.getAction();

            if(act == ProjectEvent.ACTION_SET_CURRENT){
/*
                circ = proj.getCurrentCircuit();
                circState = proj.getCircuitState();
                ptContext = new ComponentDrawContext(circ, circState, g);

                setErrorMessage(null, null);
*/
            }
/*
            if (act != ProjectEvent.ACTION_SELECTION
                    && act != ProjectEvent.ACTION_START
                    && act != ProjectEvent.UNDO_START) {
                proj.getSimulator().requestPropagate();
            }
*/
            if (act == ProjectEvent.ACTION_SET_TOOL) {

                Tool t = event.getTool();
                if (t == null)  setCursor(Cursor.DEFAULT);
                else            setCursor(t.getCursor());

                setErrorMessage(null, null);

            }

        }

        public void libraryChanged(LibraryEvent event) {
            if (event.getAction() == LibraryEvent.REMOVE_TOOL) {
                Object t = event.getData();
                Circuit circ = null;
                if (t instanceof AddTool) {
                    t = ((AddTool) t).getFactory();
                    if (t instanceof SubcircuitFactory) {
                        circ = ((SubcircuitFactory) t).getSubcircuit();
                    }
                }

                if (proj.getTool() == event.getData()) {
                    Tool next = findTool(proj.getLogisimFile().getOptions()
                            .getToolbarData().getContents());
                    if (next == null) {
                        for (Library lib : proj.getLogisimFile().getLibraries()) {
                            next = findTool(lib.getTools());
                            if (next != null) break;
                        }
                    }
                    proj.setTool(next);
                }

            }
        }

        public void circuitChanged(CircuitEvent event) {
            int act = event.getAction();
            if (act == CircuitEvent.ACTION_ADD ||
                    act == CircuitEvent.ACTION_REMOVE ||
                    act == CircuitEvent.ACTION_CHANGE ||
                    act == CircuitEvent.ACTION_INVALIDATE ||
                    act == CircuitEvent.TRANSACTION_DONE) {
                proj.getSimulator().requestPropagate();
            }
        }

        private Tool findTool(List<? extends Tool> opts) {
           Tool ret = null;
            for (Tool o : opts) {
                if (ret == null && o != null) ret = o;
                else if (o instanceof EditTool) ret = o;
            }
            return ret;
        }

    }

    /* framerate debug*/
    /*
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;

     */
//6
    private int frameCap = 6;
    private int dragCap = 2;
    private boolean requestUpdate = true;

    private  double dx, dy;

    public LayoutCanvas(AnchorPane rt, LayoutEditor layoutEditor){

        //Super & set cache options & focus traversable
        super(rt.getWidth(),rt.getHeight());
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        this.setFocusTraversable(true);

        root = rt;

        this.layoutEditor = layoutEditor;
        proj = layoutEditor.getProj();

        //set Listeners
        proj.addProjectListener(myProjectListener);
        proj.addLibraryListener(myProjectListener);
        proj.addCircuitListener(myProjectListener);

        //init Graphics shell
        g = new Graphics(this.getGraphicsContext2D());
        g.toDefault();

        setCanvasEvents();

        //set init transforms
        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        this.selection = new Selection(proj, this);
        this.tickCounter = new TickCounter();

        proj.getSimulator().addSimulatorListener(tickCounter);

        pauseTransition = new PauseTransition(Duration.millis(750));

        this.circ = layoutEditor.getCirc();
        circState = proj.getCircuitState(circ);
        ptContext = new ComponentDrawContext(circ, circState, g);

        mouseXProperty = new SimpleStringProperty("0");
        mouseYProperty = new SimpleStringProperty("0");
        zoomProperty = new SimpleStringProperty("100");

        update = new AnimationTimer() {

            int frames = 0;

            @Override
            public void handle(long now) {

                if(requestUpdate){
                    Update();
                    requestUpdate = false;
                }

                if(frames % frameCap == 0) {

                    Update();
/*
                    long oldFrameTime = frameTimes[frameTimeIndex] ;
                    frameTimes[frameTimeIndex] = now ;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                    if (frameTimeIndex == 0) {
                        arrayFilled = true ;
                    }
                    if (arrayFilled) {
                        long elapsedNanos = now - oldFrameTime ;
                        long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                        double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                        System.out.println(String.format("Current frame rate: %.3f", frameRate));
                    }

 */

                }

                frames++;
                if(frames == 60) frames = 0;

            }

        };

        Update();

    }

    public void updateStop(){
        update.stop();
    }

    public void updateResume(){
        update.start();
    }

    //Unity Hie!
    private void Update(){

        updateCanvasSize();

        clearRect40K(transform[4], transform[5]);

       // transform[4] += dx;
       // transform[5] += dy;

        //transform[0] = zoom;
       // transform[3] = zoom;

        //dx = 0;
        //dy = 0;

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        if (AppPreferences.SHOW_GRID.get()) {
            drawGrid();
        }

        circState = proj.getCircuitState(circ);
        proj.getSimulator().setCircuitState(circState);

        drawWithUserState();

        drawWidthIncompatibilityData();

        ptContext = new ComponentDrawContext(circ, circState, g);
        ptContext.setHighlightedWires(highlightedWires);

        g.setColor(Color.RED);
        circState.drawOscillatingPoints(ptContext);

        g.setColor(Color.BLUE);
        proj.getSimulator().drawStepPoints(ptContext);

        //StringBinding message = errorMessage;
        if (errorMessage != null) {
            g.setColor(errorColor);
            drawString(g, errorMessage.get());
            return;
        }

        if (proj.getSimulator().isOscillating()) {
            g.setColor(DEFAULT_ERROR_COLOR);
            drawString(g, LC.get("canvasOscillationError"));
            return;
        }

        if (proj.getSimulator().isExceptionEncountered()) {
            g.setColor(DEFAULT_ERROR_COLOR);
            drawString(g, LC.get("canvasExceptionError"));
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

        if(AppPreferences.SHOW_GRID.get()) {

            for (int x = inverseSnapXToGrid(0);
                 x < inverseSnapXToGrid((int) this.getWidth()); x += SPACING_X) {

                for (int y = inverseSnapYToGrid(0);
                     y < inverseSnapYToGrid((int) this.getHeight()); y += SPACING_Y) {

                    if (zoom < 0.8f && (float) x % 50 == 0 && (float) y % 50 == 0) {
                        g.setColor(GRID_DOT_QUARTER);
                        g.c.fillRect(x, y, 2, 2);
                    } else {
                        g.setColor(GRID_DOT);
                        g.c.fillRect(x, y, 1, 1);
                    }

                }

            }

        }

    }

    private void drawWithUserState() {

        Set<Component> hidden;

        if (dragTool == null) {
            hidden = NO_COMPONENTS;
        } else {
            hidden = dragTool.getHiddenComponents(this);
            if (hidden == null) hidden = NO_COMPONENTS;
        }

        // draw halo around component which we are looking for
        if (highlightedComponents != null && !highlightedComponents.isEmpty()) {

            for(Component comp: highlightedComponents) {

                if (hidden.contains(comp)) continue;

                g.setLineWidth(3);
                g.setColor(HALO_COLOR);
                Bounds bds = comp.getBounds(g).expand(5);
                int w = bds.getWidth();
                int h = bds.getHeight();
                double a = SQRT_2 * w;
                double b = SQRT_2 * h;
                g.c.strokeOval((int) Math.round(bds.getX() + w / 2.0 - a / 2.0),
                        (int) Math.round(bds.getY() + h / 2.0 - b / 2.0),
                        (int) Math.round(a), (int) Math.round(b));

                g.toDefault();

            }

        }

        // draw circuit and selection
        boolean printerView = AppPreferences.PRINTER_VIEW.getBoolean();
        context = new ComponentDrawContext(circ, circState, g, printerView);
        context.setHighlightedWires(highlightedWires);
        circ.draw(context, hidden, inverseTransformX(0),inverseTransformY(0),
                inverseTransformX(this.getWidth()),inverseTransformY(this.getHeight()));

        selection.draw(context, hidden);

        // draw tool
        Tool tool = dragTool != null ? dragTool : proj.getTool();
        if (tool != null && mouseOver) {
            tool.draw(this, context);
            g.toDefault();
        }

    }

    private void drawWidthIncompatibilityData() {

        Set<WidthIncompatibilityData> exceptions;
        exceptions = circ.getWidthIncompatibilityData();
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
            Font f = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 14/zoom);
            g.setFont(f);
            g.setColor(errorColor);
            g.c.fillText(errorMessage.getValue(), inverseTransformX(this.getWidth() / 2),
                   inverseTransformY(3 * this.getHeight() / 4));

            g.toDefault();
        }

    }

    private void drawString(Graphics g, String msg){

        Font f = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 14/zoom);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int x = inverseTransformX((getWidth() - OldFontmetrics.computeStringWidth(fm,msg)) / 2);
        if (x < 0) x = 0;
        g.c.fillText(msg, x, inverseTransformY(getHeight() - 23));

    }

    private void drawTickRate(){

        if (AppPreferences.SHOW_TICK_RATE.getBoolean()) {

            String hz = tickCounter.getTickRate();

            if (hz != null && !hz.equals("")) {
                Font f = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 12/zoom);
                g.setColor(TICK_RATE_COLOR);
                g.setFont(f);
                FontMetrics fm = g.getFontMetrics();
                int x = inverseTransformX(getWidth() - OldFontmetrics.computeStringWidth(fm,hz) - 5);
                int y = inverseTransformY(fm.getAscent() + 5);
                g.c.fillText(hz, x, y);
            }

            g.toDefault();

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

        x = (int) ((x-transform[4])/transform[0]);

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

    private void setCanvasEvents(){

        //this.addEventFilter(MouseEvent.ANY, (e) -> this.requestFocus());
        //this.addEventFilter(KeyEvent.ANY, (e) -> this.requestFocus());

        this.setOnMousePressed(event -> {

            this.requestFocus();

            pauseTransition.stop();
            if(tooltip != null && tooltip.isShowing())tooltip.hide();

            dragScreenX = event.getX();
            dragScreenY = event.getY();

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
                    //proj.getSimulator().requestPropagate();
                }
            }

            if(event.getButton() == MouseButton.SECONDARY) {
                dragTool = new MenuTool();
                dragTool.mousePressed(this, getGraphics(), new CME(event));
                setHighlightedComponent(null);
                setHighlightedWires(WireSet.EMPTY);
            }

            //Accidentally press on canvas happens before press on tab content, so copy it for tab content
            //Event.fireEvent(layoutEditor, event.copyFor(event.getSource(), layoutEditor));

        });

        this.setOnMouseDragged(event -> {

            if (dragTool != null) {
                dragTool.mouseDragged(this, getGraphics(), new CME(event));
            }

            if (event.getButton() == MouseButton.MIDDLE ||
                    (event.getButton() == MouseButton.PRIMARY && dragTool instanceof PokeTool && !((PokeTool)dragTool).hasPokedComponent())) {

                dx = (event.getX() - dragScreenX);
                dy = (event.getY() - dragScreenY);

                dragScreenX = event.getX();
                dragScreenY = event.getY();

                moveCanvasArea(dx, dy);

            }

        });

        this.setOnScroll(event -> {

            if (contextMenu != null && contextMenu.isShowing()) contextMenu.hide();
            pauseTransition.stop();
            if (tooltip != null && tooltip.isShowing()) tooltip.hide();

            clearRect40K(transform[4], transform[5]);

            double newScale;
            double oldScale = transform[0];
            double val = event.getDeltaY() * .005;

            newScale = Math.max(oldScale + val, MAX_ZOOM);
            newScale = Math.min(newScale, MIN_ZOOM);

            int cx = inverseTransformX(width / 2);
            int cy = inverseTransformY(height / 2);

            transform[0] = newScale;
            transform[3] = newScale;

            // adjust translation to keep center of screen constant
            // inverse transform = (x-t4)/t0

            zoom = newScale;
            zoomProperty.set(Double.toString(Math.round(zoom*100)));

            dx = width / 2 - cx * newScale;
            dy = height / 2 - cy * newScale;

            transform[4] = width / 2 - cx * newScale;
            transform[5] = height / 2 - cy * newScale;

            if (transform[4] > 0) transform[4] = 0;
            if (transform[5] > 0) transform[5] = 0;

            requestUpdate = true;

        });

        this.setOnMouseMoved(event -> {

            this.requestFocus();

            mouseXProperty.set(Integer.toString(inverseTransformX(event.getX())));
            mouseYProperty.set(Integer.toString(inverseTransformY(event.getY())));

            mouseOver = true;

            Tool tool = proj.getTool();
            if (tool != null) {
                tool.mouseMoved(this, g, new CME(event));
            }

            pauseTransition.stop();
            if(tooltip != null && tooltip.isShowing())tooltip.hide();

            computeToolTipe(event);

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

            //proj.getSimulator().requestPropagate();

        });

        this.setOnMouseEntered(event -> {

            mouseOver = true;

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

            mouseXProperty.set("0");
            mouseYProperty.set("0");

            mouseOver = false;

            pauseTransition.stop();
            if(tooltip != null && tooltip.isShowing())tooltip.hide();

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

            this.requestFocus();

            //Event.fireEvent(layoutEditor, event.copyFor(event.getSource(), layoutEditor));

            Tool tool = proj.getTool();
            if (tool != null) tool.keyPressed(this, event);

            //to avoid focus traversable that binded on arrow keys in javafx
            if(event.getCode().isArrowKey())event.consume();

            if(event.getCode() == KeyCode.Y){
                AppPreferences.SHOW_GRID.set(!AppPreferences.SHOW_GRID.get());
            }

            if(event.getCode() == KeyCode.J){
                frameCap++;
                if(frameCap == 16) frameCap = 1;
                System.out.println(frameCap);
            }

        });

        this.setOnKeyReleased(event -> {

            Tool tool = proj.getTool();
            if (tool != null) tool.keyReleased(this, event);

        });

        this.setOnKeyTyped(event -> {

            Tool tool = proj.getTool();
            if (tool != null) tool.keyTyped(this, event);

        });

    }



    public void zoomIn(){
        zoom(40);
    }

    public void zoomOut(){
        zoom(-40);
    }

    private void zoom(double delta){

        clearRect40K(transform[4], transform[5]);

        double newScale;
        double oldScale = transform[0];
        double val = delta * .005;

        newScale = Math.max(oldScale + val, MAX_ZOOM);
        newScale = Math.min(newScale, MIN_ZOOM);

        int cx = inverseTransformX(width / 2);
        int cy = inverseTransformY(height / 2);

        transform[0] = newScale;
        transform[3] = newScale;

        // adjust translation to keep center of screen constant
        // inverse transform = (x-t4)/t0

        zoom = newScale;
        zoomProperty.set(Double.toString(Math.round(zoom*100)));

        dx = width / 2 - cx * newScale;
        dy = height / 2 - cy * newScale;

        transform[4] = width / 2 - cx * newScale;
        transform[5] = height / 2 - cy * newScale;

        if (transform[4] > 0) transform[4] = 0;
        if (transform[5] > 0) transform[5] = 0;

        requestUpdate = true;

    }

    public void toDefaultZoom(){

        clearRect40K(transform[4], transform[5]);

        transform[0] = transform[3] = 1;
        zoomProperty.set(Double.toString(100));
        requestUpdate = true;

    }

    public void showContextMenu(ContextMenu menu, double x, double y){

        if(contextMenu != null)contextMenu.hide();

        contextMenu = menu;
        contextMenu.show(this,x,y);

    }

    private void computeToolTipe(MouseEvent event){

        if (AppPreferences.COMPONENT_TIPS.getBoolean()) {
            CME cme = new CME(event);
            Location loc = Location.create(cme.snappedX, cme.snappedY);
            ComponentUserEvent e;
            for (Component comp : getCircuit().getAllContaining(loc)) {
                Object makerObj = comp.getFeature(ToolTipMaker.class);
                if (makerObj instanceof ToolTipMaker) {
                    ToolTipMaker maker = (ToolTipMaker) makerObj;
                    e = new ComponentUserEvent(this, loc.getX(), loc.getY(), cme);
                    if(maker.getToolTip(e) != null) {
                        tooltip.setText(maker.getToolTip(e).getValue());
                        pauseTransition.setOnFinished(ev -> tooltip.show(this, event.getScreenX()+5, event.getScreenY()+5));
                        pauseTransition.play();
                    }
                }
            }
        }

    }

    private void moveCanvasArea(double dx, double dy){

        if (dx == 0 && dy == 0) {
            return;
        }

        if (transform[4] + dx > 0) {
            dx = 0;
            transform[4] = 0;
        }

        if (transform[5] + dy > 0) {
            dy = 0;
            transform[5] = 0;
        }

        clearRect40K(transform[4], transform[5]);

        transform[4] += dx;
        transform[5] += dy;

        requestUpdate = true;

    }

    //Canvas trail cleaner

    private void clearRect40K() {

        g.setColor(BACKGROUND);
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

    public void clearErrorMessage(){
        errorMessage = null;
    }


    public void setHighlightedWires(WireSet value) {
        highlightedWires = value == null ? WireSet.EMPTY : value;
    }

    public void setHighlightedComponent(Component comp) {
        highlightedComponents.clear();
        if (comp != null) {
            highlightedComponents.add(comp);
        }
    }

    public void setHighlightedComponents(Collection<Component> comps) {
        highlightedComponents.clear();
        highlightedComponents.addAll(comps);
    }

    public void focusOnComp(Component comp){

        setHighlightedComponent(comp);

        if (comp == null) {
            return;
        }

        double targetX = Math.max(0, comp.getLocation().getX() - (width / 2 / transform[0]));
        double targetY = Math.max(0, comp.getLocation().getY() - (height / 2 / transform[0]));

        double ddx = (inverseTransformX(0) - targetX) * transform[0];
        double ddy = (inverseTransformY(0) - targetY) * transform[0];

        moveCanvasArea(
                ddx,
                ddy
        );

    }


    public void setCircState(CircuitState circState){
        this.circState = circState;
    }


    public Selection getSelection() {
        return selection;
    }

    public Circuit getCircuit(){
        return circ;
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



    //ReadLike CanvasMouseEvent
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

        }

        public void print(){

            System.out.println("Global " + globalX + " " + globalY);
            System.out.println("Local " + localX + " " + localY);
            System.out.println("Snapped " + snappedX + " " + snappedY);

        }

    }


    public void terminateCanvas(){

        update.stop();

        proj.removeProjectListener(myProjectListener);
        proj.removeLibraryListener(myProjectListener);
        proj.removeCircuitListener(myProjectListener);

        proj.getSimulator().removeSimulatorListener(tickCounter);


    }

}

package com.cburch.LogisimFX.newgui.MainFrame;

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
import com.cburch.LogisimFX.file.MouseMappings;
import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.proj.Project;


import com.cburch.LogisimFX.tools.Tool;
import com.sun.javafx.tk.FontMetrics;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Collections;
import java.util.Set;

public class CustomCanvas extends Canvas {

    private AnchorPane root;

    private Project proj;

    public Canvas cv;
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

    //Hertz rate
    static final Color HALO_COLOR = Color.color(0.753, 1, 1);

    private static final int BOUNDS_BUFFER = 70;
    // pixels shown in canvas beyond outermost boundaries
    private static final int THRESH_SIZE_UPDATE = 10;
    // don't bother to update the size if it hasn't changed more than this
    static final double SQRT_2 = Math.sqrt(2.0);
    //private static final int BUTTONS_MASK = InputEvent.BUTTON1_DOWN_MASK
    //       | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
    private static final Color DEFAULT_ERROR_COLOR = Color.color(0.753, 0, 0);

    private static final Color TICK_RATE_COLOR = Color.color(0, 0, 0.361, 0.361);
    private static final Font TICK_RATE_FONT = Font.font("serif", FontWeight.BOLD, FontPosture.REGULAR, 12);



    private Component haloedComponent = null;
    private Circuit haloedCircuit = null;
    private WireSet highlightedWires = WireSet.EMPTY;

    private static final Set<Component> NO_COMPONENTS = Collections.emptySet();
    private ComponentDrawContext context, ptContext;

    private Tool dragTool;
    private Selection selection;
    private MouseMappings mappings;


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

        this.selection = new Selection(proj, this);

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

        ptContext = new ComponentDrawContext(circ, circState, g);
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

            for (int x = snapXToGrid(inverseTransformX(0));
                 x < snapXToGrid(inverseTransformX(cv.getWidth())); x += SPACING_X) {

                for (int y = snapYToGrid(inverseTransformY(0));
                     y < snapYToGrid(inverseTransformY(cv.getHeight())); y += SPACING_Y) {

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

    private void setCanvasEvents(){

        cv.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            dragTool = proj.getTool();
            if (dragTool != null) {
                dragTool.mousePressed(this, getGraphics(), event);
                proj.getSimulator().requestPropagate();
            }

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

        cv.setOnMouseMoved(event -> {

            //Tool tool = getToolFor(e);
            //if (tool != null) {
            //    tool.mouseMoved(this, g, event);
            //}

        });

        cv.setOnMouseEntered(event -> {

            if (dragTool != null) {
                dragTool.mouseEntered(this, getGraphics(), event);
            } else {
                Tool tool = proj.getTool();
                if (tool != null) {
                    tool.mouseEntered(this, getGraphics(), event);
                }
            }

        });

        cv.setOnMouseExited(event -> {

            if (dragTool != null) {
                dragTool.mouseExited(this, getGraphics(), event);
            } else {
                Tool tool = proj.getTool();
                if (tool != null) {
                    tool.mouseExited(this, getGraphics(), event);
                }
            }

        });

        /*
        		//
		// MouseListener methods
		//
		public void mouseClicked(MouseEvent e) { }

		public void mouseMoved(MouseEvent e) {
			if ((e.getModifiersEx() & BUTTONS_MASK) != 0) {
				// If the control key is down while the mouse is being
				// dragged, mouseMoved is called instead. This may well be
				// an issue specific to the MacOS Java implementation,
				// but it exists there in the 1.4 and 5.0 versions.
				mouseDragged(e);
				return;
			}

			Tool tool = getToolFor(e);
			if (tool != null) {
				tool.mouseMoved(Canvas.this, getGraphics(), e);
			}
		}

		public void mouseDragged(MouseEvent e) {
			if (drag_tool != null) {
				drag_tool.mouseDragged(Canvas.this, getGraphics(), e);
			}
		}

		public void mouseEntered(MouseEvent e) {
			if (drag_tool != null) {
				drag_tool.mouseEntered(Canvas.this, getGraphics(), e);
			} else {
				Tool tool = getToolFor(e);
				if (tool != null) {
					tool.mouseEntered(Canvas.this, getGraphics(), e);
				}
			}
		}

		public void mouseExited(MouseEvent e) {
			if (drag_tool != null) {
				drag_tool.mouseExited(Canvas.this, getGraphics(), e);
			} else {
				Tool tool = getToolFor(e);
				if (tool != null) {
					tool.mouseExited(Canvas.this, getGraphics(), e);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			viewport.setErrorMessage(null, null);
			proj.setStartupScreen(false);
			Canvas.this.requestFocus();
			drag_tool = getToolFor(e);
			if (drag_tool != null) {
				drag_tool.mousePressed(Canvas.this, getGraphics(), e);
			}

			completeAction();
		}

		public void mouseReleased(MouseEvent e) {
			if (drag_tool != null) {
				drag_tool.mouseReleased(Canvas.this, getGraphics(), e);
				drag_tool = null;
			}

			Tool tool = proj.getTool();
			if (tool != null) {
				tool.mouseMoved(Canvas.this, getGraphics(), e);
			}

			completeAction();
		}

		private Tool getToolFor(MouseEvent e) {
			if (menu_on) return null;

			Tool ret = mappings.getToolFor(e);
			if (ret == null) return proj.getTool();
			else return ret;
		}
         */

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


    public void setHighlightedWires(WireSet value) {
        highlightedWires = value == null ? WireSet.EMPTY : value;
    }

    public void setHaloedComponent(Circuit circ, Component comp) {
        if (comp == haloedComponent) return;
        exposeHaloedComponent();
        haloedCircuit = circ;
        haloedComponent = comp;
        exposeHaloedComponent();
    }

    private void exposeHaloedComponent() {
        Component c = haloedComponent;
        if (c == null) return;
        Bounds bds = c.getBounds(g).expand(7);
        int w = bds.getWidth();
        int h = bds.getHeight();
        double a = SQRT_2 * w;
        double b = SQRT_2 * h;
      //  canvas.repaint((int) Math.round(bds.getX() + w/2.0 - a/2.0),
       //         (int) Math.round(bds.getY() + h/2.0 - b/2.0),
         //       (int) Math.round(a), (int) Math.round(b));
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

    public Canvas getCanvas(){return cv;}

    public Image getPrintImage(){

        //return cv.snapshot(0,new WritableImage());
        return null;
        // return cv.snapshot();
    }

}

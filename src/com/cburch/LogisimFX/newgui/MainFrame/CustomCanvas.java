package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.awt.event.MouseEvent;

public class CustomCanvas extends Canvas {

    private AnchorPane root;

    public Canvas cv;
    private GraphicsContext cvcontext;
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

    public CustomCanvas(AnchorPane rt){

        root = rt;

        cv = new Canvas(root.getWidth(),root.getHeight());
        cvcontext = cv.getGraphicsContext2D();

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

        cvcontext.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        drawBackground();

        draw();

    }

    public void updateCanvasSize(){

        width = root.getWidth();
        height = root.getHeight();

        cv.setWidth(width);
        cv.setHeight(height);

    }

    private void drawBackground(){

        cvcontext.setFill(Color.RED);
        cvcontext.setStroke(Color.RED);
        cvcontext.fillOval(0,0,10,10);

        drawGrid();

    }

    private void drawGrid(){

            for (int x = snapXToGrid(inverseTransformX(0)); x < snapXToGrid(inverseTransformX(cv.getWidth())); x += SPACING_X) {

                for (int y = snapYToGrid(inverseTransformY(0)); y < snapYToGrid(inverseTransformY(cv.getHeight())); y += SPACING_Y) {

                    if(zoom < 0.8f && (float)x % 50 == 0 && (float)y % 50 == 0){
                        cvcontext.setFill(GRID_DOT_QUARTER);
                        cvcontext.fillRect(x,y,2,2);
                    }else{
                        cvcontext.setFill(GRID_DOT);
                        cvcontext.fillRect(x,y,1,1);
                    }

                }

            }


    }

    public void draw(){


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

        cvcontext.setFill(BACKGROUND);
        cvcontext.fillRect(0,0,(cv.getWidth()/transform[0])*2,(cv.getHeight()/transform[0])*2);
    }

    private void clearRect40K(double prevX, double prevY) {
        cvcontext.setFill(BACKGROUND);
        cvcontext.fillRect(-prevX/transform[0],-prevY/transform[0],cv.getWidth()/transform[0],cv.getHeight()/transform[0]);
    }

    public Canvas getCanvas(){return cv;}

}

package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class CustomCanvas extends Canvas {

    private AnchorPane root;

    public Canvas cv;
    private GraphicsContext cvcontext;
    private double width, height;

    private double dragScreenX, dragScreenY;
    private double[] transform;


    //Grid
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color GRID_DOT = Color.gray(0.18,1);

    private static final double SPACING_X = 10;
    private static final double SPACING_Y = 10;

    private AnimationTimer update;

    public CustomCanvas(AnchorPane rt){

        root = rt;

        cv = new Canvas(root.getWidth(),root.getHeight());
        cvcontext = cv.getGraphicsContext2D();

        root.getChildren().add(cv);

        setCanvasEvents();

        //AnchorPane.setLeftAnchor(cv,0.0);
        //AnchorPane.setRightAnchor(cv,0.0);
        //AnchorPane.setTopAnchor(cv,0.0);
        //AnchorPane.setBottomAnchor(cv,0.0);

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

            cvcontext.setFill(GRID_DOT);

            for (int x = 0; x < cv.getWidth(); x += SPACING_X) {

                for (int y = 0; y < cv.getHeight(); y += SPACING_Y) {

                    //double offsetY = (y%(2*SPACING_Y)) == 0 ? SPACING_X /2 : 0;
                    //cvcontext.fillOval(x-RADIUS,y-RADIUS,RADIUS+RADIUS,RADIUS+RADIUS);
                    cvcontext.fillRect(x,y,1,1);

                }

            }


    }

    public void draw(){


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


    //Tools

    // convert screen coordinates to grid coordinates by inverting circuit transform
    private int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    private int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
    }

    private void setCanvasEvents(){

        cv.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            System.out.println("Point " + event.getX() + " " + event.getY());

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

            clearRect40K();

            double newScale;
            double oldScale = transform[0];
            double val = event.getDeltaY()*.005;

            newScale = Math.max(oldScale+val, .2);
            newScale = Math.min(newScale, 2.5);

            int cx = inverseTransformX(width / 2);
            int cy = inverseTransformY(height / 2);

            transform[0] = newScale;
            transform[3] = newScale;

            // adjust translation to keep center of screen constant
            // inverse transform = (x-t4)/t0

            transform[4] = width / 2 - cx * newScale;
            transform[5] = height / 2 - cy * newScale;


        });

    }

}

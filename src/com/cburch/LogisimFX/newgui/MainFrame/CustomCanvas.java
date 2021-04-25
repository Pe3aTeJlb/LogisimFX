package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CustomCanvas extends Canvas {

    private Pane root;

    public Canvas cv;
    private GraphicsContext cvcontext;
    private double width, height;

    private double dragScreenX, dragScreenY;
    private double[] transform;
    private Color background = Color.BLACK;

    private AnimationTimer update;

    public CustomCanvas(Pane rt){

        root = rt;

        cv = new Canvas(root.getWidth(),root.getHeight());
        cvcontext = cv.getGraphicsContext2D();

        root.getChildren().add(cv);

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        cv.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

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

        draw();

    }

    public void draw(){

        updateCanvasSize();

        clearRect40K(transform[4], transform[5]);

        cvcontext.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        cvcontext.setFill(Color.RED);
        cvcontext.setStroke(Color.RED);
        cvcontext.fillOval(0,0,10,10);
        //cvcontext.f

    }

    public void updateCanvasSize(){

        width = root.getWidth();
        height = root.getHeight();

        cv.setWidth(width);
        cv.setHeight(height);

    }



    private void clearRect40K() {

        cvcontext.setFill(background);
        cvcontext.fillRect(0,0,(cv.getWidth()/transform[0])*2,(cv.getHeight()/transform[0])*2);
    }

    private void clearRect40K(double prevX, double prevY) {
        cvcontext.setFill(background);
        cvcontext.fillRect(-prevX/transform[0],-prevY/transform[0],cv.getWidth()/transform[0],cv.getHeight()/transform[0]);
    }


    // convert screen coordinates to grid coordinates by inverting circuit transform
    private int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    private int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
    }

}

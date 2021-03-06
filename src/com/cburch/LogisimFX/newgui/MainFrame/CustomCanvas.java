package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CustomCanvas extends Canvas {

    private Pane root;
    public Canvas cv;
    private GraphicsContext cvcontext;
    private double[] transform;
    private Color background = Color.BLACK;

    public CustomCanvas(Pane rt){

        root = rt;

        cv = new Canvas(root.getWidth(),root.getHeight());
        cvcontext = cv.getGraphicsContext2D();

        root.getChildren().add(cv);

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;
    }

    public void draw(){

        updateCanvasSize();
        clearRect40K(transform[4], transform[5]);
        cvcontext.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );
    }

    public void updateCanvasSize(){
        cv.setWidth(root.getWidth());
        cv.setHeight(root.getHeight());
    }

    private void clearRect40K()
    {

        cvcontext.setFill(background);
        cvcontext.fillRect(0,0,(cv.getWidth()/transform[0])*2,(cv.getHeight()/transform[0])*2);
    }

    private void clearRect40K(double prevX, double prevY)
    {
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

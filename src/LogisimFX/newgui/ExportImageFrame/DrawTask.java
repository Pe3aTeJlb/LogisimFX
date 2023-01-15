/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.ExportImageFrame;

import LogisimFX.circuit.Circuit;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.proj.Project;

import javafx.concurrent.Task;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Transform;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class DrawTask extends Task<ImageView> {

    private Project proj;

    //public Canvas;
    private Graphics g;

    private static double[] transform;

    //Grid
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color GRID_DOT = Color.gray(0.18,1);
    private static final Color GRID_DOT_QUARTER = Color.gray(0.10,1);

    private static final double SPACING_X = 10;
    private static final double SPACING_Y = 10;

    private static final Font HEADER_FONT = Font.font("monospace", FontWeight.NORMAL, FontPosture.REGULAR, 14);

    private static final int BORDER_SIZE = 50;

    //Correction data
    private double scale;
    private double pixelScale = 1;

    public ImageView img;

    private Canvas canvas;
    private Circuit circuit;
    private boolean printerView;

    private CountDownLatch latch;

    public DrawTask(double sizeX, double sizeY, Project project, CountDownLatch latch){

        canvas = new Canvas(sizeX,sizeY);

        System.out.println("scree "+ sizeX+""+sizeY);

        proj = project;
        this.latch = latch;

        g = new Graphics(canvas.getGraphicsContext2D());

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

    }


    public DrawTask(double sizeX, double sizeY, Project project, Circuit circuit, boolean printerView){

        canvas = new Canvas(sizeX,sizeY);

        proj = project;
        this.circuit = circuit;
        this.printerView = printerView;

        g = new Graphics(canvas.getGraphicsContext2D());

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

    }

    public void UpdateDrawTask(Circuit circuit, boolean printerView, CountDownLatch latch){
        this.circuit = circuit;
        this.printerView = printerView;
        this.latch = latch;
    }

    public void UpdateLatch(CountDownLatch latch){
        this.latch = latch;
    }

    //Unity Hie!
    @Override
    public ImageView call() {

        Circuit circ = circuit;
        proj.getSimulator().setCircuitState(proj.getCircuitState(circ));

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        clearRect40K();

        Bounds bds = circ.getBounds(g).expand(BORDER_SIZE);

        canvas.setWidth(bds.getWidth());
        canvas.setHeight(bds.getHeight());

        if(canvas.getWidth() < 200 || canvas.getHeight() < 200){
            canvas.setWidth(200);
            canvas.setHeight(200);
        }

        // And finally draw the circuit onto the page
        ComponentDrawContext context = new ComponentDrawContext(circ, proj.getCircuitState(circ), g, printerView);
        Collection<Component> noComps = Collections.emptySet();
        circ.draw(context, noComps);

        WritableImage writableImage = new WritableImage((int)(pixelScale * canvas.getWidth()),
                (int)(pixelScale * canvas.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(1, 1));

        img = new ImageView(canvas.snapshot(spa, writableImage));

        this.latch.countDown();

        return img;

    }


    private void drawGrid(){

        for (int x = inverseSnapXToGrid(0);
             x < inverseSnapXToGrid((int)canvas.getWidth()); x += SPACING_X) {

            for (int y = inverseSnapYToGrid(0);
                 y < inverseSnapYToGrid((int)canvas.getHeight()); y += SPACING_Y) {

                if(scale < 0.8f && (float)x % 50 == 0 && (float)y % 50 == 0){
                    g.c.setFill(GRID_DOT_QUARTER);
                    g.c.fillRect(x,y,2,2);
                }else{
                    g.c.setFill(GRID_DOT);
                    g.c.fillRect(x,y,1,1);
                }

            }

        }


    }

    private int inverseTransformX(double x) {
        return (int) ((x-transform[4])/transform[0]);
    }

    private int inverseTransformY(double y) {
        return (int) ((y-transform[5])/transform[3]);
    }

    private int inverseSnapXToGrid(int x) {

        x = (int) ((x-transform[4])/transform[0]);;

        if (x < 0) {
            return -((-x + 5) / 10) * 10;
        } else {
            return ((x + 5) / 10) * 10;
        }

    }

    private int inverseSnapYToGrid(int y) {

        y = (int) ((y-transform[5])/transform[3]);

        if (y < 0) {
            return -((-y + 5) / 10) * 10;
        } else {
            return ((y + 5) / 10) * 10;
        }

    }

    private void setScale(double scale){

        transform[0] = scale;
        transform[3] = scale;

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

    }

    //Canvas trail cleaner

    private void clearRect40K() {

        g.c.setFill(BACKGROUND);
        g.c.fillRect(0,0,(canvas.getWidth()/transform[0])*2,(canvas.getHeight()/transform[0])*2);

    }

}

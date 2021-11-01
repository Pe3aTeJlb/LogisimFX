package LogisimFX.newgui.PrintFrame;

import LogisimFX.OldFontmetrics;
import LogisimFX.circuit.Circuit;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.proj.Project;

import com.sun.javafx.tk.FontMetrics;
import javafx.print.PageLayout;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

import java.util.Collection;
import java.util.Collections;

public class PrintCanvas extends Canvas{

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

    private static final int BORDER_SIZE = 5;

    //Correction data
    private double headHeight;
    private double imWidth;
    private double imHeight;
    private double scale;
    private double pixelScale = 1;
    private double rotation;

    public PrintCanvas(double sizeX, double sizeY, Project project){

        super(sizeX,sizeY);

        System.out.println("scree "+ sizeX+""+sizeY);

        proj = project;

        g = new Graphics(this.getGraphicsContext2D());

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

    }

    //Unity Hie!
    public ImageView draw(PageLayout layout, Circuit circuit, String header, boolean printerView, boolean rotateToFit){

        Circuit circ = circuit;

        boolean rotated = false;

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        clearRect40K();

        FontMetrics fm = g.getFontmetricsForFont(HEADER_FONT);
        headHeight = (header == null ? 0 : fm.getAscent()+fm.getDescent()+20);

        // Compute image size
        imWidth = layout.getPrintableWidth();
        imHeight = layout.getPrintableHeight();

        Bounds bds = circ.getBounds(g).expand(8);
        scale = Math.min(imWidth / bds.getWidth(), (imHeight - headHeight) / bds.getHeight());
        System.out.println("scale "+scale);
        if (rotateToFit && scale < 1.0 / 1.1) {
            double scale2 = Math.min(imHeight / bds.getWidth(),
                    (imWidth - headHeight) / bds.getHeight());
            System.out.println("scale2 "+scale2);
            if (scale2 >= scale * 1.1) { // will rotate
                scale = scale2;
                rotated = true;
                if (imHeight > imWidth) { // portrait -> landscape
                    rotation = -90;
                } else { // landscape -> portrait
                    rotation = 90;
                }
            }
        }

        double pow = Math.pow(10, 3);
        scale = Math.ceil(scale * pow) / pow;
        if(scale < 0.05) scale = 0.05;
        System.out.println("final scale "+scale);

        if(header != null){

            g.setColor(Color.BLACK);
            g.setFont(HEADER_FONT);

            FontMetrics fm2 = g.getFontMetrics();

            g.c.strokeText(header,Math.round((imWidth - OldFontmetrics.computeStringWidth(fm2,header)) / 2), fm2.getAscent());

            g.c.translate(0, headHeight);

            g.toDefaultColor();
            g.toDefaultFont();

        }


        // And finally draw the circuit onto the page
        ComponentDrawContext context = new ComponentDrawContext(circ, proj.getCircuitState(circ), g, printerView);
        Collection<Component> noComps = Collections.emptySet();
        circ.draw(context, noComps);

        WritableImage writableImage = new WritableImage((int)(pixelScale*this.getWidth()),
                (int)(pixelScale*this.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(1, 1));

        ImageView img = new ImageView(this.snapshot(spa, writableImage));

        if(scale<1) {

            // Creating the Scale transformation
            Scale s = new Scale();

            // Setting the scaliing factor.
            s.setX(scale);
            s.setY(scale);

            // Setting Orgin of new coordinate system
            s.setPivotX(img.getX());
            s.setPivotY(img.getY());

            img.getTransforms().addAll(s);

        }

        if(rotated){

            // Creating the rotation transformation
            Rotate rotate = new Rotate();

            // Setting the angle for the rotation
            rotate.setAngle(rotation);

            // Setting pivot points for the rotation
            rotate.setPivotX(bds.getWidth()/2);
            rotate.setPivotY(3.1*bds.getHeight()/4);

            System.out.println(bds.getWidth() + " " + bds.getHeight());

            // Adding the transformation to img
            img.getTransforms().addAll(rotate);


        }

        //img.setEffect(new DropShadow(20, Color.BLACK));

        return img;

    }

    public ImageView getImage(Circuit circuit, boolean printerView){

        Circuit circ = circuit;

        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;

        g.c.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        clearRect40K();

        Bounds bds = circ.getBounds(g).expand(BORDER_SIZE);
        scale = Math.min(imHeight / bds.getWidth(), (imWidth - headHeight) / bds.getHeight());
        System.out.println("scale "+scale);

        double pow = Math.pow(10, 3);
        scale = Math.ceil(scale * pow) / pow;
        if(scale < 0.05) scale = 0.05;
        System.out.println("final scale "+scale);


        // And finally draw the circuit onto the page
        ComponentDrawContext context = new ComponentDrawContext(circ, proj.getCircuitState(circ), g, printerView);
        Collection<Component> noComps = Collections.emptySet();
        circ.draw(context, noComps);

        WritableImage writableImage = new WritableImage((int)(pixelScale*this.getWidth()),
                (int)(pixelScale*this.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(1, 1));

        ImageView img = new ImageView(this.snapshot(spa, writableImage));

        if(scale<1) {

            // Creating the Scale transformation
            Scale s = new Scale();

            // Setting the scaliing factor.
            s.setX(scale);
            s.setY(scale);

            // Setting Orgin of new coordinate system
            s.setPivotX(img.getX());
            s.setPivotY(img.getY());

            img.getTransforms().addAll(s);

        }

        return img;

    }


    private void drawGrid(){

        for (int x = inverseSnapXToGrid(0);
             x < inverseSnapXToGrid((int)this.getWidth()); x += SPACING_X) {

            for (int y = inverseSnapYToGrid(0);
                 y < inverseSnapYToGrid((int)this.getHeight()); y += SPACING_Y) {

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
        g.c.fillRect(0,0,(this.getWidth()/transform[0])*2,(this.getHeight()/transform[0])*2);

    }

}

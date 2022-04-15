/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.Canvas;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class Graphics{

    public GraphicsContext c;

    public static int THIN_FONT_SIZE = 1;
    public static int DEFAULT_FONT_SIZE = 1;
    public static int NORMAL_FONT_SIZE = 1;
    public static int THICK_FONT_SIZE = 1;

    public static double THIN_LINE_SIZE = 0.5;
    public static double DEFAULT_LINE_SIZE = 1;
    public static double NORMAL_LINE_SIZE = 2;
    public static double THICK_LINE_SIZE = 2.5;

    private StrokeLineCap DEFAULT_STROKELINECAP = StrokeLineCap.SQUARE;
    private StrokeLineJoin DEFAULT_STROKELINEJOIN = StrokeLineJoin.MITER;

    private final Font DEFAULT_FONT = Font.font("System", FontWeight.THIN, FontPosture.REGULAR, 12);
    private FontMetrics fm;

    private double Degrees = 0;
    private double xTranslate = 0;
    private double yTranslate = 0;

    private final Color DEFAULT_COLOR = Color.BLACK;

    public Graphics(GraphicsContext context){
        c = context;
        fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(c.getFont());
        toDefault();
    }



    public void setColor(Color color){

        if(c.getFill() == color) return;
        setColor((Paint)color);
/*
        c.setStroke(color);
        c.setFill(color);

 */

    }

    public void setColor(Paint color){

        if(c.getFill() == color) return;

        c.setStroke(color);
        c.setFill(color);

    }

    public Color getColor(){
        return (Color) c.getFill();
    }

    public Paint getPaint(){
        return c.getFill();
    }

    public void toDefaultColor(){

        if(c.getFill() == DEFAULT_COLOR) return;

        c.setStroke(DEFAULT_COLOR);
        c.setFill(DEFAULT_COLOR);

    }



    public void setLineExtras(StrokeLineCap lineCap){
        setLineExtras(lineCap, null);
    }

    public void setLineExtras(StrokeLineJoin lineJoin){
        setLineExtras(null, lineJoin);
    }

    public void setLineExtras(StrokeLineCap lineCap, StrokeLineJoin lineJoin){

        if(lineCap != null && lineCap != DEFAULT_STROKELINECAP) {
            c.setLineCap(lineCap);
        }

        if(lineJoin != null && lineJoin != DEFAULT_STROKELINEJOIN) {
            c.setLineJoin(lineJoin);
        }

    }

    public void toDefaultLineExtras(){

       if(c.getLineCap() != DEFAULT_STROKELINECAP){
           c.setLineCap(DEFAULT_STROKELINECAP);
       }

       if(c.getLineJoin() != DEFAULT_STROKELINEJOIN){
           c.setLineJoin(DEFAULT_STROKELINEJOIN);
       }

    }


    public void setLineWidth(double width){
        if(c.getLineWidth() == width) return;
        c.setLineWidth(width);
    }

    public double getLineWidth(){return c.getLineWidth();}

    public void toDefaultLineWidth(){
        if(c.getLineWidth() == DEFAULT_LINE_SIZE) return;
        c.setLineWidth(DEFAULT_LINE_SIZE);
    }



    public void setFont(Font font){

        if(c.getFont() == font) return;

        Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        c.setFont(font);

    }

    public Font getFont(){
        return c.getFont();
    }

    public void toDefaultFont(){
        if(c.getFont() == DEFAULT_FONT) return;
        c.setFont(DEFAULT_FONT);
    }

    public FontMetrics getFontMetrics(){
       return fm;
    }

    public FontMetrics getFontmetricsForFont(Font font){

        return Toolkit.getToolkit().getFontLoader().getFontMetrics(font);

    }



    public void rotate(double degrees){

        Degrees += degrees;
        c.rotate(degrees);

    }

    public void toDefaultRotation(){

        c.rotate(-Degrees);
        Degrees = 0;

    }



    public void translate(double x, double y){

        xTranslate += x;
        yTranslate += y;

        c.translate(x,y);

    }

    public void toDefaultCoords(){

        c.translate(-xTranslate, -yTranslate);
        xTranslate = 0;
        yTranslate = 0;

    }



    public void toDefault(){

        toDefaultLineWidth();
        toDefaultLineExtras();
        toDefaultFont();
        toDefaultColor();

        toDefaultRotation();
        toDefaultCoords();

    }

}

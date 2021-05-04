package com.cburch.LogisimFX.newgui.AboutFrame;

import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.Main;
import com.cburch.LogisimFX.data.Value;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;


public class About extends AbstractController {

    @FXML
    private AnchorPane root;

    @FXML
    private Canvas cv;
    private GraphicsContext g;
    private int width, height;

    private Stage stage;

    private Localizer lc = new Localizer("start");

    private AnimationTimer update;

    private final Color fadeColor = Color.rgb(255, 255, 255, 0);
    private final Color headerColor = Color.rgb(143,0,0,0);
    private final Color gateColor = Color.GREEN;
    private final Font headerFont = new Font("Monospaced",72);
    private final Font versionFont = new Font("Serif", 32);
    private final Font copyrightFont = new Font("Serif", 18);

    /*
    private final Font headerFont = new Font("Monospaced", Font.BOLD, 72);
    private final Font versionFont = new Font("Serif", Font.PLAIN | Font.ITALIC, 32);
    private final Font copyrightFont = new Font("Serif", Font.ITALIC, 18);
     */

    private Value upper = Value.FALSE;
    private Value lower = Value.TRUE;



    /** Time to spend freezing the credits before after after scrolling */
    private static final int MILLIS_FREEZE = 1000;

    /** Speed of how quickly the scrolling occurs */
    private static final int MILLIS_PER_PIXEL = 20;

    /** Path to Hendrix College's logo - if you want your own logo included,
     * please add it separately rather than replacing this. */
    private static final String HENDRIX_PATH = "Logisimfx/resources/hendrix.png";
    private static final int HENDRIX_WIDTH = 50;

    private static class CreditsLine {
        private int y;
        private int type;
        private String text;
        private Image img;
        private int imgWidth;

        public CreditsLine(int type, String text) {
            this(type, text, null, 0);
        }

        public CreditsLine(int type, String text, Image img, int imgWidth) {
            this.y = 0;
            this.type = type;
            this.text = text;
            this.img = img;
            this.imgWidth = imgWidth;
        }
    }

    private ArrayList<About.CreditsLine> lines = new ArrayList<>();

    private Color[] colorBase;
    private Paint[] paintSteady;
    private Font[] font;

    private int scroll;
    private long start;
    private float fadeStop;

    private int initialLines; // number of lines to show in initial freeze
    private int initialHeight; // computed in code based on above
    private int linesHeight; // computed in code based on above


    @FXML
    public void initialize(){

        start = System.currentTimeMillis();

        g = cv.getGraphicsContext2D();

        setCreditsLines();

        update = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };

        update.start();

    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;
        stage.setTitle("LogisimFX " + Main.VERSION_NAME);
        stage.setResizable(false);

    }

    private void setCreditsLines(){

        scroll = 0;

        int prefWidth = (int)root.getWidth();
        int prefHeight = (int)root.getHeight();

        fadeStop = (float) (prefHeight / 4.0);

        colorBase = new Color[] {
                Color.rgb(143,0,0),
                Color.rgb(48, 0, 96),
                Color.rgb(48, 0, 96),
        };
        font = new Font[] {
                new Font("Sans Serif", 20),
                new Font("Sans Serif", 24),
                new Font("Sans Serif", 18),
        };

        paintSteady = new Paint[colorBase.length];
        for (int i = 0; i < colorBase.length; i++) {
            Color hue = colorBase[i];
            Stop[] stops = { new Stop(0, derive(hue, 0)), new Stop(1, hue)};
            paintSteady[i] = new LinearGradient(0.0f, 0.0f,
                    0.0f, fadeStop, true,CycleMethod.NO_CYCLE, stops);
        }

        URL url = About.class.getClassLoader().getResource(HENDRIX_PATH);
        Image hendrixLogo = null;


        // Logisim's policy concerning who is given credit:
        // Past contributors are not acknowledged in the About dialog for the current
        // version, but they do appear in the acknowledgements section of the User's
        // Guide. Current contributors appear in both locations.

        linesHeight = 0; // computed in paintComponent
        lines.add(new About.CreditsLine(1, "www.cburch.com/logisim/"));
        lines.add(new About.CreditsLine(0, lc.get("creditsRoleLead"),
                hendrixLogo, HENDRIX_WIDTH));
        lines.add(new About.CreditsLine(1, "Carl Burch"));
        lines.add(new About.CreditsLine(2, "Hendrix College"));
        initialLines = lines.size();
        lines.add(new About.CreditsLine(0, lc.get("creditsRoleGerman")));
        lines.add(new About.CreditsLine(1, "Uwe Zimmerman"));
        lines.add(new About.CreditsLine(2, "Uppsala universitet"));
        lines.add(new About.CreditsLine(0, lc.get("creditsRoleGreek")));
        lines.add(new About.CreditsLine(1, "Thanos Kakarountas"));
        lines.add(new About.CreditsLine(2, "\u03A4.\u0395.\u0399 \u0399\u03BF\u03BD\u03AF\u03C9\u03BD \u039D\u03AE\u03C3\u03C9\u03BD"));
        lines.add(new About.CreditsLine(0, lc.get("creditsRolePortuguese")));
        lines.add(new About.CreditsLine(1, "Theldo Cruz Franqueira"));
        lines.add(new About.CreditsLine(2, "PUC Minas"));
        lines.add(new About.CreditsLine(0, lc.get("creditsRoleRussian")));
        lines.add(new About.CreditsLine(1, "Ilia Lilov"));
        lines.add(new About.CreditsLine(2, "\u041C\u043E\u0441\u043A\u043E\u0432\u0441\u043A\u0438\u0439 \u0433\u043E\u0441\u0443\u0434\u0430\u0440\u0441\u0442\u0432\u0435\u043D\u043D\u044B\u0439"));
        lines.add(new About.CreditsLine(2, "\u0443\u043D\u0438\u0432\u0435\u0440\u0441\u0438\u0442\u0435\u0442 \u043F\u0435\u0447\u0430\u0442\u0438"));
        lines.add(new About.CreditsLine(0, lc.get("creditsRoleTesting")));
        lines.add(new About.CreditsLine(1, "Ilia Lilov"));
        lines.add(new About.CreditsLine(2, "\u041C\u043E\u0441\u043A\u043E\u0432\u0441\u043A\u0438\u0439 \u0433\u043E\u0441\u0443\u0434\u0430\u0440\u0441\u0442\u0432\u0435\u043D\u043D\u044B\u0439"));
        lines.add(new About.CreditsLine(2, "\u0443\u043D\u0438\u0432\u0435\u0440\u0441\u0438\u0442\u0435\u0442 \u043F\u0435\u0447\u0430\u0442\u0438"));

        /* If you fork Logisim, feel free to change the above lines, but
         * please do not change these last four lines! */
        lines.add(new About.CreditsLine(0, lc.get("creditsRoleOriginal"),
                hendrixLogo, HENDRIX_WIDTH));
        lines.add(new About.CreditsLine(1, "Carl Burch"));
        lines.add(new About.CreditsLine(2, "Hendrix College"));
        lines.add(new About.CreditsLine(1, "www.cburch.com/logisim/"));

    }

    private Color derive(Color base, int alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
    }


    private void Update(){

        clearRect40K();

        updateCanvasSize();

        setScroll();
        updateCredits();

        g.setFill(gateColor);
        g.setStroke(gateColor);

        drawWires(0,0);
        drawNot(0, 0, 70, 10);
        drawNot(0, 0, 70, 110);
        drawAnd(0, 0, 130, 30);
        drawAnd(0, 0, 130, 90);
        drawOr(0, 0, 220, 60);

        drawVersion(5,5);

    }

    private void setScroll(){

        long elapse = System.currentTimeMillis() - start;
        scroll = (int)elapse;

    }

    private void updateCanvasSize(){

        width = (int)stage.getWidth();
        height = (int)(stage.getHeight());

        cv.setWidth(width);
        cv.setHeight(height);

    }

    private void updateCredits(){

        if (linesHeight == 0) {

            int y = 3*height/4;
            int index = -1;

            for (About.CreditsLine line : lines) {

                Font f = font[line.type];

                index++;
                if (index == initialLines) initialHeight = y;
                if (line.type == 0) y += 10;


                line.y = y + (int)f.getSize();
                y += (int)f.getSize();
            }
            linesHeight = y;

        }

        Paint[] paint = paintSteady;
        int yPos = 0;
        int initY = height-100;
        int maxY = height/4;
        int totalMillis = 2 * MILLIS_FREEZE + (linesHeight + height) * MILLIS_PER_PIXEL;
        int offs = scroll % totalMillis;

        if (offs >= 0 && offs < MILLIS_FREEZE) {

            // frozen before starting the credits scroll
            int a = 255 * (MILLIS_FREEZE - offs) / MILLIS_FREEZE;
            if (a > 245) {
                paint = null;
            } else if (a < 15) {
                paint = paintSteady;
            } else {
                paint = new Paint[colorBase.length];
                for (int i = 0; i < paint.length; i++) {
                    Color hue = colorBase[i];
                    Stop[] stops = { new Stop(150, derive(hue, 0)), new Stop(300, hue)};
                    paint[i] =new LinearGradient(0.0f, 0.0f,
                            0.0f, fadeStop, true,CycleMethod.NO_CYCLE, stops);
                }
            }
            yPos = initY;

        } else if (offs < MILLIS_FREEZE + maxY * MILLIS_PER_PIXEL) {
            // scrolling through credits
            yPos = initY + (offs - MILLIS_FREEZE) / MILLIS_PER_PIXEL;
        } else if (offs < 2 * MILLIS_FREEZE + maxY * MILLIS_PER_PIXEL) {
            // freezing at bottom of scroll
            yPos = initY + maxY;
        } else if (offs < 2 * MILLIS_FREEZE + (linesHeight - initY) * MILLIS_PER_PIXEL) {
            // scrolling bottom off screen
            yPos = initY + (offs - 2 * MILLIS_FREEZE) / MILLIS_PER_PIXEL;
        } else {
            // scrolling next credits onto screen
            int millis = offs - 2 * MILLIS_FREEZE - (linesHeight - initY) * MILLIS_PER_PIXEL;
            paint = null;
            yPos = height + millis / MILLIS_PER_PIXEL;
        }

        int centerX = (int)cv.getWidth() / 5;
        maxY = 3*height/4;

        for (About.CreditsLine line : lines) {

            int y = line.y - yPos;
            //if (y < -100 || y > maxY + 50) continue;

            int type = line.type;

            if (paint == null) {
                g.setStroke(colorBase[type]);
                g.setFill(colorBase[type]);
            } else {
                g.setStroke(paint[type]);
                g.setFill(paint[type]);
            }

            g.setFont(font[type]);
            //int textWidth = fms[type].stringWidth(line.text);
            int textWidth = 5;
            System.out.println(line.y);
            g.fillText(line.text, centerX, line.y - yPos);

            Image img = line.img;
            if (img != null) {
                int x = width - line.imgWidth;
                int top = y - (int)font[type].getSize();
                g.drawImage(img, x,top);
            }

        }

    }

    private void drawWires(int x0, int y0) {

        Value upperNot = upper.not();
        Value lowerNot = lower.not();
        Value upperAnd = upperNot.and(lower);
        Value lowerAnd = lowerNot.and(upper);
        Value out = upperAnd.or(lowerAnd);
        int x;
        int y;

        //g.setStroke(Paint.valueOf(upper.toString()));
        //g.setFill();
        g.setStroke(Color.BLUE);
        x = toX(x0, 20);
        y = toY(y0, 10);
        g.fillOval(x - 7, y - 7, 14, 14);
        g.strokeLine(toX(x0, 0), y, toX(x0, 40), y);
        g.strokeLine(x, y, x, toY(y0, 70));
        y = toY(y0, 70);
        g.strokeLine(x, y, toX(x0, 80), y);

        //g.setColor(upperNot.getColor());
        y = toY(y0, 10);
        g.strokeLine((toX(x0, 70)), y, toX(x0, 80), y);

       // g.setColor(lower.getColor());
        x = toX(x0, 30);
        y = toY(y0, 110);
        g.fillOval(x - 7, y - 7, 14, 14);
        g.strokeLine((toX(x0, 0)), y, toX(x0, 40), y);
        g.strokeLine(x, y, x, toY(y0, 50));
        y = toY(y0, 50);
        g.strokeLine(x, y, toX(x0, 80), y);

        //g.setColor(lowerNot.getColor());
        y = toY(y0, 110);
        g.strokeLine((toX(x0, 70)), y, toX(x0, 80), y);

        //g.setColor((upperAnd.getColor());
        x = toX(x0, 150);
        y = toY(y0, 30);
        g.strokeLine((toX(x0, 130)), y, x, y);
        g.strokeLine(x, y, x, toY(y0, 45));
        y = toY(y0, 45);
        g.strokeLine(x, y, toX(x0, 174), y);

        //g.setColor(lowerAnd.getColor());
        y = toY(y0, 90);
        g.strokeLine((toX(x0, 130)), y, x, y);
        g.strokeLine(x, y, x, toY(y0, 75));
        y = toY(y0, 75);
        g.strokeLine(x, y, toX(x0, 174), y);

        //g.setColor(out.getColor());
        y = toY(y0, 60);
        g.strokeLine(toX(x0, 220), y, toX(x0, 240), y);

    }

    private void drawNot(int x0, int y0, int x, int y) {

        double[] xp = new double[4];
        double[] yp = new double[4];

        xp[0] = toX(x0, x - 10); yp[0] = toY(y0, y);
        xp[1] = toX(x0, x - 29); yp[1] = toY(y0, y - 7);
        xp[2] = xp[1]; yp[2] = toY(y0, y + 7);
        xp[3] = xp[0]; yp[3] = yp[0];

        g.strokePolyline(xp,yp,4);

        int diam = toDim(10);
        g.strokeOval(xp[0], yp[0] - diam / 2, diam, diam);

    }

    private void drawAnd(int x0, int y0, int x, int y) {

        double[] xp = new double[4];
        double[] yp = new double[4];

        xp[0] = toX(x0, x - 25); yp[0] = toY(y0, y - 25);
        xp[1] = toX(x0, x - 50); yp[1] = yp[0];
        xp[2] = xp[1]; yp[2] = toY(y0, y + 25);
        xp[3] = xp[0]; yp[3] = yp[2];

        int diam = toDim(50);
        g.strokeArc(xp[1], yp[1], diam, diam, -90, 180, ArcType.OPEN);
        g.strokePolyline(xp, yp, 4);

    }

    private void drawOr(int x0, int y0, int x, int y) {

        int cd = toDim(62);
        int cx = toX(x0, x - 50)-cd;

        g.strokeArc(cx, toY(y0, y - 37)-cd, 2*cd, 2*cd, -90, 53, ArcType.OPEN);
        g.strokeArc(cx, toY(y0, y + 37)-cd, 2*cd, 2*cd, 90, -53, ArcType.OPEN);
        g.strokeArc(toX(x0, x - 93)-toDim(50), toY(y0, y)-toDim(50), 2*toDim(50), 2*toDim(50), -30, 60, ArcType.OPEN);

    }

    private void drawVersion(int x, int y) {

        String str;

        g.setStroke(Color.RED);
        g.setFill(Color.RED);

        g.setFont(headerFont);
        g.fillText("LogisimFX", x, y + 60);

        g.setFont(copyrightFont);
        str = "\u00a9 " + Main.COPYRIGHT_YEAR;
        g.fillText(str, x+370, y + 90);

        g.setFont(versionFont);
        str = "Version " + Main.VERSION_NAME;
        g.fillText(str, x+150, y + 90);
    }

    private int toX(int x0, int offs) {
        return x0 + offs * 3 / 2;
    }

    private int toY(int y0, int offs) {
        return y0 + offs * 3 / 2;
    }

    private int toDim(int offs) {
        return offs * 3 / 2;
    }


    private void clearRect40K()
    {

        g.setFill(Color.WHITE);
        g.fillRect(0,0,(cv.getWidth())*2,(cv.getHeight())*2);
    }

    @Override
    public void onClose() {

        update.stop();
        System.out.println("About closed");
    }

}

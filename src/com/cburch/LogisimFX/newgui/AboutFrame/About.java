package com.cburch.LogisimFX.newgui.AboutFrame;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.Main;
import com.cburch.LogisimFX.data.Value;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;


public class About extends AbstractController {

    @FXML
    private Canvas cv;
    private GraphicsContext g;
    private int width, height;

    private Stage stage;

    private AnimationTimer update;

    private final Color headerColor = Color.rgb(0,178,255,1);
    private final Color gateColor = Color.DARKGRAY;

    private final Font headerFont = Font.font("Monospaced",FontWeight.BOLD, FontPosture.ITALIC,72);
    private final Font versionFont = Font.font("Serif", FontWeight.NORMAL, FontPosture.ITALIC, 32);
    private final Font copyrightFont = Font.font("Serif", FontWeight.NORMAL, FontPosture.ITALIC, 18);

    private Value upper = Value.FALSE;
    private Value lower = Value.TRUE;

    private final int CircOffsetX = 20;
    private final int CircOffsetY = 40;


    /** Time to spend freezing the credits before after after scrolling */
    private static final int MILLIS_FREEZE = 1000;

    /** Speed of how quickly the scrolling occurs */
    private static final int MILLIS_PER_PIXEL = 20;

    /** Path to Hendrix College's logo - if you want your own logo included,
     * please add it separately rather than replacing this. */
    private static final String HENDRIX_PATH = "hendrix.png";
    private static final int HENDRIX_WIDTH = 80;

    /** Path to Pplos Stuido logo  */
    private static final String PPLOS_PATH = "pplosstudio.png";
    private static final int PPLOS_WIDTH = 80;

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
    private Font[] font;

    private int scroll;
    private long start;

    private int initialLines; // number of lines to show in initial freeze
    private int initialHeight; // computed in code based on above
    private int linesHeight; // computed in code based on above


    @FXML
    public void initialize(){

        g = cv.getGraphicsContext2D();

    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;
        stage.setTitle("LogisimFX " + Main.VERSION_NAME);
        stage.setResizable(false);

        updateCanvasSize();

        setCreditsLines();

        update = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };

        start = System.currentTimeMillis();
        update.start();

    }

    private void setCreditsLines(){

        scroll = 0;

        colorBase = new Color[] {
                Color.rgb(143, 0, 0),
                Color.rgb(48, 0, 96),
                Color.rgb(48, 0, 96),
        };
        font = new Font[] {
                new Font("Sans Serif", 20),
                new Font("Sans Serif", 24),
                new Font("Sans Serif", 18),
        };

        Image hendrixLogo = IconsManager.getLogo(HENDRIX_PATH);
        Image pplosLogo = IconsManager.getLogo(PPLOS_PATH);

        // Logisim's policy concerning who is given credit:
        // Past contributors are not acknowledged in the About dialog for the current
        // version, but they do appear in the acknowledgements section of the User's
        // Guide. Current contributors appear in both locations.

        linesHeight = 0; // computed in paintComponent

        lines.add(new About.CreditsLine(1, "sites.google.com/view/pplosstudio"));
        lines.add(new About.CreditsLine(1, "Pplos Studio",pplosLogo, PPLOS_WIDTH));
        lines.add(new About.CreditsLine(1, " "));
        lines.add(new About.CreditsLine(1, " "));

        initialLines = lines.size();

        lines.add(new About.CreditsLine(0, LC.get("creditsRoleLead")));
        lines.add(new About.CreditsLine(1, "Pe3aTeJlb"));

        lines.add(new About.CreditsLine(0, LC.get("creditsRoleProgrammer")));
        lines.add(new About.CreditsLine(1, "TexHoMa|‾"));

        lines.add(new About.CreditsLine(0, LC.get("creditsRoleProgrammer")));
        lines.add(new About.CreditsLine(1, "Kolhozniy punk"));

        lines.add(new About.CreditsLine(1, ""));
        lines.add(new About.CreditsLine(1, ""));
        lines.add(new About.CreditsLine(1, ""));

        lines.add(new About.CreditsLine(0, LC.get("creditsRoleGerman")));
        lines.add(new About.CreditsLine(1, "Uwe Zimmerman"));
        lines.add(new About.CreditsLine(2, "Uppsala universitet"));
        lines.add(new About.CreditsLine(0, LC.get("creditsRoleGreek")));
        lines.add(new About.CreditsLine(1, "Thanos Kakarountas"));
        lines.add(new About.CreditsLine(2, "\u03A4.\u0395.\u0399 \u0399\u03BF\u03BD\u03AF\u03C9\u03BD \u039D\u03AE\u03C3\u03C9\u03BD"));
        lines.add(new About.CreditsLine(0, LC.get("creditsRolePortuguese")));
        lines.add(new About.CreditsLine(1, "Theldo Cruz Franqueira"));
        lines.add(new About.CreditsLine(2, "PUC Minas"));
        lines.add(new About.CreditsLine(0, LC.get("creditsRoleRussian")));
        lines.add(new About.CreditsLine(1, "Ilia Lilov"));
        lines.add(new About.CreditsLine(2, "\u041C\u043E\u0441\u043A\u043E\u0432\u0441\u043A\u0438\u0439 \u0433\u043E\u0441\u0443\u0434\u0430\u0440\u0441\u0442\u0432\u0435\u043D\u043D\u044B\u0439"));
        lines.add(new About.CreditsLine(2, "\u0443\u043D\u0438\u0432\u0435\u0440\u0441\u0438\u0442\u0435\u0442 \u043F\u0435\u0447\u0430\u0442\u0438"));
        lines.add(new About.CreditsLine(0, LC.get("creditsRoleTesting")));
        lines.add(new About.CreditsLine(1, "Ilia Lilov"));
        lines.add(new About.CreditsLine(2, "\u041C\u043E\u0441\u043A\u043E\u0432\u0441\u043A\u0438\u0439 \u0433\u043E\u0441\u0443\u0434\u0430\u0440\u0441\u0442\u0432\u0435\u043D\u043D\u044B\u0439"));
        lines.add(new About.CreditsLine(2, "\u0443\u043D\u0438\u0432\u0435\u0440\u0441\u0438\u0442\u0435\u0442 \u043F\u0435\u0447\u0430\u0442\u0438"));



        /* If you fork Logisim, feel free to change the above lines, but
         * please do not change these last four lines! */

        lines.add(new About.CreditsLine(0, LC.get("creditsRoleOriginal"),
                hendrixLogo, HENDRIX_WIDTH));
        lines.add(new About.CreditsLine(1, "Carl Burch"));
        lines.add(new About.CreditsLine(2, "Hendrix College"));
        lines.add(new About.CreditsLine(1, "www.cburch.com/logisim/"));

    }

    private Color derive(Color base, float alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
    }


    private void Update(){

        clearRect40K();

        updateCanvasSize();

        long elapse = System.currentTimeMillis() - start;
        int count = (int) (elapse / 500) % 4;
        scroll = (int)elapse;

        upper = (count == 2 || count == 3) ? Value.TRUE : Value.FALSE;
        lower = (count == 1 || count == 2) ? Value.TRUE : Value.FALSE;

        g.setLineWidth(4);

        drawWires(50, 100);

        g.setFill(gateColor);
        g.setStroke(gateColor);

        drawNot(CircOffsetX, CircOffsetY, 70+CircOffsetX, 10+CircOffsetY);
        drawNot(CircOffsetX, CircOffsetY, 70+CircOffsetX, 110+CircOffsetY);
        drawAnd(CircOffsetX, CircOffsetY, 130+CircOffsetX, 30+CircOffsetY);
        drawAnd(CircOffsetX, CircOffsetY, 130+CircOffsetX, 90+CircOffsetY);
        drawOr(CircOffsetX, CircOffsetY, 220+CircOffsetX, 60+CircOffsetY);

        updateCredits();

        drawVersion(5,5);
    }

    private void updateCanvasSize(){

        width = (int)stage.getWidth();
        height = (int)(stage.getHeight());

        cv.setWidth(width);
        cv.setHeight(height);

    }

    private void updateCredits(){

        FontMetrics[] fms = new FontMetrics[font.length];
        for (int i = 0; i < fms.length; i++) {
            fms[i] = Toolkit.getToolkit().getFontLoader().getFontMetrics(font[i]);
        }

        if (linesHeight == 0) {

            int y = 0;
            int index = -1;

            for (About.CreditsLine line : lines) {

                index++;

                if (index == initialLines) initialHeight = y;
                if (line.type == 0) y += 10;

                FontMetrics fm = fms[line.type];

                line.y = y + (int)fm.getAscent();
                y += (int)fm.getLineHeight();

            }
            linesHeight = y;
        }

        int yPos;
        int initY =  Math.min(0, initialHeight - height);
        int maxY = linesHeight - height - initY;
        int totalMillis = 2 * MILLIS_FREEZE + (linesHeight + height) * MILLIS_PER_PIXEL;
        int offs = scroll % totalMillis;

        if (offs >= 0 && offs < MILLIS_FREEZE) {
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
            yPos = -height + millis / MILLIS_PER_PIXEL;
        }

        int centerX = (int)cv.getWidth()/2;
        maxY = height/4;

        for (About.CreditsLine line : lines) {

            int y = line.y - yPos;
            if (y < -100 || y < maxY) continue;

            int type = line.type;
            float alpha = 0;

            if((line.y - yPos < height-100)) {

                float currPos = line.y - yPos;

                alpha = 1 - ((height-currPos-100)/(height-100-maxY));
                alpha = Math.max(0,alpha);
                alpha = Math.min(1,alpha);

                Color fadeout = derive(colorBase[type], alpha);
                g.setStroke(fadeout);
                g.setFill(fadeout);

            }else {
                g.setStroke(colorBase[type]);
                g.setFill(colorBase[type]);
            }

            g.setFont(font[type]);
            int textWidth = (int)fms[type].computeStringWidth(line.text);

            g.fillText(line.text, centerX-textWidth/2,  line.y - yPos);

            Image img = line.img;
            if (img != null) {

                Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
                LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

                int x = width - line.imgWidth;
                int top = y - (int) fms[type].getAscent();

                if((line.y - yPos < height-100)) {
                    g.setGlobalAlpha(alpha);
                }
                g.drawImage(img, x,top);
                g.setGlobalAlpha(1);

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

        g.setStroke(upper.getColor());
        g.setFill(upper.getColor());

        x = toX(x0, 20);
        y = toY(y0, 10);
        g.fillOval(x - 7, y - 7, 14, 14);
        g.strokeLine(toX(x0, 0), y, toX(x0, 40), y);
        g.strokeLine(x, y, x, toY(y0, 70));
        y = toY(y0, 70);
        g.strokeLine(x, y, toX(x0, 80), y);


        g.setStroke(upperNot.getColor());
        g.setFill(upperNot.getColor());

        y = toY(y0, 10);
        g.strokeLine((toX(x0, 70)), y, toX(x0, 80), y);

        g.setStroke(lower.getColor());
        g.setFill(lower.getColor());

        x = toX(x0, 30);
        y = toY(y0, 110);
        g.fillOval(x - 7, y - 7, 14, 14);
        g.strokeLine((toX(x0, 0)), y, toX(x0, 40), y);
        g.strokeLine(x, y, x, toY(y0, 50));
        y = toY(y0, 50);
        g.strokeLine(x, y, toX(x0, 80), y);

        g.setStroke(lowerNot.getColor());
        g.setFill(lowerNot.getColor());

        y = toY(y0, 110);
        g.strokeLine((toX(x0, 70)), y, toX(x0, 80), y);

        g.setStroke(upperAnd.getColor());
        g.setFill(upperAnd.getColor());

        x = toX(x0, 150);
        y = toY(y0, 30);
        g.strokeLine((toX(x0, 130)), y, x, y);
        g.strokeLine(x, y, x, toY(y0, 45));
        y = toY(y0, 45);
        g.strokeLine(x, y, toX(x0, 174), y);

        g.setStroke(lowerAnd.getColor());
        g.setFill(lowerAnd.getColor());

        y = toY(y0, 90);
        g.strokeLine((toX(x0, 130)), y, x, y);
        g.strokeLine(x, y, x, toY(y0, 75));
        y = toY(y0, 75);
        g.strokeLine(x, y, toX(x0, 174), y);

        g.setStroke(out.getColor());
        g.setFill(out.getColor());

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

        g.setStroke(headerColor);
        g.setFill(headerColor);

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

    private void clearRect40K() {

        g.setFill(Color.WHITE);
        g.fillRect(0,0,(cv.getWidth())*2,(cv.getHeight())*2);
    }

    @Override
    public void onClose() {
        update.stop();
        System.out.println("About closed");
    }

}

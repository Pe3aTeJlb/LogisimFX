package LogisimFX.newgui.LoadingFrame;

import LogisimFX.IconsManager;
import LogisimFX.Main;
import LogisimFX.OldFontmetrics;
import LogisimFX.data.Value;
import LogisimFX.newgui.AbstractController;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class LoadingScreen extends AbstractController {

    private static Stage stage;

    @FXML
    private Canvas cv;

    private GraphicsContext g;
    private int width, height;

    private AnimationTimer update;

    private final Color headerColor = Color.rgb(0,178,255,1);
    private final Color gateColor = Color.DARKGRAY;

    private final Font headerFont = Font.font("Monospaced", FontWeight.BOLD, FontPosture.ITALIC,72);
    private final Font versionFont = Font.font("Serif", FontWeight.NORMAL, FontPosture.ITALIC, 32);
    private final Font copyrightFont = Font.font("Serif", FontWeight.NORMAL, FontPosture.ITALIC, 18);

    private Value upper = Value.FALSE;
    private Value lower = Value.TRUE;

    private final int CircOffsetX = 20;
    private final int CircOffsetY = 40;

    /** Path to Pplos Stuido logo  */
    private static final String PPLOS_PATH = "pplosstudio.png";
    private static final int PPLOS_WIDTH = 150;

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

    private ArrayList<LoadingScreen.CreditsLine> lines = new ArrayList<>();

    private Color[] colorBase;
    private Font[] font;

    private long start;

    private int initialLines; // number of lines to show in initial freeze
    private int linesHeight; // computed in code based on above


    @FXML
    private ProgressBar ProgressBar;

    private static final SimpleDoubleProperty progress = new SimpleDoubleProperty();

    @FXML
    public void initialize(){

        g = cv.getGraphicsContext2D();

        progress.set(0f);
        ProgressBar.progressProperty().bind(progress);

    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        stage.setTitle("LogisimFX");

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

        Image pplosLogo = IconsManager.getLogo(PPLOS_PATH);

        linesHeight = 0; // computed in paintComponent

        lines.add(new LoadingScreen.CreditsLine(1, "Pplos Studio",pplosLogo, PPLOS_WIDTH));
        lines.add(new LoadingScreen.CreditsLine(1, "sites.google.com/view/pplosstudio"));
        lines.add(new LoadingScreen.CreditsLine(1, " "));
        lines.add(new LoadingScreen.CreditsLine(1, " "));

        initialLines = lines.size();

    }

    private void Update(){

        clearRect40K();

        updateCanvasSize();

        long elapse = System.currentTimeMillis() - start;
        int count = (int) (elapse / 500) % 4;

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

            for (LoadingScreen.CreditsLine line : lines) {

                index++;

                if (line.type == 0) y += 10;

                FontMetrics fm = fms[line.type];

                line.y = y + (int)fm.getAscent();
                y += (int)fm.getLineHeight();

            }
            linesHeight = y;
        }

        int yPos = 270;
        int centerX = (int)cv.getWidth()/2;

        for (LoadingScreen.CreditsLine line : lines) {

            int type = line.type;

            g.setStroke(colorBase[type]);
            g.setFill(colorBase[type]);
            g.setFont(font[type]);

            int textWidth = OldFontmetrics.computeStringWidth(fms[type],line.text);

            g.fillText(line.text, centerX-textWidth/2,  line.y + yPos);

            Image img = line.img;
            if (img != null) {
                g.drawImage(img, width - line.imgWidth,line.y + yPos - 60);
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



    public static void nextStep(){
        double buff = progress.get();
        buff += 0.143;
        progress.set(buff);
    }

    public static void Close(){
        progress.set(1);
        if(stage != null)stage.close();
    }

    @Override
    public void onClose() {

        update.stop();

    }


}

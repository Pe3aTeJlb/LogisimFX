package LogisimFX;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Text;

public class OldFontmetrics {

    private static final Text internal = new Text();

    public static float computeStringWidthFloat(FontMetrics fm, String txt) {
        internal.setFont(fm.getFont());
        internal.setText(txt);
        return (float) internal.getLayoutBounds().getWidth();
    }

    public static int computeStringWidth(FontMetrics fm, String txt) {

        float width = 0;

        for (char c: txt.toCharArray()){
            width += fm.getCharWidth(c);
        }

        return (int) width;
    }

}

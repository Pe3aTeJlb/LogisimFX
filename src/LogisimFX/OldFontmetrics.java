package LogisimFX;

import com.sun.javafx.tk.FontMetrics;
import javafx.scene.text.Text;

public class OldFontmetrics {

    private static final Text internal = new Text();

    public static float computeStringWidthFloat(FontMetrics fm, String txt) {
        internal.setFont(fm.getFont());
        internal.setText(txt);
        return (float) internal.getLayoutBounds().getWidth();
    }

    public static int computeStringWidth(FontMetrics fm, String txt) {
        internal.setFont(fm.getFont());
        internal.setText(txt);
        return (int) internal.getLayoutBounds().getWidth();
    }

}

/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX;

import com.sun.javafx.tk.FontMetrics;
import javafx.scene.text.Text;

import java.util.HashMap;

public class OldFontmetrics {

    private static final HashMap<String, Integer> strings = new HashMap<>();
    private static final Text internal = new Text();

    public static float computeStringWidthFloat(FontMetrics fm, String txt) {
        internal.setFont(fm.getFont());
        internal.setText(txt);
        return (float) internal.getLayoutBounds().getWidth();
    }

    public static int computeStringWidth(FontMetrics fm, String txt) {

        if(strings.containsKey(txt)){
            return strings.get(txt);
        }else {

            float width = 0;

            for (char c : txt.toCharArray()) {
                width += fm.getCharWidth(c);
            }

            strings.put(txt,(int)width);

            return (int) width;

        }

    }

}

/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX;

import com.sun.javafx.tk.FontMetrics;
import javafx.scene.text.Text;

import java.util.HashMap;

public class OldFontmetric_8 {

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
            int width = 0;
            //int width = (int) fm.computeStringWidth(txt);
            strings.put(txt,width);
            return width;
        }

    }

}

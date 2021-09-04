package com.cburch.LogisimFX.draw;

import com.cburch.LogisimFX.localization.LC_draw;
import com.cburch.LogisimFX.localization.Localizer;
import javafx.beans.binding.StringBinding;

public class LC {

    private static Localizer lc = LC_draw.getInstance();

    public static StringBinding createStringBinding(final String key, Object... args) {
        return lc.createStringBinding(key, args);
    }

    public static StringBinding createComplexStringBinding(final String key, String... strings) {
        return lc.createComplexStringBinding(key, strings);
    }

    public static StringBinding castToBind(final String string){
        return lc.castToBind(string);
    }

    public static String get(final String key, final Object... args) {
        return lc.get(key, args);
    }

    public static String getFormatted(final String key, String... strings) {
        return lc.getFormatted(key,strings);
    }

}

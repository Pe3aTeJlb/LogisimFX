package com.cburch.LogisimFX.data;

import com.cburch.LogisimFX.localization.LC_data;
import com.cburch.LogisimFX.localization.Localizer;
import javafx.beans.binding.StringBinding;

// package localizer
// LC - localize
// much easier to localize hole bundle, but mainframe has several bundles, so
// we will use localizer manually

public class LC {

    private static Localizer lc = LC_data.getInstance();

    public static StringBinding createStringBinding(final String key, Object... args) {
        return lc.createStringBinding(key, args);
    }

    public static String createComplexString(final String key, String... strings){
        return lc.createComplexString(key, strings);
    }

    public static StringBinding createComplexStringBinding(final String key, String... strings) {
        return lc.createComplexStringBinding(key, strings);
    }

    public static String get(final String key, final Object... args) {
       return lc.get(key, args);
    }

}

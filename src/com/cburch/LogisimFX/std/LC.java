package com.cburch.LogisimFX.std;

import com.cburch.LogisimFX.Localizer;
import javafx.beans.binding.StringBinding;

// package localizer
// LC - localize
// much easier to localize hole bundle, but mainframe has several bundles, so
// we will use localizer manually

public class LC {

    private static Localizer lc = new Localizer("std");

    public static StringBinding createStringBinding(final String key, Object... args) {
        return lc.createStringBinding(key, args);
    }

    public static String createComplexString(final String key, String... strings){
        return lc.createComplexString(key, strings);
    }

    public static StringBinding createComplexStringBinding(final String key, String... strings) {
        return lc.createComplexStringBinding(key, strings);
    }

    public static StringBinding createRawStringBinding(final String string){
        return lc.createRawStringBinding(string);
    }

    public static String get(final String key, final Object... args) {
       return lc.get(key, args);
    }

}

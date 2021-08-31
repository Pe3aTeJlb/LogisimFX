package com.cburch.LogisimFX.localization;

public class LC_statistics {

    private static final String packageName = "statistics";

    private static Localizer lc;

    private LC_statistics(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("statistics localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

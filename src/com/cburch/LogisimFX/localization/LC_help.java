package com.cburch.LogisimFX.localization;

public class LC_help {

    private static final String packageName = "help";

    private static Localizer lc;

    private LC_help(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("help localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

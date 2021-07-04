package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_log {

    private static final String packageName = "log";

    private static Localizer lc;

    private LC_log(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("log localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

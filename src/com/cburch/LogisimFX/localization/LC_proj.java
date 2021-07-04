package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_proj {

    private static final String packageName = "proj";

    private static Localizer lc;

    private LC_proj(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("proj localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

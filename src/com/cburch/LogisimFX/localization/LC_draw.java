package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_draw {

    private static final String packageName = "draw";

    private static Localizer lc;

    private LC_draw(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("draw localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

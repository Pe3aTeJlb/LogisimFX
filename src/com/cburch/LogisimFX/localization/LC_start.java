package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_start {

    private static final String packageName = "start";

    private static Localizer lc;

    private LC_start(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("start localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

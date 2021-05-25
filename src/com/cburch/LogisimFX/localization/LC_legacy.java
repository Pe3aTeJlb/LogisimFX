package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_legacy {

    private static final String packageName = "legacy";

    private static Localizer lc;

    private LC_legacy(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("legacy localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

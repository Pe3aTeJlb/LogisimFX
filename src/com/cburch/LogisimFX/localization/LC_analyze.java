package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_analyze {

    private static final String packageName = "analyze";

    private static Localizer lc;

    private LC_analyze(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("analyze localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

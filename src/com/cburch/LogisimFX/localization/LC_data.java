package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_data {

    private static final String packageName = "data";

    private static Localizer lc;

    private LC_data(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("data localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

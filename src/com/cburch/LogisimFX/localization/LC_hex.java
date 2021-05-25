package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_hex {

    private static final String packageName = "hex";

    private static Localizer lc;

    private LC_hex(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("hex localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

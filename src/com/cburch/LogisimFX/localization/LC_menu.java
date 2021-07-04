package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_menu {

    private static final String packageName = "menu";

    private static Localizer lc;

    private LC_menu(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("menu localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

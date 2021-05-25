package com.cburch.LogisimFX.localization;

//implement Singleton
public class LC_file {

    private static final String packageName = "file";

    private static Localizer lc;

    private LC_file(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("file localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

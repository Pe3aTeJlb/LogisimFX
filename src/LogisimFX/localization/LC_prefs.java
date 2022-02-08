/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

//implement Singleton
public class LC_prefs {

    private static final String packageName = "prefs";

    private static Localizer lc;

    private LC_prefs(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("prefs localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

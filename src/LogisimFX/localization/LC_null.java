/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

//implement Singleton
public class LC_null {

    private static final String packageName = "gui";

    private static Localizer lc;

    private LC_null(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("null localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

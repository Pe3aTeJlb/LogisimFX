/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

public class LC_print {

    private static final String packageName = "print";

    private static Localizer lc;

    private LC_print(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("print localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

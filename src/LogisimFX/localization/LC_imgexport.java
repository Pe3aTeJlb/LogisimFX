/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

public class LC_imgexport {

    private static final String packageName = "imgexport";

    private static Localizer lc;

    private LC_imgexport(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("imgexport localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

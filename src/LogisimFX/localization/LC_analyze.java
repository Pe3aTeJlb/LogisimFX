/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

//implement Singleton
public class LC_analyze {

    private static final String packageName = "analyze";

    private static Localizer lc;

    private LC_analyze(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("analyze localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

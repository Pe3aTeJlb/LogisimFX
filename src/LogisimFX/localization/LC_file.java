/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

//implement Singleton
public class LC_file {

    private static final String packageName = "file";

    private static Localizer lc;

    private LC_file(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("file localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

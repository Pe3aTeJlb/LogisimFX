/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

//implement Singleton
public class LC_hex {

    private static final String packageName = "hex";

    private static Localizer lc;

    private LC_hex(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("hex localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

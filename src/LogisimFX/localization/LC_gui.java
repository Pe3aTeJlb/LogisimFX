package LogisimFX.localization;

//implement Singleton
public class LC_gui {

    private static final String packageName = "gui";

    private static Localizer lc;

    private LC_gui(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("gui localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

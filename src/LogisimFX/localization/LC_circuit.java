package LogisimFX.localization;

//implement Singleton
public class LC_circuit {

    private static final String packageName = "circuit";

    private static Localizer lc;

    private LC_circuit(){}

    public static Localizer getInstance(){

        if(lc == null){
            if(Localizer.debug)System.out.println("circuit localizer created from static");
            lc = new Localizer(packageName);
        }

        return lc;

    }

}

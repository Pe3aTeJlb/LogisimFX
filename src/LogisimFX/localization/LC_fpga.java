package LogisimFX.localization;

public class LC_fpga {

	private static final String packageName = "fpga";

	private static Localizer lc;

	private LC_fpga(){}

	public static Localizer getInstance(){

		if(lc == null){
			if(Localizer.debug)System.out.println("fpga localizer created from static");
			lc = new Localizer(packageName);
		}

		return lc;

	}

}

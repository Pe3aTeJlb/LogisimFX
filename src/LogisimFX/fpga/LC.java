package LogisimFX.fpga;

import LogisimFX.localization.LC_fpga;
import LogisimFX.localization.Localizer;
import javafx.beans.binding.StringBinding;

public class LC {

	private static Localizer lc = LC_fpga.getInstance();

	public static StringBinding createStringBinding(final String key) {
		return lc.createStringBinding(key);
	}

	public static StringBinding createComplexStringBinding(final String key, String... strings) {
		return lc.createComplexStringBinding(key, strings);
	}

	public static StringBinding castToBind(final String string){
		return lc.castToBind(string);
	}

	public static String get(final String key) {
		return lc.get(key);
	}

	public static String getFormatted(final String key, String... strings) {
		return lc.getFormatted(key,strings);
	}

}

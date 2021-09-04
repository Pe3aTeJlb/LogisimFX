/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.util;

import java.awt.*;

public class FontUtil {
	public static String toStyleStandardString(int style) {
		switch (style) {
		case Font.PLAIN:
			return "plain";
		case Font.ITALIC:
			return "italic";
		case Font.BOLD:
			return "bold";
		case Font.BOLD | Font.ITALIC:
			return "bolditalic";
		default:
			return "??";
		}
	}

	public static String toStyleDisplayString(int style) {
		switch (style) {
		case Font.PLAIN:
			return LC.get("fontPlainStyle");
		case Font.ITALIC:
			return LC.get("fontItalicStyle");
		case Font.BOLD:
			return LC.get("fontBoldStyle");
		case Font.BOLD | Font.ITALIC:
			return LC.get("fontBoldItalicStyle");
		default:
			return "??";
		}
	}

}

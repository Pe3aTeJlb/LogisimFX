/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.util;

import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.newgui.DialogManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SyntaxChecker {

	private static final Pattern variablePattern = Pattern.compile("^([a-zA-Z]+\\w*)");
	private static final Pattern forbiddenPattern = Pattern.compile("__");

	private static Matcher forbiddenMatcher;
	private static Matcher variableMatcher;

	private SyntaxChecker() {
		throw new IllegalStateException("Utility class. No instantiation allowed.");
	}

	public static String getErrorMessage(String val) {
		if (StringUtil.isNullOrEmpty(val)) return null;
		variableMatcher = variablePattern.matcher(val);
		forbiddenMatcher = forbiddenPattern.matcher(val);
		final var hdl = CorrectLabel.hdlCorrectLabel(val);
		var message = "";
		if (!variableMatcher.matches()) {
			message = message.concat(LC.get("variableInvalidCharacters"));
		}
		if (forbiddenMatcher.find()) {
			message = message.concat(LC.get("variableDoubleUnderscore"));
		}
		if (hdl != null) {
			message = message.concat(LC.get("variableVerilogKeyword"));
		}
		if (val.endsWith("_")) {
			message = message.concat(LC.get("variableEndsWithUndescore"));
		}
		return (message.length() == 0) ? null : message;
	}

	public static boolean isVariableNameAcceptable(String val, Boolean showDialog) {
		final var message = getErrorMessage(val);
		if (message != null && showDialog) {
			DialogManager.CreateWarningDialog("Error", message.concat(LC.get("variableNameNotAcceptable")));
		}
		return message == null;
	}

}

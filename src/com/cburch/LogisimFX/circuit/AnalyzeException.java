/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.circuit;

import com.cburch.LogisimFX.util.StringUtil;

public class AnalyzeException extends Exception {
	public static class Circular extends AnalyzeException {
		public Circular() {
			super(LC.get("analyzeCircularError"));
		}
	}

	public static class Conflict extends AnalyzeException {
		public Conflict() {
			super(LC.get("analyzeConflictError"));
		}
	}
	
	public static class CannotHandle extends AnalyzeException {
		public CannotHandle(String reason) {
			super(LC.getFormatted("analyzeCannotHandleError", reason));
		}
	}
	
	public AnalyzeException() { }
	
	public AnalyzeException(String message) {
		super(message);
	}
}

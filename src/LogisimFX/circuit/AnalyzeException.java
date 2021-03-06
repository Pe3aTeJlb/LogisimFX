/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

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

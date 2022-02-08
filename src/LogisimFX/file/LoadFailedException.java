/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

public class LoadFailedException extends Exception {
	private boolean shown;
	
	LoadFailedException(String desc) {
		this(desc, false);
	}
	
	LoadFailedException(String desc, boolean shown) {
		super(desc);
		this.shown = shown;
	}
	
	public boolean isShown() {
		return shown;
	}
}
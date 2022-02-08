/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.tools.Library;

interface LibraryLoader {
	public Library loadLibrary(String desc);
	public String getDescriptor(Library lib);
	public void showError(String description);
}

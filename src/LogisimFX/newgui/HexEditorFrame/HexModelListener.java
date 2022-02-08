/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.HexEditorFrame;

public interface HexModelListener {

	void metainfoChanged(HexModel source);
	void bytesChanged(HexModel source, long start, long numBytes, int[] oldValues);

}

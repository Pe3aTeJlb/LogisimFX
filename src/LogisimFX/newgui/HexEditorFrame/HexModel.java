/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.HexEditorFrame;

public interface HexModel {

	/** Registers a listener for changes to the values. */
	void addHexModelListener(HexModelListener l);
	
	/** Unregisters a listener for changes to the values. */
	void removeHexModelListener(HexModelListener l);
	
	/** Returns the offset of the initial value to be displayed. */
	long getFirstOffset();
	
	/** Returns the number of values to be displayed. */
	long getLastOffset();
	
	/** Returns number of bits in each value. */
	int getValueWidth();
	
	/** Returns the value at the given address. */
	int get(long address);
	
	/** Changes the value at the given address. */
	void set(long address, int value);
	
	/** Changes a series of values at the given addresses. */
	void set(long start, int[] values);
	
	/** Fills a series of values with the same value. */
	void fill(long start, long length, int value);

}

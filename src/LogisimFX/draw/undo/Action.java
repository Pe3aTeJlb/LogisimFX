/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.undo;

public abstract class Action {

	public boolean isModification() { return true; }

	public abstract String getName();

	public abstract void doIt();

	public abstract void undo();

	public boolean shouldAppendTo(Action other) { return false; }

	public Action append(Action other) {
		return new ActionUnion(this, other);
	}

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.proj;

public abstract class Action {

	public boolean isModification() { return true; }

	public abstract String getName();

	public abstract void doIt(Project proj);

	public abstract void undo(Project proj);

	public boolean shouldAppendTo(Action other) { return false; }

	public Action append(Action other) {
		return new JoinedAction(this, other);
	}

}
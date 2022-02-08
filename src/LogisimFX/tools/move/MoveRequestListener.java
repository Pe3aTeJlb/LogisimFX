/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.move;

public interface MoveRequestListener {
	public void requestSatisfied(MoveGesture gesture, int dx, int dy);
}

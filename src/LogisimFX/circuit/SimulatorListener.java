/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

public interface SimulatorListener {
	public void propagationCompleted(SimulatorEvent e);
	public void tickCompleted(SimulatorEvent e);
	public void simulatorStateChanged(SimulatorEvent e);
}

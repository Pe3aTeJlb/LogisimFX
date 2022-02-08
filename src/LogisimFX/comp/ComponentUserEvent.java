/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

import LogisimFX.circuit.CircuitState;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;

public class ComponentUserEvent {

	private LayoutCanvas.CME e;
	private LayoutCanvas canvas;
	private int x = 0;
	private int y = 0;

	ComponentUserEvent(LayoutCanvas canvas) {
		this.canvas = canvas;
	}

	public ComponentUserEvent(LayoutCanvas canvas, int x, int y, LayoutCanvas.CME e) {
		this.canvas = canvas;
		this.x = x;
		this.y = y;
		this.e = e;
	}

	public LayoutCanvas getCanvas() {
		return canvas;
	}

	public CircuitState getCircuitState() {
		return canvas.getCircuitState();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public LayoutCanvas.CME getEvent() {
		return e;
	}
}

/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.comp;

import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.newgui.MainFrame.CustomCanvas;

public class ComponentUserEvent {

	private CustomCanvas canvas;
	private int x = 0;
	private int y = 0;

	ComponentUserEvent(CustomCanvas canvas) {
		this.canvas = canvas;
	}

	public ComponentUserEvent(CustomCanvas canvas, int x, int y) {
		this.canvas = canvas;
		this.x = x;
		this.y = y;
	}

	public CustomCanvas getCanvas() {
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

}

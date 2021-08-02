/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.instance;

import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.comp.ComponentUserEvent;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.newgui.MainFrame.CustomCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.tools.AbstractCaret;
import com.cburch.LogisimFX.tools.Caret;
import com.cburch.LogisimFX.tools.Pokable;
import com.cburch.LogisimFX.circuit.CircuitState;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

class InstancePokerAdapter extends AbstractCaret implements Pokable {

	private InstanceComponent comp;
	private CustomCanvas canvas;
	private InstancePoker poker;
	private InstanceStateImpl state;
	private ComponentDrawContext context;

	public InstancePokerAdapter(InstanceComponent comp, Class<? extends InstancePoker> pokerClass) {
		try {
			this.comp = comp;
			poker = pokerClass.newInstance();
		} catch (Throwable t) {
			handleError(t, pokerClass);
			poker = null;
		}
	}

	private void handleError(Throwable t, Class<? extends InstancePoker> pokerClass) {
		String className = pokerClass.getName();
		System.err.println("error while instantiating poker " + className //OK
				+ ": " + t.getClass().getName());
		String msg = t.getMessage();
		if (msg != null) System.err.println("  (" + msg + ")"); //OK
	}

	public Caret getPokeCaret(ComponentUserEvent event) {
		if (poker == null) {
			return null;
		} else {
			canvas = event.getCanvas();
			CircuitState circState = event.getCircuitState();
			InstanceStateImpl state = new InstanceStateImpl(circState, comp);
			CustomCanvas.CME e = new CustomCanvas.CME(new MouseEvent(
					MouseEvent.MOUSE_PRESSED,
					0,0,
					event.getX(), event.getY(),
					MouseButton.PRIMARY,1,
					false,false,false,false,
					true,false,false,
					false,false,false,
					new PickResult(event.getCanvas(),event.getX(), event.getY())
					)
			);

			boolean isAccepted = poker.init(state, e);
			if (isAccepted) {
				this.state = state;
				this.context = new ComponentDrawContext(
						event.getCanvas().getCircuit(), circState, null, false);
				mousePressed(e);
				return this;
			} else {
				poker = null;
				return null;
			}
		}
	}

	@Override
	public void mousePressed(CustomCanvas.CME e) {
		if (poker != null) { poker.mousePressed(state, e); checkCurrent(); }
	}

	@Override
	public void mouseDragged(CustomCanvas.CME e) {
		if (poker != null) { poker.mouseDragged(state, e); checkCurrent(); }
	}

	@Override
	public void mouseReleased(CustomCanvas.CME e) {
		if (poker != null) { poker.mouseReleased(state, e); checkCurrent(); }
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (poker != null) { poker.keyPressed(state, e); checkCurrent(); }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (poker != null) { poker.keyReleased(state, e); checkCurrent(); }
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (poker != null) { poker.keyTyped(state, e); checkCurrent(); }
	}

	@Override
	public void stopEditing() {
		if (poker != null) { poker.stopEditing(state); checkCurrent(); }
	}

	@Override
	public Bounds getBounds(Graphics g) {
		if (poker != null) {
			context.setGraphics(g);
			InstancePainter painter = new InstancePainter(context, comp);
			return poker.getBounds(painter);
		} else {
			return Bounds.EMPTY_BOUNDS;
		}
	}

	@Override
	public void draw(Graphics g) {

		if (poker != null) {
			context.setGraphics(g);
			InstancePainter painter = new InstancePainter(context, comp);
			poker.paint(painter);
		}

	}

	private void checkCurrent() {
		if (state != null && canvas != null) {
			CircuitState s0 = state.getCircuitState();
			CircuitState s1 = canvas.getCircuitState();
			if (s0 != s1) {
				state = new InstanceStateImpl(s1, comp);
			}
		}
	}

}

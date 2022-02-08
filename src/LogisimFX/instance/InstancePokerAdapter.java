/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.comp.ComponentUserEvent;
import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.tools.AbstractCaret;
import LogisimFX.tools.Caret;
import LogisimFX.tools.Pokable;
import LogisimFX.circuit.CircuitState;

import javafx.scene.input.KeyEvent;

class InstancePokerAdapter extends AbstractCaret implements Pokable {

	private InstanceComponent comp;
	private LayoutCanvas canvas;
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
			/*
			LayoutCanvas.CME e = new LayoutCanvas.CME(new MouseEvent(
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

			 */

			boolean isAccepted = poker.init(state, event.getEvent());
			if (isAccepted) {
				this.state = state;
				this.context = new ComponentDrawContext(
						event.getCanvas().getCircuit(), circState, null, false);
				mousePressed(event.getEvent());
				return this;
			} else {
				poker = null;
				return null;
			}
		}
	}

	@Override
	public void mousePressed(LayoutCanvas.CME e) {
		if (poker != null) { poker.mousePressed(state, e); checkCurrent(); }
	}

	@Override
	public void mouseDragged(LayoutCanvas.CME e) {
		if (poker != null) { poker.mouseDragged(state, e); checkCurrent(); }
	}

	@Override
	public void mouseReleased(LayoutCanvas.CME e) {
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

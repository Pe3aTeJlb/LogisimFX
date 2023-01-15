/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstancePoker;
import LogisimFX.instance.InstanceState;

import javafx.scene.paint.Color;

public class SubcircuitPoker extends InstancePoker {
	
	private static final Color MAGNIFYING_INTERIOR = new Color(0.784, 0.784, 1, 0.251);
	private static final Color MAGNIFYING_INTERIOR_DOWN = new Color(0.502, 0.502, 1, 0.753);
	
	private boolean mouseDown;

	@Override
	public Bounds getBounds(InstancePainter painter) {

		Bounds bds = painter.getInstance().getBounds();
		int cx = bds.getX() + bds.getWidth() / 2;
		int cy = bds.getY() + bds.getHeight() / 2;

		return Bounds.create(cx - 5, cy - 5, 15, 15);

	}
	
	@Override
	public void paint(InstancePainter painter) {

		if (painter.getData() instanceof CircuitState) {
			Bounds bds = painter.getInstance().getBounds();
			int cx = bds.getX() + bds.getWidth() / 2;
			int cy = bds.getY() + bds.getHeight() / 2;

			int tx = cx + 3;
			int ty = cy + 3;
			double[] xp = { tx - 1, cx + 8, cx + 10, tx + 1 };
			double[] yp = { ty + 1, cy + 10, cy + 8, ty - 1 };
			Graphics g = painter.getGraphics();
			if (mouseDown) {
				g.setColor(MAGNIFYING_INTERIOR_DOWN);
			} else {
				g.setColor(MAGNIFYING_INTERIOR);
			}
			g.c.fillOval(cx - 5, cy - 5, 10, 10);
			g.setColor(Color.BLACK);
			g.c.strokeOval(cx - 5, cy - 5, 10, 10);
			g.c.fillPolygon(xp, yp, xp.length);
			g.toDefault();
		}

	}

	@Override
	public void mousePressed(InstanceState state, LayoutCanvas.CME e) {

		if (isWithin(state, e)) {
			mouseDown = true;
			state.getInstance().fireInvalidated();
		}

	}

	@Override
	public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {

		if (mouseDown) {
			mouseDown = false;
			Object sub = state.getData();
			if (e.event.getClickCount() == 2 && isWithin(state, e)
					&& sub instanceof CircuitState) {
				state.getProject().setCircuitState((CircuitState) sub);
			} else {
				state.getInstance().fireInvalidated();
			}
		}

	}
	
	private boolean isWithin(InstanceState state, LayoutCanvas.CME e) {

		Bounds bds = state.getInstance().getBounds();
		int cx = bds.getX() + bds.getWidth() / 2;
		int cy = bds.getY() + bds.getHeight() / 2;
		int dx = e.localX - cx;
		int dy = e.localY - cy;

		return dx * dx + dy * dy <= 60;

	}

}

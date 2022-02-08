/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.tools;

import LogisimFX.IconsManager;
import LogisimFX.draw.LC;
import LogisimFX.draw.actions.ModelAddAction;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.shapes.Curve;
import LogisimFX.draw.shapes.CurveUtil;
import LogisimFX.draw.shapes.DrawAttr;
import LogisimFX.draw.shapes.LineUtil;
import LogisimFX.data.Attribute;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.List;

public class CurveTool extends AbstractTool {

	private static final int BEFORE_CREATION = 0;
	private static final int ENDPOINT_DRAG = 1;
	private static final int CONTROL_DRAG = 2;
	
	private DrawingAttributeSet attrs;
	private int state;
	private Location end0;
	private Location end1;
	private Curve curCurve;
	private boolean mouseDown;
	private int lastMouseX;
	private int lastMouseY;
	
	public CurveTool(DrawingAttributeSet attrs) {

		this.attrs = attrs;
		state = BEFORE_CREATION;
		mouseDown = false;

	}

	@Override
	public String getName(){
		return  LC.get("shapeCurve");
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("drawcurv.gif");
	}

	@Override
	public Cursor getCursor() {
		return Cursor.CROSSHAIR;
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		state = BEFORE_CREATION;
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		int mx = e.localX;
		int my = e.localY;
		lastMouseX = mx;
		lastMouseY = my;
		mouseDown = true;

		if (e.event.isControlDown()) {
			mx = e.snappedX;
			my = e.snappedY;
		}
		
		switch (state) {
		case BEFORE_CREATION:
		case CONTROL_DRAG:
			end0 = Location.create(mx, my);
			end1 = end0;
			state = ENDPOINT_DRAG;
			break;
		case ENDPOINT_DRAG:
			curCurve = new Curve(end0, end1, Location.create(mx, my));
			state = CONTROL_DRAG;
			break;
		}

	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		updateMouse(canvas, e.localX, e.localY, e);
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		Curve c = updateMouse(canvas, e.localX, e.localY, e);
		mouseDown = false;
		if (state == CONTROL_DRAG) {
			if (c != null) {
				attrs.applyTo(c);
				CanvasModel model = canvas.getModel();
				canvas.doAction(new ModelAddAction(model, c));
				canvas.toolGestureComplete(this, c);
			}
			state = BEFORE_CREATION;
		}

	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {

		KeyCode code = e.getCode();
		if (mouseDown && (code == KeyCode.SHIFT
				|| code == KeyCode.CONTROL || code == KeyCode.ALT)) {
			updateMouse(canvas, lastMouseX, lastMouseY, null);
		}

	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}

	@Override
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) {

		if (e.getCode() == KeyCode.ESCAPE) { // escape key
			state = BEFORE_CREATION;
			canvas.toolGestureComplete(this, null);
		}

	}
	
	private Curve updateMouse(AppearanceCanvas canvas, int mx, int my, AppearanceCanvas.CME e) {

		lastMouseX = mx;
		lastMouseY = my;
		
		boolean shiftDown = e.event.isShiftDown();
		boolean ctrlDown = e.event.isControlDown();
		boolean altDown = e.event.isAltDown();
		Curve ret = null;
		switch (state) {
		case ENDPOINT_DRAG:
			if (mouseDown) {
				if (shiftDown) {
					Location p = LineUtil.snapTo8Cardinals(end0, mx, my);
					mx = p.getX();
					my = p.getY();
				}
				if (ctrlDown) {
					mx = e.snappedX;
					my = e.snappedY;
				}
				end1 = Location.create(mx, my);
			}
			break;
		case CONTROL_DRAG:
			if (mouseDown) {
				int cx = mx;
				int cy = my;
				if (ctrlDown) {
					cx = AppearanceCanvas.snapXToGrid(cx);
					cy = AppearanceCanvas.snapYToGrid(cy);
				}
				if (shiftDown) {
					double x0 = end0.getX();
					double y0 = end0.getY();
					double x1 = end1.getX();
					double y1 = end1.getY();
					double midx = (x0 + x1) / 2;
					double midy = (y0 + y1) / 2;
					double dx = x1 - x0;
					double dy = y1 - y0;
					double[] p = LineUtil.nearestPointInfinite(cx, cy,
							midx, midy, midx - dy, midy + dx);
					cx = (int) Math.round(p[0]);
					cy = (int) Math.round(p[1]);
				}
				if (altDown) {
					double[] e0 = { end0.getX(), end0.getY() };
					double[] e1 = { end1.getX(), end1.getY() };
					double[] mid = { cx, cy };
					double[] ct = CurveUtil.interpolate(e0, e1, mid);
					cx = (int) Math.round(ct[0]);
					cy = (int) Math.round(ct[1]);
				}
				ret = new Curve(end0, end1, Location.create(cx, cy));
				curCurve = ret;
			}
			break;
		}

		return ret;

	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getFillAttributes(attrs.getValue(DrawAttr.PAINT_TYPE));
	}

	@Override
	public void draw(AppearanceCanvas canvas) {

		Graphics g = canvas.getGraphics();
		g.setColor(Color.GRAY);

		switch (state) {
		case ENDPOINT_DRAG:
			g.c.strokeLine(end0.getX(), end0.getY(), end1.getX(), end1.getY());
			break;
		case CONTROL_DRAG:
			curCurve.strokeCurve2D(g);
			break;
		}

		g.toDefault();

	}

}

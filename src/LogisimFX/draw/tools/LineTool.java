/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.draw.tools;

import LogisimFX.IconsManager;
import LogisimFX.draw.LC;
import LogisimFX.draw.actions.ModelAddAction;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.shapes.DrawAttr;
import LogisimFX.draw.shapes.LineUtil;
import LogisimFX.draw.shapes.Poly;
import LogisimFX.data.Attribute;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.util.UnmodifiableList;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.List;

public class LineTool extends AbstractTool {

	private DrawingAttributeSet attrs;
	private boolean active;
	private Location mouseStart;
	private Location mouseEnd;
	private int lastMouseX;
	private int lastMouseY;
	
	public LineTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
		active = false;
	}

	@Override
	public String getName(){
		return LC.get("shapeLine");
	}

	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("drawline.gif");
	}

	@Override
	public Cursor getCursor() { return Cursor.CROSSHAIR;}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.ATTRS_STROKE;
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		active = false;
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		int x = e.localX;
		int y = e.localY;
		if (e.event.isControlDown()) {
			x = e.snappedX;
			y = e.snappedY;
		}
		Location loc = Location.create(x, y);
		mouseStart = loc;
		mouseEnd = loc;
		lastMouseX = loc.getX();
		lastMouseY = loc.getY();
		active = canvas.getModel() != null;

	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		updateMouse(canvas, e.localX, e.localY, e);
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		if (active) {
			updateMouse(canvas, e.localX, e.localY, e);
			Location start = mouseStart;
			Location end = mouseEnd;
			CanvasObject add = null;
			if (!start.equals(end)) {
				active = false;
				CanvasModel model = canvas.getModel();
				Location[] ends = { start, end };
				List<Location> locs = UnmodifiableList.create(ends);
				add = attrs.applyTo(new Poly(false, locs));
				add.setValue(DrawAttr.PAINT_TYPE, DrawAttr.PAINT_STROKE);
				canvas.doAction(new ModelAddAction(model, add));
			}
			canvas.toolGestureComplete(this, add);
		}

	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {

		KeyCode code = e.getCode();
		if (active && (code == KeyCode.SHIFT || code == KeyCode.CONTROL)) {
			updateMouse(canvas, lastMouseX, lastMouseY, null);
		}

	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}
	
	private void updateMouse(AppearanceCanvas canvas, int mx, int my, AppearanceCanvas.CME e) {

		if (active) {
			boolean shift = e.event.isShiftDown();
			Location newEnd;
			if (shift) {
				newEnd = LineUtil.snapTo8Cardinals(mouseStart, mx, my);
			} else {
				newEnd = Location.create(mx, my);
			}
			
			if (e.event.isControlDown()) {
				int x = newEnd.getX();
				int y = newEnd.getY();
				x = AppearanceCanvas.snapXToGrid(x);
				y = AppearanceCanvas.snapYToGrid(y);
				newEnd = Location.create(x, y);
			}
			
			if (!newEnd.equals(mouseEnd)) {
				mouseEnd = newEnd;
			}
		}
		lastMouseX = mx;
		lastMouseY = my;

	}
	
	@Override
	public void draw(AppearanceCanvas canvas) {

		Graphics g = canvas.getGraphics();

		if (active) {
			Location start = mouseStart;
			Location end = mouseEnd;
			g.setColor(Color.GRAY);
			g.c.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
		}

		g.toDefault();

	}
	
	static Location snapTo4Cardinals(Location from, int mx, int my) {

		int px = from.getX();
		int py = from.getY();
		if (mx != px && my != py) {
			if (Math.abs(my - py) < Math.abs(mx - px)) {
				return Location.create(mx, py);
			} else {
				return Location.create(px, my);
			}
		}

		return Location.create(mx, my); // should never happen

	}

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit.appear;

import LogisimFX.circuit.LC;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.model.Handle;
import LogisimFX.draw.model.HandleGesture;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.instance.Instance;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.wiring.Pin;
import LogisimFX.util.UnmodifiableList;

import javafx.scene.paint.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class AppearancePort extends AppearanceElement {

	private static final int INPUT_RADIUS = 4;
	private static final int OUTPUT_RADIUS = 5;
	private static final int MINOR_RADIUS = 2;
	public static final Color COLOR = Color.BLUE;
	
	private Instance pin;
	
	public AppearancePort(Location location, Instance pin) {

		super(location);
		this.pin = pin;

	}
	
	@Override
	public boolean matches(CanvasObject other) {

		if (other instanceof AppearancePort) {
			AppearancePort that = (AppearancePort) other;
			return this.matches(that) && this.pin == that.pin;
		} else {
			return false;
		}

	}

	@Override
	public int matchesHashCode() {
		return super.matchesHashCode() + pin.hashCode();
	}

	@Override
	public String getDisplayName() {
		return LC.get("circuitPort");
	}
	
	@Override
	public Element toSvgElement(Document doc) {

		Location loc = getLocation();
		Location pinLoc = pin.getLocation();
		Element ret = doc.createElement("circ-port");
		int r = isInput() ? INPUT_RADIUS : OUTPUT_RADIUS;
		ret.setAttribute("x", "" + (loc.getX() - r));
		ret.setAttribute("y", "" + (loc.getY() - r));
		ret.setAttribute("width", "" + 2 * r);
		ret.setAttribute("height", "" + 2 * r);
		ret.setAttribute("pin", "" + pinLoc.getX() + "," + pinLoc.getY());

		return ret;

	}
	
	public Instance getPin() {
		return pin;
	}
	
	void setPin(Instance value) {
		pin = value;
	}
	
	private boolean isInput() {
		Instance p = pin;
		return p == null || Pin.FACTORY.isInputPin(p);
	}
	
	@Override
	public Bounds getBounds() {
		int r = isInput() ? INPUT_RADIUS : OUTPUT_RADIUS;
		return super.getBounds(r);
	}
	
	@Override
	public boolean contains(Location loc, boolean assumeFilled) {

		if (isInput()) {
			return getBounds().contains(loc);
		} else {
			return super.isInCircle(loc, OUTPUT_RADIUS);
		}

	}

	@Override
	public List<Handle> getHandles(HandleGesture gesture) {

		Location loc = getLocation();
		
		int r = isInput() ? INPUT_RADIUS : OUTPUT_RADIUS;
		return UnmodifiableList.create(new Handle[] {
				new Handle(this, loc.translate(-r, -r)),
				new Handle(this, loc.translate(r, -r)),
				new Handle(this, loc.translate(r, r)),
				new Handle(this, loc.translate(-r, r)) });

	}

	@Override
	public void paint(Graphics g, HandleGesture gesture) {

		Location location = getLocation();
		int x = location.getX();
		int y = location.getY();
		g.setColor(COLOR);
		if (isInput()) {
			int r = INPUT_RADIUS;
			g.c.strokeRect(x - r, y - r, 2 * r, 2 * r);
		} else {
			int r = OUTPUT_RADIUS;
			g.c.strokeOval(x - r, y - r, 2 * r, 2 * r);
		}
		g.c.fillOval(x - MINOR_RADIUS, y - MINOR_RADIUS, 2 * MINOR_RADIUS, 2 * MINOR_RADIUS);

	}

}

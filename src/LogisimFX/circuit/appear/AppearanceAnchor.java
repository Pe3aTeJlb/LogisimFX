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
import LogisimFX.data.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.util.UnmodifiableList;

import javafx.scene.paint.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class AppearanceAnchor extends AppearanceElement {

	public static final Attribute<Direction> FACING
		= Attributes.forDirection("facing", LC.createStringBinding("appearanceFacingAttr"));
	static final List<Attribute<?>> ATTRIBUTES
		= UnmodifiableList.create(new Attribute<?>[] { FACING });

	private static final int RADIUS = 3;
	private static final int INDICATOR_LENGTH = 8;
	private static final Color SYMBOL_COLOR = Color.color(0, 0.502, 0);

	private Direction facing;

	public AppearanceAnchor(Location location) {

		super(location);
		facing = Direction.EAST;

	}

	@Override
	public boolean matches(CanvasObject other) {

		if (other instanceof AppearanceAnchor) {
			AppearanceAnchor that = (AppearanceAnchor) other;
			return super.matches(that) && this.facing.equals(that.facing);
		} else {
			return false;
		}

	}

	@Override
	public int matchesHashCode() {
		return super.matchesHashCode() * 31 + facing.hashCode();
	}

	@Override
	public String getDisplayName() {
		return LC.get("circuitAnchor");
	}
	
	@Override
	public Element toSvgElement(Document doc) {

		Location loc = getLocation();
		Element ret = doc.createElement("circ-anchor");
		ret.setAttribute("x", "" + (loc.getX() - RADIUS));
		ret.setAttribute("y", "" + (loc.getY() - RADIUS));
		ret.setAttribute("width", "" + 2 * RADIUS);
		ret.setAttribute("height", "" + 2 * RADIUS);
		ret.setAttribute("facing", facing.toString());

		return ret;

	}

	public Direction getFacing() {
		return facing;
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return ATTRIBUTES;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == FACING) {
			return (V) facing;
		} else {
			return super.getValue(attr);
		}

	}
	
	@Override
	protected void updateValue(Attribute<?> attr, Object value) {

		if (attr == FACING) {
			facing = (Direction) value;
		} else {
			super.updateValue(attr, value);
		}

	}

	@Override
	public void paint(Graphics g, HandleGesture gesture) {

		Location location = getLocation();
		int x = location.getX();
		int y = location.getY();
		g.setColor(SYMBOL_COLOR);
		g.c.strokeOval(x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS);
		Location e0 = location.translate(facing, RADIUS);
		Location e1 = location.translate(facing, RADIUS + INDICATOR_LENGTH);
		g.c.strokeLine(e0.getX(), e0.getY(), e1.getX(), e1.getY());

	}
	
	@Override
	public Bounds getBounds() {

		Bounds bds = super.getBounds(RADIUS);
		Location center = getLocation();
		Location end = center.translate(facing, RADIUS + INDICATOR_LENGTH);

		return bds.add(end);

	}

	@Override
	public boolean contains(Location loc, boolean assumeFilled) {

		if (super.isInCircle(loc, RADIUS)) {
			return true;
		} else {
			Location center = getLocation();
			Location end = center.translate(facing, RADIUS + INDICATOR_LENGTH);
			if (facing == Direction.EAST || facing == Direction.WEST) {
				return Math.abs(loc.getY() - center.getY()) < 2
					&& (loc.getX() < center.getX()) != (loc.getX() < end.getX());
			} else {
				return Math.abs(loc.getX() - center.getX()) < 2
					&& (loc.getY() < center.getY()) != (loc.getY() < end.getY());
			}
		}

	}
	
	@Override
	public List<Handle> getHandles(HandleGesture gesture) {

		Location c = getLocation();
		Location end = c.translate(facing, RADIUS + INDICATOR_LENGTH);
		return UnmodifiableList.create(new Handle[] { new Handle(this, c),
				new Handle(this, end) });
	}

}

/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.circuit;

import LogisimFX.comp.Component;
import LogisimFX.comp.*;
import LogisimFX.data.*;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.tools.CustomHandles;
import LogisimFX.util.Cache;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Wire implements Component, AttributeSet, CustomHandles,
		Iterable<Location> {

	/** Stroke width when drawing wires. */
	public static final int WIDTH = 3;

	public static final AttributeOption VALUE_HORZ
			= new AttributeOption("horz", LC.createStringBinding("wireDirectionHorzOption"));
	public static final AttributeOption VALUE_VERT
			= new AttributeOption("vert", LC.createStringBinding("wireDirectionVertOption"));
	public static final AttributeOption VALUE_DIAG
			= new AttributeOption("diag", LC.createStringBinding("wireDirectionDiagOption"));
	public static final Attribute<AttributeOption> dir_attr
			= Attributes.forOption("direction", LC.createStringBinding("wireDirectionAttr"),
			new AttributeOption[] { VALUE_HORZ, VALUE_VERT, VALUE_DIAG });
	public static final Attribute<Integer> len_attr
			= Attributes.forInteger("length", LC.createStringBinding("wireLengthAttr"));
	public static final Attribute<Double> rot_attr
			= Attributes.forDouble("rotation", LC.createStringBinding("wireRotationAttr"));

	private static final List<Attribute<?>> ATTRIBUTES
			= Arrays.asList(new Attribute<?>[] {StdAttr.FPGA_SUPPORTED, dir_attr, len_attr, rot_attr });
	private static final Cache cache = new Cache();

	public static Wire create(Location e0, Location e1) {
		return (Wire) cache.get(new Wire(e0, e1));
	}

	private class EndList extends AbstractList<EndData> {
		@Override
		public EndData get(int i) {
			return getEnd(i);
		}
		@Override
		public int size() { return 2; }
	}

	final Location e0;
	final Location e1;
	final boolean fpga = true;
	final boolean is_x_equal;
	final boolean is_diagonal;
	final double rot;
	final int deg_rot; // lol

	private Wire(Location e0, Location e1) {
		this.is_x_equal = e0.getX() == e1.getX();
		if (is_x_equal) {
			if (e0.getY() > e1.getY()) {
				this.e0 = e1;
				this.e1 = e0;
			} else {
				this.e0 = e0;
				this.e1 = e1;
			}
			this.rot = 0;
			this.deg_rot = 0;
			this.is_diagonal = false;
		} else if (e0.getY() == e1.getY()) {
			if (e0.getX() > e1.getX()) {
				this.e0 = e1;
				this.e1 = e0;
			} else {
				this.e0 = e0;
				this.e1 = e1;
			}
			this.deg_rot = 0;
			this.rot = 0;
			this.is_diagonal = false;
		}else{
			this.is_diagonal = true;
			this.e0 = e0;
			this.e1 = e1;

			if(e1.getX() > e0.getX()){
				if(e1.getY() > e0.getY()){
					this.rot = Math.toRadians(135);
					this.deg_rot = 135;
				}else{
					this.rot = Math.toRadians(45);
					this.deg_rot = 45;
				}
			}else{
				if(e0.getY() > e1.getY()){
					this.rot = Math.toRadians(315);
					this.deg_rot = 315;
				}else{
					this.rot = Math.toRadians(225);
					this.deg_rot = 225;
				}
			}

		}

	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Wire)) return false;
		Wire w = (Wire) other;
		return w.e0.equals(this.e0) && w.e1.equals(this.e1) && w.rot == this.rot;
	}

	@Override
	public int hashCode() {
		return e0.hashCode() * 31 + e1.hashCode();
	}

	public int getLength() {
		if(is_diagonal){
			int y = e1.getY() - e0.getY();
			int x = e1.getX() - e0.getX();
			return (int) Math.sqrt(x*x+y*y);
		}else {
			return (e1.getY() - e0.getY()) + (e1.getX() - e0.getX());
		}
	}

	public double getRotation(){
		return rot;
	}

	public double toRadians(int deg){
		return deg * Math.PI / 180;
	}

	@Override
	public String toString() {
		return "Wire[" + e0 + "-" + e1 + "]";
	}

	//
	// Component methods
	//
	// (Wire never issues ComponentEvents, so we don't need to track listeners)
	public void addComponentListener(ComponentListener e) { }
	public void removeComponentListener(ComponentListener e) { }

	public ComponentFactory getFactory() {
		return WireFactory.instance;
	}

	public AttributeSet getAttributeSet() {
		return this;
	}

	// location/extent methods
	public Location getLocation() {
		return e0;
	}

	public Bounds getBounds() {

		int x0 = e0.getX();
		int y0 = e0.getY();

		if(is_diagonal){
			return Bounds.create(x0 - 2, y0 - 2,
					getLength() + 5, 5, rot);
		}else {
			return Bounds.create(x0 - 2, y0 - 2,
					e1.getX() - x0 + 5, e1.getY() - y0 + 5);
		}

	}

	public Bounds getBounds(Graphics g) {
		return getBounds();
	}

	public boolean contains(Location q) {

		int qx = q.getX();
		int qy = q.getY();

		if(is_diagonal){

			Location left = getLeftPoint();
			Location right = getRightPoint();
			Location upper = getUpperPoint();
			Location bottom = getBottomPoint();

			return qx >= left.getX() - 2 && qx <= right.getX() + 2 && qy >= bottom.getY() - 2 && qy <= upper.getY() + 2
					&& Math.abs(Math.abs(e0.getX() - qx) - Math.abs(e0.getY() - qy)) < 4;

		}else {

			if (is_x_equal) {
				int wx = e0.getX();
				return qx >= wx - 2 && qx <= wx + 2
						&& e0.getY() <= qy && qy <= e1.getY();
			} else {
				int wy = e0.getY();
				return qy >= wy - 2 && qy <= wy + 2
						&& e0.getX() <= qx && qx <= e1.getX();
			}

		}

	}

	public boolean contains(Location pt, Graphics g) {
		return contains(pt);
	}

	//
	// propagation methods
	//
	public List<EndData> getEnds() {
		return new EndList();
	}

	public EndData getEnd(int index) {
		Location loc = getEndLocation(index);
		return new EndData(loc, BitWidth.UNKNOWN,
				EndData.INPUT_OUTPUT);
	}

	public boolean endsAt(Location pt) {
		return e0.equals(pt) || e1.equals(pt);
	}

	public void propagate(CircuitState state) {
		// Normally this is handled by CircuitWires, and so it won't get
		// called. The exception is when a wire is added or removed
		state.markPointAsDirty(e0);
		state.markPointAsDirty(e1);
	}

	//
	// user interface methods
	//

	public void draw(ComponentDrawContext context) {

		CircuitState state = context.getCircuitState();
		Graphics g = context.getGraphics();

		g.setLineWidth(WIDTH);
		g.setColor(state.getValue(e0).getColor());
		g.c.strokeLine(e0.getX(), e0.getY(), e1.getX(), e1.getY());

	}

	public Object getFeature(Object key) {
		if (key == CustomHandles.class) return this;
		return null;
	}


	//
	// AttributeSet methods
	//
	// It makes some sense for a wire to be its own attribute, since
	// after all it is immutable.
	//
	@Override
	public Object clone() { return this; }
	public void addAttributeListener(AttributeListener l) { }
	public void removeAttributeListener(AttributeListener l) { }
	public List<Attribute<?>> getAttributes() { return ATTRIBUTES; }
	public boolean containsAttribute(Attribute<?> attr) { return ATTRIBUTES.contains(attr); }
	public Attribute<?> getAttribute(String name) {
		for (Attribute<?> attr : ATTRIBUTES) {
			if (name.equals(attr.getName())) return attr;
		}
		return null;
	}
	public boolean isReadOnly(Attribute<?> attr) { return true; }
	public void setReadOnly(Attribute<?> attr, boolean value) {
		throw new UnsupportedOperationException();
	}
	public boolean isToSave(Attribute<?> attr) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == dir_attr) {
			if(is_diagonal) return (V) VALUE_DIAG;
			else return (V) (is_x_equal ? VALUE_VERT : VALUE_HORZ);
		} else if (attr == len_attr) {
			return (V) Integer.valueOf(getLength());
		} else if (attr == rot_attr) {
			return (V) Double.valueOf(getRotation());
		} else if (attr == StdAttr.FPGA_SUPPORTED) {
			return (V) Boolean.valueOf(fpga);
		} else {
			return null;
		}

	}

	public <V> void setValue(Attribute<V> attr, V value) {
		throw new IllegalArgumentException("read only attribute");
	}

	//
	// other methods
	//
	public boolean isVertical() { return is_x_equal; }

	public boolean isDiagonal() { return is_diagonal; }

	public Location getEndLocation(int index) { return index == 0 ? e0 : e1; }

	public Location getEnd0() { return e0; }

	public Location getEnd1() { return e1; }

	public Location getOtherEnd(Location loc) {
		return (loc.equals(e0) ? e1 : e0);
	}

	public Location getLeftPoint(){
		return e0.getX() < e1.getX() ? e0 : e1;
	}

	public Location getRightPoint(){
		return e0.getX() < e1.getX() ? e1 : e0;
	}

	public Location getUpperPoint(){
		return e0.getY() < e1.getY() ? e1 : e0;
	}

	public Location getBottomPoint(){
		return e0.getY() < e1.getY() ? e0 : e1;
	}

	public boolean sharesEnd(Wire other) {
		return this.e0.equals(other.e0) || this.e1.equals(other.e0)
				|| this.e0.equals(other.e1) || this.e1.equals(other.e1);
	}

	public boolean overlaps(Wire other, boolean includeEnds) {
		return overlaps(other.e0, other.e1, other.rot, includeEnds);
	}

	private boolean overlaps(Location q0, Location q1, double rot, boolean includeEnds) {

		if(is_diagonal){
			int x0 = q0.getX();
			if (rot == 0 || !((Math.abs(rot - this.rot) == Math.PI || Math.abs(rot - this.rot) == 0))) return false;
			if (includeEnds) {
				return e1.getX() >= q0.getX() && e0.getX() <= q1.getX() || e0.getX() <= q0.getX() && e1.getX() >= q1.getX() ||
						e1.getX() <= q0.getX() && e0.getX() <= q1.getX() || e0.getX() >= q0.getX() && e1.getX() >= q1.getX();
			} else {
				//System.out.println("overlaps diag " + (e1.getX() > q0.getX() && e0.getX() < q1.getX()) + " " +  (e0.getX() < q0.getX() && e1.getX() > q1.getX()));
				return e1.getX() > q0.getX() && e0.getX() < q1.getX() || e0.getX() < q0.getX() && e1.getX() > q1.getX() ||
						e1.getX() < q0.getX() && e0.getX() < q1.getX() || e0.getX() > q0.getX() && e1.getX() > q1.getX();
			}
		} else if (is_x_equal) {
			int x0 = q0.getX();
			if (x0 != q1.getX() || x0 != e0.getX()) return false;
			if (includeEnds) {
				return e1.getY() >= q0.getY() && e0.getY() <= q1.getY();
			} else {
				return e1.getY() > q0.getY() && e0.getY() < q1.getY();
			}
		} else {
			int y0 = q0.getY();
			if (y0 != q1.getY() || y0 != e0.getY()) return false;
			if (includeEnds) {
				return e1.getX() >= q0.getX() && e0.getX() <= q1.getX();
			} else {
				return e1.getX() > q0.getX() && e0.getX() < q1.getX();
			}
		}

	}

	public boolean isParallel(Wire other) {

		if(this.is_diagonal && other.is_diagonal){
			return Math.abs(other.rot - this.rot) == Math.PI || Math.abs(other.rot - this.rot) == 0;
		}else {
			return this.is_x_equal == other.is_x_equal && !this.is_diagonal && !other.is_diagonal;
		}

	}

	public Iterator<Location> iterator() {
		return new WireIterator(e0, e1);
	}

	public void drawHandles(ComponentDrawContext context) {
		context.drawHandle(e0);
		context.drawHandle(e1);
	}

}

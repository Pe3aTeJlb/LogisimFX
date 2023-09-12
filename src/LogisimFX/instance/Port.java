/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

import LogisimFX.comp.EndData;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.BitWidth;
import LogisimFX.data.Location;

import javafx.beans.binding.StringBinding;

public class Port {
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String INOUT = "inout";
	
	public static final String EXCLUSIVE = "exclusive";
	public static final String SHARED = "shared";

	public static final String CLOCK = "clock";
	
	private int dx;
	private int dy;
	private int type;
	private BitWidth widthFixed;
	private Attribute<BitWidth> widthAttr;
	private boolean exclude;
	private StringBinding toolTip;
	
	public Port(int dx, int dy, String type, BitWidth bits) {
		this(dx, dy, type, bits, defaultExclusive(type));
	}

	public Port(int dx, int dy, String type, int bits) {
		this(dx, dy, type, BitWidth.create(bits), defaultExclusive(type));
	}
	
	public Port(int dx, int dy, String type, int bits, String exclude) {
		this(dx, dy, type, BitWidth.create(bits), exclude);
	}
	
	public Port(int dx, int dy, String type, BitWidth bits, String exclude) {
		this.dx = dx;
		this.dy = dy;
		this.type = toType(type);
		this.widthFixed = bits;
		this.widthAttr = null;
		this.exclude = toExclusive(exclude);
		this.toolTip = null;
	}
	
	public Port(int dx, int dy, String type, Attribute<BitWidth> attr) {
		this(dx, dy, type, attr, defaultExclusive(type));
	}
	
	public Port(int dx, int dy, String type, Attribute<BitWidth> attr,
			String exclude) {
		this.dx = dx;
		this.dy = dy;
		this.type = toType(type);
		this.widthFixed = null;
		this.widthAttr = attr;
		this.exclude = toExclusive(exclude);
		this.toolTip = null;
	}
	
	public void setToolTip(StringBinding value) {
		toolTip = value;
	}
	
	public StringBinding getToolTip() {
		StringBinding getter = toolTip;
		return getter;
	}
	
	public Attribute<BitWidth> getWidthAttribute() {
		return widthAttr;
	}
	
	public EndData toEnd(Location loc, AttributeSet attrs) {
		Location pt = loc.translate(dx, dy);
		if (widthFixed != null) {
			return new EndData(pt, widthFixed, type, exclude);
		} else {
			Object val = attrs.getValue(widthAttr);
			if (!(val instanceof BitWidth)) {
				throw new IllegalArgumentException("Width attribute not set");
			}
			return new EndData(pt, (BitWidth) val, type, exclude);
		}
	}

	public int getRelX(){
		return dx;
	}

	public int getRelY(){
		return dy;
	}
	
	private static int toType(String s) {
		if (s == null) throw new IllegalArgumentException("Null port type");
		else if (s.equals(INPUT))  return EndData.INPUT_ONLY;
		else if (s.equals(OUTPUT)) return EndData.OUTPUT_ONLY;
		else if (s.equals(INOUT))  return EndData.INPUT_OUTPUT;
		else throw new IllegalArgumentException("Not recognized port type");
	}
	
	private static String defaultExclusive(String s) {
		if (s == null) throw new IllegalArgumentException("Null port type");
		else if (s.equals(INPUT))  return SHARED;
		else if (s.equals(OUTPUT)) return EXCLUSIVE;
		else if (s.equals(INOUT))  return SHARED;
		else throw new IllegalArgumentException("Not recognized port type");
	}
	
	private static boolean toExclusive(String s) {
		if (s == null) throw new IllegalArgumentException("Null exclusion type");
		else if (s.equals(EXCLUSIVE)) return true;
		else if (s.equals(SHARED))    return false;
		else throw new IllegalArgumentException("Not recognized exclusion type");
	}
}

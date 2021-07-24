/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.plexers;

import java.util.List;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.FactoryDescription;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;

import javafx.beans.binding.StringBinding;
import javafx.scene.canvas.GraphicsContext;

public class Plexers extends Library {

	public static final Attribute<BitWidth> ATTR_SELECT
		= Attributes.forBitWidth("select", LC.createStringBinding("plexerSelectBitsAttr"), 1, 5);
	public static final Object DEFAULT_SELECT = BitWidth.create(1);

	public static final Attribute<Boolean> ATTR_TRISTATE
		= Attributes.forBoolean("tristate", LC.createStringBinding("plexerThreeStateAttr"));
	public static final Object DEFAULT_TRISTATE = Boolean.FALSE;

	public static final AttributeOption DISABLED_FLOATING
		= new AttributeOption("Z", LC.createStringBinding("plexerDisabledFloating"));
	public static final AttributeOption DISABLED_ZERO
		= new AttributeOption("0", LC.createStringBinding("plexerDisabledZero"));
	public static final Attribute<AttributeOption> ATTR_DISABLED
		= Attributes.forOption("disabled", LC.createStringBinding("plexerDisabledAttr"),
				new AttributeOption[] { DISABLED_FLOATING, DISABLED_ZERO });

	public static final Attribute<Boolean> ATTR_ENABLE
		= Attributes.forBoolean("enable", LC.createStringBinding("plexerEnableAttr"));

	static final AttributeOption SELECT_BOTTOM_LEFT
		= new AttributeOption("bl", LC.createStringBinding("plexerSelectBottomLeftOption"));
	static final AttributeOption SELECT_TOP_RIGHT
		= new AttributeOption("tr", LC.createStringBinding("plexerSelectTopRightOption"));
	static final Attribute<AttributeOption> ATTR_SELECT_LOC = Attributes.forOption("selloc",
			LC.createStringBinding("plexerSelectLocAttr"),
			new AttributeOption[] { SELECT_BOTTOM_LEFT, SELECT_TOP_RIGHT });

	protected static final int DELAY = 3;

	private static FactoryDescription[] DESCRIPTIONS = {
		new FactoryDescription("Multiplexer", LC.createStringBinding("multiplexerComponent"),
				"multiplexer.gif", "Multiplexer"),
		new FactoryDescription("Demultiplexer", LC.createStringBinding("demultiplexerComponent"),
				"demultiplexer.gif", "Demultiplexer"),
		new FactoryDescription("Decoder", LC.createStringBinding("decoderComponent"),
				"decoder.gif", "Decoder"),
		new FactoryDescription("Priority Encoder", LC.createStringBinding("priorityEncoderComponent"),
				"priencod.gif", "PriorityEncoder"),
		new FactoryDescription("BitSelector", LC.createStringBinding("bitSelectorComponent"),
				"bitSelector.gif", "BitSelector"),
	};

	private List<Tool> tools = null;

	public Plexers() { }

	@Override
	public String getName() { return "Plexers"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("plexerLibrary"); }

	@Override
	public List<Tool> getTools() {

		if (tools == null) {
			tools = FactoryDescription.getTools(Plexers.class, DESCRIPTIONS);
		}
		return tools;

	}

	static void drawTrapezoid(GraphicsContext g, Bounds bds, Direction facing,
							  int facingLean) {
		int wid = bds.getWidth();
		int ht = bds.getHeight();
		int x0 = bds.getX(); int x1 = x0 + wid;
		int y0 = bds.getY(); int y1 = y0 + ht;
		double[] xp = { x0, x1, x1, x0 };
		double[] yp = { y0, y0, y1, y1 };
		if (facing == Direction.WEST) {
			yp[0] += facingLean; yp[3] -= facingLean;
		} else if (facing == Direction.NORTH) {
			xp[0] += facingLean; xp[1] -= facingLean;
		} else if (facing == Direction.SOUTH) {
			xp[2] -= facingLean; xp[3] += facingLean;
		} else {
			yp[1] += facingLean; yp[2] -= facingLean;
		}
		g.setLineWidth(2);
		g.strokePolygon(xp, yp, 4);
	}
	
	static boolean contains(Location loc, Bounds bds, Direction facing) {

		if (bds.contains(loc, 1)) {
			int x = loc.getX();
			int y = loc.getY();
			int x0 = bds.getX();
			int x1 = x0 + bds.getWidth();
			int y0 = bds.getY();
			int y1 = y0 + bds.getHeight();
			if (facing == Direction.NORTH || facing == Direction.SOUTH) {
				if (x < x0 + 5 || x > x1 - 5) {
					if (facing == Direction.SOUTH) {
						return y < y0 + 5;
					} else {
						return y > y1 - 5;
					}
				} else {
					return true;
				}
			} else {
				if (y < y0 + 5 || y > y1 - 5) {
					if (facing == Direction.EAST) {
						return x < x0 + 5;
					} else {
						return x > x1 - 5;
					}
				} else {
					return true;
				}
			}
		} else {
			return false;
		}

	}

}

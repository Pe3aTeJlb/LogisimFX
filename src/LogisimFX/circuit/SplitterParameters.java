/* Copyright (c) 2010, Carl Burch.
 *  Copyright (c) 2022, Pplos Studio
 *  License information is located in the Launch file */

package LogisimFX.circuit;

import LogisimFX.data.Direction;
import LogisimFX.util.GraphicsUtil;

class SplitterParameters {
	private int dxEnd0; // location of split end 0 relative to origin
	private int dyEnd0;
	private int ddxEnd; // distance from split end i to split end (i + 1)
	private int ddyEnd;
	private int dxEndSpine; // distance from split end to spine
	private int dyEndSpine;
	private int dxSpine0; // distance from origin to far end of spine
	private int dySpine0;
	private int dxSpine1; // distance from origin to near end of spine
	private int dySpine1;
	private int textAngle; // angle to rotate text
	private int halign; // justification of text
	private int valign;

	SplitterParameters(SplitterAttributes attrs) {
		
		Object appear = attrs.appear;
		int fanout = attrs.fanout;
		Direction facing = attrs.facing;
		boolean tunnelView = attrs.tunnelView;
		
		int justify;
		if (appear == SplitterAttributes.APPEAR_CENTER || appear == SplitterAttributes.APPEAR_LEGACY) {
			justify = 0;
		} else if (appear == SplitterAttributes.APPEAR_RIGHT) {
			justify = 1;
		} else {
			justify = -1;
		}
		int width = 20;

		int offs = 6;
		if (facing == Direction.NORTH || facing == Direction.SOUTH) { // ^ or V
			int m = facing == Direction.NORTH ? 1 : -1;
			int baseval;
			if(tunnelView) baseval = 20; else baseval = 10;
			dxEnd0 = justify == 0 ? baseval * ((fanout + 1) / 2 - 1) : m * justify < 0 ? -1 * baseval : baseval * fanout;
			dyEnd0 = -m * width;
			if(tunnelView) ddxEnd = -20; else ddxEnd = -10;
			ddyEnd = 0;
			dxEndSpine = 0;
			dyEndSpine = m * (width - offs);
			dxSpine0 = m * justify * (baseval * fanout - 1);
			dySpine0 = -m * offs;
			dxSpine1 = m * justify * offs;
			dySpine1 = -m * offs;
			textAngle = 90;
			halign = m > 0 ? GraphicsUtil.H_RIGHT : GraphicsUtil.H_LEFT;
			valign = m * justify <= 0 ? GraphicsUtil.V_BASELINE : GraphicsUtil.V_TOP;
		} else { // > or <
			int m = facing == Direction.WEST ? -1 : 1;
			dxEnd0 = m * width;
			int baseval;
			if(tunnelView) baseval = 20; else baseval = 10;
			dyEnd0 = justify == 0 ? -1 *baseval  * (fanout / 2) : m * justify > 0 ? baseval : -1 * baseval * fanout;
			ddxEnd = 0;
			if(tunnelView) ddyEnd = 20; else ddyEnd = 10;
			dxEndSpine = -m * (width - offs);
			dyEndSpine = 0;
			dxSpine0 = m * offs;
			dySpine0 = m * justify * (baseval * fanout - 1);
			dxSpine1 = m * offs;
			dySpine1 = m * justify * offs;
			textAngle = 0;
			halign = m > 0 ? GraphicsUtil.H_LEFT : GraphicsUtil.H_RIGHT;
			valign = m * justify < 0 ? GraphicsUtil.V_TOP : GraphicsUtil.V_BASELINE;
		}
	}
	
	public int getEnd0X() {
		return dxEnd0;
	}
	
	public int getEnd0Y() {
		return dyEnd0;
	}
	
	public int getEndToEndDeltaX() {
		return ddxEnd;
	}
	
	public int getEndToEndDeltaY() {
		return ddyEnd;
	}
	
	public int getEndToSpineDeltaX() {
		return dxEndSpine;
	}
	
	public int getEndToSpineDeltaY() { 
		return dyEndSpine;
	}
	
	public int getSpine0X() {
		return dxSpine0;
	}
	
	public int getSpine0Y() {
		return dySpine0;
	}
	
	public int getSpine1X() {
		return dxSpine1;
	}
	
	public int getSpine1Y() {
		return dySpine1;
	}
	
	public int getTextAngle() {
		return textAngle;
	}
	
	public int getTextHorzAlign() {
		return halign;
	}
	
	public int getTextVertAlign() {
		return valign;
	}
}

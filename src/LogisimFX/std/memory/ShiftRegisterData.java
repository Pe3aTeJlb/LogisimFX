/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.BitWidth;
import LogisimFX.data.Value;
import LogisimFX.instance.InstanceData;

import java.util.Arrays;

public class ShiftRegisterData extends ClockState implements InstanceData {

	private BitWidth width;
	private Value[] vs;
	private int vsPos;
	private boolean shiftLeft;
	
	public ShiftRegisterData(BitWidth width, int len, Value shiftDir) {

		this.width = width;
		this.vs = new Value[len];
		Arrays.fill(this.vs, Value.createKnown(width, 0));
		this.shiftLeft = shiftDir == Value.TRUE;

	}
	
	@Override
	public ShiftRegisterData clone() {

		ShiftRegisterData ret = (ShiftRegisterData) super.clone();
		ret.vs = this.vs.clone();
		return ret;

	}
	
	public int getLength() {
		return vs.length;
	}
	
	public void setDimensions(BitWidth newWidth, int newLength, Value shiftDir) {

		Value[] v = vs;
		BitWidth oldWidth = width;
		int oldW = oldWidth.getWidth();
		int newW = newWidth.getWidth();

		if (v.length != newLength) {
			Value[] newV = new Value[newLength];
			int j = vsPos;
			int copy = Math.min(newLength, v.length);
			for (int i = 0; i < copy; i++) {
				newV[i] = v[j];
				j++;
				if (j == v.length) j = 0;
			}
			Arrays.fill(newV, copy, newLength, Value.createKnown(newWidth, 0));
			v = newV;
			vsPos = 0;
			vs = newV;
		}

		if(shiftLeft != (shiftDir == Value.TRUE)) {
			shiftLeft = (shiftDir == Value.TRUE);
		}

		if (oldW != newW) {
			for (int i = 0; i < v.length; i++) {
				Value vi = v[i];
				if (vi.getWidth() != newW) {
					v[i] = vi.extendWidth(newW, Value.FALSE);
				}
			}
			width = newWidth;
		}

	}
	
	public void clear() {

		Arrays.fill(vs, Value.createKnown(width, 0));

	}
	
	public void push(Value v) {

		if(!shiftLeft) {

			if (vs.length - 1 >= 0) System.arraycopy(vs, 0, vs, 1, vs.length - 1);
			vs[0] = v;

		}else{

			if (vs.length - 1 >= 0) System.arraycopy(vs, 1, vs, 0, vs.length - 1);
			vs[vs.length - 1] = v;

		}

	}
	
	public Value get(int index) {

		Value[] v = vs;
		return v[index];

	}

	public Integer getAsInt() {

		StringBuilder bin = new StringBuilder();
		for(int i = 0; i < vs.length; i++){
			bin.append(vs[i]);
		}

		return Integer.parseInt(bin.toString(),2);

	}
	
	public void set(int index, Value val) {

		Value[] v = vs;
		v[index] = val;

	}

}
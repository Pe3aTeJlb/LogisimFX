/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.data.Value;

class GateFunctions {

	private GateFunctions() { }
	
	static Value computeOr(Value[] inputs, int numInputs) {

		Value ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = ret.or(inputs[i]);
		}
		return ret;

	}
	
	static Value computeAnd(Value[] inputs, int numInputs) {

		Value ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = ret.and(inputs[i]);
		}
		return ret;

	}
	
	static Value computeOddParity(Value[] inputs, int numInputs) {

		Value ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = ret.xor(inputs[i]);
		}
		return ret;

	}
	
	static Value computeExactlyOne(Value[] inputs, int numInputs) {

		int width = inputs[0].getWidth();
		Value[] ret = new Value[width];
		for (int i = 0; i < width; i++) {
			int count = 0;
			for (int j = 0; j < numInputs; j++) {
				Value v = inputs[j].get(i);
				if (v == Value.TRUE) {
					count++;
				} else if (v == Value.FALSE) {
					; // do nothing
				} else {
					count = -1;
					break;
				}
			}
			if (count < 0) {
				ret[i] = Value.ERROR;
			} else if (count == 1) {
				ret[i] = Value.TRUE;
			} else {
				ret[i] = Value.FALSE;
			}
		}

		return Value.create(ret);

	}

}

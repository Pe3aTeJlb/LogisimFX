/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.wiring;

import javax.swing.JTextField;

import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.util.StringUtil;

import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class DurationAttribute extends Attribute<Integer> {

	private int min;
	private int max;
	
	public DurationAttribute(String name, StringBinding disp, int min, int max) {

		super(name, disp);
		this.min = min;
		this.max = max;

	}

	@Override
	public Integer parse(String value) {

		try {
			Integer ret = Integer.valueOf(value);
			if (ret.intValue() < min) {
				throw new NumberFormatException(LC.getFormatted("durationSmallMessage", "" + min));
			} else if (ret.intValue() > max) {
				throw new NumberFormatException(LC.getFormatted("durationLargeMessage", "" + max));
			}
			return ret;
		} catch (NumberFormatException e) {
			throw new NumberFormatException(LC.get("freqInvalidMessage"));
		}

	}

	@Override
	public String toDisplayString(Integer value) {

		if (value.equals(Integer.valueOf(1))) {
			return LC.get("clockDurationOneValue");
		} else {
			return LC.getFormatted("clockDurationValue", value.toString());
		}

	}

	@Override
	public Node getCell(Integer value){

		TextField field = new TextField();
		field.setText(value.toString());
		return field;

	}

}

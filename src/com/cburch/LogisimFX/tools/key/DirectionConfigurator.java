/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools.key;

import com.cburch.LogisimFX.KeyEvents;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.Direction;
import javafx.scene.input.KeyEvent;

public class DirectionConfigurator implements KeyConfigurator, Cloneable {

	private Attribute<Direction> attr;
	private KeyEvent modsEx;
	
	public DirectionConfigurator(Attribute<Direction> attr, KeyEvent modifiersEx) {
		this.attr = attr;
		this.modsEx = modifiersEx;
	}
	
	@Override
	public DirectionConfigurator clone() {
		try {
			return (DirectionConfigurator) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public KeyConfigurationResult keyEventReceived(KeyConfigurationEvent event) {
		if (event.getType() == KeyConfigurationEvent.KEY_PRESSED) {
			KeyEvent e = event.getKeyEvent();
			if (e == modsEx) {
				Direction value = null;
				switch (e.getCode()) {
				case UP: value = Direction.NORTH; break;
				case DOWN: value = Direction.SOUTH; break;
				case LEFT: value = Direction.WEST; break;
				case RIGHT: value = Direction.EAST; break;
				}
				if (value != null) {
					event.consume();
					return new KeyConfigurationResult(event, attr, value);
				}
			}
		}
		return null;
	}

}

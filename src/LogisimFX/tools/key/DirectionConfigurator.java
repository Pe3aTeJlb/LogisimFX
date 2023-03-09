/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.key;

import LogisimFX.data.Attribute;
import LogisimFX.data.Direction;
import javafx.scene.input.KeyEvent;

public class DirectionConfigurator implements KeyConfigurator, Cloneable {

	private Attribute<?> attr;
	private KeyEvent modsEx;
	
	public DirectionConfigurator(Attribute<?> attr, KeyEvent modifiersEx) {
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

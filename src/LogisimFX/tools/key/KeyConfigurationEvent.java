/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.key;

import LogisimFX.data.AttributeSet;
import javafx.scene.input.KeyEvent;

public class KeyConfigurationEvent {

	public static final int KEY_PRESSED = 0;
	public static final int KEY_RELEASED = 1;
	public static final int KEY_TYPED = 2;
	
	private int type;
	private AttributeSet attrs;
	private KeyEvent event;
	private Object data;
	private boolean consumed;
	
	public KeyConfigurationEvent(int type, AttributeSet attrs, KeyEvent event, Object data) {
		this.type = type;
		this.attrs = attrs;
		this.event = event;
		this.data = data;
		this.consumed = false;
	}
	
	public int getType() {
		return type;
	}
	
	public KeyEvent getKeyEvent() {
		return event;
	}
	
	public AttributeSet getAttributeSet() {
		return attrs;
	}
	
	public void consume() {
		consumed = true;
	}
	
	public boolean isConsumed() {
		return consumed;
	}

	public Object getData() {
		return data;
	}

}

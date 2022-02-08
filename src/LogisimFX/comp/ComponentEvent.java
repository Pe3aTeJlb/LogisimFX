/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

public class ComponentEvent {
	private Component source;
	private Object oldData;
	private Object newData;

	public ComponentEvent(Component source) {
		this(source, null, null);
	}

	public ComponentEvent(Component source, Object oldData, Object newData) {
		this.source = source;
		this.oldData = oldData;
		this.newData = newData;
	}

	public Component getSource() {
		return source;
	}

	public Object getData() {
		return newData;
	}

	public Object getOldData() {
		return oldData;
	}
}

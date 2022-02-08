/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.data;

public class AttributeEvent {
	private AttributeSet source;
	private Attribute<?> attr;
	private Object value;

	public AttributeEvent(AttributeSet source, Attribute<?> attr,
                          Object value) {
		this.source = source;
		this.attr = attr;
		this.value = value;
	}

	public AttributeEvent(AttributeSet source) {
		this(source, null, null);
	}

	public Attribute<?> getAttribute() { return attr; }

	public AttributeSet getSource() { return source; }

	public Object getValue() { return value; }
}

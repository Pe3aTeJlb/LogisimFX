/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.data;

import java.util.List;

public interface AttributeSet {

	public Object clone();
	public void addAttributeListener(AttributeListener l);
	public void removeAttributeListener(AttributeListener l);

	public List<Attribute<?>> getAttributes();
	public boolean containsAttribute(Attribute<?> attr);
	public Attribute<?> getAttribute(String name);

	public boolean isReadOnly(Attribute<?> attr);
	public void setReadOnly(Attribute<?> attr, boolean value);  // optional

	public boolean isToSave(Attribute<?> attr);

	public <V> V getValue(Attribute<V> attr);
	public <V> void setValue(Attribute<V> attr, V value);

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

public class InstanceDataSingleton implements InstanceData, Cloneable {
	private Object value;
	
	public InstanceDataSingleton(Object value) {
		this.value = value;
	}
	
	@Override
	public InstanceDataSingleton clone() {
		try {
			return (InstanceDataSingleton) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}

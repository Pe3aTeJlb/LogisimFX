/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class UnmodifiableList<E> extends AbstractList<E> {
	public static <E> List<E> create(E[] data) {
		if (data.length == 0) {
			return Collections.emptyList();
		} else {
			return new UnmodifiableList<E>(data);
		}
	}
	
	private E[] data;
	
	public UnmodifiableList(E[] data) {
		this.data = data;
	}

	@Override
	public E get(int index) {
		return data[index];
	}

	@Override
	public int size() {
		return data.length;
	}
}

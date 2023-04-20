/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.comp.ComponentFactory;
import LogisimFX.util.ListUtil;
import javafx.beans.binding.StringBinding;

import java.util.Collections;
import java.util.List;

public abstract class Library {

	public String getName() {
		return getClass().getName();
	}

	public abstract List<? extends Tool> getTools();

	@Override
	public String toString() { return getName(); }

	public StringBinding getDisplayName() { return new StringBinding() {
			@Override
			protected String computeValue() {
				return getName();
			}
		};
	}
	
	public boolean isDirty() { return false; }

	public List<Library> getLibraries() {
		return Collections.emptyList();
	}

	public List<?> getElements() {
		return ListUtil.joinImmutableLists(getTools(), getLibraries());
	}

	public Tool getTool(String name) {
		for (Tool tool : getTools()) {
			if (tool.getName().equals(name)) {
				return tool;
			}
		}
		return null;
	}

	public boolean containsFromSource(Tool query) {
		for (Tool tool : getTools()) {
			if (tool.sharesSource(query)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOf(ComponentFactory query) {
		int index = -1;
		for (Tool obj : getTools()) {
			index++;
			if (obj instanceof AddTool) {
				AddTool tool = (AddTool) obj;
				if (tool.getFactory() == query) return index;
			}
		}
		return -1;
	}
	
	public boolean contains(ComponentFactory query) {
		return indexOf(query) >= 0;
	}

	public Library getLibrary(String name) {
		for (Library lib : getLibraries()) {
			if (lib.getName().equals(name)) {
				return lib;
			}
		}
		return null;
	}

	public boolean removeLibrary(String name) {
		return false;
	}

}

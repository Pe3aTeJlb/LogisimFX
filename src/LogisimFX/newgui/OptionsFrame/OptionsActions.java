/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.OptionsFrame;

import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.file.MouseMappings;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.tools.Tool;
import LogisimFX.util.StringUtil;

class OptionsActions {
	private OptionsActions() { }

	public static Action setAttribute(AttributeSet attrs, Attribute<?> attr, Object value) {
		Object oldValue = attrs.getValue(attr);
		if (!oldValue.equals(value)) {
			return new SetAction(attrs, attr, value);
		} else {
			return null;
		}
	}
	
	public static Action setMapping(MouseMappings mm, Integer mods,
			Tool tool) {
		return new SetMapping(mm, mods, tool);
	}

	public static Action removeMapping(MouseMappings mm, Integer mods) {
		return new RemoveMapping(mm, mods);
	}
	
	private static class SetAction extends Action {
		private AttributeSet attrs;
		private Attribute<Object> attr;
		private Object newval;
		private Object oldval;

		SetAction(AttributeSet attrs, Attribute<?> attr,
				Object value) {
			@SuppressWarnings("unchecked")
			Attribute<Object> a = (Attribute<Object>) attr;
			this.attrs = attrs;
			this.attr = a;
			this.newval = value;
		}

		@Override
		public String getName() {
			return StringUtil.format(LC.get("setOptionAction"),
				attr.getDisplayName());
		}

		@Override
		public void doIt(Project proj) {
			oldval = attrs.getValue(attr);
			attrs.setValue(attr, newval);
		}

		@Override
		public void undo(Project proj) {
			attrs.setValue(attr, oldval);
		}
	}

	private static class SetMapping extends Action {
		MouseMappings mm;
		Integer mods;
		Tool oldtool;
		Tool tool;

		SetMapping(MouseMappings mm, Integer mods, Tool tool) {
			this.mm = mm;
			this.mods = mods;
			this.tool = tool;
		}

		@Override
		public String getName() {
			return LC.get("addMouseMappingAction");
		}

		@Override
		public void doIt(Project proj) {
			oldtool = mm.getToolFor(mods);
			mm.setToolFor(mods, tool);
		}

		@Override
		public void undo(Project proj) {
			mm.setToolFor(mods, oldtool);
		}
	}

	private static class RemoveMapping extends Action {
		MouseMappings mm;
		Integer mods;
		Tool oldtool;

		RemoveMapping(MouseMappings mm, Integer mods) {
			this.mm = mm;
			this.mods = mods;
		}

		@Override
		public String getName() {
			return LC.get("removeMouseMappingAction");
		}

		@Override
		public void doIt(Project proj) {
			oldtool = mm.getToolFor(mods);
			mm.setToolFor(mods, null);
		}

		@Override
		public void undo(Project proj) {
			mm.setToolFor(mods, oldtool);
		}
	}
}

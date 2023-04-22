/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.data.*;
import LogisimFX.newgui.MainFrame.FrameLayout;

public class Options {

	public static final AttributeOption GATE_UNDEFINED_IGNORE
	= new AttributeOption("ignore", LC.createStringBinding("gateUndefinedIgnore"));
	public static final AttributeOption GATE_UNDEFINED_ERROR
		= new AttributeOption("error", LC.createStringBinding("gateUndefinedError"));

	public static final Attribute<Integer> sim_limit_attr
		= Attributes.forInteger("simlimit", LC.createStringBinding("simLimitOption"));
	public static final Attribute<Integer> sim_rand_attr
		= Attributes.forInteger("simrand", LC.createStringBinding("simRandomOption"));
	public static final Attribute<AttributeOption> ATTR_GATE_UNDEFINED
		= Attributes.forOption("gateUndefined", LC.createStringBinding("gateUndefinedOption"),
				new AttributeOption[] { GATE_UNDEFINED_IGNORE, GATE_UNDEFINED_ERROR });

	public static final Integer sim_rand_dflt = Integer.valueOf(32);

	private static final Attribute<?>[] ATTRIBUTES = {
			ATTR_GATE_UNDEFINED, sim_limit_attr, sim_rand_attr,
	};
	private static final Object[] DEFAULTS = {
			GATE_UNDEFINED_IGNORE, Integer.valueOf(1000), Integer.valueOf(0),
	};

	private AttributeSet attrs;
	private MouseMappings mappings;
	private ToolbarData toolbar;
	private FrameLayout layout;

	public Options() {
		attrs = AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
		mappings = new MouseMappings();
		toolbar = new ToolbarData();
		layout = new FrameLayout();
	}

	public AttributeSet getAttributeSet() {
		return attrs;
	}

	public MouseMappings getMouseMappings() {
		return mappings;
	}

	public ToolbarData getToolbarData() {
		return toolbar;
	}

	public FrameLayout getMainFrameLayout(){
		return layout;
	}

	public void copyFrom(Options other, LogisimFile dest) {
		AttributeSets.copy(other.attrs, this.attrs);
		this.toolbar.copyFrom(other.toolbar, dest);
		this.mappings.copyFrom(other.mappings, dest);
		this.layout.copyFrom(other.layout, dest);
	}

}

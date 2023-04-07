/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

import LogisimFX.data.*;
import LogisimFX.LogisimVersion;

import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.HdlGeneratorFactory;
import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Represents a category of components that appear in a circuit. This class
 * and <code>Component</code> share the same sort of relationship as the
 * relation between <em>classes</em> and <em>instances</em> in Java. Normally,
 * there is only one ComponentFactory created for any particular category.
 */
public interface ComponentFactory extends AttributeDefaultProvider {

	Object SHOULD_SNAP = new Object();
	Object TOOL_TIP = new Object();
	Object FACING_ATTRIBUTE_KEY = new Object();
	
	String getName();
	StringBinding getDisplayName();
	ImageView getIcon();
	StringBinding getDisplayGetter();
	Component createComponent(Location loc, AttributeSet attrs);
	Bounds getOffsetBounds(AttributeSet attrs);
	AttributeSet createAttributeSet();
	boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver);
	Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver);
	void drawGhost(ComponentDrawContext context, Color color,
                          int x, int y, AttributeSet attrs);
	/**
	 * Retrieves special-purpose features for this factory. This technique
	 * allows for future Logisim versions to add new features
	 * for components without requiring changes to existing components.
	 * It also removes the necessity for the Component API to directly
	 * declare methods for each individual feature.
	 * In most cases, the <code>key</code> is a <code>Class</code> object
	 * corresponding to an interface, and the method should return an
	 * implementation of that interface if it supports the feature.
	 * 
	 * As of this writing, possible values for <code>key</code> include:
	 * <code>TOOL_TIP</code> (return a <code>String</code>) and
	 * <code>SHOULD_SNAP</code> (return a <code>Boolean</code>).
	 * 
	 * @param key  an object representing a feature.
	 * @return an object representing information about how the component
	 *    supports the feature, or <code>null</code> if it does not support
	 *    the feature.
	 */
	 Object getFeature(Object key, AttributeSet attrs);



	HdlGeneratorFactory getHDLGenerator(AttributeSet attrs);

	String getHDLName(AttributeSet attrs);

	boolean activeOnHigh(AttributeSet attrs);

	boolean hasThreeStateDrivers(AttributeSet attrs);

	boolean isHDLSupportedComponent(AttributeSet attrs);

	boolean isHDLGeneratorAvailable();

	boolean checkForGatedClocks(netlistComponent comp);

	int[] clockPinIndex(netlistComponent comp);

	boolean requiresGlobalClock();

	boolean requiresNonZeroLabel();

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

import java.util.List;

import LogisimFX.data.AttributeSet;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.circuit.CircuitState;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

public interface Component {

	// listener methods
	 void addComponentListener(ComponentListener l);
	 void removeComponentListener(ComponentListener l);

	// basic information methods
	 ComponentFactory getFactory();
	 AttributeSet getAttributeSet();

	// location/extent methods
	 Location getLocation();
	 Bounds getBounds();
	 Bounds getBounds(Graphics g);
	 boolean contains(Location pt);
	 boolean contains(Location pt, Graphics g);

	// user interface methods
	 void draw(ComponentDrawContext context);
	/**
	 * Retrieves information about a special-purpose feature for this
	 * component. This technique allows future Logisim versions to add
	 * new features for components without requiring changes to existing
	 * components. It also removes the necessity for the Component API to
	 * directly declare methods for each individual feature.
	 * In most cases, the <code>key</code> is a <code>Class</code> object
	 * corresponding to an interface, and the method should return an
	 * implementation of that interface if it supports the feature.
	 * 
	 * As of this writing, possible values for <code>key</code> include:
	 * <code>Pokable.class</code>, <code>CustomHandles.class</code>,
	 * <code>WireRepair.class</code>, <code>TextEditable.class</code>,
	 * <code>MenuExtender.class</code>, <code>ToolTipMaker.class</code>,
	 * <code>ExpressionComputer.class</code>, and <code>Loggable.class</code>.
	 * 
	 * @param key  an object representing a feature.
	 * @return an object representing information about how the component
	 *    supports the feature, or <code>null</code> if it does not support
	 *    the feature.
	 */
	 Object getFeature(Object key);
	
	// propagation methods
	 List<EndData> getEnds(); // list of EndDatas
	 EndData getEnd(int index);
	 boolean endsAt(Location pt);
	 void propagate(CircuitState state);

}

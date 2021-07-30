/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.instance;

import java.util.Collections;
import java.util.List;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.comp.AbstractComponentFactory;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.Pokable;
import com.cburch.LogisimFX.tools.key.KeyConfigurator;
import com.cburch.LogisimFX.util.UnmodifiableList;
import com.cburch.LogisimFX.LogisimVersion;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.logisim.gui.log.Loggable;

import javafx.beans.binding.StringBinding;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Represents a category of components that appear in a circuit. This class
 * and <code>Component</code> share the same sort of relationship as the
 * relation between <em>classes</em> and <em>instances</em> in Java. Normally,
 * there is only one ComponentFactory created for any particular category.
 */
public abstract class InstanceFactory extends AbstractComponentFactory {

	private String name;
	private StringBinding displayName;
	private StringBinding defaultToolTip;
	private ImageView icon;
	private Attribute<?>[] attrs;
	private Object[] defaults;
	private AttributeSet defaultSet;
	private Bounds bounds;
	private List<Port> portList;
	private Attribute<Direction> facingAttribute;
	private Boolean shouldSnap;
	private KeyConfigurator keyConfigurator;
	private Class<? extends InstancePoker> pokerClass;
	private Class<? extends InstanceLogger> loggerClass;

	public InstanceFactory(String name) {
		this(name, LC.createStringBinding(name));
	}

	public InstanceFactory(String name, StringBinding displayName) {
		this.name = name;
		this.displayName = displayName;
		this.icon = null;
		this.attrs = null;
		this.defaults = null;
		this.bounds = Bounds.EMPTY_BOUNDS;
		this.portList = Collections.emptyList();
		this.keyConfigurator = null;
		this.facingAttribute = null;
		this.shouldSnap = Boolean.TRUE;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public StringBinding getDisplayName() {
		return displayName == null ? LC.createRawStringBinding(getName()) : getDisplayGetter();
	}

	@Override
	public StringBinding getDisplayGetter() {
		return displayName;
	}

	public void setIcon(String value) {
		icon = IconsManager.getIcon(value);
	}

	@Override
	public ImageView getIcon(){
		return icon;
	}

	@Override
	public final Component createComponent(Location loc, AttributeSet attrs) {
		InstanceComponent ret = new InstanceComponent(this, loc, attrs);
		configureNewInstance(ret.getInstance());
		return ret;
	}

	public void setOffsetBounds(Bounds value) {
		bounds = value;
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {
		Bounds ret = bounds;
		if (ret == null) {
			throw new RuntimeException("offset bounds unknown: "
					+ "use setOffsetBounds or override getOffsetBounds");
		}
		return ret;
	}

	public boolean contains(Location loc, AttributeSet attrs) {
		Bounds bds = getOffsetBounds(attrs);
		if (bds == null) return false;
		return bds.contains(loc, 1);
	}


	public Attribute<Direction> getFacingAttribute() {
		return facingAttribute;
	}

	public void setFacingAttribute(Attribute<Direction> value) {
		facingAttribute = value;
	}

	public KeyConfigurator getKeyConfigurator() {
		return keyConfigurator;
	}

	public void setKeyConfigurator(KeyConfigurator value) {
		keyConfigurator = value;
	}

	public void setAttributes(Attribute<?>[] attrs, Object[] defaults) {
		this.attrs = attrs;
		this.defaults = defaults;
	}

	@Override
	public AttributeSet createAttributeSet() {
		Attribute<?>[] as = attrs;
		AttributeSet ret = as == null ? AttributeSets.EMPTY : AttributeSets.fixedSet(as, defaults);
		return ret;
	}

	@Override
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {
		Attribute<?>[] as = attrs;
		if (as != null) {
			for (int i = 0; i < as.length; i++) {
				if (as[i] == attr) {
					return defaults[i];
				}
			}
			return null;
		} else {
			AttributeSet dfltSet = defaultSet;
			if (dfltSet == null) {
				dfltSet = createAttributeSet();
				defaultSet = dfltSet;
			}
			return dfltSet.getValue(attr);
		}
	}


	public void setPorts(Port[] ports) {
		portList = new UnmodifiableList<Port>(ports);
	}

	public void setPorts(List<Port> ports) {
		portList = Collections.unmodifiableList(ports);
	}

	public List<Port> getPorts() {
		return portList;
	}

	public void setDefaultToolTip(StringBinding value) {
		defaultToolTip = value;
	}

	public StringBinding getDefaultToolTip() {
		return defaultToolTip;
	}

	public void setInstancePoker(Class<? extends InstancePoker> pokerClass) {
		if (isClassOk(pokerClass, InstancePoker.class)) {
			this.pokerClass = pokerClass;
		}
	}

	public void setInstanceLogger(Class<? extends InstanceLogger> loggerClass) {
		if (isClassOk(loggerClass, InstanceLogger.class)) {
			this.loggerClass = loggerClass;
		}
	}

	public void setShouldSnap(boolean value) {
		shouldSnap = Boolean.valueOf(value);
	}

	private boolean isClassOk(Class<?> sub, Class<?> sup) {
		boolean isSub = sup.isAssignableFrom(sub);
		if (!isSub) {
			System.err.println(sub.getName() + " must be a subclass of " + sup.getName()); //OK
			return false;
		}
		try {
			sub.getConstructor(new Class[0]);
			return true;
		} catch (SecurityException e) {
			System.err.println(sub.getName() + " needs its no-args constructor to be public"); //OK
		} catch (NoSuchMethodException e) {
			System.err.println(sub.getName() + " is missing a no-arguments constructor"); //OK
		}
		return true;
	}

	@Override
	public final Object getFeature(Object key, AttributeSet attrs) {
		if (key == FACING_ATTRIBUTE_KEY) return facingAttribute;
		if (key == KeyConfigurator.class) return keyConfigurator;
		if (key == SHOULD_SNAP) return shouldSnap;
		return super.getFeature(key, attrs);
	}

	@Override
	public final void drawGhost(ComponentDrawContext context, Color color,
                                int x, int y, AttributeSet attrs) {
		InstancePainter painter = context.getInstancePainter();
		Graphics g = painter.getGraphics();
		g.setColor(color);
		g.c.translate(x, y);
		painter.setFactory(this, attrs);
		paintGhost(painter);
		g.c.translate(-x, -y);
		if (painter.getFactory() == null) {
			super.drawGhost(context, color, x, y, attrs);
		}
		g.toDefault();

	}

	public void paintGhost(InstancePainter painter) {
		painter.setFactory(null, null);
	}

	public abstract void paintInstance(InstancePainter painter);
	public abstract void propagate(InstanceState state);

	// event methods
	protected void configureNewInstance(Instance instance) { }
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) { }
	protected Object getInstanceFeature(Instance instance, Object key) {
		if (key == Pokable.class && pokerClass != null) {
			return new InstancePokerAdapter(instance.getComponent(), pokerClass);
		} else if (key == Loggable.class && loggerClass != null) {
			return new InstanceLoggerAdapter(instance.getComponent(), loggerClass);
		} else {
			return null;
		}
	}

	public InstanceState createInstanceState(CircuitState state, Instance instance) {
		return new InstanceStateImpl(state, instance.getComponent());
	}

	public final InstanceState createInstanceState(CircuitState state, Component comp) {
		return createInstanceState(state, ((InstanceComponent) comp).getInstance());
	}
}

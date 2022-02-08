/* Copyright (c) 2010, Carl Burch.
 *  Copyright (c) 2022, Pplos Studio
 *  License information is located in the Launch file */

package LogisimFX.circuit;

import LogisimFX.comp.Component;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.std.wiring.Pin;
import LogisimFX.tools.MenuExtender;
import LogisimFX.util.GraphicsUtil;
import com.sun.javafx.tk.FontMetrics;
import javafx.beans.binding.StringBinding;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

public class SubcircuitFactory extends InstanceFactory {

	private Circuit source;

	public SubcircuitFactory(Circuit source) {
		super("", null);
		this.source = source;
		setFacingAttribute(StdAttr.FACING);
		setInstancePoker(SubcircuitPoker.class);
		setIcon("subcirc.gif");
	}

	public Circuit getSubcircuit() {
		return source;
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public StringBinding getDisplayGetter() {
		return LC.castToBind(source.getName());
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {
		Direction facing = attrs.getValue(StdAttr.FACING);
		Direction defaultFacing = source.getAppearance().getFacing();
		Bounds bds = source.getAppearance().getOffsetBounds();
		return bds.rotate(defaultFacing, facing, 0, 0);
	}

	@Override
	public AttributeSet createAttributeSet() {
		return new CircuitAttributes(source);
	}

	//
	// methods for configuring instances
	//
	@Override
	public void configureNewInstance(Instance instance) {
		CircuitAttributes attrs = (CircuitAttributes) instance.getAttributeSet();
		attrs.setSubcircuit(instance);

		instance.addAttributeListener();
		computePorts(instance);
		// configureLabel(instance); already done in computePorts
	}

	@Override
	public void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
		if (attr == StdAttr.FACING) {
			computePorts(instance);
		} else if (attr == CircuitAttributes.LABEL_LOCATION_ATTR) {
			configureLabel(instance);
		}
	}

	@Override
	public Object getInstanceFeature(Instance instance, Object key) {
		if (key == MenuExtender.class) return new ContextMenuManager.CircuitComponentContextMenu(instance);
		return super.getInstanceFeature(instance, key);
	}

	void computePorts(Instance instance) {
		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		Map<Location, Instance> portLocs = source.getAppearance().getPortOffsets(facing);
		Port[] ports = new Port[portLocs.size()];
		Instance[] pins = new Instance[portLocs.size()];
		int i = -1;
		for (Map.Entry<Location, Instance> portLoc : portLocs.entrySet()) {
			i++;
			Location loc = portLoc.getKey();
			Instance pin = portLoc.getValue();
			String type = Pin.FACTORY.isInputPin(pin) ? Port.INPUT : Port.OUTPUT;
			BitWidth width = pin.getAttributeValue(StdAttr.WIDTH);
			ports[i] = new Port(loc.getX(), loc.getY(), type, width);
			pins[i] = pin;

			String label = pin.getAttributeValue(StdAttr.LABEL);
			if (label != null && label.length() > 0) {
				ports[i].setToolTip(new StringBinding() {
					@Override
					protected String computeValue() {
						return label;
					}
				});
			}
		}

		CircuitAttributes attrs = (CircuitAttributes) instance.getAttributeSet();
		attrs.setPinInstances(pins);
		instance.setPorts(ports);
		instance.recomputeBounds();
		configureLabel(instance); // since this affects the circuit's bounds
	}

	private void configureLabel(Instance instance) {
		Bounds bds = instance.getBounds();
		Direction loc = instance.getAttributeValue(CircuitAttributes.LABEL_LOCATION_ATTR);

		int x = bds.getX() + bds.getWidth() / 2;
		int y = bds.getY() + bds.getHeight() / 2;
		int ha = GraphicsUtil.H_CENTER;
		int va = GraphicsUtil.V_CENTER;
		if (loc == Direction.EAST) {
			x = bds.getX() + bds.getWidth() + 2;
			ha = GraphicsUtil.H_LEFT;
		} else if (loc == Direction.WEST) {
			x = bds.getX() - 2;
			ha = GraphicsUtil.H_RIGHT;
		} else if (loc == Direction.SOUTH) {
			y = bds.getY() + bds.getHeight() + 2;
			va = GraphicsUtil.V_TOP;
		} else {
			y = bds.getY() - 2;
			va = GraphicsUtil.V_BASELINE;
		}
		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT, x, y, ha, va);
	}

	//
	// propagation-oriented methods
	//
	public CircuitState getSubstate(CircuitState superState, Instance instance) {
		return getSubstate(createInstanceState(superState, instance));
	}

	public CircuitState getSubstate(CircuitState superState, Component comp) {
		return getSubstate(createInstanceState(superState, comp));
	}

	private CircuitState getSubstate(InstanceState instanceState) {
		CircuitState subState = (CircuitState) instanceState.getData();
		if (subState == null) {
			subState = new CircuitState(instanceState.getProject(), source);
			instanceState.setData(subState);
			instanceState.fireInvalidated();
		}
		return subState;
	}

	@Override
	public void propagate(InstanceState superState) {
		CircuitState subState = getSubstate(superState);

		CircuitAttributes attrs = (CircuitAttributes) superState.getAttributeSet();
		Instance[] pins = attrs.getPinInstances();
		for (int i = 0; i < pins.length; i++) {
			Instance pin = pins[i];
			InstanceState pinState = subState.getInstanceState(pin);
			if (Pin.FACTORY.isInputPin(pin)) {
				Value newVal = superState.getPort(i);
				Value oldVal = Pin.FACTORY.getValue(pinState);
				if (!newVal.equals(oldVal)) {
					Pin.FACTORY.setValue(pinState, newVal);
					Pin.FACTORY.propagate(pinState);
				}
			} else { // it is output-only
				Value val = pinState.getPort(0);
				superState.setPort(i, val, 1);
			}
		}
	}

	//
	// user interface features
	//
	@Override
	public void paintGhost(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Color fg =  g.getColor();
		int v = (int)fg.getRed() + (int)fg.getGreen() + (int)fg.getBlue();

		//Composite oldComposite = null;
		//if (g instanceof Graphics2D && v > 50) {
			//oldComposite = ((Graphics2D) g).getComposite();
			//Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			//((Graphics2D) g).setComposite(c);
		//}
		paintBase(painter, g);
		//if (oldComposite != null) {
		//	((Graphics2D) g).setComposite(oldComposite);
		//}


	}

	@Override
	public void paintInstance(InstancePainter painter) {

		paintBase(painter, painter.getGraphics());
		painter.drawPorts();

	}

	private void paintBase(InstancePainter painter, Graphics g) {

		CircuitAttributes attrs = (CircuitAttributes) painter.getAttributeSet();
		Direction facing = attrs.getFacing();
		Direction defaultFacing = source.getAppearance().getFacing();
		Location loc = painter.getLocation();
		g.c.translate(loc.getX(), loc.getY());
		source.getAppearance().paintSubcircuit(g, facing);
		drawCircuitLabel(painter, getOffsetBounds(attrs), facing, defaultFacing);
		g.c.translate(-loc.getX(), -loc.getY());
		painter.drawLabel();
		g.toDefault();

	}

	private void drawCircuitLabel(InstancePainter painter, Bounds bds,
			Direction facing, Direction defaultFacing) {

		AttributeSet staticAttrs = source.getStaticAttributes();
		String label = staticAttrs.getValue(CircuitAttributes.CIRCUIT_LABEL_ATTR);
		if (label != null && !label.equals("")) {
			Direction up = staticAttrs.getValue(CircuitAttributes.CIRCUIT_LABEL_FACING_ATTR);
			Font font = staticAttrs.getValue(CircuitAttributes.CIRCUIT_LABEL_FONT_ATTR);

			int back = label.indexOf('\\');
			int lines = 1;
			boolean backs = false;
			while (back >= 0 && back <= label.length() - 2) {
				char c = label.charAt(back + 1);
				if (c == 'n') lines++;
				else if (c == '\\') backs = true;
				back = label.indexOf('\\', back + 2);
			}
			
			int x = bds.getX() + bds.getWidth() / 2;
			int y = bds.getY() + bds.getHeight() / 2;
			Graphics g = painter.getGraphics();
			double angle = 180 / 2 - (up.toDegrees() - defaultFacing.toDegrees()) - facing.toDegrees();
			if (Math.abs(angle) > 0.01) {
				g.c.translate(x,y);
				g.c.rotate(angle);
				g.c.translate(-x,-y);
			}
			g.setFont(font);
			if (lines == 1 && !backs) {
				GraphicsUtil.drawCenteredText(g, label, x, y);
			} else {
				FontMetrics fm = g.getFontMetrics();
				int height = (int)fm.getAscent();
				y = y - (height * lines - (int)fm.getLeading()) / 2 + (int)fm.getAscent();
				back = label.indexOf('\\');
				while (back >= 0 && back <= label.length() - 2) {
					char c = label.charAt(back + 1);
					if (c == 'n') {
						String line = label.substring(0, back);
						GraphicsUtil.drawText(g, line, x, y,
								GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
						y += height;
						label = label.substring(back + 2);
						back = label.indexOf('\\');
					} else if (c == '\\') {
						label = label.substring(0, back) + label.substring(back + 1);
						back = label.indexOf('\\', back + 1);
					} else {
						back = label.indexOf('\\', back + 2);
					}
				}
				GraphicsUtil.drawText(g, label, x, y,
						GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
			}

			g.c.translate(x,y);
			g.c.rotate(-angle);
			g.c.translate(-x,-y);

			g.toDefault();

		}

	}

	/* TODO
	public String getToolTip(ComponentUserEvent e) {
		return StringUtil.format(Strings.get("subcircuitCircuitTip"), source.getDisplayName());
	} */

}

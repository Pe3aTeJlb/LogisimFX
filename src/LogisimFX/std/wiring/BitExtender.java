/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.wiring;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.util.GraphicsUtil;
import com.sun.javafx.tk.FontMetrics;

public class BitExtender extends InstanceFactory {

	private static final Attribute<BitWidth> ATTR_IN_WIDTH
		= Attributes.forBitWidth("in_width", LC.createStringBinding("extenderInAttr"));
	private static final Attribute<BitWidth> ATTR_OUT_WIDTH
		= Attributes.forBitWidth("out_width", LC.createStringBinding("extenderOutAttr"));
	private static final Attribute<AttributeOption> ATTR_TYPE
		= Attributes.forOption("type", LC.createStringBinding("extenderTypeAttr"),
			new AttributeOption[] {
				new AttributeOption("zero", "zero", LC.createStringBinding("extenderZeroType")),
				new AttributeOption("one", "one", LC.createStringBinding("extenderOneType")),
				new AttributeOption("sign", "sign", LC.createStringBinding("extenderSignType")),
				new AttributeOption("input", "input", LC.createStringBinding("extenderInputType")),
			});
	
	public static final BitExtender FACTORY = new BitExtender();

	public BitExtender() {

		super("Bit Extender", LC.createStringBinding("extenderComponent"));
		setIcon("extender.gif");
		setAttributes(new Attribute[] {
				ATTR_IN_WIDTH, ATTR_OUT_WIDTH, ATTR_TYPE
			}, new Object[] {
				BitWidth.create(8), BitWidth.create(16), ATTR_TYPE.parse("zero")
			});
		setKeyConfigurator(JoinedConfigurator.create(
				new BitWidthConfigurator(ATTR_OUT_WIDTH),
				new BitWidthConfigurator(ATTR_IN_WIDTH, 1, Value.MAX_WIDTH, null)));
		setOffsetBounds(Bounds.create(-40, -20, 40, 40));

	}
	
	//
	// graphics methods
	//
	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		FontMetrics fm = painter.getFontMetrics();
		int asc = (int)fm.getAscent();

		painter.drawBounds();
		
		String s0;
		String type = getType(painter.getAttributeSet());
		if (type.equals("zero")) s0 = LC.get("extenderZeroLabel");
		else if (type.equals("one")) s0 = LC.get("extenderOneLabel");
		else if (type.equals("sign")) s0 = LC.get("extenderSignLabel");
		else if (type.equals("input")) s0 = LC.get("extenderInputLabel");
		else s0 = "???"; // should never happen
		String s1 = LC.get("extenderMainLabel");
		Bounds bds = painter.getBounds();
		int x = bds.getX() + bds.getWidth() / 2;
		int y0 = bds.getY() + (bds.getHeight() / 2 + asc) / 2;
		int y1 = bds.getY() + (3 * bds.getHeight() / 2 + asc) / 2;
		GraphicsUtil.drawText(g, s0, x, y0,
				GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
		GraphicsUtil.drawText(g, s1, x, y1,
				GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
		
		BitWidth w0 = painter.getAttributeValue(ATTR_OUT_WIDTH);
		BitWidth w1 = painter.getAttributeValue(ATTR_IN_WIDTH);
		painter.drawPort(0, "" + w0.getWidth(), Direction.WEST);
		painter.drawPort(1, "" + w1.getWidth(), Direction.EAST);
		if (type.equals("input")) painter.drawPort(2);

		g.toDefault();

	}
	
	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		configurePorts(instance);
		instance.addAttributeListener();

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == ATTR_TYPE) {
			configurePorts(instance);
			instance.fireInvalidated();
		} else {
			instance.fireInvalidated();
		}

	}
	
	private void configurePorts(Instance instance) {

		Port p0 = new Port(0, 0, Port.OUTPUT, ATTR_OUT_WIDTH);
		Port p1 = new Port(-40, 0, Port.INPUT, ATTR_IN_WIDTH);
		String type = getType(instance.getAttributeSet());
		if (type.equals("input")) {
			instance.setPorts(new Port[] { p0, p1, new Port(-20, -20, Port.INPUT, 1) });
		} else {
			instance.setPorts(new Port[] { p0, p1 });
		}

	}

	@Override
	public void propagate(InstanceState state) {

		Value in = state.getPort(1);
		BitWidth wout = state.getAttributeValue(ATTR_OUT_WIDTH);
		String type = getType(state.getAttributeSet());
		Value extend;
		if (type.equals("one")) {
			extend = Value.TRUE;
		} else if (type.equals("sign")) {
			int win = in.getWidth();
			extend = win > 0 ? in.get(win - 1) : Value.ERROR;
		} else if (type.equals("input")) {
			extend = state.getPort(2);
			if (extend.getWidth() != 1) extend = Value.ERROR;
		} else {
			extend = Value.FALSE;
		}
		
		Value out = in.extendWidth(wout.getWidth(), extend);
		state.setPort(0, out, 1);

	}

	
	private String getType(AttributeSet attrs) {

		AttributeOption topt = attrs.getValue(ATTR_TYPE);
		return (String) topt.getValue();

	}

}
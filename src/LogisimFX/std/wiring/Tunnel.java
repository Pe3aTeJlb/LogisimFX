/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.wiring;

import LogisimFX.OldFontmetrics;
import LogisimFX.comp.TextField;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.util.GraphicsUtil;
import com.sun.javafx.tk.FontMetrics;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.paint.Color;

public class Tunnel extends InstanceFactory {

	public static final Tunnel FACTORY = new Tunnel();

	static final int MARGIN = 3;
	static final int ARROW_MARGIN = 5;
	static final int ARROW_DEPTH = 4;
	static final int ARROW_MIN_WIDTH = 16;
	static final int ARROW_MAX_WIDTH = 20;

	public Tunnel() {

		super("Tunnel", LC.createStringBinding("tunnelComponent"));
		setIcon("tunnel.gif");
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));

	}

	@Override
	public AttributeSet createAttributeSet() {
		return new TunnelAttributes();
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrsBase) {

		TunnelAttributes attrs = (TunnelAttributes) attrsBase;
		Bounds bds = attrs.getOffsetBounds();
		if (bds != null) {
			return bds;
		} else {
			int ht = (int)attrs.getFont().getSize();
			int wd = ht * attrs.getLabel().length() / 2;
			bds = computeBounds(attrs, wd, ht, null, "");
			attrs.setOffsetBounds(bds);
			return bds;
		}

	}
	
	//
	// graphics methods
	//
	@Override
	public void paintGhost(InstancePainter painter) {

		TunnelAttributes attrs = (TunnelAttributes) painter.getAttributeSet();
		Direction facing = attrs.getFacing();
		String label = attrs.getLabel();
		
		Graphics g = painter.getGraphics();
		g.setFont(attrs.getFont());
		FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont());
		Bounds bds = computeBounds(attrs, OldFontmetrics.computeStringWidth(fm,label),
				(int)fm.getAscent() + (int)fm.getDescent(), g, label);
		if (attrs.setOffsetBounds(bds)) {
			Instance instance = painter.getInstance();
			if (instance != null) instance.recomputeBounds();
		}
		
		int x0 = bds.getX();
		int y0 = bds.getY();
		int x1 = x0 + bds.getWidth();
		int y1 = y0 + bds.getHeight();
		int mw = ARROW_MAX_WIDTH / 2;
		double[] xp;
		double[] yp;
		if (facing == Direction.NORTH) {
			int yb = y0 + ARROW_DEPTH;
			if (x1 - x0 <= ARROW_MAX_WIDTH) {
				xp = new double[] { x0, 0,  x1, x1, x0 };
				yp = new double[] { yb, y0, yb, y1, y1 };
			} else {
				xp = new double[] { x0, -mw, 0,  mw, x1, x1, x0 };
				yp = new double[] { yb, yb,  y0, yb, yb, y1, y1 };
			}
		} else if (facing == Direction.SOUTH) {
			int yb = y1 - ARROW_DEPTH;
			if (x1 - x0 <= ARROW_MAX_WIDTH) {
				xp = new double[] { x0, x1, x1, 0,  x0 };
				yp = new double[] { y0, y0, yb, y1, yb };
			} else {
				xp = new double[] { x0, x1, x1, mw, 0,  -mw, x0 };
				yp = new double[] { y0, y0, yb, yb, y1, yb,  yb };
			}
		} else if (facing == Direction.EAST) {
			int xb = x1 - ARROW_DEPTH;
			if (y1 - y0 <= ARROW_MAX_WIDTH) {
				xp = new double[] { x0, xb, x1, xb, x0 };
				yp = new double[] { y0, y0, 0,  y1, y1 };
			} else {
				xp = new double[] { x0, xb, xb,  x1, xb, xb, x0 };
				yp = new double[] { y0, y0, -mw, 0,  mw,  y1, y1 };
			}
		} else {
			int xb = x0 + ARROW_DEPTH;
			if (y1 - y0 <= ARROW_MAX_WIDTH) {
				xp = new double[] { xb, x1, x1, xb, x0 };
				yp = new double[] { y0, y0, y1, y1, 0  };
			} else {
				xp = new double[] { xb, x1, x1, xb, xb, x0, xb  };
				yp = new double[] { y0, y0, y1, y1, mw, 0,  -mw };
			}
		}
		g.setLineWidth(2);
		g.c.strokePolygon(xp, yp, xp.length);
		g.toDefault();

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		Graphics g = painter.getGraphics();
		g.c.translate(x, y);
		g.setColor(Color.BLACK);
		paintGhost(painter);
		g.c.translate(-x, -y);
		painter.drawPorts();
		g.toDefault();

	}
	
	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		instance.setPorts(new Port[] {
				new Port(0, 0, Port.INOUT, StdAttr.WIDTH)
			});
		configureLabel(instance);

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING) {
			configureLabel(instance);
			instance.recomputeBounds();
		} else if (attr == StdAttr.LABEL || attr == StdAttr.LABEL_FONT) {
			instance.recomputeBounds();
		}

	}
	
	@Override
	public void propagate(InstanceState state) {
		 // nothing to do - handled by circuit
	}
	
	//
	// private methods
	//
	private void configureLabel(Instance instance) {

		TunnelAttributes attrs = (TunnelAttributes) instance.getAttributeSet();
		Location loc = instance.getLocation();
		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
				loc.getX() + attrs.getLabelX(), loc.getY() + attrs.getLabelY(),
				attrs.getLabelHAlign(), attrs.getLabelVAlign());

	}

	private Bounds computeBounds(TunnelAttributes attrs, int textWidth,
								 int textHeight, Graphics g, String label) {

		int x = attrs.getLabelX();
		int y = attrs.getLabelY();
		int halign = attrs.getLabelHAlign();
		int valign = attrs.getLabelVAlign();
		
		int minDim = ARROW_MIN_WIDTH - 2 * MARGIN;
		int bw = Math.max(minDim, textWidth);
		int bh = Math.max(minDim, textHeight);
		int bx;
		int by;
		switch (halign) {
		case TextField.H_LEFT: bx = x; break;
		case TextField.H_RIGHT: bx = x - bw; break;
		default: bx = x - (bw / 2);
		}
		switch (valign) {
		case TextField.V_TOP: by = y; break;
		case TextField.V_BOTTOM: by = y - bh; break;
		default: by = y - (bh / 2);
		}
		
		if (g != null) {
			GraphicsUtil.drawText(g, label, bx + bw / 2, by + bh / 2,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_CENTER_OVERALL);
		}

		return Bounds.create(bx, by, bw, bh).expand(MARGIN).add(0, 0);

	}


	@Override
	public boolean isHDLSupportedComponent(AttributeSet attrs) {
		return true;
	}

}
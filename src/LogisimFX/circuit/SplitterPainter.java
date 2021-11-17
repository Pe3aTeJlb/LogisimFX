/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.circuit;

import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Direction;
import LogisimFX.data.Location;
import LogisimFX.data.Value;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.util.GraphicsUtil;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

class SplitterPainter {

	private static final int SPINE_WIDTH = Wire.WIDTH + 2;
	private static final int SPINE_DOT = Wire.WIDTH + 4;
	
	static void drawLines(ComponentDrawContext context,
			SplitterAttributes attrs, Location origin) {

		boolean showState = context.getShowState();
		CircuitState state = showState ? context.getCircuitState() : null;
		if (state == null) showState = false;

		SplitterParameters parms = attrs.getParameters();
		int x0 = origin.getX();
		int y0 = origin.getY();
		int x = x0 + parms.getEnd0X();
		int y = y0 + parms.getEnd0Y();
		int dx = parms.getEndToEndDeltaX();
		int dy = parms.getEndToEndDeltaY();
		int dxEndSpine = parms.getEndToSpineDeltaX();
		int dyEndSpine = parms.getEndToSpineDeltaY();
		
		Graphics g = context.getGraphics();
		Paint oldColor = g.getPaint();
		g.setLineWidth(Wire.WIDTH);

		for (int i = 0, n = attrs.fanout; i < n; i++) {
			if (showState) {
				Value val = state.getValue(Location.create(x, y));
				g.setColor(val.getColor());
			}
			g.c.strokeLine(x, y, x + dxEndSpine, y + dyEndSpine);
			x += dx;
			y += dy;
		}
		g.setLineWidth(SPINE_WIDTH);
		g.setColor(oldColor);
		int spine0x = x0 + parms.getSpine0X();
		int spine0y = y0 + parms.getSpine0Y();
		int spine1x = x0 + parms.getSpine1X();
		int spine1y = y0 + parms.getSpine1Y();
		if (spine0x == spine1x && spine0y == spine1y) { // centered
			int fanout = attrs.fanout;
			spine0x = x0 + parms.getEnd0X() + parms.getEndToSpineDeltaX();
			spine0y = y0 + parms.getEnd0Y() + parms.getEndToSpineDeltaY();
			spine1x = spine0x + (fanout - 1) * parms.getEndToEndDeltaX();
			spine1y = spine0y + (fanout - 1) * parms.getEndToEndDeltaY();
			if (parms.getEndToEndDeltaX() == 0) { // vertical spine
				if (spine0y < spine1y) {
					spine0y++;
					spine1y--;
				} else {
					spine0y--;
					spine1y++;
				}
				g.c.strokeLine(x0 + parms.getSpine1X() / 4, y0, spine0x, y0);
			} else {
				if (spine0x < spine1x) {
					spine0x++;
					spine1x--;
				} else {
					spine0x--;
					spine1x++;
				}
				g.c.strokeLine(x0, y0 + parms.getSpine1Y() / 4, x0, spine0y);
			}
			if (fanout <= 1) { // spine is empty
				int diam = SPINE_DOT;
				g.c.fillOval(spine0x - diam / 2, spine0y - diam / 2, diam, diam);
			} else {
				g.c.strokeLine(spine0x, spine0y, spine1x, spine1y);
			}
		} else {
			double[] xSpine = { spine0x, spine1x, x0 + parms.getSpine1X() / 4 };
			double[] ySpine = { spine0y, spine1y, y0 + parms.getSpine1Y() / 4 };
			g.c.strokePolyline(xSpine, ySpine, 3);
		}

		g.toDefault();

	}

	static void drawLabels(ComponentDrawContext context,
			SplitterAttributes attrs, Location origin) {

		// compute labels
		String[] ends = new String[attrs.fanout + 1];
		int curEnd = -1;
		int cur0 = 0;
		for (int i = 0, n = attrs.bit_end.length; i <= n; i++) {
			int bit = i == n ? -1 : attrs.bit_end[i];
			if (bit != curEnd) {
				int cur1 = i - 1;
				String toAdd;
				if (curEnd <= 0) {
					toAdd = null;
				} else if (cur0 == cur1) {
					toAdd = "" + cur0;
				} else {
					toAdd = cur0 + "-" + cur1;
				}
				if (toAdd != null) {
					String old = ends[curEnd];
					if (old == null) {
						ends[curEnd] = toAdd;
					} else {
						ends[curEnd] = old + "," + toAdd;
					}
				}
				curEnd = bit;
				cur0 = i;
			}
		}

		Graphics g = context.getGraphics();
		Font f = Font.font("System", FontWeight.THIN, FontPosture.REGULAR, 7);
		g.setFont(f);
		
		SplitterParameters parms = attrs.getParameters();
		int x = origin.getX() + parms.getEnd0X() + parms.getEndToSpineDeltaX();
		int y = origin.getY() + parms.getEnd0Y() + parms.getEndToSpineDeltaY();
		int dx = parms.getEndToEndDeltaX();
		int dy = parms.getEndToEndDeltaY();
		if (parms.getTextAngle() != 0) {
			g.rotate(180 / 2.0);
			int t;
			t = -x; x = y; y = t;
			t = -dx; dx = dy; dy = t;
		}
		int halign = parms.getTextHorzAlign();
		int valign = parms.getTextVertAlign();
		x += (halign == GraphicsUtil.H_RIGHT ? -1.5 : 1) * (SPINE_WIDTH / 2 + 3);
		y += valign == GraphicsUtil.V_TOP ? -5 : -9;
		for (int i = 0, n = attrs.fanout; i < n; i++) {
			String text = ends[i + 1];
			if (text != null) {
				GraphicsUtil.drawText(g, text, x, y, halign, valign);
			}
			x += dx;
			y += dy;
		}

		g.toDefault();

	}
	
	static void drawLegacy(ComponentDrawContext context, SplitterAttributes attrs,
			Location origin) {

		Graphics g = context.getGraphics();
		CircuitState state = context.getCircuitState();
		Direction facing = attrs.facing;
		int fanout = attrs.fanout;
		SplitterParameters parms = attrs.getParameters();

		g.setColor(Color.BLACK);
		int x0 = origin.getX();
		int y0 = origin.getY();
		int x1 = x0 + parms.getEnd0X();
		int y1 = y0 + parms.getEnd0Y();
		int dx = parms.getEndToEndDeltaX();
		int dy = parms.getEndToEndDeltaY();
		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			int ySpine = (y0 + y1) / 2;
			g.setLineWidth(Wire.WIDTH);
			g.c.strokeLine(x0, y0, x0, ySpine);
			int xi = x1;
			int yi = y1;
			for (int i = 1; i <= fanout; i++) {
				if (context.getShowState()) {
					g.setColor(state.getValueColor(Location.create(xi, yi)));
				}
				int xSpine = xi + (xi == x0 ? 0 : (xi < x0 ? 10 : -10));
				g.c.strokeLine(xi, yi, xSpine, ySpine);
				xi += dx;
				yi += dy;
			}
			if (fanout > 3) {
				g.setLineWidth(SPINE_WIDTH);
				g.setColor(Color.BLACK);
				g.c.strokeLine(x1 + dx, ySpine, x1 + (fanout - 2) * dx, ySpine);
			} else {
				g.setColor(Color.BLACK);
				g.c.fillOval(x0 - SPINE_DOT / 2, ySpine - SPINE_DOT / 2,
						SPINE_DOT, SPINE_DOT);
			}
		} else {
			int xSpine = (x0 + x1) / 2;
			g.setLineWidth(Wire.WIDTH);
			g.c.strokeLine(x0, y0, xSpine, y0);
			int xi = x1;
			int yi = y1;
			for (int i = 1; i <= fanout; i++) {
				if (context.getShowState()) {
					g.setColor(state.getValueColor(Location.create(xi, yi)));
				}
				int ySpine = yi + (yi == y0 ? 0 : (yi < y0 ? 10 : -10));
				g.c.strokeLine(xi, yi, xSpine, ySpine);
				xi += dx;
				yi += dy;
			}
			if (fanout >= 3) {
				g.setLineWidth(SPINE_WIDTH);
				g.setColor(Color.BLACK);
				g.c.strokeLine(xSpine, y1 + dy, xSpine, y1 + (fanout - 2) * dy);
			} else {
				g.setColor(Color.BLACK);
				g.c.fillOval(xSpine - SPINE_DOT / 2, y0 - SPINE_DOT / 2,
						SPINE_DOT, SPINE_DOT);
			}
		}

		g.toDefault();

	}

}
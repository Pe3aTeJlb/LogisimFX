/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.util;

import LogisimFX.OldFontmetrics;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import com.sun.javafx.tk.FontMetrics;

import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class GraphicsUtil {

	public static final int H_LEFT = -1;
	public static final int H_CENTER = 0;
	public static final int H_RIGHT = 1;
	public static final int V_TOP = -1;
	public static final int V_CENTER = 0;
	public static final int V_BASELINE = 1;
	public static final int V_BOTTOM = 2;
	public static final int V_CENTER_OVERALL = 3;

	static public void drawCenteredArc(Graphics g, int x, int y,
									   int r, int start, int dist) {

		g.c.strokeArc(x - r, y - r, 2 * r, 2 * r, start, dist, ArcType.OPEN);

	}

	static public Rectangle getTextBounds(Graphics g, Font font,
										  String text, int x, int y, int halign, int valign) {

		if (g == null) return new Rectangle(x, y, 0, 0);
		Font oldfont = g.getFont();
		if (font != null) g.setFont(font);
		Rectangle ret = getTextBounds(g, text, x, y, halign, valign);
		if (font != null) g.setFont(oldfont);
		return ret;

	}

	static public Rectangle getTextBounds(Graphics g, String text,
			int x, int y, int halign, int valign) {

		if (g == null) return new Rectangle(x, y, 0, 0);
		FontMetrics mets = g.getFontMetrics();
		int width = OldFontmetrics.computeStringWidth(mets,text);
		int ascent = (int)mets.getAscent();
		int descent = (int)mets.getDescent();
		int height = ascent + descent;
		
		Rectangle ret = new Rectangle(x, y, width, height);
		switch (halign) {
			case H_CENTER: ret.setX(ret.getX()-(width / 2)); break;
			case H_RIGHT:  ret.setX(ret.getX()-width); break;
			//case H_CENTER: ret.translate(-(width / 2), 0); break;
			//case H_RIGHT:  ret.translate(-width, 0); break;
			default: ;
		}

		switch (valign) {
			case V_TOP:      break;
			case V_CENTER:  ret.setY(ret.getY()-(ascent / 2)); break;
			case V_CENTER_OVERALL:  ret.setY(ret.getY()-(height / 2)); break;
			case V_BASELINE: ret.setY(ret.getY()-ascent); break;
			case V_BOTTOM:   ret.setY(ret.getY()-height); break;
			//case V_CENTER:   ret.translate(0, -(ascent / 2)); break;
			//case V_CENTER_OVERALL: ret.translate(0, -(height / 2)); break;
			//case V_BASELINE: ret.translate(0, -ascent); break;
			//case V_BOTTOM:   ret.translate(0, -height); break;
			default: ;
		}

		return ret;

	}

	static public Double[] getTextCoords(Graphics g, String text,
										  int x, int y, int halign, int valign) {

		Double[] xy = new Double[]{(double)x,(double)y};
		if (g == null) return xy;

		FontMetrics mets = g.getFontMetrics();
		int width = OldFontmetrics.computeStringWidth(mets,text);
		int ascent = (int)mets.getAscent();
		int descent = (int)mets.getDescent();
		int height = ascent + descent;

		switch (halign) {
			case H_CENTER: xy[0] = (xy[0]-(width / 2)); break;
			case H_RIGHT:  xy[0] = (xy[0]-width); break;
			//case H_CENTER: ret.translate(-(width / 2), 0); break;
			//case H_RIGHT:  ret.translate(-width, 0); break;
			default: ;
		}

		switch (valign) {
			case V_TOP:      break;
			case V_CENTER:  xy[1]=(xy[1]-(ascent / 2)); break;
			case V_CENTER_OVERALL:  xy[1]=(xy[1]-(height / 2)); break;
			case V_BASELINE: xy[1]=(xy[1]-ascent); break;
			case V_BOTTOM:   xy[1]=(xy[1]-height); break;
			//case V_CENTER:   ret.translate(0, -(ascent / 2)); break;
			//case V_CENTER_OVERALL: ret.translate(0, -(height / 2)); break;
			//case V_BASELINE: ret.translate(0, -ascent); break;
			//case V_BOTTOM:   ret.translate(0, -height); break;
			default: ;
		}

		return xy;

	}

	static public void drawText(Graphics g, Font font,
			String text, int x, int y, int halign, int valign) {

		Font oldfont = g.getFont();
		if (font != null) g.setFont(font);
		drawText(g, text, x, y, halign, valign);
		if (font != null) g.setFont(oldfont);

	}

	static public void drawText(Graphics g, String text,
			int x, int y, int halign, int valign) {

		if (text.length() == 0) return;
		//Rectangle bd = getTextBounds(g, text, x, y, halign, valign);
		Double[] xy = getTextCoords(g, text, x, y, halign, valign);
		double buf = g.getLineWidth();
		g.setLineWidth(1);
		//g.c.fillText(text, bd.getX(), bd.getY() + g.getFontMetrics().getAscent());
		g.c.fillText(text, xy[0], xy[1] + g.getFontMetrics().getAscent());
		g.setLineWidth(buf);


	}

	static public void drawCenteredText(Graphics g, String text,
			int x, int y) {

		drawText(g, text, x, y, H_CENTER, V_CENTER);

	}

	static public void drawArrow(Graphics g, int x0, int y0, int x1, int y1,
			int headLength, int headAngle) {

		double offs = headAngle * Math.PI / 180.0;
		double angle = Math.atan2(y0 - y1, x0 - x1);
		double[] xs = { x1 + (int) (headLength * Math.cos(angle + offs)), x1,
			x1 + (int) (headLength * Math.cos(angle - offs)) };
		double[] ys = { y1 + (int) (headLength * Math.sin(angle + offs)), y1,
			y1 + (int) (headLength * Math.sin(angle - offs)) };
		g.c.strokeLine(x0, y0, x1, y1);
		g.c.strokePolyline(xs, ys, 3);

	}

}

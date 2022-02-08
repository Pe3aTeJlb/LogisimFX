/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.shapes;

import LogisimFX.draw.model.AbstractCanvasObject;
import LogisimFX.draw.model.Handle;
import LogisimFX.data.Location;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SvgCreator {

	private SvgCreator() { }
	
	public static Element createRectangle(Document doc, Rectangle rect) {
		return createRectangular(doc, rect);
	}

	public static Element createRoundRectangle(Document doc,
			RoundRectangle rrect) {

		Element elt = createRectangular(doc, rrect);
		int r = rrect.getValue(DrawAttr.CORNER_RADIUS).intValue();
		elt.setAttribute("rx", "" + r);
		elt.setAttribute("ry", "" + r);

		return elt;

	}

	private static Element createRectangular(Document doc, Rectangular rect) {

		Element elt = doc.createElement("rect");
		elt.setAttribute("x", "" + rect.getX());
		elt.setAttribute("y", "" + rect.getY());
		elt.setAttribute("width", "" + rect.getWidth());
		elt.setAttribute("height", "" + rect.getHeight());
		populateFill(elt, rect);

		return elt;

	}

	public static Element createOval(Document doc, Oval oval) {

		double x = oval.getX();
		double y = oval.getY();
		double width = oval.getWidth();
		double height = oval.getHeight();
		Element elt = doc.createElement("ellipse");
		elt.setAttribute("cx", "" + (x + width / 2));
		elt.setAttribute("cy", "" + (y + height / 2));
		elt.setAttribute("rx", "" + (width / 2));
		elt.setAttribute("ry", "" + (height / 2));
		populateFill(elt, oval);

		return elt;

	}

	public static Element createLine(Document doc, Line line) {

		Element elt = doc.createElement("line");
		Location v1 = line.getEnd0();
		Location v2 = line.getEnd1();
		elt.setAttribute("x1", "" + v1.getX());
		elt.setAttribute("y1", "" + v1.getY());
		elt.setAttribute("x2", "" + v2.getX());
		elt.setAttribute("y2", "" + v2.getY());
		populateStroke(elt, line);

		return elt;

	}

	public static Element createCurve(Document doc, Curve curve) {

		Element elt = doc.createElement("path");
		Location e0 = curve.getEnd0();
		Location e1 = curve.getEnd1();
		Location ct = curve.getControl();
		elt.setAttribute("d", "M" + e0.getX() + "," + e0.getY()
				+ " Q" + ct.getX() + "," + ct.getY()
				+ " " + e1.getX() + "," + e1.getY());
		populateFill(elt, curve);

		return elt;

	}

	public static Element createPoly(Document doc, Poly poly) {

		Element elt;
		if (poly.isClosed()) {
			elt = doc.createElement("polygon");
		} else {
			elt = doc.createElement("polyline");
		}

		StringBuilder points = new StringBuilder();
		boolean first = true;
		for (Handle h : poly.getHandles(null)) {
			if (!first) points.append(" ");
			points.append(h.getX() + "," + h.getY());
			first = false;
		}
		elt.setAttribute("points", points.toString());

		populateFill(elt, poly);

		return elt;

	}

	public static Element createText(Document doc, Text text) {

		Element elt = doc.createElement("text");
		Location loc = text.getLocation();
		Font font = text.getValue(DrawAttr.FONT);
		Color fill = text.getValue(DrawAttr.FILL_COLOR);
		Object halign = text.getValue(DrawAttr.ALIGNMENT);
		elt.setAttribute("x", "" + loc.getX());
		elt.setAttribute("y", "" + loc.getY());
		if (!colorMatches(fill, Color.BLACK)) {
			elt.setAttribute("fill", getColorString(fill));
		}
		if (showOpacity(fill)) {
			elt.setAttribute("fill-opacity", getOpacityString(fill));
		}
		elt.setAttribute("font-family", font.getFamily());
		elt.setAttribute("font-size", "" + (int)font.getSize());

		if (font.getStyle().contains("ITALIC")) {
			elt.setAttribute("font-style", "italic");
		}
		if (font.getStyle().contains("BOLD")) {
			elt.setAttribute("font-weight", "bold");
		}
		if (halign == DrawAttr.ALIGN_LEFT) {
			elt.setAttribute("text-anchor", "start");
		} else if (halign == DrawAttr.ALIGN_RIGHT) {
			elt.setAttribute("text-anchor", "end");
		} else {
			elt.setAttribute("text-anchor", "middle");
		}
		elt.appendChild(doc.createTextNode(text.getText()));

		return elt;

	}

	private static void populateFill(Element elt, AbstractCanvasObject shape) {

		Object type = shape.getValue(DrawAttr.PAINT_TYPE);
		if (type == DrawAttr.PAINT_FILL) {
			elt.setAttribute("stroke", "none");
		} else {
			populateStroke(elt, shape);
		}
		if (type == DrawAttr.PAINT_STROKE) {
			elt.setAttribute("fill", "none");
		} else {
			Color fill = shape.getValue(DrawAttr.FILL_COLOR);
			if (colorMatches(fill, Color.BLACK)) {
				elt.removeAttribute("fill");
			} else {
				elt.setAttribute("fill", getColorString(fill));
			}
			if (showOpacity(fill)) {
				elt.setAttribute("fill-opacity", getOpacityString(fill));
			}
		}

	}

	private static void populateStroke(Element elt, AbstractCanvasObject shape) {

		Integer width = shape.getValue(DrawAttr.STROKE_WIDTH);
		if (width != null && width.intValue() != 1) {
			elt.setAttribute("stroke-width", width.toString());
		}
		Color stroke = shape.getValue(DrawAttr.STROKE_COLOR);
		elt.setAttribute("stroke", getColorString(stroke));
		if (showOpacity(stroke)) {
			elt.setAttribute("stroke-opacity", getOpacityString(stroke));
		}
		elt.setAttribute("fill", "none");

	}
	
	private static boolean colorMatches(Color a, Color b) {
		return a.getRed() == b.getRed() && a.getGreen() == b.getGreen()
			&& a.getBlue() == b.getBlue();
	}
	
	private static String getColorString(Color color) {
		return String.format("#%02x%02x%02x",
				(int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
	}
	
	private static boolean showOpacity(Color color) {
		return color.getOpacity() != 1;
	}
	
	private static String getOpacityString(Color color) {
		StringBuilder buff = new StringBuilder(String.format("%5.3f", color.getOpacity()));
		buff.replace(1,2,".");
		return buff.toString();
	}

}

/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import java.util.HashMap;

import com.cburch.LogisimFX.data.Direction;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;

import com.cburch.LogisimFX.util.GraphicsUtil;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class PainterShaped {

	private PainterShaped() { }

	private static HashMap<Integer,int[]> INPUT_LENGTHS = new HashMap<>();

	private static final Path2D PATH_NARROW;
	private static final Path2D PATH_MEDIUM;
	private static final Path2D PATH_WIDE;

	private static final Path2D SHIELD_NARROW;
	private static final Path2D SHIELD_MEDIUM;
	private static final Path2D SHIELD_WIDE;

	static {

		PATH_NARROW = new Path2D();
		PATH_NARROW.moveTo(0, 0);
		PATH_NARROW.quadTo(-10, -15, -30, -15);
		PATH_NARROW.quadTo(-22,   0, -30,  15);
		PATH_NARROW.quadTo(-10,  15,   0,   0);
		PATH_NARROW.closePath();

		PATH_MEDIUM = new Path2D();
		PATH_MEDIUM.moveTo(0, 0);
		PATH_MEDIUM.quadTo(-20, -25, -50, -25);
		PATH_MEDIUM.quadTo(-37,   0, -50,  25);
		PATH_MEDIUM.quadTo(-20,  25,   0,   0);
		PATH_MEDIUM.closePath();

		PATH_WIDE = new Path2D();
		PATH_WIDE.moveTo(0, 0);
		PATH_WIDE.quadTo(-25, -35, -70, -35);
		PATH_WIDE.quadTo(-50,   0, -70,  35);
		PATH_WIDE.quadTo(-25,  35,   0,   0);
		PATH_WIDE.closePath();

		SHIELD_NARROW = new Path2D();
		SHIELD_NARROW.moveTo(-30, -15);
		SHIELD_NARROW.quadTo(-22,   0, -30,  15);

		SHIELD_MEDIUM = new Path2D();
		SHIELD_MEDIUM.moveTo(-50, -25);
		SHIELD_MEDIUM.quadTo(-37,   0, -50,  25);

		SHIELD_WIDE = new Path2D();
		SHIELD_WIDE.moveTo(-70, -35);
		SHIELD_WIDE.quadTo(-50,   0, -70,  35);

	}

	private static void narrowPath(Graphics g){

		g.c.beginPath();
		g.c.moveTo(0, 0);
		g.c.quadraticCurveTo(-10, -15, -30, -15);
		g.c.quadraticCurveTo(-22,   0, -30,  15);
		g.c.quadraticCurveTo(-10,  15,   0,   0);
		g.c.stroke();

	}

	private static void mediumPath(Graphics g){

		g.c.beginPath();
		g.c.moveTo(0, 0);
		g.c.quadraticCurveTo(-20, -25, -50, -25);
		g.c.quadraticCurveTo(-37,   0, -50,  25);
		g.c.quadraticCurveTo(-20,  25,   0,   0);
		g.c.stroke();

	}

	private static void widePath(Graphics g){

		g.c.beginPath();
		g.c.moveTo(0, 0);
		g.c.quadraticCurveTo(-25, -35, -70, -35);
		g.c.quadraticCurveTo(-50,   0, -70,  35);
		g.c.quadraticCurveTo(-25,  35,   0,   0);
		g.c.stroke();

	}

	private static void narrowShield(Graphics g){

		g.c.beginPath();
		g.c.moveTo(-30, -15);
		g.c.quadraticCurveTo(-22,   0, -30,  15);
		g.c.stroke();

	}

	private static void mediumShield(Graphics g){

		g.c.beginPath();
		g.c.moveTo(-50, -25);
		g.c.quadraticCurveTo(-37,   0, -50,  25);
		g.c.stroke();

	}

	private static void wideShield(Graphics g){

		g.c.beginPath();
		g.c.moveTo(-70, -35);
		g.c.quadraticCurveTo(-50,   0, -70,  35);
		g.c.stroke();

	}

	static void paintAnd(InstancePainter painter, int width, int height) {

		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		double[] xp = new double[] { -width / 2, -width + 1, -width + 1, -width / 2 };
		double[] yp = new double[] { -width / 2, -width / 2, width / 2, width / 2 };
		//g.c.strokeArc(-width / 2, 0, width / 2,width / 2,-90, 180, ArcType.OPEN);
		GraphicsUtil.drawCenteredArc(g, -width / 2, 0, width / 2, -90, 180);

		g.c.strokePolyline(xp, yp, 4);
		if (height > width) {
			g.c.strokeLine(-width + 1, -height / 2, -width + 1, height / 2);
		}

	}

	static void paintOr(InstancePainter painter, int width, int height) {

		Graphics g = painter.getGraphics();
		g.setLineWidth(2);

		/*
		if (width < 40) {
			GraphicsUtil.drawCenteredArc(g, -30, -21, 36, -90, 53);
			GraphicsUtil.drawCenteredArc(g, -30,  21, 36, 90, -53);
			GraphicsUtil.drawCenteredArc(g,  -56, 0, 30, -30, 60);
		} else if (width < 60) {
			GraphicsUtil.drawCenteredArc(g, -50, -37, 62, -90, 53);
			GraphicsUtil.drawCenteredArc(g, -50,  37, 62, 90, -53);
			GraphicsUtil.drawCenteredArc(g,  -93, 0, 50, -30, 60);
		} else {
			GraphicsUtil.drawCenteredArc(g, -70, -50, 85, -90, 55);
			GraphicsUtil.drawCenteredArc(g, -70,  50, 85, 90, -55);
			GraphicsUtil.drawCenteredArc(g,  -130, 0, 70, -30, 60);
		}
		*/
		if (width < 40) {
			narrowPath(g);
		} else if (width < 60) {
			mediumPath(g);
		} else {
			widePath(g);
		}

		if (height > width) {
			paintShield(g, 0, width, height);
		}

		g.toDefault();

	}

	static void paintNot(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		if (painter.getAttributeValue(NotGate.ATTR_SIZE) == NotGate.SIZE_NARROW) {
			g.setLineWidth(2);
			double[] xp = new double[4];
			double[] yp = new double[4];
			xp[0] =  -6; yp[0] =  0;
			xp[1] = -19; yp[1] = -6;
			xp[2] = -19; yp[2] =  6;
			xp[3] =  -6; yp[3] =  0;
			g.c.strokePolyline(xp, yp, 4);
			g.c.strokeOval(-6, -3, 6, 6);
		} else {
			double[] xp = new double[4];
			double[] yp = new double[4];
			xp[0] = -10; yp[0] = 0;
			xp[1] = -29; yp[1] = -7;
			xp[2] = -29; yp[2] = 7;
			xp[3] = -10; yp[3] = 0;
			g.c.strokePolyline(xp, yp, 4);
			g.c.strokeOval(-9, -4, 9, 9);
		}

		g.toDefault();

	}

	static void paintXor(InstancePainter painter, int width, int height) {

		Graphics g = painter.getGraphics();
		paintOr(painter, width - 10, width - 10);
		paintShield(g, -10, width - 10, height);

		g.toDefault();

	}

	private static void paintShield(Graphics g, int xlate,
									int width, int height) {

		g.setLineWidth(2);

		g.c.translate(xlate,0);

		if (height <= width) {

			// no wings
			if (width < 40) {
				narrowShield(g);
			} else if (width < 60) {
				mediumShield(g);
			} else {
				wideShield(g);
			}

		} else {

			// we need to add wings
			int wingHeight = (height - width) / 2;
			int dx = Math.min(20, wingHeight / 4);

			g.c.beginPath();
			g.c.moveTo(-width, -height / 2);
			g.c.quadraticCurveTo(-width + dx, -(width + height) / 4, -width, -width / 2);
			g.c.stroke();

			/*
			g.setColor(Color.GREEN);
			g.c.fillOval(-width, -height / 2,3,3);
			g.setColor(Color.RED);
			g.c.fillOval(-width + dx, -(width + height) / 4,3,3);
			g.setColor(Color.CYAN);
			g.c.fillOval(-width, -width / 2,3,3);

			 */

			g.toDefaultColor();

			if (width < 40) {
				narrowShield(g);
			} else if (width < 60) {
				mediumShield(g);
			} else {
				wideShield(g);
			}

			g.c.beginPath();
			g.c.moveTo(-width, width / 2);
			g.c.quadraticCurveTo( -width + dx, (width + height) / 4, -width, height / 2);
			g.c.stroke();

			/*
			g.setColor(Color.GREEN);
			g.c.fillOval(-width, height / 2,3,3);
			g.setColor(Color.RED);
			g.c.fillOval(-width + dx, (width + height) / 4,3,3);
			g.setColor(Color.CYAN);
			g.c.fillOval(-width, width / 2,3,3);

			 */

		}

		g.c.translate(-xlate,0);

	}

	private static Path2D computeShield(Graphics g, int width, int height) {

		g.setLineWidth(2);

		Path2D base;

		if (width < 40) {
			base = SHIELD_NARROW;
		} else if (width < 60) {
			base = SHIELD_MEDIUM;
		} else {
			base = SHIELD_WIDE;
		}

		if (height <= width) { // no wings
			return base;
		} else {

			// we need to add wings
			int wingHeight = (height - width) / 2;
			int dx = Math.min(20, wingHeight / 4);

			Path2D path = new Path2D();

			path.moveTo(-width, -height / 2);

			path.quadTo(-width + dx, -(width + height) / 4, -width, -width / 2);

			path.append(base, true);

			g.c.moveTo(-width, width / 2);

			path.quadTo(-width + dx, (width + height) / 4, -width, height / 2);

			return path;

		}

	}

	static void paintInputLines(InstancePainter painter, AbstractGate factory) {

		Location loc = painter.getLocation();
		boolean printView = painter.isPrintView();
		GateAttributes attrs = (GateAttributes) painter.getAttributeSet();
		Direction facing = attrs.facing;
		int inputs = attrs.inputs;
		int negated = attrs.negated;

		int[] lengths = getInputLineLengths(painter.getGraphics(),attrs, factory);
		if (painter.getInstance() == null) { // drawing ghost - negation bubbles only
			for (int i = 0; i < inputs; i++) {
				boolean iNegated = ((negated >> i) & 1) == 1;
				if (iNegated) {
					Location offs = factory.getInputOffset(attrs, i);
					Location loci = loc.translate(offs.getX(), offs.getY());
					Location cent = loci.translate(facing, lengths[i] + 5);
					painter.drawDongle(cent.getX(), cent.getY());
				}
			}
		} else {
			Graphics g = painter.getGraphics();
			Paint baseColor = g.getPaint();
			g.setLineWidth(3);
			for (int i = 0; i < inputs; i++) {
				Location offs = factory.getInputOffset(attrs, i);
				Location src = loc.translate(offs.getX(), offs.getY());
				int len = lengths[i];
				if (len != 0 && (!printView || painter.isPortConnected(i + 1))) {
					if (painter.getShowState()) {
						Value val = painter.getPort(i + 1);
						g.setColor(val.getColor());
					} else {
						g.setColor(baseColor);
					}
					Location dst = src.translate(facing, len);
					g.c.strokeLine(src.getX(), src.getY(), dst.getX(), dst.getY());
				}
				if (((negated >> i) & 1) == 1) {
					Location cent = src.translate(facing, lengths[i] + 5);
					g.setColor(baseColor);
					painter.drawDongle(cent.getX(), cent.getY());
					g.setLineWidth(3);
				}
			}
		}

	}

	private static int[] getInputLineLengths(Graphics g, GateAttributes attrs, AbstractGate factory) {


		int inputs = attrs.inputs;
		int mainHeight = ((Integer) attrs.size.getValue()).intValue();
		Integer key = Integer.valueOf(inputs * 31 + mainHeight);
		Object ret = INPUT_LENGTHS.get(key);
		if (ret != null) {
			return (int[]) ret;
		}

		Direction facing = attrs.facing;
		if (facing != Direction.EAST) {
			attrs = (GateAttributes) attrs.clone();
			attrs.facing = Direction.EAST;
		}

		int[] lengths = new int[inputs];
		INPUT_LENGTHS.put(key, lengths);
		int width = mainHeight;
		Location loc0 = OrGate.FACTORY.getInputOffset(attrs, 0);
		Location locn = OrGate.FACTORY.getInputOffset(attrs, inputs - 1);
		int totalHeight = 10 + loc0.manhattanDistanceTo(locn);
		if (totalHeight < width) totalHeight = width;

		Path2D path = computeShield(g,width, totalHeight);

		for (int i = 0; i < inputs; i++) {
			Location loci = OrGate.FACTORY.getInputOffset(attrs, i);

			Point2D p = new Point2D(loci.getX() + 1, loci.getY());
			int iters = 0;
			while (path.contains(p) && iters < 15) {
				iters++;
				p.setLocation(p.x+1, p.y);
			}
			if (iters >= 15) iters = 0;
			lengths[i] = iters;
		}

		/* used prior to 2.5.1, when moved to GeneralPath */
		/*
		int wingHeight = (totalHeight - mainHeight) / 2;
		double wingCenterX = wingHeight * Math.sqrt(3) / 2;
		double mainCenterX = mainHeight * Math.sqrt(3) / 2;

		for (int i = 0; i < inputs; i++) {
			Location loci = factory.getInputOffset(attrs, i);
			int disti = 5 + loc0.manhattanDistanceTo(loci);
			if (disti > totalHeight - disti) { // ensure on top half
				disti = totalHeight - disti;
			}
			double dx;
			if (disti < wingHeight) { // point is on wing
				int dy = wingHeight / 2 - disti;
				dx = Math.sqrt(wingHeight * wingHeight - dy * dy) - wingCenterX;
			} else { // point is on main shield
				int dy = totalHeight / 2 - disti;
				dx = Math.sqrt(mainHeight * mainHeight - dy * dy) - mainCenterX;
			}
			lengths[i] = (int) (dx - 0.5);
		}

		 */

		return lengths;

	}

}

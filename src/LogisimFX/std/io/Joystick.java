/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.io;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;

import javafx.scene.paint.Color;

public class Joystick extends InstanceFactory {

	static final Attribute<BitWidth> ATTR_WIDTH = Attributes.forBitWidth("bits",
			LC.createStringBinding("ioBitWidthAttr"), 2, 5);

	public Joystick() {

		super("Joystick", LC.createStringBinding("joystickComponent"));
		setAttributes(new Attribute[] { ATTR_WIDTH, Io.ATTR_COLOR },
				new Object[] { BitWidth.create(4), Color.RED });
		setKeyConfigurator(new BitWidthConfigurator(ATTR_WIDTH, 2, 5));
		setOffsetBounds(Bounds.create(-30, -10, 30, 30));
		setIcon("joystick.gif");
		setPorts(new Port[] {
				new Port(0, 0, Port.OUTPUT, ATTR_WIDTH),
				new Port(0, 10, Port.OUTPUT, ATTR_WIDTH),
			});
		setInstancePoker(Poker.class);

	}

	@Override
	public void propagate(InstanceState state) {

		BitWidth bits = state.getAttributeValue(ATTR_WIDTH);
		int dx;
		int dy;
		State s = (State) state.getData();
		if (s == null) { dx = 0; dy = 0; }
		else { dx = s.xPos; dy = s.yPos; }

		int steps = (1 << bits.getWidth()) - 1;
		dx = (dx + 14) * steps / 29 + 1;
		dy = (dy + 14) * steps / 29 + 1;
		if (bits.getWidth() > 4) {
			if (dx >= steps / 2) dx++;
			if (dy >= steps / 2) dy++;
		}
		state.setPort(0, Value.createKnown(bits, dx), 1);
		state.setPort(1, Value.createKnown(bits, dy), 1);

	}

	@Override
	public void paintGhost(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		g.c.strokeRoundRect(-30, -10, 30, 30, 8, 8);
		g.toDefault();

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();

		Graphics g = painter.getGraphics();
		g.c.strokeRoundRect(x - 30, y - 10, 30, 30, 8, 8);
		g.c.strokeRoundRect(x - 28, y - 8, 26, 26, 4, 4);
		drawBall(g, x - 15, y + 5, painter.getAttributeValue(Io.ATTR_COLOR),
				painter.shouldDrawColor());
		painter.drawPorts();

		g.toDefault();

	}

	private static void drawBall(Graphics g, int x, int y, Color c,
			boolean inColor) {

		if (inColor) {
			g.setColor(c == null ? Color.RED : c);
		} else {
			double hue = c == null ? 0.502
					: (c.getRed() + c.getGreen() + c.getBlue()) / 3;
			g.setColor(Color.color(hue, hue, hue));
		}
		g.setLineWidth(1);
		g.c.fillOval(x - 4, y - 4, 8, 8);
		g.setColor(Color.BLACK);
		g.c.strokeOval(x - 4, y - 4, 8, 8);

	}

	private static class State implements InstanceData, Cloneable {

		private int xPos;
		private int yPos;
		
		public State(int x, int y) { xPos = x; yPos = y; }
		
		@Override
		public Object clone() {
			try { return super.clone(); }
			catch (CloneNotSupportedException e) { return null; }
		}

	}
	
	public static class Poker extends InstancePoker {

		@Override
		public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
			mouseDragged(state, e);
		}
		
		@Override
		public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {
			updateState(state, 0, 0);
		}
		
		@Override
		public void mouseDragged(InstanceState state, LayoutCanvas.CME e) {

			Location loc = state.getInstance().getLocation();
			int cx = loc.getX() - 15;
			int cy = loc.getY() + 5;
			updateState(state, e.localX - cx, e.localY - cy);

		}
		
		private void updateState(InstanceState state, int dx, int dy) {

			State s = (State) state.getData();
			if (dx < -14) dx = -14;
			if (dy < -14) dy = -14;
			if (dx > 14) dx = 14;
			if (dy > 14) dy = 14;
			if (s == null) {
				s = new State(dx, dy);
				state.setData(s);
			} else {
				s.xPos = dx;
				s.yPos = dy;
			}
			state.getInstance().fireInvalidated();

		}
		
		@Override
		public void paint(InstancePainter painter) {

			State state = (State) painter.getData();
			if (state == null) {
				state = new State(0, 0);
				painter.setData(state);
			}
			Location loc = painter.getLocation();
			int x = loc.getX();
			int y = loc.getY();
			Graphics g = painter.getGraphics();
			g.setColor(Color.WHITE);
			g.c.fillRect(x - 20, y, 10, 10);
			g.setLineWidth(3);
			g.setColor(Color.BLACK);
			int dx = state.xPos;
			int dy = state.yPos;
			int x0 = x - 15 + (dx > 5 ? 1 : dx < -5 ? -1 : 0);
			int y0 = y + 5 + (dy > 5 ? 1 : dy < 0 ? -1 : 0);
			int x1 = x - 15 + dx;
			int y1 = y + 5 + dy;
			g.c.strokeLine(x0, y0, x1, y1);
			Color ballColor = painter.getAttributeValue(Io.ATTR_COLOR);
			Joystick.drawBall(g, x1, y1, ballColor, true);

		}

	}

}

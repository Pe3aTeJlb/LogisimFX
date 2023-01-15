/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.io;

import LogisimFX.OldFontmetrics;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class Keyboard extends InstanceFactory {

	private static final int CLR = 0;
	private static final int CK = 1;
	private static final int RE = 2;
	private static final int AVL = 3;
	private static final int OUT = 4;
	
	private static final int DELAY0 = 9;
	private static final int DELAY1 = 11;

	static final int WIDTH = 145;
	static final int HEIGHT = 25;
	
	private static final Font DEFAULT_FONT = Font.font("monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12);
	private static final char FORM_FEED = '\u000c'; // control-L

	private static final Attribute<Integer> ATTR_BUFFER
		= Attributes.forIntegerRange("buflen",
			LC.createStringBinding("keybBufferLengthAttr"), 1, 256);

	public Keyboard() {

		super("Keyboard", LC.createStringBinding("keyboardComponent"));
		setAttributes(new Attribute[] { ATTR_BUFFER, StdAttr.EDGE_TRIGGER },
				new Object[] { Integer.valueOf(32), StdAttr.TRIG_RISING });
		setOffsetBounds(Bounds.create(0, -15, WIDTH, HEIGHT));
		setIcon("keyboard.gif");
		setInstancePoker(Poker.class);

		Port[] ps = new Port[5];
		ps[CLR] = new Port( 20, 10, Port.INPUT, 1);
		ps[CK]  = new Port(  0,  0, Port.INPUT, 1);
		ps[RE]  = new Port( 10, 10, Port.INPUT, 1);
		ps[AVL] = new Port(130, 10, Port.OUTPUT, 1);
		ps[OUT] = new Port(140, 10, Port.OUTPUT, 7);
		ps[CLR].setToolTip(LC.createStringBinding("keybClearTip"));
		ps[CK].setToolTip(LC.createStringBinding("keybClockTip"));
		ps[RE].setToolTip(LC.createStringBinding("keybEnableTip"));
		ps[AVL].setToolTip(LC.createStringBinding("keybAvailTip"));
		ps[OUT].setToolTip(LC.createStringBinding("keybOutputTip"));
		setPorts(ps);

	}

	@Override
	public void propagate(InstanceState circState) {

		Object trigger = circState.getAttributeValue(StdAttr.EDGE_TRIGGER);
		KeyboardData state = getKeyboardState(circState);
		Value clear = circState.getPort(CLR);
		Value clock = circState.getPort(CK);
		Value enable = circState.getPort(RE);
		char c;

		synchronized(state) {
			Value lastClock = state.setLastClock(clock);
			if (clear == Value.TRUE) {
				state.clear();
			} else if (enable != Value.FALSE) {
				boolean go;
				if (trigger == StdAttr.TRIG_FALLING) {
					go = lastClock == Value.TRUE && clock == Value.FALSE;
				} else {
					go = lastClock == Value.FALSE && clock == Value.TRUE;
				}
				if (go) state.dequeue();
			}

			c = state.getChar(0);
		}
		Value out = Value.createKnown(BitWidth.create(7), c & 0x7F);
		circState.setPort(OUT, out, DELAY0);
		circState.setPort(AVL, c != '\0' ? Value.TRUE : Value.FALSE, DELAY1);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		boolean showState = painter.getShowState();
		Graphics g = painter.getGraphics();
		painter.drawClock(CK, Direction.EAST);
		painter.drawBounds();
		painter.drawPort(CLR);
		painter.drawPort(RE);
		painter.drawPort(AVL);
		painter.drawPort(OUT);

		if (showState) {
			String str;
			int dispStart;
			int dispEnd;
			ArrayList<Integer> specials = new ArrayList<Integer>();
			FontMetrics fm = null;
			KeyboardData state = getKeyboardState(painter);
			synchronized(state) {
				str = state.toString();
				for (int i = state.getNextSpecial(0); i >= 0; i = state.getNextSpecial(i + 1)) {
					char c = state.getChar(i);
					specials.add(Integer.valueOf(c << 16 | i));
				}
				if (!state.isDisplayValid()) {
					fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(DEFAULT_FONT);
					state.updateDisplay(fm);
				}
				dispStart = state.getDisplayStart();
				dispEnd = state.getDisplayEnd();
			}

			if (str.length() > 0) {
				Bounds bds = painter.getBounds();
				drawBuffer(g, fm, str, dispStart, dispEnd, specials, bds);
			}
		} else {
			Bounds bds = painter.getBounds();
			int len = getBufferLength(painter.getAttributeValue(ATTR_BUFFER));
			String str = LC.getFormatted("keybDesc", "" + len);
			FontMetrics fm = g.getFontMetrics();
			int x = bds.getX() + (WIDTH - OldFontmetrics.computeStringWidth(fm,str)) / 2;
			int y = bds.getY() + (HEIGHT + (int)fm.getAscent()) / 2;
			g.c.strokeText(str, x, y);
		}
		g.toDefault();

	}

	private void drawDots(Graphics g, int x, int y, int width, int ascent) {

		int r = width / 10;
		if (r < 1) r = 1;
		int d = 2 * r;
		if (2 * r + 1 * d <= width) g.c.fillOval(x + r, y - d, d, d);
		if (3 * r + 2 * d <= width) g.c.fillOval(x + 2 * r + d, y - d, d, d);
		if (5 * r + 3 * d <= width) g.c.fillOval(x + 3 * r + 2 * d, y - d, d, d);

	}

	private void drawBuffer(Graphics g, FontMetrics fm, String str,
			int dispStart, int dispEnd, ArrayList<Integer> specials, Bounds bds) {

		int x = bds.getX();
		int y = bds.getY();

		g.setFont(DEFAULT_FONT);
		if (fm == null) fm = g.getFontMetrics();
		int asc = (int) fm.getAscent();
		int x0 = x + 8;
		int ys = y + (HEIGHT + asc) / 2;
		int dotsWidth = OldFontmetrics.computeStringWidth(fm,"m");
		int xs;
		if (dispStart > 0) {
			g.c.fillText(str.substring(0, 1), x0, ys);
			xs = x0 + OldFontmetrics.computeStringWidth(fm,str.charAt(0) + "m");
			drawDots(g, xs - dotsWidth, ys, dotsWidth, asc);
			String sub = str.substring(dispStart, dispEnd);
			g.c.fillText(sub, xs, ys);
			if (dispEnd < str.length()) {
				drawDots(g, xs + OldFontmetrics.computeStringWidth(fm,sub), ys, dotsWidth, asc);
			}
		} else if (dispEnd < str.length()) {
			String sub = str.substring(dispStart, dispEnd);
			xs = x0;
			g.c.fillText(sub, xs, ys);
			drawDots(g, xs + OldFontmetrics.computeStringWidth(fm,sub), ys, dotsWidth, asc);
		} else {
			xs = x0;
			g.c.fillText(str, xs, ys);
		}

		if (specials.size() > 0) {
			drawSpecials(specials, x0, xs, ys, asc, g, fm,
					str, dispStart, dispEnd);
		}


	}

	private void drawSpecials(ArrayList<Integer> specials, int x0, int xs, int ys,
			int asc, Graphics g, FontMetrics fm,
			String str, int dispStart, int dispEnd) {

		double[] px = new double[3];
		double[] py = new double[3];
		for (Integer special : specials) {
			int code = special.intValue();
			int pos = code & 0xFF;
			int w0;
			int w1;
			if (pos == 0) {
				w0 = x0;
				w1 = x0 + OldFontmetrics.computeStringWidth(fm,str.substring(0, 1));
			} else if (pos >= dispStart && pos < dispEnd) {
				w0 = xs + OldFontmetrics.computeStringWidth(fm,str.substring(dispStart, pos));
				w1 = xs + OldFontmetrics.computeStringWidth(fm,str.substring(dispStart, pos + 1));
			} else {
				continue; // this character is not in current view
			}
			w0++;
			w1--;

			int key = code >> 16;
			if (key == '\b') {
				int y1 = ys - asc / 2;
				g.c.strokeLine(w0, y1, w1, y1);
				px[0] = w0 + 3; py[0] = y1 - 3;
				px[1] = w0;     py[1] = y1;
				px[2] = w0 + 3; py[2] = y1 + 3;
				g.c.strokePolyline(px, py, 3);
			} else if (key == '\n') {
				int y1 = ys - 3;
				px[0] = w1; py[0] = ys - asc;
				px[1] = w1; py[1] = y1;
				px[2] = w0; py[2] = y1;
				g.c.strokePolyline(px, py, 3);
				px[0] = w0 + 3; py[0] = y1 - 3;
				px[1] = w0;     py[1] = y1;
				px[2] = w0 + 3; py[2] = y1 + 3;
				g.c.strokePolyline(px, py, 3);
			} else if (key == FORM_FEED) {
				g.c.strokeRect(w0, ys - asc, w1 - w0, asc);
			}
		}

	}

	private static int getBufferLength(Object bufferAttr) {

		if (bufferAttr instanceof Integer) return ((Integer) bufferAttr).intValue();
		else return 32;

	}

	private static KeyboardData getKeyboardState(InstanceState state) {

		int bufLen = getBufferLength(state.getAttributeValue(ATTR_BUFFER));
		KeyboardData ret = (KeyboardData) state.getData();
		if (ret == null) {
			ret = new KeyboardData(bufLen);
			state.setData(ret);
		} else {
			ret.updateBufferLength(bufLen);
		}
		return ret;

	}

	public static void addToBuffer(InstanceState state, char[] newChars) {

		KeyboardData keyboardData = getKeyboardState(state);
		for (int i = 0; i < newChars.length; i++) {
			keyboardData.insert(newChars[i]);
		}

	}

	public static class Poker extends InstancePoker {

		@Override
		public void keyPressed(InstanceState state, KeyEvent e) {

			KeyboardData data = getKeyboardState(state);
			boolean changed = false;
			boolean used = true;
			synchronized(data) {
				switch (e.getCode()) {
				case DELETE: changed = data.delete(); break;
				case LEFT:   data.moveCursorBy(-1); break;
				case RIGHT:  data.moveCursorBy(1); break;
				case HOME:   data.setCursor(0); break;
				case END:    data.setCursor(Integer.MAX_VALUE); break;
				default: used = false;
				}
			}
			if (used) e.consume();
			if (changed) state.getInstance().fireInvalidated();

		}

		@Override
		public void keyTyped(InstanceState state, KeyEvent e) {

			KeyboardData data = getKeyboardState(state);
			char ch = e.getCharacter().toCharArray()[0];
			boolean changed = false;
			//if (e.getCode() != KeyCode.UNDEFINED) {
				if (!Character.isISOControl(ch) || ch == '\b' || ch == '\n'
						|| ch == FORM_FEED) {
					synchronized(data) { changed = data.insert(ch); }
					e.consume();
				}
			//}
			if (changed) state.getInstance().fireInvalidated();

		}

		public void draw(InstancePainter painter) {

			KeyboardData data = getKeyboardState(painter);
			Bounds bds = painter.getInstance().getBounds();
			Graphics g = painter.getGraphics();
			FontMetrics fm = g.getFontmetricsForFont(DEFAULT_FONT);

			String str;
			int cursor;
			int dispStart;
			synchronized(data) {
				str = data.toString();
				cursor = data.getCursorPosition();
				if (!data.isDisplayValid()) data.updateDisplay(fm);
				dispStart = data.getDisplayStart();
			}

			int asc = (int)fm.getAscent();
			int x = bds.getX() + 8;
			if (dispStart > 0) {
				x += OldFontmetrics.computeStringWidth(fm,str.charAt(0) + "m");
				x += OldFontmetrics.computeStringWidth(fm,str.substring(dispStart, cursor));
			} else if (cursor >= str.length()) {
				x += OldFontmetrics.computeStringWidth(fm,str);
			} else {
				x += OldFontmetrics.computeStringWidth(fm,str.substring(0, cursor));
			}
			int y = bds.getY() + (bds.getHeight() + asc) / 2;
			g.c.strokeLine(x, y - asc, x, y);
			g.toDefault();

		}

	}

}

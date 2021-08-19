/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.io;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.instance.*;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;
import com.cburch.LogisimFX.std.LC;

import com.sun.javafx.tk.FontMetrics;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class Tty extends InstanceFactory {

	private static final int CLR = 0;
	private static final int CK = 1;
	private static final int WE = 2;
	private static final int IN = 3;
	
	private static final int BORDER = 5;
	private static final int ROW_HEIGHT = 15;
	private static final int COL_WIDTH = 7; 
	private static final Color DEFAULT_BACKGROUND = Color.color(0, 0, 0, 0.250);
	
	private static final Font DEFAULT_FONT = Font.font("monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12);

	private static final Attribute<Integer> ATTR_COLUMNS
		= Attributes.forIntegerRange("cols",
			LC.createStringBinding("ttyColsAttr"), 1, 120);
	private static final Attribute<Integer> ATTR_ROWS
		= Attributes.forIntegerRange("rows",
			LC.createStringBinding("ttyRowsAttr"), 1, 48);

	public Tty() {
		super("TTY", LC.createStringBinding("ttyComponent"));
		setAttributes(new Attribute[] {
				ATTR_ROWS, ATTR_COLUMNS, StdAttr.EDGE_TRIGGER,
				Io.ATTR_COLOR, Io.ATTR_BACKGROUND
			}, new Object[] {
				Integer.valueOf(8), Integer.valueOf(32), StdAttr.TRIG_RISING,
				Color.BLACK, DEFAULT_BACKGROUND
			});
		setIcon("tty.gif");

		Port[] ps = new Port[4];
		ps[CLR] = new Port(20,  10, Port.INPUT, 1);
		ps[CK]  = new Port( 0,   0, Port.INPUT, 1);
		ps[WE]  = new Port(10,  10, Port.INPUT, 1);
		ps[IN]  = new Port( 0, -10, Port.INPUT, 7);
		ps[CLR].setToolTip(LC.createStringBinding("ttyClearTip"));
		ps[CK].setToolTip(LC.createStringBinding("ttyClockTip"));
		ps[WE].setToolTip(LC.createStringBinding("ttyEnableTip"));
		ps[IN].setToolTip(LC.createStringBinding("ttyInputTip"));
		setPorts(ps);
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {
		int rows = getRowCount(attrs.getValue(ATTR_ROWS));
		int cols = getColumnCount(attrs.getValue(ATTR_COLUMNS));
		int width = 2 * BORDER + cols * COL_WIDTH;
		int height = 2 * BORDER + rows * ROW_HEIGHT;
		if (width < 30) width = 30;
		if (height < 30) height = 30;
		return Bounds.create(0, 10 - height, width, height);
	}

	@Override
	protected void configureNewInstance(Instance instance) {
		instance.addAttributeListener();
	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
		if (attr == ATTR_ROWS || attr == ATTR_COLUMNS) {
			instance.recomputeBounds();
		}
	}

	@Override
	public void propagate(InstanceState circState) {
		Object trigger = circState.getAttributeValue(StdAttr.EDGE_TRIGGER);
		TtyState state = getTtyState(circState);
		Value clear = circState.getPort(CLR);
		Value clock = circState.getPort(CK);
		Value enable = circState.getPort(WE);
		Value in = circState.getPort(IN);

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
				if (go) state.add(in.isFullyDefined() ? (char) in.toIntValue() : '?');
			}
		}
	}

	@Override
	public void paintGhost(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		Bounds bds = painter.getBounds();
		g.c.strokeRoundRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(),
				10, 10);
		g.toDefaultFont();

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		boolean showState = painter.getShowState();
		Graphics g = painter.getGraphics();
		Bounds bds = painter.getBounds();
		painter.drawClock(CK, Direction.EAST);
		if (painter.shouldDrawColor()) {
			g.setColor(painter.getAttributeValue(Io.ATTR_BACKGROUND));
			g.c.fillRoundRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(),
					10, 10);
		}
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		g.c.strokeRoundRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(),
				2 * BORDER, 2 * BORDER);
		g.setLineWidth(1);
		painter.drawPort(CLR);
		painter.drawPort(WE);
		painter.drawPort(IN);

		int rows = getRowCount(painter.getAttributeValue(ATTR_ROWS));
		int cols = getColumnCount(painter.getAttributeValue(ATTR_COLUMNS));

		if (showState) {
			String[] rowData = new String[rows];
			int curRow;
			int curCol;
			TtyState state = getTtyState(painter);
			synchronized(state) {
				for (int i = 0; i < rows; i++) {
					rowData[i] = state.getRowString(i);
				}
				curRow = state.getCursorRow();
				curCol = state.getCursorColumn();
			}

			g.setFont(DEFAULT_FONT);
			g.setColor(painter.getAttributeValue(Io.ATTR_COLOR));
			FontMetrics fm = painter.getFontMetrics();
			int x = bds.getX() + BORDER;
			int y = bds.getY() + BORDER + (ROW_HEIGHT + (int)fm.getAscent()) / 2;
			for (int i = 0; i < rows; i++) {
				g.c.strokeText(rowData[i], x, y);
				if (i == curRow) {
					int x0 = x + (int)fm.computeStringWidth(rowData[i].substring(0, curCol));
					g.c.strokeLine(x0, y - fm.getAscent(), x0, y);
				}
				y += ROW_HEIGHT;
			}
		} else {
			String str = LC.getFormatted("ttyDesc", "" + rows, "" + cols);
			FontMetrics fm = painter.getFontMetrics();
			int strWidth = (int)fm.computeStringWidth(str);
			if (strWidth + BORDER > bds.getWidth()) {
				str = LC.get("ttyDescShort");
				strWidth = (int)fm.computeStringWidth(str);
			}
			int x = bds.getX() + (bds.getWidth() - strWidth) / 2;
			int y = bds.getY() + (bds.getHeight() + (int)fm.getAscent()) / 2;
			g.c.strokeText(str, x, y);
		}

		g.toDefault();

	}

	private TtyState getTtyState(InstanceState state) {

		int rows = getRowCount(state.getAttributeValue(ATTR_ROWS));
		int cols = getColumnCount(state.getAttributeValue(ATTR_COLUMNS));
		TtyState ret = (TtyState) state.getData();
		if (ret == null) {
			ret = new TtyState(rows, cols);
			state.setData(ret);
		} else {
			ret.updateSize(rows, cols);
		}

		return ret;

	}
	
	public void sendToStdout(InstanceState state) {

		TtyState tty = getTtyState(state);
		tty.setSendStdout(true);

	}
	
	private static int getRowCount(Object val) {

		if (val instanceof Integer) return ((Integer) val).intValue();
		else return 4;

	}
	
	private static int getColumnCount(Object val) {

		if (val instanceof Integer) return ((Integer) val).intValue();
		else return 16;

	}

}

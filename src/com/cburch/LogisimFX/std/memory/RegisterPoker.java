/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.memory;

import com.cburch.LogisimFX.data.BitWidth;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstancePoker;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.instance.StdAttr;
import com.cburch.LogisimFX.newgui.MainFrame.LayoutCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class RegisterPoker extends InstancePoker {

	private int initValue;
	private int curValue;

	@Override
	public boolean init(InstanceState state, LayoutCanvas.CME e) {

		RegisterData data = (RegisterData) state.getData();
		if (data == null) {
			data = new RegisterData();
			state.setData(data);
		}
		initValue = data.value;
		curValue = initValue;
		return true;

	}

	@Override
	public void paint(InstancePainter painter) {

		Bounds bds = painter.getBounds();
		BitWidth dataWidth = painter.getAttributeValue(StdAttr.WIDTH);
		int width = dataWidth == null ? 8 : dataWidth.getWidth();
		int len = (width + 3) / 4;

		Graphics g = painter.getGraphics();
		g.setColor(Color.RED);
		if (len > 4) {
			g.c.strokeRect(bds.getX(), bds.getY() + 3, bds.getWidth(), 25);
		} else {
			int wid = 7 * len + 2;
			g.c.strokeRect(bds.getX() + (bds.getWidth() - wid) / 2, bds.getY() + 4, wid, 15);
		}
		g.setColor(Color.BLACK);

		g.toDefault();

	}

	@Override
	public void keyTyped(InstanceState state, KeyEvent e) {

		int val = Character.digit(e.getKeyChar(), 16);
		if (val < 0) return;

		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
		if (dataWidth == null) dataWidth = BitWidth.create(8);
		curValue = (curValue * 16 + val) & dataWidth.getMask();
		RegisterData data = (RegisterData) state.getData();
		data.value = curValue;

		state.fireInvalidated();

	}

}

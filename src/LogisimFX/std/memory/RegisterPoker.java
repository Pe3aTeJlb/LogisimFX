/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.memory;

import LogisimFX.data.BitWidth;
import LogisimFX.data.Bounds;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstancePoker;
import LogisimFX.instance.InstanceState;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

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

		int val = Character.digit(e.getCharacter().toCharArray()[0], 16);
		if (val < 0) return;

		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
		if (dataWidth == null) dataWidth = BitWidth.create(8);
		curValue = (curValue * 16 + val) & dataWidth.getMask();
		RegisterData data = (RegisterData) state.getData();
		data.value = curValue;

		state.fireInvalidated();

	}

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.BitWidth;
import LogisimFX.data.Bounds;
import LogisimFX.data.Value;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstancePoker;
import LogisimFX.instance.InstanceState;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class ShiftRegisterPoker extends InstancePoker {

	private int loc;
	
	@Override
	public boolean init(InstanceState state, LayoutCanvas.CME e) {

		loc = computeStage(state, e);
		return loc >= 0;

	}
	
	private int computeStage(InstanceState state, LayoutCanvas.CME e) {

		Integer lenObj = state.getAttributeValue(ShiftRegister.ATTR_LENGTH);
		BitWidth widObj = state.getAttributeValue(StdAttr.WIDTH);
		Boolean loadObj = state.getAttributeValue(ShiftRegister.ATTR_LOAD);
		Bounds bds = state.getInstance().getBounds();
		int y = bds.getY();
		String label = state.getAttributeValue(StdAttr.LABEL);
		if (label == null || label.equals("")) y += bds.getHeight() / 2;
		else y += 3 * bds.getHeight() / 4;
		y = e.localY - y;
		//y = e.localY;
		if (y <= -6 || y >= 8) return -1;
		
		int x = e.localX - (bds.getX() + 15);
		//int x = e.localX;
		if (!loadObj.booleanValue() || widObj.getWidth() > 4) return -1;
		if (x < 0 || x >= lenObj.intValue() * 10) return -1;
		return x / 10;

	}

	@Override
	public void paint(InstancePainter painter) {

		int loc = this.loc;
		if (loc < 0) return;
		Bounds bds = painter.getInstance().getBounds();
		int x = bds.getX() + 15 + loc * 10;
		int y = bds.getY();
		String label = painter.getAttributeValue(StdAttr.LABEL);
		if (label == null || label.equals("")) y += bds.getHeight() / 2;
		else y += 3 * bds.getHeight() / 4;
		Graphics g = painter.getGraphics();
		g.setColor(Color.RED);
		g.c.strokeRect(x, y - 6, 10, 13);

	}
	
	@Override
	public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
		loc = computeStage(state, e);
	}
	
	@Override
	public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {

		int oldLoc = loc;
		if (oldLoc < 0) return;
		BitWidth widObj = state.getAttributeValue(StdAttr.WIDTH);
		if (widObj.equals(BitWidth.ONE)) {
			int newLoc = computeStage(state, e);
			if (oldLoc == newLoc) {
				ShiftRegisterData data = (ShiftRegisterData) state.getData();
				int i = data.getLength() - 1 - loc;
				Value v = data.get(loc);
				if (v == Value.FALSE) v = Value.TRUE;
				else v = Value.FALSE;
				data.set(loc, v);
				state.fireInvalidated();
			}
		}

	}

	@Override
	public void keyTyped(InstanceState state, KeyEvent e) {

		int loc = this.loc;
		if (loc < 0) return;
		char c = e.getCharacter().toCharArray()[0];
		if (c == ' ') {
			Integer lenObj = state.getAttributeValue(ShiftRegister.ATTR_LENGTH);
			if (loc < lenObj.intValue() - 1) {
				this.loc = loc + 1;
				state.fireInvalidated();
			}
		} else if (c == '\u0008') {
			if (loc > 0) {
				this.loc = loc - 1;
				state.fireInvalidated();
			}
		} else {
			try {
				int val = Integer.parseInt("" + c, 16);
				BitWidth widObj = state.getAttributeValue(StdAttr.WIDTH);
				if ((val & ~widObj.getMask()) != 0) return;
				Value valObj = Value.createKnown(widObj, val);
				ShiftRegisterData data = (ShiftRegisterData) state.getData();
				int i = data.getLength() - 1 - loc;
				if (!data.get(loc).equals(valObj)) {
					data.set(loc, valObj);
					state.fireInvalidated();
				}
			} catch (NumberFormatException ex) {
				return;
			}
		}

	}

}

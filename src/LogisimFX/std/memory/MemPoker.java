/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.Bounds;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstancePoker;
import LogisimFX.instance.InstanceState;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.proj.Project;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class MemPoker extends InstancePoker {

	private MemPoker sub;

	@Override
	public boolean init(InstanceState state, LayoutCanvas.CME event) {

		Bounds bds = state.getInstance().getBounds();
		MemState data = (MemState) state.getData();
		long addr = data.getAddressAt(event.localX - bds.getX(),
				event.localY - bds.getY());

		//long addr = data.getAddressAt(event.localX, event.localY);

		// See if outside box
		if (addr < 0) {
			sub = new AddrPoker();
		} else {
			sub = new DataPoker(state, data, addr);
		}

		return true;

	}
	
	@Override
	public Bounds getBounds(InstancePainter state) {
		return sub.getBounds(state);
	}

	@Override
	public void paint(InstancePainter painter) {
		sub.paint(painter);
	}

	@Override
	public void keyTyped(InstanceState state, KeyEvent e) {
		sub.keyTyped(state, e);
	}
	
	private static class DataPoker extends MemPoker {

		int initValue;
		int curValue;

		private DataPoker(InstanceState state, MemState data, long addr) {

			data.setCursor(addr);
			initValue = data.getContents().get(data.getCursor());
			curValue = initValue;
			
			Object attrs = state.getInstance().getAttributeSet();
			if (attrs instanceof RomAttributes) {
				Project proj = state.getProject();
				if (proj != null) {
					((RomAttributes) attrs).setProject(proj);
				}
			}

		}
	
		@Override
		public Bounds getBounds(InstancePainter painter) {

			MemState data = (MemState) painter.getData();
			Bounds inBounds = painter.getInstance().getBounds();
			return data.getBounds(data.getCursor(), inBounds);

		}
	
		@Override
		public void paint(InstancePainter painter) {

			Bounds bds = getBounds(painter);
			Graphics g = painter.getGraphics();
			g.setColor(Color.RED);
			g.c.strokeRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
			g.setColor(Color.BLACK);

		}
	
		@Override
		public void stopEditing(InstanceState state) {

			MemState data = (MemState) state.getData();
			data.setCursor(-1);

		}
	
		@Override
		public void keyTyped(InstanceState state, KeyEvent e) {

			char c = e.getCharacter().toCharArray()[0];
			int val = Character.digit(c, 16);

			MemState data = (MemState) state.getData();
			if (val >= 0) {
				curValue = curValue * 16 + val;
				data.getContents().set(data.getCursor(), curValue);
				state.fireInvalidated();
			} else if (c == ' ' || c == '\t') {
				moveTo(data, data.getCursor() + 1);
			} else if (c == '\r' || c == '\n') {
				moveTo(data, data.getCursor() + data.getColumns());
			} else if (c == '\u0008' || c == '\u007f') {
				moveTo(data, data.getCursor() - 1);
			}

		}
	
		private void moveTo(MemState data, long addr) {

			if (data.isValidAddr(addr)) {
				data.setCursor(addr);
				data.scrollToShow(addr);
				initValue = data.getContents().get(addr);
				curValue = initValue;
			}

		}
	}

	private static class AddrPoker extends MemPoker {

		@Override
		public Bounds getBounds(InstancePainter painter) {

			MemState data = (MemState) painter.getData();
			return data.getBounds(-1, painter.getBounds());

		}
	
		@Override
		public void paint(InstancePainter painter) {

			Bounds bds = getBounds(painter);
			Graphics g = painter.getGraphics();
			g.setColor(Color.RED);
			g.c.strokeRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
			g.setColor(Color.BLACK);

			g.toDefault();

		}
	
		@Override
		public void keyTyped(InstanceState state, KeyEvent e) {

			char c = e.getCharacter().toCharArray()[0];
			int val = Character.digit(c, 16);
			MemState data = (MemState) state.getData();
			if (val >= 0) {
				long newScroll = (data.getScroll() * 16 + val) & (data.getLastAddress());
				data.setScroll(newScroll);
			} else if (c == ' ') {
				data.setScroll(data.getScroll() + (data.getRows() - 1) * data.getColumns());
			} else if (c == '\r' || c == '\n') {
				data.setScroll(data.getScroll() + data.getColumns());
			} else if (c == '\u0008' || c == '\u007f') {
				data.setScroll(data.getScroll() - data.getColumns());
			}

		}

	}

}
/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gray;

import LogisimFX.data.BitWidth;
import LogisimFX.data.Bounds;
import LogisimFX.data.Value;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstancePoker;
import LogisimFX.instance.InstanceState;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;

import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/** When the user clicks a counter using the Poke Tool, a CounterPoker object
 * is created, and that object will handle all user events. Note that
 * CounterPoker is a class specific to GrayCounter, and that it must be a
 * subclass of InstancePoker in the com.cburch.logisim.instance package. */
public class CounterPoker extends InstancePoker {

	public CounterPoker() { }

	/** Determines whether the location the mouse was pressed should result
	 * in initiating a poke. 
	 */
	@Override
	public boolean init(InstanceState state, LayoutCanvas.CME e) {
		return state.getInstance().getBounds().contains(e.localX, e.localY);
			// Anywhere in the main rectangle initiates the poke. The user might
			// have clicked within a label, but that will be outside the bounds.
	}

	/** Draws an indicator that the caret is being selected. Here, we'll draw
	 * a red rectangle around the value. */
	@Override
	public void paint(InstancePainter painter) {

		Bounds bds = painter.getBounds();
		BitWidth width = painter.getAttributeValue(StdAttr.WIDTH);
		int len = (width.getWidth() + 3) / 4;

		Graphics g = painter.getGraphics();
		g.setColor(Color.RED);
		int wid = 7 * len + 2; // width of caret rectangle
		int ht = 16; // height of caret rectangle
		g.c.strokeRect(bds.getX() + (bds.getWidth() - wid) / 2,
				bds.getY() + (bds.getHeight() - ht) / 2, wid, ht);
		g.setColor(Color.BLACK);

	}

	/** Processes a key by just adding it onto the end of the current value. */
	@Override
	public void keyTyped(InstanceState state, KeyEvent e) {

		// convert it to a hex digit; if it isn't a hex digit, abort.
		int val = Character.digit(e.getCharacter().toCharArray()[0], 16);
		BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
		if (val < 0 || (val & width.getMask()) != val) return;

		// compute the next value
		CounterData cur = CounterData.get(state, width);
		int newVal = (cur.getValue().toIntValue() * 16 + val) & width.getMask();
		Value newValue = Value.createKnown(width, newVal);
		cur.setValue(newValue);
		state.fireInvalidated();
		
		// You might be tempted to propagate the value immediately here, using
		// state.setPort. However, the circuit may currently be propagating in
		// another thread, and invoking setPort directly could interfere with
		// that. Using fireInvalidated notifies the propagation thread to
		// invoke propagate on the counter at its next opportunity.

	}

}

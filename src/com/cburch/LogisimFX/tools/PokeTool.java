/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.comp.ComponentUserEvent;
import com.cburch.LogisimFX.data.AttributeSet;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitEvent;
import com.cburch.LogisimFX.circuit.CircuitListener;
import com.cburch.LogisimFX.circuit.RadixOption;
import com.cburch.LogisimFX.circuit.Wire;
import com.cburch.LogisimFX.circuit.WireSet;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;
import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.proj.Project;

import com.sun.javafx.tk.FontMetrics;
import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class PokeTool extends Tool {

	private static final ImageView icon = IconsManager.getIcon("poke.gif");
	private static final Color caretColor = Color.color(1, 1, 0.588);

	private static class WireCaret extends AbstractCaret {
		AttributeSet opts;
		LayoutCanvas canvas;
		Wire wire;
		int x;
		int y;

		WireCaret(LayoutCanvas c, Wire w, int x, int y, AttributeSet opts) {
			canvas = c;
			wire = w;
			this.x = x;
			this.y = y;
			this.opts = opts;
		}

		@Override
		public void draw(Graphics g) {
			Value v = canvas.getCircuitState().getValue(wire.getEnd0());
			RadixOption radix1 = RadixOption.decode(AppPreferences.POKE_WIRE_RADIX1.get());
			RadixOption radix2 = RadixOption.decode(AppPreferences.POKE_WIRE_RADIX2.get());
			if (radix1 == null) radix1 = RadixOption.RADIX_2;
			String vStr = radix1.toString(v);
			if (radix2 != null && v.getWidth() > 1) {
				vStr += " / " + radix2.toString(v);
			}
			
			FontMetrics fm = g.getFontMetrics();
			g.setColor(caretColor);
			g.c.fillRect(x + 2, y + 2, fm.computeStringWidth(vStr) + 4,
					fm.getAscent() + fm.getDescent() + 4);
			g.setColor(Color.BLACK);
			g.c.strokeRect(x + 2, y + 2, fm.computeStringWidth(vStr) + 4,
					fm.getAscent() + fm.getDescent() + 4);
			g.c.fillOval(x - 2, y - 2, 5, 5);
			g.c.strokeText(vStr, x + 4, y + 4 + fm.getAscent());
		}
	}
	
	private class Listener implements CircuitListener {
		public void circuitChanged(CircuitEvent event) {
			Circuit circ = pokedCircuit;
			if (event.getCircuit() == circ && circ != null
					&& (event.getAction() == CircuitEvent.ACTION_REMOVE
							|| event.getAction() == CircuitEvent.ACTION_CLEAR)
					&& !circ.contains(pokedComponent)) {
				removeCaret(false);
			}
		}
	}

	private static Cursor cursor = Cursor.HAND;

	private Listener listener;
	private Circuit pokedCircuit;
	private Component pokedComponent;
	private Caret pokeCaret;

	public PokeTool() {
		this.listener = new Listener();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof PokeTool;
	}

	@Override
	public int hashCode() {
		return PokeTool.class.hashCode();
	}

	@Override
	public String getName() {
		return "Poke Tool";
	}

	@Override
	public StringBinding getDisplayName() {
		return LC.createStringBinding("pokeTool");
	}

	private void removeCaret(boolean normal) {
		Circuit circ = pokedCircuit;
		Caret caret = pokeCaret;
		if (caret != null) {
			if (normal) caret.stopEditing(); else caret.cancelEditing();
			circ.removeCircuitListener(listener);
			pokedCircuit = null;
			pokedComponent = null;
			pokeCaret = null;
		}
	}

	private void setPokedComponent(Circuit circ, Component comp, Caret caret) {
		removeCaret(true);
		pokedCircuit = circ;
		pokedComponent = comp;
		pokeCaret = caret;
		if (caret != null) {
			circ.addCircuitListener(listener);
		}
	}

	@Override
	public StringBinding getDescription() {
		return LC.createStringBinding("pokeToolDesc");
	}

	@Override
	public ImageView getIcon() {
		return icon;
	}

	@Override
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {
		if (pokeCaret != null) pokeCaret.draw(context.getGraphics());
	}

	@Override
	public void deselect(LayoutCanvas canvas) {
		removeCaret(true);
		canvas.setHighlightedWires(WireSet.EMPTY);
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		int x = e.localX;
		int y = e.localY;
		Location loc = Location.create(x, y);
		boolean dirty = false;
		canvas.setHighlightedWires(WireSet.EMPTY);
		if (pokeCaret != null && !pokeCaret.getBounds(g).contains(loc)) {
			dirty = true;
			removeCaret(true);
		}
		if (pokeCaret == null) {
			ComponentUserEvent event = new ComponentUserEvent(canvas, x, y,e);
			Circuit circ = canvas.getCircuit();
			for (Component c : circ.getAllContaining(loc, g)) {
				if (pokeCaret != null) break;

				if (c instanceof Wire) {
					Caret caret = new WireCaret(canvas, (Wire) c, x, y,
						canvas.getProject().getOptions().getAttributeSet());
					setPokedComponent(circ, c, caret);
					canvas.setHighlightedWires(circ.getWireSet((Wire) c));
				} else {
					Pokable p = (Pokable) c.getFeature(Pokable.class);
					if (p != null) {
						Caret caret = p.getPokeCaret(event);
						setPokedComponent(circ, c, caret);
						AttributeSet attrs = c.getAttributeSet();
						if (attrs != null && attrs.getAttributes().size() > 0) {
							Project proj = canvas.getProject();
							proj.getFrameController().setAttributeTable(circ,c);
						}
					}
				}
			}
		}
		if (pokeCaret != null) {
			dirty = true;
			pokeCaret.mousePressed(e);
		}

	}

	@Override
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (pokeCaret != null) {
			pokeCaret.mouseDragged(e);
		}
	}

	@Override
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (pokeCaret != null) {
			pokeCaret.mouseReleased(e);
		}
	}

	@Override
	public void keyTyped(LayoutCanvas canvas, KeyEvent e) {
		if (pokeCaret != null) {
			pokeCaret.keyTyped(e);
		}
	}

	@Override
	public void keyPressed(LayoutCanvas canvas, KeyEvent e) {
		if (pokeCaret != null) {
			pokeCaret.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(LayoutCanvas canvas, KeyEvent e) {
		if (pokeCaret != null) {
			pokeCaret.keyReleased(e);
		}
	}

	@Override
	public Cursor getCursor() { return cursor; }

}


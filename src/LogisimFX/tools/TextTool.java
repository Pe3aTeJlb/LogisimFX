/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.IconsManager;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.comp.ComponentUserEvent;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.base.Text;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitEvent;
import LogisimFX.circuit.CircuitListener;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class TextTool extends Tool {

	private class MyListener
			implements CaretListener, CircuitListener {
		public void editingCanceled(CaretEvent e) {
			if (e.getCaret() != caret) {
				e.getCaret().removeCaretListener(this);
				return;
			}
			caret.removeCaretListener(this);
			caretCircuit.removeCircuitListener(this);

			caretCircuit = null;
			caretComponent = null;
			caretCreatingText = false;
			caret = null;
		}
		
		public void editingStopped(CaretEvent e) {
			if (e.getCaret() != caret) {
				e.getCaret().removeCaretListener(this);
				return;
			}
			caret.removeCaretListener(this);
			caretCircuit.removeCircuitListener(this);
			
			String val = caret.getText();
			boolean isEmpty = (val == null || val.equals(""));
			Action a;
			Project proj = caretCanvas.getProject();
			if (caretCreatingText) {
				if (!isEmpty) {
					CircuitMutation xn = new CircuitMutation(caretCircuit);
					xn.add(caretComponent);
					a = xn.toAction(LC.createComplexStringBinding("addComponentAction",
							Text.FACTORY.getDisplayGetter().getValue()));
				} else {
					a = null; // don't add the blank text field
				}
			} else {
				if (isEmpty && caretComponent.getFactory() instanceof Text) {
					CircuitMutation xn = new CircuitMutation(caretCircuit);
					xn.add(caretComponent);
					a = xn.toAction((LC.createComplexStringBinding("removeComponentAction",
							Text.FACTORY.getDisplayGetter().getValue())));
				} else {
					Object obj = caretComponent.getFeature(TextEditable.class);
					if (obj == null) { // should never happen
						a = null;
					} else {
						TextEditable editable = (TextEditable) obj;
						a = editable.getCommitAction(caretCircuit, e.getOldText(), e.getText());
					}
				}
			}

			caretCircuit = null;
			caretComponent = null;
			caretCreatingText = false;
			caret = null;

			if (a != null) proj.doAction(a);
		}

		public void circuitChanged(CircuitEvent event) {
			if (event.getCircuit() != caretCircuit) {
				event.getCircuit().removeCircuitListener(this);
				return;
			}
			int action = event.getAction();
			if (action == CircuitEvent.ACTION_REMOVE) {
				if (event.getData() == caretComponent) {
					caret.cancelEditing();
				}
			} else if (action == CircuitEvent.ACTION_CLEAR) {
				if (caretComponent != null) {
					caret.cancelEditing();
				}
			}
		}
	}

	private static Cursor cursor = Cursor.TEXT;

	private static final ImageView icon = IconsManager.getIcon("text.gif");

	private MyListener listener = new MyListener();
	private AttributeSet attrs;
	private Caret caret = null;
	private boolean caretCreatingText = false;
	private LayoutCanvas caretCanvas = null;
	private Circuit caretCircuit = null;
	private Component caretComponent = null;

	public TextTool() {
		attrs = Text.FACTORY.createAttributeSet();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof TextTool;
	}

	@Override
	public int hashCode() {
		return TextTool.class.hashCode();
	}

	@Override
	public String getName() {
		return "Text Tool";
	}

	@Override
	public StringBinding getDisplayName() {
		return LC.createStringBinding("textTool");
	}

	@Override
	public StringBinding getDescription() {
		return LC.createStringBinding("textToolDesc");
	}

	@Override
	public AttributeSet getAttributeSet() {
		return attrs;
	}

	@Override
	public ImageView getIcon(){
		return icon;
	}

	@Override
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {
		if (caret != null) caret.draw(context.getGraphics());
	}

	@Override
	public void deselect(LayoutCanvas canvas) {
		if (caret != null) {
			caret.stopEditing();
			caret = null;
		}
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		Project proj = canvas.getProject();
		Circuit circ = canvas.getCircuit();

		if (!proj.getLogisimFile().contains(circ)) {
			if (caret != null) caret.cancelEditing();
			canvas.setErrorMessage(LC.createStringBinding("cannotModifyError"),null);
			return;
		}

		// Maybe user is clicking within the current caret.
		if (caret != null) {
			if (caret.getBounds(g).contains(e.localX,e.localY)) { // Yes
				caret.mousePressed(e);
				return;
			} else { // No. End the current caret.
				caret.stopEditing();
			}
		}
		// caret will be null at this point

		// Otherwise search for a new caret.
		int x = e.localX;
		int y = e.localY;
		Location loc = Location.create(x, y);
		ComponentUserEvent event = new ComponentUserEvent(canvas, x, y,e);

		// First search in selection.
		for (Component comp : canvas.getSelection().getComponentsContaining(loc, g)) {
			TextEditable editable = (TextEditable) comp.getFeature(TextEditable.class);
			if (editable != null) {
				caret = editable.getTextCaret(event);
				if (caret != null) {
					proj.getFrameController().setAttributeTable(circ,comp);
					caretComponent = comp;
					caretCreatingText = false;
					break;
				}
			}
		}

		// Then search in circuit
		if (caret == null) {
			for (Component comp : circ.getAllContaining(loc, g)) {
				TextEditable editable = (TextEditable) comp.getFeature(TextEditable.class);
				if (editable != null) {
					caret = editable.getTextCaret(event);
					if (caret != null) {
						proj.getFrameController().setAttributeTable(circ, comp);
						caretComponent = comp;
						caretCreatingText = false;
						break;
					}
				}
			}
		}

		// if nothing found, create a new label
		if (caret == null) {
			if (loc.getX() < 0 || loc.getY() < 0) return;
			AttributeSet copy = (AttributeSet) attrs.clone();
			caretComponent = Text.FACTORY.createComponent(loc, copy);
			caretCreatingText = true;
			TextEditable editable = (TextEditable) caretComponent.getFeature(TextEditable.class);
			if (editable != null) {
				caret = editable.getTextCaret(event);
				proj.getFrameController().setAttributeTable(circ, caretComponent);
			}
		}

		if (caret != null) {
			caretCanvas = canvas;
			caretCircuit = canvas.getCircuit();
			caret.addCaretListener(listener);
			caretCircuit.addCircuitListener(listener);
		}

	}

	@Override
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		//TODO: enhance label editing
	}

	@Override
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		//TODO: enhance label editing
	}

	@Override
	public void keyPressed(LayoutCanvas canvas, KeyEvent e) {
		if (caret != null) {
			caret.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(LayoutCanvas canvas, KeyEvent e) {
		if (caret != null) {
			caret.keyReleased(e);
		}
	}

	@Override
	public void keyTyped(LayoutCanvas canvas, KeyEvent e) {
		if (caret != null) {
			caret.keyTyped(e);
		}
	}

	@Override
	public Cursor getCursor() {
		return cursor;
	}

}


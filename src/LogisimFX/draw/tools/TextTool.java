/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.draw.tools;

import LogisimFX.IconsManager;
import LogisimFX.draw.actions.ModelAddAction;
import LogisimFX.draw.actions.ModelEditTextAction;
import LogisimFX.draw.actions.ModelRemoveAction;
import LogisimFX.draw.util.Caret;
import LogisimFX.draw.util.CaretEvent;
import LogisimFX.draw.util.CaretListener;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.shapes.DrawAttr;
import LogisimFX.draw.shapes.Text;
import LogisimFX.data.Attribute;
import LogisimFX.data.Location;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.Collections;
import java.util.List;

public class TextTool extends AbstractTool {
	
	private DrawingAttributeSet attrs;

	private Text curText;
	private AppearanceCanvas curCanvas;
	private boolean isTextNew;

	private Caret caret = null;
	private Text textComponent;
	private AppearanceCanvas caretCanvas;
	private boolean caretCreatingText;

	private MyListener listener = new MyListener();

    private class MyListener
            implements CaretListener {

        public void editingCanceled(CaretEvent e) {

            if (e.getCaret() != caret) {
                e.getCaret().removeCaretListener(this);
                return;
            }

            caret.removeCaretListener(this);

            textComponent = null;
            caretCreatingText = false;
            caret = null;

        }

        public void editingStopped(CaretEvent e) {

            if (e.getCaret() != caret) {
                e.getCaret().removeCaretListener(this);
                return;
            }

            caret.removeCaretListener(this);

            String val = caret.getText();
            boolean isEmpty = (val == null || val.equals(""));

            if (caretCreatingText) {

                if (!isEmpty) {
					System.out.println("add act");
					curText.setText(caret.getText());
                    caretCanvas.doAction(new ModelAddAction(caretCanvas.getModel(), curText));
                }

            } else {

                if (isEmpty && textComponent != null) {
					System.out.println("remove act");
                    caretCanvas.doAction(new ModelRemoveAction(caretCanvas.getModel(), curText));
                } else {
					System.out.println("edit act");
                    caretCanvas.doAction(new ModelEditTextAction(caretCanvas.getModel(), curText, e.getText()));
                }

            }

            textComponent = null;
            caretCreatingText = false;
            caret = null;

        }

    }
	
	public TextTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
		curText = null;
		isTextNew = false;
	}

	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("text.gif");
	}

	@Override
	public Cursor getCursor() {
		return Cursor.TEXT;
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.ATTRS_TEXT_TOOL;
	}


	@Override
	public void toolSelected(AppearanceCanvas canvas) {
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		System.out.println("deselect");
		if (caret != null) {
			caret.stopEditing();
			caret = null;
			textComponent = null;
		}
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {

		Text clicked = null;
		boolean found = false;
		int mx = e.localX;
		int my = e.localY;

        // Maybe user is clicking within the current caret.
        if (caret != null) {
            if (caret.getBounds(canvas.getGraphics()).contains(e.localX,e.localY)) { // Yes
                caret.mousePressed(e);
                //caretCreatingText = false;
                return;
            } else { // No. End the current caret.
                caret.stopEditing();
            }
        }

		Location mloc = Location.create(mx, my);
		for (CanvasObject o : canvas.getModel().getObjectsFromTop()) {
			if (o instanceof Text && o.contains(mloc, true)) {
				clicked = (Text) o;
				found = true;
				//caretCreatingText = false;
				caret = clicked.getTextField().getCaret(canvas.getGraphics(), mx,my);
				break;
			}
		}
		if (!found) {
			clicked = attrs.applyTo(new Text(mx, my, ""));
		}

		curText = clicked;
		curCanvas = canvas;
		isTextNew = !found;

        // if nothing found, create a new label
        if (caret == null) {

            if (mloc.getX() < 0 || mloc.getY() < 0) return;
            textComponent = new Text(e.localX, e.localY, "");
            attrs.applyTo(textComponent);
            caret = textComponent.getTextField().getCaret(canvas.getGraphics(),mx,my);
            clicked = textComponent;
			System.out.println(clicked.getAttributes());
            caretCreatingText = true;

        }

        if (caret != null) {
            caretCanvas = canvas;
            caret.addCaretListener(listener);
        }

        canvas.getSelection().setSelected(clicked, true);
        canvas.getSelection().setHidden(Collections.singleton(clicked), true);

	}

	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {
		if (caret != null) {
			caret.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		if (caret != null) {
			caret.keyReleased(e);
		}
	}

	@Override
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) {
		if (caret != null) {
			caret.keyTyped(e);
		}
	}
	
	@Override
	public void draw(AppearanceCanvas canvas) {
		if (caret != null) caret.draw(canvas.getGraphics());
	}

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.util;

import LogisimFX.OldFontmetrics;
import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;

import com.sun.javafx.tk.FontMetrics;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.LinkedList;

class TextFieldCaret implements Caret, TextFieldListener {

	private LinkedList<CaretListener> listeners = new LinkedList<CaretListener>();
	private TextField field;
	private Graphics g;
	private String oldText;
	private String curText;
	private int pos;

	public TextFieldCaret(TextField field, Graphics g, int pos) {
		this.field = field;
		this.g = g;
		this.oldText = field.getText();
		this.curText = field.getText();
		this.pos = pos;

		field.addTextFieldListener(this);
	}
	public TextFieldCaret(TextField field, Graphics g, int x, int y) {
		this(field, g, 0);
		moveCaret(x, y);
	}

	public void addCaretListener(CaretListener l) {
		listeners.add(l);
	}

	public void removeCaretListener(CaretListener l) {
		listeners.remove(l);
	}



	public String getText() { return curText; }

	public void commitText(String text) {
		curText = text;
		pos = curText.length();
		field.setText(text);
	}

	public void draw(Graphics g) {

		if (field.getFont() != null) g.setFont(field.getFont());
		// draw boundary
		Bounds bds = getBounds(g);
		g.setColor(Color.WHITE);
		g.c.fillRect(bds.getX(), bds.getY(),
				bds.getWidth(), bds.getHeight());
		g.setColor(Color.BLACK);
		g.c.strokeRect(bds.getX(), bds.getY(),
				bds.getWidth(), bds.getHeight());

		// draw text
		int x = field.getX();
		int y = field.getY();
		FontMetrics fm = g.getFontmetricsForFont(field.getFont());
		int width = OldFontmetrics.computeStringWidth(fm,curText);
		int ascent = (int)fm.getAscent();
		int descent = (int)fm.getDescent();
		switch (field.getHAlign()) {
			case TextField.H_CENTER:    x -= width / 2; break;
			case TextField.H_RIGHT:  x -= width; break;
			default:                    break;
		}
		switch (field.getVAlign()) {
			case TextField.V_TOP:      y += ascent; break;
			case TextField.V_CENTER:    y += (ascent - descent) / 2; break;
			case TextField.V_BOTTOM:    y -= descent; break;
			default:                    break;
		}
		g.c.fillText(curText, x, y);

		// draw cursor
		if (pos > 0) x += Math.ceil(OldFontmetrics.computeStringWidth(fm,curText.substring(0, pos)));
		g.c.strokeLine(x, y, x, y - ascent);

	}

	public Bounds getBounds(Graphics g) {
		int x = field.getX();
		int y = field.getY();
		Font font = field.getFont();
		FontMetrics fm;
		if (font == null)   fm = g.getFontMetrics();
		else                fm = g.getFontmetricsForFont(font);
		int width = OldFontmetrics.computeStringWidth(fm,curText);
		int ascent = (int)fm.getAscent();
		int descent = (int)fm.getDescent();
		int height = ascent + descent;
		switch (field.getHAlign()) {
			case TextField.H_CENTER:    x -= width / 2; break;
			case TextField.H_RIGHT:  x -= width; break;
			default:                    break;
		}
		switch (field.getVAlign()) {
			case TextField.V_TOP:      y += ascent; break;
			case TextField.V_CENTER:    y += (ascent - descent) / 2; break;
			case TextField.V_BOTTOM:    y -= descent; break;
			default:                    break;
		}
		return Bounds.create(x, y - ascent, width, height)
				.add(field.getBounds(g))
				.expand(3);
	}

	public void cancelEditing() {
		CaretEvent e = new CaretEvent(this, oldText, oldText);
		curText = oldText;
		pos = curText.length();
		for (CaretListener l : new ArrayList<CaretListener>(listeners)) {
			l.editingCanceled(e);
		}
		field.removeTextFieldListener(this);
	}

	public void stopEditing() {
		CaretEvent e = new CaretEvent(this, oldText, curText);
		field.setText(curText);
		for (CaretListener l : new ArrayList<CaretListener>(listeners)) {
			l.editingStopped(e);
		}
		field.removeTextFieldListener(this);
	}

	public void mousePressed(AppearanceCanvas.CME e) {
		//TODO: enhance label editing
		moveCaret(e.localX, e.localY);
	}

	public void mouseDragged(AppearanceCanvas.CME e) {
		//TODO: enhance label editing
	}

	public void mouseReleased(AppearanceCanvas.CME e) {
		//TODO: enhance label editing
		moveCaret(e.localX, e.localY);
	}

	public void keyPressed(KeyEvent e) {
		if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) return;
		switch (e.getCode()) {
		case LEFT:
		case KP_LEFT:
			if (pos > 0) --pos;
			break;
		case RIGHT:
		case KP_RIGHT:
			if (pos < curText.length()) ++pos;
			break;
		case HOME:
			pos = 0;
			break;
		case END:
			pos = curText.length();
			break;
		case ESCAPE:
		case CANCEL:
			cancelEditing();
			break;
		case CLEAR:
			curText = "";
			pos = 0;
			break;
		case ENTER:
			stopEditing();
			break;
		case BACK_SPACE:
			if (pos > 0) {
				curText = curText.substring(0, pos - 1)
					+ curText.substring(pos);
				--pos;
			}
			break;
		case DELETE:
			if (pos < curText.length()) {
				curText = curText.substring(0, pos)
					+ curText.substring(pos + 1);
			}
			break;
		case INSERT:
		case COPY:
		case CUT:
		case PASTE:
			//TODO: enhance label editing
			break;
		default:
			; // ignore
		}
	}

	public void keyReleased(KeyEvent e) { }

	public void keyTyped(KeyEvent e) {

		if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) return;

		char c = e.getCharacter().toCharArray()[0];

		//System.out.println("is not ind "+ (e.getCode() != KeyCode.UNDEFINED)+" "+e.getCode()+" "+e.getCharacter()+" "+e.getCode().getName());
		//System.out.println("is iso " +(!Character.isISOControl(c)));

		if (c == '\n') {
			stopEditing();
		} else if (
				/*e.getCode() != KeyCode.UNDEFINED &&*/
				!Character.isISOControl(c)) {
			if (pos < curText.length()) {
				curText = curText.substring(0, pos) + c
					+ curText.substring(pos);
			} else {
				curText += c;
			}
			++pos;
		}
	}

	private void moveCaret(int x, int y) {
		Bounds bds = getBounds(g);
		FontMetrics fm = g.getFontMetrics();
		x -= bds.getX();
		int last = 0;
		for (int i = 0; i < curText.length(); i++) {
			int cur = OldFontmetrics.computeStringWidth(fm,curText.substring(0, i + 1));
			if (x <= (last + cur) / 2) {
				pos = i;
				return;
			}
			last = cur;
		}
		pos = curText.length();
	}
	
	public void textChanged(TextFieldEvent e) {
		curText = field.getText();
		oldText = curText;
		pos = curText.length();
	}

}

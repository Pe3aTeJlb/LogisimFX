/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.shapes;

import LogisimFX.OldFontmetrics;
import LogisimFX.draw.LC;
import LogisimFX.draw.model.AbstractCanvasObject;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.model.Handle;
import LogisimFX.draw.model.HandleGesture;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeOption;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.draw.util.TextField;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.util.UnmodifiableList;

import com.sun.javafx.tk.FontMetrics;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;

public class Text extends AbstractCanvasObject {

	public static final int LEFT = 2;
	public static final int RIGHT = 4;
	public static final int CENTER = 3;

	public static final int TOP = 8;
	public static final int MIDDLE = 9;
	public static final int BASELINE = 10;
	public static final int BOTTOM = 11;

	private TextField textField;

	private Color color;
	private Font font;

	private int x, y;
	private int horzAlign, vertAlign;
	private int width;
	private int ascent;
	private int descent;
	private boolean dimsKnown;
	private int[] charX;
	private int[] charY;
	
	public Text(int x, int y, String text) {

		this(x, y, LEFT, BASELINE, text,
				DrawAttr.DEFAULT_FONT, Color.BLACK);


	}

	private Text(int x, int y, int halign, int valign, String text, Font font,
		Color color) {

		this.x = x;
		this.y = y;
		this.horzAlign = halign;
		this.vertAlign = valign;
		this.font = font;
		this.color = color;
		this.dimsKnown = false;
		textField = new TextField(x,y,halign,valign,font);
		textField.setText(text);

	}

	@Override
	public Text clone() {

		Text ret = (Text) super.clone();
		return ret;

	}

	@Override
	public boolean matches(CanvasObject other) {

		if (other instanceof Text) {
			Text that = (Text) other;
			return this.textField.equals(that.textField);
		} else {
			return false;
		}

	}

	@Override
	public int matchesHashCode() {
		return this.hashCode();
	}

	@Override
	public Element toSvgElement(Document doc) {
		return SvgCreator.createText(doc, this);
	}

	public TextField getTextField(){ return textField; }

	public Location getLocation() {
		return Location.create(textField.getX(),textField.getY());
	}

	public String getText() {
		return textField.getText();
	}

	public void setText(String value) {
		dimsKnown = false;
		textField.setText(value);
	}

	@Override
	public String getDisplayName() {
		return LC.get("shapeText");
	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.ATTRS_TEXT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == DrawAttr.FONT) {
			dimsKnown = false;
			return (V) font;
		} else if (attr == DrawAttr.FILL_COLOR) {
			return (V) color;
		} else if (attr == DrawAttr.ALIGNMENT) {
			dimsKnown = false;
			int halign = horzAlign;
			AttributeOption h;
			if (halign == Text.LEFT) {
				h = DrawAttr.ALIGN_LEFT;
			} else if (halign == Text.RIGHT) {
				h = DrawAttr.ALIGN_RIGHT;
			} else {
				h = DrawAttr.ALIGN_CENTER;
			}
			return (V) h;
		} else {
			return null;
		}

	}

	@Override
	public void updateValue(Attribute<?> attr, Object value) {

		if (attr == DrawAttr.FONT) {
			font = (Font) value;
		} else if (attr == DrawAttr.FILL_COLOR) {
			color = (Color) value;
		} else if (attr == DrawAttr.ALIGNMENT) {
			Integer intVal = (Integer) ((AttributeOption) value).getValue();
			horzAlign = intVal;
		}

	}

	private int getLeftX() {
		switch (horzAlign) {
			case LEFT:   return x;
			case CENTER: return x - width / 2;
			case RIGHT:  return x - width;
			default:     return x;
		}
	}

	private int getBaseY() {
		switch (vertAlign) {
			case TOP:      return y + ascent;
			case MIDDLE:   return y + (ascent - descent) / 2;
			case BASELINE: return y;
			case BOTTOM:   return y - descent;
			default:       return y;
		}
	}

	@Override
	public Bounds getBounds() {
		int x0 = getLeftX();
		int y0 = getBaseY() - ascent;
		int w = width;
		int h = ascent + descent;
		return Bounds.create(x0, y0, w, h);
	}
	
	@Override
	public boolean contains(Location loc, boolean assumeFilled) {
		int qx = loc.getX();
		int qy = loc.getY();
		int x0 = getLeftX();
		int y0 = getBaseY();
		if (qx >= x0 && qx < x0 + width
				&& qy >= y0 - ascent && qy < y0 + descent) {
			int[] xs = charX;
			int[] ys = charY;
			if (xs == null || ys == null) {
				return true;
			} else {
				int i = Arrays.binarySearch(xs, qx - x0);
				if (i < 0) i = -(i + 1);
				if (i >= xs.length) {
					return false;
				} else {
					int asc = (ys[i] >> 16) & 0xFFFF;
					int desc = ys[i] & 0xFFFF;
					int dy = y0 - qy;
					return dy >= -desc && dy <= asc;
				}
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void translate(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	public List<Handle> getHandles() {
		Bounds bds = getBounds();
		int x = bds.getX();
		int y = bds.getY();
		int w = bds.getWidth();
		int h = bds.getHeight();

		return UnmodifiableList.create(new Handle[] {
				new Handle(this, x, y), new Handle(this, x + w, y),
				new Handle(this, x + w, y + h), new Handle(this, x, y + h) });

	}
	
	@Override
	public List<Handle> getHandles(HandleGesture gesture) {
		return getHandles();
	}
	
	@Override
	public void paint(Graphics g, HandleGesture gesture) {

		g.setFont(font);

		if (!dimsKnown) {
			computeDimensions(g);
		}

		g.setColor(color);
		g.c.fillText(textField.getText(), getLeftX(), getBaseY());

		g.toDefault();

	}

	private void computeDimensions(Graphics g) {

		FontMetrics fm = g.getFontMetrics();
		width = OldFontmetrics.computeStringWidth(fm,textField.getText());
		ascent = (int)fm.getAscent();
		descent = (int)fm.getDescent();

		dimsKnown = true;

	}

}

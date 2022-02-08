/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.shapes;

import LogisimFX.draw.model.AbstractCanvasObject;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeOption;

import javafx.scene.paint.Color;

abstract class FillableCanvasObject extends AbstractCanvasObject {

	private AttributeOption paintType;
	private int strokeWidth;
	private Color strokeColor;
	private Color fillColor;
	
	public FillableCanvasObject() {
		paintType = DrawAttr.PAINT_STROKE;
		strokeWidth = 1;
		strokeColor = Color.BLACK;
		fillColor = Color.WHITE;
	}

	@Override
	public boolean matches(CanvasObject other) {

		if (other instanceof FillableCanvasObject) {
			FillableCanvasObject that = (FillableCanvasObject) other;
			boolean ret = this.paintType == that.paintType;
			if (ret && this.paintType != DrawAttr.PAINT_FILL) {
				ret = ret && this.strokeWidth == that.strokeWidth
					&& this.strokeColor.equals(that.strokeColor);
			}
			if (ret && this.paintType != DrawAttr.PAINT_STROKE) {
				ret = ret && this.fillColor.equals(that.fillColor);
			}
			return ret;
		} else {
			return false;
		}

	}

	@Override
	public int matchesHashCode() {

		int ret = paintType.hashCode();
		if (paintType != DrawAttr.PAINT_FILL) {
			ret = ret * 31 + strokeWidth;
			ret = ret * 31 + strokeColor.hashCode();
		} else {
			ret = ret * 31 * 31;
		}
		if (paintType != DrawAttr.PAINT_STROKE) {
			ret = ret * 31 + fillColor.hashCode();
		} else {
			ret = ret * 31;
		}

		return ret;

	}

	public AttributeOption getPaintType() {
		return paintType;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == DrawAttr.PAINT_TYPE) {
			return (V) paintType;
		} else if (attr == DrawAttr.STROKE_COLOR) {
			return (V) strokeColor;
		} else if (attr == DrawAttr.FILL_COLOR) {
			return (V) fillColor;
		} else if (attr == DrawAttr.STROKE_WIDTH) {
			return (V) Integer.valueOf(strokeWidth);
		} else {
			return null;
		}

	}

	@Override
	public void updateValue(Attribute<?> attr, Object value) {

		if (attr == DrawAttr.PAINT_TYPE) {
			paintType = (AttributeOption) value;
			fireAttributeListChanged();
		} else if (attr == DrawAttr.STROKE_COLOR) {
			strokeColor = (Color) value;
		} else if (attr == DrawAttr.FILL_COLOR) {
			fillColor = (Color) value;
		} else if (attr == DrawAttr.STROKE_WIDTH) {
			strokeWidth = ((Integer) value).intValue();
		}

	}

}

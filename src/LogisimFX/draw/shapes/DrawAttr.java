/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.shapes;

import LogisimFX.draw.LC;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeOption;
import LogisimFX.data.Attributes;
import LogisimFX.util.UnmodifiableList;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.List;

public class DrawAttr {

	public static final Font DEFAULT_FONT
		= Font.font("SansSerif", FontWeight.NORMAL, FontPosture.REGULAR, 12);

	public static final AttributeOption ALIGN_LEFT
		= new AttributeOption(Integer.valueOf(Text.LEFT), LC.createStringBinding("alignStart"));
	public static final AttributeOption ALIGN_CENTER
		= new AttributeOption(Integer.valueOf(Text.CENTER), LC.createStringBinding("alignMiddle"));
	public static final AttributeOption ALIGN_RIGHT
		= new AttributeOption(Integer.valueOf(Text.RIGHT), LC.createStringBinding("alignEnd"));

	public static final AttributeOption PAINT_STROKE
		= new AttributeOption("stroke", LC.createStringBinding("paintStroke"));
	public static final AttributeOption PAINT_FILL
		= new AttributeOption("fill", LC.createStringBinding("paintFill"));
	public static final AttributeOption PAINT_STROKE_FILL
		= new AttributeOption("both", LC.createStringBinding("paintBoth"));

	public static final Attribute<Font> FONT
		= Attributes.forFont("font", LC.createStringBinding("attrFont"));
	public static final Attribute<AttributeOption> ALIGNMENT
		= Attributes.forOption("align", LC.createStringBinding("attrAlign"),
			new AttributeOption[] { ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT });
	public static final Attribute<AttributeOption> PAINT_TYPE
		= Attributes.forOption("paintType", LC.createStringBinding("attrPaint"),
			new AttributeOption[] { PAINT_STROKE, PAINT_FILL, PAINT_STROKE_FILL });
	public static final Attribute<Integer> STROKE_WIDTH
		= Attributes.forIntegerRange("stroke-width", LC.createStringBinding("attrStrokeWidth"), 1, 8);
	public static final Attribute<Color> STROKE_COLOR
		= Attributes.forColor("stroke",LC.createStringBinding("attrStroke"));
	public static final Attribute<Color> FILL_COLOR
		= Attributes.forColor("fill", LC.createStringBinding("attrFill"));
	public static final Attribute<Color> TEXT_DEFAULT_FILL
		= Attributes.forColor("fill", LC.createStringBinding("attrFill"));
	public static final Attribute<Integer> CORNER_RADIUS
		= Attributes.forIntegerRange("rx", LC.createStringBinding("attrRx"), 1, 1000);

	public static final List<Attribute<?>> ATTRS_TEXT // for text
		= createAttributes(new Attribute[] { FONT, ALIGNMENT, FILL_COLOR });
	public static final List<Attribute<?>> ATTRS_TEXT_TOOL // for text tool
		= createAttributes(new Attribute[] { FONT, ALIGNMENT, TEXT_DEFAULT_FILL });
	public static final List<Attribute<?>> ATTRS_STROKE // for line, polyline
		= createAttributes(new Attribute[] { STROKE_WIDTH, STROKE_COLOR });
	
	// attribute lists for rectangle, oval, polygon
	private static final List<Attribute<?>> ATTRS_FILL_STROKE
		= createAttributes(new Attribute[] { PAINT_TYPE,
				STROKE_WIDTH, STROKE_COLOR });
	private static final List<Attribute<?>> ATTRS_FILL_FILL
		= createAttributes(new Attribute[] { PAINT_TYPE, FILL_COLOR });
	private static final List<Attribute<?>> ATTRS_FILL_BOTH
		= createAttributes(new Attribute[] { PAINT_TYPE,
				STROKE_WIDTH, STROKE_COLOR, FILL_COLOR });
	
	// attribute lists for rounded rectangle
	private static final List<Attribute<?>> ATTRS_RRECT_STROKE
		= createAttributes(new Attribute[] { PAINT_TYPE,
				STROKE_WIDTH, STROKE_COLOR, CORNER_RADIUS });
	private static final List<Attribute<?>> ATTRS_RRECT_FILL
		= createAttributes(new Attribute[] { PAINT_TYPE, 
				FILL_COLOR, CORNER_RADIUS });
	private static final List<Attribute<?>> ATTRS_RRECT_BOTH
		= createAttributes(new Attribute[] { PAINT_TYPE,
				STROKE_WIDTH, STROKE_COLOR, FILL_COLOR, CORNER_RADIUS });
	
	private static List<Attribute<?>> createAttributes(Attribute<?>[] values) {
		return UnmodifiableList.create(values);
	}
	
	public static List<Attribute<?>> getFillAttributes(AttributeOption paint) {

		if (paint == PAINT_STROKE) {
			return ATTRS_FILL_STROKE;
		} else if (paint == PAINT_FILL) {
			return ATTRS_FILL_FILL;
		} else {
			return ATTRS_FILL_BOTH;
		}

	}
	
	public static List<Attribute<?>> getRoundRectAttributes(AttributeOption paint) {

		if (paint == PAINT_STROKE) {
			return ATTRS_RRECT_STROKE;
		} else if (paint == PAINT_FILL) {
			return ATTRS_RRECT_FILL;
		} else {
			return ATTRS_RRECT_BOTH;
		}

	}

}

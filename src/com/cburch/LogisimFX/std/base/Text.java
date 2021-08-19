/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.base;

import com.cburch.LogisimFX.comp.TextField;
import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.instance.Instance;
import com.cburch.LogisimFX.instance.InstanceFactory;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.util.GraphicsUtil;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;



public class Text extends InstanceFactory {

	public static Attribute<String> ATTR_TEXT = Attributes.forString("text",
			LC.createStringBinding("textTextAttr"));
	public static Attribute<Font> ATTR_FONT = Attributes.forFont("font",
			LC.createStringBinding("textFontAttr"));
	public static Attribute<AttributeOption> ATTR_HALIGN = Attributes.forOption("halign",
			LC.createStringBinding("textHorzAlignAttr"), new AttributeOption[] {
			new AttributeOption(Integer.valueOf(TextField.H_LEFT),
				"left", LC.createStringBinding("textHorzAlignLeftOpt")),
			new AttributeOption(Integer.valueOf(TextField.H_RIGHT),
				"right", LC.createStringBinding("textHorzAlignRightOpt")),
			new AttributeOption(Integer.valueOf(TextField.H_CENTER),
				"center", LC.createStringBinding("textHorzAlignCenterOpt")),
		});
	public static Attribute<AttributeOption> ATTR_VALIGN = Attributes.forOption("valign",
			LC.createStringBinding("textVertAlignAttr"), new AttributeOption[] {
			new AttributeOption(Integer.valueOf(TextField.V_TOP),
				"top", LC.createStringBinding("textVertAlignTopOpt")),
			new AttributeOption(Integer.valueOf(TextField.V_BASELINE),
				"base", LC.createStringBinding("textVertAlignBaseOpt")),
			new AttributeOption(Integer.valueOf(TextField.V_BOTTOM),
				"bottom", LC.createStringBinding("textVertAlignBottomOpt")),
			new AttributeOption(Integer.valueOf(TextField.H_CENTER),
				"center", LC.createStringBinding("textVertAlignCenterOpt")),
		});

	public static final Text FACTORY = new Text();

	private Text() {

		super("Text", LC.createStringBinding("textComponent"));
		setIcon("text.gif");
		setShouldSnap(false);

	}
	
	@Override
	public AttributeSet createAttributeSet() {
		return new TextAttributes();
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrsBase) {

		TextAttributes attrs = (TextAttributes) attrsBase;
		String text = attrs.getText();
		if (text == null || text.equals("")) {
			return Bounds.EMPTY_BOUNDS;
		} else {
			Bounds bds = attrs.getOffsetBounds();
			if (bds == null) {
				bds = estimateBounds(attrs);
				attrs.setOffsetBounds(bds);
			}
			return bds == null ? Bounds.EMPTY_BOUNDS : bds;
		}

	}

	private Bounds estimateBounds(TextAttributes attrs) {

		// TODO - you can imagine being more clever here
		String text = attrs.getText();
		if (text == null || text.length() == 0) return Bounds.EMPTY_BOUNDS;
		int size = (int)attrs.getFont().getSize();
		int h = size;
		int w = size * text.length() / 2;
		int ha = attrs.getHorizontalAlign();
		int va = attrs.getVerticalAlign();
		int x;
		int y;
		if (ha == TextField.H_LEFT) {
			x = 0;
		} else if (ha == TextField.H_RIGHT) {
			x = -w;
		} else {
			x = -w / 2;
		}
		if (va == TextField.V_TOP) {
			y = 0;
		} else if (va == TextField.V_CENTER) {
			y = -h / 2;
		} else {
			y = -h;
		}
		return Bounds.create(x, y, w, h);

	}

	//
	// graphics methods
	//
	@Override
	public void paintGhost(InstancePainter painter) {

		TextAttributes attrs = (TextAttributes) painter.getAttributeSet();
		String text = attrs.getText();
		if (text == null || text.equals("")) return;
		
		int halign = attrs.getHorizontalAlign();
		int valign = attrs.getVerticalAlign();
		Graphics g = painter.getGraphics();
		g.setFont(attrs.getFont());
		GraphicsUtil.drawText(g, text, 0, 0, halign, valign);
		
		String textTrim = text.endsWith(" ") ? text.substring(0, text.length() - 1) : text;
		Bounds newBds;
		if (textTrim.equals("")) {
			newBds = Bounds.EMPTY_BOUNDS;
		} else {
			Rectangle bdsOut = GraphicsUtil.getTextBounds(g, textTrim, 0, 0,
					halign, valign);
			newBds = Bounds.create(bdsOut).expand(4);
		}
		if (attrs.setOffsetBounds(newBds)) {
			Instance instance = painter.getInstance();
			if (instance != null) instance.recomputeBounds();
		}

		g.toDefault();

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		Graphics g = painter.getGraphics();
		g.c.translate(x, y);
		g.setColor(Color.BLACK);
		paintGhost(painter);
		g.c.translate(-x, -y);

		g.toDefault();

	}

	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		configureLabel(instance);
		instance.addAttributeListener();

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == ATTR_HALIGN || attr == ATTR_VALIGN) {
			configureLabel(instance);
		}

	}
	
	private void configureLabel(Instance instance) {

		TextAttributes attrs = (TextAttributes) instance.getAttributeSet();
		Location loc = instance.getLocation();
		instance.setTextField(ATTR_TEXT, ATTR_FONT, loc.getX(), loc.getY(),
				attrs.getHorizontalAlign(), attrs.getVerticalAlign());

	}      

	@Override
	public void propagate(InstanceState state) { }

}

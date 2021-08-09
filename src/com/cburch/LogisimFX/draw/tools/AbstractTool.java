/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.draw.canvas.AppearanceCanvas;
import com.cburch.LogisimFX.draw.canvas.CanvasTool;
import com.cburch.LogisimFX.data.Attribute;

import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.List;

public abstract class AbstractTool extends CanvasTool {

	public static AbstractTool[] getTools(DrawingAttributeSet attrs) {

		return new AbstractTool[] {
			new SelectTool(),
			new LineTool(attrs),
			new CurveTool(attrs),
			new PolyTool(false, attrs),
			new RectangleTool(attrs),
			new RoundRectangleTool(attrs),
			new OvalTool(attrs),
			new PolyTool(true, attrs),
		};

	}
	
	public abstract ImageView getIcon();
	public abstract List<Attribute<?>> getAttributes();
	public String getDescription() { return null; }
	
	//
	// CanvasTool methods
	//
	@Override
	public abstract Cursor getCursor();
	
	@Override
	public void toolSelected(AppearanceCanvas canvas) { }
	@Override
	public void toolDeselected(AppearanceCanvas canvas) { }
	
	@Override
	public void mouseMoved(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	@Override
	public void mouseEntered(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	@Override
	public void mouseExited(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }

	/** This is because a popup menu may result from the subsequent mouse release */ 
	@Override
	public void cancelMousePress(AppearanceCanvas canvas) { }

	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) { }
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) { }
	@Override
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) { }
	
	@Override
	public void draw(Graphics g) { }

}

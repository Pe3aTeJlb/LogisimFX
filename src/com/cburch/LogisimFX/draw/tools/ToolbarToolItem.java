/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.draw.toolbar.ToolbarItem;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;

public class ToolbarToolItem implements ToolbarItem {

	private AbstractTool tool;
	private ImageView icon;
	
	public ToolbarToolItem(AbstractTool tool) {
		this.tool = tool;
		this.icon = tool.getIcon();
	}
	
	public AbstractTool getTool() {
		return tool;
	}
	
	public boolean isSelectable() {
		return true;
	}
	
	public void paintIcon(Component destination, Graphics g) {
		if (icon == null) {
			g.setColor(new Color(255, 128, 128));
			g.fillRect(4, 4, 8, 8);
			g.setColor(Color.BLACK);
			g.drawLine(4, 4, 12, 12);
			g.drawLine(4, 12, 12, 4);
			g.drawRect(4, 4, 8, 8);
		} else {
			icon.paintIcon(destination, g, 4, 4);
		}
	}
	
	public String getToolTip() {
		return tool.getDescription();
	}
	
	public Dimension getDimension(Object orientation) {
		if (icon == null) {
			return new Dimension(16, 16);
		} else {
			return new Dimension(icon.getIconWidth() + 8, icon.getIconHeight() + 8);
		}
	}
}

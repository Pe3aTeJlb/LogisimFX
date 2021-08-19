/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;


import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.newgui.ContextMenuManager;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.Selection;
import com.cburch.LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.ImageView;

import java.util.Collection;

public class MenuTool extends Tool {

	private static final ImageView icon = IconsManager.getIcon("drawoval.gif");

	public MenuTool() { }
	
	@Override
	public boolean equals(Object other) {
		return other instanceof MenuTool;
	}
	
	@Override
	public int hashCode() {
		return MenuTool.class.hashCode();
	}

	@Override
	public String getName() { return "Menu Tool"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("menuTool"); }

	@Override
	public StringBinding getDescription() { return LC.createStringBinding("menuToolDesc"); }

	@Override
	public ImageView getIcon() {
		return icon;
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		int x = e.localX;
		int y = e.localY;
		Location pt = Location.create(x, y);

		ContextMenu menu;
		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		Collection<Component> in_sel = sel.getComponentsContaining(pt, g);
		if (!in_sel.isEmpty()) {
			Component comp = in_sel.iterator().next();
			if (sel.getComponents().size() > 1) {
				menu = ContextMenuManager.SelectionContextMenu(canvas);
			} else {
				menu = ContextMenuManager.ComponentDefaultContextMenu(proj, canvas.getCircuit(), comp);
				MenuExtender extender = (MenuExtender) comp.getFeature(MenuExtender.class);
				if (extender != null) extender.configureMenu(menu, proj);
			}
		} else {
			Collection<Component> cl = canvas.getCircuit().getAllContaining(pt, g);
			if (!cl.isEmpty()) {
				Component comp = cl.iterator().next();
				menu = ContextMenuManager.ComponentDefaultContextMenu(proj, canvas.getCircuit(), comp);
				MenuExtender extender = (MenuExtender) comp.getFeature(MenuExtender.class);
				if (extender != null) extender.configureMenu(menu, proj);
			} else {
				menu = null;
			}
		}

		if (menu != null) {
			canvas.showContextMenu(menu, e.event.getScreenX(),e.event.getScreenY());
		}

	}

}

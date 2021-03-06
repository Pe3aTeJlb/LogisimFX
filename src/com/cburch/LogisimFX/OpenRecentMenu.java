/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX;


import com.cburch.logisim.prefs.AppPreferences;
//import LogisimFX.proj.Project;
//import LogisimFX.proj.ProjectActions;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenRecentMenu extends Menu implements PropertyChangeListener {

	private Localizer lc;

	private final int MAX_ITEM_LENGTH = 50;
	
	private class RecentItem extends MenuItem {

		private File file;
		
		RecentItem(File file) {

			super(getFileText(file));
			this.file = file;
			this.setOnAction(event -> {
				//ToDO: implement dis shit
				//Component par = proj == null ? null : proj.getFrame().getCanvas();
				//ProjectActions.doOpen(par, proj, file);
			});

		}


	}

	private List<RecentItem> recentItems;
	
	public OpenRecentMenu(Localizer l) {

		lc = l;

		this.recentItems = new ArrayList<>();
		AppPreferences.addPropertyChangeListener(AppPreferences.RECENT_PROJECTS, this);
		renewItems();

	}
	
	private void renewItems() {

		this.getItems().clear();
		recentItems.clear();
		
		List<File> files = AppPreferences.getRecentFiles();

		if (files.isEmpty()) {
			recentItems.add(new RecentItem(null));
		} else {
			for (File file : files) {
				recentItems.add(new RecentItem(file));
			}
		}

		this.getItems().addAll(recentItems);

	}
	
	private String getFileText(File file) {

		if (file == null) {

			return lc.get("fileOpenRecentNoChoices");

		} else {

			String ret;

			try {
				ret = file.getCanonicalPath();
			} catch (IOException e) {
				ret = file.toString();
			}

			if (ret.length() <= MAX_ITEM_LENGTH) {

				return ret;

			} else {

				ret = ret.substring(ret.length() - MAX_ITEM_LENGTH + 3);
				int splitLoc = ret.indexOf(File.separatorChar);
				if (splitLoc >= 0) {
					ret = ret.substring(splitLoc);
				}
				return "..." + ret;

			}

		}

	}

	public void propertyChange(PropertyChangeEvent event) {
		//ToDO: remove dis shit
		if (event.getPropertyName().equals(AppPreferences.RECENT_PROJECTS)) {
			renewItems();
		}
	}

}

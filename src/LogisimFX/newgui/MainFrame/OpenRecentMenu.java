/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.newgui.MainFrame;

import LogisimFX.localization.LC_menu;
import LogisimFX.localization.Localizer;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectActions;
import LogisimFX.prefs.AppPreferences;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenRecentMenu extends Menu implements PropertyChangeListener {

	private final int MAX_ITEM_LENGTH = 50;

	private static Localizer lc = LC_menu.getInstance();

	private Project proj;
	
	private class RecentItem extends MenuItem {

		private File file;
		
		RecentItem(File file) {

			super(getFileText(file));
			this.file = file;
			this.setOnAction(event -> {
				if(file != null) ProjectActions.doOpen(null, file);
			});

		}

	}

	private List<RecentItem> recentItems;
	
	public OpenRecentMenu(Project project) {

		proj = project;

		this.recentItems = new ArrayList<>();
		AppPreferences.addPropertyChangeListener(AppPreferences.RECENT_PROJECTS, this);
		renewItems();

	}
	
	public void renewItems() {

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
		if (event.getPropertyName().equals(AppPreferences.RECENT_PROJECTS)) {
			renewItems();
		}
	}


}

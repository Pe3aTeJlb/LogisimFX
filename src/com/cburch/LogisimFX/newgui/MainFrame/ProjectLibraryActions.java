/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.FileSelector;
import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.file.Loader;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.Library;


import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ProjectLibraryActions {

	private static Localizer lc = new Localizer("menu");

	private static class BuiltinOption {

		Library lib;
		BuiltinOption(Library lib) { this.lib = lib; }

		@Override
		public String toString() { return lib.getDisplayName().toString(); }

	}

	public static void doLoadBuiltinLibrary(Project proj) {

		LogisimFile file = proj.getLogisimFile();
		List<Library> baseBuilt = file.getLoader().getBuiltin().getLibraries();
		ArrayList<Library> builtins = new ArrayList<>(baseBuilt);
		builtins.removeAll(file.getLibraries());

		if (builtins.isEmpty()) {
			DialogManager.CreateInfoDialog(lc.get("loadBuiltinErrorTitle"),lc.get("loadBuiltinNoneError"));
			return;
		}

		Library[] libs = DialogManager.CreateLibSelectionDialog(builtins);
		if (libs != null) proj.doAction(LogisimFileActions.loadLibraries(libs));

		/*
		LibraryJList list = new LibraryJList(builtins);
		JScrollPane listPane = new JScrollPane(list);
		int action = JOptionPane.showConfirmDialog(proj.getFrame(), listPane,
				Strings.get("loadBuiltinDialogTitle"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (action == JOptionPane.OK_OPTION) {
			Library[] libs = list.getSelectedLibraries();
			if (libs != null) proj.doAction(LogisimFileActions.loadLibraries(libs));
		}

		 */

	}
	
	public static void doLoadLogisimLibrary(Project proj) {

		Loader loader = proj.getLogisimFile().getLoader();

		FileSelector fs = new FileSelector(proj.getFrameController().getStage());
		File f = fs.OpenCircFile();

		if(f != null){
			Library lib = loader.loadLogisimLibrary(f);
			if (lib != null) {
				proj.doAction(LogisimFileActions.loadLibrary(lib));
			}
		}

	}
	
	public static void doLoadJarLibrary(Project proj) {

		Loader loader = proj.getLogisimFile().getLoader();

		FileSelector fs = new FileSelector(proj.getFrameController().getStage());
		File f = fs.OpenCircFile();

		String className = null;

		// try to retrieve the class name from the "Library-Class"
		// attribute in the manifest. This section of code was contributed
		// by Christophe Jacquet (Request Tracker #2024431).
		JarFile jarFile = null;

		try {
			jarFile = new JarFile(f);
			Manifest manifest = jarFile.getManifest();
			className = manifest.getMainAttributes().getValue("Library-Class");
		} catch (IOException e) {
			// if opening the JAR file failed, do nothing
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			}
		}

		// if the class name was not found, go back to the good old dialog
		if (className == null) {
			className = DialogManager.CreateInputDialog(lc.get("jarClassNameTitle"),
					lc.get("jarClassNamePrompt"));
			// if user canceled selection, abort
			if (className == null) return;
		}

		Library lib = loader.loadJarLibrary(f, className);
		if (lib != null) {
			proj.doAction(LogisimFileActions.loadLibrary(lib));
		}

	}
	
	public static void doUnloadLibraries(Project proj) {

		LogisimFile file = proj.getLogisimFile();
		ArrayList<Library> canUnload = new ArrayList<>();

		for (Library lib : file.getLibraries()) {
			String message = file.getUnloadLibraryMessage(lib);
			if (message == null) canUnload.add(lib);
		}

		if (canUnload.isEmpty()) {
			DialogManager.CreateErrorDialog(lc.get("unloadErrorTitle"),lc.get("unloadNoneError"));
			return;
		}

		Library[] libs = DialogManager.CreateLibSelectionDialog(canUnload);

		if (libs != null) proj.doAction(LogisimFileActions.unloadLibraries(libs));

	}

	public static void doUnloadLibrary(Project proj, Library lib) {

		String message = proj.getLogisimFile().getUnloadLibraryMessage(lib);
		if (message != null) {
			DialogManager.CreateErrorDialog(lc.get("unloadErrorTitle"),message);
		} else {
			proj.doAction(LogisimFileActions.unloadLibrary(lib));
		}

	}

}

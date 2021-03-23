/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.proj;

import com.cburch.LogisimFX.FrameManager;
import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.file.LoadFailedException;
import com.cburch.LogisimFX.file.Loader;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.newgui.LoadingFrame.LoadingScreen;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.start.SplashScreen;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.JFileChoosers;
import com.cburch.logisim.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class ProjectActions {

	private static Localizer lc = new Localizer("LogisimFX/resources/localization/proj");

	private ProjectActions() { }
	
	private static class CreateFrame implements Runnable {
		private Loader loader;
		private Project proj;
		private boolean isStartupScreen;

		public CreateFrame(Loader loader, Project proj, boolean isStartup) {
			this.loader = loader;
			this.proj = proj;
			this.isStartupScreen = isStartup;
		}

		public void run() {

			FrameManager.CreateMainFrame(proj);

			Frame frame = createFrame(null, proj);
			frame.setVisible(true);
			frame.toFront();
			frame.getCanvas().requestFocus();
			if (isStartupScreen) proj.setStartupScreen(true);
		}
	}

	public static Project doNew() {
		return doNew(false);
	}

	public static Project doNew(boolean isStartupScreen) {
		LoadingScreen.nextStep();
		Loader loader = new Loader();
		InputStream templReader = AppPreferences.getTemplate().createStream();
		LogisimFile file = null;
		try {
			file = loader.openLogisimFile(templReader);
		} catch (IOException ex) {
			displayException(ex);
		} catch (LoadFailedException ex) {
			displayException(ex);
		} finally {
			try { templReader.close(); } catch (IOException e) { }
		}
		if (file == null) file = createEmptyFile(loader);
		return completeProject(loader, file, isStartupScreen);
	}

	private static void displayException(Exception ex) {
		String msg = StringUtil.format(lc.get("templateOpenError"),
				ex.toString());
		String ttl = lc.get("templateOpenErrorTitle");
		DialogManager.CreateErrorDialog(ttl,msg);
	}

	private static LogisimFile createEmptyFile(Loader loader) {
		InputStream templReader = AppPreferences.getEmptyTemplate().createStream();
		LogisimFile file;
		try {
			file = loader.openLogisimFile(templReader);
		} catch (Throwable t) {
			file = LogisimFile.createNew(loader);
			file.addCircuit(new Circuit("main"));
		} finally {
			try { templReader.close(); } catch (IOException e) { }
		}
		return file;
	}

	private static Project completeProject(Loader loader, LogisimFile file, boolean isStartup) {
		LoadingScreen.nextStep();

		Project ret = new Project(file);

		LoadingScreen.nextStep();
		SwingUtilities.invokeLater(new CreateFrame(loader, ret, isStartup));
		return ret;
	}

	public static LogisimFile createNewFile(Project baseProject) {
		Loader loader = new Loader();
		InputStream templReader = AppPreferences.getTemplate().createStream();
		LogisimFile file;
		try {
			file = loader.openLogisimFile(templReader);
		} catch (IOException ex) {
			displayException(ex);
			file = createEmptyFile(loader);
		} catch (LoadFailedException ex) {
			if (!ex.isShown()) {
				displayException(ex);
			}
			file = createEmptyFile(loader);
		} finally {
			try { templReader.close(); } catch (IOException e) { }
		}
		return file;
	}

	private static Frame createFrame(Project sourceProject, Project newProject) {
		if (sourceProject != null) {
			Frame frame = sourceProject.getFrame();
			if (frame != null) {
				frame.savePreferences();
			}
		}
		Frame newFrame = new Frame(newProject);
		newProject.setFrame(newFrame);
		return newFrame;
	}

	public static Project doNew(Project baseProject) {

		LogisimFile file = createNewFile(baseProject);
		Project newProj = new Project(file);
		Frame frame = createFrame(baseProject, newProj);
		frame.setVisible(true);
		frame.getCanvas().requestFocus();
		return newProj;
	}

	public static Project doOpen(File source, Map<File,File> substitutions) throws LoadFailedException {

		LoadingScreen.nextStep();
		Loader loader = new Loader();
		LogisimFile file = loader.openLogisimFile(source, substitutions);
		AppPreferences.updateRecentFile(source);

		return completeProject(loader, file, false);
	}

	public static void doOpen(Component parent, Project baseProject) {
		JFileChooser chooser;
		if (baseProject != null) {
			Loader oldLoader = baseProject.getLogisimFile().getLoader();
			chooser = oldLoader.createChooser();
			if (oldLoader.getMainFile() != null) {
				chooser.setSelectedFile(oldLoader.getMainFile());
			}
		} else {
			chooser = JFileChoosers.create();
		}
		chooser.setFileFilter(Loader.LOGISIM_FILTER);

		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		File selected = chooser.getSelectedFile();
		if (selected != null) {
			doOpen(parent, baseProject, selected);
		}
	}

	public static Project doOpen(Component parent,
                                 Project baseProject, File f) {
		Project proj = Projects.findProjectFor(f);
		Loader loader = null;
		if (proj != null) {
			proj.getFrame().toFront();
			loader = proj.getLogisimFile().getLoader();
			if (proj.isFileDirty()) {
				String message = StringUtil.format(lc.get("openAlreadyMessage"),
						proj.getLogisimFile().getName());
				String[] options = {
						lc.get("openAlreadyLoseChangesOption"),
						lc.get("openAlreadyNewWindowOption"),
						lc.get("openAlreadyCancelOption"),
					};
				int result = JOptionPane.showOptionDialog(proj.getFrame(),
						message, lc.get("openAlreadyTitle"), 0,
						JOptionPane.QUESTION_MESSAGE, null,
						options, options[2]);
				if (result == 0) {
					; // keep proj as is, so that load happens into the window
				} else if (result == 1) {
					proj = null; // we'll create a new project
				} else {
					return proj;
				}
			}
		}

		if (proj == null && baseProject != null && baseProject.isStartupScreen()) {
			proj = baseProject;
			proj.setStartupScreen(false);
			loader = baseProject.getLogisimFile().getLoader();
		} else {
			loader = new Loader();
		}

		try {
			LogisimFile lib = loader.openLogisimFile(f);
			AppPreferences.updateRecentFile(f);
			if (lib == null) return null;
			if (proj == null) {
				proj = new Project(lib);
			} else {
				proj.setLogisimFile(lib);
			}
		} catch (LoadFailedException ex) {
			if (!ex.isShown()) {
				DialogManager.CreateStackTraceDialog(lc.get("fileOpenErrorTitle"),StringUtil.format(lc.get("fileOpenError"),
						ex.toString()),ex);
			}
			return null;
		}

		Frame frame = proj.getFrame();
		if (frame == null) {
			frame = createFrame(baseProject, proj);
		}
		frame.setVisible(true);
		frame.toFront();
		frame.getCanvas().requestFocus();
		return proj;
	}


	// returns true if save is completed
	public static boolean doSaveAs(Project proj) {
		Loader loader = proj.getLogisimFile().getLoader();
		JFileChooser chooser = loader.createChooser();
		chooser.setFileFilter(Loader.LOGISIM_FILTER);
		if (loader.getMainFile() != null) {
			chooser.setSelectedFile(loader.getMainFile());
		}
		int returnVal = chooser.showSaveDialog(proj.getFrame());
		if (returnVal != JFileChooser.APPROVE_OPTION) return false;

		File f = chooser.getSelectedFile();
		String circExt = Loader.LOGISIM_EXTENSION;
		if (!f.getName().endsWith(circExt)) {
			String old = f.getName();
			int ext0 = old.lastIndexOf('.');
			if (ext0 < 0 || !Pattern.matches("\\.\\p{L}{2,}[0-9]?", old.substring(ext0))) {
				f = new File(f.getParentFile(), old + circExt);
			} else {
				String ext = old.substring(ext0);
				String ttl = lc.get("replaceExtensionTitle");
				String msg = lc.get("replaceExtensionMessage", ext);
				Object[] options = {
						lc.get("replaceExtensionReplaceOpt", ext),
						lc.get("replaceExtensionAddOpt", circExt),
						lc.get("replaceExtensionKeepOpt")
					};
				JOptionPane dlog = new JOptionPane(msg);
				dlog.setMessageType(JOptionPane.QUESTION_MESSAGE);
				dlog.setOptions(options);
				dlog.createDialog(proj.getFrame(), ttl).setVisible(true);

				Object result = dlog.getValue();
				if (result == options[0]) {
					String name = old.substring(0, ext0) + circExt;
					f = new File(f.getParentFile(), name);
				} else if (result == options[1]) {
					f = new File(f.getParentFile(), old + circExt);
				}
			}
		}

		if (f.exists()) {
			int confirm = JOptionPane.showConfirmDialog(proj.getFrame(),
					lc.get("confirmOverwriteMessage"),
					lc.get("confirmOverwriteTitle"),
				JOptionPane.YES_NO_OPTION);
			if (confirm != JOptionPane.YES_OPTION) return false;
		}
		return doSave(proj, f);
	}

	public static boolean doSave(Project proj) {
		Loader loader = proj.getLogisimFile().getLoader();
		File f = loader.getMainFile();
		if (f == null) return doSaveAs(proj);
		else return doSave(proj, f);
	}

	private static boolean doSave(Project proj, File f) {
		Loader loader = proj.getLogisimFile().getLoader();
		Tool oldTool = proj.getTool();
		proj.setTool(null);
		boolean ret = loader.save(proj.getLogisimFile(), f);
		if (ret) {
			AppPreferences.updateRecentFile(f);
			proj.setFileAsClean();
		}
		proj.setTool(oldTool);
		return ret;
	}

	public static void doQuit() {
		Frame top = Projects.getTopFrame();
		top.savePreferences();

		for (Project proj : new ArrayList<Project>(Projects.getOpenProjects())) {
			if (!proj.confirmClose(lc.get("confirmQuitTitle"))) return;
		}
		System.exit(0);
	}
}

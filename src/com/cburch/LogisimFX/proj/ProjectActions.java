/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.proj;

import com.cburch.LogisimFX.FileSelector;
import com.cburch.LogisimFX.newgui.FrameManager;
import com.cburch.LogisimFX.file.LoadFailedException;
import com.cburch.LogisimFX.file.Loader;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.newgui.LoadingFrame.LoadingScreen;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.tools.Tool;
import com.cburch.LogisimFX.util.StringUtil;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ProjectActions {

	private ProjectActions() { }
	
	private static class CreateFrame implements Runnable {

		private Loader loader;
		private Project proj;

		public CreateFrame(Loader loader, Project proj) {
			this.loader = loader;
			this.proj = proj;
			run();
		}

		public void run() {

			LoadingScreen.Close();
			FrameManager.CreateMainFrame(proj);

		}

	}

	public static Project doNew() {

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

		return completeProject(loader, file);

	}



	private static void displayException(Exception ex) {
		String msg = StringUtil.format(LC.get("templateOpenError"),
				ex.toString());
		String ttl = LC.get("templateOpenErrorTitle");
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

	private static Project completeProject(Loader loader, LogisimFile file) {

		LoadingScreen.nextStep();

		Project ret = new Project(file);

		LoadingScreen.nextStep();

		Platform.runLater(()->new CreateFrame(loader, ret));
		//SwingUtilities.invokeLater(new CreateFrame(loader, ret, isStartup));
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


	public static Project doNew(Project baseProject) {

		LogisimFile file = createNewFile(baseProject);
		Project newProj = new Project(file);

		FrameManager.CreateMainFrame(newProj);

		return newProj;

	}

	public static Project spamNew(Project baseProject) {

		LogisimFile file = createNewFile(baseProject);
		Project newProj = new Project(file);

		FrameManager.SpamNew(newProj);

		return newProj;

	}

	public static Project doOpen(File source, Map<File,File> substitutions) throws LoadFailedException {

		LoadingScreen.nextStep();

		Loader loader = new Loader();
		LogisimFile file = loader.openLogisimFile(source, substitutions);
		AppPreferences.updateRecentFile(source);

		return completeProject(loader, file);

	}

	public static void doOpen(Project baseProject) {

		FileSelector fs = new FileSelector(baseProject.getFrameController().getStage());

		File selected = fs.OpenCircFile();

		if(selected != null){
			doOpen(baseProject, selected);
		}else{ return;}

		/*
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
			doOpen(baseProject, selected);
		}

		 */

	}

	public static Project doOpen(Project baseProject, File f) {

		//Project proj = Projects.findProjectFor(f);

		Project proj = FrameManager.FindProjectForFile(f);
		System.out.println("proj "+proj);

		Loader loader = null;

		if (proj != null) {

			if (proj.isFileDirty()) {

				System.out.println("tersr");

				int type = DialogManager.CreateFileReloadDialog(proj);

				if(type == 2){

				}else if(type == 1){
					proj = null;
				}else if(type == 0){
					return proj;
				}

				/*
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
				*/

			}else {
				System.out.println("test2");
				FrameManager.FocusOnFrame(proj);
				return proj;
			}

		}

		if (proj == null && baseProject != null) {
			proj = baseProject;
			//proj.setStartupScreen(false);
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
				FrameManager.CreateMainFrame(proj);
			} else {
				proj.setLogisimFile(lib);
				FrameManager.CreateMainFrame(proj);
			}

		} catch (LoadFailedException ex) {
			if (!ex.isShown()) {
				DialogManager.CreateStackTraceDialog(LC.get("fileOpenErrorTitle"),StringUtil.format(LC.get("fileOpenError"),
						ex.toString()),ex);
			}
			return null;
		}

		/*
		Frame frame = proj.getFrame();
		if (frame == null) {
			frame = createFrame(baseProject, proj);
		}

		frame.setVisible(true);
		frame.toFront();
		frame.getCanvas().requestFocus();
		 */

		return proj;

	}



	// returns true if save is completed
	public static boolean doSaveAs(Project proj) {

		Loader loader = proj.getLogisimFile().getLoader();

		FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());

		if (loader.getMainFile() != null) {
			//fileSelector.setInitialDirectory(loader.getMainFile());
		}

		File f = fileSelector.SaveCircFile();

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

}

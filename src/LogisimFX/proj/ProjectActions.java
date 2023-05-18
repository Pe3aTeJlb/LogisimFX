/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.proj;

import LogisimFX.FileSelector;
import LogisimFX.newgui.FrameManager;
import LogisimFX.file.LoadFailedException;
import LogisimFX.file.Loader;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.LoadingFrame.LoadingScreen;
import LogisimFX.circuit.Circuit;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.tools.Tool;
import LogisimFX.util.StringUtil;
import LogisimFX.util.ZipUtils;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
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
		LogisimFile file = null;

		try {
			Object f = AppPreferences.getTemplate();
			if (f instanceof InputStream){
				file = loader.openLogisimFile((InputStream) f);
			} else if (f instanceof File) {
				file = loader.openLogisimFile((File) f, true);
			}
		} catch (LoadFailedException | IOException ex) {
			displayException(ex);
		}

		if (file == null) file = createEmptyFile(loader);

		return completeProject(loader, file);

	}



	private static void displayException(Exception ex) {
		String msg = StringUtil.format(LC.get("templateOpenError"),
				ex.toString());
		String ttl = LC.get("templateOpenErrorTitle");
		DialogManager.createErrorDialog(ttl,msg);
	}

	private static LogisimFile createEmptyFile(Loader loader) {
		LogisimFile file;
		try {
			file = loader.openLogisimFile(AppPreferences.getEmptyTemplate());
		} catch (Throwable t) {
			file = LogisimFile.createNew(loader);
			file.addCircuit(new Circuit("main"));
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

		LogisimFile file = null;
		try {
			Object f = AppPreferences.getTemplate();
			if (f instanceof InputStream){
				file = loader.openLogisimFile((InputStream) f);
			} else if (f instanceof File) {
				file = loader.openLogisimFile((File) f, true);
			}
		} catch (LoadFailedException ex) {
			if (!ex.isShown()) {
				displayException(ex);
			}
			file = createEmptyFile(loader);
		} catch (IOException e) {
			e.printStackTrace();
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

		File selected = fs.openCircFile();

		if(selected != null){
			doOpen(null, selected);
		} else {
			return;
		}

	}

	public static Project doOpen(Project baseProject, File file) {

		Project proj = FrameManager.FindProjectForFile(file);

		Loader loader = null;

		if (proj != null) {

			if (proj.isFileDirty()) {

				int type = DialogManager.createFileReloadDialog(proj);

				if(type == 2){
					FrameManager.ReloadFrame(proj);
				}else if(type == 1){
					proj = null;
				}else if(type == 0){
					return proj;
				}

			}else {
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

			LogisimFile lib = loader.openLogisimFile(file);
			AppPreferences.updateRecentFile(file);

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
				DialogManager.createStackTraceDialog(LC.get("fileOpenErrorTitle"),StringUtil.format(LC.get("fileOpenError"),
						ex.toString()),ex);
			}
			return null;
		}

		return proj;

	}



	// returns true if save is completed
	public static boolean doSaveAs(Project proj) {

		Loader loader = proj.getLogisimFile().getLoader();

		FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());

		File f = fileSelector.saveCircFile();

		if (f == null) return false;

		return doSave(proj, f);

	}

	public static boolean doSave(Project proj) {

		Loader loader = proj.getLogisimFile().getLoader();

		File f = loader.getMainFile();

		if (f == null) {
			return doSaveAs(proj);
		} else {
			return doSave(proj, f);
		}

	}

	private static boolean doSave(Project proj, File f) {

		Loader loader = proj.getLogisimFile().getLoader();

		Tool oldTool = proj.getTool();
		proj.setTool(null);

		boolean ret = loader.save(
				proj,
				proj.getLogisimFile(),
				Paths.get(proj.getLogisimFile().getProjectDir() + File.separator + f.getName().split("\\.")[0]+".proj").toFile()
		);

		if (ret) {

			AppPreferences.updateRecentFile(f);
			proj.setFileAsClean();

			try {
				ZipUtils.zipFolder(proj.getLogisimFile().getProjectDir(), f.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		proj.setTool(oldTool);

		return ret;

	}

}

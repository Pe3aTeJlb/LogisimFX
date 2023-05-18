/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.file;

import LogisimFX.FileSelector;
import LogisimFX.localization.LC_file;
import LogisimFX.localization.Localizer;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.FrameManager;
import LogisimFX.proj.Project;
import LogisimFX.std.Builtin;
import LogisimFX.tools.Library;
import LogisimFX.util.StringUtil;
import LogisimFX.util.ZipClassLoader;
import LogisimFX.util.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Loader implements LibraryLoader {

	private static Localizer lc = LC_file.getInstance();

	public static final String LOGISIM_EXTENSION = ".circ";
	public static final String LOGISIM_PROJ_DESC = ".proj";

	// fixed
	private Builtin builtin = new Builtin();

	// to be cleared with each new file
	private File mainFile = null;
	private Stack<File> filesOpening = new Stack<File>();
	private Map<File, File> substitutions = new HashMap<File, File>();

	public Loader() {
		clear();
	}

	public Builtin getBuiltin() {
		return builtin;
	}

	private File getSubstitution(File source) {

		File ret = substitutions.get(source);
		return ret == null ? source : ret;

	}

	//
	// file chooser related methods
	//
	public File getMainFile() {
		return mainFile;
	}

	// used here and in LibraryManager only
	File getCurrentDirectory() {
		File ref;
		if (!filesOpening.empty()) {
			ref = filesOpening.peek();
		} else {
			ref = mainFile;
		}
		return ref == null ? null : ref.getParentFile();
	}

	private void setMainFile(File value) {
		mainFile = value;
	}

	//
	// more substantive methods accessed from outside this package
	//
	public void clear() {
		filesOpening.clear();
		mainFile = null;
	}

	public LogisimFile openLogisimFile(File file, Map<File, File> substitutions)
			throws LoadFailedException {
		this.substitutions = substitutions;
		try {
			return openLogisimFile(file);
		} finally {
			this.substitutions = Collections.emptyMap();
		}
	}

	public LogisimFile openLogisimFile(InputStream reader) throws IOException {
		LogisimFile ret;
		try {
			ret = LogisimFile.load(reader, this, false, null);
		} catch (LoaderException e) {
			return null;
		}
		showMessages(ret);
		return ret;
	}

	public LogisimFile openLogisimFile(File file) throws LoadFailedException {
		return openLogisimFile(file, false);
	}

	public LogisimFile openLogisimFile(File file, boolean isTemplate) throws LoadFailedException {
		try {
			LogisimFile ret = loadLogisimFile(file, false, null);
			if (ret != null && !isTemplate) setMainFile(file);
			showMessages(ret);
			return ret;
		} catch (LoaderException e) {
			throw new LoadFailedException(e.getMessage(), e.isShown());
		}
	}

	public Library loadLogisimLibrary(LogisimFile baseLogisimFile, File file) {
		File actual = getSubstitution(file);
		LoadedLibrary ret = LibraryManager.instance.loadLogisimLibrary(baseLogisimFile, this, actual);
		if (ret != null) {
			LogisimFile retBase = (LogisimFile) ret.getBase();
			showMessages(retBase);
		}
		return ret;
	}

	public Library loadJarLibrary(File file, String className) {
		File actual = getSubstitution(file);
		return LibraryManager.instance.loadJarLibrary(this, actual, className);
	}

	public void reload(LoadedLibrary lib) {
		LibraryManager.instance.reload(this, lib);
	}

	public boolean save(Project proj, LogisimFile file, File dest) {
		Library reference = LibraryManager.instance.findReference(file, dest);
		if (reference != null) {
			DialogManager.createErrorDialog(
					StringUtil.format(lc.get("fileCircularError"), reference.getDisplayName().getValue()),
					lc.get("fileSaveErrorTitle"));
			return false;
		}

		proj.getFrameController().doSaveCodeEditors();

		File backup = null;
		if (dest != null) backup = determineBackupName(dest);
		boolean backupCreated = backup != null && dest.renameTo(backup);

		FileOutputStream fwrite = null;
		try {
			fwrite = new FileOutputStream(dest);
			file.write(fwrite, this);
			file.setName(toProjectName(dest));

			File oldFile = getMainFile();
			setMainFile(dest);
			LibraryManager.instance.fileSaved(this, dest, oldFile, file);
		} catch (IOException e) {
			if (backupCreated) recoverBackup(backup, dest);
			if (dest.exists() && dest.length() == 0) dest.delete();
			DialogManager.createStackTraceDialog(lc.get("fileSaveErrorTitle"), StringUtil.format(lc.get("fileSaveError"),
					e.toString()), e);
			return false;
		} finally {
			if (fwrite != null) {
				try {
					fwrite.close();
				} catch (IOException e) {
					if (backupCreated) recoverBackup(backup, dest);
					if (dest.exists() && dest.length() == 0) dest.delete();
					DialogManager.createStackTraceDialog(lc.get("fileSaveErrorTitle"), LC.getFormatted("fileSaveCloseError",
							e.toString()), e);
					return false;
				}
			}
		}

		if (!dest.exists() || dest.length() == 0) {
			if (backupCreated && backup != null && backup.exists()) {
				recoverBackup(backup, dest);
			} else {
				dest.delete();
			}
			DialogManager.createErrorDialog(lc.get("fileSaveErrorTitle"), lc.get("fileSaveZeroError"));
			return false;
		}

		if (backupCreated && backup.exists()) {
			backup.delete();
		}
		return true;
	}

	private static File determineBackupName(File base) {
		File dir = base.getParentFile();
		String name = base.getName();
		if (name.endsWith(LOGISIM_EXTENSION)) {
			name = name.substring(0, name.length() - LOGISIM_EXTENSION.length());
		}
		for (int i = 1; i <= 20; i++) {
			String ext = i == 1 ? ".bak" : (".bak" + i);
			File candidate = new File(dir, name + ext);
			if (!candidate.exists()) return candidate;
		}
		return null;
	}

	private static void recoverBackup(File backup, File dest) {
		if (backup != null && backup.exists()) {
			if (dest.exists()) dest.delete();
			backup.renameTo(dest);
		}
	}

	//
	// methods for LibraryManager
	//
	LogisimFile loadLogisimFile(File request, boolean isLib, LogisimFile baseLogisimFile) throws LoadFailedException {
		File actual = getSubstitution(request);
		for (File fileOpening : filesOpening) {
			if (fileOpening.equals(actual)) {
				throw new LoadFailedException(LC.getFormatted("logisimCircularError",
						toProjectName(actual)));
			}
		}

		LogisimFile ret = null;
		filesOpening.push(actual);
		boolean isZip = ZipUtils.isZip(actual);

		try {
			if (!isZip) {
				ret = LogisimFile.load(actual, this, isLib, baseLogisimFile);
			} else {
				ret = LogisimFile.loadZip(actual, this, isLib, baseLogisimFile);
			}
		} catch (IOException e) {
			throw new LoadFailedException(LC.getFormatted("logisimLoadError",
					toProjectName(actual), e.toString()));
		} finally {
			filesOpening.pop();
		}
		ret.setName(toProjectName(actual));
		return ret;
	}

	Library loadJarFile(File request, String className) throws LoadFailedException {
		File actual = getSubstitution(request);
		// Up until 2.1.8, this was written to use a URLClassLoader, which
		// worked pretty well, except that the class never releases its file
		// handles. For this reason, with 2.2.0, it's been switched to use
		// a custom-written class ZipClassLoader instead. The ZipClassLoader
		// is based on something downloaded off a forum, and I'm not as sure
		// that it works as well. It certainly does more file accesses.

		// Anyway, here's the line for this new version:
		ZipClassLoader loader = new ZipClassLoader(actual);

		// And here's the code that was present up until 2.1.8, and which I
		// know to work well except for the closing-files bit. If necessary, we
		// can revert by deleting the above declaration and reinstating the below.
		/*
		URL url;
		try {
			url = new URL("file", "localhost", file.getCanonicalPath());
		} catch (MalformedURLException e1) {
			throw new LoadFailedException("Internal error: Malformed URL");
		} catch (IOException e1) {
			throw new LoadFailedException(Strings.get("jarNotOpenedError"));
		}
		URLClassLoader loader = new URLClassLoader(new URL[] { url });
		*/

		// load library class from loader
		Class<?> retClass;
		try {
			retClass = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new LoadFailedException(LC.getFormatted("jarClassNotFoundError", className));
		}
		if (!(Library.class.isAssignableFrom(retClass))) {
			throw new LoadFailedException(LC.getFormatted("jarClassNotLibraryError", className));
		}

		// instantiate library
		Library ret;
		try {
			ret = (Library) retClass.newInstance();
		} catch (Exception e) {
			throw new LoadFailedException(LC.getFormatted("jarLibraryNotCreatedError", className));
		}
		return ret;
	}

	//
	// Library methods
	//
	public Library loadLibrary(LogisimFile file, String desc) {
		return LibraryManager.instance.loadLibrary(this, file, desc);
	}

	public String getDescriptor(Library lib) {
		return LibraryManager.instance.getDescriptor(this, lib);
	}

	public void showError(String description) {
		if (!filesOpening.empty()) {
			File top = filesOpening.peek();
			String init = toProjectName(top) + ":";
			if (description.contains("\n")) {
				description = init + "\n" + description;
			} else {
				description = init + " " + description;
			}
		}

		if (description.contains("\n") || description.length() > 60) {

			DialogManager.createScrollError(lc.get("fileErrorTitle"), description);

		} else {
			DialogManager.createScrollError(lc.get("fileErrorTitle"), description);
		}
	}

	private void showMessages(LogisimFile source) {
		if (source == null) return;
		String message = source.getMessage();
		while (message != null) {
			DialogManager.createInfoDialog(lc.get("fileMessageTitle"), message);
			message = source.getMessage();
		}
	}

	//
	// helper methods
	//
	File getFileFor(String name, String filter) {

		// Determine the actual file name.
		File file = new File(name);

		if (!file.isAbsolute()) {
			File currentDirectory = getCurrentDirectory();
			if (currentDirectory != null) file = new File(currentDirectory, name);
		}

		while (!file.canRead()) {

			// It doesn't exist. Figure it out from the user.
			DialogManager.createInfoDialog("File missing", StringUtil.format(lc.get("fileLibraryMissingError"),
					file.getName()));

			FileSelector fileSelector = new FileSelector(null);
			if (filter.equals("circ")) fileSelector.setCircFilter();
			if (filter.equals("jar")) fileSelector.setJarFilter();
			file = fileSelector.showOpenDialog(LC.getFormatted("fileLibraryMissingTitle", file.getName()));

		}
		return file;

	}

	private String toProjectName(File file) {

		String ret = file.getName();
		if (ret.endsWith(LOGISIM_EXTENSION) || ret.endsWith(LOGISIM_PROJ_DESC)) {
			return ret.substring(0, ret.length() - LOGISIM_EXTENSION.length());
		} else {
			String name = LC.get("defaultProjectName");
			if (FrameManager.windowNamed(name)) {
				for (int i = 2; true; i++) {
					if (!FrameManager.windowNamed(name + " " + i)) {
						name = name + " " + i;
						break;
					}
				}
			}
			return name;
		}

	}

}
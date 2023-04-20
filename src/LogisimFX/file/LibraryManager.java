/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.newgui.DialogManager;
import LogisimFX.tools.Library;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

class LibraryManager {
	public static final LibraryManager instance = new LibraryManager();

	private static char desc_sep = '#';
	
	private static abstract class LibraryDescriptor {
		abstract boolean concernsFile(File query);
		abstract String toDescriptor(Loader loader);
		abstract void setBase(Loader loader, LoadedLibrary lib)
			throws LoadFailedException;
	}

	private static class LogisimProjectDescriptor extends LibraryDescriptor {
		private File file;

		LogisimProjectDescriptor(File file) {
			this.file = file;
		}

		@Override
		boolean concernsFile(File query) {
			return file.equals(query);
		}

		@Override
		String toDescriptor(Loader loader) {
			return "file#" + toRelative(loader, file);
		}

		@Override
		void setBase(Loader loader, LoadedLibrary lib) throws LoadFailedException {
			lib.setBase(loader.loadLogisimFile(file, true));
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof LogisimProjectDescriptor)) return false;
			LogisimProjectDescriptor o = (LogisimProjectDescriptor) other;
			return this.file.equals(o.file);
		}

		@Override
		public int hashCode() {
			return file.hashCode();
		}
	}

	private static class JarDescriptor extends LibraryDescriptor {
		private File file;
		private String className;

		JarDescriptor(File file, String className) {
			this.file = file;
			this.className = className;
		}

		@Override
		boolean concernsFile(File query) {
			return file.equals(query);
		}

		@Override
		String toDescriptor(Loader loader) {
			return "jar#" + toRelative(loader, file) + desc_sep + className;
		}

		@Override
		void setBase(Loader loader, LoadedLibrary lib) throws LoadFailedException {
			lib.setBase(loader.loadJarFile(file, className));
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof JarDescriptor)) return false;
			JarDescriptor o = (JarDescriptor) other;
			return this.file.equals(o.file) && this.className.equals(o.className);
		}

		@Override
		public int hashCode() {
			return file.hashCode() * 31 + className.hashCode();
		}
	}

	private HashMap<LibraryDescriptor,WeakReference<LoadedLibrary>> fileMap;
	private WeakHashMap<LoadedLibrary,LibraryDescriptor> invMap;

	private LibraryManager() {
		fileMap = new HashMap<LibraryDescriptor,WeakReference<LoadedLibrary>>();
		invMap = new WeakHashMap<LoadedLibrary,LibraryDescriptor>();
	}

	void setDirty(File file, boolean dirty) {
		LoadedLibrary lib = findKnown(file);
		if (lib != null) {
			lib.setDirty(dirty);
		}
	}

	Collection<LogisimFile> getLogisimLibraries() {
		ArrayList<LogisimFile> ret = new ArrayList<LogisimFile>();
		for (LoadedLibrary lib : invMap.keySet()) {
			if (lib.getBase() instanceof LogisimFile) {
				ret.add((LogisimFile) lib.getBase());
			}
		}
		return ret;
	}

	public Library loadLibrary(Loader loader, String desc) {
		// It may already be loaded.
		// Otherwise we'll have to decode it.
		int sep = desc.indexOf(desc_sep);
		if (sep < 0) {
			loader.showError(LC.getFormatted("fileDescriptorError", desc));
			return null;
		}
		String type = desc.substring(0, sep);
		String name = desc.substring(sep + 1);

		if (type.equals("")) {
			Library ret = loader.getBuiltin().getLibrary(name);
			if (ret == null) {
				loader.showError(LC.getFormatted("fileBuiltinMissingError", name));
				return null;
			}
			return ret;
		} else if (type.equals("file")) {
			File toRead = loader.getFileFor(name, "circ");
			return loadLogisimLibrary(loader, toRead);
		} else if (type.equals("jar")) {
			int sepLoc = name.lastIndexOf(desc_sep);
			String fileName = name.substring(0, sepLoc);
			String className = name.substring(sepLoc + 1);
			File toRead = loader.getFileFor(fileName, "jar");
			return loadJarLibrary(loader, toRead, className);
		} else {
			loader.showError(LC.getFormatted("fileTypeError",
				type, desc));
			return null;
		}
	}

	public LoadedLibrary loadLogisimLibrary(Loader loader, File toRead) {
		LoadedLibrary ret = findKnown(toRead);
		if (ret != null) return ret;

		try {
			ret = new LoadedLibrary(loader.loadLogisimFile(toRead, true));
		} catch (LoadFailedException e) {
			loader.showError(e.getMessage());
			return null;
		}

		LogisimProjectDescriptor desc = new LogisimProjectDescriptor(toRead);
		fileMap.put(desc, new WeakReference<LoadedLibrary>(ret));
		invMap.put(ret, desc);
		return ret;
	}

	public LoadedLibrary loadJarLibrary(Loader loader, File toRead, String className) {
		JarDescriptor jarDescriptor = new JarDescriptor(toRead, className);
		LoadedLibrary ret = findKnown(jarDescriptor);
		if (ret != null) return ret;

		try {
			ret = new LoadedLibrary(loader.loadJarFile(toRead, className));
		} catch (LoadFailedException e) {
			loader.showError(e.getMessage());
			return null;
		}

		fileMap.put(jarDescriptor, new WeakReference<LoadedLibrary>(ret));
		invMap.put(ret, jarDescriptor);
		return ret;
	}

	public void reload(Loader loader, LoadedLibrary lib) {
		LibraryDescriptor descriptor = invMap.get(lib);
		if (descriptor == null) {
			DialogManager.createErrorDialog("Error",
					LC.getFormatted("unknownLibraryFileError",lib.getDisplayName().getValue()));
		} else {
			try {
				descriptor.setBase(loader, lib);
			} catch (LoadFailedException e) {
				loader.showError(e.getMessage());
			}
		}
	}

	public Library findReference(LogisimFile file, File query) {
		for (Library lib : file.getLibraries()) {
			LibraryDescriptor desc = invMap.get(lib);
			if (desc != null && desc.concernsFile(query)) {
				return lib;
			}
			if (lib instanceof LoadedLibrary) {
				LoadedLibrary loadedLib = (LoadedLibrary) lib;
				if (loadedLib.getBase() instanceof LogisimFile) {
					LogisimFile loadedProj = (LogisimFile) loadedLib.getBase();
					Library ret = findReference(loadedProj, query);
					if (ret != null) return lib;
				}
			}
		}
		return null;
	}

	public static Set<String> getBuildinNames(Loader loader) {
		final var buildinNames = new HashSet<String>();
		for (final var lib : loader.getBuiltin().getLibraries()) {
			buildinNames.add(lib.getName());
		}
		return buildinNames;
	}

	public void fileSaved(Loader loader, File dest, File oldFile, LogisimFile file) {
		LoadedLibrary old = findKnown(oldFile);
		if (old != null) {
			old.setDirty(false);
		}

		LoadedLibrary lib = findKnown(dest);
		if (lib != null) {
			LogisimFile clone = file.cloneLogisimFile(loader);
			clone.setName(file.getName());
			clone.setDirty(false);
			lib.setBase(clone);
		}
	}

	public String getDescriptor(Loader loader, Library lib) {
		if (loader.getBuiltin().getLibraries().contains(lib)) {
			return desc_sep + lib.getName();
		} else {
			LibraryDescriptor desc = invMap.get(lib);
			if (desc != null) {
				return desc.toDescriptor(loader);
			} else {
				throw new LoaderException(LC.getFormatted("fileDescriptorUnknownError",
						lib.getDisplayName().getValue()));
			}
		}
	}

	private LoadedLibrary findKnown(Object key) {
		WeakReference<LoadedLibrary> retLibRef;
		retLibRef = fileMap.get(key);
		if (retLibRef == null) {
			return null;
		} else {
			LoadedLibrary retLib = retLibRef.get();
			if (retLib == null) {
				fileMap.remove(key);
				return null;
			} else {
				return retLib;
			}
		}
	}
	
	private static String toRelative(Loader loader, File file) {
		File currentDirectory = loader.getCurrentDirectory();
		if (currentDirectory == null) {
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				return file.toString();
			}
		}

		File fileDir = file.getParentFile();
		if (fileDir != null) {
			if (currentDirectory.equals(fileDir)) {
				return file.getName();
			} else if (currentDirectory.equals(fileDir.getParentFile())) {
				return fileDir.getName() + "/" + file.getName();
			} else if (fileDir.equals(currentDirectory.getParentFile())) {
				return "../" + file.getName();
			}
		}
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.toString();
		}
	}

	public static void removeUnusedLibraries(Library lib) {
		LogisimFile logiLib = null;
		if (lib instanceof LoadedLibrary) {
			if (((LoadedLibrary)lib).getBase() instanceof LogisimFile) {
				logiLib = (LogisimFile) ((LoadedLibrary)lib).getBase();
			}
		} else if (lib instanceof LogisimFile) {
			logiLib = (LogisimFile) lib;
		}
		if (logiLib == null) return;
		final var toBeRemoved = new HashSet<String>();
		for (final var library : logiLib.getLibraries()) {
			var isUsed = false;
			for (final var circ : logiLib.getCircuits()) {
				for (final var tool : circ.getNonWires()) {
					isUsed |= library.contains(tool.getFactory());
				}
			}
			if (!isUsed) {
				toBeRemoved.add(library.getName());
			} else {
				removeUnusedLibraries(library);
			}
		}
		for (final var remove : toBeRemoved) {
			lib.removeLibrary(remove);
		}
	}

	public static Set<String> getUsedBaseLibraries(Library library) {
		final var result = new HashSet<String>();
		for (final var lib : library.getLibraries()) {
			result.addAll(getUsedBaseLibraries(lib));
			if (!(lib instanceof LoadedLibrary) && !(lib instanceof LogisimFile)) {
				result.add(lib.getName());
			}
		}
		return result;
	}

	public static void removeBaseLibraries(Library library, Set<String> baseLibs) {
		final var libIterator = library.getLibraries().iterator();
		while (libIterator.hasNext()) {
			final var lib = libIterator.next();
			if (baseLibs.contains(lib.getName())) {
				libIterator.remove();
			} else {
				removeBaseLibraries(lib, baseLibs);
			}
		}
	}

}
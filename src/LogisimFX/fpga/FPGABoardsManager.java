package LogisimFX.fpga;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class FPGABoardsManager {

	public final String BOARDS_PATH = "LogisimFX/resources/boards";
	private ArrayList<String> boardsFiles = new ArrayList<>();

	public FPGABoardsManager(){
		boardsFiles.addAll(Arrays.asList(getResourceFolderFiles(BOARDS_PATH)));
	}

	private String[] getResourceFolderFiles(String path) {

		URL dirURL = ProjectConstrainsManager.class.getClassLoader().getResource(path);

		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			try {
				return new File(dirURL.toURI()).list();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			String me = ProjectConstrainsManager.class.getName().replace(".", "/")+".class";
			dirURL = ProjectConstrainsManager.class.getClassLoader().getResource(me);
		}

		if (dirURL != null && dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
			JarFile jar = null;
			try {
				jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) { //filter according to the path
					String entry = name.substring(path.length()).replace("/", "");
					if (entry.isEmpty()) {
						// if it is a subdirectory, we just return the directory name
						continue;
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[0]);
		}

		throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);

	}



	@SuppressWarnings("serial")
	private static class SortedArrayList<T> extends ArrayList<T> {

		@SuppressWarnings("unchecked")
		public void insertSorted(T value) {
			add(value);
			Comparable<T> cmp = (Comparable<T>) value;
			for (int i = size() - 1; i > 0 && cmp.compareTo(get(i - 1)) < 0; i--) {
				Collections.swap(this, i, i - 1);
			}
		}
	}

	public List<String> getBoardNames() {
		final var ret = new SortedArrayList<String>();
		for (final var board : boardsFiles) {
			ret.insertSorted(getBoardName(board));
		}
		return ret;
	}

	public String getBoardName(String boardIdentifier) {
		final var parts =
				boardIdentifier.contains("url:")
						? boardIdentifier.split("/")
						: boardIdentifier.split(Pattern.quote(File.separator));
		return parts[parts.length - 1].replace(".xml", "");
	}

	public String getBoardFilePath(String boardName) {
		if (boardName == null) return null;
		for (final var board : boardsFiles) {
			if (getBoardName(board).equals(boardName)) {
				return board;
			}
		}
		return null;
	}

}

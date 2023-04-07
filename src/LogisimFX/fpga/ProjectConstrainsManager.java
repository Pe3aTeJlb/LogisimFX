package LogisimFX.fpga;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProjectConstrainsManager {

	public final String CONSTRAINS_PATH = "LogisimFX/resources/constrains";
	private ArrayList<String> constrainFiles;

	public ProjectConstrainsManager() {

		String[] files = getResourceFolderFiles(CONSTRAINS_PATH);
		Arrays.sort(files);
		constrainFiles = new ArrayList<>(Arrays.asList(files));

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

	public ArrayList<String> getConstrainFiles() {
		return constrainFiles;
	}
}

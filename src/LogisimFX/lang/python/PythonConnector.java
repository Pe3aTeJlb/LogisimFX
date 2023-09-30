package LogisimFX.lang.python;

import LogisimFX.file.LogisimFile;
import LogisimFX.proj.Project;
import com.sun.jna.Platform;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PythonConnector {

	private static Path LOGISIMFX_VENV = Paths.get(LogisimFile.LOGISIMFX_RUNTIME + File.separator + "venv");

	private static Path LOGISIMFX_VENV_SCRIPTS_WIN = Paths.get(LogisimFile.LOGISIMFX_RUNTIME + File.separator + "venv" + File.separator + "Scripts");
	private static Path LOGISIMFX_VENV_LIBS_WIN = Paths.get(LogisimFile.LOGISIMFX_RUNTIME + File.separator + "venv" + File.separator + "Lib");

	private static Path LOGISIMFX_VENV_BIN_LIN = Paths.get(LogisimFile.LOGISIMFX_RUNTIME + File.separator + "venv" + File.separator + "bin");
	private static Path LOGISIMFX_VENV_LIBS_LIN = Paths.get(LogisimFile.LOGISIMFX_RUNTIME + File.separator + "venv" + File.separator + "lib");

	public static final Pattern PYTHON_VERSION = Pattern.compile("Python\\s3\\.?[\\d\\.]*");

	public static boolean isPythonPresent(Project proj) {

		String version = "";

		try {

			Process process = proj.getTerminal().executeAsNonPty("python3 --version");
			if (process != null) {
				try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					version = input.readLine();
					System.out.println(version);
				}
			}

			if (!PYTHON_VERSION.matcher(version).matches()) {
				Process process2 = proj.getTerminal().executeAsNonPty("python --version");
				if (process2 != null) {
					try (BufferedReader input = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
						version = input.readLine();
						System.out.println(version);
					}
				} else {
					return false;
				}
			}

		} catch (IOException e) {
			return false;
		}

		return PYTHON_VERSION.matcher(version).matches();

	}

	public static Thread activateVenv(Project proj){
		if (Platform.isWindows()) {
			return proj.getTerminal().execute(LOGISIMFX_VENV_SCRIPTS_WIN + File.separator + "activate.bat");
		} else {
			return proj.getTerminal().execute("source " + LOGISIMFX_VENV_BIN_LIN + File.separator + "activate");
		}
	}

	public static Thread deactivateVenv(Project proj){
		if (Platform.isWindows()) {
			return proj.getTerminal().execute(LOGISIMFX_VENV_SCRIPTS_WIN + File.separator + "deactivate.bat");
		} else {
			return proj.getTerminal().execute("deactivate");
		}
	}

	public static Thread executeFile(Project proj, File file) {
		return proj.getTerminal().execute("python \"" + file.toString() + "\"");
	}


	public static String getLibPath(String name) {
		if (Platform.isWindows()) {
			return LOGISIMFX_VENV_LIBS_WIN + File.separator + name;
		} else {
			return LOGISIMFX_VENV_LIBS_LIN + File.separator + name;
		}
	}



	public static void unpackVenv() {

		String protocol = PythonConnector.class.getResource("/venv").getProtocol();

		if (protocol.equals("jar")) {
			try {
				if (Platform.isWindows()) {
					extractDir("/venv/win", LOGISIMFX_VENV.toFile());
				} else {
					extractDir("/venv/lin", LOGISIMFX_VENV.toFile());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (protocol.equals("file")){
			try {
				if (Platform.isWindows()) {
					FileUtils.copyDirectory(
							Paths.get(PythonConnector.class.getResource("/venv/win").toString().replace(protocol, "").substring(2)).toFile(),
							LOGISIMFX_VENV.toFile()
					);
				} else {
					FileUtils.copyDirectory(
							Paths.get(PythonConnector.class.getResource("/venv/lin").toString().replace(protocol, "").substring(1)).toFile(),
							LOGISIMFX_VENV.toFile()
					);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * extract the subdirectory from a jar on the classpath to {@code writeDirectory}
	 *
	 * @param sourceDirectory directory (in a jar on the classpath) to extract
	 * @param writeDirectory  the location to extract to
	 * @throws IOException if an IO exception occurs
	 */
	private static void extractDir(String sourceDirectory, File writeDirectory) throws IOException {

		final URL dirURL = PythonConnector.class.getResource(sourceDirectory);
		final String path = sourceDirectory.substring(1);

		if ((dirURL != null) && dirURL.getProtocol().equals("jar")) {
			final JarURLConnection jarConnection = (JarURLConnection) dirURL.openConnection();

			final ZipFile jar = jarConnection.getJarFile();

			final Enumeration<? extends ZipEntry> entries = jar.entries(); // gives ALL entries in jar

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				final String name = entry.getName();
				// System.out.println( name );
				if (!name.startsWith(path)) {
					// entry in wrong subdir -- don't copy
					continue;
				}
				final String entryTail = name.substring(path.length());

				final File f = new File(writeDirectory + File.separator + entryTail);
				if (entry.isDirectory()) {
					// if its a directory, create it
					final boolean bMade = f.mkdir();
				} else {
					final InputStream is = jar.getInputStream(entry);
					final OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
					final byte buffer[] = new byte[4096];
					int readCount;
					// write contents of 'is' to 'os'
					while ((readCount = is.read(buffer)) > 0) {
						os.write(buffer, 0, readCount);
					}
					os.close();
					is.close();
				}
			}

		} else if (dirURL == null) {
			throw new IllegalStateException("can't find " + sourceDirectory + " on the classpath");
		} else {
			// not a "jar" protocol URL
			throw new IllegalStateException("don't know how to handle extracting from " + dirURL);
		}
	}

}

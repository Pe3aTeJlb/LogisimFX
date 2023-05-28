package LogisimFX.lang.python;

import LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab.Terminal;
import LogisimFX.proj.Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class PythonConnector {

	public static final Pattern PYTHON_VERSION = Pattern.compile("Python\\s3[\\d\\.]+");

	public static boolean isPythonPresent(Project proj){

		String version = "";

		try {

			Process process = proj.getTerminal().silentExecuteAsProcess("python --version");
			try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				version = input.readLine();
			}

			if (!PYTHON_VERSION.matcher(version).matches()){
				Process process2 = proj.getTerminal().silentExecuteAsProcess("python3 --version");
				try (BufferedReader input = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
					version = input.readLine();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return PYTHON_VERSION.matcher(version).matches();

	}

	public static String getLibPath(String name){
		return Terminal.getJarPath() + "locallibs" + File.separator + "python" + File.separator + name;
	}

	public static void executeFile(Project proj, File file){
		proj.getTerminal().execute("python \"" + file.toString()+"\"");
	}

}

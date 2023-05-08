package LogisimFX.lang.python;

import LogisimFX.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class PythonConnector {

	public static final Pattern PYTHON_VERSION = Pattern.compile("Python\\s3[\\d\\.]+");

	public static boolean isPythonPresent(){

		String version = "";

		try {
			Process process = Runtime.getRuntime().exec("python --version");
			try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				version = input.readLine();
			}

			if (!PYTHON_VERSION.matcher(version).matches()){
				Process process2 = Runtime.getRuntime().exec("python3 --version");
				try (BufferedReader input = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
					version = input.readLine();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return PYTHON_VERSION.matcher(version).matches();

	}

	public static String getJarPath(String name){

		try {
			String jarPath = Main.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath().substring(1).replace("/", File.separator).replace("\\", File.separator) +
					"locallibs" + File.separator + "python" + File.separator + name;
			return jarPath;
		} catch (Exception e){

		}

		return null;
	}

	public static void executeFile(File file){

		try {
			Process process = Runtime.getRuntime().exec("python \"" + file.toString()+"\"");
			try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = input.readLine()) != null) {
					//System.out.println(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

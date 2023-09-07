package LogisimFX.fpga;

import LogisimFX.proj.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class DockerConnector {

	public static final Pattern DOCKER_VERSION = Pattern.compile("Docker\\s.+");

	public static boolean isDockerPresent(Project proj) {

		String version = "";

		try {

			Process process = proj.getTerminal().executeAsNonPty("docker --version");
			if (process == null) return false;
			try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				version = input.readLine();
			}

		} catch (IOException e) {
			return false;
		}

		return DOCKER_VERSION.matcher(version).matches();

	}

	public static boolean isImagePresent(Project proj, String imageName) {

		String responseString = "";

		try {

			Process process = proj.getTerminal().executeAsNonPty("docker images -q " + imageName);
			if (process == null) return false;
			try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				responseString = input.readLine();
			}

		} catch (IOException e) {
			return false;
		}

		return responseString != null && !responseString.equals("");

	}

}

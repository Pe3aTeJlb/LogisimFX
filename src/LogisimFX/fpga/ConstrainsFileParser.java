package LogisimFX.fpga;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstrainsFileParser {

	private ArrayList<String> boardPorts;

	public ConstrainsFileParser(){
		boardPorts = new ArrayList<>();
	}

	public ArrayList<String> getBoardPorts() {
		return boardPorts;
	}

	public void parseConstrainsFile(File file){

		String extension = file.getName().split("\\.")[1];

		switch (extension){
			case "xdc": parsePorts_XDC(file); break;
		}

	}

	private void parsePorts_XDC(File file){

		ArrayList<String> ports = new ArrayList<>();

		Pattern pattern = Pattern.compile("\\{\\s?\\S+\\s?\\}");
		Matcher matcher;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

				if(!line.trim().isEmpty()){

					String ln = line.trim();
					if (ln.contains("set_property")){
						matcher = pattern.matcher(ln);
						if (matcher.find()){
							ports.add(matcher.group().replace("{", "").replace("}", "").replace(" ", ""));
						}
					}

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		boardPorts = new ArrayList<>(ports);

	}


}

/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.file;

import LogisimFX.fpga.LC;
import LogisimFX.fpga.Reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileWriter {

	public static final String ENTITY_EXTENSION = "_entity";

	public static File getFilePointer(String targetDirectory, String name) {
		final var fileName = new StringBuilder();
		try {
			final var outDir = new File(targetDirectory);
			if (!outDir.exists()) {
				if (!outDir.mkdirs()) {
					return null;
				}
			}
			fileName.append(targetDirectory);
			if (!targetDirectory.endsWith(File.separator)) fileName.append(File.separator);
			fileName.append(name);
			fileName.append(".v");
			final var outFile = new File(fileName.toString());
			Reporter.report.addInfo(LC.getFormatted("fileCreateScriptFile", fileName.toString()));
			if (outFile.exists()) {
				Reporter.report.addWarning(LC.getFormatted("fileScriptFileExists", fileName.toString()));
				return null;
			}
			return outFile;
		} catch (Exception e) {
			Reporter.report.addFatalError(LC.getFormatted("fileUnableToCreate", fileName.toString()));
			return null;
		}


	}

	public static List<String> getGenerateRemark(String compName) {
		ArrayList<String> lines = new ArrayList<>();
		final int headWidth;
		final String headOpen;
		final String headClose;

		final var headText = " LogisimFX goes FPGA automatic generated Verilog code";
		final var headUrl = " " + "https://github.com/Pe3aTeJlb/LogisimFX";
		final var headComp = " Component : " + compName;

		headWidth = 74;
		headOpen = " **";
		headClose = "**";

		lines.add("/**" + "*".repeat(headWidth) + headClose);
		lines.add(
				headOpen + headText + " ".repeat(Math.max(0, headWidth - headText.length())) + headClose);
		lines.add(
				headOpen + headUrl + " ".repeat(Math.max(0, headWidth - headUrl.length())) + headClose);
		lines.add(headOpen + " ".repeat(headWidth) + headClose);
		lines.add(
				headOpen + headComp + " ".repeat(Math.max(0, headWidth - headComp.length())) + headClose);
		lines.add(headOpen + " ".repeat(headWidth) + headClose);
		lines.add(headOpen + "*".repeat(headWidth) + "*/");
		lines.add("");

		return lines;
	}

	public static boolean writeContents(File outfile, List<String> contents) {
		try {
			final var output = new FileOutputStream(outfile);
			for (var thisLine : contents) {
				if (!thisLine.isEmpty()) {
					output.write(thisLine.getBytes());
				}
				output.write("\n".getBytes());
			}
			output.flush();
			output.close();
			return true;
		} catch (Exception e) {
			Reporter.report.addFatalError(LC.getFormatted("fileUnableToWrite", outfile.getAbsolutePath()));
			return false;
		}
	}
}

package LogisimFX.fpga;

import LogisimFX.FileSelector;
import LogisimFX.circuit.Circuit;
import LogisimFX.file.LogisimFile;
import LogisimFX.fpga.data.MappableResourcesContainer;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.TickComponentHdlGeneratorFactory;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;

public class FPGAToolchainOrchestrator {

	private Project proj;
	private MappableResourcesContainer mappableResourcesContainer;

	private ProjectConstrainsManager constrainsManager;
	private ConstrainsFileParser parser;

	private File boardConstrainsFile;

	public FPGAToolchainOrchestrator(Project project) {

		proj = project;
		constrainsManager = new ProjectConstrainsManager();
		parser = new ConstrainsFileParser();

		boardConstrainsFile = new File(proj.getLogisimFile().getOtherDir()+ File.separator + "constrains");
		try {
			boardConstrainsFile.getParentFile().mkdirs();
			boardConstrainsFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		deserializeVerilogFiles(proj.getLogisimFile().getOptions().getSerializedFilesContainer());

	}


	public void annotate(boolean ClearExistingLabels) {

		Circuit root = proj.getLogisimFile().getMainCircuit();

		if (ClearExistingLabels) {
			root.clearAnnotationLevel();
		}

		root.annotate(ClearExistingLabels);
		Reporter.report.addInfo(LC.get("FpgaGuiAnnotationDone"));

	}


	public void selectBoard() {

		String board = DialogManager.createBoardSelectionDialog(constrainsManager.getConstrainFiles());
		URL url = FPGAToolchainOrchestrator.class.getResource("/" + constrainsManager.CONSTRAINS_PATH + "/" + board);

		if (url != null) {
			try {

				File constrains = new File(url.toURI());

				FileChannel src = new FileInputStream(constrains).getChannel();
				FileChannel dest = new FileOutputStream(boardConstrainsFile).getChannel();
				dest.transferFrom(src, 0, src.size());

				parser.parseConstrainsFile(boardConstrainsFile);

			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void openConstrainsFile(){

		proj.getFrameController().addCodeEditor(boardConstrainsFile);

	}


	public void exportHDLFiles(long fpgaClockFreq, double frequency) {

		Reporter.report.clearConsole();

		//Choose output directory
		FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());
		File dir = fileSelector.chooseDirectory(LC.get("fileDirectorySelect"));

		if (dir == null) return;

		exportHDL(dir, fpgaClockFreq, frequency);

	}


	private void exportHDL(File dir, long fpgaClockFreq, double frequency) {

		String projDir = dir + File.separator + proj.getLogisimFile().getName();

		//Annotate all components
		annotate(false);

		//perform DRC check
		if (!performDrc(proj.getLogisimFile().getMainCircuit().getName())) {
			return;
		}

		mappableResourcesContainer = new MappableResourcesContainer(proj.getLogisimFile().getMainCircuit());
		System.out.println(mappableResourcesContainer.getMappableResources());

		if (frequency <= 0) frequency = 1;
		if (frequency > (fpgaClockFreq / 4)) {
			frequency = fpgaClockFreq / 4;
		}

		//mappableResourcesContainer = new MappableResourcesContainer(proj.getLogisimFile().getMainCircuit());

		writeHDL(projDir, fpgaClockFreq, frequency);

	}

	private void writeHDL(String projDir, long fpgaClockFreq, double frequency) {

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError(
					"Unable to create directory: \""
							+ projDir
							+ "\"");
			return;
		}

		Circuit topLevelCirc = proj.getLogisimFile().getMainCircuit();
		projDir += File.separator + topLevelCirc.getName();

		if (!cleanDirectory(projDir)) {
			Reporter.report.addFatalError(
					"Unable to cleanup old project files in directory: \"" + projDir + "\"");
			return;
		}

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError("Unable to create directory: \"" + projDir + "\"");
			return;
		}

		final HashSet<String> generatedHDLComponents = new HashSet<>();
		HdlGeneratorFactory worker = topLevelCirc.getSubcircuitFactory().getHDLGenerator(topLevelCirc.getStaticAttributes());
		if (worker == null) {
			Reporter.report.addFatalError("Internal error on HDL generation, null pointer exception");
			return;
		}
		if (!worker.generateAllHDLDescriptions(generatedHDLComponents, projDir, null)) {
			return;
		}
		/* Here we generate the top-level shell */
		if (topLevelCirc.getNetList().numberOfClockTrees() > 0) {

			final TickComponentHdlGeneratorFactory ticker =
					new TickComponentHdlGeneratorFactory(
							fpgaClockFreq,
							frequency /* , boardFreq.isSelected() */);

			if (!Hdl.writeArchitecture(
					projDir + ticker.getRelativeDirectory(),
					ticker.getArchitecture(
							topLevelCirc.getNetList(), null, TickComponentHdlGeneratorFactory.HDL_IDENTIFIER),
					TickComponentHdlGeneratorFactory.HDL_IDENTIFIER)) {
				return;
			}

			final HdlGeneratorFactory clockGen =
					topLevelCirc
							.getNetList()
							.getAllClockSources()
							.get(0)
							.getFactory()
							.getHDLGenerator(
									topLevelCirc.getNetList().getAllClockSources().get(0).getAttributeSet());
			final String compName =
					topLevelCirc.getNetList().getAllClockSources().get(0).getFactory().getHDLName(null);

			if (!Hdl.writeArchitecture(
					projDir + clockGen.getRelativeDirectory(),
					clockGen.getArchitecture(topLevelCirc.getNetList(), null, compName),
					compName)) {
				return;
			}
		}
/*
		final ToplevelHdlGeneratorFactory top = new ToplevelHdlGeneratorFactory(fpgaClockFreq, frequency, topLevelCirc, mappableResourcesContainer);

		if (top.hasLedArray()) {
			for (String type : LedArrayDriving.DRIVING_STRINGS) {
				if (top.hasLedArrayType(type)) {
					worker = LedArrayGenericHdlGeneratorFactory.getSpecificHDLGenerator(type);
					final String name = LedArrayGenericHdlGeneratorFactory.getSpecificHDLName(type);
					if (worker != null && name != null) {
						if (!Hdl.writeArchitecture(
								projDir + worker.getRelativeDirectory(),
								worker.getArchitecture(topLevelCirc.getNetList(), null, name),
								name)) {
							return;
						}
					}
				}
			}
		}

		Hdl.writeArchitecture(
				projDir + top.getRelativeDirectory(),
				top.getArchitecture(
						topLevelCirc.getNetList(), null, ToplevelHdlGeneratorFactory.FPGA_TOP_LEVEL_NAME),
						ToplevelHdlGeneratorFactory.FPGA_TOP_LEVEL_NAME
				);

*/

	}

	private boolean genDirectory(String dirPath) {
		try {
			File dir = new File(dirPath);
			return dir.exists() || dir.mkdirs();
		} catch (Exception e) {
			Reporter.report.addFatalError("Could not check/create directory :" + dirPath);
			return false;
		}
	}

	private boolean cleanDirectory(String dir) {
		try {
			final File thisDir = new File(dir);
			if (!thisDir.exists()) return true;
			for (File theFiles : thisDir.listFiles()) {
				if (theFiles.isDirectory()) {
					if (!cleanDirectory(theFiles.getPath())) return false;
				} else {
					if (!theFiles.delete()) return false;
				}
			}
			return thisDir.delete();
		} catch (Exception e) {
			Reporter.report.addFatalError("Could not remove directory tree :" + dir);
			return false;
		}
	}

	private boolean performDrc(String circuitName) {
		final var root = proj.getLogisimFile().getCircuit(circuitName);
		final var sheetNames = new ArrayList<String>();
		var drcResult = Netlist.DRC_PASSED;
		if (root == null) {
			drcResult |= Netlist.DRC_ERROR;
		} else {
			root.getNetList().clear();
			drcResult = root.getNetList().designRuleCheckResult(true, sheetNames);
		}
		return drcResult == Netlist.DRC_PASSED;
	}


	public void deserializeVerilogFiles(SerializedFilesContainer files){

		files.registerProject(proj);

		for (SerializedFilesContainer.SerializedFile serializedFile : files.getSerializedFiles()){

			try {
				byte[] decodedBytes = Base64.getDecoder().decode(serializedFile.data);
				String subPath = serializedFile.path.replace("\\/", File.separator).replace("\\", File.separator);
				Path path = Paths.get(proj.getLogisimFile().getProjectDir() + File.separator + subPath);
				if (!path.toFile().exists()){
					path.toFile().getParentFile().mkdirs();
					path.toFile().createNewFile();
				}
				Files.write(path, decodedBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public Element getSerializedFiles(Document doc) {

		Element files = doc.createElement("files");

		for (Circuit circuit: proj.getLogisimFile().getCircuits()){

			for (File filt : circuit.getHDLFiles(proj)){

				if (filt.length() > 0){

					Element file = doc.createElement("file");
					file.setAttribute("path", filt.toString().split(proj.getLogisimFile().getProjectDir().getFileName().toString())[1].substring(1));

					byte[] byteData = new byte[0];
					try {
						byteData = Files.readAllBytes(filt.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}

					String modelData = Base64.getEncoder().encodeToString(byteData);
					file.setAttribute("data", modelData);

					files.appendChild(file);

				}

			}

		}

		if (boardConstrainsFile.length() > 0) {

			Element file = doc.createElement("file");
			file.setAttribute("path", boardConstrainsFile.toString().split(proj.getLogisimFile().getOtherDir().getFileName().toString())[1].substring(1));

			byte[] byteData = new byte[0];
			try {
				byteData = Files.readAllBytes(boardConstrainsFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}

			String constrainsData = Base64.getEncoder().encodeToString(byteData);

			file.setAttribute("data", constrainsData);

			if (file.getChildNodes().getLength() > 0) {
				files.appendChild(file);
			}

		}

		if (files.getChildNodes().getLength() > 0) {
			return files;
		} else {
			return null;
		}

	}

}

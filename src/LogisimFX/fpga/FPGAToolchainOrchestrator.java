package LogisimFX.fpga;

import LogisimFX.FileSelector;
import LogisimFX.circuit.Circuit;
import LogisimFX.fpga.data.LedArrayDriving;
import LogisimFX.fpga.data.MappableResourcesContainer;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.TickComponentHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.ToplevelHdlGeneratorFactory;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab.TerminalMessageContainer;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

public class FPGAToolchainOrchestrator {

	private Project proj;

	private String dockerImageName = AppPreferences.DOCKER_IMAGE.get();

	private String targetArchitecture = "nexys4ddr";

	/*  arty_35 arty_100 nexys4ddr basys3 nexys_video zybo */

	private MappableResourcesContainer mappableResourcesContainer;

	private ProjectConstrainsManager constrainsManager;

	private File boardConstrainsFile;

	private String makefilecontent =
			"current_dir := ${CURDIR}\n\n" +
			"# TopFile/TopLevelShell name\n" +
			"TOP := {1}\n\n" +
			"# Target\n"+
			"TARGET := {2}\n\n"+
			"# Sources" +
			"SOURCES := {3}\n\n" +
			"# XDC file" +
			"XDC := {4}\n\n"+
			"include /shared/f4pga-examples/common/common.mk";


	public FPGAToolchainOrchestrator(Project project) {

		proj = project;
		constrainsManager = new ProjectConstrainsManager();

		boardConstrainsFile = new File(proj.getLogisimFile().getOtherDir() + File.separator + "constrains.xdc");
		try {
			boardConstrainsFile.getParentFile().mkdirs();
			boardConstrainsFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public void annotate(boolean ClearExistingLabels) {

		Reporter.report.setTerminal(proj.getTerminal());

		Circuit root = proj.getLogisimFile().getMainCircuit();

		if (ClearExistingLabels) {
			root.clearAnnotationLevel();
		}

		root.annotate(ClearExistingLabels);
		Reporter.report.addInfo(LC.get("FpgaGuiAnnotationDone"));

	}

	public void selectBoard() {

		String board = DialogManager.createBoardSelectionDialog(constrainsManager.getConstrainFiles());

		try {
			InputStream src = FPGAToolchainOrchestrator.class.getResource(
					"/" + constrainsManager.CONSTRAINS_PATH + "/" + board).openStream();
			FileOutputStream dest = new FileOutputStream(boardConstrainsFile);
			src.transferTo(dest);
			src.close();
			dest.close();
			proj.getFrameController().reloadFile(boardConstrainsFile);
			//parser.parseConstrainsFile(boardConstrainsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void selectChip(){

	}
	
	public void openConstrainsFile(){

		proj.getFrameController().addCodeEditor(boardConstrainsFile);

	}

	public void exportHDLFiles(long fpgaClockFreq, double frequency) {

		Reporter.report.setTerminal(proj.getTerminal());
		Reporter.report.clearConsole();

		//Choose output directory
		FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());
		File dir = fileSelector.chooseDirectory(LC.get("fileDirectorySelect"));

		if (dir == null) return;

		//Project directory
		String projDir = dir + File.separator + proj.getLogisimFile().getName();

		if (!cleanDirectory(projDir)) {
			Reporter.report.addFatalError(
					"Unable to cleanup old project files in directory: \"" + projDir + "\"");
			return;
		}

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError("Unable to create directory: \"" + projDir + "\"");
			return;
		}


		Circuit topLevelCirc = proj.getLogisimFile().getMainCircuit();
		projDir = projDir + File.separator + topLevelCirc.getName();

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError("Unable to create directory: \"" + projDir + "\"");
			return;
		}

		exportHDL(projDir, fpgaClockFreq, frequency);

	}

	public void generateBitFile(long fpgaClockFreq, double frequency){

		//Clear terminal
		Reporter.report.setTerminal(proj.getTerminal());
		Reporter.report.clearConsole();

		if (!boardConstrainsFile.exists()){
			Reporter.report.addFatalError(LC.get("boardConstrainsFileNotExist"));
			return;
		}
		if (boardConstrainsFile.length() == 0){
			TerminalMessageContainer mc = new TerminalMessageContainer(
					boardConstrainsFile,
					LC.get("boardConstrainsFileEmpty"),
					TerminalMessageContainer.LEVEL_SEVERE
			);
			Reporter.report.addWarning(mc);
		}

		//Export .v files to fpgaBuild
		exportHDL(proj.getLogisimFile().getFpgaDir().toString(), fpgaClockFreq, frequency);

		//Generate makefile in fpgaBuild
		generateMakefile();

		//Copy constrains.xdc to fpgaBuild
		try {
			FileUtils.copyFileToDirectory(boardConstrainsFile, proj.getLogisimFile().getFpgaDir().toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Start Docker


	}


	private void exportHDL(String dir, long fpgaClockFreq, double frequency) {

		//Annotate all components
		annotate(false);

		//perform DRC check
		if (!performDrc(proj.getLogisimFile().getMainCircuit().getName())) {
			return;
		}

		if (frequency <= 0) frequency = 1;
		if (frequency > (fpgaClockFreq / 4)) {
			frequency = fpgaClockFreq / 4;
		}

		mappableResourcesContainer = new MappableResourcesContainer(proj.getLogisimFile().getMainCircuit());

		writeHDL(dir, fpgaClockFreq, frequency);

	}

	private void writeHDL(String projDir, long fpgaClockFreq, double frequency) {

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError("Unable to create directory: \"" + projDir + "\"");
			return;
		}

		Circuit topLevelCirc = proj.getLogisimFile().getMainCircuit();

		final HashSet<String> generatedHDLComponents = new HashSet<>();
		HdlGeneratorFactory worker = topLevelCirc.getSubcircuitFactory().getHDLGenerator(topLevelCirc.getStaticAttributes());
		if (worker == null) {
			Reporter.report.addFatalError("Internal error on HDL generation, null pointer exception");
			return;
		}
		if (!worker.generateAllHDLDescriptions(proj, generatedHDLComponents, projDir, null)) {
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


	private void generateMakefile(){


		StringBuilder files = new StringBuilder();

		try (Stream<Path> paths = Files.walk(proj.getLogisimFile().getFpgaDir())) {
			paths.filter(Files::isRegularFile).forEach(
					path -> files.append("${current_dir}")
							.append(
									path.toString().replace(proj.getLogisimFile().getFpgaDir().toString(), "")
											.replace("\\", "/")
									)
							.append(" \\ \n")
			);
		} catch (IOException e) {
			e.printStackTrace();
		}

		files.replace(files.length()-4, files.length(), "");

		String content  = makefilecontent.
							replace("{1}", proj.getLogisimFile().getMainCircuit().getTopLevelShell(proj).getName()).
							replace("{2}", targetArchitecture).
							replace("{3}", files.toString()).
							replace("{4}", "${current_dir}/"+boardConstrainsFile.getName());


		System.out.println(content);
		try {
			FileUtils.write(new File(proj.getLogisimFile().getFpgaDir()+File.separator+"makefile.mk"), content);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

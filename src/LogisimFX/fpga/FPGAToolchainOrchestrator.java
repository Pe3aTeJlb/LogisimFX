package LogisimFX.fpga;

import LogisimFX.FileSelector;
import LogisimFX.circuit.Circuit;
import LogisimFX.fpga.data.*;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.file.BoardReaderClass;
import LogisimFX.fpga.file.FileWriter;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.TickComponentHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.ToplevelHdlGeneratorFactory;
import LogisimFX.newgui.FrameManager;
import LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab.TerminalMessageContainer;
import LogisimFX.proj.Project;
import LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory;
import LogisimFX.util.LineBuffer;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FPGAToolchainOrchestrator {

	private Project proj;

	private String dockerImageName = "pe3atejlb/logisimfx-f4pga:latest";

	private FPGABoardsManager boardsManager;
	private String selectedBoard = "BASYS3";
	private BoardInformation boardInformation = null;
	private MappableResourcesContainer mappableResourcesContainer;
	/*  arty_35 arty_100 nexys4ddr basys3 nexys_video zybo */


	private ProjectConstrainsManager constrainsManager;
	private boolean generateConstrainsFile = true;
	private File boardConstrainsFile;
	private String BOARD_CONSTRAINS_FILE_NAME = "constrains.xdc";

	private double frequency = -1, divider = -1;

	private boolean generateTopLevel = true;
	private int actionNum = 0;
	private int EXPORT_FILES = 0;
	private int GENERATE_BIT_FILE = 1;
	private boolean relableAll = false;

	private String makefilecontent =
			"current_dir := ${CURDIR}\n\n" +
			"# TopFile/TopLevelShell name\n" +
			"TOP := {1}\n\n" +
			"# Target\n"+
			"TARGET := {2}\n\n"+
			"# Sources" +
			"SOURCES := {3}\n\n" +
			"# XDC file\n" +
			"XDC := {4}\n\n"+
			"include /shared/f4pga-examples/common/common.mk";


	public FPGAToolchainOrchestrator(Project project) {

		proj = project;

		boardsManager = new FPGABoardsManager();
		updateBoardInformation(selectedBoard);

		constrainsManager = new ProjectConstrainsManager();
		boardConstrainsFile = new File(proj.getLogisimFile().getOtherDir() + File.separator + BOARD_CONSTRAINS_FILE_NAME);
		try {
			boardConstrainsFile.getParentFile().mkdirs();
			boardConstrainsFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		restoreOrchestratorData(proj.getLogisimFile().getOptions().getFPGAToolchainOrchestratorData());

	}

	public void restoreOrchestratorData(FPGAToolchainOrchestratorData data){

		data.registerOrchestrator(this);

		if (!data.isDefault()) {
			setDockerImageName(data.getDockerImg());
			setSelectedBoard(data.getBoardName());
			setGenerateConstrainsFile(data.isGenerateConstrains());
			setFrequency(data.getFreq());
			setDivider(data.getDiv());
		}

	}


	//1. Docker image

	public String getDockerImageName(){
		return dockerImageName;
	}

	public void setDockerImageName(String s){
		dockerImageName = s;
	}

	//2. Board settings

	public List<String> getBoardsList(){
		return boardsManager.getBoardNames();
	}

	public String getSelectedBoard(){
		return selectedBoard;
	}

	public void setSelectedBoard(String s){
		selectedBoard = s;
	}

	public BoardInformation getBoardInformation(){
		return boardInformation;
	}

	public void updateBoardInformation(String boardName){
		selectedBoard = boardName;
		try {
			InputStream src = FPGAToolchainOrchestrator.class.getResource(
					"/" + boardsManager.BOARDS_PATH + "/" + boardsManager.getBoardFilePath(boardName)).openStream();
			boardInformation = new BoardReaderClass(src).getBoardInformation();
			boardInformation.setBoardName(selectedBoard);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mapComponents(){

		Reporter.report.setTerminal(proj.getTerminal());
		Reporter.report.clearConsole();

		//perform DRC check
		if (!performDrc(proj.getLogisimFile().getMainCircuit().getName())) {
			return;
		}

		if (!mapDesign()){
			return;
		}

		FrameManager.CreateIOMapperFrame(proj);

	}

	private boolean mapDesign() {

		Circuit circ = proj.getLogisimFile().getMainCircuit();

		if (circ == null) {
			Reporter.report.addError("INTERNAL ERROR: Circuit not found ?!?");
			return false;
		}
		if (boardInformation == null) {
			Reporter.report.addError("INTERNAL ERROR: No board information available ?!?");
			return false;
		}

		final var boardComponents = boardInformation.getComponents();
		Reporter.report.addInfo("The Board " + boardInformation.getBoardName() + " has:");
		for (final var key : boardComponents.keySet()) {
			Reporter.report.addInfo(boardComponents.get(key).size() + " " + key + "(s)");
		}
		/*
		 * At this point I require 2 sorts of information: 1) A hierarchical
		 * netlist of all the wires that needs to be bubbled up to the toplevel
		 * in order to connect the LEDs, Buttons, etc. (hence for the HDL
		 * generation). 2) A list with all components that are required to be
		 * mapped to PCB components. Identification can be done by a hierarchy
		 * name plus component/sub-circuit name
		 */
		getMappableResourcesContainer();

		return true;

	}

	public MappableResourcesContainer getMappableResourcesContainer(){
		mappableResourcesContainer = proj.getLogisimFile().getMainCircuit().getBoardMap(boardInformation.getBoardName());
		if (mappableResourcesContainer == null) {
			mappableResourcesContainer = new MappableResourcesContainer(boardInformation, proj.getLogisimFile().getMainCircuit());
		} else {
			mappableResourcesContainer.updateMapableComponents();
		}
		return mappableResourcesContainer;
	}

	//3. Constrains settings

	public boolean isGenerateConstrainsFile(){
		return generateConstrainsFile;
	}

	public void setGenerateConstrainsFile(boolean b){
		generateConstrainsFile = b;
	}

	public ArrayList<String> getConstrainsFiles(){
		return constrainsManager.getConstrainFiles();
	}

	public void setConstrainsFile(String filename){

		try {
			InputStream src = FPGAToolchainOrchestrator.class.getResource(
					"/" + constrainsManager.CONSTRAINS_PATH + "/" + filename).openStream();
			FileOutputStream dest = new FileOutputStream(boardConstrainsFile);
			src.transferTo(dest);
			src.close();
			dest.close();
			proj.getFrameController().reloadFile(boardConstrainsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void openConstrainsFile(){
		proj.getFrameController().addCodeEditor(boardConstrainsFile);
	}

	//4. Frequency settings

	public double getFrequency(){
		return frequency;
	}

	public void setFrequency(double d){
		frequency = d;
	}

	public double getDivider(){
		return divider;
	}

	public void setDivider(double d){
		divider = d;
	}

	//5. Annotation settings

	public void setRelableAll(boolean b){
		relableAll = b;
	}

	public boolean getRelableAll(){
		return relableAll;
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

	//6. Action settings

	public void setGenerateTopLevel(boolean b){
		generateTopLevel = b;
	}

	public boolean isGenerateTopLevel(){
		return generateTopLevel;
	}

	public int getActionNum(){
		return actionNum;
	}

	public void setActionNum(int i){
		actionNum = i;
	}

	//7. Execution

	public void execute(int type){

		Reporter.report.setTerminal(proj.getTerminal());
		Reporter.report.clearConsole();

		if (!performDrc(proj.getLogisimFile().getMainCircuit().getName())) {
			return;
		}

		getMappableResourcesContainer();

		File bitOutdir = null;
		Path destDir = null;

		if (type == EXPORT_FILES){
			destDir = getUserDestDir();
		} else if (type == GENERATE_BIT_FILE){
			//Choose output directory
			FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());
			bitOutdir = fileSelector.chooseDirectory(LC.get("fileDirectorySelect"));
			if (bitOutdir == null) return;
			destDir = getLogisimFXFPGADir();
		}
		if (destDir == null) return;

		//Export .v files to fpgaBuild
		exportHDL(destDir, frequency);

		//Generate makefile in fpgaBuild
		generateMakefile(destDir);

		//Process board constrains file
		processBoardConstrainsFile(destDir);

		if (type == GENERATE_BIT_FILE){
			executeDocker(bitOutdir.toString());
		}

	}


	private Path getUserDestDir(){

		//Choose output directory
		FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());
		File dir = fileSelector.chooseDirectory(LC.get("fileDirectorySelect"));

		if (dir == null) return null;

		//Generate Project directory
		Path projDir = Paths.get(dir.toString() + File.separator + proj.getLogisimFile().getName());

		if (!cleanDirectory(projDir)) {
			Reporter.report.addFatalError(
					"Unable to cleanup old project files in directory: \"" + projDir + "\"");
			return null;
		}

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError("Unable to create directory: \"" + projDir + "\"");
			return null;
		}

		//Generate directory for output relative to current toplevel circuit
		Circuit topLevelCirc = proj.getLogisimFile().getMainCircuit();
		projDir = Paths.get(projDir.toString() + File.separator + topLevelCirc.getName());

		if (!genDirectory(projDir)) {
			Reporter.report.addFatalError("Unable to create directory: \"" + projDir + "\"");
			return null;
		}

		return projDir;

	}

	private Path getLogisimFXFPGADir(){

		if (!cleanDirectory(proj.getLogisimFile().getFpgaDir())) {
			Reporter.report.addFatalError(
					"Unable to cleanup old project files in directory: \"" + proj.getLogisimFile().getFpgaDir() + "\"");
			return null;
		}

		if (!genDirectory(proj.getLogisimFile().getFpgaDir())) {
			Reporter.report.addFatalError("Unable to create directory: \"" + proj.getLogisimFile().getFpgaDir() + "\"");
			return null;
		}

		return proj.getLogisimFile().getFpgaDir();

	}


	private void exportHDL(Path destDir, double frequency) {

		//Annotate all components
		annotate(false);

		if (frequency <= 0) frequency = 1;
		if (frequency > (boardInformation.fpga.getClockFrequency() / 4)) {
			frequency = boardInformation.fpga.getClockFrequency() / 4;
		}

		writeHDL(destDir, frequency);

	}

	private void writeHDL(Path destDir, double frequency) {

		Circuit topLevelCirc = proj.getLogisimFile().getMainCircuit();

		final HashSet<String> generatedHDLComponents = new HashSet<>();
		HdlGeneratorFactory worker = topLevelCirc.getSubcircuitFactory().getHDLGenerator(topLevelCirc.getStaticAttributes());
		if (worker == null) {
			Reporter.report.addFatalError("Internal error on HDL generation, null pointer exception");
			return;
		}
		if (!worker.generateAllHDLDescriptions(proj, generatedHDLComponents, destDir.toString(), null)) {
			return;
		}
		/* Here we generate the top-level shell */
		if (topLevelCirc.getNetList().numberOfClockTrees() > 0) {

			final TickComponentHdlGeneratorFactory ticker =
					new TickComponentHdlGeneratorFactory(
							boardInformation.fpga.getClockFrequency(),
							frequency /* , boardFreq.isSelected() */);

			if (!Hdl.writeArchitecture(
					destDir.toString() + File.separator + ticker.getRelativeDirectory(),
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
					destDir.toString() + File.separator + clockGen.getRelativeDirectory(),
					clockGen.getArchitecture(topLevelCirc.getNetList(), null, compName),
					compName)) {
				return;
			}
		}

		final ToplevelHdlGeneratorFactory top = new ToplevelHdlGeneratorFactory(boardInformation.fpga.getClockFrequency(), frequency, topLevelCirc, mappableResourcesContainer);

		if (top.hasLedArray()) {
			for (String type : LedArrayDriving.DRIVING_STRINGS) {
				if (top.hasLedArrayType(type)) {
					worker = LedArrayGenericHdlGeneratorFactory.getSpecificHDLGenerator(type);
					final String name = LedArrayGenericHdlGeneratorFactory.getSpecificHDLName(type);
					if (worker != null && name != null) {
						if (!Hdl.writeArchitecture(
								destDir.toString() + File.separator + worker.getRelativeDirectory(),
								worker.getArchitecture(topLevelCirc.getNetList(), null, name),
								name)) {
							return;
						}
					}
				}
			}
		}

		if (generateTopLevel) {
			Hdl.writeArchitecture(
					destDir.toString() + File.separator + top.getRelativeDirectory(),
					top.getArchitecture(
							topLevelCirc.getNetList(), null, ToplevelHdlGeneratorFactory.FPGA_TOP_LEVEL_NAME),
					ToplevelHdlGeneratorFactory.FPGA_TOP_LEVEL_NAME
			);
		} else {
			try {
				FileUtils.copyFileToDirectory(topLevelCirc.getTopLevelShell(proj),
						Paths.get(destDir.toString() + File.separator + top.getRelativeDirectory()).toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	private void generateMakefile(Path destDir){

		StringBuilder files = new StringBuilder();

		try (Stream<Path> paths = Files.walk(destDir)) {
			paths.filter(Files::isRegularFile).forEach(
					path -> files.append("${current_dir}")
							.append(
									path.toString().replace(destDir.toString(), "")
											.replace("\\", "/")
							)
							.append(" \\ \n")
			);
		} catch (IOException e) {
			e.printStackTrace();
		}

		files.append(" \\");

		files.replace(files.length()-4, files.length(), "");

		String content  = makefilecontent.
				replace("{1}", proj.getLogisimFile().getMainCircuit().getTopLevelShell(proj).getName()).
				replace("{2}", boardInformation.fpga.getF4pgatarget()).
				replace("{3}", files.toString()).
				replace("{4}", "${current_dir}/"+boardConstrainsFile.getName());


		try {
			FileUtils.write(new File(destDir+File.separator+"makefile.mk"), content);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void processBoardConstrainsFile(Path destDir){

		if (generateConstrainsFile){
			generateBoardConstrainsFile();
		} else {

			if (!boardConstrainsFile.exists()) {
				Reporter.report.addFatalError(LC.get("boardConstrainsFileNotExist"));
				return;
			}
			if (boardConstrainsFile.length() == 0) {
				TerminalMessageContainer mc = new TerminalMessageContainer(
						boardConstrainsFile,
						LC.get("boardConstrainsFileEmpty"),
						TerminalMessageContainer.LEVEL_SEVERE
				);
				Reporter.report.addWarning(mc);
			}

		}

		try {
			FileUtils.copyFileToDirectory(boardConstrainsFile, destDir.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void generateBoardConstrainsFile(){

		var contents = new ArrayList<String>();
		Netlist rootNetList = proj.getLogisimFile().getMainCircuit().getNetList();

		if (rootNetList.numberOfClockTrees() > 0 || rootNetList.requiresGlobalClockConnection()) {
			final var clockPin = boardInformation.fpga.getClockPinLocation();
			final var clockSignal = TickComponentHdlGeneratorFactory.FPGA_CLOCK;
			final var getPortsString = " [get_ports {" + clockSignal + "}]";
			contents.add("set_property PACKAGE_PIN " + clockPin + getPortsString);

			if (boardInformation.fpga.getClockStandard() != IoStandards.DEFAULT_STANDARD
					&& boardInformation.fpga.getClockStandard() != IoStandards.UNKNOWN) {
				final var clockIoStandard = IoStandards.BEHAVIOR_STRINGS[boardInformation.fpga.getClockStandard()];
				contents.add("    set_property IOSTANDARD " + clockIoStandard + getPortsString);
			}

			final var clockFrequency = boardInformation.fpga.getClockFrequency();
			var clockPeriod = 1000000000.0 / (double) clockFrequency;
			contents.add(
					"    create_clock -add -name sys_clk_pin -period "
							+ String.format(Locale.US, "%.2f", clockPeriod)
							+ " -waveform {0 "
							+ String.format("%1$,.0f", clockPeriod / 2)
							+ "} "
							+ getPortsString);
			contents.add("");
		}

		contents.addAll(getPinLocStrings());
		FileWriter.writeContents(boardConstrainsFile, contents);

	}

	private List<String> getPinLocStrings() {
		final var contents = LineBuffer.getBuffer();
		for (final var key : mappableResourcesContainer.getMappableResources().keySet()) {
			final var map = mappableResourcesContainer.getMappableResources().get(key);
			for (var i = 0; i < map.getNrOfPins(); i++) {
				if (map.isMapped(i) && !map.isOpenMapped(i) && !map.isConstantMapped(i) && !map.isInternalMapped(i)) {
					final var netName = (map.isExternalInverted(i) ? "n_" : "") + map.getHdlString(i);
					// Note {{2}} is wrapped in additional {}!
					contents.add("set_property PACKAGE_PIN {{1}} [get_ports {{{2}}}]", map.getPinLocation(i), netName);
					final var info = map.getFpgaInfo(i);
					if (info != null) {
						final var ioStandard = info.getIoStandard();
						if (ioStandard != IoStandards.UNKNOWN && ioStandard != IoStandards.DEFAULT_STANDARD)
							contents.add("    set_property IOSTANDARD {{1}} [get_ports {{{2}}}]", IoStandards.getConstraintedIoStandard(info.getIoStandard()), netName);
					}
				}
			}
		}
		final var LedArrayMap = getLedArrayMaps(mappableResourcesContainer, proj.getLogisimFile().getMainCircuit().getNetList(), boardInformation);
		for (final var key : LedArrayMap.keySet()) {
			contents.add("set_property PACKAGE_PIN {{1}} [get_ports {{{2}}}]", key, LedArrayMap.get(key));
		}
		return contents.get();
	}

	public static Map<String, String> getLedArrayMaps(
			MappableResourcesContainer maps, Netlist nets, BoardInformation board) {
		final var ledArrayMaps = new HashMap<String, String>();
		var hasMappedClockedArray = false;
		for (final var comp : maps.getIOComponents()) {
			if (comp.getType().equals(IoComponentTypes.LedArray)) {
				if (comp.hasMap()) {
					hasMappedClockedArray |=
							LedArrayGenericHdlGeneratorFactory.requiresClock(comp.getArrayDriveMode());
					for (var pin = 0; pin < comp.getExternalPinCount(); pin++) {
						ledArrayMaps.put(
								LedArrayGenericHdlGeneratorFactory.getExternalSignalName(
										comp.getArrayDriveMode(),
										comp.getNrOfRows(),
										comp.getNrOfColumns(),
										comp.getArrayId(),
										pin),
								comp.getPinLocation(pin));
					}
				}
			}
		}
		if (hasMappedClockedArray
				&& (nets.numberOfClockTrees() == 0)
				&& !nets.requiresGlobalClockConnection()) {
			ledArrayMaps.put(
					TickComponentHdlGeneratorFactory.FPGA_CLOCK, board.fpga.getClockPinLocation());
		}
		return ledArrayMaps;
	}


	private void executeDocker(String outdir){

	}


	private boolean genDirectory(Path dirPath) {
		try {
			File dir = dirPath.toFile();
			return dir.exists() || dir.mkdirs();
		} catch (Exception e) {
			Reporter.report.addFatalError("Could not check/create directory :" + dirPath);
			return false;
		}
	}

	private boolean cleanDirectory(Path dir) {
		try {
			final File thisDir = dir.toFile();
			if (!thisDir.exists()) return true;
			for (File theFiles : thisDir.listFiles()) {
				if (theFiles.isDirectory()) {
					if (!cleanDirectory(theFiles.toPath())) return false;
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


	public Element getOrchestratorData(Document doc){

		Element fpga = doc.createElement("fpgaOrchestrator");

		Element dockerImg = doc.createElement("dockerImg");
		dockerImg.setAttribute("name", dockerImageName);

		Element board = doc.createElement("board");
		board.setAttribute("name", selectedBoard);

		Element generateConstrains = doc.createElement("generateConstrains");
		generateConstrains.setAttribute("gen", Boolean.toString(isGenerateConstrainsFile()));

		Element freq = doc.createElement("frequency");
		freq.setAttribute("val", Double.toString(frequency));
		Element div = doc.createElement("divider");
		div.setAttribute("val", Double.toString(divider));

		Element generateTopLevel = doc.createElement("generateTopLevel");
		generateTopLevel.setAttribute("gen", Boolean.toString(isGenerateTopLevel()));

		fpga.appendChild(dockerImg);
		fpga.appendChild(board);
		fpga.appendChild(generateConstrains);
		fpga.appendChild(freq);
		fpga.appendChild(div);
		fpga.appendChild(generateTopLevel);

		return fpga;

	}

	public String getTopLevelShellCode(Circuit circ){

		Reporter.report.setTerminal(proj.getTerminal());
		Reporter.report.clearConsole();

		StringBuilder builder = new StringBuilder();

		MappableResourcesContainer mappableResourcesContainer = getMappableResourcesContainer();
		ToplevelHdlGeneratorFactory top = new ToplevelHdlGeneratorFactory(boardInformation.fpga.getClockFrequency(), frequency, circ, mappableResourcesContainer);

		final var sheetNames = new ArrayList<String>();
		circ.getNetList().designRuleCheckResult(true, sheetNames);

		for(String s: top.getArchitecture(
				circ.getNetList(), null,  ToplevelHdlGeneratorFactory.FPGA_TOP_LEVEL_NAME)) {
			builder.append(s).append("\n");
		}

		return builder.toString();

	}

}

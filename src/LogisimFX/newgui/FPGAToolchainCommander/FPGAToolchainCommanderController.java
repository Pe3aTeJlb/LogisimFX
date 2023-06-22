package LogisimFX.newgui.FPGAToolchainCommander;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.fpga.FPGAToolchainOrchestrator;
import LogisimFX.fpga.LC;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.FrameManager;
import LogisimFX.newgui.MainFrame.ProjectCircuitActions;
import LogisimFX.proj.Project;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FPGAToolchainCommanderController extends AbstractController {

	private Stage stage;


	@FXML
	private Label DockerImageLbl;

	@FXML
	private TextField DockerImageTxtfld;

	@FXML
	private Button DockerImageInfoBtn;



	@FXML
	private Label BoardLbl;

	@FXML
	private ChoiceBox<String> BoardChbx;

	@FXML
	private ImageView BoardImg;

	@FXML
	private Button BoardIOMapperBtn;



	@FXML
	private Label ConstrainsLbl;

	@FXML
	private CheckBox ConstrainsChckBx;

	@FXML
	private ChoiceBox<String> ConstrainsChbx;

	@FXML
	private Button OpenConstrainsBtn;



	@FXML
	private Label ClockSettingsLbl;

	@FXML
	private Label ClockSettingsFreqLbl;

	@FXML
	private TextField ClockSettingsFreqTxtfld;

	@FXML
	private Label ClockSettingsDividerLbl;

	@FXML
	private TextField ClockSettingsDividerTxtfld;



	@FXML
	private Label AnnotationLbl;

	@FXML
	private ChoiceBox<Boolean> AnnotationTypeChbx;

	@FXML
	private Button AnnotationBtn;



	@FXML
	private Label ActionLbl;

	@FXML
	private Label MainCircLbl;

	@FXML
	private ChoiceBox<Circuit> MainCircChbx;

	@FXML
	private CheckBox TopLevelChckBx;

	@FXML
	private ChoiceBox<Integer> Actionchbx;

	@FXML
	private Button ExecuteBtn;


	private Project proj;
	private FPGAToolchainOrchestrator orchestrator;

	private double FPGAClockFrequency;

	LibListener libListener = new LibListener();

	class LibListener implements LibraryListener {

		public void libraryChanged(LibraryEvent e) {

			if (e.getAction() == LibraryEvent.SET_NAME) {
				updateCircList();
			}

			if (e.getAction() == LibraryEvent.SET_MAIN){
				updateCircList();
			}

			if (e.getAction() == LibraryEvent.REMOVE_TOOL) {
				updateCircList();
			}

			if(e.getAction() == LibraryEvent.ADD_TOOL){
				updateCircList();
			}

		}

	}


	@FXML
	public void initialize(){ }

	@Override
	public void postInitialization(Stage s, Project project) {

		stage = s;
		s.setWidth(550);
		s.setHeight(500);
		s.setResizable(false);
		s.titleProperty().bind(LC.createStringBinding("commanderTitle"));

		this.proj = project;
		this.proj.addLibraryListener(libListener);
		this.orchestrator = project.getFpgaToolchainOrchestrator();


		DockerImageLbl.textProperty().bind(LC.createStringBinding("dockerImage"));
		DockerImageTxtfld.setPromptText("pe3atejlb/logisimfx-f4pga:latest");
		DockerImageTxtfld.setText(orchestrator.getDockerImageName());
		DockerImageInfoBtn.setOnAction(event -> {
			DialogManager.createInfoDialog(LC.get("dockerInfoHeader"), LC.get("dockerInfoBody"));
		});


		BoardLbl.textProperty().bind(LC.createStringBinding("targetBoard"));
		BoardChbx.getItems().addAll(orchestrator.getBoardsList());
		BoardChbx.setValue(orchestrator.getSelectedBoard());
		BoardChbx.setOnAction(event -> {
			orchestrator.updateBoardInformation(BoardChbx.getValue());
			BoardImg.setImage(orchestrator.getBoardInformation().getImage());
			setFpgaClockFrequency(orchestrator.getBoardInformation().fpga.getClockFrequency());
		});
		BoardImg.setImage(orchestrator.getBoardInformation().getImage());
		BoardIOMapperBtn.textProperty().bind(LC.createStringBinding("openIOMapper"));
		BoardIOMapperBtn.setOnAction(event ->orchestrator.mapComponents());


		ConstrainsLbl.textProperty().bind(LC.createStringBinding("constrainsFile"));
		ConstrainsChckBx.textProperty().bind(LC.createStringBinding("constrainsCheckBox"));
		ConstrainsChckBx.setSelected(orchestrator.isGenerateConstrainsFile());
		ConstrainsChckBx.setOnAction(event -> orchestrator.setGenerateConstrainsFile(ConstrainsChckBx.isSelected()));
		ConstrainsChbx.disableProperty().bind(ConstrainsChckBx.selectedProperty());
		ConstrainsChbx.getItems().addAll(orchestrator.getConstrainsFiles());
		ConstrainsChbx.setOnAction(event -> orchestrator.setConstrainsFile(ConstrainsChbx.getValue()));
		OpenConstrainsBtn.textProperty().bind(LC.createStringBinding("openConstrainsFile"));
		OpenConstrainsBtn.disableProperty().bind(ConstrainsChckBx.selectedProperty());
		OpenConstrainsBtn.setOnAction(event -> orchestrator.openConstrainsFile());


		ClockSettingsLbl.textProperty().bind(LC.createStringBinding("clockSettings"));
		ClockSettingsFreqLbl.textProperty().bind(LC.createStringBinding("FpgaFreqFrequency"));
		ClockSettingsFreqTxtfld.setOnAction(event -> recalculateFrequency());
		setSelectedFrequency(orchestrator.getFrequency() == -1 ? proj.getSimulator().getTickFrequency() : orchestrator.getFrequency());
		ClockSettingsDividerLbl.textProperty().bind(LC.createStringBinding("FpgaFreqDivider"));
		ClockSettingsDividerTxtfld.setOnAction(keyEvent -> recalculateDivider());
		setFpgaClockFrequency(orchestrator.getBoardInformation().fpga.getClockFrequency());


		AnnotationLbl.textProperty().bind(LC.createStringBinding("annotation"));
		AnnotationTypeChbx.getItems().add(false);
		AnnotationTypeChbx.getItems().add(true);
		AnnotationTypeChbx.setConverter(new StringConverter<>() {
			@Override
			public String toString(Boolean aBoolean) {
				return aBoolean ? LC.get("FpgaGuiRelabelAll") : LC.get("FpgaGuiRelabelEmpty");
			}

			@Override
			public Boolean fromString(String s) {
				if (s.equals(LC.get("FpgaGuiRelabelAll"))) {
					return true;
				} else {
					return false;
				}
			}
		});
		AnnotationTypeChbx.setValue(orchestrator.getRelableAll());
		AnnotationBtn.textProperty().bind(LC.createStringBinding("FpgaGuiAnnotate"));
		AnnotationBtn.setOnAction(event -> orchestrator.annotate(AnnotationTypeChbx.getValue()));


		ActionLbl.textProperty().bind(LC.createStringBinding("action"));
		MainCircLbl.textProperty().bind(LC.createStringBinding("FpgaGuiMainCircuit"));
		MainCircChbx.setConverter(new StringConverter<>() {
			@Override
			public String toString(Circuit circuit) {
				return circuit.getName();
			}

			@Override
			public Circuit fromString(String s) {
				return proj.getLogisimFile().getCircuit(s);
			}
		});
		MainCircChbx.setOnAction(event -> ProjectCircuitActions.doSetAsMainCircuit(proj, MainCircChbx.getValue()));
		updateCircList();
		TopLevelChckBx.textProperty().bind(LC.createStringBinding("toplevelCheckBox"));
		TopLevelChckBx.setSelected(orchestrator.isGenerateTopLevel());
		TopLevelChckBx.setOnAction(event -> orchestrator.setGenerateTopLevel(TopLevelChckBx.isSelected()));
		Actionchbx.getItems().addAll(0, 1);
		Actionchbx.setConverter(new StringConverter<>() {
			@Override
			public String toString(Integer integer) {
				if (integer == 0) {
					return LC.get("fpgaExportFilesItem");
				} else {
					return LC.get("fpgaGenerateBitFile");
				}
			}

			@Override
			public Integer fromString(String s) {
				if (s.equals(LC.get("fpgaExportFilesItem"))) {
					return 0;
				} else if (s.equals(LC.get("fpgaGenerateBitFile"))) {
					return 1;
				}
				return null;
			}
		});
		Actionchbx.setValue(orchestrator.getActionNum());
		Actionchbx.setOnAction(event -> orchestrator.setActionNum(Actionchbx.getValue()));

		ExecuteBtn.textProperty().bind(LC.createStringBinding("FpgaGuiExecute"));
		ExecuteBtn.setOnAction(event -> orchestrator.execute(Actionchbx.getValue()));

	}

	private void setFpgaClockFrequency(long frequency) {
		FPGAClockFrequency = frequency;
		recalculateFrequency();
	}

	public double getTickFrequency() {
		String TickIndex =  ClockSettingsFreqTxtfld.getText().trim().toUpperCase();
		int i = 0;
		/* first pass, find the number */
		StringBuilder number = new StringBuilder();
		while (i < TickIndex.length()
				&& (TickIndex.charAt(i) == '.' || Character.isDigit(TickIndex.charAt(i))))
			number.append(TickIndex.charAt(i++));
		/*second pass, get the Hz, etc */
		char extention = 0;
		while (i < TickIndex.length()) {
			if (TickIndex.charAt(i) == 'K' || TickIndex.charAt(i) == 'M')
				extention = TickIndex.charAt(i);
			i++;
		}
		switch (extention) {
			case 'K':  return Double.parseDouble(number.toString()) * 1000d;
			case 'M':  return Double.parseDouble(number.toString()) * 1000000d;
			default :  return Double.parseDouble(number.toString()) * 1d;
		}
	}

	private void recalculateFrequency() {
		double freq = getTickFrequency();
		double divider = FPGAClockFrequency / freq;
		long longDivider = (long) divider;
		if (longDivider <= 1) longDivider = 2;
		if ((longDivider & 1) != 0) longDivider++;
		double corfreq = FPGAClockFrequency / longDivider;
		ClockSettingsDividerTxtfld.setText(Long.toString((longDivider) >> 1));
		setSelectedFrequency(corfreq);
	}

	private void recalculateDivider() {
		long divider = 0;
		try {
			divider = Long.parseUnsignedLong(ClockSettingsDividerTxtfld.getText());
		} catch (NumberFormatException e) {
			recalculateFrequency();
			return;
		}
		divider <<= 1;
		if (divider <= 1) divider = 2;
		double corfreq = FPGAClockFrequency / divider;
		if (corfreq < 0.00001) {
			recalculateFrequency();
			return;
		}
		System.out.println(corfreq);
		setSelectedFrequency(corfreq);
	}

	private void setSelectedFrequency(double freq) {
		if (freq <= 0) return;
		StringBuilder extention = new StringBuilder();
		extention.append(" ");
		double work = freq;
		if (work > 1000000.0) {
			extention.append("M");
			work /= 1000000.0;
		}
		if (work > 1000.0) {
			extention.append("k");
			work /= 1000.0;
		}
		extention.append("Hz");
		DecimalFormat df = new DecimalFormat("#.#####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		String tick = df.format(work) + extention;
		ClockSettingsFreqTxtfld.setText(tick);
	}




	private void updateCircList(){

		MainCircChbx.getItems().clear();

		//Circuits
		for (AddTool tool: proj.getLogisimFile().getTools()) {
			addCirc(tool);
		}

		//Libs and tools
		for (Library lib: proj.getLogisimFile().getLibraries()) {
			checkLibrary(lib);
		}

		MainCircChbx.setValue(proj.getLogisimFile().getMainCircuit());

	}

	private void addCirc(AddTool tool){
		MainCircChbx.getItems().add(((SubcircuitFactory)tool.getFactory()).getSubcircuit());
	}

	private void checkLibrary(Library lib){
		for (Tool tool: lib.getTools()) {
			if (tool instanceof AddTool) {
				ComponentFactory fact = ((AddTool) tool).getFactory(false);
				if (fact instanceof SubcircuitFactory) {
					addCirc((AddTool)tool);
				}
			}
		}
		for (Library sublib: lib.getLibraries()) {
			checkLibrary(sublib);
		}
	}


	@Override
	public void onClose() {

	}

}

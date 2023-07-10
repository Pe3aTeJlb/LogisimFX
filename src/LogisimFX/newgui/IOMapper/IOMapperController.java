package LogisimFX.newgui.IOMapper;

import LogisimFX.FileSelector;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitMapInfo;
import LogisimFX.file.XmlIterator;
import LogisimFX.fpga.FPGAToolchainOrchestrator;
import LogisimFX.fpga.LC;
import LogisimFX.fpga.data.*;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Project;
import LogisimFX.util.StringUtil;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;

public class IOMapperController extends AbstractController {

	private Stage stage;


	@FXML
	private Label UnmappedCompsLbl;


	@FXML
	private TreeView<MapInfo> UnmappedCompsTrvw;

	private TreeItem<MapInfo> UnmappedCompsTrvwRoot;


	@FXML
	private Label MappedCompsLbl;


	@FXML
	private TreeView<MapInfo> MappedCompsTrvw;

	private TreeItem<MapInfo> MappedCompsTrvwRoot;


	@FXML
	private Label ConstantsLbl;

	@FXML
	private Button ConstantZeroBtn;

	@FXML
	private Button ConstantOneBtn;

	@FXML
	private Button ConstantConstantBtn;

	@FXML
	private Button ConstantUnconnectedBtn;


	@FXML
	private Label ActionsLbl;

	@FXML
	private Button ActionReleaseCompBtn;

	@FXML
	private Button ActionReleaseCompsBtn;

	@FXML
	private Button ActionLoadMapBtn;

	@FXML
	private Button ActionSaveMapBtn;

	@FXML
	private Button ActionDoneBtn;


	@FXML
	private Pane ImagePane;

	@FXML
	private ImageView BoardImgVw;


	private Project proj;
	private FPGAToolchainOrchestrator orchestrator;
	private MappableResourcesContainer mapInfo;

	@FXML
	public void initialize() {
	}

	@Override
	public void postInitialization(Stage s, Project project) {

		stage = s;
		s.setWidth(750);
		s.setHeight(740);
		s.setResizable(false);
		s.titleProperty().bind(LC.createStringBinding("BoardMapTitle"));

		this.proj = project;
		this.orchestrator = project.getFpgaToolchainOrchestrator();
		mapInfo = orchestrator.getMappableResourcesContainer();

		UnmappedCompsLbl.textProperty().bind(LC.createStringBinding("BoardMapUnmapped"));
		UnmappedCompsTrvwRoot = new TreeItem<>();
		UnmappedCompsTrvw.setRoot(UnmappedCompsTrvwRoot);
		UnmappedCompsTrvw.getRoot().setExpanded(true);
		UnmappedCompsTrvw.setShowRoot(false);
		UnmappedCompsTrvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		UnmappedCompsTrvw.getSelectionModel().selectedItemProperty().addListener((observableValue, mapInfo, t1) -> {
			redrawRects(t1);
			recalculateConstantBtns(t1);
		});
		UnmappedCompsTrvw.setCellFactory(mapInfoTreeView -> new TreeCell<>() {

			@Override
			public void updateItem(MapInfo info, boolean empty) {
				super.updateItem(info, empty);
				if (empty || info == null) {
					setText(null);
				} else {
					setText(info.getMap().getDisplayString(info.getPin()));
				}
			}

		});

		MappedCompsLbl.textProperty().bind(LC.createStringBinding("BoardMapMapped"));
		MappedCompsTrvwRoot = new TreeItem<>();
		MappedCompsTrvw.setRoot(MappedCompsTrvwRoot);
		MappedCompsTrvw.getRoot().setExpanded(true);
		MappedCompsTrvw.setShowRoot(false);
		MappedCompsTrvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		MappedCompsTrvw.getSelectionModel().selectedItemProperty().addListener((observableValue, selectionMode, t1) -> {
			redrawRects(t1);
			recalculateConstantBtns(t1);
		});
		MappedCompsTrvw.setCellFactory(mapInfoTreeView -> new TreeCell<>() {

			@Override
			public void updateItem(MapInfo info, boolean empty) {
				super.updateItem(info, empty);
				if (empty || info == null) {
					setText(null);
				} else {
					setText(info.getMap().getDisplayString(info.getPin()));
				}
			}

		});

		ConstantsLbl.textProperty().bind(LC.createStringBinding("BoardMapConstantTitle"));
		ConstantZeroBtn.textProperty().bind(LC.createStringBinding("BoardMapConstantZero"));
		ConstantZeroBtn.setOnAction(event -> {
			UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().map.tryConstantMap(
					UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().getPin(),
					0L
			);
			updateLists();
		});
		ConstantOneBtn.textProperty().bind(LC.createStringBinding("BoardMapConstantOne"));
		ConstantOneBtn.setOnAction(event -> {
			UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().map.tryConstantMap(
					UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().getPin(),
					-1L
			);
			updateLists();
		});
		ConstantConstantBtn.textProperty().bind(LC.createStringBinding("BoardMapConst"));
		ConstantConstantBtn.setOnAction(event -> {
			getConstant(
					UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().getPin(),
					UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().map
			);
			updateLists();
		});
		ConstantUnconnectedBtn.textProperty().bind(LC.createStringBinding("BoardMapOpen"));
		ConstantUnconnectedBtn.setOnAction(event -> {
			UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().map.tryOpenMap(
					UnmappedCompsTrvw.getSelectionModel().getSelectedItem().getValue().getPin()
			);
			updateLists();
		});


		ActionsLbl.textProperty().bind(LC.createStringBinding("BoardMapActions"));
		ActionReleaseCompBtn.textProperty().bind(LC.createStringBinding("BoardMapRelease"));
		ActionReleaseCompBtn.disableProperty().bind(MappedCompsTrvw.getSelectionModel().selectedItemProperty().isNull());
		ActionReleaseCompBtn.setOnAction(event -> {
			if (MappedCompsTrvw.getSelectionModel().getSelectedIndex() >= 0) {
				final var map = MappedCompsTrvw.getSelectionModel().getSelectedItem().getValue();
				if (map.getPin() < 0) {
					map.getMap().unmap();
				} else {
					map.getMap().unmap(map.getPin());
				}
				updateLists();
			}
		});
		ActionReleaseCompsBtn.textProperty().bind(LC.createStringBinding("BoardMapRelAll"));
		ActionReleaseCompsBtn.disableProperty().bind(Bindings.isEmpty(MappedCompsTrvw.getRoot().getChildren()));
		ActionReleaseCompsBtn.setOnAction(event -> {
			mapInfo.unMapAll();
			updateLists();
		});
		ActionLoadMapBtn.textProperty().bind(LC.createStringBinding("BoardMapLoad"));
		ActionLoadMapBtn.setOnAction(event -> {
			loadMap();
			updateLists();
			redrawRects(null);
		});
		ActionSaveMapBtn.textProperty().bind(LC.createStringBinding("BoardMapSave"));
		ActionSaveMapBtn.setOnAction(event -> {
			saveMap();
		});
		ActionDoneBtn.textProperty().bind(LC.createStringBinding("FpgaBoardDone"));
		ActionDoneBtn.setOnAction(event -> {
			if (mapDesignCheckIOs()) {
				stage.close();
			}
		});


		//BoardImgVw
		BoardImgVw.setImage(mapInfo.getBoardInformation().getImage());

		for (FpgaIoInformationContainer container : mapInfo.getIOComponents()) {
			ImagePane.getChildren().add(container.getRectangle());
			container.getRectangle().setVisible(false);
		}

		ImagePane.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof BoardRectangle) {
				if (((BoardRectangle) event.getTarget()).onMouseClicked()) {
					redrawRects(null);
					updateLists();
				}
			}
		});

		updateLists();
		recalculateConstantBtns(null);

	}


	private void updateLists() {

		UnmappedCompsTrvw.getRoot().getChildren().clear();
		for (var key : mapInfo.getMappableResources().keySet()) {
			MapComponent map = mapInfo.getMappableResources().get(key);
			if (map.isNotMapped()) {
				TreeItem<MapInfo> subroot = new TreeItem<>(new MapInfo(map.getNrOfPins() == 1 ? 0 : -1, map));
				if (map.getNrOfPins() > 1) {
					for (var i = map.getNrOfPins() - 1; i >= 0; i--) {
						subroot.getChildren().add(new TreeItem<>(new MapInfo(i, map)));
					}
				}
				UnmappedCompsTrvwRoot.getChildren().add(subroot);
			} else {
				if (map.getNrOfPins() > 1) {
					for (var i = map.getNrOfPins() - 1; i >= 0; i--) {
						if (!map.isMapped(i)) {
							UnmappedCompsTrvwRoot.getChildren().add(new TreeItem<>(new MapInfo(i, map)));
						}
					}
				}
			}
		}

		MappedCompsTrvw.getRoot().getChildren().clear();
		for (var key : mapInfo.getMappableResources().keySet()) {
			MapComponent map = mapInfo.getMappableResources().get(key);
			if (map.isCompleteMap(false)) {
				TreeItem<MapInfo> subroot = new TreeItem<>(new MapInfo(map.getNrOfPins() == 1 ? 0 : -1, map));
				if (map.getNrOfPins() > 1) {
					for (var i = map.getNrOfPins() - 1; i >= 0; i--) {
						subroot.getChildren().add(new TreeItem<>(new MapInfo(i, map)));
					}
				}
				MappedCompsTrvwRoot.getChildren().add(subroot);
			} else {
				for (var i = map.getNrOfPins() - 1; i >= 0; i--) {
					if (map.isMapped(i)) {
						MappedCompsTrvwRoot.getChildren().add(new TreeItem<>(new MapInfo(i, map)));
					}
				}
			}
		}

	}

	//Constant Btns

	private boolean getConstant(int pin, MapComponent map) {
		var v = 0L;
		boolean correct;
		do {
			correct = true;

			final var value = DialogManager.createInputDialog(LC.get("FpgaMapSpecConst"), "");
			if (value == null) return false;
			if (value.startsWith("0x")) {
				try {
					v = Long.parseLong(value.substring(2), 16);
				} catch (NumberFormatException e1) {
					correct = false;
				}
			} else {
				try {
					v = Long.parseLong(value);
				} catch (NumberFormatException e) {
					correct = false;
				}
			}
			if (!correct) DialogManager.createErrorDialog("", LC.get("FpgaMapSpecErr"));
		} while (!correct);
		return map.tryConstantMap(pin, v);
	}

	private void recalculateConstantBtns(TreeItem<MapInfo> comp) {

		if (comp == null) {
			ConstantZeroBtn.setDisable(true);
			ConstantOneBtn.setDisable(true);
			ConstantConstantBtn.setDisable(true);
			ConstantUnconnectedBtn.setDisable(true);
			return;
		}

		final var map = comp.getValue().getMap();
		int connect = comp.getValue().getPin();
		if (connect < 0) {
			if (map.hasInputs()) {
				ConstantZeroBtn.setDisable(false);
				ConstantOneBtn.setDisable(false);
				ConstantConstantBtn.setDisable(map.nrInputs() < 1);
				ConstantUnconnectedBtn.setDisable(true);
			}
			if (map.hasOutputs() || map.hasIos()) {
				ConstantZeroBtn.setDisable(true);
				ConstantOneBtn.setDisable(true);
				ConstantConstantBtn.setDisable(true);
				ConstantUnconnectedBtn.setDisable(false);
			}
		} else {
			if (map.isInput(connect)) {
				ConstantZeroBtn.setDisable(false);
				ConstantOneBtn.setDisable(false);
				ConstantConstantBtn.setDisable(true);
				ConstantUnconnectedBtn.setDisable(true);
			}
			if (map.isOutput(connect) || map.isIo(connect)) {
				ConstantZeroBtn.setDisable(true);
				ConstantOneBtn.setDisable(true);
				ConstantConstantBtn.setDisable(true);
				ConstantUnconnectedBtn.setDisable(false);
			}
		}
	}

	//Rect methods

	private void redrawRects(TreeItem<MapInfo> info) {

		for (FpgaIoInformationContainer container : mapInfo.getIOComponents()) {
			if (info != null && container.setSelectable(info.getValue())) {
				container.getRectangle().setVisible(true);
				container.getRectangle().checkPin(info.getValue());
			} else {
				container.getRectangle().setVisible(false);
			}
		}

	}

	//Map methods

	private void loadMap() {

		FileSelector fileSelector = new FileSelector(stage);
		File file = fileSelector.openMapFile();
		if (file == null) return;

		try {

			InputStream in = new FileInputStream(file);
			Document doc = loadXmlFrom(in);

			HashMap<String, CircuitMapInfo> map = new HashMap<>();
			Element boardmap = doc.getDocumentElement();

			if (!boardmap.getAttribute("boardname").equals(orchestrator.getSelectedBoard())) {
				DialogManager.createErrorDialog("Error", "Unmatched board IO map file");
				return;
			}

			for (Element cmap : XmlIterator.forChildElements(boardmap, "mc")) {
				int x, y, w, h;
				String key = cmap.getAttribute("key");
				if (StringUtil.isNullOrEmpty(key)) continue;
				if (cmap.hasAttribute("open")) {
					map.put(key, new CircuitMapInfo());
				} else if (cmap.hasAttribute("vconst")) {
					long v;
					try {
						v = Long.parseLong(cmap.getAttribute("vconst"));
					} catch (NumberFormatException e) {
						continue;
					}
					map.put(key, new CircuitMapInfo(v));
				} else if (cmap.hasAttribute("valx")
						&& cmap.hasAttribute("valy")
						&& cmap.hasAttribute("valw")
						&& cmap.hasAttribute("valh")) {
					/* Backward compatibility: */
					try {
						x = Integer.parseUnsignedInt(cmap.getAttribute("valx"));
						y = Integer.parseUnsignedInt(cmap.getAttribute("valy"));
						w = Integer.parseUnsignedInt(cmap.getAttribute("valw"));
						h = Integer.parseUnsignedInt(cmap.getAttribute("valh"));
					} catch (NumberFormatException e) {
						continue;
					}
					final var br = new BoardRectangle(x, y, w, h);
					map.put(key, new CircuitMapInfo(br));
				} else {
					CircuitMapInfo cmapi = MapComponent.getMapInfo(cmap);
					if (cmapi != null)
						map.put(key, cmapi);
				}
			}
			if (!map.isEmpty()) {
				proj.getLogisimFile().getMainCircuit().addLoadedMap(orchestrator.getSelectedBoard(), map);
				proj.getLogisimFile().getMainCircuit().setBoardMap(orchestrator.getSelectedBoard(), mapInfo);
			}


		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

	}

	private Document loadXmlFrom(InputStream is) throws SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		}
		return builder.parse(is);
	}

	private void saveMap() {

		//Choose output directory
		FileSelector fileSelector = new FileSelector(stage);
		File file = fileSelector.saveMapFile();
		if (file == null) return;

		Circuit circuit = proj.getLogisimFile().getMainCircuit();
		String boardName = orchestrator.getSelectedBoard();

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element ret = doc.createElement("boardmap");
			ret.setAttribute("boardname", boardName);
			for (String key : mapInfo.getCircuitMap().keySet()) {
				Element map = doc.createElement("mc");
				CircuitMapInfo mapInfo = circuit.getMapInfo(boardName).get(key);
				MapComponent nmap = mapInfo.getMap();
				if (nmap != null) {
					nmap.getMapElement(map);
				} else {
					map.setAttribute("key", key);
					MapComponent.getComplexMap(map, mapInfo);
				}
				ret.appendChild(map);
			}
			doc.appendChild(ret);

			exportToFile(doc, file);

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

	}

	private void exportToFile(Document doc, File out) throws TransformerException {

		if (!out.exists()) {
			try {
				out.getParentFile().mkdirs();
				out.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		TransformerFactory tfFactory = TransformerFactory.newInstance();
		try {
			tfFactory.setAttribute("indent-number", Integer.valueOf(2));
		} catch (IllegalArgumentException e) {
		}
		Transformer tf = tfFactory.newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"2");
		} catch (IllegalArgumentException e) {
		}

		Source src = new DOMSource(doc);
		Result dest = new StreamResult(out);
		tf.transform(src, dest);

	}

	private boolean mapDesignCheckIOs() {
		if (mapInfo.isCompletelyMapped()) return true;
		return DialogManager.createConfirmDialog(LC.get("FpgaIncompleteMap"), LC.get("FpgaNotCompleteMap"));
	}

	@Override
	public void onClose() {

	}


	public static class MapInfo {
		private final int pinNr;
		private final MapComponent map;

		public MapInfo(int pin, MapComponent map) {
			pinNr = pin;
			this.map = map;
		}

		public int getPin() {
			return pinNr;
		}

		public MapComponent getMap() {
			return map;
		}

		@Override
		public String toString() {
			return map.getDisplayString(pinNr);
		}
	}

}

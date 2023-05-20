package LogisimFX.newgui;

import LogisimFX.IconsManager;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.instance.StdAttr;
import LogisimFX.proj.Project;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CircSearchDialog extends Dialog<Object> {

	private final GridPane grid;
	private final Project proj;
	private final TextField findTxtFld;
	private final Label findResultLbl;
	private final Button prevCompBtn, nextCompBtn;
	private final SimpleStringProperty currFindIndex, totalFindIndex;
	private final ListView<Pair<Circuit, Component>> listView;
	private ObservableList<Pair<Circuit, Component>> compList = FXCollections.observableArrayList();
	private AtomicInteger currCompIndex = new AtomicInteger(0);

	public CircSearchDialog(Project project) {

		this.proj = project;

		final DialogPane dialogPane = getDialogPane();

		// -- grid
		this.grid = new GridPane();
		this.grid.setHgap(10);
		this.grid.setVgap(10);
		this.grid.setMaxWidth(Double.MAX_VALUE);
		this.grid.setAlignment(Pos.CENTER_LEFT);

		// -- label
		//label = DialogPane.createContentLabel(dialogPane.getContentText());
		// label.setPrefWidth(Region.USE_COMPUTED_SIZE);
		//label.textProperty().bind(dialogPane.contentTextProperty());

		dialogPane.contentTextProperty().addListener(o -> updateGrid());

		setTitle(ControlResources.getString("Dialog.confirm.title"));
		dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
		dialogPane.getStyleClass().add("choice-dialog");
		//dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);

		final double MIN_WIDTH = 150;

		currFindIndex = new SimpleStringProperty("0");
		totalFindIndex = new SimpleStringProperty("0");

		findTxtFld = new TextField();

		findTxtFld.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				find();
				event.consume();
			}
		});

		findResultLbl = new Label();
		findResultLbl.textProperty().bind(
				Bindings.concat(
						currFindIndex,
						"/",
						totalFindIndex
				)
		);

		prevCompBtn = new Button();
		prevCompBtn.setGraphic(IconsManager.getImageView("arrowleft.gif"));
		prevCompBtn.setOnAction(event -> prevComp());

		nextCompBtn = new Button();
		nextCompBtn.setGraphic(IconsManager.getImageView("arrowright.gif"));
		nextCompBtn.setOnAction(event -> nextComp());

		listView = new ListView<>();
		listView.setItems(compList);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		listView.setCellFactory(cell -> {

			ListCell<Pair<Circuit, Component>> listCell = new ListCell<>() {

				@Override
				public void updateItem(Pair<Circuit, Component> pair, boolean empty) {
					super.updateItem(pair, empty);
					if (empty || pair == null) {
						setText(null);
					} else {
						setText(pair.getKey().getName() + " - "
								+ (pair.getValue().getAttributeSet().getValue(StdAttr.LABEL)) + pair.getValue().getLocation().toString());
					}
				}

			};

			listCell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {

				if (!listCell.isEmpty()) {

					if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
							&& !event.isConsumed()) {

						currCompIndex.set(listView.getSelectionModel().getSelectedIndex());
						gotoNextIndexComp(currCompIndex);
						currFindIndex.set(String.valueOf(currCompIndex.get() + 1));

					}

				}

			});

			return listCell;

		});



		listView.setMinSize(MIN_WIDTH, MIN_WIDTH);
		listView.setMaxSize(Double.MAX_VALUE, 250);
		GridPane.setHgrow(listView, Priority.ALWAYS);
		GridPane.setFillWidth(listView, true);

		updateGrid();

		setResultConverter((dialogButton) -> {
			proj.getFrameController().getLayoutCanvas().setHighlightedComponent(null);
			ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			return data == ButtonBar.ButtonData.OK_DONE ? getSelectedItem() : null;
		});

	}


	private void find() {

		if (findTxtFld.getText().isEmpty()) {
			return;
		}

		compList.clear();


		for (AddTool tool : proj.getLogisimFile().getTools()) {
			calculateComps(((SubcircuitFactory) tool.getFactory()).getSubcircuit());
		}

		List<Library> builtinLibraries = proj.getLogisimFile().getLoader().getBuiltin().getLibraries();
		for (Library lib : proj.getLogisimFile().getLibraries()) {
			if (!builtinLibraries.contains(lib)) {
				calculateLibComps(lib);
			}
		}


		if (compList.size() == 0) {
			return;
		}

		listView.getSelectionModel().select(0);
		proj.getFrameController().addCircLayoutEditor(compList.get(0).getKey());
		proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(0).getValue());
		totalFindIndex.set(String.valueOf(compList.size()));
		currFindIndex.set(String.valueOf(currCompIndex.get() + 1));

	}

	private void calculateComps(Circuit circ) {

		for (Component comp : circ.getNonWires()) {
			if (comp.getAttributeSet().containsAttribute(StdAttr.LABEL)) {
				if (comp.getAttributeSet().getValue(StdAttr.LABEL).contains(findTxtFld.getText())) {
					compList.add(new Pair<>(circ, comp));
				}
			}
		}

	}

	private void calculateLibComps(Library lib) {

		for (Tool tool : lib.getTools()) {
			if (tool instanceof AddTool) {
				ComponentFactory fact = ((AddTool) tool).getFactory(false);
				if (fact instanceof SubcircuitFactory) {
					calculateComps(((SubcircuitFactory) fact).getSubcircuit());
				}
			}
		}

		for (Library sublib : lib.getLibraries()) {
			calculateLibComps(sublib);
		}

	}


	private void gotoNextIndexComp(AtomicInteger currCompIndex) {
		int index = currCompIndex.get();
		proj.getFrameController().getLayoutCanvas().setHighlightedComponent(null);
		proj.getFrameController().addCircLayoutEditor(compList.get(index).getKey());
		proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(index).getValue());
	}

	private void nextComp() {
		if (compList.size() == 0) {
			return;
		}
		proj.getFrameController().getLayoutCanvas().setHighlightedComponent(null);
		gotoNextComp(compList, currCompIndex);
		currFindIndex.set(String.valueOf(currCompIndex.get() + 1));
	}

	private void gotoNextComp(ObservableList<Pair<Circuit, Component>> coordinateList, AtomicInteger currCompIndex) {
		if (currCompIndex.get() >= (coordinateList.size() - 1) && coordinateList.size() != 0) return;
		currCompIndex.incrementAndGet();
		int index = currCompIndex.get();
		proj.getFrameController().addCircLayoutEditor(compList.get(index).getKey());
		proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(index).getValue());
		listView.getSelectionModel().select(index);
	}


	private void prevComp() {
		if (compList.size() == 0) {
			return;
		}
		proj.getFrameController().getLayoutCanvas().setHighlightedComponent(null);
		gotoPrevComp(compList, currCompIndex);
		currFindIndex.set(String.valueOf(currCompIndex.get() + 1));
	}

	private void gotoPrevComp(ObservableList<Pair<Circuit, Component>> coordinateList, AtomicInteger currCompIndex) {
		if (currCompIndex.get() <= 0 && coordinateList.size() != 0) return;
		currCompIndex.decrementAndGet();
		int index = currCompIndex.get();
		proj.getFrameController().addCircLayoutEditor(compList.get(index).getKey());
		proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(index).getValue());
		listView.getSelectionModel().select(index);
	}


	public final ReadOnlyObjectProperty<Pair<Circuit, Component>> selectedItemProperty() {
		return listView.getSelectionModel().selectedItemProperty();
	}

	public final ObservableList<Pair<Circuit, Component>> getSelectedItems() {
		return listView.getSelectionModel().getSelectedItems();
	}

	public final Object getSelectedItem() {
		return listView.getSelectionModel().getSelectedItem();
	}

	public final void setSelectedItem(Pair<Circuit, Component> item) {
		listView.getSelectionModel().select(item);
	}

	public final ObservableList<Pair<Circuit, Component>> getItems() {
		return listView.getItems();
	}


	private void updateGrid() {

		grid.getChildren().clear();

		//grid.add(label, 0, 0);
		grid.add(findTxtFld, 0, 0);
		grid.add(findResultLbl, 1, 0);
		grid.add(prevCompBtn, 2, 0);
		grid.add(nextCompBtn, 3, 0);
		grid.add(listView, 0, 1);
		getDialogPane().setContent(grid);

		Platform.runLater(() -> findTxtFld.requestFocus());

	}



}

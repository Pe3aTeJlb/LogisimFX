/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.CircuitStatisticFrame;

import LogisimFX.circuit.Circuit;
import LogisimFX.file.FileStatistics;
import LogisimFX.newgui.AbstractController;
import LogisimFX.proj.Project;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class CircuitStatisticController extends AbstractController {

    private Stage stage;

    private Project proj;

    @FXML
    private AnchorPane Anchor;

    private TableView<FileStatistics.Count> statisticsTable;

    @FXML
    public void initialize(){

        statisticsTable = new TableView<>();

        statisticsTable.setMaxWidth(Region.USE_COMPUTED_SIZE);
        statisticsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        statisticsTable.setEditable(false);


        TableColumn<FileStatistics.Count, String> componentName = new TableColumn<>();
        componentName.textProperty().bind(LC.createStringBinding("statsComponentColumn"));
        componentName.setCellValueFactory(data -> data.getValue().getComponentName());

        TableColumn<FileStatistics.Count, String> libName = new TableColumn<>();
        libName.textProperty().bind(LC.createStringBinding("statsLibraryColumn"));
        libName.setCellValueFactory(data -> data.getValue().getLibName());

        TableColumn<FileStatistics.Count, Integer> simpleComponentCount = new TableColumn<>();
        simpleComponentCount.textProperty().bind(LC.createStringBinding("statsSimpleCountColumn"));
        simpleComponentCount.setCellValueFactory(new PropertyValueFactory<>("SimpleCount"));

        TableColumn<FileStatistics.Count, Integer> uniqComponentCount = new TableColumn<>();
        uniqComponentCount.textProperty().bind(LC.createStringBinding("statsUniqueCountColumn"));
        uniqComponentCount.setCellValueFactory(new PropertyValueFactory<>("UniqueCount"));

        TableColumn<FileStatistics.Count, Integer> recursiveComponentCount = new TableColumn<>();
        recursiveComponentCount.textProperty().bind(LC.createStringBinding("statsRecursiveCountColumn"));
        recursiveComponentCount.setCellValueFactory(new PropertyValueFactory<>("RecursiveCount"));

        statisticsTable.getColumns().addAll(
                componentName,
                libName,
                simpleComponentCount,
                uniqComponentCount,
                recursiveComponentCount
        );

        Anchor.getChildren().addAll(statisticsTable);

        AnchorPane.setLeftAnchor(statisticsTable,0.0);
        AnchorPane.setTopAnchor(statisticsTable,0.0);
        AnchorPane.setRightAnchor(statisticsTable,0.0);
        AnchorPane.setBottomAnchor(statisticsTable,0.0);

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        proj = project;
        stage.setTitle("LogisimFx: circuit statistics");

    }

    public void describeCircuit(Circuit circuit){

        stage.titleProperty().bind(LC.createComplexStringBinding("statsDialogTitle",circuit.getName()));

        FileStatistics fileStats = FileStatistics.compute(proj.getLogisimFile(), circuit);

        statisticsTable.getItems().addAll(fileStats.getCounts());
        statisticsTable.getItems().addAll(fileStats.getTotalWithoutSubcircuits(),
                fileStats.getTotalWithSubcircuits());

    }

    @Override
    public void onClose() {
        statisticsTable.getItems().clear();
    }

}

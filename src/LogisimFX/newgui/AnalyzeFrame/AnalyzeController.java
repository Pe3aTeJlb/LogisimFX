/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.AnalyzeFrame;

import LogisimFX.IconsManager;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.ListViewDialog;
import LogisimFX.proj.Project;

import LogisimFX.tools.Library;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AnalyzeController extends AbstractController {

    //Inputs

    @FXML
    private Tab InputsTab;


    @FXML
    private ListView<String> InputsLstvw;

    private MultipleSelectionModel<String> inputsSelectionModel;

    @FXML
    private Button InputsDeleteBtn;

    @FXML
    private Button InputsUpBtn;

    @FXML
    private Button InputsDownBtn;


    @FXML
    private TextField InputsTxtFld;

    @FXML
    private Button InputsAddBtn;

    @FXML
    private Button InputsRenameBtn;


    //Outputs

    @FXML
    private Tab OutputsTab;


    @FXML
    private ListView<String> OutputsLstvw;

    private MultipleSelectionModel<String> outputsSelectionModel;

    @FXML
    private Button OutputsDeleteBtn;

    @FXML
    private Button OutputsUpBtn;

    @FXML
    private Button OutputsDownBtn;


    @FXML
    private TextField OutputsTxtFld;

    @FXML
    private Button OutputsAddBtn;

    @FXML
    private Button OutputsRenameBtn;


    //Truth table

    @FXML
    private Tab TruthTableTab;

    @FXML
    private TableView<TruthTableDataModel> TruthTblvw;

    private TableView.TableViewSelectionModel<TruthTableDataModel> truthTableSelectionModel;


    //Expression

    @FXML
    private Tab LogicExpressionTab;


    @FXML
    private Label ExpressionOutputLbl;

    @FXML
    private ComboBox<String> ExpressionOutputCmbbx;

    @FXML
    private Label ExpressionLineLbl;


    @FXML
    private TextArea ExpressionTxtarea;

    @FXML
    private Button ExpressionAddBtn;

    @FXML
    private Button ExpressionRevertBtn;

    @FXML
    private Button ExpressionClearBtn;

    @FXML
    private Label ExpressionErrorLbl;


    //Minimisation

    @FXML
    private Tab MinimisationTab;


    @FXML
    private Button MinimisationSetSelectedBtn;

    @FXML
    private Label MinimisationExpressionLbl;

    @FXML
    private Label MinimisationOutputLbl;

    @FXML
    private ComboBox<Integer> MinimisationFormatCmbbx;

    @FXML
    private ComboBox<String> MinimisationOutputCmbbx;

    @FXML
    private Label MinimisationFormatLbl;

    @FXML
    private AnchorPane MinimizationKarnaughMapRoot;

    @FXML
    private TableView<String> MinimazationKarnaughMapHeadersTrTblvw;

    @FXML
    private TableView<KarnaughMapDataModel> MinimazationKarnaughMapVarsTrTblvw;


    @FXML
    private Button BuildCircuitBtn;


    private static final Color[] IMP_COLORS = new Color[] {
            new Color(1, 0, 0, 0.5),
            new Color(0, 0.59, 0, 0.5),
            new Color(0, 0, 1, 0.5),
            new Color(1, 0, 1, 0.5),
    };

    private static final int IMP_INSET = 5;
    private static final int IMP_RADIUS = 5;

    private static final int CELL_WIDTH = 45;
    private static final int CELL_HEIGHT = 45;

    private static final int INIT_OFFSET_X = 45;
    private static final int INIT_OFFSET_Y = 45;

    private int rows;
    private int cols;

    private final ArrayList<Rectangle> implicantsRect = new ArrayList<>();

    private static final int MAX_VARS = 4;

    private static final int[] ROW_VARS = { 0, 0, 1, 1, 2 };
    private static final int[] COL_VARS = { 0, 1, 1, 2, 2 };


    private AnalyzerModel model;

    private ObservableList<Entry> copyBuffer;



    @FXML
    public void initialize(){}

    @Override
    public void postInitialization(Stage s, Project proj) {

        s.titleProperty().bind(LC.createStringBinding("analyzerWindowTitle"));

        model = new AnalyzerModel();

        initializeInputsTab();
        initializeOutputsTab();
        initializeTruthTableTab();
        initializeExpressionTab();
        initializeMinimizationTab();

        BuildCircuitBtn.textProperty().bind(LC.createStringBinding("buildCircuitButton"));
        BuildCircuitBtn.setOnAction(event ->{

            BuildDialog<AnalyzerModel> dialog = new BuildDialog<>(model);

            dialog.titleProperty().bind(LC.createStringBinding("buildDialogTitle"));
            dialog.headerTextProperty().bind(LC.createStringBinding("buildDialogTitle"));

            ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

            dialog.showAndWait();

        });

    }

    public AnalyzerModel getModel(){
        return model;
    }



    private void initializeInputsTab(){

        InputsTab.textProperty().bind(LC.createStringBinding("inputsTab"));

        inputsSelectionModel = InputsLstvw.getSelectionModel();
        inputsSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        inputsSelectionModel.selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(inputsSelectionModel.getSelectedItem() != null) {
                        int localIndex = inputsSelectionModel.getSelectedIndex();
                        InputsUpBtn.setDisable(localIndex == 0);
                        InputsDownBtn.setDisable(
                                localIndex == InputsLstvw.getItems().size()-1);
                        InputsDeleteBtn.setDisable(InputsLstvw.getItems().isEmpty());
                    }else{
                        InputsUpBtn.setDisable(true);
                        InputsDownBtn.setDisable(true);
                        InputsDeleteBtn.setDisable(true);
                    }
                }
        );
        InputsLstvw.setItems(model.getInputs());
        inputsSelectionModel.select(0);


        InputsDeleteBtn.textProperty().bind(LC.createStringBinding("variableRemoveButton"));
        InputsDeleteBtn.setDisable(true);
        InputsDeleteBtn.setOnAction(event ->{
            InputsLstvw.getItems().remove(inputsSelectionModel.getSelectedIndex());
        });

        InputsUpBtn.textProperty().bind(LC.createStringBinding("variableMoveUpButton"));
        InputsUpBtn.setDisable(true);
        InputsUpBtn.setOnAction(event ->{
            int i = inputsSelectionModel.getSelectedIndex();
            String buff = InputsLstvw.getItems().get(i - 1);
            InputsLstvw.getItems().set(i - 1, InputsLstvw.getItems().get(i));
            InputsLstvw.getItems().set(i, buff);
        });

        InputsDownBtn.textProperty().bind(LC.createStringBinding("variableMoveDownButton"));
        InputsDownBtn.setDisable(true);
        InputsDownBtn.setOnAction(event ->{
            int i = inputsSelectionModel.getSelectedIndex();
            String buff = InputsLstvw.getItems().get(i + 1);
            InputsLstvw.getItems().set(i + 1, InputsLstvw.getItems().get(i));
            InputsLstvw.getItems().set(i, buff);
        });


        final Pattern pattern = Pattern.compile("(^[A-za-zА-Яа-я]{1}[A-Za-zА-Яа-я0-9]{0,19}$)?");
        TextFormatter<?> formatter = new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change; // allow this change to happen
            } else {
                return null; // prevent change
            }
        });

        InputsTxtFld.setTextFormatter(formatter);

        InputsTxtFld.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(InputsTxtFld.getText() != null) {
                        InputsAddBtn.setDisable(false);
                        if(!inputsSelectionModel.getSelectedItems().isEmpty())InputsRenameBtn.setDisable(false);
                    }else{
                        InputsAddBtn.setDisable(true);
                        InputsRenameBtn.setDisable(true);
                    }
                }
        );

        InputsTxtFld.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.ENTER){
                addInputItem();
            }

        });

        InputsAddBtn.textProperty().bind(LC.createStringBinding("variableAddButton"));
        InputsAddBtn.setDisable(true);
        InputsAddBtn.setOnAction(event ->{
            addInputItem();
        });

        InputsRenameBtn.textProperty().bind(LC.createStringBinding("variableRenameButton"));
        InputsRenameBtn.setDisable(true);
        InputsRenameBtn.setOnAction(event ->{
            if(!InputsTxtFld.getText().isEmpty()) {
                InputsLstvw.getItems().set(inputsSelectionModel.getSelectedIndex(), InputsTxtFld.getText());
                inputsSelectionModel.select(inputsSelectionModel.getSelectedIndex());
                InputsTxtFld.clear();
            }
        });

    }

    private void addInputItem(){

        if(!InputsTxtFld.getText().isEmpty() && InputsLstvw.getItems().size() < AnalyzerModel.MAX_INPUTS) {
            InputsLstvw.getItems().add(InputsTxtFld.getText());
            inputsSelectionModel.select(InputsLstvw.getItems().size()-1);
            InputsTxtFld.clear();
        }

    }



    private void initializeOutputsTab(){

        OutputsTab.textProperty().bind(LC.createStringBinding("outputsTab"));

        outputsSelectionModel = OutputsLstvw.getSelectionModel();
        outputsSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        outputsSelectionModel.selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(outputsSelectionModel.getSelectedItem() != null) {
                        int localIndex = outputsSelectionModel.getSelectedIndex();
                        OutputsUpBtn.setDisable(localIndex == 0);
                        OutputsDownBtn.setDisable(
                                localIndex == OutputsLstvw.getItems().size()-1);
                        OutputsDeleteBtn.setDisable(OutputsLstvw.getItems().isEmpty());
                    }else{
                        OutputsUpBtn.setDisable(true);
                        OutputsDownBtn.setDisable(true);
                        OutputsDeleteBtn.setDisable(true);
                    }
                }
        );
        OutputsLstvw.setItems(model.getOutputs());
        outputsSelectionModel.select(0);


        OutputsDeleteBtn.textProperty().bind(LC.createStringBinding("variableRemoveButton"));
        OutputsDeleteBtn.setDisable(true);
        OutputsDeleteBtn.setOnAction(event ->{
            OutputsLstvw.getItems().remove(outputsSelectionModel.getSelectedIndex());
        });

        OutputsUpBtn.textProperty().bind(LC.createStringBinding("variableMoveUpButton"));
        OutputsUpBtn.setDisable(true);
        OutputsUpBtn.setOnAction(event ->{
            int i = outputsSelectionModel.getSelectedIndex();
            String buff = OutputsLstvw.getItems().get(i - 1);
            OutputsLstvw.getItems().set(i - 1, OutputsLstvw.getItems().get(i));
            OutputsLstvw.getItems().set(i, buff);
        });

        OutputsDownBtn.textProperty().bind(LC.createStringBinding("variableMoveDownButton"));
        OutputsDownBtn.setDisable(true);
        OutputsDownBtn.setOnAction(event ->{
            int i = outputsSelectionModel.getSelectedIndex();
            String buff = OutputsLstvw.getItems().get(i + 1);
            OutputsLstvw.getItems().set(i + 1, OutputsLstvw.getItems().get(i));
            OutputsLstvw.getItems().set(i, buff);
        });


        final Pattern pattern = Pattern.compile("(^[A-Za-zА-Яа-я]{1}[A-Za-zА-Яа-я0-9]{0,19}$)?");
        TextFormatter<?> formatter = new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change; // allow this change to happen
            } else {
                return null; // prevent change
            }
        });

        OutputsTxtFld.setTextFormatter(formatter);

        OutputsTxtFld.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(OutputsTxtFld.getText() != null) {
                        OutputsAddBtn.setDisable(false);
                        if(!outputsSelectionModel.getSelectedItems().isEmpty())OutputsRenameBtn.setDisable(false);
                    }else{
                        OutputsAddBtn.setDisable(true);
                        OutputsRenameBtn.setDisable(true);
                    }
                }
        );

        OutputsTxtFld.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.ENTER){
                addOutputItem();
            }

        });

        OutputsAddBtn.textProperty().bind(LC.createStringBinding("variableAddButton"));
        OutputsAddBtn.setDisable(true);
        OutputsAddBtn.setOnAction(event ->{
            addOutputItem();
        });

        OutputsRenameBtn.textProperty().bind(LC.createStringBinding("variableRenameButton"));
        OutputsRenameBtn.setDisable(true);
        OutputsRenameBtn.setOnAction(event ->{
            OutputsLstvw.getItems().set(outputsSelectionModel.getSelectedIndex(),OutputsTxtFld.getText());
            outputsSelectionModel.select(outputsSelectionModel.getSelectedIndex());
            OutputsTxtFld.clear();
        });


    }

    private void addOutputItem(){

        if(!OutputsTxtFld.getText().isEmpty() && OutputsLstvw.getItems().size() < AnalyzerModel.MAX_OUTPUTS) {
            OutputsLstvw.getItems().add(OutputsTxtFld.getText());
            outputsSelectionModel.select(OutputsLstvw.getItems().size()-1);
            OutputsTxtFld.clear();
        }

    }



    private void initializeTruthTableTab(){

        TruthTableTab.textProperty().bind(LC.createStringBinding("tableTab"));
        TruthTableTab.setOnSelectionChanged(event -> {
            updateTruthTable();
            TruthTblvw.requestFocus();
        });

        TruthTblvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        truthTableSelectionModel = TruthTblvw.getSelectionModel();
        truthTableSelectionModel.setCellSelectionEnabled(true);
        truthTableSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        copyBuffer = FXCollections.observableArrayList();

        TruthTblvw.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.DELETE){

                for(int i = 0; i < truthTableSelectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = truthTableSelectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() >= model.getTruthTable().getInputColumnCount()) {
                        model.getTruthTable().setOutputEntry(pos.getRow(),
                                pos.getColumn()-model.getTruthTable().getInputColumnCount(), Entry.DONT_CARE);
                    }

                }

                TruthTblvw.refresh();

            }

            if(event.getCode() == KeyCode.C && event.isControlDown()){

                copyBuffer = FXCollections.observableArrayList();

                for (TablePosition pos: truthTableSelectionModel.getSelectedCells()) {

                    TableColumn tableColumn = TruthTblvw.getColumns().get(pos.getColumn());
                    SimpleObjectProperty<Entry> cellItem = (SimpleObjectProperty<Entry>)tableColumn.getCellObservableValue(pos.getRow());
                    if(pos.getColumn() >= 0) {copyBuffer.add(cellItem.getValue());}

                }

            }

            if(event.getCode() == KeyCode.V && event.isControlDown()){

                int n = 0;

                for(int i = 0; i < truthTableSelectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = truthTableSelectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() >= model.getTruthTable().getInputColumnCount() && !copyBuffer.isEmpty()) {

                        model.getTruthTable().setOutputEntry(pos.getRow(),
                                pos.getColumn()-model.getTruthTable().getInputColumnCount(), copyBuffer.get(n));

                        n++;
                        if (n == copyBuffer.size()) n = 0;//cycle paste

                    }

                }

                TruthTblvw.refresh();

            }

            if(event.getCode() == KeyCode.X && event.isControlDown()){

                copyBuffer = FXCollections.observableArrayList();

                for(int i = 0; i < truthTableSelectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = truthTableSelectionModel.getSelectedCells().get(i);
                    TableColumn tableColumn = TruthTblvw.getColumns().get(pos.getColumn());
                    SimpleObjectProperty<Entry> cellItem = (SimpleObjectProperty<Entry>)tableColumn.getCellObservableValue(pos.getRow());

                    if(pos.getColumn() >= model.getTruthTable().getInputColumnCount()) {
                        copyBuffer.add(cellItem.getValue());
                        model.getTruthTable().setOutputEntry(pos.getRow(),
                                pos.getColumn()-model.getTruthTable().getInputColumnCount(), Entry.DONT_CARE);
                    }

                }

                TruthTblvw.refresh();

            }

            if(event.getCode() == KeyCode.DIGIT0){

                for(int i = 0; i < truthTableSelectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = truthTableSelectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() >= model.getTruthTable().getInputColumnCount()) {
                        model.getTruthTable().setOutputEntry(pos.getRow(),
                                pos.getColumn()-model.getTruthTable().getInputColumnCount(), Entry.ZERO);
                    }

                }

                TruthTblvw.refresh();

            }

            if(event.getCode() == KeyCode.DIGIT1){

                for(int i = 0; i < truthTableSelectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = truthTableSelectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() >= model.getTruthTable().getInputColumnCount()) {
                        model.getTruthTable().setOutputEntry(pos.getRow(),
                                pos.getColumn()-model.getTruthTable().getInputColumnCount(), Entry.ONE);
                    }

                }

                TruthTblvw.refresh();

            }

            if(event.getCode() == KeyCode.X){

                for(int i = 0; i < truthTableSelectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = truthTableSelectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() >= model.getTruthTable().getInputColumnCount()) {
                        model.getTruthTable().setOutputEntry(pos.getRow(),
                                pos.getColumn()-model.getTruthTable().getInputColumnCount(), Entry.DONT_CARE);
                    }

                }

                TruthTblvw.refresh();

            }

        });

        //Define focusmodel listener for custom cell edit and selection
        //Request focus abuse by startedit override
        TruthTblvw.getFocusModel().focusedCellProperty().addListener((observable, oldValue, newValue) ->
                TruthTblvw.edit(TruthTblvw.getFocusModel().getFocusedCell().getRow(),
                        TruthTblvw.getFocusModel().getFocusedCell().getTableColumn())
        );

    }

    private void updateTruthTable(){

        TruthTblvw.getItems().clear();
        TruthTblvw.getColumns().clear();

        for(int i = 0; i < model.getTruthTable().getInputColumnCount(); i++){

            int j = i;

            TableColumn<TruthTableDataModel, Entry> inputColumn = new TableColumn<>(model.getTruthTable().getInputHeader(i));
            inputColumn.setCellValueFactory(data -> data.getValue().getInputEntry(j));

            inputColumn.setCellFactory(param -> {

                TableCell<TruthTableDataModel, Entry> cell = new TableCell<TruthTableDataModel, Entry>() {

                    @Override
                    protected void updateItem(Entry item, boolean empty) {

                        if (item == getItem()) return;

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(item.getDescription());
                            super.setGraphic(null);
                        }

                    }

                };

                cell.setAlignment(Pos.CENTER);

                return cell;

            });

            inputColumn.setEditable(false);
            inputColumn.setSortable(false);

            TruthTblvw.getColumns().add(inputColumn);

        }

        for(int i = 0; i < model.getTruthTable().getOutputColumnCount(); i++){

            int j = i;

            TableColumn<TruthTableDataModel, Entry> outputColumn = new TableColumn<>(model.getTruthTable().getOutputHeader(i));
            outputColumn.setCellValueFactory(data -> data.getValue().getOutputEntry(j));

            outputColumn.setCellFactory(param -> {

                TableCell<TruthTableDataModel, Entry> cell = new TableCell<TruthTableDataModel, Entry>() {

                    @Override
                    protected void updateItem(Entry item, boolean empty) {

                        if (item == getItem()) return;

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(item.getDescription());
                            super.setGraphic(null);
                        }

                    }

                    @Override
                    public void startEdit() {
                        if (!isEmpty()) {
                            //abuse focus system
                            //see focusmodel listener
                            super.startEdit();
                            this.requestFocus();
                        }
                    }

                };

                cell.setOnMouseClicked(event -> {

                    if(!event.isShiftDown()) {

                        if (cell.getItem() == Entry.DONT_CARE) {
                            model.getTruthTable().setOutputEntry(cell.getTableRow().getIndex(),
                                    TruthTblvw.getColumns().indexOf(cell.getTableColumn()) - model.getTruthTable().getInputColumnCount(), Entry.ZERO);
                        } else if (cell.getItem() == Entry.ZERO) {
                            model.getTruthTable().setOutputEntry(cell.getTableRow().getIndex(),
                                    TruthTblvw.getColumns().indexOf(cell.getTableColumn()) - model.getTruthTable().getInputColumnCount(), Entry.ONE);
                        } else if (cell.getItem() == Entry.ONE) {
                            model.getTruthTable().setOutputEntry(cell.getTableRow().getIndex(),
                                    TruthTblvw.getColumns().indexOf(cell.getTableColumn()) - model.getTruthTable().getInputColumnCount(), Entry.DONT_CARE);
                        }

                    }else if(event.isShiftDown() && !truthTableSelectionModel.getSelectedCells().isEmpty()){

                        TablePosition pos = truthTableSelectionModel.getSelectedCells().get(0);

                        int minX, maxX;
                        int minY, maxY;

                        if(pos.getColumn() < TruthTblvw.getColumns().indexOf(cell.getTableColumn())){
                            minX = pos.getColumn();
                            maxX = TruthTblvw.getColumns().indexOf(cell.getTableColumn());
                        }else{
                            minX = TruthTblvw.getColumns().indexOf(cell.getTableColumn());
                            maxX = pos.getColumn();
                        }

                        if(pos.getRow() < cell.getTableRow().getIndex()){
                            minY = pos.getRow();
                            maxY = cell.getTableRow().getIndex();
                        }else{
                            minY = cell.getTableRow().getIndex();
                            maxY = pos.getRow();
                        }

                        for(int y = minY; y < maxY; y++){
                            for(int x = minX; x < maxX; x++){
                                truthTableSelectionModel.select(y, TruthTblvw.getColumns().get(x));
                            }
                        }

                    }

                    TruthTblvw.refresh();

                    TruthTblvw.requestFocus();

                    event.consume();

                });

                cell.setAlignment(Pos.CENTER);

                return cell;

            });

            outputColumn.setEditable(false);
            outputColumn.setSortable(false);

            TruthTblvw.getColumns().add(outputColumn);

        }

        for(int i = 0; i < model.getTruthTable().getRowCount(); i++){

            TruthTblvw.getItems().add(new TruthTableDataModel(model, i));

        }

    }

    private static class TruthTableDataModel{

        TruthTable truthTable;
        private int row;

        public TruthTableDataModel(AnalyzerModel model, int row){
            this.truthTable = model.getTruthTable();
            this.row = row;
        }

        public ObservableValue<Entry> getInputEntry(int column){
            return new SimpleObjectProperty<>(truthTable.getInputEntry(row, column));
        }

        public ObservableValue<Entry> getOutputEntry(int column){
            return new SimpleObjectProperty<>(truthTable.getOutputEntry(row, column));
        }

    }



    private void initializeExpressionTab(){

        LogicExpressionTab.textProperty().bind(LC.createStringBinding("expressionTab"));
        LogicExpressionTab.setOnSelectionChanged(event -> {

            if(!model.getOutputs().isEmpty() && ExpressionOutputCmbbx.getValue() == null)
                ExpressionOutputCmbbx.setValue(model.getOutputs().get(0));

            ExpressionLineLbl.setText(getCurrentStringInExpression());
            ExpressionTxtarea.setText(getCurrentStringInExpression());

        });

        ExpressionOutputLbl.textProperty().bind(LC.createStringBinding("outputSelectLabel"));

        ExpressionOutputCmbbx.setItems(model.getOutputs());
        ExpressionOutputCmbbx.setOnAction(actionEvent -> {
            ExpressionLineLbl.setText(getCurrentStringInExpression());
            ExpressionTxtarea.setText(getCurrentStringInExpression());
        });

        ExpressionLineLbl.setText("");

        ExpressionTxtarea.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.ENTER && !event.isShiftDown()){
                enterExpression();
            }else{
                calculateEnable();
            }

        });
        

        ExpressionClearBtn.textProperty().bind(LC.createStringBinding("exprClearButton"));
        ExpressionClearBtn.setOnAction(event ->{
            setErrorMessage(null);
            ExpressionTxtarea.setText("");
            calculateEnable();
        });

        ExpressionRevertBtn.textProperty().bind(LC.createStringBinding("exprRevertButton"));
        ExpressionRevertBtn.setDisable(true);
        ExpressionRevertBtn.setOnAction(event ->{
            setErrorMessage(null);
            ExpressionTxtarea.setText(getCurrentStringInExpression());
            ExpressionTxtarea.requestFocus();
        });

        ExpressionAddBtn.textProperty().bind(LC.createStringBinding("exprEnterButton"));
        ExpressionAddBtn.setDisable(true);
        ExpressionAddBtn.setOnAction(event ->{
            enterExpression();
        });

        ExpressionErrorLbl.setText("");

    }

    private String getCurrentStringInExpression() {

        String output = ExpressionOutputCmbbx.getValue();
        return output == null ? ""
                : model.getOutputExpressions().getExpressionString(output);

    }

    private void enterExpression(){

        try {
            String exprString = ExpressionTxtarea.getText();
            Expression expr = Parser.parse(ExpressionTxtarea.getText(), model);
            setErrorMessage(null);
            model.getOutputExpressions().setExpression(ExpressionOutputCmbbx.getValue(), expr, exprString);
        } catch (ParserException ex) {
            setErrorMessage(ex.getMessageGetter());
            ExpressionTxtarea.positionCaret(ex.getOffset());
        }

        ExpressionLineLbl.setText(getCurrentStringInExpression());

    }

    private void calculateEnable(){

        if(!ExpressionTxtarea.getText().equals(getCurrentStringInExpression()) && !ExpressionTxtarea.getText().equals("")){

            ExpressionClearBtn.setDisable(false);
            ExpressionAddBtn.setDisable(false);
            ExpressionRevertBtn.setDisable(false);

        }else if(!ExpressionTxtarea.getText().equals("")){

            ExpressionClearBtn.setDisable(false);
            ExpressionAddBtn.setDisable(true);
            ExpressionRevertBtn.setDisable(true);

        }

        if(ExpressionTxtarea.getText().equals("")){
            ExpressionClearBtn.setDisable(true);
        }


    }

    private void setErrorMessage(StringBinding key){

        if(key == null){
            ExpressionErrorLbl.textProperty().unbind();
        }else{
            ExpressionErrorLbl.textProperty().bind(key);
        }

    }



    private void initializeMinimizationTab(){

        MinimisationTab.textProperty().bind(LC.createStringBinding("minimizedTab"));
        MinimisationTab.setOnSelectionChanged(event ->{

            if(!model.getOutputs().isEmpty() && MinimisationOutputCmbbx.getValue() == null)
                MinimisationOutputCmbbx.setValue(model.getOutputs().get(0));

            setMinExpression();
            recalculateKarnaughMap();

        });

        MinimisationOutputLbl.textProperty().bind(LC.createStringBinding("outputSelectLabel"));

        MinimisationOutputCmbbx.setItems(model.getOutputs());
        MinimisationOutputCmbbx.setOnAction(actionEvent -> {

            setMinExpression();
            recalculateKarnaughMap();

        });

        MinimisationFormatLbl.textProperty().bind(LC.createStringBinding("minimizedFormat"));

        ObservableList<Integer> formats = FXCollections.observableArrayList();
        formats.addAll(AnalyzerModel.FORMAT_SUM_OF_PRODUCTS, AnalyzerModel.FORMAT_PRODUCT_OF_SUMS);

        MinimisationFormatCmbbx.setItems(formats);
        MinimisationFormatCmbbx.setValue(formats.get(0));
        MinimisationFormatCmbbx.setCellFactory(stringBindingListView -> new TranslationCell());
        MinimisationFormatCmbbx.setButtonCell(new TranslationCell());
        MinimisationFormatCmbbx.setOnAction(event ->{

            model.getOutputExpressions().setMinimizedFormat(MinimisationOutputCmbbx.getValue(), MinimisationFormatCmbbx.getValue());
            setMinExpression();
            redrawImplicants();

        });

        MinimisationExpressionLbl.setText("");

        MinimisationSetSelectedBtn.textProperty().bind(LC.createStringBinding("minimizedSetButton"));
        MinimisationSetSelectedBtn.setDisable(true);
        MinimisationSetSelectedBtn.setOnAction(event ->{

            model.getOutputExpressions().setExpression(MinimisationOutputCmbbx.getValue(),
                    model.getOutputExpressions().getMinimalExpression(MinimisationOutputCmbbx.getValue()));

            calculateSetMinEnable();

        });

        calculateSetMinEnable();

        MinimazationKarnaughMapHeadersTrTblvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        MinimazationKarnaughMapHeadersTrTblvw.setPlaceholder(null);

        MinimazationKarnaughMapVarsTrTblvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        MinimazationKarnaughMapVarsTrTblvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        MinimazationKarnaughMapVarsTrTblvw.getSelectionModel().setCellSelectionEnabled(true);

        recalculateKarnaughMap();

    }

    static class TranslationCell extends ListCell<Integer> {

        @Override
        protected void updateItem(Integer item, boolean empty) {

            super.updateItem(item, empty);

            textProperty().unbind();

            if (empty || item == null) {
                setText("");
            } else {
                if(item == 0){
                    textProperty().bind(LC.createStringBinding("minimizedSumOfProducts"));
                }else if(item == 1){
                    textProperty().bind(LC.createStringBinding("minimizedProductOfSums"));
                }
            }

        }
    }

    private void setMinExpression(){
        if(model.getOutputExpressions().getMinimalExpression(MinimisationOutputCmbbx.getValue()) != null) {
            MinimisationExpressionLbl.setText(model.getOutputExpressions().getMinimalExpression(MinimisationOutputCmbbx.getValue()).toString());
            calculateSetMinEnable();
        }
    }

    private void calculateSetMinEnable(){

        MinimisationSetSelectedBtn.setDisable(
                MinimisationOutputCmbbx.getValue() == null || model.getOutputExpressions().isExpressionMinimal(MinimisationOutputCmbbx.getValue())
        );

    }

    private void recalculateKarnaughMap(){

        MinimazationKarnaughMapHeadersTrTblvw.getColumns().clear();
        MinimazationKarnaughMapHeadersTrTblvw.refresh();

        MinimazationKarnaughMapVarsTrTblvw.getItems().clear();
        MinimazationKarnaughMapVarsTrTblvw.getColumns().clear();
        MinimazationKarnaughMapVarsTrTblvw.refresh();

        if(model.getTruthTable().getInputColumnCount() > MAX_VARS || MinimisationOutputCmbbx.getValue() == null){
            MinimazationKarnaughMapHeadersTrTblvw.setDisable(true);
            MinimazationKarnaughMapVarsTrTblvw.setDisable(true);
            MinimazationKarnaughMapHeadersTrTblvw.setVisible(false);
            MinimazationKarnaughMapVarsTrTblvw.setVisible(false);
            MinimizationKarnaughMapRoot.getChildren().removeAll(implicantsRect);
            return;
        }else{
            MinimazationKarnaughMapHeadersTrTblvw.setDisable(false);
            MinimazationKarnaughMapVarsTrTblvw.setDisable(false);
            MinimazationKarnaughMapHeadersTrTblvw.setVisible(true);
            MinimazationKarnaughMapVarsTrTblvw.setVisible(true);
        }

        int colCount = model.getTruthTable().getInputColumnCount();

        rows = 1 << ROW_VARS[colCount];
        cols = 1 << COL_VARS[colCount];

        int rowVars = ROW_VARS[colCount];
        int colVars = COL_VARS[colCount];

        int outputColumn = model.getTruthTable().getOutputIndex(MinimisationOutputCmbbx.getValue());

        String rowHeader = header(0, rowVars);
        String colHeader = header(rowVars, rowVars + colVars);

        //Rows headers, cause it use fake table, so only tablecolumns here

        TableColumn<String, String> rowHeaderColumn = new TableColumn<>(rowHeader);
        rowHeaderColumn.setResizable(false);
        rowHeaderColumn.setReorderable(false);
        rowHeaderColumn.setSortable(false);

        rowHeaderColumn.setPrefWidth(45*rows);

        for (int i = 0; i < rows; i++) {

            TableColumn<String, String> column = new TableColumn<>(label(i, rows));
            column.setResizable(false);
            column.setReorderable(false);
            column.setSortable(false);

            column.setPrefWidth(45);

            rowHeaderColumn.getColumns().add(column);

        }

        MinimazationKarnaughMapHeadersTrTblvw.getColumns().add(rowHeaderColumn);

        //Vars table

        TableColumn<KarnaughMapDataModel, Entry> varHeaderColumn = new TableColumn<>(colHeader);
        varHeaderColumn.setResizable(false);
        varHeaderColumn.setReorderable(false);
        varHeaderColumn.setSortable(false);

        rowHeaderColumn.setPrefWidth(45*cols);

        for (int i = 0; i < cols; i++) {

            int j = i;

            TableColumn<KarnaughMapDataModel, Entry> column = new TableColumn<>(label(i, cols));

            column.setCellValueFactory(data -> data.getValue().getValue(j));
            column.setCellFactory(param -> {

                TableCell<KarnaughMapDataModel, Entry> cell = new TableCell<KarnaughMapDataModel, Entry>() {

                    @Override
                    protected void updateItem(Entry item, boolean empty) {

                        if (item == getItem()) return;

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(item.getDescription());
                            super.setGraphic(null);
                        }

                    }

                };

                cell.setOnMouseClicked(event -> {

                    if(!event.isShiftDown()) {

                        if (cell.getItem() == Entry.DONT_CARE) {
                            model.getTruthTable().setOutputEntry(
                                    getTableRow(cell.getTableRow().getIndex(),
                                            MinimazationKarnaughMapVarsTrTblvw.getColumns().get(0).getColumns().indexOf(cell.getTableColumn()), rows, cols),
                                    outputColumn,
                                    Entry.ZERO
                            );
                        } else if (cell.getItem() == Entry.ZERO) {
                            model.getTruthTable().setOutputEntry(
                                    getTableRow(cell.getTableRow().getIndex(),
                                            MinimazationKarnaughMapVarsTrTblvw.getColumns().get(0).getColumns().indexOf(cell.getTableColumn()), rows, cols),
                                    outputColumn,
                                    Entry.ONE
                            );
                        } else if (cell.getItem() == Entry.ONE) {
                            model.getTruthTable().setOutputEntry(
                                    getTableRow(cell.getTableRow().getIndex(),
                                            MinimazationKarnaughMapVarsTrTblvw.getColumns().get(0).getColumns().indexOf(cell.getTableColumn()), rows, cols),
                                    outputColumn,
                                    Entry.DONT_CARE
                            );
                        }

                        model.getOutputExpressions().invalidate(MinimisationOutputCmbbx.getValue(), false);
                        setMinExpression();
                        redrawImplicants();

                    }

                    MinimazationKarnaughMapVarsTrTblvw.refresh();

                    MinimazationKarnaughMapVarsTrTblvw.requestFocus();

                    event.consume();

                });

                cell.setAlignment(Pos.CENTER);
                cell.setPrefHeight(45);

                return cell;

            });


            column.setResizable(false);
            column.setReorderable(false);
            column.setSortable(false);

            column.setPrefWidth(45);


            varHeaderColumn.getColumns().add(column);

        }

        MinimazationKarnaughMapVarsTrTblvw.getColumns().add(varHeaderColumn);

        for (int i = 0; i < rows; i++) {

            Integer[] data = new Integer[cols];

            for (int j = 0; j < cols; j++) {

                int row = getTableRow(i, j, rows, cols);
                data[j] = row;

            }

            MinimazationKarnaughMapVarsTrTblvw.getItems().add(new KarnaughMapDataModel(model, data, outputColumn));

        }

        redrawImplicants();

    }

    private void redrawImplicants(){

        if(model.getTruthTable().getInputColumnCount() > MAX_VARS || MinimisationOutputCmbbx.getValue() == null)
            return;

        if(MinimisationOutputCmbbx.getValue() != null)
            model.getOutputExpressions().invalidate(MinimisationOutputCmbbx.getValue(), false);

        MinimizationKarnaughMapRoot.getChildren().removeAll(implicantsRect);

        List<Implicant> implicants = model.getOutputExpressions().getMinimalImplicants(MinimisationOutputCmbbx.getValue());

        if (implicants != null) {

            int index = 0;
            for (Implicant imp : implicants) {

                Color c = IMP_COLORS[index % IMP_COLORS.length];

                int rowMax = -1;
                int rowMin = rows;
                int colMax = -1;
                int colMin = cols;
                boolean oneRowFound = false;
                int count = 0;
                for (Implicant sq : imp.getTerms()) {
                    int tableRow = sq.getRow();
                    int row = getRow(tableRow, rows, cols);
                    int col = getCol(tableRow, rows, cols);
                    if (row == 1) oneRowFound = true;
                    if (row > rowMax) rowMax = row;
                    if (row < rowMin) rowMin = row;
                    if (col > colMax) colMax = col;
                    if (col < colMin) colMin = col;
                    ++count;
                }

                int numCols = colMax - colMin + 1;
                int numRows = rowMax - rowMin + 1;
                int covered = numCols * numRows;
                int d = 2 * IMP_RADIUS;
                if (covered == count) {
                    createRectAt(INIT_OFFSET_X + colMin * CELL_WIDTH + IMP_INSET,
                            INIT_OFFSET_Y + rowMin * CELL_HEIGHT + IMP_INSET,
                            numCols * CELL_WIDTH - 2 * IMP_INSET,
                            numRows * CELL_HEIGHT - 2 * IMP_INSET,
                            d,
                            d,
                            c
                    );
                } else if (covered == 16) {
                    if (count == 4) {
                        int w = CELL_WIDTH - IMP_INSET;
                        int h = CELL_HEIGHT - IMP_INSET;
                        int x1 = 45 + 3 * CELL_WIDTH + IMP_INSET;
                        int y1 = 45 + 3 * CELL_HEIGHT + IMP_INSET;
                        createRectAt(45,  45,  w, h, d, d, c);
                        createRectAt(x1, 45,  w, h, d, d, c);
                        createRectAt(45,  y1, w, h, d, d, c);
                        createRectAt(x1, y1, w, h, d, d, c);
                    } else if (oneRowFound) { // first and last columns
                        int w = CELL_WIDTH - IMP_INSET;
                        int h = 4 * CELL_HEIGHT - 2 * IMP_INSET;
                        int x1 = 45 + 3 * CELL_WIDTH + IMP_INSET;
                        createRectAt(45,  45 + IMP_INSET, w, h, d, d, c);
                        createRectAt(x1, 45 + IMP_INSET, w, h, d, d, c);
                    } else { // first and last rows
                        int w = 4 * CELL_WIDTH - 2 * IMP_INSET;
                        int h = CELL_HEIGHT - IMP_INSET;
                        int y1 = 45 + 3 * CELL_HEIGHT + IMP_INSET;
                        createRectAt(45 + IMP_INSET, 45,  w, h, d, d, c);
                        createRectAt(45 + IMP_INSET, y1, w, h, d, d, c);
                    }
                } else if (numCols == 4) {
                    int top = 45 + rowMin * CELL_HEIGHT + IMP_INSET;
                    int w = CELL_WIDTH - IMP_INSET;
                    int h = numRows * CELL_HEIGHT - 2 * IMP_INSET;
                    // handle half going off left edge
                    createRectAt(45, top, w, h, d, d, c);
                    // handle half going off right edge
                    createRectAt(45 + 3 * CELL_WIDTH + IMP_INSET, top, w, h, d, d, c);
                } else { // numRows == 4
                    int left = 45 + colMin * CELL_WIDTH + IMP_INSET;
                    int w = numCols * CELL_WIDTH - 2 * IMP_INSET;
                    int h = CELL_HEIGHT - IMP_INSET;
                    // handle half going off top edge
                    createRectAt(left, 45, w, h, d, d, c);
                    // handle half going off right edge
                    createRectAt(left, 45 + 3 * CELL_HEIGHT + IMP_INSET, w, h, d, d, c);
                }

                index++;

            }

        }

    }

    private void createRectAt(int x, int y, int w, int h, int arcW, int arcH, Color c){

        Rectangle rect = new Rectangle();

        implicantsRect.add(rect);
        MinimizationKarnaughMapRoot.getChildren().add(rect);

        rect.setMouseTransparent(true);

        rect.setX(x);
        rect.setY(y);
        rect.setWidth(w);
        rect.setHeight(h);
        rect.setArcWidth(arcW);
        rect.setArcHeight(arcH);

        rect.setFill(c);
        rect.setStroke(Color.TRANSPARENT);

    }

    private String header(int start, int stop) {
        if (start >= stop) return "";
        StringBuilder ret = new StringBuilder(model.getInputs().get(start));
        for (int i = start + 1; i < stop; i++) {
            ret.append(", ");
            ret.append(model.getInputs().get(i));
        }
        return ret.toString();
    }

    private String label(int row, int rows) {
        switch (rows) {
            case 2: return "" + row;
            case 4:
                switch (row) {
                    case 0: return "00";
                    case 1: return "01";
                    case 2: return "11";
                    case 3: return "10";
                }
            default: return "";
        }
    }

    private int getTableRow(int row, int col, int rows, int cols) {
        return toRow(row, rows) * cols + toRow(col, cols);
    }

    private int toRow(int row, int rows) {
        if (rows == 4) {
            switch (row) {
                case 2: return 3;
                case 3: return 2;
                default: return row;
            }
        } else {
            return row;
        }
    }

    private int getRow(int tableRow, int rows, int cols) {
        int ret = tableRow / cols;
        switch (ret) {
            case 2: return 3;
            case 3: return 2;
            default: return ret;
        }
    }

    private int getCol(int tableRow, int rows, int cols) {
        int ret = tableRow % cols;
        switch (ret) {
            case 2: return 3;
            case 3: return 2;
            default: return ret;
        }
    }

    static class KarnaughMapDataModel{

        private AnalyzerModel model;
        private Integer[] dataIndex;
        private int columnIndex;

        public KarnaughMapDataModel(AnalyzerModel model, Integer[] data, int column){
            this.model = model;
            this.dataIndex = data;
            this.columnIndex = column;
        }

        public ObservableValue<Entry> getValue(int tablecolumn){
            return new SimpleObjectProperty<>(model.getTruthTable().getOutputEntry(dataIndex[tablecolumn],columnIndex));
        }

    }


    @Override
    public void onClose() {
        System.out.println("Analysis closed");
    }

}

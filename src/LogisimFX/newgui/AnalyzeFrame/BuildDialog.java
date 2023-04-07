/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.AnalyzeFrame;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.file.LogisimFileActions;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.FrameManager;
import LogisimFX.proj.Project;
import LogisimFX.std.gates.CircuitBuilder;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class BuildDialog<T extends AnalyzerModel> extends Dialog<T> {

    static class TranslationCell extends ListCell<Project> {

        @Override
        protected void updateItem(Project item, boolean empty) {

            super.updateItem(item, empty);
            textProperty().unbind();

            if (empty || item == null) {
                setText("");
            } else {
                textProperty().bind(item.getLogisimFile().getDisplayName());
            }

        }
    }

    private final GridPane grid;

    private final Label projectLbl;
    private final ComboBox<Project> projectCmbBx;
    private final Label circNameLbl;
    private final TextField circNameTxtfld;
    private final CheckBox twoInputsChckbx;
    private final CheckBox nandOnlyChckbx;

    public BuildDialog() {
        this((T)null, (T[])null);
    }

    public BuildDialog(T defaultChoice,  @SuppressWarnings("unchecked") T... choices) {
        this(defaultChoice);
    }

    public BuildDialog(T model) {

        final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setVgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);


        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("choice-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        projectLbl = new Label();
        projectLbl.textProperty().bind(LC.createStringBinding("buildProjectLabel"));

        projectCmbBx = new ComboBox<>();
        projectCmbBx.setCellFactory(stringBindingListView -> new TranslationCell());
        projectCmbBx.getItems().addAll(FrameManager.getOpenProjects());
        projectCmbBx.setButtonCell(new TranslationCell());
        projectCmbBx.setValue(model.getCurrentProject());

        circNameLbl = new Label();
        circNameLbl.textProperty().bind(LC.createStringBinding("buildNameLabel"));

        circNameTxtfld = new TextField();
        if(model.getCurrentCircuit() != null) {
            circNameTxtfld.setText(model.getCurrentCircuit().getName());
        }

        twoInputsChckbx = new CheckBox();
        twoInputsChckbx.textProperty().bind(LC.createStringBinding("buildTwoInputsLabel"));
        twoInputsChckbx.setSelected(false);

        nandOnlyChckbx = new CheckBox();
        nandOnlyChckbx.textProperty().bind(LC.createStringBinding("buildNandsLabel"));
        nandOnlyChckbx.setSelected(false);

        ObservableList<String> outputs = model.getOutputs();
        boolean enableNands = true;
        for (int i = 0; i < outputs.size(); i++) {
            String output = outputs.get(i);
            Expression expr = model.getOutputExpressions().getExpression(output);
            if (expr != null && expr.containsXor()) { enableNands = false; break; }
        }
        nandOnlyChckbx.setDisable(!enableNands);

        updateGrid();

        setResultConverter((dialogButton) -> {

            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();

            if(data == ButtonBar.ButtonData.OK_DONE){

                boolean replace = false;
                boolean ok = false;
                while (!ok) {
                    if (projectCmbBx.getValue() == null) {
                        DialogManager.createErrorDialog(LC.get("buildDialogErrorTitle"), LC.get("buildNeedProjectError"));
                        continue;
                    }

                    if (circNameTxtfld.getText().equals("")) {
                        DialogManager.createErrorDialog(LC.get("buildDialogErrorTitle"), LC.get("buildNeedCircuitError"));
                        continue;
                    }

                    if (projectCmbBx.getValue().getLogisimFile().getCircuit(circNameTxtfld.getText()) != null) {
                        int choice = DialogManager.createConfirmWarningDialog(LC.get("buildConfirmReplaceTitle"), LC.get("buildConfirmReplaceMessage"));
                        if (choice != 1) {
                            continue;
                        }
                        replace = true;
                    }

                    ok = true;

                }

                performAction(projectCmbBx.getValue(), circNameTxtfld.getText(),
                        replace, twoInputsChckbx.isSelected(), nandOnlyChckbx.isSelected(), model);

            }

            return null;

        });

    }

    private void performAction(Project dest, String name, boolean replace,
                               final boolean twoInputs, final boolean useNands, AnalyzerModel model) {
        if (replace) {
            final Circuit circuit = dest.getLogisimFile().getCircuit(name);
            if (circuit == null) {
                DialogManager.createErrorDialog("Internal Error", "Internal error prevents replacing circuit.");
                return;
            }

            CircuitMutation xn = CircuitBuilder.build(circuit, model, twoInputs,
                    useNands);
            dest.doAction(xn.toAction(LC.createStringBinding("replaceCircuitAction")));
        } else {
            // add the circuit
            Circuit circuit = new Circuit(name);
            CircuitMutation xn = CircuitBuilder.build(circuit, model, twoInputs,
                    useNands);
            xn.execute();
            dest.doAction(LogisimFileActions.addCircuit(circuit));
            dest.setCurrentCircuit(circuit);
        }
    }

    private void updateGrid() {

        grid.getChildren().clear();

        grid.add(projectLbl, 0, 0);
        grid.add(projectCmbBx, 1, 0);
        grid.add(circNameLbl, 0,1);
        grid.add(circNameTxtfld, 1, 1);
        grid.add(twoInputsChckbx, 1, 2);
        grid.add(nandOnlyChckbx, 1, 3);
        getDialogPane().setContent(grid);

    }

}

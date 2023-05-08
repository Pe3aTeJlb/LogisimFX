/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitState;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;

public class EditorBase extends VBox {

    private static final int MAX_UNDO_SIZE = 64;

    private static class ActionData {
        CircuitState circuitState;
        Action action;

        public ActionData(CircuitState circuitState, Action action) {
            this.circuitState = circuitState;
            this.action = action;
        }
    }

    private LinkedList<ActionData> undoLog = new LinkedList<>();
    private LinkedList<ActionData> redoLog = new LinkedList<>();

    protected Project proj;

    protected MyProjectListener projectListener = new MyProjectListener();
    class MyProjectListener implements ProjectListener {

        public void projectChanged(ProjectEvent event) {

            int action = event.getAction();

            if (action == ProjectEvent.ACTION_COMPLETE){

                if (isSelected()) {
                    Action act = (Action) event.getData();
                    int actType = act.getActionType();
                    if (
                            actType == Action.CLIPBOARD_ACTION ||
                                    actType == Action.TOOL_ATTRIBUTE_ACTION ||
                                    actType == Action.APPEARANCE_SELECTION_ACTION ||
                                    actType == Action.ROM_CONTENTS_ACTION ||
                                    actType == Action.SET_ATTRIBUTE_ACTION ||
                                    actType == Action.REVERT_APPEARANCE_ACTION ||
                                    actType == Action.CANVAS_ACTION_ADAPTER ||
                                    actType == Action.LAYOUT_SELECTION_ACTION ||
                                    actType == Action.JOINED_ACTION ||
                                    actType == Action.CIRCUIT_ACTION
                    ) {
                        undoLog.add(new ActionData(proj.getCircuitState(), act));
                        while (undoLog.size() > MAX_UNDO_SIZE) {
                            undoLog.removeFirst();
                        }
                        setUndoAvailable(!undoLog.isEmpty());
                    }
                }

            }

        }

    }

    public EditorBase(Project project){
        super();
        this.proj = project;
        proj.getFrameController().editorProperty().addListener((observableValue, handler, t1) -> setIsSelected());
        proj.addProjectListener(projectListener);
    }

    public Project getProj() {
        return proj;
    }


    private SimpleBooleanProperty undoAvailable;

    private void setUndoAvailable(boolean val) {
        undoAvailableProperty().set(val);
    }

    public boolean isUndoAvailable() {
        return undoAvailableProperty().get();
    }

    public BooleanProperty undoAvailableProperty() {
        if (undoAvailable == null) {
            undoAvailable = new SimpleBooleanProperty(this, "undoAvailable", false);
        }
        return undoAvailable;
    }

    public Action getLastAction() {
        if (undoLog.size() == 0) {
            return null;
        } else {
            return undoLog.getLast().action;
        }
    }

    public void undo(){
        if (!undoLog.isEmpty()) {
            redoLog.addLast(undoLog.getLast());
            while (redoLog.size() > MAX_UNDO_SIZE) {
                redoLog.removeFirst();
            }
            ActionData data = undoLog.removeLast();
            proj.undoAction(data.action, data.circuitState);
            setRedoAvailable(!redoLog.isEmpty());
            setUndoAvailable(!undoLog.isEmpty());
        }
    }



    private SimpleBooleanProperty redoAvailable;

    private void setRedoAvailable(boolean val) {
        redoAvailableProperty().set(val);
    }

    public boolean isRedoAvailable() {
        return redoAvailableProperty().get();
    }

    public BooleanProperty redoAvailableProperty() {
        if (redoAvailable == null) {
            redoAvailable = new SimpleBooleanProperty(this, "redoAvailable", false);
        }
        return redoAvailable;
    }

    public Action getLastRedoAction() {
        if (redoLog.size() == 0) return null;
        else return redoLog.getLast().action;
    }

    public void redo(){
        if (!redoLog.isEmpty()) {
            undoLog.addLast(redoLog.getLast());
            ActionData data = redoLog.removeLast();
            proj.redoAction(data.action, data.circuitState);
            setRedoAvailable(!redoLog.isEmpty());
            setUndoAvailable(!undoLog.isEmpty());
        }
    }



    //IsSelected prperty

    private BooleanProperty isSelected;

    private void setIsSelected() {
        editorProperty().set(proj.getFrameController().getEditor() == this);
    }

    public boolean isSelected() {
        return editorProperty().get();
    }

    public BooleanProperty editorProperty() {
        if (isSelected == null) {
            isSelected = new SimpleBooleanProperty(this, "isSelected", false);
        }
        return isSelected;
    }

    public String getEditorDescriptor(){
        return null;
    }

    public void copyAccelerators(){

    }

    public List<MenuItem> getEditMenuItems(){
        return null;
    }

    public EditHandler getEditHandler(){
        return null;
    }

    public void terminateListeners(){
        proj.removeProjectListener(projectListener);
    }

}

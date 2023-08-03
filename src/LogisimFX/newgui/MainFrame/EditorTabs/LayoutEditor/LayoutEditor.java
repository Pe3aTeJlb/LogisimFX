/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.IconsManager;
import LogisimFX.circuit.Circuit;
import LogisimFX.comp.Component;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutEditHandler;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LayoutEditor extends EditorBase {

    private LayoutEditorToolBar toolBar;
    private LayoutCanvas layoutCanvas;
    private ToolBar findBar;
    private TextField findTxtFld;
    private SimpleStringProperty currFindIndex, totalFindIndex;
    private ArrayList<Component> compList = new ArrayList<>();
    private AtomicInteger currCompIndex = new AtomicInteger(0);
    private HBox footBar;

    private LayoutEditHandler editHandler;
    private LayoutEditorEditMenu menu;

    private Circuit circ;



    public LayoutEditor(Project project, Circuit circ){

        super(project);

        this.circ = circ;

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);
        layoutCanvas = new LayoutCanvas(canvasRoot, this);
        canvasRoot.getChildren().add(layoutCanvas);
        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        toolBar = new LayoutEditorToolBar(proj, this);
        toolBar.setOnMousePressed(event -> Event.fireEvent(this, event.copyFor(event.getSource(), this)));

        editHandler = new LayoutEditHandler(this);
        menu = new LayoutEditorEditMenu(this);
        layoutCanvas.getSelection().addListener(menu);

        initFindBar();

        initFootBar();

        this.getChildren().addAll(toolBar, canvasRoot, findBar, footBar);

        this.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                closeFindBar();
            }
        });

    }

    private void initFindBar(){

        currFindIndex = new SimpleStringProperty("0");
        totalFindIndex = new SimpleStringProperty("0");

        findBar = new ToolBar();
        findBar.setVisible(false);

        findTxtFld = new TextField();
        findTxtFld.setOnKeyPressed(event ->{
            if (event.getCode() == KeyCode.ENTER){
                find();
            }
        });
        //findTxtFld.textProperty().addListener(change -> find());

        Label findResultLbl = new Label();
        findResultLbl.textProperty().bind(
                Bindings.concat(
                        currFindIndex,
                        "/",
                        totalFindIndex
                )
        );

        Button prevCompBt = new Button();
        prevCompBt.setGraphic(IconsManager.getImageView("arrowleft.gif"));
        prevCompBt.setOnAction(event -> prevComp());

        Button nextCompBtn = new Button();
        nextCompBtn.setGraphic(IconsManager.getImageView("arrowright.gif"));
        nextCompBtn.setOnAction(event -> nextComp());

        findBar.getItems().addAll(findTxtFld, findResultLbl, prevCompBt, nextCompBtn);

        findBar.setMinHeight(0);
        findBar.setMaxHeight(0);

    }

    public void triggerFindBar(){

        if (findBar.isVisible()){
            closeFindBar();
        } else {
            openFindBar();
        }

    }

    private void openFindBar(){
        findBar.setVisible(true);
        findBar.setMinHeight(-1);
        findBar.setMaxHeight(-1);
        find();
    }

    private void closeFindBar(){
        findBar.setVisible(false);
        findBar.setMinHeight(0);
        findBar.setMaxHeight(0);
        proj.getFrameController().getLayoutCanvas().focusOnComp(null);
        //layoutCanvas.focusOnComp(null);
    }

    private void find() {

        if (findTxtFld.getText().isEmpty()) {
            return;
        }

        compList.clear();

        for (Component comp : circ.getNonWires()) {
            if (comp.getAttributeSet().containsAttribute(StdAttr.LABEL)) {
                if (comp.getAttributeSet().getValue(StdAttr.LABEL).contains(findTxtFld.getText())) {
                    compList.add(comp);
                }
            }
        }

        if (compList.size() == 0) {
            return;
        }

        proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(0));
        layoutCanvas.focusOnComp(compList.get(0));
        totalFindIndex.set(String.valueOf(compList.size()));
        currFindIndex.set(String.valueOf(currCompIndex.get() + 1));

    }

    private void nextComp() {
        if (compList.size()==0){
            return;
        }
        gotoNextComp(compList, currCompIndex);
        currFindIndex.set(String.valueOf(currCompIndex.get()+1));
    }

    private void gotoNextComp(ArrayList<Component> coordinateList, AtomicInteger currCompIndex) {
        if (currCompIndex.get() >= (coordinateList.size()-1) && coordinateList.size()!=0) return;
        currCompIndex.incrementAndGet();
        int index = currCompIndex.get();
        proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(index));
        layoutCanvas.focusOnComp(compList.get(index));
    }


    private void prevComp() {
        if (compList.size()==0) {
            return;
        }
        gotoPrevComp(compList, currCompIndex);
        currFindIndex.set(String.valueOf(currCompIndex.get()+1));
    }

    private void gotoPrevComp(ArrayList<Component> coordinateList, AtomicInteger currCompIndex) {
        if (currCompIndex.get() <= 0 && coordinateList.size()!=0) return;
        currCompIndex.decrementAndGet();
        int index = currCompIndex.get();
        proj.getFrameController().getLayoutCanvas().focusOnComp(compList.get(index));
        layoutCanvas.focusOnComp(compList.get(index));
    }


    private void initFootBar(){

        footBar = new HBox();
        footBar.setAlignment(Pos.CENTER_RIGHT);
        footBar.setSpacing(5);

        Label info = new Label();
        HBox.setHgrow(info, Priority.ALWAYS);
        info.textProperty().bind(
                Bindings.concat(
                        LC.createStringBinding("canvasX"),
                        layoutCanvas.mouseXProperty,
                        " ",
                        LC.createStringBinding("canvasY"),
                        layoutCanvas.mouseYProperty,
                        " ",
                        LC.createStringBinding("canvasZoom"),
                        layoutCanvas.zoomProperty,
                        "%"
                )
        );

        footBar.getChildren().add(info);

    }


    public List<MenuItem> getEditMenuItems(){
        return menu.getMenuItems();
    }

    public EditHandler getEditHandler(){
        return editHandler;
    }

    public LayoutCanvas getLayoutCanvas(){
        return layoutCanvas;
    }

    public LayoutEditorToolBar getLayoutEditorToolBar(){
        return toolBar;
    }

    public Circuit getCirc(){
        return circ;
    }

    @Override
    public String getEditorDescriptor(){
        return circ.getName();
    }


    @Override
    public void copyAccelerators(){
        if (this.getScene() != proj.getFrameController().getStage().getScene()){
            this.getScene().getAccelerators().putAll(
                    proj.getFrameController().getStage().getScene().getAccelerators()
            );
        }
        toolBar.recalculateAccelerators();
    }

    @Override
    public void terminateListeners(){
        proj.removeProjectListener(projectListener);
        toolBar.terminateListeners();
        layoutCanvas.getSelection().removeListener(menu);
        layoutCanvas.getSelection().removeListener(proj.getFrameController().getAttributeTable());
        layoutCanvas.terminateCanvas();
    }

}

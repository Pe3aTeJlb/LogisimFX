/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame;

import LogisimFX.circuit.Circuit;
import LogisimFX.comp.Component;
import LogisimFX.data.Attribute;
import LogisimFX.draw.tools.AbstractTool;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.SelectionEvent;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.SelectionListener;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.Selection;
import LogisimFX.proj.Project;
import LogisimFX.tools.Tool;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class AttributeTable extends GridPane
        implements Selection.Listener, SelectionListener {

    private ColumnConstraints attr,value;

    private Project proj;

    private int currRow = 1;

    private Label selectionLbl, attrNameLbl, attrValueLbl;

    private static AttrTableModel attrModel;

    public AttributeTable(Project proj){

        super();

        this.proj = proj;

        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(10));
        this.setMaxWidth(Region.USE_COMPUTED_SIZE);

        attr = new ColumnConstraints();
        attr.setPercentWidth(50);

        value = new ColumnConstraints();
        value.setPercentWidth(50);

        this.getColumnConstraints().addAll(attr,value);

   //     proj.getFrameController().getLayoutCanvas().getSelection().addListener(this);
   //     proj.getFrameController().getAppearanceCanvas().getSelection().addSelectionListener(this);

    }

    private void setTitle(){

        this.getChildren().clear();

        if(attrModel != null && attrModel.getTitle() != null && attrModel.getTitle().getValue() != null) {

            selectionLbl = new Label();
            selectionLbl.textProperty().bind(attrModel.getTitle());
            this.add(selectionLbl,0,0,2,1);
            GridPane.setHalignment(selectionLbl, HPos.CENTER);

            if(!attrModel.getAttributeSet().getAttributes().isEmpty()) {
                attrNameLbl = new Label();
                attrNameLbl.textProperty().bind(LC.createStringBinding("attributeNameTitle"));
                this.add(attrNameLbl, 0, 1);
                GridPane.setHalignment(attrNameLbl, HPos.CENTER);

                attrValueLbl = new Label();
                attrValueLbl.textProperty().bind(LC.createStringBinding("attributeValueTitle"));
                this.add(attrValueLbl, 1, 1);
                GridPane.setHalignment(attrValueLbl, HPos.CENTER);
            }
        }

    }

    private void updateTable(){

        currRow = 2;

        if(attrModel instanceof AttrTableAppearanceSelectionModel)((AttrTableAppearanceSelectionModel) attrModel).setAttrs();

        if(attrModel.getAttributeSet().getAttributes().contains(StdAttr.ACCESS_MODE)){
            if(attrModel.getAttributeSet().getValue(attrModel.getAttributeSet().getAttribute("accessmode")).equals(StdAttr.PROTECTION_MODE))
                return;
        }

        for (Attribute attr: attrModel.getAttributeSet().getAttributes()) {
            currRow += 1;
            this.add(new Label(attr.getDisplayName()),0,currRow);
            this.add(attr.getCell(attrModel.getAttributeSet().getValue(attr)), 1, currRow);
        }

    }

    public static void setValueRequested(Attribute<?> attr, Object value) throws AttrTableSetException {
        attrModel.setValueRequested((Attribute<Object>) attr, value);
    }


    //Define table model

    public void setTool(Tool tool){

        if(tool != null && tool.getAttributeSet() != null) {
            attrModel = new AttrTableToolModel(proj, tool);
            setTitle();
            updateTable();
        }

    }

    public void setTool(AbstractTool tool){

        if(tool != null && tool.getAttributes() != null) {
            attrModel = new AttrTableAbstractToolModel(tool);
            setTitle();
            updateTable();
        }

    }

    public void setComponent(Circuit circ, Component comp){

        if(comp != null) {
            attrModel = new AttrTableComponentModel(proj, circ, comp);
            setTitle();
            updateTable();
        }

    }

    public void setCircuit(Circuit circ){

        if(circ != null) {
            attrModel = new AttrTableCircuitModel(proj, circ);
            setTitle();
            updateTable();
        }

    }


    //from layout view
    @Override
    public void selectionChanged(Selection.Event event) {

        attrModel = new AttrTableSelectionModel(proj);
        setTitle();
        updateTable();

    }

    //from appearance view
    @Override
    public void selectionChanged(SelectionEvent e) {

        attrModel = new AttrTableAppearanceSelectionModel(proj);
        setTitle();
        updateTable();

    }


    public void terminateListener(){

        proj.getFrameController().getLayoutCanvas().getSelection().removeListener(this);
        proj.getFrameController().getAppearanceCanvas().getSelection().removeSelectionListener(this);

    }

}

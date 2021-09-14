package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.draw.tools.AbstractTool;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.SelectionEvent;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.SelectionListener;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.Selection;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.Tool;

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

        proj.getFrameController().getLayoutCanvas().getSelection().addListener(this);
        proj.getFrameController().getAppearanceCanvas().getSelection().addSelectionListener(this);

    }

    private void setTitle(){

        this.getChildren().clear();

        if(attrModel != null && attrModel.getTitle().getValue() != null) {

            selectionLbl = new Label();
            selectionLbl.textProperty().bind(attrModel.getTitle());
            this.add(selectionLbl,0,0,2,1);
            GridPane.setHalignment(selectionLbl, HPos.CENTER);

            attrNameLbl = new Label();
            attrNameLbl.textProperty().bind(LC.createStringBinding("attributeNameTitle"));
            this.add(attrNameLbl,0,1);
            GridPane.setHalignment(attrNameLbl, HPos.CENTER);

            attrValueLbl = new Label();
            attrValueLbl.textProperty().bind(LC.createStringBinding("attributeValueTitle"));
            this.add(attrValueLbl,1,1);
            GridPane.setHalignment(attrValueLbl, HPos.CENTER);

        }

    }

    private void updateTable(){

        currRow = 2;

       // System.out.println("attr size "+attributeSet.getAttributes().size());

        for (Attribute attr: attrModel.getAttributeSet().getAttributes()) {
            currRow += 1;
            this.add(new Label(attr.getDisplayName()),0,currRow);
            this.add(attr.getCell(attrModel.getAttributeSet().getValue(attr)), 1,currRow);

            //System.out.println("attr "+attr.getName()+" "+attributeSet.getValue(attr).toString());

        }

    }

    public static void setValueRequested(Attribute<?> attr, Object value) throws AttrTableSetException {
        attrModel.setValueRequested((Attribute<Object>) attr, value);
    }

    public static void printShit(){

        for (Attribute attr: attrModel.getAttributeSet().getAttributes()) {
           System.out.println("attr "+attr.getName()+" "+attrModel.getAttributeSet().getValue(attr).toString());
        }

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

}

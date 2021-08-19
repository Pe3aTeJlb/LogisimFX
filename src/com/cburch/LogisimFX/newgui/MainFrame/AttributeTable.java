package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.AttributeSet;
import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.tools.Tool;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class AttributeTable extends GridPane {

    private Localizer lc = LC_gui.getInstance();

    private ColumnConstraints attr,value;

    private static Tool tool = null;
    private static Component comp = null;
    private static AttributeSet attributeSet;

    private int currRow = 1;

    private Label attrNameLbl, attrValueLbl;

    public AttributeTable(){

        super();

        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(10));
        this.setMaxWidth(Region.USE_COMPUTED_SIZE);

        attr = new ColumnConstraints();
        attr.setPercentWidth(50);

        value = new ColumnConstraints();
        value.setPercentWidth(50);

        this.getColumnConstraints().addAll(attr,value);

        setTitle();

    }

    private void setTitle(){

        attrNameLbl = new Label();
        attrNameLbl.textProperty().bind(lc.createStringBinding("attributeNameTitle"));
        this.add(attrNameLbl,0,0);

        attrValueLbl = new Label();
        attrValueLbl.textProperty().bind(lc.createStringBinding("attributeValueTitle"));
        this.add(attrValueLbl,1,0);

    }

    private void updateTable(){

        this.getChildren().clear();

        setTitle();

        System.out.println("attr size "+attributeSet.getAttributes().size());

        for (Attribute attr: attributeSet.getAttributes()) {
            currRow += 1;
            this.add(new Label(attr.getDisplayName()),0,currRow);
            this.add(attr.getCell(attributeSet.getValue(attr)), 1,currRow);

            System.out.println("attr "+attr.getName()+" "+attributeSet.getValue(attr).toString());

        }

    }

    public static void printShit(){

        for (Attribute attr: attributeSet.getAttributes()) {
            System.out.println("attr "+attr.getName()+" "+attributeSet.getValue(attr).toString());
        }

    }

    public void setTool(Tool tl){

        if(tool != tl && tl.getAttributeSet() != null) {
            tool = tl;
            comp = null;
            attributeSet = tool.getAttributeSet();
            updateTable();
        }

    }

    public void setComponent(Component cmp){

        if(comp != cmp && cmp.getAttributeSet() != null) {
            tool = null;
            comp = cmp;
            attributeSet = cmp.getAttributeSet();
            updateTable();
        }

    }

}

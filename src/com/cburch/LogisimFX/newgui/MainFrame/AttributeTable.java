package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.tools.Tool;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class AttributeTable extends GridPane {

    private Localizer lc = LC_gui.getInstance();

    private Tool tool = null;

    private int currRow = 1;

    private Label attrNameLbl, attrValueLbl;

    public AttributeTable(){

        super();

        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(20));

        setTitle();

    }

    private void setTitle(){

        attrNameLbl = new Label();
        attrNameLbl.textProperty().bind(lc.createStringBinding("attributeNameTitle"));
        this.add(attrNameLbl,0,0);

        attrValueLbl = new Label();
        attrValueLbl.textProperty().bind(lc.createStringBinding("attributeValueTitle"));
        this.add(attrValueLbl,1,0);

        this.setGridLinesVisible(true);

    }

    private void updateTable(){

        this.getChildren().clear();

        setTitle();

        System.out.println("attr size "+tool.getAttributeSet().getAttributes().size());

        for (Attribute attr: tool.getAttributeSet().getAttributes()) {
            currRow += 1;
            this.add(new Label(attr.getDisplayName()),0,currRow);
            this.add(attr.getCell(tool.getAttributeSet().getValue(attr)), 1,currRow);
            System.out.println("attr "+attr.getName());
        }

    }

    public void setTool(Tool tl){

        if(tool != tl) {
            tool = tl;
            updateTable();
        }

    }

}

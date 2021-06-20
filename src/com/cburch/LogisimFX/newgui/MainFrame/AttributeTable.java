package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.tools.Tool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AttributeTable extends TableView {

    private Localizer lc = LC_gui.getInstance();

    private TableColumn<Attribute, String> attrName;
    private TableColumn<Attribute, TableCell<Attribute, Object>> attrValue;

    private Tool tool = null;

    private ObservableList<Attribute> items;

    public AttributeTable(){

        super();

        this.setEditable(true);

        attrName = new TableColumn<>();
        attrName.textProperty().bind(lc.createStringBinding("attributeNameTitle"));
        //attrName.setEditable(false);
        //attrName.setSortable(false);
        //attrName.setResizable(false);
        attrName.setCellValueFactory(new PropertyValueFactory<>("DisplayName"));

        attrValue = new TableColumn<>();
        attrValue.textProperty().bind(lc.createStringBinding("attributeValueTitle"));
        //attrValue.setSortable(false);
        //attrValue.setResizable(false);
        //attrValue.setEditable(true);
        attrValue.setCellValueFactory(new PropertyValueFactory<>("Cell"));
        attrValue.setCellFactory(param -> {

            TableCell<Attribute,TableCell<Attribute, Object>> cell = new TableCell<Attribute,TableCell<Attribute, Object>>(){

                @Override
                protected void updateItem(TableCell<Attribute, Object> item, boolean empty) {

                    super.updateItem(item, empty);

                    if(item != null) {

                        System.out.println(" its not empty");
                        setGraphic(item);

                    } else {

                        System.out.println("lol its empty");
                        setText(null);
                        setGraphic(null);

                    }

                }

            };

            return cell;

        });


        this.getColumns().setAll(attrName,attrValue);

        items = FXCollections.observableArrayList();

    }

    private void updateTable(){

        items.clear();

        System.out.println("attr size "+tool.getAttributeSet().getAttributes().size());

        for (Attribute attr: tool.getAttributeSet().getAttributes()) {
            System.out.println("attr "+attr.getName());
        }

        items.addAll(tool.getAttributeSet().getAttributes());
        this.setItems(items);

    }

    public void setTool(Tool tl){

        if(tool != tl) {
            tool = tl;
            updateTable();
        }

    }

}

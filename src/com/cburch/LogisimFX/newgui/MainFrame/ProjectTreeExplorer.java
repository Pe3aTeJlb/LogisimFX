package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ProjectTreeExplorer extends TreeView {

    private Project proj;

    boolean showingProjectTree;

    public ProjectTreeExplorer(Project project){

        super();

        this.proj = project;

        MultipleSelectionModel<TreeItem> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty) ;

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {

                        if(item instanceof Library){
                            setText(((Library)item).getName());
                            setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
                        }
                        else if(item instanceof Tool){
                            setText(((Tool)item).getName());
                            //setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
                        }
                        else if(item instanceof LogisimFile){
                            setText(proj.getLogisimFile().getName());
                            //setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
                        }
                        else{
                            setText("???");
                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && !event.isConsumed()) {
                        System.out.println("double click");
                        event.consume();

                        if(treeItem.getValue() instanceof Circuit){
                            project.setCurrentCircuit((Circuit) treeItem.getValue());
                        }

                    }else if (event.getButton().equals(MouseButton.PRIMARY)){
                        System.out.println("Left click");

                    }else if(event.getButton().equals(MouseButton.SECONDARY)){
                        System.out.println("Right click");
                        if(treeItem.getValue() instanceof Tool){
                            //project.setCurrentCircuit((Circuit) treeItem.getValue());
                        }

                    }

                }
            });

            cell.setOnMouseEntered(event -> {
                System.out.println("entered");
            });

            return cell ;

        });




        //todo: create update binding to proj tool.size

        updateAndShowProjectTree();

    }

    //not best solution, but yes
    public void updateCurrentSections(){

        if(showingProjectTree){
            updateAndShowProjectTree();
        }else{
            updateAndShowSimulationTree();
        }

    }

    public void updateAndShowProjectTree(){

        //this.getChildren().clear();
        showingProjectTree = true;

        TreeItem<Object> root = new TreeItem<>(proj.getLogisimFile());
        root.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/projadd.gif")));
        this.setRoot(root);
        root.expandedProperty().set(true);

        for (Tool tool: proj.getLogisimFile().getTools()) {

            TreeItem<Object> l = new TreeItem<>(tool);
            l.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(l);

        }
        /*
        for (Circuit circ: proj.getLogisimFile().getCircuits()) {

            TreeItem<Object> l = new TreeItem<>(circ);
            l.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(l);

        }
*/

        for (Library lib: proj.getLogisimFile().getLibraries()) {

            TreeItem<Object> l = new TreeItem<>(lib);
            root.getChildren().add(l);

            for (Tool tool: lib.getTools()) {

                TreeItem<Object> t = new TreeItem<>(tool);
                //t.setGraphic(new ToolIcon(tool));
                l.getChildren().add(t);

            }

        }

    }

    public void updateAndShowSimulationTree(){

        //this.getChildren().clear();

        showingProjectTree = false;

        TreeItem<Circuit> root = new TreeItem<>(proj.getCurrentCircuit());
        root.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
        this.setRoot(root);
        root.expandedProperty().set(true);

        for (CircuitState cState: proj.getCircuitState().getSubstates()) {

            TreeItem<Circuit> c = new TreeItem<>(cState.getCircuit());
            c.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(c);

        }

    }


}

/*
class ToolIcon extends ImageView {

    Tool tool;
    com.cburch.logisim.circuit.Circuit circ = null;

    ToolIcon(Tool tool) {

        this.setImage(new Image(""));

        this.tool = tool;
        if (tool instanceof AddTool) {
            ComponentFactory fact = ((AddTool) tool).getFactory(false);
            if (fact instanceof SubcircuitFactory) {
                circ = ((SubcircuitFactory) fact).getSubcircuit();
            }
        }
    }

    public int getIconHeight() {
        return 20;
    }

    public int getIconWidth() {
        return 20;
    }



    public void paintIcon() {

        // draw halo if appropriate
        if (tool == haloedTool && AppPreferences.ATTRIBUTE_HALO.getBoolean()) {
            g.setColor(Canvas.HALO_COLOR);
            g.fillRoundRect(x, y, 20, 20, 10, 10);
            g.setColor(Color.BLACK);
        }

        // draw tool icon
        Graphics gIcon = g.create();
        ComponentDrawContext context = new ComponentDrawContext(ProjectExplorer.this, null, null, g, gIcon);
        tool.paintIcon(context, x, y);
        gIcon.dispose();

        // draw magnifying glass if appropriate
        if (circ == proj.getCurrentCircuit()) {
            int tx = x + 13;
            int ty = y + 13;
            int[] xp = { tx - 1, x + 18, x + 20, tx + 1 };
            int[] yp = { ty + 1, y + 20, y + 18, ty - 1 };
            g.setColor(MAGNIFYING_INTERIOR);
            g.fillOval(x + 5, y + 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawOval(x + 5, y + 5, 10, 10);
            g.fillPolygon(xp, yp, xp.length);
        }

    }

}

 */

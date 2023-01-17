package LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTab;

import LogisimFX.IconsManager;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.newgui.MainFrame.SystemTabs.AbstractTreeExplorer;
import LogisimFX.proj.Project;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;

public class SimulationExplorerTreeView extends AbstractTreeExplorer {

    private Project proj;
    private CircuitState superState;

    public SimulationExplorerTreeView(Project project){

        super();

        this.proj = project;

        MultipleSelectionModel<TreeItem> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty) ;

                    if(empty || item == null) {

                        setText(null);
                        setGraphic(null);

                    }else{

                        if(item instanceof CircuitState){

                            SubcircuitFactory buff = ((CircuitState) item).getCircuit().getSubcircuitFactory();

                            setText(buff.getName());

                            if(proj.getCurrentCircuit() == ((CircuitState) item).getCircuit()){
                                setGraphic(IconsManager.getIcon("currsubcirc.gif"));
                            }else {
                                setGraphic(buff.getIcon());
                            }

                        }
                        else{
                            setText("you fucked up2");
                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {

                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
                            && !event.isConsumed()) {

                        event.consume();

                        if(treeItem.getValue() instanceof CircuitState){
                            project.setCircuitState((CircuitState) treeItem.getValue());
                            // project.setCurrentCircuit(((CircuitState) treeItem.getValue()).getCircuit());
                        }

                    }
                }

            });

            return cell;

        });

        updateTree();

    }

    public void updateTree(){

        superState = proj.getSimulator().getCircuitState();
        //superState = proj.getCircuitState(proj.getCurrentCircuit());

        TreeItem<Object> root = new TreeItem<>(superState);
        this.setRoot(root);
        root.setExpanded(true);

        getChildren(root);

    }

    private void getChildren(TreeItem root){

        for (Component comp : ((CircuitState)root.getValue()).getCircuit().getNonWires()) {

            if (comp.getFactory() instanceof SubcircuitFactory) {

                SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
                CircuitState state = factory.getSubstate(superState, comp);

                TreeItem<Object> subRoot = new TreeItem<>(state);
                root.getChildren().add(subRoot);
                root.setExpanded(true);

                getChildren(subRoot);

            }

        }

    }

}

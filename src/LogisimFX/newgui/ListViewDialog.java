package LogisimFX.newgui;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ListViewDialog<T> extends Dialog<T> {

    private final GridPane grid;
    //private final Label label;
    private final ListView<T> listView;
    private final T defaultChoice;

    public ListViewDialog() {
        this((T)null, (T[])null);
    }

    public ListViewDialog(T defaultChoice,  @SuppressWarnings("unchecked") T... choices) {
        this(defaultChoice,
                choices == null ? Collections.emptyList() : Arrays.asList(choices));
    }

    public ListViewDialog(T defaultChoice, Collection<T> choices) {
        final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        // -- label
        //label = DialogPane.createContentLabel(dialogPane.getContentText());
       // label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        //label.textProperty().bind(dialogPane.contentTextProperty());

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("choice-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final double MIN_WIDTH = 150;

        listView = new ListView<T>();
        if (choices != null) {
            listView.getItems().addAll(choices);
        }
        listView.setMinSize(MIN_WIDTH,MIN_WIDTH);
        listView.setMaxSize(Double.MAX_VALUE,250);
        GridPane.setHgrow(listView, Priority.ALWAYS);
        GridPane.setFillWidth(listView, true);

        this.defaultChoice = listView.getItems().contains(defaultChoice) ? defaultChoice : null;

        if (defaultChoice == null) {
            listView.getSelectionModel().selectFirst();
        } else {
            listView.getSelectionModel().select(defaultChoice);
        }

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? getSelectedItem() : null;
        });

    }

    public final ObservableList<T> getSelectedItems() {
        return listView.getSelectionModel().getSelectedItems();
    }

    public final T getSelectedItem() {
        return listView.getSelectionModel().getSelectedItem();
    }

    public final ReadOnlyObjectProperty<T> selectedItemProperty() {
        return listView.getSelectionModel().selectedItemProperty();
    }

    public void setSingleSelectionModel(){listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);}

    public void setMultipleSelectionModel(){listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);}

    public final void setSelectedItem(T item) {
        listView.getSelectionModel().select(item);
    }

    public final ObservableList<T> getItems() {
        return listView.getItems();
    }

    public final T getDefaultChoice() {
        return defaultChoice;
    }

    private void updateGrid() {

        grid.getChildren().clear();

        //grid.add(label, 0, 0);
        grid.add(listView, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> listView.requestFocus());

    }

}

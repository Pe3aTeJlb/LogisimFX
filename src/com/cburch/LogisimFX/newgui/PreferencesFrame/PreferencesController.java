package com.cburch.LogisimFX.newgui.PreferencesFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import com.cburch.LogisimFX.Localizer;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PreferencesController extends AbstractController {

    @FXML
    private AnchorPane Root;

    @FXML
    private TabPane TabPane;

    private Stage stage;

    @FXML
    private Tab TemplateTab;

    @FXML
    private RadioButton EmptyTemplRB;

    @FXML
    private RadioButton PlainTemplRB;

    @FXML
    private RadioButton CustomTemplRB;

    @FXML
    private Button FilePathSelectBtn;

    @FXML
    private TextField FilePathTextField;

    private File FilePath;

    private FileChooser fileChooser = new FileChooser();




    @FXML
    private Tab InternalizationTab;

    @FXML
    private ComboBox<Label> GateShapeCmbx;

    @FXML
    private ListView<Label> LocaleListView;

    @FXML
    private Label ShapeLbl;

    @FXML
    private Label LocaleLbl;

    private final List<Locale> locales = Arrays.asList(Locale.GERMAN,
            Locale.forLanguageTag("el"),
            Locale.ENGLISH,
            Locale.forLanguageTag("es"),
            Locale.forLanguageTag("pt"),
            Locale.forLanguageTag("ru")
    );

    @FXML
    private CheckBox SpecificSymbolsChbx;




    @FXML
    private Tab LayoutTab;

    @FXML
    private CheckBox ShowTickRateChbx;

    @FXML
    private CheckBox PrinterViewChbx;

    @FXML
    private CheckBox ShowAttrHaloChbx;

    @FXML
    private CheckBox ShowComponentTipsChbx;

    @FXML
    private CheckBox KeepConnectionsChbx;

    @FXML
    private CheckBox ShowGhostChbx;

    @FXML
    private Label AfterAddingLbl;

    @FXML
    private ChoiceBox<?> AfterAddingCmbx;

    @FXML
    private Label FirstRadixLbl;

    @FXML
    private ChoiceBox<?> FirstRadixCmbx;

    @FXML
    private Label SecondRadixLbl;

    @FXML
    private ChoiceBox<?> SecondRadixCmbx;




    @FXML
    private Tab ExperimentalTab;

    @FXML
    private ComboBox<Label> AcceleratorCmbx;

    @FXML
    private Label GraphicsAccelLabel;

    @FXML
    private Label RestartLogisimLabel;




    private Localizer lc = new Localizer("LogisimFX/resources/localization/prefs");


    @FXML
    public void initialize(){

        initTemplateTab();
        initInternalizationTab();
        initLayoutTab();
        initExperimentalTab();

    }

    private void initTemplateTab(){

        TemplateTab.textProperty().bind(lc.createStringBinding("templateTitle"));

        PlainTemplRB.textProperty().bind(lc.createStringBinding("templatePlainOption"));
        PlainTemplRB.setOnAction(event -> {

            EmptyTemplRB.setSelected(false);
            CustomTemplRB.setSelected(false);

        });

        EmptyTemplRB.textProperty().bind(lc.createStringBinding("templateEmptyOption"));
        EmptyTemplRB.setOnAction(event -> {

            PlainTemplRB.setSelected(false);
            CustomTemplRB.setSelected(false);

        });

        CustomTemplRB.textProperty().bind(lc.createStringBinding("templateCustomOption"));
        CustomTemplRB.setOnAction(event -> {

            if(!FilePath.toString().equals("")){
                EmptyTemplRB.setSelected(false);
                PlainTemplRB.setSelected(false);
            }

        });

        if(FilePath == null){
            CustomTemplRB.setDisable(true);
            FilePathTextField.setText("");
        }
        else{
            FilePathTextField.setText(FilePath.toString());
        }



        FilePathSelectBtn.textProperty().bind(lc.createStringBinding("templateSelectButton"));
        FilePathSelectBtn.setOnAction(event -> {

            File f = fileChooser.showOpenDialog(Root.getScene().getWindow());

            if(f == null){

            }/**/
            else{

            }

        });

    }

    private void initInternalizationTab(){

        InternalizationTab.textProperty().bind(lc.createStringBinding("intlTitle"));

        ShapeLbl.textProperty().bind(lc.createStringBinding("intlGateShape"));
        LocaleLbl.textProperty().bind(lc.createStringBinding("intlLocale"));

        SpecificSymbolsChbx.textProperty().bind(lc.createStringBinding("intlReplaceAccents"));
        SpecificSymbolsChbx.disableProperty().bind(Localizer.localeProperty().isNotEqualTo(Locale.forLanguageTag("es")));
        SpecificSymbolsChbx.setOnAction(event -> {
            //ToDO:
            if(SpecificSymbolsChbx.isSelected()){

            }else{

            }

        });

        /*
        if(Locale.getDefault() == Locale.forLanguageTag("es")){
                SpecificSymbolsChbx.setDisable(false);
        }else{
                SpecificSymbolsChbx.setDisable(true);
        }
*/

        ObservableList<Label> gateShapeLabels = FXCollections.observableArrayList();

        Label Shaped = new Label();
        Shaped.textProperty().bind(lc.createStringBinding("shapeShaped"));
        Shaped.setOnMouseClicked(event -> {
            //ToDO:
        });

        Label Rectangular = new Label();
        Rectangular.textProperty().bind(lc.createStringBinding("shapeRectangular"));
        Rectangular.setOnMouseClicked(event -> {
            //ToDO:
        });

        Label DIN40700 = new Label();
        DIN40700.textProperty().bind(lc.createStringBinding("shapeDIN40700"));
        DIN40700.setOnMouseClicked(event -> {
            //ToDO:
        });

        gateShapeLabels.addAll(
                Shaped,
                Rectangular,
                DIN40700
        );

        GateShapeCmbx.setItems(gateShapeLabels);

        //ToDO: add default value
        //GateShapeCmbx.setValue();



        ObservableList<Label> localeLabels = FXCollections.observableArrayList();

        for (Locale l: locales) {

            Label lb = new Label();

            if(l != Locale.getDefault()){
                lb.setText(l.getDisplayName(l)
                        + " / " + l.getDisplayName(Locale.getDefault()));
            }
            else{
                lb.setText(l.getDisplayName(l));
            }

            lb.setOnMouseClicked(event -> { Localizer.setLocale(l); });

            localeLabels.add(lb);
        }

        LocaleListView.setItems(localeLabels);

    }

    private void initLayoutTab(){

        LayoutTab.textProperty().bind(lc.createStringBinding("layoutTitle"));

        ShowTickRateChbx.textProperty().bind(lc.createStringBinding("windowTickRate"));
        //ShowTickRateChbx.setDisable(AppPreferences.SHOW_TICK_RATE.getBoolean());
        ShowTickRateChbx.setOnAction(event -> {});

        PrinterViewChbx.textProperty().bind(lc.createStringBinding("layoutPrinterView"));
        //PrinterViewChbx.setDisable();
        PrinterViewChbx.setOnAction(event -> {});

        ShowAttrHaloChbx.textProperty().bind(lc.createStringBinding("layoutAttributeHalo"));
        //ShowAttrHaloChbx.setDisable();
        ShowAttrHaloChbx.setOnAction(event -> {});

        ShowComponentTipsChbx.textProperty().bind(lc.createStringBinding("layoutShowTips"));
        //ShowComponentTipsChbx.setDisable();
        ShowComponentTipsChbx.setOnAction(event -> {});

        KeepConnectionsChbx.textProperty().bind(lc.createStringBinding("layoutMoveKeepConnect"));
        //KeepConnectionsChbx.setDisable();
        KeepConnectionsChbx.setOnAction(event -> {});

        ShowGhostChbx.textProperty().bind(lc.createStringBinding("layoutAddShowGhosts"));
        //ShowGhostChbx.setDisable();
        ShowGhostChbx.setOnAction(event -> {});



        AfterAddingLbl.textProperty().bind(lc.createStringBinding("layoutAddAfter"));

        //AfterAddingCmbx



        FirstRadixLbl.textProperty().bind(lc.createStringBinding("layoutRadix1"));

        //FirstRadixCmbx



        SecondRadixLbl.textProperty().bind(lc.createStringBinding("layoutRadix2"));

        //SecondRadixCmbx


    }

    private void initExperimentalTab(){

        ExperimentalTab.textProperty().bind(lc.createStringBinding("experimentTitle"));

        GraphicsAccelLabel.textProperty().bind(lc.createStringBinding("accelLabel"));
        RestartLogisimLabel.textProperty().bind(lc.createStringBinding("accelRestartLabel"));

        ObservableList<Label> accels = FXCollections.observableArrayList();

        Label Default = new Label();
        Default.textProperty().bind(lc.createStringBinding("shapeShaped"));
        Default.setOnMouseClicked(event -> {
            //ToDO:
        });

        Label None = new Label();
        None.textProperty().bind(lc.createStringBinding("accelNone"));
        None.setOnMouseClicked(event -> {
            //ToDO:
        });

        Label OpenGL = new Label();
        OpenGL.textProperty().bind(lc.createStringBinding("accelOpenGL"));
        OpenGL.setOnMouseClicked(event -> {
            //ToDO:
        });

        Label Direct3D = new Label();
        Direct3D.textProperty().bind(lc.createStringBinding("accelD3D"));
        Direct3D.setOnMouseClicked(event -> {
            //ToDO:
        });

        accels.addAll(
                Default,
                None,
                OpenGL,
                Direct3D
        );

        AcceleratorCmbx.getItems().addAll(accels);
        //ToDO:
        //AcceleratorCmbx.setValue();

    }

    @Override
    public void prepareFrame(Stage s) {
        stage = s;
        setStageTitle();
    }

    public void setStageTitle(){
        stage.titleProperty().bind(lc.createStringBinding("preferencesFrameTitle"));
    }

    @Override
    public void onClose() {

    }

}

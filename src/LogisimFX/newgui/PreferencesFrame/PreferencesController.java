/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.PreferencesFrame;

import LogisimFX.localization.LocaleManager;
import LogisimFX.newgui.AbstractController;
import LogisimFX.circuit.RadixOption;
import LogisimFX.file.Loader;
import LogisimFX.file.LoaderException;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.DialogManager;
import LogisimFX.util.StringUtil;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.localization.Localizer;
import LogisimFX.prefs.Template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PreferencesController extends AbstractController {

    //Root
    private Stage stage;

    @FXML
    private AnchorPane Root;



    //Template tab
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


    //Internationalization tab
    @FXML
    private Tab InternalizationTab;

    @FXML
    private ComboBox<PrefOption> GateShapeCmbx;

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



    //Layout tab
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
    private ComboBox<PrefOption> AfterAddingCmbx;

    @FXML
    private Label FirstRadixLbl;

    @FXML
    private ComboBox<PrefOption> FirstRadixCmbx;

    @FXML
    private Label SecondRadixLbl;

    @FXML
    private ComboBox<PrefOption> SecondRadixCmbx;



    //Experimental tab
    @FXML
    private Tab ExperimentalTab;

    @FXML
    private ComboBox<PrefOption> AcceleratorCmbx;

    @FXML
    private Label GraphicsAccelLabel;

    @FXML
    private Label RestartLogisimLabel;



    @FXML
    public void initialize(){

        initTemplateTab();
        initInternalizationTab();
        initLayoutTab();
        initExperimentalTab();

    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("preferencesFrameTitle"));

        stage.setResizable(false);

    }

    private void initTemplateTab(){

        TemplateTab.textProperty().bind(LC.createStringBinding("templateTitle"));

        PlainTemplRB.textProperty().bind(LC.createStringBinding("templatePlainOption"));
        PlainTemplRB.setOnAction(event -> {

            EmptyTemplRB.setSelected(false);
            CustomTemplRB.setSelected(false);
            AppPreferences.setTemplateType(AppPreferences.TEMPLATE_PLAIN);

        });

        EmptyTemplRB.textProperty().bind(LC.createStringBinding("templateEmptyOption"));
        EmptyTemplRB.setOnAction(event -> {

            PlainTemplRB.setSelected(false);
            CustomTemplRB.setSelected(false);
            AppPreferences.setTemplateType(AppPreferences.TEMPLATE_EMPTY);

        });

        CustomTemplRB.textProperty().bind(LC.createStringBinding("templateCustomOption"));
        CustomTemplRB.setOnAction(event -> {

            if(!FilePathTextField.getText().equals("")){
                EmptyTemplRB.setSelected(false);
                PlainTemplRB.setSelected(false);
                AppPreferences.setTemplateType(AppPreferences.TEMPLATE_CUSTOM);
            }

        });

        if(AppPreferences.getTemplateFile() == null || AppPreferences.getTemplateFile().toString().equals("")){
            CustomTemplRB.setDisable(true);
            FilePathTextField.setText("");
        }
        else{
            FilePathTextField.setText(AppPreferences.getTemplateFile().toString());
        }

        FilePathSelectBtn.textProperty().bind(LC.createStringBinding("templateSelectButton"));
        FilePathSelectBtn.setOnAction(event -> {

            File f = fileChooser.showOpenDialog(Root.getScene().getWindow());

            FileInputStream reader = null;
            InputStream reader2 = null;

            if(f != null){

                try {
                    Loader loader = new Loader();
                    reader = new FileInputStream(f);
                    Template template = Template.create(reader);
                    reader2 = template.createStream();
                    LogisimFile.load(reader2, loader); // to see if OK

                    AppPreferences.setTemplateFile(f, template);
                    AppPreferences.setTemplateType(AppPreferences.TEMPLATE_CUSTOM);

                    if(AppPreferences.getTemplateFile() != null)FilePathTextField.setText(AppPreferences.getTemplateFile().toString());
                    PlainTemplRB.setSelected(false);
                    EmptyTemplRB.setSelected(false);
                    CustomTemplRB.setSelected(true);

                } catch (LoaderException ex) {

                }
                catch (IOException ex) {
                    DialogManager.CreateErrorDialog(LC.get("templateErrorTitle"), StringUtil.format(LC.get("templateErrorMessage"), ex.toString()));
                } finally {
                    try {
                        if (reader != null) reader.close();
                    } catch (IOException ex) { }
                    try {
                        if (reader != null) reader2.close();
                    } catch (IOException ex) { }
                }

            }

        });

        switch (AppPreferences.getTemplateType()) {
            case AppPreferences.TEMPLATE_PLAIN: PlainTemplRB.setSelected(true); break;
            case AppPreferences.TEMPLATE_EMPTY: EmptyTemplRB.setSelected(true); break;
            case AppPreferences.TEMPLATE_CUSTOM: CustomTemplRB.setSelected(true); break;
        }

    }

    private void initInternalizationTab(){

        InternalizationTab.textProperty().bind(LC.createStringBinding("intlTitle"));

        ShapeLbl.textProperty().bind(LC.createStringBinding("intlGateShape"));
        LocaleLbl.textProperty().bind(LC.createStringBinding("intlLocale"));

        SpecificSymbolsChbx.textProperty().bind(LC.createStringBinding("intlReplaceAccents"));
        SpecificSymbolsChbx.disableProperty().bind(LocaleManager.localeProperty().isNotEqualTo(Locale.forLanguageTag("es")));
        SpecificSymbolsChbx.setSelected(AppPreferences.ACCENTS_REPLACE.get());
        SpecificSymbolsChbx.setOnAction(event -> AppPreferences.ACCENTS_REPLACE.set(SpecificSymbolsChbx.isSelected()));

        ObservableList<PrefOption> gateShapeLabels = FXCollections.observableArrayList();

        gateShapeLabels.addAll(
                new PrefOption(AppPreferences.SHAPE_SHAPED,
                        LC.createStringBinding("shapeShaped")),
                new PrefOption(AppPreferences.SHAPE_RECTANGULAR,
                        LC.createStringBinding("shapeRectangular")),
                new PrefOption(AppPreferences.SHAPE_DIN40700,
                        LC.createStringBinding("shapeDIN40700"))
        );


        GateShapeCmbx.setCellFactory(lv -> new TranslationCell());
        GateShapeCmbx.setButtonCell(new TranslationCell());

        GateShapeCmbx.setItems(gateShapeLabels);

        //default value
        for (PrefOption pref: GateShapeCmbx.getItems()) {

            if (pref.getValue().equals(AppPreferences.GATE_SHAPE.get())) {
                GateShapeCmbx.setValue(pref);
                break;
            }

        }

        GateShapeCmbx.setOnAction(event -> AppPreferences.GATE_SHAPE.set(GateShapeCmbx.getValue().getValue().toString()));


        //set locales list
        ObservableList<Label> localeLabels = FXCollections.observableArrayList();

        for (Locale l: locales) {

            Label lb = new Label();

            lb.textProperty().bind(LocaleManager.getComplexTitleForLocale(l));

            lb.setOnMouseClicked(event -> LocaleManager.setLocale(l));

            localeLabels.add(lb);

        }

        LocaleListView.setItems(localeLabels);

    }

    private void initLayoutTab(){

        LayoutTab.textProperty().bind(LC.createStringBinding("layoutTitle"));

        ShowTickRateChbx.textProperty().bind(LC.createStringBinding("windowTickRate"));
        ShowTickRateChbx.setSelected(AppPreferences.SHOW_TICK_RATE.get());
        ShowTickRateChbx.setOnAction(event -> AppPreferences.SHOW_TICK_RATE.set(ShowTickRateChbx.isSelected()));

        PrinterViewChbx.textProperty().bind(LC.createStringBinding("layoutPrinterView"));
        PrinterViewChbx.setSelected(AppPreferences.PRINTER_VIEW.get());
        PrinterViewChbx.setOnAction(event -> AppPreferences.PRINTER_VIEW.set(PrinterViewChbx.isSelected()));

        ShowAttrHaloChbx.textProperty().bind(LC.createStringBinding("layoutAttributeHalo"));
        ShowAttrHaloChbx.setSelected(AppPreferences.ATTRIBUTE_HALO.get());
        ShowAttrHaloChbx.setOnAction(event -> AppPreferences.ATTRIBUTE_HALO.set(ShowAttrHaloChbx.isSelected()));

        ShowComponentTipsChbx.textProperty().bind(LC.createStringBinding("layoutShowTips"));
        ShowComponentTipsChbx.setSelected(AppPreferences.COMPONENT_TIPS.get());
        ShowComponentTipsChbx.setOnAction(event -> AppPreferences.COMPONENT_TIPS.set(ShowComponentTipsChbx.isSelected()));

        KeepConnectionsChbx.textProperty().bind(LC.createStringBinding("layoutMoveKeepConnect"));
        KeepConnectionsChbx.setSelected(AppPreferences.MOVE_KEEP_CONNECT.get());
        KeepConnectionsChbx.setOnAction(event -> AppPreferences.MOVE_KEEP_CONNECT.set(KeepConnectionsChbx.isSelected()));

        ShowGhostChbx.textProperty().bind(LC.createStringBinding("layoutAddShowGhosts"));
        ShowGhostChbx.setSelected(AppPreferences.ADD_SHOW_GHOSTS.get());
        ShowGhostChbx.setOnAction(event -> AppPreferences.ADD_SHOW_GHOSTS.set(ShowGhostChbx.isSelected()));



        AfterAddingLbl.textProperty().bind(LC.createStringBinding("layoutAddAfter"));

        ObservableList<PrefOption> afterAdd = FXCollections.observableArrayList();

        afterAdd.addAll(

                new PrefOption(AppPreferences.ADD_AFTER_UNCHANGED,
                        LC.createStringBinding("layoutAddAfterUnchanged")),
                new PrefOption(AppPreferences.ADD_AFTER_EDIT,
                        LC.createStringBinding("layoutAddAfterEdit"))
        );

        AfterAddingCmbx.setCellFactory(lv -> new TranslationCell());
        AfterAddingCmbx.setButtonCell(new TranslationCell());

        AfterAddingCmbx.setItems(afterAdd);

        //default value
        for (PrefOption pref: AfterAddingCmbx.getItems()) {

            if (pref.getValue().equals(AppPreferences.ADD_AFTER.get())) {
                AfterAddingCmbx.setValue(pref);
                break;
            }

        }

        AfterAddingCmbx.setOnAction(event -> AppPreferences.ADD_AFTER.set(AfterAddingCmbx.getValue().getValue().toString()));

        //Radix section preparation
        RadixOption[] opts = RadixOption.OPTIONS;
        PrefOption[] items = new PrefOption[opts.length];

        for (int j = 0; j < RadixOption.OPTIONS.length; j++) {
            items[j] = new PrefOption(opts[j].getSaveString(), opts[j].getDisplayGetter());
        }

        ObservableList<PrefOption> radix = FXCollections.observableArrayList();

        radix.addAll(items);


        FirstRadixLbl.textProperty().bind(LC.createStringBinding("layoutRadix1"));

        FirstRadixCmbx.setCellFactory(lv -> new TranslationCell());
        FirstRadixCmbx.setButtonCell(new TranslationCell());

        FirstRadixCmbx.setItems(radix);

        //default value
        for (PrefOption pref: FirstRadixCmbx.getItems()) {

            if (pref.getValue().equals(AppPreferences.POKE_WIRE_RADIX1.get())) {
                FirstRadixCmbx.setValue(pref);
                break;
            }

        }

        FirstRadixCmbx.setOnAction(event -> AppPreferences.POKE_WIRE_RADIX1.set(FirstRadixCmbx.getValue().getValue().toString()));



        SecondRadixLbl.textProperty().bind(LC.createStringBinding("layoutRadix2"));

        //SecondRadixCmbx
        SecondRadixCmbx.setCellFactory(lv -> new TranslationCell());
        SecondRadixCmbx.setButtonCell(new TranslationCell());

        SecondRadixCmbx.setItems(radix);

        //default value
        for (PrefOption pref: SecondRadixCmbx.getItems()) {

            if (pref.getValue().equals(AppPreferences.POKE_WIRE_RADIX2.get())) {
                SecondRadixCmbx.setValue(pref);
                break;
            }

        }

        SecondRadixCmbx.setOnAction(event -> AppPreferences.POKE_WIRE_RADIX2.set(SecondRadixCmbx.getValue().getValue().toString()));

    }

    private void initExperimentalTab(){

        ExperimentalTab.textProperty().bind(LC.createStringBinding("experimentTitle"));

        GraphicsAccelLabel.textProperty().bind(LC.createStringBinding("accelLabel"));
        RestartLogisimLabel.textProperty().bind(LC.createStringBinding("accelRestartLabel"));

        ObservableList<PrefOption> accels = FXCollections.observableArrayList();

        accels.addAll(
                new PrefOption(AppPreferences.ACCEL_DEFAULT, LC.createStringBinding("accelDefault")),
                new PrefOption(AppPreferences.ACCEL_NONE, LC.createStringBinding("accelNone")),
                new PrefOption(AppPreferences.ACCEL_OPENGL, LC.createStringBinding("accelOpenGL")),
                new PrefOption(AppPreferences.ACCEL_D3D, LC.createStringBinding("accelD3D"))
        );

        AcceleratorCmbx.setCellFactory(lv -> new TranslationCell());
        AcceleratorCmbx.setButtonCell(new TranslationCell());

        AcceleratorCmbx.setItems(accels);

        //default value
        for (PrefOption pref: AcceleratorCmbx.getItems()) {

            if (pref.getValue().equals(AppPreferences.GRAPHICS_ACCELERATION.get())) {
                AcceleratorCmbx.setValue(pref);
                break;
            }

        }

        AcceleratorCmbx.setOnAction(event -> AppPreferences.GRAPHICS_ACCELERATION.set(AcceleratorCmbx.getValue().getValue().toString()));

    }


    @Override
    public void onClose() {
    }

}

class TranslationCell extends ListCell<PrefOption> {

    @Override
    protected void updateItem(PrefOption item, boolean empty) {

        super.updateItem(item, empty);
        textProperty().unbind();

        if (empty || item == null) {
            setText("");
        } else {
            textProperty().bind(item.getBinding());
        }

    }

}
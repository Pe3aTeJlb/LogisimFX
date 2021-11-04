package LogisimFX.newgui.ExportImageFrame;

import LogisimFX.FileSelector;
import LogisimFX.circuit.Circuit;
import LogisimFX.data.Bounds;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.PrintFrame.PrintCanvas;
import LogisimFX.proj.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.image.*;
import java.io.File;

public class ExportImageController extends AbstractController {

    private Stage stage;

    @FXML
    private Label CircuitsLbl;

    @FXML
    private ListView<Circuit> CircuitsLstVw;


    @FXML
    private Label FormatLbl;

    @FXML
    private RadioButton PngRb;

    @FXML
    private RadioButton GifRb;

    @FXML
    private RadioButton JpegRb;


    @FXML
    private CheckBox PrintViewChkBx;

    @FXML
    private Label PrintViewLbl;


    @FXML
    private Button OkBtn;

    @FXML
    private Button CancelBtn;

    private ObservableList<Circuit> circuits;
    private MultipleSelectionModel<Circuit> circSelectionModel;
    private Project proj;

    private FileSelector fileSelector;

    private PrintCanvas canvas;

    private String extension;

    @FXML
    public void initialize(){

        CircuitsLbl.textProperty().bind(LC.createStringBinding("labelCircuits"));

        FormatLbl.textProperty().bind(LC.createStringBinding("labelImageFormat"));

        PngRb.setOnAction(event -> {
            GifRb.setSelected(false);
            JpegRb.setSelected(false);
            fileSelector.setPngFilter();
            extension = ".png";
        });

        GifRb.setOnAction(event -> {
            PngRb.setSelected(false);
            JpegRb.setSelected(false);
            fileSelector.setGifFilter();
            extension = ".gif";
        });

        JpegRb.setOnAction(event -> {
            PngRb.setSelected(false);
            GifRb.setSelected(false);
            fileSelector.setJpgFilter();
            extension = ".jpg";
        });


        PrintViewLbl.textProperty().bind(LC.createStringBinding("labelPrinterView"));

        OkBtn.setText("Ok");
        OkBtn.setOnAction(event -> exportImage());

        CancelBtn.setText("Cancel");
        CancelBtn.setOnAction(event -> stage.close());

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("exportImageSelect"));
        stage.setHeight(325);
        stage.setWidth(375);

        stage.setResizable(false);

        fileSelector = new FileSelector(stage);

        proj = project;

        circuits = FXCollections.observableArrayList();

        boolean includeEmpty = true;

        circSelectionModel = CircuitsLstVw.getSelectionModel();
        circSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        LogisimFile file = proj.getLogisimFile();
        Circuit current = proj.getCurrentCircuit();

        boolean currentFound = false;

        for (Circuit circ : file.getCircuits()) {
            if (!includeEmpty || circ.getBounds() != Bounds.EMPTY_BOUNDS) {
                if (circ == current) currentFound = true;
                circuits.add(circ);
            }
        }

        CircuitsLstVw.setItems(circuits);

        if (currentFound) CircuitsLstVw.getSelectionModel().select(current);

        canvas = new PrintCanvas(Screen.getPrimary().getBounds().getWidth(),
                Screen.getPrimary().getBounds().getHeight(), proj);

    }

    private void exportImage(){

        File dest;

        if (circSelectionModel.getSelectedItems().size() > 1) {
            dest = fileSelector.chooseDirectory(LC.get("exportImageDirectorySelect"));
        } else {

            if(PngRb.isSelected()){
                dest = fileSelector.SavePngFile();
            }else if(GifRb.isSelected()){
                dest = fileSelector.SaveGifFile();
            }else if(JpegRb.isSelected()) {
                dest = fileSelector.SaveJpgFile();
            }else{
                dest = null;
            }

        }

        if(dest != null){
            for (Circuit circ : circSelectionModel.getSelectedItems()) {

                ImageView img = canvas.getImage(circ, PrintViewChkBx.isSelected());

                File where;
                if (dest.isDirectory()) {
                    where = new File(dest, circ.getName()+extension);
               // } else if (filter.accept(dest)) {
                    //where = dest;
                } else {
                    String newName = dest.getName();
                    where = new File(dest.getParentFile(), newName);
                }

                try {

                    BufferedImage bImage = SwingFXUtils.fromFXImage(img.getImage(), null);

                    if(PngRb.isSelected()){
                        ImageIO.write(bImage, "PNG", where);
                        //GifEncoder.toFile(img, where);
                    }else if(GifRb.isSelected()){
                        ImageIO.write(bImage, "GIF", where);
                    }else if(JpegRb.isSelected()){

                        int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
                        ColorModel RGB_OPAQUE =
                                new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

                        PixelGrabber pg = new PixelGrabber(bImage, 0, 0, -1, -1, true);
                        pg.grabPixels();
                        int width = pg.getWidth(), height = pg.getHeight();

                        DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
                        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
                        BufferedImage bi = new BufferedImage(RGB_OPAQUE, raster, false, null);

                        ImageIO.write(bi, "JPEG", where);

                    }

                } catch (Exception e) {
                    DialogManager.CreateErrorDialog(LC.get("couldNotCreateFile"), LC.get("couldNotCreateFile"));
                    stage.close();
                    return;
                }
            }
            stage.close();
        }

    }

    @Override
    public void onClose() {
        System.out.println("Export image closed");
    }
}

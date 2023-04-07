/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.ExportImageFrame;

import LogisimFX.FileSelector;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.Simulator;
import LogisimFX.circuit.SimulatorEvent;
import LogisimFX.circuit.SimulatorListener;
import LogisimFX.data.Bounds;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.PrintFrame.PrintCanvas;
import LogisimFX.proj.Project;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.*;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class ExportImageController extends AbstractController implements SimulatorListener{

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
    private RadioButton JpegRb;

    @FXML
    private RadioButton GifRb;

    @FXML
    private TextField TicksTxtFld;


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

    private Thread exportThread;
    private CountDownLatch latch = new CountDownLatch(1);
    private DrawTask drawTask;

    private String extension;

    PrintCanvas canvas;

    @FXML
    public void initialize(){

        CircuitsLbl.textProperty().bind(LC.createStringBinding("labelCircuits"));

        FormatLbl.textProperty().bind(LC.createStringBinding("labelImageFormat"));

        PngRb.setOnAction(event -> {
            GifRb.setSelected(false);
            TicksTxtFld.setDisable(true);
            JpegRb.setSelected(false);
            fileSelector.setPngFilter();
            extension = ".png";
        });

        JpegRb.setOnAction(event -> {
            PngRb.setSelected(false);
            GifRb.setSelected(false);
            TicksTxtFld.setDisable(true);
            fileSelector.setJpgFilter();
            extension = ".jpg";
        });

        GifRb.setOnAction(event -> {
            PngRb.setSelected(false);
            JpegRb.setSelected(false);
            TicksTxtFld.setDisable(false);
            fileSelector.setGifFilter();
            extension = ".gif";
        });

        final Pattern pattern = Pattern.compile("^([1-9]{0,1}[0-9]{0,2}$){0,1}");
        TextFormatter<?> formatter = new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change; // allow this change to happen
            } else {
                return null; // prevent change
            }
        });

        TicksTxtFld.setTextFormatter(formatter);
        TicksTxtFld.setPromptText("1");
        TicksTxtFld.setDisable(true);

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

        drawTask = new DrawTask(Screen.getPrimary().getBounds().getWidth(),
                Screen.getPrimary().getBounds().getHeight(), proj, latch);

    }

    private void exportImage(){

        File dest;
        int ticks = 0;

        if (circSelectionModel.getSelectedItems().size() > 1) {
            dest = fileSelector.chooseDirectory(LC.get("exportImageDirectorySelect"));
        } else {
            if(PngRb.isSelected()){
                dest = fileSelector.SavePngFile();
            }else if(GifRb.isSelected()){
                dest = fileSelector.SaveGifFile();
                ticks = Integer.parseInt(TicksTxtFld.getText());
            }else if(JpegRb.isSelected()) {
                dest = fileSelector.SaveJpgFile();
            }else{
                dest = null;
            }
        }

        if(dest != null){

            if(PngRb.isSelected()){
                exportPng(dest);
            }else if(JpegRb.isSelected()){
                exportJpg(dest);
            }else if(GifRb.isSelected()){
                exportGif(dest, ticks);
            }

        }

    }

    private void exportPng(File dest){

        ExportPngTask exportPngTask = new ExportPngTask(dest);
        exportPngTask.setOnSucceeded(workerStateEvent ->  stage.close());

        exportThread = new Thread(exportPngTask);

        DialogManager.createProgressDialog(exportPngTask);

        exportThread.start();

    }

    private void exportJpg(File dest){

        ExportJpgTask exportJpgTask = new ExportJpgTask(dest);
        exportJpgTask.setOnSucceeded(workerStateEvent ->  stage.close());

        exportThread = new Thread(exportJpgTask);

        DialogManager.createProgressDialog(exportJpgTask);

        exportThread.start();

    }

    private void exportGif(File dest, int ticks){

        ExportGifTask exportGifTask = new ExportGifTask(dest, ticks);
        exportGifTask.setOnSucceeded(workerStateEvent ->  stage.close());

        exportThread = new Thread(exportGifTask);

        DialogManager.createProgressDialog(exportGifTask);

        exportThread.start();

    }

    @Override
    public void propagationCompleted(SimulatorEvent e) {

    }

    @Override
    public void tickCompleted(SimulatorEvent e) {

    }

    @Override
    public void simulatorStateChanged(SimulatorEvent e) {

    }


    public class ExportPngTask extends Task<Void> {

        ImageView img;
        File dest;

        public ExportPngTask(File dest){
            this.dest = dest;
        }

        @Override
        protected Void call() throws InterruptedException {

            for (Circuit circ : circSelectionModel.getSelectedItems()) {

                this.updateMessage(LC.getFormatted("exportFileHeader", circ.getName() + extension));

                drawTask.UpdateDrawTask(circ, PrintViewChkBx.isSelected(), latch);

                Platform.runLater(() -> drawTask.call());

                latch.await();

                img = drawTask.img;

                File where;
                if (dest.isDirectory()) {
                    where = new File(dest, circ.getName() + extension);
                    // } else if (filter.accept(dest)) {
                    //where = dest;
                } else {
                    String newName = dest.getName();
                    where = new File(dest.getParentFile(), newName);
                }

                try {
                    BufferedImage bImage = SwingFXUtils.fromFXImage(img.getImage(), null);
                    ImageIO.write(bImage, "PNG", where);
                } catch (Exception e) {
                    DialogManager.createErrorDialog(LC.get("couldNotCreateFile"), LC.get("couldNotCreateFile"));
                    stage.close();
                }

                latch = new CountDownLatch(1);

            }

            return null;

        }

    }

    public class ExportJpgTask extends Task<Void> {

        ImageView img;
        File dest;

        public ExportJpgTask(File dest) {
            this.dest = dest;
        }

        @Override
        protected Void call() throws InterruptedException {

            for (Circuit circ : circSelectionModel.getSelectedItems()) {

                this.updateMessage(LC.getFormatted("exportFileHeader", circ.getName() + extension));

                drawTask.UpdateDrawTask(circ, PrintViewChkBx.isSelected(), latch);

                Platform.runLater(() -> drawTask.call());

                latch.await();

                img = drawTask.img;

                File where;
                if (dest.isDirectory()) {
                    where = new File(dest, circ.getName() + extension);
                    // } else if (filter.accept(dest)) {
                    //where = dest;
                } else {
                    String newName = dest.getName();
                    where = new File(dest.getParentFile(), newName);
                }

                try {

                    BufferedImage bImage = SwingFXUtils.fromFXImage(img.getImage(), null);
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

                } catch (Exception e) {

                    DialogManager.createErrorDialog(LC.get("couldNotCreateFile"), LC.get("couldNotCreateFile"));
                    stage.close();

                }

                latch = new CountDownLatch(1);

            }

            return null;

        }

    }

    public class ExportGifTask extends Task<Void> implements SimulatorListener {

        ImageView img;
        File dest;
        int ticks;

        ImageOutputStream output;
        GifSequenceWriter writer;

        CountDownLatch tickLatch = new CountDownLatch(1);

        Simulator sim;

        public ExportGifTask(File dest, int ticks){
            this.dest = dest;
            this.ticks = ticks;
            sim = proj.getSimulator();
            sim.addSimulatorListener(this);
        }

        @Override
        protected Void call() {

            for (Circuit circ : circSelectionModel.getSelectedItems()) {

                this.updateMessage(LC.getFormatted("exportFileHeader", circ.getName() + extension));

                drawTask.UpdateDrawTask(circ, PrintViewChkBx.isSelected(), latch);

                //Make dest
                File where;
                if (dest.isDirectory()) {
                    where = new File(dest, circ.getName()+extension);
                    // } else if (filter.accept(dest)) {
                    //where = dest;
                } else {
                    String newName = dest.getName();
                    where = new File(dest.getParentFile(), newName);
                }

                //Reset sim
                if (sim != null) {
                    if(sim.isTicking())sim.setIsTicking(false);
                    sim.requestReset();
                }


                try {

                    for(int i = 0; i < ticks * 2; i++){

                        if (sim != null) sim.tick();
                        tickLatch.await();

                        Platform.runLater(() -> drawTask.call());
                        latch.await();

                        img = drawTask.img;

                        BufferedImage bImage = SwingFXUtils.fromFXImage(img.getImage(), null);

                        if(i == 0){
                            output = new FileImageOutputStream(where);
                            writer = new GifSequenceWriter(output, bImage.getType(), 250, true);
                        }

                        writer.writeToSequence(bImage);

                        latch = new CountDownLatch(1);
                        drawTask.UpdateLatch(latch);
                        tickLatch = new CountDownLatch(1);

                    }

                    writer.close();
                    output.close();

                } catch (Exception e) {
                    DialogManager.createErrorDialog(LC.get("couldNotCreateFile"), LC.get("couldNotCreateFile"));
                    stage.close();
                }

                latch = new CountDownLatch(1);
                tickLatch = new CountDownLatch(1);

            }

            return null;

        }

        @Override
        public void propagationCompleted(SimulatorEvent e) {

        }

        @Override
        public void tickCompleted(SimulatorEvent e) {
            tickLatch.countDown();
        }

        @Override
        public void simulatorStateChanged(SimulatorEvent e) {

        }

    }

    @Override
    public void onClose() {
        if(exportThread != null)exportThread.interrupt();
    }

}

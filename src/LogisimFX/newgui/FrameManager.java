/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui;

import LogisimFX.IconsManager;
import LogisimFX.circuit.Circuit;
import LogisimFX.data.Bounds;
import LogisimFX.file.Loader;
import LogisimFX.file.LogisimFile;
import LogisimFX.localization.LC_gui;
import LogisimFX.newgui.AnalyzeFrame.AnalyzeController;
import LogisimFX.newgui.CircuitStatisticFrame.CircuitStatisticController;
import LogisimFX.newgui.HelpFrame.HelpController;
import LogisimFX.newgui.HexEditorFrame.HexEditorController;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectActions;

import LogisimFX.std.memory.MemContents;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/*
FrameManager controls all frames generated during program execution.
These classes implements AbstractController in purpose to postInitialization process (title binding etc.) and frame duplicating blocking.
*/

public class FrameManager {

    private static FXMLLoader loader;

    /*
    A reference to the current AbstractController, if you need to call an additional initialization method (not postInit).
    Guaranteed to work only within the Create...Frame, I can't guarantee the rest
     */
    private static AbstractController curr;

    private static final HashMap<Project, Data> OpenedMainFrames = new HashMap<>();

    private static final HashMap<String, Data> OpenedProjectIndependentFrames = new HashMap<>();

    private static boolean isStartup = true;

    private static class Data{

        public Stage stage;
        public AbstractController controller;
        public Boolean isStartup;

        public HashMap<String, Stage> ProjectAssociatedFrames = new HashMap<>();
        public HashMap<Circuit, Stage> AssociatedCircuitStatisticFrames = new HashMap<>();
        public HashMap<MemContents, Stage> AssociatedMemoryEditors = new HashMap<>();

        Data(Stage s, AbstractController c, Boolean i){
            this.stage = s;
            this.controller = c;
            this.isStartup = i;
        }

    }


    //MainFrame

    public static void CreateMainFrame(Project proj){

        loader = new FXMLLoader(ClassLoader.getSystemResource(
                "LogisimFX/newgui/MainFrame/LogisimFx.fxml"));
        Parent root = null;

        try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

        Stage newStage = new Stage();
        newStage.setScene(new Scene(root, 800, 600));

        newStage.getIcons().add(IconsManager.LogisimFX);

        AbstractController c = loader.getController();
        c.postInitialization(newStage, proj);

        newStage.setOnHiding(event -> {

                event.consume();

                //Todo: possible problem on exit https://stackoverflow.com/questions/46053974/using-platform-exit-and-system-exitint-together

                if (proj.isFileDirty()){

                    int type = DialogManager.createConfirmCloseDialog(proj);

                    if (type == 2) {

                        ProjectActions.doSave(proj);

                        CloseFrame(proj);

                    } else if (type == 1) {

                        CloseFrame(proj);

                    } else if (type == 0) {

                        newStage.showAndWait();

                    }

                }else{
                    CloseFrame(proj);
                }


            });
        newStage.show();

        OpenedMainFrames.put(proj, new Data(newStage,c,isStartup));
        isStartup = false;

        //Close startup frame when u load project from file
        if(OpenedMainFrames.size() > 1){

            // at this moment OMF contains startup frame and another project frame
            for (Project p: OpenedMainFrames.keySet()) {
                if(!p.isFileDirty() && OpenedMainFrames.get(p).isStartup) {
                    p.getFrameController().getStage().close();
                    break;
                }
            }

        }

    }

    //Ctrl-N
    public static void SpamNew(Project proj){

        for (Project p: OpenedMainFrames.keySet()) {
            OpenedMainFrames.get(p).isStartup = false;
        }

        CreateMainFrame(proj);

    }

    public static void ReloadFrame(Project proj){

        OpenedMainFrames.remove(proj);

        CreateMainFrame(proj);

    }

    //Project-depending frames

    private static void CreateNewFrame(String resourcePath, Project proj, Modality modality){

        if(!OpenedMainFrames.get(proj).ProjectAssociatedFrames.containsKey(resourcePath)){

            loader = new FXMLLoader(ClassLoader.getSystemResource(resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            newStage.getIcons().add(IconsManager.LogisimFX);

            AbstractController c = loader.getController();
            curr = c;
            c.postInitialization(newStage, proj);

            newStage.setOnHidden(event -> {
                c.onClose();
                OpenedMainFrames.get(proj).ProjectAssociatedFrames.remove(resourcePath);
            });

            newStage.initModality(modality);

            newStage.show();

            OpenedMainFrames.get(proj).ProjectAssociatedFrames.put(resourcePath, newStage);

        }else{
            FocusOnFrame(OpenedMainFrames.get(proj).ProjectAssociatedFrames.get(resourcePath));
        }

    }

    public static void CreateOptionsFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/OptionsFrame/Options.fxml", proj, Modality.NONE);
    }

    public static void CreateCircLogFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/WaveformFrame/CircLog.fxml", proj, Modality.NONE);
    }

    public static void CreatePrintFrame(Project proj){

        int i = 0;
        for (Circuit circ : proj.getLogisimFile().getCircuits()) {
            if (circ.getBounds() != Bounds.EMPTY_BOUNDS) {
                i++;
            }
        }

        if(i>0){
            CreateNewFrame("LogisimFX/newgui/PrintFrame/Print.fxml", proj, Modality.APPLICATION_MODAL);
        }else{
            DialogManager.createErrorDialog(LC_gui.getInstance().get("printEmptyCircuitsTitle"), LC_gui.getInstance().get("printEmptyCircuitsMessage"));
        }

    }

    public static void CreateExportImageFrame(Project proj){

        int i = 0;
        for (Circuit circ : proj.getLogisimFile().getCircuits()) {
            if (circ.getBounds() != Bounds.EMPTY_BOUNDS) {
                i++;
            }
        }

        if(i>0) {
            CreateNewFrame("LogisimFX/newgui/ExportImageFrame/ExportImage.fxml", proj, Modality.APPLICATION_MODAL);
        }else {
            DialogManager.createErrorDialog(LC_gui.getInstance().get("exportEmptyCircuitsTitle"), LC_gui.getInstance().get("exportEmptyCircuitsMessage"));
        }

    }

    public static void CreateCircuitAnalysisFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/AnalyzeFrame/Analysis.fxml", proj, Modality.NONE);
    }

    public static AnalyzeController CreateAndRunCircuitAnalysisFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/AnalyzeFrame/Analysis.fxml", proj, Modality.NONE);
        return (AnalyzeController) curr;
    }



    //Project independent frames

    private static void CreateNewFrame(String resourcePath, Modality modality){

        if(!OpenedProjectIndependentFrames.containsKey(resourcePath)){

            loader = new FXMLLoader(ClassLoader.getSystemResource(resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            newStage.getIcons().add(IconsManager.LogisimFX);

            AbstractController c = loader.getController();
            c.postInitialization(newStage);

            newStage.setOnHidden(event -> {
                c.onClose();
                OpenedProjectIndependentFrames.remove(resourcePath);
            });

            newStage.initModality(modality);

            newStage.show();

            OpenedProjectIndependentFrames.put(resourcePath, new Data(newStage, c, false));

        }else{
            FocusOnFrame(OpenedProjectIndependentFrames.get(resourcePath).stage);
        }

    }

    public static void CreateLoadingScreen(){
        CreateNewFrame("LogisimFX/newgui/LoadingFrame/Loading.fxml", Modality.NONE);
    }

    public static void CreatePreferencesFrame(){
        CreateNewFrame("LogisimFX/newgui/PreferencesFrame/Preferences.fxml", Modality.NONE);
    }

    public static void CreateHelpFrame(String chapter){

        String resourcepath = "LogisimFX/newgui/HelpFrame/Help.fxml";

        if(!OpenedProjectIndependentFrames.containsKey(resourcepath)){
            CreateNewFrame("LogisimFX/newgui/HelpFrame/Help.fxml", Modality.NONE);
        }else{
            FocusOnFrame(OpenedProjectIndependentFrames.get(resourcepath).stage);
        }

        ((HelpController) OpenedProjectIndependentFrames.get(resourcepath).controller).openChapter(chapter);

    }

    public static void CreateAboutFrame(){
        CreateNewFrame("LogisimFX/newgui/AboutFrame/About.fxml", Modality.APPLICATION_MODAL);
    }



    //Circuit Statistics frames

    private static void CreateNewFrame(String resourcePath, Project proj, Circuit circ, Modality modality) {

        if (!OpenedMainFrames.get(proj).AssociatedCircuitStatisticFrames.containsKey(circ)) {

            loader = new FXMLLoader(ClassLoader.getSystemResource(resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            newStage.getIcons().add(IconsManager.LogisimFX);

            AbstractController c = loader.getController();
            curr = c;
            c.postInitialization(newStage, proj);

            newStage.setOnHidden(event -> {
                c.onClose();
                OpenedMainFrames.get(proj).AssociatedCircuitStatisticFrames.remove(circ);
            });

            newStage.initModality(modality);
            //newStage.initOwner(OpenedMainFrames.get(proj).stage);

            newStage.show();

            OpenedMainFrames.get(proj).AssociatedCircuitStatisticFrames.put(circ, newStage);

        } else {
            FocusOnFrame(OpenedMainFrames.get(proj).AssociatedCircuitStatisticFrames.get(circ));
        }

    }

    public static void CreateCircuitStatisticFrame(Project proj, Circuit circ){
        CreateNewFrame("LogisimFX/newgui/CircuitStatisticFrame/CircuitStatistic.fxml", proj, circ, Modality.NONE);
        ((CircuitStatisticController)curr).describeCircuit(circ);
    }



    //Memory based frames

    private static void CreateNewFrame(String resourcePath, Project proj, MemContents memContents, Modality modality) {

        if (!OpenedMainFrames.get(proj).AssociatedMemoryEditors.containsKey(memContents)) {

            loader = new FXMLLoader(ClassLoader.getSystemResource(resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            newStage.getIcons().add(IconsManager.LogisimFX);

            AbstractController c = loader.getController();
            curr = c;
            c.postInitialization(newStage, proj);

            newStage.setOnHidden(event -> {
                c.onClose();
                OpenedMainFrames.get(proj).AssociatedMemoryEditors.remove(memContents);
            });

            newStage.initModality(modality);
            //newStage.initOwner(OpenedMainFrames.get(proj).stage);

            newStage.show();

            OpenedMainFrames.get(proj).AssociatedMemoryEditors.put(memContents, newStage);

        } else {
            FocusOnFrame(OpenedMainFrames.get(proj).AssociatedMemoryEditors.get(memContents));
        }

    }

    public static void CreateHexEditorFrame(Project proj, MemContents memContents){
        CreateNewFrame("LogisimFX/newgui/HexEditorFrame/HexEditor.fxml", proj, memContents, Modality.NONE);
        ((HexEditorController)curr).openHex(memContents);
    }



    //Getters

    public static Set<Project> getOpenedProjects(){
        return OpenedMainFrames.keySet();
    }


    //From old project Manager

    public static List<Project> getOpenProjects() {
        return Collections.unmodifiableList(new ArrayList<>(OpenedMainFrames.keySet()));
    }

    public static boolean windowNamed(String name) {
        for (Project proj : OpenedMainFrames.keySet()) {
            if (proj.getLogisimFile().getName().equals(name)) return true;
        }
        return false;
    }

    public static Project FindProjectForFile(File query){

        for(Project proj: OpenedMainFrames.keySet()){

            Loader loader = proj.getLogisimFile().getLoader();
            if (loader == null) continue;
            File f = loader.getMainFile();
            if (query.equals(f)) return proj;

        }

        return null;

    }

    /*
    public static Point getLocation(Window win) {
        Point ret = frameLocations.get(win);
        return ret == null ? null : (Point) ret.clone();
    }

     */

    /////////


    //Tools

    public static void FocusOnFrame(Project project){

        OpenedMainFrames.get(project).stage.toFront();

        for (Stage stage: OpenedMainFrames.get(project).ProjectAssociatedFrames.values()) {
            stage.toFront();
        }

        for (Stage stage: OpenedMainFrames.get(project).AssociatedCircuitStatisticFrames.values()) {
            stage.toFront();
        }

        for (Stage stage: OpenedMainFrames.get(project).AssociatedMemoryEditors.values()) {
            stage.toFront();
        }


    }

    private static void FocusOnFrame(Stage s){
        s.toFront();
    }


    public static void CloseFrame(Project proj){

        if (OpenedMainFrames.size() > 1) {

            CloseMemoryEditors(proj);
            CloseProjectAssociatedFrames(proj);
            CloseCircuitStatisticsFrames(proj);
            proj.getSimulator().shutDown();
            proj.getFrameController().onClose();

            OpenedMainFrames.remove(proj);

        } else if(OpenedMainFrames.size() == 1){

            CloseMemoryEditors(proj);
            CloseProjectIndependentFrames();
            CloseCircuitStatisticsFrames(proj);
            CloseProjectAssociatedFrames(proj);
            proj.getSimulator().shutDown();
            proj.getFrameController().onClose();
/*
            if (LogisimFile.LOGISIMFX_TEMP_DIR.toFile().exists()) {
                try {
                    FileUtils.deleteDirectory(LogisimFile.LOGISIMFX_TEMP_DIR.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
*/
            Platform.exit();
            System.exit(0);

        }

    }


    private static void CloseProjectAssociatedFrames(Project project){

        ArrayList<Stage> stages = new ArrayList<>(OpenedMainFrames.get(project).ProjectAssociatedFrames.values());

        for (Stage stage: stages) {
            stage.close();
        }

    }

    private static void CloseCircuitStatisticsFrames(Project project){

        ArrayList<Stage> stages = new ArrayList<>(OpenedMainFrames.get(project).AssociatedCircuitStatisticFrames.values());

        for (Stage stage: stages) {
            stage.close();
        }

    }

    private static void CloseMemoryEditors(Project project){

        ArrayList<Stage> stages = new ArrayList<>(OpenedMainFrames.get(project).AssociatedMemoryEditors.values());

        for (Stage stage: stages) {
            stage.close();
        }

    }

    private static void CloseProjectIndependentFrames(){

        ArrayList<String> resources = new ArrayList<>(OpenedProjectIndependentFrames.keySet());

        for (String frameResource: resources) {
            OpenedProjectIndependentFrames.get(frameResource).stage.close();
        }

    }


    public static void ExitProgram(){

        ArrayList<Project> projects = new ArrayList<>(OpenedMainFrames.keySet());

        for (Project proj: projects) {
            OpenedMainFrames.get(proj).stage.close();
        }

    }

    public static void ForceExit(){
/*
        if (LogisimFile.LOGISIMFX_TEMP_DIR.toFile().exists()) {
            try {
                FileUtils.deleteDirectory(LogisimFile.LOGISIMFX_TEMP_DIR.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/
        Platform.exit();
        System.exit(0);

    }

}
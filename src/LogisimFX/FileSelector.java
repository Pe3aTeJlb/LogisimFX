/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX;

import LogisimFX.localization.LC_file;
import LogisimFX.localization.LC_fpga;
import LogisimFX.localization.LC_gui;
import LogisimFX.prefs.AppPreferences;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;


public class FileSelector {

    private FileChooser fileChooser;

    private DirectoryChooser directoryChooser;

    private File tempFile = new File(AppPreferences.DIALOG_DIRECTORY.get());

    private FileChooser.ExtensionFilter circ = new FileChooser.ExtensionFilter(LC_file.getInstance().get("logisimFilter"),"*.circ");
    private FileChooser.ExtensionFilter jar =  new FileChooser.ExtensionFilter(LC_file.getInstance().get("jarFilter"),"*.jar");

    private FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter(LC_gui.getInstance().get("exportPngFilter"),"*.png");
    private FileChooser.ExtensionFilter jpeg = new FileChooser.ExtensionFilter(LC_gui.getInstance().get("exportJpgFilter"),
            "*.jpg", "*.jpeg", "*.jpe", "*.jfi", "*.jfif", "*.jfi");
    private FileChooser.ExtensionFilter gif = new FileChooser.ExtensionFilter(LC_gui.getInstance().get("exportGifFilter"),"*.gif");

    private FileChooser.ExtensionFilter circlog = new FileChooser.ExtensionFilter(LC_file.getInstance().get("logisimFilter"),"*.circlog");

    private FileChooser.ExtensionFilter map = new FileChooser.ExtensionFilter(LC_fpga.getInstance().get("mapFilter"),"*.map");


    private Window ownerWindow;


    public FileSelector(Window owner){

        ownerWindow = owner;

        fileChooser = new FileChooser();

        directoryChooser = new DirectoryChooser();

        fileChooser.getExtensionFilters().addAll(
                circ,
                jar
        );

    }



    public File chooseDirectory(String title){

        directoryChooser.setTitle(title);

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        tempFile = directoryChooser.showDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File showOpenDialog(String title){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().clear();

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return fileChooser.showOpenDialog(ownerWindow);

    }

    public File showSaveDialog(String title){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().clear();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }



    public File openCircFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setCircFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File openJarFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setJarFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File openCirclog(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setCirclogFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File openMapFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setMapFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File saveCircFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setCircFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File saveJarFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setJarFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File savePngFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile()); 

        setPngFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File saveJpgFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setJpgFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File saveGifFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setGifFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File saveCirclog(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setCirclogFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }

    public File saveMapFile(){

        updateLocale();

        if (tempFile != null) fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setMapFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        if (tempFile != null) AppPreferences.DIALOG_DIRECTORY.set(tempFile.getParent());

        return tempFile;

    }


    public void setCircFilter(){

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(circ);
        fileChooser.setSelectedExtensionFilter(circ);

    }

    public void setJarFilter(){

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(jar);
        fileChooser.setSelectedExtensionFilter(jar);

    }

    public void setPngFilter(){

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(png);
        fileChooser.setSelectedExtensionFilter(png);

    }

    public void setJpgFilter(){

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(jpeg);
        fileChooser.setSelectedExtensionFilter(jpeg);

    }

    public void setGifFilter(){

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(gif);
        fileChooser.setSelectedExtensionFilter(gif);

    }

    public void setFolderFilter(){

    }

    public void setCirclogFilter(){

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(circlog);
        fileChooser.setSelectedExtensionFilter(circlog);

    }

    public void setMapFilter(){
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(map);
        fileChooser.setSelectedExtensionFilter(map);
    }


    public void setSelectedFile(File f){
        fileChooser.setInitialDirectory(f);
    }

    public void setInitialDirectory(File f){
        fileChooser.setInitialDirectory(f);
    }


    private void updateLocale(){

        circ = new FileChooser.ExtensionFilter(LC_file.getInstance().get("logisimFilter"),"*.circ");
        jar =  new FileChooser.ExtensionFilter(LC_file.getInstance().get("jarFilter"),"*.jar");

    }

}

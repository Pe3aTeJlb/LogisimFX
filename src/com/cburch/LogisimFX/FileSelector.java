package com.cburch.LogisimFX;

import com.cburch.LogisimFX.localization.LC_file;
import com.cburch.LogisimFX.localization.LC_gui;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;


public class FileSelector {

    private FileChooser fileChooser;

    private DirectoryChooser directoryChooser;

    private File tempFile;

    private FileChooser.ExtensionFilter circ = new FileChooser.ExtensionFilter(LC_file.getInstance().get("logisimFilter"),"*.circ");
    private FileChooser.ExtensionFilter jar =  new FileChooser.ExtensionFilter(LC_file.getInstance().get("jarFilter"),"*.jar");

    private FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter(LC_gui.getInstance().get("exportPngFilter"),"*.png");
    private FileChooser.ExtensionFilter jpeg = new FileChooser.ExtensionFilter(LC_gui.getInstance().get("exportJpgFilter"),
            "*.jpg", "*.jpeg", "*.jpe", "*.jfi", "*.jfif", "*.jfi");
    private FileChooser.ExtensionFilter gif = new FileChooser.ExtensionFilter(LC_gui.getInstance().get("exportGifFilter"),"*.gif");

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

        tempFile = directoryChooser.showDialog(ownerWindow);

        return tempFile;

    }

    public File showOpenDialog(String title){

        UpdateLocale();

        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().clear();

        return fileChooser.showOpenDialog(ownerWindow);

    }

    public File showSaveDialog(String title){

        UpdateLocale();

        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().clear();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        return tempFile;

    }



    public File OpenCircFile(){

        UpdateLocale();

        //fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setCircFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File OpenJarFile(){

        UpdateLocale();

       // fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setJarFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File SaveCircFile(){

        UpdateLocale();

        //fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setCircFilter();

        tempFile = fileChooser.showSaveDialog(ownerWindow);

        return tempFile;

    }

    public File SaveJarFile(){

        UpdateLocale();

        // fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setJarFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File SavePngFile(){

        UpdateLocale();

        // fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setPngFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File SaveJpgFile(){

        UpdateLocale();

        // fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setJpgFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File SaveGifFile(){

        UpdateLocale();

        // fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        setGifFilter();

        tempFile = fileChooser.showOpenDialog(ownerWindow);

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



    public void setSelectedFile(File f){
        fileChooser.setInitialDirectory(f);
    }

    public void setInitialDirectory(File f){
        fileChooser.setInitialDirectory(f);
    }


    private void UpdateLocale(){

        circ = new FileChooser.ExtensionFilter(LC_file.getInstance().get("logisimFilter"),"*.circ");
        jar =  new FileChooser.ExtensionFilter(LC_file.getInstance().get("jarFilter"),"*.jar");

    }

}

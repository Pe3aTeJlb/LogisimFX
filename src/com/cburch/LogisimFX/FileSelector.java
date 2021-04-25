package com.cburch.LogisimFX;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;


public class FileSelector {

    private Localizer lc = new Localizer("LogisimFX/resources/localization/file");

    private FileChooser fileChooser;

    private File tempFile;

    private FileChooser.ExtensionFilter circ = new FileChooser.ExtensionFilter(lc.get("logisimFilter"),"*.circ");
    private FileChooser.ExtensionFilter jar =  new FileChooser.ExtensionFilter(lc.get("jarFilter"),"*.jar");

    private Window ownerWindow;

    public FileSelector(Window owner){

        ownerWindow = owner;

        fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                circ,
                jar
        );

    }

    public File showOpenDialog(){

        UpdateLocale();

        fileChooser.setSelectedExtensionFilter(null);
        return fileChooser.showOpenDialog(ownerWindow);

    }

    public File showSaveDialog(){

        UpdateLocale();

        fileChooser.setSelectedExtensionFilter(null);
        return fileChooser.showSaveDialog(ownerWindow);

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

    public void setSelectedFile(File f){
        fileChooser.setInitialDirectory(f);
    }

    public void setInitialDirectory(File f){
        fileChooser.setInitialDirectory(f);
    }


    private void UpdateLocale(){

        circ = new FileChooser.ExtensionFilter(lc.get("logisimFilter"),"*.circ");
        jar =  new FileChooser.ExtensionFilter(lc.get("jarFilter"),"*.jar");

    }

}

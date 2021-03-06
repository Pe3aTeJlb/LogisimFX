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

    public FileSelector(){

        fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                circ,
                jar
        );

    }

    public File OpenCircFile(final Window ownerWindow){

        UpdateLocale();

        //fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(circ);
        fileChooser.setSelectedExtensionFilter(circ);

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File OpenJarFile(final Window ownerWindow){

        UpdateLocale();

       // fileChooser.setInitialDirectory(tempFile.getAbsoluteFile());

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(jar);
        fileChooser.setSelectedExtensionFilter(jar);

        tempFile = fileChooser.showOpenDialog(ownerWindow);

        return tempFile;

    }

    public File showOpenDialog(final Window ownerWindow){

        UpdateLocale();

        fileChooser.setSelectedExtensionFilter(null);
        return fileChooser.showOpenDialog(ownerWindow);
    }

    private void UpdateLocale(){

        circ = new FileChooser.ExtensionFilter(lc.get("logisimFilter"),"*.circ");
        jar =  new FileChooser.ExtensionFilter(lc.get("jarFilter"),"*.jar");

    }

}

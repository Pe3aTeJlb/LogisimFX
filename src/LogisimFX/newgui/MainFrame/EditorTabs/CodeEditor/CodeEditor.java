/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.fpga.Reporter;
import LogisimFX.fpga.data.MappableResourcesContainer;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.ToplevelHdlGeneratorFactory;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.EditorTabs.TextEditor.TextEditor;
import LogisimFX.newgui.MainFrame.EditorTabs.TextEditor.TextEditorToolBar;
import LogisimFX.proj.Project;
import LogisimFX.lang.python.PythonConnector;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.IntFunction;


public class CodeEditor extends TextEditor {

    private Component comp;
    private Circuit circ;
    private File file;

    //Comp viewer
    public CodeEditor(Project project, Circuit circ, Component comp){

        super(project);

        //remove find&replace and save
        getTextEditorToolBar().getItems().remove(3);
        getTextEditorToolBar().getItems().remove(2);
        getTextEditorToolBar().getItems().remove(1);

        this.circ = circ;
        this.comp = comp;

        new SyntaxHighlighter(getTextArea()).start("v");
        StringBuilder builder = new StringBuilder();

        for(String s: comp.getFactory().getHDLGenerator(comp.getAttributeSet()).getArchitecture(
                circ.getNetList(), comp.getAttributeSet(), comp.getFactory().getHDLName(comp.getAttributeSet()))) {
            builder.append(s).append("\n");
        }

        IntFunction<Node> noFactory = LineNumberFactory.get(getTextArea());
        IntFunction<Node> graphicFactory = line -> {
            HBox lineBox = new HBox(noFactory.apply(line));
            lineBox.getStyleClass().add("lineno-box");
            lineBox.setAlignment(Pos.CENTER_LEFT);
            return lineBox;
        };
        getTextArea().setParagraphGraphicFactory(graphicFactory);

        getTextArea().appendText(builder.toString());

        getTextArea().setEditable(false);

    }

    public CodeEditor(Project project, Circuit circ, File file){

        super(project);

        this.file = file;
        this.circ = circ;

        circ.registerProject(proj);

        new SyntaxHighlighter(getTextArea()).start(file.getName().split("\\.")[1]);

        IntFunction<Node> noFactory = LineNumberFactory.get(getTextArea());
        IntFunction<Node> graphicFactory = line -> {
            HBox lineBox = new HBox(noFactory.apply(line));
            lineBox.getStyleClass().add("lineno-box");
            lineBox.setAlignment(Pos.CENTER_LEFT);
            return lineBox;
        };
        getTextArea().setParagraphGraphicFactory(graphicFactory);

        switch (file.getName()){
            case "TopLevelShell.v": extendEditorToolBarWithTopLevel();   break;
            case "VerilogModel.v":  extendEditorToolBarWithVerilog();    break;
            case "HLS.py":          extendEditorToolBarWithHLS();        break;
        }

        getTextArea().requestFocus();

        if (file.length() > 0) {

            try {
                getTextArea().appendText(Files.readString(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(file.getName().equals("TopLevelShell.v")) {

            generateTopLevel();

        } else if(file.getName().equals("VerilogModel.v")){

            getTextArea().clear();

            StringBuilder builder = new StringBuilder();
            SubcircuitFactory factory = circ.getSubcircuitFactory();

            for(String s: factory.getHDLGenerator(circ.getStaticAttributes()).getArchitecture(
                    circ.getNetList(), circ.getStaticAttributes(), factory.getHDLName(circ.getStaticAttributes()))) {
                builder.append(s).append("\n");
            }

            getTextArea().appendText(builder.toString());

        } else if(file.getName().equals("HLS.py")){

            String importSection = "import sys\n" +
                    "import os\n" +
                    "sys.path.append(r'path_to_lib_do_not_change_this_line')\n"+
                    "from sfgen import * \n" +
                    "from sfgen.verilog_backend import * \n" +
                    "#for more information visit https://github.com/dillonhuff/SFGen \n"+
                    "def cube(x):\n" +
                    "\tout = x*x*x\n" +
                    "\treturn out\n"+
                    "constraints = ScheduleConstraints() \n"+
                    "synthesize_verilog(os.path.abspath(__file__), 'cube', [l.ArrayType(32)], constraints)"
                    ;

            getTextArea().appendText(importSection);

        }

        getTextArea().setEditable(proj.getLogisimFile().contains(circ));

    }

    //Code editor
    public CodeEditor(Project project, File file){

        super(project);

        this.file = file;

        if (file.getName().split("\\.").length > 1) {
            new SyntaxHighlighter(getTextArea()).start(file.getName().split("\\.")[1]);
        }

        IntFunction<Node> noFactory = LineNumberFactory.get(getTextArea());
        IntFunction<Node> graphicFactory = line -> {
            HBox lineBox = new HBox(noFactory.apply(line));
            lineBox.getStyleClass().add("lineno-box");
            lineBox.setAlignment(Pos.CENTER_LEFT);
            return lineBox;
        };
        getTextArea().setParagraphGraphicFactory(graphicFactory);

        getTextArea().requestFocus();

        if (file.length() > 0) {
            try {
                getTextArea().appendText(Files.readString(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getEditorDescriptor(){

        if (comp != null){
            return circ.getName() + " " + comp.getFactory().getName() + " " + comp.getLocation().toString();
        } else if (circ != null){
            return circ.getHDLFile(proj, file.getName()).toString().split(proj.getLogisimFile().getProjectDir().getFileName().toString())[1].substring(1);
        } else {
            return file.toString().split(proj.getLogisimFile().getProjectDir().getFileName().toString())[1].substring(1);
        }

    }


    @Override
    public void doSave(){

        FileOutputStream writer;

        if (comp == null && circ != null){

            try {

                if (getTextArea().getText().isEmpty() || !proj.getLogisimFile().contains(circ)){
                    return;
                }

                File f = circ.getHDLFile(proj, file.getName());
                if (!f.exists()){
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
                writer = new FileOutputStream(f);
                writer.write(getTextArea().getText().getBytes());
                writer.flush();
                writer.close();

            } catch (IOException e) {
                DialogManager.createStackTraceDialog("Error!", "Error during saving code editor content " + file.getName(), e);
                e.printStackTrace();
            }

        } else {

            try {
                if (!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                writer = new FileOutputStream(file);
                writer.write(getTextArea().getText().getBytes());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                DialogManager.createStackTraceDialog("Error!", "Error during saving code editor content " + file.getName(), e);
                e.printStackTrace();
            }

        }

    }

    @Override
    public void doDelete() {

        if (DialogManager.createConfirmDialog()) {

            this.getTextArea().clear();

            try {
                if (file.exists()) {
                    FileUtils.delete(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    private void generateTopLevel(){

        getTextArea().clear();
        getTextArea().appendText(proj.getFpgaToolchainOrchestrator().getTopLevelShellCode(circ));

    }

    public void toSchematics(){
/*
        if (PythonConnector.isPythonPresent(proj)) {

            String pathToLib = PythonConnector.getLibPath("yowsap_yosys");

            //Save HLS file and execute it
            replace("path_to_lib_do_not_change_this_line", pathToLib);
            doSave();
            PythonConnector.executeFile(proj, file);
            replace(pathToLib.replace("/", File.separator+File.separator).replace("\\", File.separator+File.separator),"path_to_lib_do_not_change_this_line");
            doSave();

            //reload verilog model
            proj.getFrameController().reloadFile(circ.getVerilogModel(proj));
            proj.getFrameController().addCodeEditor(circ, circ.getVerilogModel(proj));


        } else {
            DialogManager.createErrorDialog("Error", "Python3 required");
        }*/

    }

    public void fromSchematics(){

        getTextArea().clear();

        proj.getFpgaToolchainOrchestrator().annotate();

        StringBuilder builder = new StringBuilder();
        SubcircuitFactory factory = circ.getSubcircuitFactory();

        for(String s: factory.getHDLGenerator(circ.getStaticAttributes()).getArchitecture(
                circ.getNetList(), circ.getStaticAttributes(), factory.getHDLName(circ.getStaticAttributes()))) {
            builder.append(s).append("\n");
        }

        getTextArea().appendText(builder.toString());

    }

    public void doHLS(){

        if (PythonConnector.isPythonPresent(proj)) {

            String pathToLib = PythonConnector.getLibPath("sfgen");

            //Save HLS file and execute it
            replace("path_to_lib_do_not_change_this_line", pathToLib);
            doSave();
            PythonConnector.executeFile(proj, file);
            replace(pathToLib.replace("/", File.separator+File.separator).replace("\\", File.separator+File.separator),"path_to_lib_do_not_change_this_line");
            doSave();

            //reload verilog model
            proj.getFrameController().reloadFile(circ.getVerilogModel(proj));
            proj.getFrameController().addCodeEditor(circ, circ.getVerilogModel(proj));


        } else {
            DialogManager.createErrorDialog("Error", "Python3 required");
        }

    }


    public void reloadFile(){

        try {
            getTextArea().clear();
            getTextArea().appendText(Files.readString(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Component getComp(){
        return comp;
    }

    public Circuit getCirc(){
        return circ;
    }



    private final int prefWidth = 15;
    private final int prefHeight = 15;

    private void extendEditorToolBarWithTopLevel(){

        Button renegTopLevel = new Button();
        renegTopLevel.graphicProperty().setValue(IconsManager.getIcon("regen.gif"));
        renegTopLevel.setTooltip(new TextEditorToolBar.ToolTip("regenTopLevel"));
        renegTopLevel.setOnAction(event -> generateTopLevel());
        renegTopLevel.setPrefSize(prefWidth,prefHeight);
        renegTopLevel.setMinSize(prefWidth,prefHeight);
        renegTopLevel.setMaxSize(prefWidth,prefHeight);

        getTextEditorToolBar().getItems().addAll(
                new Separator(),
                renegTopLevel
        );

    }

    private void extendEditorToolBarWithVerilog(){

        Button toSchematics = new Button();
        toSchematics.graphicProperty().setValue(IconsManager.getIcon("codetortl.gif"));
        toSchematics.setTooltip(new TextEditorToolBar.ToolTip("codeToRTL"));
        toSchematics.setOnAction(event -> toSchematics());
        toSchematics.setPrefSize(prefWidth,prefHeight);
        toSchematics.setMinSize(prefWidth,prefHeight);
        toSchematics.setMaxSize(prefWidth,prefHeight);

        Button fromSchematics = new Button();
        fromSchematics.graphicProperty().setValue(IconsManager.getIcon("rtltocode.gif"));
        fromSchematics.setTooltip(new TextEditorToolBar.ToolTip("RTLToCode"));
        fromSchematics.setOnAction(event -> fromSchematics());
        fromSchematics.setPrefSize(prefWidth,prefHeight);
        fromSchematics.setMinSize(prefWidth,prefHeight);
        fromSchematics.setMaxSize(prefWidth,prefHeight);

        getTextEditorToolBar().getItems().addAll(
                new Separator(),
                toSchematics,
                fromSchematics
        );

    }

    private void extendEditorToolBarWithHLS(){

        Button toVerilog = new Button();
        toVerilog.graphicProperty().setValue(IconsManager.getIcon("hls.png"));
        toVerilog.setTooltip(new TextEditorToolBar.ToolTip("hls"));
        toVerilog.setOnAction(event -> doHLS());
        toVerilog.setPrefSize(prefWidth,prefHeight);
        toVerilog.setMinSize(prefWidth,prefHeight);
        toVerilog.setMaxSize(prefWidth,prefHeight);

        getTextEditorToolBar().getItems().addAll(
                new Separator(),
                toVerilog
        );

    }

}

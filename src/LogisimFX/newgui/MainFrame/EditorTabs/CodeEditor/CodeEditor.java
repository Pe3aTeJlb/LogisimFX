/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.EditorTabs.TextEditor.TextEditor;
import LogisimFX.newgui.MainFrame.EditorTabs.TextEditor.TextEditorToolBar;
import LogisimFX.proj.Project;
import LogisimFX.lang.python.PythonConnector;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;


public class CodeEditor extends TextEditor {

    private Component comp;
    private Circuit circ;
    private File file;

    //Comp viewer
    public CodeEditor(Project project, Circuit circ, Component comp){

        super(project);

        this.circ = circ;
        this.comp = comp;

        new SyntaxHighlighter(getCodeArea()).start("v");
        StringBuilder builder = new StringBuilder();

        for(String s: comp.getFactory().getHDLGenerator(comp.getAttributeSet()).getArchitecture(
                circ.getNetList(), comp.getAttributeSet(), comp.getFactory().getHDLName(comp.getAttributeSet()))) {
            builder.append(s).append("\n");
        }

        getCodeArea().insertText(0, builder.toString());

        getCodeArea().setEditable(false);

    }

    public CodeEditor(Project project, Circuit circ, File file){

        super(project);

        this.file = file;

        circ.registerProject(proj);

        new SyntaxHighlighter(getCodeArea()).start(file.getName().split("\\.")[1]);

        switch (file.getName()){
            case "VerilogModel.v":  extendEditorToolBarWithVerilog();    break;
            case "HLS.py":          extendEditorToolBarWithHLS();        break;
        }

        getCodeArea().requestFocus();

        if (file.length() > 0) {

            try {
                getCodeArea().insertText(0, Files.readString(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(file.getName().equals("VerilogModel.v")){

            StringBuilder builder = new StringBuilder();
            SubcircuitFactory factory = circ.getSubcircuitFactory();
                /*
                for (String s : ((CircuitHdlGeneratorFactory) factory.getHDLGenerator(circ.getStaticAttributes())).getModuleFunctionality(
                        circ.getNetList(), circ.getStaticAttributes()
                ).get()) {
                    builder.append(s).append("\n");
                }
                */

            for(String s: factory.getHDLGenerator(circ.getStaticAttributes()).getArchitecture(
                    circ.getNetList(), circ.getStaticAttributes(), factory.getHDLName(circ.getStaticAttributes()))) {
                builder.append(s).append("\n");
            }

            getCodeArea().insertText(0, builder.toString());

        } else if(file.getName().equals("HLS.py")){

            String pathToLib = PythonConnector.getJarPath("sfgen");
            String importSection = "import sys\n" +
                    "import os\n" +
                    "sys.path.append(r'" + pathToLib + "')\n"+
                    "from sfgen import * \n" +
                    "from sfgen.verilog_backend import * \n" +
                    "#for more information visit https://github.com/dillonhuff/SFGen \n"+
                    "\n\n\n"+
                    "constraints = ScheduleConstraints() \n"+
                    "synthesize_verilog(os.path.abspath(__file__), '!func name!', '!args type!', constraints)"
                    ;

            getCodeArea().insertText(0, importSection);

        }

        getCodeArea().setEditable(proj.getLogisimFile().contains(circ));

    }

    //Code editor
    public CodeEditor(Project project, File file){

        super(project);

        this.file = file;

        new SyntaxHighlighter(getCodeArea()).start(file.getName().split("\\.")[1]);

        getCodeArea().requestFocus();

        if (file.length() > 0) {
            try {
                getCodeArea().insertText(0, Files.readString(file.toPath()));
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



    public void doSave(){

        FileOutputStream writer;

        if (comp == null && circ != null){

            try {

                if (getCodeArea().getText().isEmpty() || !proj.getLogisimFile().contains(circ)){
                    return;
                }

                File f = circ.getHDLFile(proj, file.getName());
                if (!f.exists()){
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
                writer = new FileOutputStream(f);
                writer.write(getCodeArea().getText().getBytes());
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
                writer.write(getCodeArea().getText().getBytes());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                DialogManager.createStackTraceDialog("Error!", "Error during saving code editor content " + file.getName(), e);
                e.printStackTrace();
            }

        }

    }

    public void doHLS(){

        if (PythonConnector.isPythonPresent()) {

            //Save HLS file and execute it
            doSave();
            PythonConnector.executeFile(file);

            //reload verilog model
            proj.getFrameController().reloadFile(circ.getVerilogModel(proj));

            proj.getFrameController().addCodeEditor(circ, circ.getVerilogModel(proj));

            //codeArea.getText().replace(circ.getHLS(proj).getParent(), "\'do not change\'");
            //doSave();
        } else {
            DialogManager.createErrorDialog("Error", "Python3 required");
        }
    }

    public void reloadFile(){

        try {
            getCodeArea().insertText(0, Files.readString(file.toPath()));
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

    private void extendEditorToolBarWithVerilog(){

        Button toRTL = new Button();
        toRTL.graphicProperty().setValue(IconsManager.getIcon("codetortl.gif"));
        toRTL.setTooltip(new TextEditorToolBar.ToolTip("codeToRTL"));
        toRTL.setOnAction(event -> openFindBar());
        toRTL.setPrefSize(prefWidth,prefHeight);
        toRTL.setMinSize(prefWidth,prefHeight);
        toRTL.setMaxSize(prefWidth,prefHeight);

        Button fromRTL = new Button();
        fromRTL.graphicProperty().setValue(IconsManager.getIcon("rtltocode.gif"));
        fromRTL.setTooltip(new TextEditorToolBar.ToolTip("RTLToCode"));
        fromRTL.setOnAction(event -> openReplaceBar());
        fromRTL.setPrefSize(prefWidth,prefHeight);
        fromRTL.setMinSize(prefWidth,prefHeight);
        fromRTL.setMaxSize(prefWidth,prefHeight);

        getTextEditorToolBar().getItems().addAll(
                new Separator(),
                toRTL,
                fromRTL
        );

    }

}

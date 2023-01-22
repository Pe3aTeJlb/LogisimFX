package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import org.fxmisc.richtext.StyleClassedTextArea;

public class SyntaxHighlighter {

    private final StyleClassedTextArea codeArea;

    public SyntaxHighlighter(StyleClassedTextArea codeArea) {
        this.codeArea = codeArea;
    }

    public void start(String ext) {
        switch (ext) {
            case "java" : new JavaKeywordHighlighter().start(codeArea); break;
            case "verilog" : new JavaKeywordHighlighter().start(codeArea); break;
            case "vhdl" : new JavaKeywordHighlighter().start(codeArea); break;
        }
    }

}

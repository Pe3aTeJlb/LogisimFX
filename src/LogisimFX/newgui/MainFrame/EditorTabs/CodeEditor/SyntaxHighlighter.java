package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.verilog.VerilogKeywordHighlighter;
import org.fxmisc.richtext.StyleClassedTextArea;

public class SyntaxHighlighter {

    private final StyleClassedTextArea codeArea;

    public SyntaxHighlighter(StyleClassedTextArea codeArea) {
        this.codeArea = codeArea;
    }

    public void start(String ext) {
        switch (ext) {
            case "verilog" : new VerilogKeywordHighlighter().start(codeArea); break;
        }
    }

}

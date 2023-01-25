package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerilogKeywordHighlighter {
    private static final String[] KEYWORDS = new String[] {
            "always", "event", "or", "strong1", "and", "for",
            "output", "supply0", "assign", "force", "parameter",
            "supply1", "begin", "forever", "pmos", "table", "buffork",
            "posedgetask", "bufif0", "function", "primitive", "time",
            "bufif1", "highz0", "pull0", "tran", "case", "highz1", "pull1",
            "tranif0", "casex", "if", "pullup", "tranif1", "casez", "initial",
            "pulldown", "tri", "cmos", "inout", "rcmos", "tri0", "deassign",
            "input", "regtri1", "default", "integer", "release", "triand",
            "defparam", "join", "repeat", "trior", "disable", "large", "rnmos",
            "trireg", "edge", "macromodule", "rpmos", "vectored", "else",
            "medium", "rtran", "wait", "end", "module", "rtranif0", "wand",
            "endcase", "nand", "rtranif1", "weak0", "endmodule", "negedge",
            "scalared", "weak1", "endfunction", "nmos", "small", "while",
            "endprimitive", "nor", "specify", "wire", "endspecify", "not",
            "specparam", "wor", "endtable", "notif0", "strength", "xnor",
            "endtask", "strong0", "xor"
    };
//    private static final String[] DATA_TYPES = new String[] {
//            "int", "String", "float", "double",
//            "long", "char", "short", "boolean",
//            "byte"
//    };
//    private static final String[] LITERALS = new String[] {"true", "false", "null"};

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    //    private static final String DATA_TYPE_PATTERN = "\\b(" + String.join("|", DATA_TYPES) + ")\\b";
//    private static final String LITERALS_PATTERN = "\\b(" + String.join("|", LITERALS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACKET_PATTERN = "[\\[\\]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public void start(StyleClassedTextArea codeArea) {
        codeArea.plainTextChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}

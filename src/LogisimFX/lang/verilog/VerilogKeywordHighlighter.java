/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.lang.verilog;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerilogKeywordHighlighter {
    private static final String[] KEYWORDS = new String[] {
            "always", "and", "assign", "attribute", "begin", "buf", "bufif0", "bufif1", "case", "casex", "casez", "cmos", "deassign", "default", "defparam", "disable", "edge", "else", "end", "endattribute", "endcase", "endfunction", "endmodule", "endprimitive", "endspecify", "endtable", "endtask", "event", "for", "force", "forever", "fork", "function", "highz0", "highz1", "if", "ifnone", "initial", "inout", "input", "integer", "join", "medium", "module", "large", "localparam", "macromodule", "nand", "negedge", "nmos", "nor", "not", "notif0", "notif1", "or", "output", "parameter", "pmos", "posedge", "primitive", "pull0", "pull1", "pulldown", "pullup", "rcmos", "real", "realtime", "reg", "release", "repeat", "rnmos", "rpmos", "rtran", "rtranif0", "rtranif1", "scalared", "signed", "small", "specify", "specparam", "strength", "strong0", "strong1", "supply0", "supply1", "table", "task", "time", "tran", "tranif0", "tranif1", "tri", "tri0", "tri1", "triand", "trior", "trireg", "unsigned", "vectored", "wait", "wand", "weak0", "weak1", "while", "wire", "wor", "xnor", "xor", "alias", "always_comb", "always_ff", "always_latch", "assert", "assume", "automatic", "before", "bind", "bins", "binsof", "break", "constraint", "context", "continue", "cover", "cross", "design", "dist", "do", "expect", "export", "extends", "extern", "final", "first_match", "foreach", "forkjoin", "iff", "ignore_bins", "illegal_bins", "import", "incdir", "include", "inside", "instance", "intersect", "join_any", "join_none", "liblist", "library", "matches", "modport", "new", "noshowcancelled", "null", "packed", "priority", "protected", "pulsestyle_onevent", "pulsestyle_ondetect", "pure", "rand", "randc", "randcase", "randsequence", "ref", "return", "showcancelled", "solve", "tagged", "this", "throughout", "timeprecision", "timeunit", "unique", "unique0", "use", "wait_order", "wildcard", "with", "within", "class", "clocking", "config", "generate", "covergroup", "interface", "package", "program", "property", "sequence", "endclass", "endclocking", "endconfig", "endgenerate", "endgroup", "endinterface", "endpackage", "endprogram", "endproperty", "endsequence", "bit", "byte", "cell", "chandle", "const", "coverpoint", "enum", "genvar", "int", "local", "logic", "longint", "shortint", "shortreal", "static", "string", "struct", "super", "type", "typedef", "union", "var", "virtual", "void"
    };
    private static final String[] PREPROCESSORS = new String[]{
            "SYNTHESIS", "$assertkill", "$assertoff", "$asserton", "$bits", "$bitstoreal", "$bitstoshortreal", "$cast", "$comment", "$countdrivers", "$countones", "$dimensions", "$display", "$dist_chi_square", "$dist_erlang", "$dist_exponential", "$dist_normal", "$dist_poisson", "$dist_t", "$dist_uniform", "$dumpall", "$dumpfile", "$dumpflush", "$dumplimit", "$dumpoff", "$dumpon", "$dumpvars", "$error", "$exit", "$fatal", "$fclose", "$fdisplay", "$fell", "$feof", "$ferror", "$fflush", "$fgetc", "$fgets", "$finish", "$fmonitor", "$fopen", "$fread", "$fscanf", "$fseek", "$fstrobe", "$ftell", "$fullskew", "$fwrite", "$get_coverage", "$getpattern", "$high", "$history", "$hold", "$increment", "$incsave", "$info", "$input", "$isunbounded", "$isunknown", "$itor", "$key", "$left", "$list", "$load_coverage_db", "$log", "$low", "$monitor", "$monitoroff", "$monitoron", "$nochange", "$nokey", "$nolog", "$onehot", "$onehot0", "$past", "$period", "$printtimescale", "$q_add", "$q_exam", "$q_full", "$q_initialize", "$q_remove", "$random", "$readmemb", "$readmemh", "$realtime", "$realtobits", "$recovery", "$recrem", "$removal", "$reset", "$reset_count", "$reset_value", "$restart", "$rewind", "$right", "$root", "$rose", "$rtoi", "$sampled", "$save", "$scale", "$scope", "$set_coverage_db_name", "$setup", "$setuphold", "$sformat", "$shortrealtobits", "$showscopes", "$showvariables", "$showvars", "$signed", "$size", "$skew", "$sreadmemb", "$sreadmemh", "$sscanf", "$stable", "$stime", "$stop", "$strobe", "$swrite", "$time", "$timeformat", "$timescale", "$timeskew", "$typename", "$typeof", "$uandom", "$ungetc", "$unit", "$unpacked_dimensions", "$unsigned", "$upscope", "$urandom_range", "$value$plusargs", "$var", "$vcdclose", "$version", "$warning", "$width", "$write"
    };
//    private static final String[] DATA_TYPES = new String[] {
//            "int", "String", "float", "double",
//            "long", "char", "short", "boolean",
//            "byte"
//    };
//    private static final String[] LITERALS = new String[] {"true", "false", "null"};

    private static final String PREPROCESSOR_PATTERN = "\\b(" + String.join("|", PREPROCESSORS) + ")\\b";
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
//    private static final String DATA_TYPE_PATTERN = "\\b(" + String.join("|", DATA_TYPES) + ")\\b";
//    private static final String LITERALS_PATTERN = "\\b(" + String.join("|", LITERALS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[\\]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static final Pattern PATTERN = Pattern.compile(
                    "(?<PREPROCESSOR>" + PREPROCESSOR_PATTERN + ")"
                    + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
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
            matcher.group("PREPROCESSOR") != null ? "preprocessor" :
            matcher.group("KEYWORD") != null ? "keyword" :
            matcher.group("PAREN") != null ? "paren" :
            matcher.group("BRACE") != null ? "brace" :
            matcher.group("BRACKET") != null ? "bracket" :
            matcher.group("SEMICOLON") != null ? "semicolon" :
            matcher.group("STRING") != null ? "string" :
            matcher.group("COMMENT") != null ? "comment" :
            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
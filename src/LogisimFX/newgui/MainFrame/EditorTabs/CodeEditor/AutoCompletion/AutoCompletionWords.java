/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.AutoCompletion;

import java.util.List;

public class AutoCompletionWords {

    private List<String> words;

    public List<String> getWords(String ext) {
        switch (ext) {
            case "java":  words = List.of(
                    "abstract", "assert", "boolean", "break", "byte",
                    "case", "catch", "char", "class", "const",
                    "continue", "default", "do", "double", "else",
                    "enum", "extends", "final", "finally", "float",
                    "for", "goto", "if", "implements", "import",
                    "instanceof", "int", "interface", "long", "native",
                    "new", "package", "private", "protected", "public",
                    "return", "short", "static", "strictfp", "super",
                    "switch", "synchronized", "this", "throw", "throws",
                    "transient", "try", "void", "volatile", "while",
                    "true", "false", "String"
            ); break;
        }

        return words;
    }

}

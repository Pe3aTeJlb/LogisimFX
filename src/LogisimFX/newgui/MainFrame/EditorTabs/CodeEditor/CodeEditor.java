package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.IconsManager;
import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.AutoCompletion.AutoCompletion;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.AutoCompletion.AutoCompletionWords;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.proj.Project;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.util.UndoUtils;
import org.fxmisc.undo.UndoManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends EditorBase {

    private Circuit circ;
    private String ext;

    private CodeEditorEditMenu menu;

    private VirtualizedScrollPane virtualizedScrollPane;
    private StyleClassedTextArea codeArea;

    private Popup autoCompletionPopup;
    private ListView<String> autoCompletionList;


    //Find&Searc bar
    private ToolBar findBar, replaceBar;
    private TextField findTxtFld, replaceTxtFld;
    private SimpleStringProperty currFindIndex, totalFindIndex;
    private ArrayList<ArrayList<Integer>> coordinateList = new ArrayList<>();


    //Bottom info bar
    private HBox infoBar;
    private Label selectTextLabel;
    private SimpleStringProperty lineNum, colNum, selectedTextNum;
    private AtomicInteger currWordIndex = new AtomicInteger(0);

    public CodeEditor(Project project, Circuit circ, String ext){

        super(project, circ);

        this.circ = circ;
        this.ext = ext;

        initFindReplaceBar();
        initCodeArea("java");
        initInfoBar();

        this.getChildren().addAll(findBar, replaceBar, virtualizedScrollPane, infoBar);

        proj.getFrameController().editorProperty().addListener((observableValue, editorBase, t1) -> {
            if (this.isSelected()){
                recalculateAccelerators();
            }
        });

        this.recalculateAccelerators();

        menu = new CodeEditorEditMenu(this);

        codeArea.requestFocus();

    }



    private void initCodeArea(String ext){

        autoCompletionPopup = new Popup();
        autoCompletionPopup.setAutoHide(true);
        autoCompletionPopup.setHideOnEscape(true);

        codeArea = new StyleClassedTextArea();
        virtualizedScrollPane = new VirtualizedScrollPane(codeArea);
        IntFunction<Node> noFactory = LineNumberFactory.get(codeArea);
        IntFunction<Node> graphicFactory = line -> {
            HBox lineBox = new HBox(noFactory.apply(line));
            lineBox.getStyleClass().add("lineno-box");
            lineBox.setAlignment(Pos.CENTER_LEFT);
            return lineBox;
        };
        UndoManager<List<PlainTextChange>> um = UndoUtils.plainTextUndoManager(codeArea);
        codeArea.setUndoManager(um);

        new SyntaxHighlighter(codeArea).start(ext);
        autoIndent(codeArea);  // auto-indent: insert previous line's indents on enter
        configureAllShortcuts(ext, codeArea);
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(x -> {
            autoCompletion(getCurrWord(codeArea), ext, codeArea);
            //fileModified(tab, codeArea);
        });

        setSelectionListener(codeArea);
        codeArea.caretPositionProperty().addListener((observableValue, oldPos, newPos) -> updateCaret(codeArea));
        codeArea.setParagraphGraphicFactory(graphicFactory);

        VBox.setVgrow(virtualizedScrollPane, Priority.ALWAYS);

    }



    private void autoIndent(StyleClassedTextArea area) {
        area.addEventHandler( KeyEvent.KEY_PRESSED, KE -> {
            if (KE.getCode() == KeyCode.ENTER && !KE.isControlDown()) indent(area);
        });
    }

    private void configureAllShortcuts(String fileName, StyleClassedTextArea codeArea) {
        final KeyCombination showCompletionComb = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlEnterComb = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        codeArea.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (showCompletionComb.match(event)) {
                autoCompletion(getCurrWord(codeArea), fileName, codeArea);
            } else if (ctrlEnterComb.match(event)) {
                codeArea.selectLine();
                IndexRange range = codeArea.getCaretSelectionBind().getRange();
                codeArea.insertText(range.getEnd(), "\n");
                indent(codeArea);
            }
        });
    }

    private void setSelectionListener(StyleClassedTextArea area) {
        area.selectedTextProperty().addListener((observableValue, s, t1) -> {
            selectTextLabel.setVisible(true);
            selectedTextNum.set(String.valueOf(t1.length()));
            if (t1.length()==0) selectTextLabel.setVisible(false);
        });
    }

    private void indent(StyleClassedTextArea area) {
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        int caretPosition = area.getCaretPosition();
        int currentParagraph = area.getCurrentParagraph();
        Matcher m0 = whiteSpace.matcher(area.getParagraph(currentParagraph-1).getSegments().get(0));
        if (m0.find()) area.insertText(caretPosition, m0.group());
    }



    private void autoCompletion(String toSearch, String fileName, StyleClassedTextArea currCodeArea) {
        if (toSearch.length()==0) {
            autoCompletionPopup.hide();
            return;
        }
        List<String> words = new AutoCompletionWords().getWords(fileName);
        if (words==null) return;
        AutoCompletion completion = new AutoCompletion(words);
        showCompletion(completion.suggest(toSearch), currCodeArea, toSearch.length());
    }

    public void insertCompletion(String s, StyleClassedTextArea currCodeArea, int wordLen) {
        currCodeArea.replaceText(currCodeArea.getCaretPosition()-wordLen, currCodeArea.getCaretPosition(), s);
        autoCompletionPopup.hide();
    }

    private void showCompletion(List<String> list, StyleClassedTextArea currCodeArea, int wordLen) {

        if (list.size()>0) {

            autoCompletionList = new ListView<>();
            autoCompletionList.setPrefWidth(360);
            autoCompletionList.getItems().addAll(list);
            autoCompletionList.prefHeightProperty().bind(Bindings.size(autoCompletionList.getItems()).multiply(31));
            autoCompletionList.getSelectionModel().selectFirst();

            // set listeners
            autoCompletionList.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) insertCompletion(autoCompletionList.getSelectionModel().getSelectedItem(), currCodeArea, wordLen);
            });
            autoCompletionList.setOnMouseClicked(event -> {
                if (autoCompletionList.getSelectionModel().getSelectedItem()!=null) insertCompletion(autoCompletionList.getSelectionModel().getSelectedItem(), currCodeArea, wordLen);
            });

            // replacing old data with new one.
            autoCompletionPopup.getContent().clear();
            autoCompletionPopup.getContent().add(autoCompletionList);
            autoCompletionPopup.show(proj.getFrameController().getStage(), currCodeArea.getCaretBounds().get().getMaxX(), currCodeArea.getCaretBounds().get().getMaxY());

        } else {

            if (autoCompletionPopup != null) {
                autoCompletionPopup.getContent().clear();
                autoCompletionPopup.hide();
            }

        }

    }



    private void updateCaret(StyleClassedTextArea codeArea) {
        lineNum.set(Integer.toString(codeArea.getCaretSelectionBind().getParagraphIndex()+1));
        colNum.set(Integer.toString(codeArea.getCaretSelectionBind().getColumnPosition()+1));
    }

    private String getCurrWord(StyleClassedTextArea currCodeArea) {
        Set<Character> charSet = new HashSet<>();
        charSet.add('}');
        charSet.add('{');
        charSet.add(']');
        charSet.add('[');
        charSet.add(')');
        charSet.add('(');
        StringBuilder word = new StringBuilder();
        for (int i = currCodeArea.getCaretPosition(); i>0; i--) {
            char ch = currCodeArea.getText().charAt(i-1);
            if (ch == ' ' || ch == '\n' || charSet.contains(ch)) break;
            else word.append(ch);
        }

        return word.reverse().toString();
    }



    private void initInfoBar(){

        lineNum = new SimpleStringProperty("1");
        colNum = new SimpleStringProperty("1");
        selectedTextNum = new SimpleStringProperty("");

        infoBar = new HBox();
        infoBar.setAlignment(Pos.CENTER_RIGHT);
        infoBar.setSpacing(5);

        selectTextLabel = new Label();
        HBox.setHgrow(selectTextLabel, Priority.ALWAYS);
        selectTextLabel.setVisible(false);
        selectTextLabel.textProperty().bind(selectedTextNum.concat(" ").concat(LC.createStringBinding("codeAreaSelectedTextInfo")));

        Label lineColInfo = new Label();
        HBox.setHgrow(lineColInfo, Priority.ALWAYS);
        lineColInfo.textProperty().bind(lineNum.concat(":").concat(colNum));
        
        Label tabInfo = new Label();
        HBox.setHgrow(tabInfo, Priority.ALWAYS);
        tabInfo.textProperty().bind(LC.createComplexStringBinding("codeAreaIndentInfo", Integer.toString(4)));

        infoBar.getChildren().addAll(selectTextLabel, lineColInfo, tabInfo);

    }



    private void initFindReplaceBar(){

        currFindIndex = new SimpleStringProperty("0");
        totalFindIndex = new SimpleStringProperty("0");

        findBar = new ToolBar();
        findBar.setVisible(false);

        findTxtFld = new TextField();
        findTxtFld.textProperty().addListener(change -> find());

        Label findResultLbl = new Label();
        findResultLbl.textProperty().bind(currFindIndex.concat("/").concat(totalFindIndex));

        Button prevWordBt = new Button();
        //prevWordBt.disableProperty().bind(coordinateList.s);
        prevWordBt.setGraphic(IconsManager.getImageView("projup.gif"));
        prevWordBt.setOnAction(event -> prevWord());

        Button nextWordBtn = new Button();
        //nextWordBtn.disableProperty().bind();
        nextWordBtn.setGraphic(IconsManager.getImageView("projdown.gif"));
        nextWordBtn.setOnAction(event -> nextWord());

        findBar.getItems().addAll(findTxtFld, findResultLbl, prevWordBt, nextWordBtn);


        replaceBar = new ToolBar();
        replaceBar.setVisible(false);

        replaceTxtFld = new TextField();

        Button replaceBtn = new Button();
        replaceBtn.textProperty().bind(LC.createStringBinding("codeAreaReplaceBtn"));
        replaceBtn.setOnAction(event -> replace());

        Button replaceAllBtn = new Button();
        replaceAllBtn.textProperty().bind(LC.createStringBinding("codeAreaReplaceAllBtn"));
        replaceAllBtn.setOnAction(event -> replaceAll());

        findBar.setMinHeight(0);
        findBar.setMaxHeight(0);
        replaceBar.setMinHeight(0);
        replaceBar.setMaxHeight(0);

        replaceBar.getItems().addAll(replaceTxtFld, replaceBtn, replaceAllBtn);

    }

    private void find() {
        if (findTxtFld.getText().isEmpty()) return;
        highlightText(findTxtFld, coordinateList, currWordIndex, codeArea);
        if (coordinateList.size()==0) return;
        totalFindIndex.set(String.valueOf(coordinateList.size()));
        currFindIndex.set(String.valueOf(currWordIndex.get()+1));
    }


    private void nextWord() {
        if (coordinateList.size()==0) return;
        gotoNextWord(coordinateList, currWordIndex, codeArea);
        currFindIndex.set(String.valueOf(currWordIndex.get()+1));
    }

    private void gotoNextWord(ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea codeArea) {
        if (currWordIndex.get() >= (coordinateList.size()-1) && coordinateList.size()!=0) return;
        currWordIndex.incrementAndGet();
        int index = currWordIndex.get();
        codeArea.getCaretSelectionBind().moveTo(coordinateList.get(currWordIndex.get()).get(0));
        codeArea.setStyleClass(coordinateList.get(index).get(0), coordinateList.get(index).get(1), "findActive");
        removeHighlightedTxtInRange(coordinateList.get(index-1).get(0), coordinateList.get(index-1).get(1), codeArea);
    }


    private void prevWord() {
        if (coordinateList.size()==0) return;
        gotoPrevWord(coordinateList, currWordIndex, codeArea);
        currFindIndex.set(String.valueOf(currWordIndex.get()+1));
    }

    private void gotoPrevWord(ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea codeArea) {
        if (currWordIndex.get() <= 0 && coordinateList.size()!=0) return;
        currWordIndex.decrementAndGet();
        int index = currWordIndex.get();
        codeArea.setStyleClass(coordinateList.get(index).get(0), coordinateList.get(index).get(1), "findActive");
        codeArea.getCaretSelectionBind().moveTo(coordinateList.get(currWordIndex.get()).get(0));
        removeHighlightedTxtInRange(coordinateList.get(index+1).get(0), coordinateList.get(index+1).get(1), codeArea);
    }


    private void replace() {
        if (coordinateList.size()==0) return;
        codeArea.replaceText(coordinateList.get(currWordIndex.get()).get(0), coordinateList.get(currWordIndex.get()).get(1), replaceTxtFld.getText());
        highlightText(findTxtFld, coordinateList, currWordIndex, codeArea);
    }

    private void replaceAll() {
        if (coordinateList.size()==0) return;
        codeArea.replaceText(codeArea.getText().replaceAll("\\b(" + findTxtFld.getText() + ")\\b", replaceTxtFld.getText()));
    }


    //Text highlight

    public void removeHighlightedTxt(ArrayList<ArrayList<Integer>> coordinateList, StyleClassedTextArea currCodeArea, AtomicInteger currWordIndex) {
        if (coordinateList.size()!=0) {
            for (ArrayList<Integer> arrayList : coordinateList) {
                currCodeArea.setStyleClass(arrayList.get(0), arrayList.get(1), "");
            }
            coordinateList.clear();
            currWordIndex.set(0);
        }
    }

    public void removeHighlightedTxtInRange(int start, int end, StyleClassedTextArea currCodeArea) {
        currCodeArea.setStyleClass(start, end, "find");
    }

    public void highlightText(TextField textField, ArrayList<ArrayList<Integer>> coordinateList, AtomicInteger currWordIndex, StyleClassedTextArea currCodeArea) {
        removeHighlightedTxt(coordinateList, currCodeArea, currWordIndex);
        Pattern pattern = Pattern.compile("\\b("+textField.getText()+")\\b");
        Matcher matcher = pattern.matcher(currCodeArea.getText());
        while (matcher.find()) {
            currCodeArea.setStyleClass(matcher.start(), matcher.end(), "find");
            coordinateList.add(new ArrayList<>(Arrays.asList(matcher.start(), matcher.end())));
        }
        if (coordinateList.size()!=0) currCodeArea.getCaretSelectionBind().moveTo(coordinateList.get(0).get(0));
        currCodeArea.requestFollowCaret();
    }


    //Accelerator events

    public void undo() {
        codeArea.undo();
    }

    public void redo() {
        codeArea.redo();
    }

    void cut() {
        if (codeArea.getSelectedText().length()==0) codeArea.selectLine();
        codeArea.cut();
    }

    void copy() {
        if (codeArea.getSelectedText().length()==0) {
            codeArea.selectLine();
            codeArea.copy();
            return;
        }
        codeArea.copy();
    }

    void paste(){
        if (codeArea.getSelectedText().length()==0) codeArea.selectLine();
        codeArea.paste();
    }

    void delete(){
        codeArea.deleteText(codeArea.getSelection());
    }

    void duplicate(){
        if (codeArea.getSelectedText().length()==0) return;
        codeArea.insertText(codeArea.getCaretPosition(), codeArea.getSelectedText());
    }

    void selectAll(){
        codeArea.selectAll();
    }


    public StyleClassedTextArea getCodeArea(){
        return codeArea;
    }

    public List<MenuItem> getEditMenuItems(){
        return menu.getMenuItems();
    }

    public void recalculateAccelerators(){

        if (this.getScene() == null) return;

        this.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
                new Runnable() {
                    @FXML
                    public void run() {
                        findBar.setVisible(true);
                        replaceBar.setVisible(false);
                        findBar.setMinHeight(-1);
                        findBar.setMaxHeight(-1);
                        replaceBar.setMinHeight(0);
                        replaceBar.setMaxHeight(0);
                    }
                }
        );

        this.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN),
                new Runnable() {
                    @FXML
                    public void run() {
                        findBar.setVisible(true);
                        replaceBar.setVisible(true);
                        findBar.setMinHeight(-1);
                        findBar.setMaxHeight(-1);
                        replaceBar.setMinHeight(-1);
                        replaceBar.setMaxHeight(-1);
                    }
                }
        );

        this.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE),
                new Runnable() {
                    @FXML
                    public void run() {
                        findBar.setVisible(false);
                        replaceBar.setVisible(false);

                        findBar.setMinHeight(0);
                        findBar.setMaxHeight(0);
                        replaceBar.setMinHeight(0);
                        replaceBar.setMaxHeight(0);

                        removeHighlightedTxt(coordinateList, codeArea, currWordIndex);
                        /*
                         * After closing the popup dialog the color of keywords is also changed.
                         * So in order to re-highlight the syntax I am appending and deleting the text to fire plaintext change event.
                         * I think, there are some better way, but until that I am going with this one.
                         */
                        codeArea.appendText(" ");
                        int len = codeArea.getText().length();
                        codeArea.deleteText(len-1, len);

                    }
                }
        );

    }

    @Override
    public void copyAccelerators(){
        if (this.getScene() != proj.getFrameController().getStage().getScene()){
            this.getScene().getAccelerators().putAll(
                    proj.getFrameController().getStage().getScene().getAccelerators()
            );
        }
        recalculateAccelerators();
    }

}

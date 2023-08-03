package LogisimFX.newgui.MainFrame.EditorTabs.TextEditor;

import LogisimFX.IconsManager;
import LogisimFX.newgui.MainFrame.LC;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

public class
TextEditorToolBar extends ToolBar {

	public static class ToolTip extends Tooltip {

		public ToolTip(String text){
			super();
			textProperty().bind(LC.createStringBinding(text));
		}

	}

	private final int prefWidth = 15;
	private final int prefHeight = 15;

	private TextEditor textEditor;

	public TextEditorToolBar(TextEditor textEditor){
		super();
		this.textEditor = textEditor;
		initButtons();
		this.setMaxHeight(-1);
		this.setMaxWidth(-1);
	}

	private void initButtons(){

		Button findBtn = new Button();
		findBtn.graphicProperty().setValue(IconsManager.getIcon("find.png"));
		findBtn.setTooltip(new ToolTip("codeAreaFindBtn"));
		findBtn.setOnAction(event -> textEditor.openFindBar());
		findBtn.setPrefSize(prefWidth,prefHeight);
		findBtn.setMinSize(prefWidth,prefHeight);
		findBtn.setMaxSize(prefWidth,prefHeight);

		Button findAndReplaceBtn = new Button();
		findAndReplaceBtn.graphicProperty().setValue(IconsManager.getIcon("findreplace.png"));
		findAndReplaceBtn.setTooltip(new ToolTip("codeAreaFindAndReplaceBtn"));
		findAndReplaceBtn.setOnAction(event -> textEditor.openReplaceBar());
		findAndReplaceBtn.setPrefSize(prefWidth,prefHeight);
		findAndReplaceBtn.setMinSize(prefWidth,prefHeight);
		findAndReplaceBtn.setMaxSize(prefWidth,prefHeight);

		Button saveFileBtn = new Button();
		saveFileBtn.graphicProperty().setValue(IconsManager.getIcon("savefile.gif"));
		saveFileBtn.setTooltip(new ToolTip("codeAreaSaveBtn"));
		saveFileBtn.setOnAction(event -> textEditor.doSave());
		saveFileBtn.setPrefSize(prefWidth,prefHeight);
		saveFileBtn.setMinSize(prefWidth,prefHeight);
		saveFileBtn.setMaxSize(prefWidth,prefHeight);

		Button deleteFileBtn = new Button();
		deleteFileBtn.graphicProperty().setValue(IconsManager.getIcon("deletefile.gif"));
		deleteFileBtn.setTooltip(new ToolTip("codeAreaDeleteBtn"));
		deleteFileBtn.setOnAction(event -> textEditor.doDelete());
		deleteFileBtn.setPrefSize(prefWidth,prefHeight);
		deleteFileBtn.setMinSize(prefWidth,prefHeight);
		deleteFileBtn.setMaxSize(prefWidth,prefHeight);

		getItems().addAll(
				findBtn,
				findAndReplaceBtn,
				saveFileBtn,
				deleteFileBtn
		);

	}

}

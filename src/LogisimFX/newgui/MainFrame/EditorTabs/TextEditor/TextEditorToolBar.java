package LogisimFX.newgui.MainFrame.EditorTabs.TextEditor;

import LogisimFX.IconsManager;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.proj.Project;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

public class TextEditorToolBar extends ToolBar {

	public static class ToolTip extends Tooltip {

		public ToolTip(String text){
			super();
			textProperty().bind(LC.createStringBinding(text));
		}

	}

	private final int prefWidth = 15;
	private final int prefHeight = 15;

	private Project proj;
	private TextEditor codeEditor;

	public TextEditorToolBar(Project project, TextEditor codeEditor){
		super();
		this.proj = project;
		this.codeEditor = codeEditor;
		initButtons();
		this.setMaxHeight(-1);
		this.setMaxWidth(-1);
	}

	private void initButtons(){

		Button find = new Button();
		find.graphicProperty().setValue(IconsManager.getIcon("find.png"));
		find.setTooltip(new ToolTip("codeAreaFindBtn"));
		find.setOnAction(event -> codeEditor.openFindBar());
		find.setPrefSize(prefWidth,prefHeight);
		find.setMinSize(prefWidth,prefHeight);
		find.setMaxSize(prefWidth,prefHeight);

		Button findAndReplace = new Button();
		findAndReplace.graphicProperty().setValue(IconsManager.getIcon("findreplace.png"));
		findAndReplace.setTooltip(new ToolTip("codeAreaFindAndReplaceBtn"));
		findAndReplace.setOnAction(event -> codeEditor.openReplaceBar());
		findAndReplace.setPrefSize(prefWidth,prefHeight);
		findAndReplace.setMinSize(prefWidth,prefHeight);
		findAndReplace.setMaxSize(prefWidth,prefHeight);

		getItems().addAll(
				find,
				findAndReplace
		);

	}

}

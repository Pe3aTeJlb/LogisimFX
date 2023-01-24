/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.OptionsFrame;

import LogisimFX.file.ToolbarData;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.tools.Tool;

class ToolbarActions {
	private ToolbarActions() { }

	public static Action addTool(ToolbarData toolbar, Tool tool) {
		return new AddTool(toolbar, tool);
	}

	public static Action removeTool(ToolbarData toolbar, int pos) {
		return new RemoveTool(toolbar, pos);
	}

	public static Action moveTool(ToolbarData toolbar,
			int src, int dest) {
		return new MoveTool(toolbar, src, dest);
	}

	public static Action addSeparator(ToolbarData toolbar,
			int pos) {
		return new AddSeparator(toolbar, pos);
	}

	public static Action removeSeparator(ToolbarData toolbar, int pos) {
		return new RemoveSeparator(toolbar, pos);
	}

	private static class AddTool extends Action {
		ToolbarData toolbar;
		Tool tool;
		int pos;

		AddTool(ToolbarData toolbar, Tool tool) {
			this.toolbar = toolbar;
			this.tool = tool;
		}

		@Override
		public String getName() {
			return LC.get("toolbarAddAction");
		}

		@Override
		public int getActionType() {
			return Action.TOOLBAR_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			pos = toolbar.getContents().size();
			toolbar.addTool(pos, tool);
		}

		@Override
		public void undo(Project proj) {
			toolbar.remove(pos);
		}
	}

	private static class RemoveTool extends Action {
		ToolbarData toolbar;
		Object removed;
		int which;

		RemoveTool(ToolbarData toolbar, int which) {
			this.toolbar = toolbar;
			this.which = which;
		}

		@Override
		public String getName() {
			return LC.get("toolbarRemoveAction");
		}

		@Override
		public int getActionType() {
			return Action.TOOLBAR_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			removed = toolbar.remove(which);
		}

		@Override
		public void undo(Project proj) {
			if (removed instanceof Tool) {
				toolbar.addTool(which, (Tool) removed);
			} else if (removed == null) {
				toolbar.addSeparator(which);
			}
		}
	}

	private static class MoveTool extends Action {
		ToolbarData toolbar;
		int oldpos;
		int dest;

		MoveTool(ToolbarData toolbar, int oldpos, int dest) {
			this.toolbar = toolbar;
			this.oldpos = oldpos;
			this.dest = dest;
		}

		@Override
		public String getName() {
			return LC.get("toolbarMoveAction");
		}

		@Override
		public int getActionType() {
			return Action.TOOLBAR_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			toolbar.move(oldpos, dest);
		}

		@Override
		public void undo(Project proj) {
			toolbar.move(dest, oldpos);
		}
		
		@Override
		public boolean shouldAppendTo(Action other) {
			if (other instanceof MoveTool) {
				MoveTool o = (MoveTool) other;
				return this.toolbar == o.toolbar
					&& o.dest == this.oldpos;
			} else {
				return false;
			}
		}

		@Override
		public Action append(Action other) {
			if (other instanceof MoveTool) {
				MoveTool o = (MoveTool) other;
				if (this.toolbar == o.toolbar && this.dest == o.oldpos) {
					// TODO if (this.oldpos == o.dest) return null;
					return new MoveTool(toolbar, this.oldpos, o.dest);
				}
			}
			return super.append(other);
		}
	}

	private static class AddSeparator extends Action {
		ToolbarData toolbar;
		int pos;

		AddSeparator(ToolbarData toolbar, int pos) {
			this.toolbar = toolbar;
			this.pos = pos;
		}

		@Override
		public String getName() {
			return LC.get("toolbarInsertSepAction");
		}

		@Override
		public int getActionType() {
			return Action.TOOLBAR_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			toolbar.addSeparator(pos);
		}

		@Override
		public void undo(Project proj) {
			toolbar.remove(pos);
		}
	}

	private static class RemoveSeparator extends Action {
		ToolbarData toolbar;
		int pos;

		RemoveSeparator(ToolbarData toolbar, int pos) {
			this.toolbar = toolbar;
			this.pos = pos;
		}

		@Override
		public String getName() {
			return LC.get("toolbarRemoveSepAction");
		}

		@Override
		public int getActionType() {
			return Action.TOOLBAR_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			toolbar.remove(pos);
		}

		@Override
		public void undo(Project proj) {
			toolbar.addSeparator(pos);
		}
	}

}

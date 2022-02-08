/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.proj;

import java.util.Arrays;
import java.util.List;

public class JoinedAction extends Action {
	Action[] todo;

	JoinedAction(Action... actions) {
		todo = actions;
	}

	public Action getFirstAction() {
		return todo[0];
	}

	public Action getLastAction() {
		return todo[todo.length - 1];
	}

	public List<Action> getActions() {
		return Arrays.asList(todo);
	}

	@Override
	public boolean isModification() {
		for (Action act : todo) {
			if (act.isModification()) return true;
		}
		return false;
	}

	@Override
	public String getName() { return todo[0].getName(); }

	@Override
	public void doIt(Project proj) {
		for (Action act : todo) {
			act.doIt(proj);
		}
	}

	@Override
	public void undo(Project proj) {
		for (int i = todo.length - 1; i >= 0; i--) {
			todo[i].undo(proj);
		}
	}

	@Override
	public Action append(Action other) {
		int oldLen = todo.length;
		Action[] newToDo = new Action[oldLen + 1];
		System.arraycopy(todo, 0, newToDo, 0, oldLen);
		newToDo[oldLen] = other;
		todo = newToDo;
		return this;
	}
}
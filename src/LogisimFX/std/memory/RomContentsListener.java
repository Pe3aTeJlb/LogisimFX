/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.newgui.HexEditorFrame.HexModel;
import LogisimFX.newgui.HexEditorFrame.HexModelListener;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.std.LC;

public class RomContentsListener implements HexModelListener {

	private static class Change extends Action {

		private RomContentsListener source;
		private MemContents contents;
		private long start;
		private int[] oldValues;
		private int[] newValues;
		private boolean completed = true;
		
		Change(RomContentsListener source, MemContents contents,
               long start, int[] oldValues, int[] newValues) {

			this.source = source;
			this.contents = contents;
			this.start = start;
			this.oldValues = oldValues;
			this.newValues = newValues;

		}

		@Override
		public String getName() {
			return LC.get("romChangeAction");
		}

		@Override
		public int getActionType() {
			return Action.ROM_CONTENTS_ACTION;
		}

		@Override
		public void doIt(Project proj) {

			if (!completed) {
				completed = true;
				try {
					source.setEnabled(false);
					contents.set(start, newValues);
				} finally {
					source.setEnabled(true);
				}
			}

		}

		@Override
		public void undo(Project proj) {

			if (completed) {
				completed = false;
				try {
					source.setEnabled(false);
					contents.set(start, oldValues);
				} finally {
					source.setEnabled(true);
				}
			}

		}
		
		@Override
		public boolean shouldAppendTo(Action other) {

			if (other instanceof Change) {
				Change o = (Change) other;
				long oEnd = o.start + o.newValues.length;
				long end = start + newValues.length;
				if (oEnd >= start && end >= o.start) return true;
			}

			return super.shouldAppendTo(other);

		}
		
		@Override
		public Action append(Action other) {

			if (other instanceof Change) {
				Change o = (Change) other;
				long oEnd = o.start + o.newValues.length;
				long end = start + newValues.length;
				if (oEnd >= start && end >= o.start) {
					long nStart = Math.min(start, o.start);
					long nEnd = Math.max(end, oEnd);
					int[] nOld = new int[(int) (nEnd - nStart)];
					int[] nNew = new int[(int) (nEnd - nStart)];
					System.arraycopy(o.oldValues, 0, nOld, (int) (o.start - nStart), o.oldValues.length);
					System.arraycopy(oldValues,   0, nOld, (int) (start - nStart),   oldValues.length);
					System.arraycopy(newValues,   0, nNew, (int) (start - nStart),   newValues.length);
					System.arraycopy(o.newValues, 0, nNew, (int) (o.start - nStart), o.newValues.length);
					return new Change(source, contents, nStart, nOld, nNew);
				}
			}

			return super.append(other);

		}

	}

	Project proj;
	boolean enabled = true;
	
	RomContentsListener(Project proj) {
		this.proj = proj;
	}
	
	void setEnabled(boolean value) {
		enabled = value;
	}
	
	public void metainfoChanged(HexModel source) {
		// ignore - this can only come from an already-registered
		// action
	}
	
	public void bytesChanged(HexModel source, long start,
			long numBytes, int[] oldValues) {

		if (enabled && proj != null && oldValues != null) {
			// this change needs to be logged in the undo log
			int[] newValues = new int[oldValues.length];
			for (int i = 0; i < newValues.length; i++) {
				newValues[i] = source.get(start + i);
			}
			proj.doAction(new Change(this, (MemContents) source,
					start, oldValues, newValues));
		}

	}

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas;

import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Location;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Action;
import LogisimFX.proj.JoinedAction;
import LogisimFX.proj.Project;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.newgui.MainFrame.LC;

import java.util.*;

public class SelectionActions {

	private SelectionActions() { }
	
	public static Action drop(Selection sel, Collection<Component> comps) {
		HashSet<Component> floating = new HashSet<>(sel.getFloatingComponents());
		HashSet<Component> anchored = new HashSet<>(sel.getAnchoredComponents());
		ArrayList<Component> toDrop = new ArrayList<>();
		ArrayList<Component> toIgnore = new ArrayList<>();
		for (Component comp : comps) {
			if (floating.contains(comp)) {
				toDrop.add(comp);
			} else if (anchored.contains(comp)) {
				toDrop.add(comp);
				toIgnore.add(comp);
			}
		}
		int numDrop = toDrop.size() - toIgnore.size();
		if (numDrop == 0) {
			for (Component comp : toIgnore) {
				sel.remove(null, comp);
			}
			return null;
		} else {
			return new Drop(sel, toDrop, numDrop);
		}
	}
	
	public static Action dropAll(Selection sel) {
		return drop(sel, sel.getComponents());
	}

	public static Action clear(Selection sel) {
		return new Delete(sel);
	}
	
	public static Action duplicate(Selection sel) {
		return new Duplicate(sel);
	}

	public static Action cut(Selection sel) {
		return new Cut(sel);
	}

	public static Action copy(Selection sel) {
		return new Copy(sel);
	}

	public static Action pasteMaybe(Project proj, Selection sel) {
		HashMap<Component, Component> replacements = getReplacementMap(proj);
		return new Paste(sel, replacements);
	}
	
	public static Action translate(Selection sel, int dx, int dy, ReplacementMap repl) {
		return new Translate(sel, dx, dy, repl);
	}
	
	private static class Drop extends Action {
		private Selection sel;
		private Component[] drops;
		private int numDrops;
		private SelectionSave before;
		private CircuitTransaction xnReverse;
		
		Drop(Selection sel, Collection<Component> toDrop, int numDrops) {
			this.sel = sel;
			this.drops = new Component[toDrop.size()];
			toDrop.toArray(this.drops);
			this.numDrops = numDrops;
			this.before = SelectionSave.create(sel);
		}

		@Override
		public String getName() {
			return numDrops == 1 ? LC.get("dropComponentAction")
					: LC.get("dropComponentsAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			Circuit circuit = proj.getCurrentCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);
			for (Component comp : drops) {
				sel.remove(xn, comp);
			}
			CircuitTransactionResult result = xn.execute();
			xnReverse = result.getReverseTransaction();
		}

		@Override
		public void undo(Project proj) {
			xnReverse.execute();
		}

		@Override
		public boolean shouldAppendTo(Action other) {
			Action last;
			if (other instanceof JoinedAction) last = ((JoinedAction) other).getLastAction();
			else last = other;
			
			SelectionSave otherAfter = null;
			if (last instanceof Paste) {
				otherAfter = ((Paste) last).after;
			} else if (last instanceof Duplicate) {
				otherAfter = ((Duplicate) last).after;
			}
			return otherAfter != null && otherAfter.equals(this.before);
		}
	}

	private static class Delete extends Action {
		private Selection sel;
		private CircuitTransaction xnReverse;

		Delete(Selection sel) {
			this.sel = sel;
		}

		@Override
		public String getName() {
			return LC.get("deleteSelectionAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			Circuit circuit = proj.getCurrentCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);
			sel.deleteAllHelper(xn);
			CircuitTransactionResult result = xn.execute();
			xnReverse = result.getReverseTransaction();
		}

		@Override
		public void undo(Project proj) {
			xnReverse.execute();
		}
	}

	private static class Duplicate extends Action {
		private Selection sel;
		private CircuitTransaction xnReverse;
		private SelectionSave after;

		Duplicate(Selection sel) {
			this.sel = sel;
		}

		@Override
		public String getName() {
			return LC.get("duplicateSelectionAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			Circuit circuit = proj.getCurrentCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);
			sel.duplicateHelper(xn);

			CircuitTransactionResult result = xn.execute();
			xnReverse = result.getReverseTransaction();
			after = SelectionSave.create(sel);
		}

		@Override
		public void undo(Project proj) {
			xnReverse.execute();
		}
	}

	private static class Cut extends Action {
		private Action first;
		private Action second;

		Cut(Selection sel) {
			first = new Copy(sel);
			second = new Delete(sel);
		}

		@Override
		public String getName() {
			return LC.get("cutSelectionAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			first.doIt(proj);
			second.doIt(proj);
		}

		@Override
		public void undo(Project proj) {
			second.undo(proj);
			first.undo(proj);
		}
	}

	private static class Copy extends Action {
		private Selection sel;
		private Clipboard oldClip;

		Copy(Selection sel) {
			this.sel = sel;
		}

		@Override
		public boolean isModification() { return false; }

		@Override
		public String getName() {
			return LC.get("copySelectionAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			oldClip = Clipboard.get();
			Clipboard.set(sel, sel.getAttributeSet());
		}

		@Override
		public void undo(Project proj) {
			Clipboard.set(oldClip);
		}
	}
	
	private static HashMap<Component, Component> getReplacementMap(Project proj) {
		HashMap<Component, Component> replMap;
		replMap = new HashMap<Component, Component>();
		
		LogisimFile file = proj.getLogisimFile();
		ArrayList<Library> libs = new ArrayList<Library>();
		libs.add(file);
		libs.addAll(file.getLibraries());
		
		ArrayList<String> dropped = null; 
		Clipboard clip = Clipboard.get();
		Collection<Component> comps = clip.getComponents();
		HashMap<ComponentFactory, ComponentFactory> factoryReplacements;
		factoryReplacements = new HashMap<ComponentFactory, ComponentFactory>();
		for (Component comp : comps) {
			if (comp instanceof Wire) continue;
			
			ComponentFactory compFactory = comp.getFactory();
			ComponentFactory copyFactory = findComponentFactory(compFactory, libs, false);
			if (factoryReplacements.containsKey(compFactory)) {
				copyFactory = factoryReplacements.get(compFactory);
			} else if (copyFactory == null) {
				ComponentFactory candidate = findComponentFactory(compFactory, libs, true);
				if (candidate == null) {
					if (dropped == null) {
						dropped = new ArrayList<String>();
					}
					dropped.add(compFactory.getDisplayName().getValue());
				} else {
					String msg = LC.getFormatted("pasteCloneQuery",
							compFactory.getName());
					Object[] opts = { LC.get("pasteCloneReplace"),
							LC.get("pasteCloneIgnore"),
							LC.get("pasteCloneCancel") };
					int select = DialogManager.CreateConfirmWarningDialog(LC.get("pasteCloneTitle"),"");
					if (select == 0) {
						copyFactory = candidate;
					} else if (select == 1) {
						copyFactory = null;
					} else {
						return null;
					}
					factoryReplacements.put(compFactory, copyFactory);
				}
			}
			
			if (copyFactory == null) {
				replMap.put(comp, null);
			} else if (copyFactory != compFactory) {
				Location copyLoc = comp.getLocation();
				AttributeSet copyAttrs = (AttributeSet) comp.getAttributeSet().clone();
				Component copy = copyFactory.createComponent(copyLoc, copyAttrs);
				replMap.put(comp, copy);
			}
		}
		
		if (dropped != null) {
			Collections.sort(dropped);
			StringBuilder droppedStr = new StringBuilder();
			droppedStr.append(LC.get("pasteDropMessage"));
			String curName = dropped.get(0);
			int curCount = 1;
			int lines = 1;
			for (int i = 1; i <= dropped.size(); i++) {
				String nextName = i == dropped.size() ? "" : dropped.get(i);
				if (nextName.equals(curName)) {
					curCount++;
				} else {
					lines++;
					droppedStr.append("\n  ");
					droppedStr.append(curName);
					if (curCount > 1) {
						droppedStr.append(" \u00d7 " + curCount);
					}
					
					curName = nextName;
					curCount = 1;
				}
			}
			
			DialogManager.CreateScrollWarning(LC.get("pasteDropTitle"),droppedStr.toString());

		}
		
		return replMap;
	}
	
	private static ComponentFactory findComponentFactory(ComponentFactory factory,
			ArrayList<Library> libs, boolean acceptNameMatch) {
		String name = factory.getName();
		for (Library lib : libs) {
			for (Tool tool : lib.getTools()) {
				if (tool instanceof AddTool) {
					AddTool addTool = (AddTool) tool;
					if (name.equals(addTool.getName())) {
						ComponentFactory fact = addTool.getFactory(true);
						if (acceptNameMatch) {
							return fact;
						} else if (fact == factory) {
							return fact;
						} else if (fact.getClass() == factory.getClass()
								&& !(fact instanceof SubcircuitFactory)) {
							return fact;
						}
					}
				}
			}
		}
		return null;
	}

	private static class Paste extends Action {
		private Selection sel;
		private CircuitTransaction xnReverse;
		private SelectionSave after;
		private HashMap<Component,Component> componentReplacements; 

		Paste(Selection sel, HashMap<Component, Component> replacements) {
			this.sel = sel;
			this.componentReplacements = replacements;
		}

		@Override
		public String getName() {
			return LC.get("pasteClipboardAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			Clipboard clip = Clipboard.get();
			Circuit circuit = proj.getCurrentCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);
			Collection<Component> comps = clip.getComponents();
			Collection<Component> toAdd = computeAdditions(comps);
			if (toAdd.size() > 0) {
				sel.pasteHelper(xn, toAdd);
				CircuitTransactionResult result = xn.execute();
				xnReverse = result.getReverseTransaction();
				after = SelectionSave.create(sel);
			} else {
				xnReverse = null;
			}
		}
		
		private Collection<Component> computeAdditions(Collection<Component> comps) {
			HashMap<Component, Component> replMap = componentReplacements;
			ArrayList<Component> toAdd = new ArrayList<Component>(comps.size());
			for (Iterator<Component> it = comps.iterator(); it.hasNext(); ) {
				Component comp = it.next();
				if (replMap.containsKey(comp)) {
					Component repl = replMap.get(comp);
					if (repl != null) {
						toAdd.add(repl);
					}
				} else {
					toAdd.add(comp);
				}
			}
			return toAdd;
		}
		
		@Override
		public void undo(Project proj) {
			if (xnReverse != null) {
				xnReverse.execute();
			}
		}
	}

	private static class Translate extends Action {
		private Selection sel;
		private int dx;
		private int dy;
		private ReplacementMap replacements;
		private SelectionSave before;
		private CircuitTransaction xnReverse;
		
		Translate(Selection sel, int dx, int dy, ReplacementMap replacements) {
			this.sel = sel;
			this.dx = dx;
			this.dy = dy;
			this.replacements = replacements;
			this.before = SelectionSave.create(sel);
		}

		@Override
		public String getName() {
			return LC.get("moveSelectionAction");
		}

		@Override
		public int getActionType() {
			return Action.LAYOUT_SELECTION_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			Circuit circuit = proj.getCurrentCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);

			sel.translateHelper(xn, dx, dy);
			if (replacements != null) {
				xn.replace(replacements);
			}

			CircuitTransactionResult result = xn.execute();
			xnReverse = result.getReverseTransaction();
		}

		@Override
		public void undo(Project proj) {
			xnReverse.execute();
		}

		@Override
		public boolean shouldAppendTo(Action other) {
			Action last;
			if (other instanceof JoinedAction) last = ((JoinedAction) other).getLastAction();
			else last = other;
			
			SelectionSave otherAfter = null;
			if (last instanceof Paste) {
				otherAfter = ((Paste) last).after;
			} else if (last instanceof Duplicate) {
				otherAfter = ((Duplicate) last).after;
			}
			return otherAfter != null && otherAfter.equals(this.before);
		}
	}

}

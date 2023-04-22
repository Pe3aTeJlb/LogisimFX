/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectActions;
import LogisimFX.circuit.Circuit;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.std.base.Text;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;

import java.util.*;

public class LogisimFileActions {

	private LogisimFileActions() { }

	public static Action addCircuit(Circuit circuit) {
		return new AddCircuit(circuit);
	}

	public static Action removeCircuit(Circuit circuit) {
		return new RemoveCircuit(circuit);
	}
	
	public static Action moveCircuit(AddTool tool, int toIndex) {
		return new MoveCircuit(tool, toIndex);
	}

	public static Action loadLibrary(Library lib, LogisimFile source) {
		return new LoadLibraries(new Library[] { lib }, source);
	}

	public static Action loadLibraries(Library[] libs, LogisimFile source) {
		return new LoadLibraries(libs, source);
	}

	public static Action unloadLibrary(Library lib) {
		return new UnloadLibraries(new Library[] { lib });
	}

	public static Action unloadLibraries(Library[] libs) {
		return new UnloadLibraries(libs);
	}

	public static Action setMainCircuit(Circuit circuit) {
		return new SetMainCircuit(circuit);
	}

	public static Action revertDefaults() {
		return new RevertDefaults();
	}

	private static class AddCircuit extends Action {
		private Circuit circuit;

		AddCircuit(Circuit circuit) {
			this.circuit = circuit;
		}

		@Override
		public String getName() {
			return LC.get("addCircuitAction");
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			proj.getLogisimFile().addCircuit(circuit);
		}

		@Override
		public void undo(Project proj) {
			proj.getLogisimFile().removeCircuit(circuit);
		}
	}

	private static class RemoveCircuit extends Action {
		private Circuit circuit;
		private int index;

		RemoveCircuit(Circuit circuit) {
			this.circuit = circuit;
		}

		@Override
		public String getName() {
			return LC.get("removeCircuitAction");
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			index = proj.getLogisimFile().getCircuits().indexOf(circuit);
			proj.getLogisimFile().removeCircuit(circuit);
		}

		@Override
		public void undo(Project proj) {
			proj.getLogisimFile().addCircuit(circuit, index);
		}
	}

	private static class MoveCircuit extends Action {
		private AddTool tool;
		private int fromIndex;
		private int toIndex;

		MoveCircuit(AddTool tool, int toIndex) {
			this.tool = tool;
			this.toIndex = toIndex;
		}

		@Override
		public String getName() {
			return LC.get("moveCircuitAction");
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			fromIndex = proj.getLogisimFile().getTools().indexOf(tool);
			proj.getLogisimFile().moveCircuit(tool, toIndex);
		}

		@Override
		public void undo(Project proj) {
			proj.getLogisimFile().moveCircuit(tool, fromIndex);
		}

		@Override
		public boolean shouldAppendTo(Action other) {
			return other instanceof MoveCircuit
				&& ((MoveCircuit) other).tool == this.tool;
		}

		@Override
		public Action append(Action other) {
			MoveCircuit ret = new MoveCircuit(tool, ((MoveCircuit) other).toIndex);
			ret.fromIndex = this.toIndex;
			return ret.fromIndex == ret.toIndex ? null : ret;
		}
	}

	private static class LoadLibraries extends Action {
		private final List<Library> mergedLibs = new ArrayList<>();
		private final Set<String> baseLibsToEnable = new HashSet<>();

		LoadLibraries(Library[] libs, LogisimFile source) {
			final var libNames = new HashMap<String, Library>();
			final var toolList = new HashSet<String>();
			final var errors = new HashMap<String, String>();
			for (final var newLib : libs) {
				// first cleanup step: remove unused libraries from loaded library
				LibraryManager.removeUnusedLibraries(newLib);
				// second cleanup step: promote base libraries
				baseLibsToEnable.addAll(LibraryManager.getUsedBaseLibraries(newLib));
			}
			// promote the none visible base libraries to toplevel
			final var builtinLibraries = LibraryManager.getBuildinNames(source.getLoader());
			for (final var lib : source.getLibraries()) {
				final var libName = lib.getName();
				if (baseLibsToEnable.contains(libName) || !builtinLibraries.contains(libName)) {
					baseLibsToEnable.remove(libName);
				}
			}
			// remove the promoted base libraries from the loaded library
			for (final var newLib : libs) {
				LibraryManager.removeBaseLibraries(newLib, baseLibsToEnable);
			}
			for (final var lib : source.getLibraries()) {
				LibraryTools.buildLibraryList(lib, libNames);
			}
			LibraryTools.buildToolList(source, toolList);
			for (final var lib : libs) {
				if (libNames.containsKey(lib.getName().toUpperCase())) {
					DialogManager.createWarningDialog( LC.get("LibLoadErrors") + " " + lib.getName() + " !", "\"" + lib.getName() + "\": " + LC.get("LibraryAlreadyLoaded"));
				} else {
					LibraryTools.removePresentLibraries(lib, libNames, false);
					if (LibraryTools.isLibraryConform(lib, new HashSet<>(), new HashSet<>(), errors)) {
						final var addedToolList = new HashSet<String>();
						LibraryTools.buildToolList(lib, addedToolList);
						for (final var tool : addedToolList)
							if (toolList.contains(tool))
								errors.put(tool, LC.get("LibraryMultipleToolError"));
						if (errors.keySet().isEmpty()) {
							LibraryTools.buildLibraryList(lib, libNames);
							toolList.addAll(addedToolList);
							mergedLibs.add(lib);
						} else {
							LibraryTools.showErrors(lib.getName(), errors);
							baseLibsToEnable.clear();
						}
					} else
						LibraryTools.showErrors(lib.getName(), errors);
				}
			}
		}

		@Override
		public void doIt(Project proj) {
			for (final var lib : baseLibsToEnable) {
				final var logisimFile = proj.getLogisimFile();
				logisimFile.addLibrary(logisimFile.getLoader().getBuiltin().getLibrary(lib));
			}
			for (final var lib : mergedLibs) {
				if (lib instanceof LoadedLibrary) {
					if (((LoadedLibrary)lib).getBase() instanceof LogisimFile) {
						repair(proj, ((LoadedLibrary)lib).getBase());
					}
				} else if (lib instanceof LogisimFile) {
					repair(proj, lib);
				}
				proj.getLogisimFile().addLibrary(lib);
			}
		}

		private void repair(Project proj, Library lib) {
			final var availableTools = new HashMap<String, AddTool>();
			LibraryTools.buildToolList(proj.getLogisimFile(), availableTools);
			if (lib instanceof LogisimFile) {
				for (final var circ : ((LogisimFile)lib).getCircuits()) {
					for (final var tool : circ.getNonWires()) {
						if (availableTools.containsKey(tool.getFactory().getName().toUpperCase())) {
							final var current = availableTools.get(tool.getFactory().getName().toUpperCase());
							if (current != null) {
								tool.setFactory(current.getFactory());
							} else if ("Text".equals(tool.getFactory().getName())) {
								final var newComp = Text.FACTORY.createComponent(tool.getLocation(), (AttributeSet) tool.getAttributeSet().clone());
								tool.setFactory(newComp.getFactory());
							} else
								System.out.println("Not found:" + tool.getFactory().getName());
						}
					}
				}
			}
			for (final var libs : lib.getLibraries()) {
				repair(proj, libs);
			}
		}

		@Override
		public boolean isModification() {
			return !mergedLibs.isEmpty();
		}

		@Override
		public String getName() {
			return (mergedLibs.size() <= 1) ? LC.get("loadLibraryAction") : LC.get("loadLibrariesAction");
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void undo(Project proj) {
			for (final var lib : mergedLibs) proj.getLogisimFile().removeLibrary(lib);
			for (final var lib : baseLibsToEnable) proj.getLogisimFile().removeLibrary(lib);
		}
	}

	private static class UnloadLibraries extends Action {
		private Library[] libs;

		UnloadLibraries(Library[] libs) {
			this.libs = libs;
		}

		@Override
		public String getName() {
			if (libs.length == 1) {
				return LC.get("unloadLibraryAction");
			} else {
				return LC.get("unloadLibrariesAction");
			}
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			for (int i = libs.length - 1; i >= 0; i--) {
				proj.getLogisimFile().removeLibrary(libs[i]);
			}
		}

		@Override
		public void undo(Project proj) {
			for (int i = 0; i < libs.length; i++) {
				proj.getLogisimFile().addLibrary(libs[i]);
			}
		}
	}

	private static class SetMainCircuit extends Action {
		private Circuit oldval;
		private Circuit newval;

		SetMainCircuit(Circuit circuit) {
			newval = circuit;
		}

		@Override
		public String getName() {
			return LC.get("setMainCircuitAction");
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			oldval = proj.getLogisimFile().getMainCircuit();
			proj.getLogisimFile().setMainCircuit(newval);
		}

		@Override
		public void undo(Project proj) {
			proj.getLogisimFile().setMainCircuit(oldval);
		}
	}

	private static class RevertAttributeValue {
		private AttributeSet attrs;
		private Attribute<Object> attr;
		private Object value;

		RevertAttributeValue(AttributeSet attrs, Attribute<Object> attr, Object value) {
			this.attrs = attrs;
			this.attr = attr;
			this.value = value;
		}
	}

	private static class RevertDefaults extends Action {
		private Options oldOpts;
		private ArrayList<Library> libraries = null;
		private ArrayList<RevertAttributeValue> attrValues;

		RevertDefaults() {
			libraries = null;
			attrValues = new ArrayList<RevertAttributeValue>();
		}

		@Override
		public String getName() {
			return LC.get("revertDefaultsAction");
		}

		@Override
		public int getActionType() {
			return Action.LOGISIM_FILE_ACTION;
		}

		@Override
		public void doIt(Project proj) {
			LogisimFile src = ProjectActions.createNewFile(proj);
			LogisimFile dst = proj.getLogisimFile();

			copyToolAttributes(src, dst);
			for (Library srcLib : src.getLibraries()) {
				Library dstLib = dst.getLibrary(srcLib.getName());
				if (dstLib == null) {
					String desc = src.getLoader().getDescriptor(srcLib);
					dstLib = dst.getLoader().loadLibrary(proj.getLogisimFile(), desc);
					proj.getLogisimFile().addLibrary(dstLib);
					if (libraries == null) libraries = new ArrayList<Library>();
					libraries.add(dstLib);
				}
				copyToolAttributes(srcLib, dstLib);
			}

			Options newOpts = proj.getOptions();
			oldOpts = new Options();
			oldOpts.copyFrom(newOpts, dst);
			newOpts.copyFrom(src.getOptions(), dst);
		}
		
		private void copyToolAttributes(Library srcLib, Library dstLib) {
			for (Tool srcTool : srcLib.getTools()) {
				AttributeSet srcAttrs = srcTool.getAttributeSet();
				Tool dstTool = dstLib.getTool(srcTool.getName());
				if (srcAttrs != null && dstTool != null) {
					AttributeSet dstAttrs = dstTool.getAttributeSet();
					for (Attribute<?> attrBase : srcAttrs.getAttributes()) {
						@SuppressWarnings("unchecked")
						Attribute<Object> attr = (Attribute<Object>) attrBase;
						Object srcValue = srcAttrs.getValue(attr);
						Object dstValue = dstAttrs.getValue(attr);
						if (!dstValue.equals(srcValue)) {
							dstAttrs.setValue(attr, srcValue);
							attrValues.add(new RevertAttributeValue(dstAttrs, attr, dstValue));
						}
					}
				}
			}
		}

		@Override
		public void undo(Project proj) {
			proj.getOptions().copyFrom(oldOpts, proj.getLogisimFile());
			
			for (RevertAttributeValue attrValue : attrValues) {
				attrValue.attrs.setValue(attrValue.attr, attrValue.value);
			}

			if (libraries != null) {
				for (Library lib : libraries) {
					proj.getLogisimFile().removeLibrary(lib);
				}
			}
		}
	}

}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame;

import LogisimFX.circuit.Analyze;
import LogisimFX.circuit.AnalyzeException;
import LogisimFX.circuit.Circuit;
import LogisimFX.file.LogisimFileActions;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.instance.Instance;
import LogisimFX.instance.StdAttr;
import LogisimFX.localization.LC_menu;
import LogisimFX.newgui.AnalyzeFrame.AnalyzeController;
import LogisimFX.newgui.AnalyzeFrame.AnalyzerModel;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.FrameManager;
import LogisimFX.proj.Project;
import LogisimFX.std.wiring.Pin;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.util.StringUtil;
import LogisimFX.util.SyntaxChecker;

import java.util.ArrayList;
import java.util.Map;

public class ProjectCircuitActions {


	public static void doAddCircuit(Project proj) {

		String circuitName = DialogManager.createInputDialog(proj.getLogisimFile());

		String error = null;

		if (circuitName == null || circuitName.isEmpty()) {
			error = LC_menu.getInstance().get("circuitNameMissingError");
		} else if (CorrectLabel.isKeyword(circuitName, false)) {
			error = circuitName + "\": " + LC_menu.getInstance().get("circuitNameKeyword");
		} else if (!SyntaxChecker.isVariableNameAcceptable(circuitName, false)) {
			error = circuitName + "\": " + LC_menu.getInstance().get("circuitNameInvalidName");
		} else if (nameIsInUse(proj, circuitName)) {
			error = circuitName + "\": " + LC_menu.getInstance().get("circuitNameExists");
		}
		if (error != null) {
			DialogManager.createErrorDialog("Error", error);
		} else {
			Circuit circuit = new Circuit(circuitName);
			proj.doAction(LogisimFileActions.addCircuit(circuit));
			proj.setCurrentCircuit(circuit);
		}

	}

	private static boolean nameIsInUse(Project proj, String name) {
		for (Library mylib : proj.getLogisimFile().getLibraries()) {
			if (nameIsInLibraries(mylib, name)) return true;
		}
		for (AddTool mytool : proj.getLogisimFile().getTools()) {
			if (name.equalsIgnoreCase(mytool.getName())) return true;
		}
		return false;
	}

	private static boolean nameIsInLibraries(Library lib, String name) {
		for (final var myLib : lib.getLibraries()) {
			if (nameIsInLibraries(myLib, name)) return true;
		}
		for (final var myTool : lib.getTools()) {
			if (name.equalsIgnoreCase(myTool.getName())) return true;
		}
		return false;
	}


	public static void doMoveCircuit(Project proj, Circuit cur, int delta) {
		AddTool tool = proj.getLogisimFile().getAddTool(cur);
		if (tool != null) {
			int oldPos = proj.getLogisimFile().getCircuits().indexOf(cur);
			int newPos = oldPos + delta;
			int toolsCount = proj.getLogisimFile().getTools().size();
			if (newPos >= 0 && newPos < toolsCount) {
				proj.doAction(LogisimFileActions.moveCircuit(tool, newPos));
			}
		}
	}

	public static void doSetAsMainCircuit(Project proj, Circuit circuit) {
		proj.doAction(LogisimFileActions.setMainCircuit(circuit));
	}

	public static void doRemoveCircuit(Project proj, Circuit circuit) {

		if (proj.getLogisimFile().getTools().size() == 1) {
			DialogManager.createErrorDialog(LC_menu.getInstance().get("circuitRemoveErrorTitle"),LC_menu.getInstance().get("circuitRemoveLastError"));
		} else if (!proj.getDependencies().canRemove(circuit)) {
			DialogManager.createErrorDialog(LC_menu.getInstance().get("circuitRemoveErrorTitle"),LC_menu.getInstance().get("circuitRemoveUsedError"));
		} else {
			proj.doAction(LogisimFileActions.removeCircuit(circuit));
		}

	}
	
	public static void doAnalyze(Project proj, Circuit circuit) {
		Map<Instance, String> pinNames = Analyze.getPinLabels(circuit);
		ArrayList<String> inputNames = new ArrayList<>();
		ArrayList<String> outputNames = new ArrayList<>();
		for (Map.Entry<Instance, String> entry : pinNames.entrySet()) {
			Instance pin = entry.getKey();
			boolean isInput = Pin.FACTORY.isInputPin(pin);
			if (isInput) {
				inputNames.add(entry.getValue());
			} else {
				outputNames.add(entry.getValue());
			}
			if (pin.getAttributeValue(StdAttr.WIDTH).getWidth() > 1) {
				if (isInput) {
					analyzeError(proj, LC_menu.getInstance().get("analyzeMultibitInputError"));
				} else {
					analyzeError(proj, LC_menu.getInstance().get("analyzeMultibitOutputError"));
				}
				return;
			}
		}
		if (inputNames.size() > AnalyzerModel.MAX_INPUTS) {
			analyzeError(proj, StringUtil.format(LC_menu.getInstance().get("analyzeTooManyInputsError"),
					"" + AnalyzerModel.MAX_INPUTS));
			return;
		}
		if (outputNames.size() > AnalyzerModel.MAX_OUTPUTS) {
			analyzeError(proj, StringUtil.format(LC_menu.getInstance().get("analyzeTooManyOutputsError"),
					"" + AnalyzerModel.MAX_OUTPUTS));
			return;
		}

		AnalyzeController analyzeController = FrameManager.CreateAndRunCircuitAnalysisFrame(proj);
		analyzeController.setAnalyzeOnlyMode(true);
		AnalyzerModel model = analyzeController.getModel();

		model.setVariables(inputNames, outputNames);

		try {
			Analyze.computeExpression(model, circuit, pinNames);
			return;
		} catch (AnalyzeException ex) {
			DialogManager.createScrollError(LC_menu.getInstance().get("analyzeNoExpressionTitle"),ex.getMessage());
		}

		Analyze.computeTable(model, proj, circuit, pinNames);

	}
		
	private static void analyzeError(Project proj, String message) {
		DialogManager.createErrorDialog(LC_menu.getInstance().get("analyzeErrorTitle"), message);
	}

}

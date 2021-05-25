/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.analyze.gui.Analyzer;
import com.cburch.LogisimFX.analyze.gui.AnalyzerManager;
import com.cburch.LogisimFX.analyze.model.AnalyzerModel;
import com.cburch.LogisimFX.circuit.Analyze;
import com.cburch.LogisimFX.circuit.AnalyzeException;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.instance.Instance;
import com.cburch.LogisimFX.instance.StdAttr;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.std.wiring.Pin;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.util.StringUtil;

import java.util.ArrayList;
import java.util.Map;

public class ProjectCircuitActions {

	private static Localizer lc = LC_gui.getInstance();

	public static void doAddCircuit(Project proj) {

		String circuitName = DialogManager.CreateInputDialog(proj.getLogisimFile());

		if (circuitName != null) {
			Circuit circuit = new Circuit(circuitName);
			proj.doAction(LogisimFileActions.addCircuit(circuit));
			proj.setCurrentCircuit(circuit);
		}

	}

	private static String promptForCircuitName(JFrame frame,
			Library lib, String initialValue) {
		JLabel label = new JLabel(Strings.get("circuitNamePrompt"));
		final JTextField field = new JTextField(15);
		field.setText(initialValue);
		JLabel error = new JLabel(" ");
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		JPanel strut = new JPanel(null);
		strut.setPreferredSize(new Dimension(3 * field.getPreferredSize().width / 2, 0));
		JPanel panel = new JPanel(gb);
		gc.gridx = 0;
		gc.gridy = GridBagConstraints.RELATIVE;
		gc.weightx = 1.0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_START;
		gb.setConstraints(label, gc); panel.add(label);
		gb.setConstraints(field, gc); panel.add(field);
		gb.setConstraints(error, gc); panel.add(error);
		gb.setConstraints(strut, gc); panel.add(strut);
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		pane.setInitialValue(field);
		JDialog dlog = pane.createDialog(frame, Strings.get("circuitNameDialogTitle"));
		dlog.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent arg0) {
				field.requestFocus();
			}

			public void windowLostFocus(WindowEvent arg0) { }
		});
		
		while (true) {
			field.selectAll();
			dlog.pack();
			dlog.setVisible(true);
			field.requestFocusInWindow();
			Object action = pane.getValue();
			if (action == null || !(action instanceof Integer)
					|| ((Integer) action).intValue() != JOptionPane.OK_OPTION) {
				return null;
			}

			String name = field.getText().trim();
			if (name.equals("")) {
				error.setText(Strings.get("circuitNameMissingError"));
			} else {
				if (lib.getTool(name) == null) {
					return name;
				} else {
					error.setText(Strings.get("circuitNameDuplicateError"));
				}
			}
		}
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
			DialogManager.CreateErrorDialog(lc.get("circuitRemoveErrorTitle"),lc.get("circuitRemoveLastError"));
		} else if (!proj.getDependencies().canRemove(circuit)) {
			DialogManager.CreateErrorDialog(lc.get("circuitRemoveErrorTitle"),lc.get("circuitRemoveUsedError"));
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
					analyzeError(proj, lc.get("analyzeMultibitInputError"));
				} else {
					analyzeError(proj, lc.get("analyzeMultibitOutputError"));
				}
				return;
			}
		}
		if (inputNames.size() > AnalyzerModel.MAX_INPUTS) {
			analyzeError(proj, StringUtil.format(lc.get("analyzeTooManyInputsError"),
					"" + AnalyzerModel.MAX_INPUTS));
			return;
		}
		if (outputNames.size() > AnalyzerModel.MAX_OUTPUTS) {
			analyzeError(proj, StringUtil.format(lc.get("analyzeTooManyOutputsError"),
					"" + AnalyzerModel.MAX_OUTPUTS));
			return;
		}
		
		Analyzer analyzer = AnalyzerManager.getAnalyzer();
		analyzer.getModel().setCurrentCircuit(proj, circuit);
		configureAnalyzer(proj, circuit, analyzer, pinNames, inputNames, outputNames);
		analyzer.setVisible(true);
		analyzer.toFront();
	}
	
	private static void configureAnalyzer(Project proj, Circuit circuit,
			Analyzer analyzer, Map<Instance, String> pinNames,
			ArrayList<String> inputNames, ArrayList<String> outputNames) {

		analyzer.getModel().setVariables(inputNames, outputNames);
		
		// If there are no inputs, we stop with that tab selected
		if (inputNames.size() == 0) {
			analyzer.setSelectedTab(Analyzer.INPUTS_TAB);
			return;
		}
		
		// If there are no outputs, we stop with that tab selected
		if (outputNames.size() == 0) {
			analyzer.setSelectedTab(Analyzer.OUTPUTS_TAB);
			return;
		}
		
		// Attempt to show the corresponding expression
		try {
			Analyze.computeExpression(analyzer.getModel(), circuit, pinNames);
			analyzer.setSelectedTab(Analyzer.EXPRESSION_TAB);
			return;
		} catch (AnalyzeException ex) {
			DialogManager.CreateScrollError(lc.get("analyzeNoExpressionTitle"),ex.getMessage());
		}
		
		// As a backup measure, we compute a truth table.
		Analyze.computeTable(analyzer.getModel(), proj, circuit, pinNames);
		analyzer.setSelectedTab(Analyzer.TABLE_TAB);
	}
		
	private static void analyzeError(Project proj, String message) {
		DialogManager.CreateErrorDialog(lc.get("analyzeErrorTitle"), message);
	}

}

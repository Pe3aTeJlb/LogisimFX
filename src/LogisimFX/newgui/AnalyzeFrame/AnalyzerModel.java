/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.AnalyzeFrame;

import LogisimFX.circuit.Circuit;
import LogisimFX.proj.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class AnalyzerModel {

	public static final int MAX_INPUTS = 12;
	public static final int MAX_OUTPUTS = 12;
	
	public static final int FORMAT_SUM_OF_PRODUCTS = 0;
	public static final int FORMAT_PRODUCT_OF_SUMS = 1;
	
	private ObservableList<String> inputs = FXCollections.observableArrayList();
	private ObservableList<String> outputs = FXCollections.observableArrayList();
	private TruthTable table;
	private OutputExpressions outputExpressions;
	private Project currentProject = null;
	private Circuit currentCircuit = null;

	public AnalyzerModel() {
		// the order here is important, because the output expressions
		// need the truth table to exist for listening.
		table = new TruthTable(this);
		outputExpressions = new OutputExpressions(this);
	}
	
	//
	// access methods
	//
	public Project getCurrentProject() {
		return currentProject;
	}
	
	public Circuit getCurrentCircuit() {
		return currentCircuit;
	}
	
	public ObservableList<String> getInputs() {
		return inputs;
	}
	
	public ObservableList<String> getOutputs() {
		return outputs;
	}
	
	public TruthTable getTruthTable() {
		return table;
	}
	
	public OutputExpressions getOutputExpressions() {
		return outputExpressions;
	}
	
	//
	// modifier methods
	//
	public void setCurrentCircuit(Project value, Circuit circuit) {
		currentProject = value;
		currentCircuit = circuit;
	}
	
	public void setVariables(List<String> inputs, List<String> outputs) {
		this.inputs.setAll(inputs);
		this.outputs.setAll(outputs);
	}

}

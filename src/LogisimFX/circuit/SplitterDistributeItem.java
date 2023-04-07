/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.circuit;

import LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;
import javafx.scene.control.MenuItem;


class SplitterDistributeItem extends MenuItem {

	private Project proj;
	private Splitter splitter;
	private int order;
	
	public SplitterDistributeItem(Project proj, Splitter splitter, int order) {

		this.proj = proj;
		this.splitter = splitter;
		this.order = order;
		
		SplitterAttributes attrs = (SplitterAttributes) splitter.getAttributeSet();
		byte[] actual = attrs.bitEnd;
		byte[] desired = SplitterAttributes.computeDistribution(attrs.fanout,
				actual.length, order);
		boolean same = actual.length == desired.length;
		for (int i = 0; same && i < desired.length; i++) {
			if (actual[i] != desired[i]) {
				same = false;
			}
		}

		this.setOnAction(event -> {

			CircuitMutation xn = new CircuitMutation(proj.getCircuitState().getCircuit());
			for (int i = 0, n = Math.min(actual.length, desired.length); i < n; i++) {
				if (actual[i] != desired[i]) {
					xn.set(splitter, attrs.getBitOutAttribute(i),
							Integer.valueOf(desired[i]));
				}
			}
			proj.doAction(xn.toAction(toGetter()));

		});

		this.setDisable(same);
		this.textProperty().bind(toGetter());

	}
	
	private StringBinding toGetter() {

		if (order > 0) {
			return LC.createStringBinding("splitterDistributeAscending");
		} else {
			return LC.createStringBinding("splitterDistributeDescending");
		}

	}

}

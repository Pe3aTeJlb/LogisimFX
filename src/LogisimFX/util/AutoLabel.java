/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.util;

import LogisimFX.circuit.Circuit;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.comp.PositionComparator;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.std.wiring.Tunnel;

import java.util.*;

import static LogisimFX.circuit.Circuit.isCorrectLabel;

public class AutoLabel {

	private final HashMap<Circuit, String> labelBase = new HashMap<>();
	private final HashMap<Circuit, Integer> currentIndex = new HashMap<>();
	private final HashMap<Circuit, Boolean> useLabelBaseOnly = new HashMap<>();
	private final HashMap<Circuit, Boolean> useUnderscore = new HashMap<>();
	private final HashMap<Circuit, Boolean> active = new HashMap<>();
	private final HashMap<Circuit, String> currentLabel = new HashMap<>();

	public AutoLabel() {
		this("", null, false);
	}

	public AutoLabel(String label, Circuit circ) {
		this(label, circ, true);
	}

	public AutoLabel(String label, Circuit circ, boolean useFirstLabel) {
		update(circ, label, useFirstLabel, null);
		activate(circ);
	}

	public boolean hasNext(Circuit circ) {
		if (circ == null || !active.containsKey(circ)) return false;
		return active.get(circ);
	}

	public String getCurrent(Circuit circ, ComponentFactory me) {
		if (circ == null || !currentLabel.containsKey(circ) || currentLabel.get(circ).isEmpty()) {
			return "";
		}
		if (isCorrectLabel(circ.getName(), currentLabel.get(circ), circ.getNonWires(), null, me, false)) {
			return currentLabel.get(circ);
		}

		if (hasNext(circ)) {
			return getNext(circ, me);
		} else {
			setLabel("", circ, me);
		}
		return "";
	}

	public boolean correctMatrixBaseLabel(Circuit circ, ComponentFactory me, String common, int maxX, int maxY) {
		if (StringUtil.isNullOrEmpty(common) || (maxX < 0) || (maxY < 0)) return true;
		if (!SyntaxChecker.isVariableNameAcceptable(common, true)) return false;
		for (var x = 0; x < maxX; x++)
			for (var y = 0; y < maxY; y++) {
				if (getMatrixLabel(circ, me, common, x, y).isEmpty()) {
					return false;
				}
			}
		return true;
	}

	public String getMatrixLabel(Circuit circ, ComponentFactory me, String common, int x, int y) {
		if (StringUtil.isNullOrEmpty(common) || (x < 0) || (y < 0)) return "";
		if (circ == null || !currentLabel.containsKey(circ) || currentLabel.get(circ).isEmpty()) return "";
		final var label = common.concat("_X" + x + "_Y" + y);
		if (isCorrectLabel(circ.getName(), label, circ.getNonWires(), null, me, false)
				&& SyntaxChecker.isVariableNameAcceptable(label, false)) return label;
		return "";
	}

	public String getNext(Circuit circ, ComponentFactory me) {
		if (circ == null) return "";
		if (useLabelBaseOnly.get(circ)) {
			useLabelBaseOnly.put(circ, false);
			return labelBase.get(circ);
		}
		if (me instanceof Tunnel) {
			return labelBase.get(circ);
		}
		var newLabel = "";
		var curIdx = currentIndex.get(circ);
		final var baseLabel = labelBase.get(circ);
		boolean undescore = useUnderscore.get(circ);
		do {
			curIdx++;
			newLabel = baseLabel;
			if (undescore) newLabel = newLabel.concat("_");
			newLabel = newLabel.concat(Integer.toString(curIdx));
		} while (!isCorrectLabel(circ.getName(), newLabel, circ.getNonWires(), null, me, false));
		currentIndex.put(circ, curIdx);
		currentLabel.put(circ, newLabel);
		return newLabel;
	}

	public boolean isActive(Circuit circ) {
		if (circ != null && active.containsKey(circ)) return active.get(circ);
		return false;
	}

	public void setLabel(String label, Circuit circ, ComponentFactory me) {
		if (circ != null) update(circ, label, true, me);
	}

	public void activate(Circuit circ) {
		if (circ != null) {
			if (labelBase.containsKey(circ)
					&& currentIndex.containsKey(circ)
					&& useLabelBaseOnly.containsKey(circ)
					&& useUnderscore.containsKey(circ)) active.put(circ, !labelBase.get(circ).isEmpty());
		}
	}

	public void stop(Circuit circ) {
		if (circ != null) {
			setLabel("", circ, null);
			active.put(circ, false);
		}
	}

	public static boolean labelEndsWithNumber(String label) {
		return CorrectLabel.NUMBERS.contains(label.substring(label.length() - 1));
	}

	private int getLabelBaseEndIndex(String label) {
		var index = label.length();
		while ((index > 1) && CorrectLabel.NUMBERS.contains(label.substring(index - 1, index))) index--;
		return (index - 1);
	}

	private void update(Circuit circ, String label, boolean useFirstLabel, ComponentFactory me) {
		if (circ == null) return;
		if (label.isEmpty() || !SyntaxChecker.isVariableNameAcceptable(label, false)) {
			labelBase.put(circ, "");
			currentIndex.put(circ, 0);
			useLabelBaseOnly.put(circ, false);
			currentLabel.put(circ, "");
			return;
		}
		useLabelBaseOnly.put(circ, useFirstLabel);
		if (labelEndsWithNumber(label)) {
			int index = getLabelBaseEndIndex(label);
			currentIndex.put(circ, Integer.valueOf(label.substring(index + 1)));
			labelBase.put(circ, label.substring(0, index + 1));
			useUnderscore.put(circ, false);
			useLabelBaseOnly.put(circ, false);
		} else {
			labelBase.put(circ, label);
			currentIndex.put(circ, 0);
			useUnderscore.put(circ, !label.endsWith("_"));
		}
		if (useFirstLabel) {
			currentLabel.put(circ, label);
		} else {
			currentLabel.put(circ, getNext(circ, me));
		}
	}

	public static SortedSet<Component> sort(Set<Component> comps) {
		SortedSet<Component> sorted = new TreeSet<>(new PositionComparator());
		sorted.addAll(comps);
		return sorted;
	}

}

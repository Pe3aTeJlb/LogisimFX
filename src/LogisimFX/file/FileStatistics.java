/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.newgui.CircuitStatisticFrame.LC;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

import java.util.*;

public class FileStatistics {

	public static class Count {

		private StringBinding extraTitleF,extraTitleS;
		private Library library;
		private ComponentFactory factory;
		private int simpleCount;
		private int uniqueCount;
		private int recursiveCount;
		
		private Count(ComponentFactory factory) {
			this.library = null;
			this.factory = factory;
			this.simpleCount = 0;
			this.uniqueCount = 0;
			this.recursiveCount = 0;
		}

		public void setExtraTitleF(StringBinding extraTitle) {
			this.extraTitleF = extraTitle;
		}
		public void setExtraTitleS(StringBinding extraTitle) {
			this.extraTitleS = extraTitle;
		}

		public StringBinding getComponentName() {
			return factory == null ? extraTitleF : factory.getDisplayName();
		}

		public StringBinding getLibName() {
			return library == null ? null : library.getDisplayName();
		}
		
		public int getSimpleCount() {
			return simpleCount;
		}
		
		public int getUniqueCount() {
			return uniqueCount;
		}
		
		public int getRecursiveCount() {
			return recursiveCount;
		}


		public Library getLibrary() {
			return library;
		}

		public ComponentFactory getFactory() {
			return factory;
		}

	}
	
	public static FileStatistics compute(LogisimFile file, Circuit circuit) {
		Set<Circuit> include = new HashSet<Circuit>(file.getCircuits());
		Map<Circuit,Map<ComponentFactory,Count>> countMap;
		countMap = new HashMap<Circuit,Map<ComponentFactory,Count>>();
		doRecursiveCount(circuit, include, countMap);
		doUniqueCounts(countMap.get(circuit), countMap);
		List<Count> countList = sortCounts(countMap.get(circuit), file);
		return new FileStatistics(countList, getTotal(countList, include),
				getTotal(countList, null));
	}
	
	private static Map<ComponentFactory,Count> doRecursiveCount(Circuit circuit,
			Set<Circuit> include,
			Map<Circuit,Map<ComponentFactory,Count>> countMap) {
		if (countMap.containsKey(circuit)) {
			return countMap.get(circuit);
		}

		Map<ComponentFactory,Count> counts = doSimpleCount(circuit);
		countMap.put(circuit, counts);
		for (Count count : counts.values()) {
			count.uniqueCount = count.simpleCount;
			count.recursiveCount = count.simpleCount;
		}
		for (Circuit sub : include) {
			SubcircuitFactory subFactory = sub.getSubcircuitFactory();
			if (counts.containsKey(subFactory)) {
				int multiplier = counts.get(subFactory).simpleCount;
				Map<ComponentFactory,Count> subCount;
				subCount = doRecursiveCount(sub, include, countMap);
				for (Count subcount : subCount.values()) {
					ComponentFactory subfactory = subcount.factory;
					Count supercount = counts.get(subfactory);
					if (supercount == null) {
						supercount = new Count(subfactory);
						counts.put(subfactory, supercount);
					}
					supercount.recursiveCount += multiplier * subcount.recursiveCount;
				}
			}
		}
		
		return counts;
	}
	
	private static Map<ComponentFactory,Count> doSimpleCount(Circuit circuit) {
		Map<ComponentFactory,Count> counts;
		counts = new HashMap<ComponentFactory,Count>();
		for (Component comp : circuit.getNonWires()) {
			ComponentFactory factory = comp.getFactory();
			Count count = counts.get(factory);
			if (count == null) {
				count = new Count(factory);
				counts.put(factory, count);
			}
			count.simpleCount++;
		}
		return counts;
	}
	
	private static void doUniqueCounts(Map<ComponentFactory,Count> counts,
			Map<Circuit,Map<ComponentFactory,Count>> circuitCounts) {
		for (Count count : counts.values()) {
			ComponentFactory factory = count.getFactory();
			int unique = 0;
			for (Circuit circ : circuitCounts.keySet()) {
				Count subcount = circuitCounts.get(circ).get(factory);
				if (subcount != null) {
					unique += subcount.simpleCount;
				}
			}
			count.uniqueCount = unique;
		}
	}
	
	private static List<Count> sortCounts(Map<ComponentFactory,Count> counts,
			LogisimFile file) {
		List<Count> ret = new ArrayList<Count>();
		for (AddTool tool : file.getTools()) {
			ComponentFactory factory = tool.getFactory();
			Count count = counts.get(factory);
			if (count != null) {
				count.library = file;
				ret.add(count);
			}
		}
		for (Library lib : file.getLibraries()) {
			for (Tool tool : lib.getTools()) {
				if (tool instanceof AddTool) {
					ComponentFactory factory = ((AddTool) tool).getFactory();
					Count count = counts.get(factory);
					if (count != null) {
						count.library = lib;
						ret.add(count);
					}
				}
			}
		}
		return ret;
	}
	
	private static Count getTotal(List<Count> counts, Set<Circuit> exclude) {
		Count ret = new Count(null);
		for (Count count : counts) {
			ComponentFactory factory = count.getFactory();
			Circuit factoryCirc = null;
			if (factory instanceof SubcircuitFactory) {
				factoryCirc = ((SubcircuitFactory) factory).getSubcircuit();
			}
			if (exclude == null || !exclude.contains(factoryCirc)) {
				ret.simpleCount += count.simpleCount;
				ret.uniqueCount += count.uniqueCount;
				ret.recursiveCount += count.recursiveCount;
			}
		}
		return ret;
	}
	
	private List<Count> counts;
	private Count totalWithout;
	private Count totalWith;
	
	private FileStatistics(List<Count> counts, Count totalWithout,
			Count totalWith) {

		this.counts = Collections.unmodifiableList(counts);

		this.totalWithout = totalWithout;
		this.totalWithout.setExtraTitleF(LC.createStringBinding("statsTotalWithout"));

		this.totalWith = totalWith;
		this.totalWith.setExtraTitleF(LC.createStringBinding("statsTotalWith"));

	}
	
	public List<Count> getCounts() {
		return counts;
	}
	
	public Count getTotalWithoutSubcircuits() {
		return totalWithout;
	}
	
	public Count getTotalWithSubcircuits() {
		return totalWith;
	}
}

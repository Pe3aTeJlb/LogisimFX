/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.Wire;
import LogisimFX.comp.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TerminalMessageContainer {

	public static final int LEVEL_NORMAL = 1;
	public static final int LEVEL_SEVERE = 2;
	public static final int LEVEL_FATAL = 3;

	private final String message;
	private final int severityLevel;
	private Set<Component> drcComponents;
	private Set<Wire> drcWires;
	private Circuit myCircuit;
	private File file;
	private int listNumber;
	private final boolean suppressCount;

	public TerminalMessageContainer(String message, int level) {
		this.message = message;
		this.severityLevel = level;
		this.listNumber = 0;
		this.suppressCount = false;
	}

	public TerminalMessageContainer(String message, int level, boolean supressCount) {
		this.message = message;
		this.severityLevel = level;
		this.listNumber = 0;
		this.suppressCount = supressCount;
	}

	public TerminalMessageContainer(Object message, int level) {
		this.message = message.toString();
		this.severityLevel = level;
		this.listNumber = 0;
		this.suppressCount = false;
	}

	public TerminalMessageContainer(Object message, int level, boolean supressCount) {
		this.message = message.toString();
		this.severityLevel = level;
		this.listNumber = 0;
		this.suppressCount = supressCount;
	}

	public TerminalMessageContainer(Circuit circ, Object message, int level) {
		this.message = message.toString();
		this.severityLevel = level;
		this.myCircuit = circ;
		this.listNumber = 0;
		this.suppressCount = false;
	}

	public TerminalMessageContainer(Circuit circ, Object message, int level, boolean supressCount) {
		this.message = message.toString();
		this.severityLevel = level;
		this.myCircuit = circ;
		this.listNumber = 0;
		this.suppressCount = supressCount;
	}

	public TerminalMessageContainer(File file, Object message, int level){
		this.message = message.toString();
		this.severityLevel = level;
		this.file = file;
		this.listNumber = 0;
		this.suppressCount = false;
	}

	public TerminalMessageContainer(Circuit circ, File file, Object message, int level){
		this.message = message.toString();
		this.severityLevel = level;
		this.file = file;
		this.listNumber = 0;
		this.suppressCount = false;
	}

	@Override
	public String toString() {
		return message;
	}

	public int getSeverity() {
		return severityLevel;
	}

	public boolean isDrcInfoPresent() {
		if (drcComponents == null || myCircuit == null) return false;
		return !drcComponents.isEmpty() && file == null;
	}

	public Circuit getCircuit() {
		return myCircuit;
	}

	public boolean hasCircuit() {
		return (myCircuit != null);
	}

	public boolean hasFile(){
		return (file != null);
	}

	public File getFile(){
		return file;
	}

	public void addMarkComponent(Component comp) {
		if (drcComponents == null) drcComponents = new HashSet<>();
		drcComponents.add(comp);
	}

	public void addMarkWires(Set<Wire> set) {
		if (drcWires == null) drcWires = new HashSet<>();
		drcWires.addAll(set);
	}

	public void addMarkComponents(Set<Component> set) {
		if (drcComponents == null) drcComponents = new HashSet<>();
		drcComponents.addAll(set);
	}

	public void setListNumber(int number) {
		listNumber = number;
	}

	public boolean getSupressCount() {
		return suppressCount;
	}

	public int getListNumber() {
		return listNumber;
	}

	public Set<Component> getDrcComponents(){
		return drcComponents;
	}

	public Set<Wire> getDrcWires(){
		return drcWires;
	}

}

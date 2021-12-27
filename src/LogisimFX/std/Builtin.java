/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std;

import LogisimFX.std.arith.Arithmetic;
import LogisimFX.std.base.Base;
import LogisimFX.std.gates.Gates;
import LogisimFX.std.gray.Components;
import LogisimFX.std.io.Io;
import LogisimFX.std.memory.Memory;
import LogisimFX.std.plexers.Plexers;
import LogisimFX.std.verifiers.Verifiers;
import LogisimFX.std.wiring.Wiring;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;

import javafx.beans.binding.StringBinding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Builtin extends Library {

	private List<Library> libraries = null;

	public Builtin() {
		libraries = Arrays.asList(new Library[] {
			new Base(),
			new Gates(),
			new Wiring(),
			new Plexers(),
			new Arithmetic(),
			new Memory(),
			new Io(),
			new Components(),
			new Verifiers()
		});
	}

	@Override
	public String getName() { return "Builtin"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("builtinLibrary"); }

	@Override
	public List<Tool> getTools() { return Collections.emptyList(); }
	
	@Override
	public List<Library> getLibraries() {
		return libraries;
	}

}

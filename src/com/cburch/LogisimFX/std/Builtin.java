/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std;

import com.cburch.LogisimFX.std.arith.Arithmetic;
import com.cburch.LogisimFX.std.base.Base;
import com.cburch.LogisimFX.std.gates.Gates;
import com.cburch.LogisimFX.std.io.Io;
import com.cburch.LogisimFX.std.memory.Memory;
import com.cburch.LogisimFX.std.plexers.Plexers;
import com.cburch.LogisimFX.std.wiring.Wiring;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;

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
		});
	}

	@Override
	public String getName() { return "Builtin"; }

	@Override
	public String getDisplayName() { return LC.get("builtinLibrary"); }

	@Override
	public List<Tool> getTools() { return Collections.emptyList(); }
	
	@Override
	public List<Library> getLibraries() {
		return libraries;
	}
}

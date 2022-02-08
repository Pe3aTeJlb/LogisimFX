/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.std.verifiers;

import LogisimFX.std.LC;
import LogisimFX.tools.FactoryDescription;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;

import javafx.beans.binding.StringBinding;

import java.util.List;

public class Verifiers extends Library {

    private static FactoryDescription[] DESCRIPTIONS = {

            new FactoryDescription("Verifier", LC.createStringBinding("verifier"),
                    "projapp.gif", "Verifier"),

    };

    private List<Tool> tools = null;

    public Verifiers() { }

    @Override
    public String getName() { return "Verifiers"; }

    @Override
    public StringBinding getDisplayName() { return LC.createStringBinding("verifierLibrary"); }

    @Override
    public List<Tool> getTools() {

        if (tools == null) {
            tools = FactoryDescription.getTools(Verifiers.class, DESCRIPTIONS);
        }

        return tools;

    }

}
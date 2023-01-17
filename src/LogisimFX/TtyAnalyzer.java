/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.Propagator;
import LogisimFX.circuit.Wire;
import LogisimFX.comp.Component;
import LogisimFX.comp.EndData;
import LogisimFX.data.Location;
import LogisimFX.data.Value;
import LogisimFX.file.FileStatistics;
import LogisimFX.file.LoadFailedException;
import LogisimFX.file.Loader;
import LogisimFX.file.LogisimFile;
import LogisimFX.instance.Instance;
import LogisimFX.instance.InstanceState;
import LogisimFX.newgui.WaveformFrame.Loggable;
import LogisimFX.proj.Project;
import LogisimFX.std.io.Keyboard;
import LogisimFX.std.io.Tty;
import LogisimFX.std.memory.AbstractFlipFlop;
import LogisimFX.std.memory.Ram;
import LogisimFX.std.verifiers.Verifier;
import LogisimFX.tools.Library;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class TtyAnalyzer {

    public static final int FORMAT_TABLE = 1;
    public static final int FORMAT_SPEED = 2;
    public static final int FORMAT_TTY = 4;
    public static final int FORMAT_HALT = 8;
    public static final int FORMAT_STATISTICS = 16;

    private static boolean lastIsNewline = true;

    private static boolean debug = false;

    public static void sendFromTty(char c) {

        lastIsNewline = c == '\n';
        System.out.print(c); //OK

    }

    private static void ensureLineTerminated() {

        if (!lastIsNewline) {
            lastIsNewline = true;
            System.out.print("\n"); //OK
        }

    }

    public static void run(Startup args) {

        File fileToOpen = args.getFilesToOpen().get(0);
        Loader loader = new Loader();
        LogisimFile file;
        try {
            file = loader.openLogisimFile(fileToOpen, args.getSubstitutions());
        } catch (LoadFailedException e) {
            System.err.println("Error loading circuit file"); //OK
            System.exit(-1);
            return;
        }

        Project proj = new Project(file);
        Circuit circuit = file.getMainCircuit();

        Component verifierComponent = null;

        for(Component comp: circuit.getNonWires()){
            if(comp.getFactory() instanceof Verifier) {
                verifierComponent = comp;
            }
        }


        int format = args.getTtyFormat();
        if ((format & FORMAT_STATISTICS) != 0) {
            format &= ~FORMAT_STATISTICS;
            displayStatistics(file);
            analyseCircuitGraph(circuit, verifierComponent);
        }
        if (format == 0) { // no simulation remaining to perform, so just exit
            System.exit(0);
        }


        Verifier verifier = (Verifier) verifierComponent.getFactory();
        verifier.setRestrictProp(true);


        CircuitState circState = new CircuitState(proj, circuit);
        // we have to do our initial propagation before the simulation starts -
        // it's necessary to populate the circuit with substates.
        circState.getPropagator().propagate();

        verifier.setRestrictProp(false);

        if (args.getLoadFile() != null) {
            try {
                boolean loaded = loadRam(circState, args.getLoadFile());
                if (!loaded) {
                    System.err.println(" No RAM was found for the \"-load\" option"); //OK
                    System.exit(-1);
                }
            } catch (IOException e) {
                System.err.println("Error while reading image file" + ": " + e.toString()); //OK
                System.exit(-1);
            }
        }

        int ttyFormat = args.getTtyFormat();
        int simCode = runSimulation(circState, verifierComponent, ttyFormat);

        System.exit(simCode);

    }

    private static void displayStatistics(LogisimFile file) {

        FileStatistics stats = FileStatistics.compute(file, file.getMainCircuit());
        FileStatistics.Count total = stats.getTotalWithSubcircuits();

        for (FileStatistics.Count count : stats.getCounts()) {
            Library lib = count.getLibrary();
            String libName = lib == null ? "-" : lib.getName();
            System.out.println(count.getFactory().getName() + " " + libName + " " + count.getUniqueCount() + " " + count.getRecursiveCount());
        }

        FileStatistics.Count totalWithout = stats.getTotalWithoutSubcircuits();
        System.out.println("TOTAL (without project's subcircuits)" + " " + totalWithout.getUniqueCount() + " " + totalWithout.getRecursiveCount());
        System.out.println("TOTAL (with subcircuits)" + " " + total.getUniqueCount() + " " + total.getRecursiveCount());
        System.out.println("/block end");

    }

    private static void analyseCircuitGraph(Circuit circ, Component verifier){

        ArrayList<Component> visited = new ArrayList<>();
        visited.add(verifier);

        boolean cycled;
        cycled = hasCycle(visited, verifier, circ, null);

        if(debug) {
            System.out.println(Arrays.toString(visited.toArray()));
            System.out.println(circ.getNonWires());
        }

        System.out.println("circ graph is cycled: "+ cycled);
        System.out.println("/block end");

    }

    private static boolean hasCycle(ArrayList<Component> visited, Component comp, Circuit circ, Location lastLoc){

        boolean meh = true;

        if(debug) {
            String l = "";
            for (EndData endData : comp.getEnds()) {
                l += "[" + endData.getLocation() + " - " + endData.getType() + "] ";
            }
            System.out.println(comp.getFactory().getName() + " " + l);
        }

        for(EndData endData: comp.getEnds()){

            Location loc = endData.getLocation();
            if(debug)System.out.println("loc "+loc+ " " + lastLoc);

            if(((lastLoc == null || loc.compareTo(lastLoc) != 0) && endData.getType() == 3) || endData.getType() == 2) {

                if(debug)System.out.println("loc passed " + loc);

                ArrayList<Component> nextComps = new ArrayList<>();

                if (!circ.getWires(loc).isEmpty()) {
                    getNextComp(circ.getWires(loc), loc, circ, nextComps);

                    if(debug) {
                        System.out.println(Arrays.toString(circ.getWires(loc).toArray()));
                        System.out.println(Arrays.toString(nextComps.toArray()));
                    }

                    for (Component nextComp : nextComps) {

                        if (nextComp == null) {
                            if(debug)System.out.println("obriv!!!!!!!!");
                            return false;
                        }

                        if(nextComp == comp && !(comp.getFactory() instanceof AbstractFlipFlop)){
                            if(debug)System.out.println("cycle on shit");
                            return false;
                        }


                        if(debug)System.out.println("take a look at " + nextComp.getFactory().getName());

                        if (visited.contains(nextComp)) {
                            if(debug)System.out.println("already visited " + nextComp.getFactory().getName());
                            //if(nextComp.equals(visited.get(0))) return true;
                        } else {
                            if(debug)System.out.println("first time here " + nextComp.getLocation());
                            visited.add(nextComp);
                            meh = hasCycle(visited, nextComp, circ, nextComp.getLocation());
                            if(!meh)return meh;
                            if(debug) System.out.println("meh for "+nextComp.getFactory().getName()+" "+meh);
                        }

                    }

                } else {
                    if(debug)System.out.println("unused port at " + loc);
                }
            }

        }

        return meh;

    }

    private static void getNextComp(Collection<Wire> wires, Location loc, Circuit circ, ArrayList<Component> nextComps){

        for(Wire w: wires) {

            if(debug)System.out.println("get next comp, curr w "+w);
            Location otherEnd = w.getOtherEnd(loc);

            if (!circ.getNonWires(otherEnd).isEmpty()) {

                nextComps.addAll(circ.getNonWires(w.getOtherEnd(loc)));

            } else {

                Collection<Wire> nextWires = circ.getWires(otherEnd);
                nextWires.remove(w);
                if(debug)System.out.println(otherEnd+" "+Arrays.toString(circ.getWires(otherEnd).toArray()));

                if(!nextWires.isEmpty()) {
                    if(debug) System.out.println("deep down");
                    getNextComp(nextWires, otherEnd, circ, nextComps);
                }else{
                    nextComps.add(null);
                }

            }
        }

    }

    private static boolean loadRam(CircuitState circState, File loadFile)
            throws IOException {
        if (loadFile == null) return false;

        boolean found = false;
        for (Component comp : circState.getCircuit().getNonWires()) {
            if (comp.getFactory() instanceof Ram) {
                Ram ramFactory = (Ram) comp.getFactory();
                InstanceState ramState = circState.getInstanceState(comp);
                ramFactory.loadImage(ramState, loadFile);
                found = true;
            }
        }

        for (CircuitState sub : circState.getSubstates()) {
            found |= loadRam(sub, loadFile);
        }
        return found;
    }

    private static boolean prepareForTty(CircuitState circState,
                                         ArrayList<InstanceState> keybStates) {
        boolean found = false;
        for (Component comp : circState.getCircuit().getNonWires()) {
            Object factory = comp.getFactory();
            if (factory instanceof Tty) {
                Tty ttyFactory = (Tty) factory;
                InstanceState ttyState = circState.getInstanceState(comp);
                ttyFactory.sendToStdout(ttyState);
                found = true;
            } else if (factory instanceof Keyboard) {
                keybStates.add(circState.getInstanceState(comp));
                found = true;
            }
        }

        for (CircuitState sub : circState.getSubstates()) {
            found |= prepareForTty(sub, keybStates);
        }
        return found;
    }

    private static int runSimulation(CircuitState circState, Component verifierComponent, int format) {

        boolean showTable = (format & FORMAT_TABLE) != 0;
        boolean showTty = (format & FORMAT_TTY) != 0;
        boolean showHalt = (format & FORMAT_HALT) != 0;

        ArrayList<InstanceState> keyboardStates = null;
        TtyAnalyzer.StdinThread stdinThread = null;
        if (showTty) {
            keyboardStates = new ArrayList<>();
            boolean ttyFound = prepareForTty(circState, keyboardStates);
            if (!ttyFound) {
                System.err.println("No TTY or Keyboard component was found"); //OK
                System.exit(-1);
            }
            if (keyboardStates.isEmpty()) {
                keyboardStates = null;
            } else {
                stdinThread = new TtyAnalyzer.StdinThread();
                stdinThread.start();
            }
        }

        int retCode;
        boolean halted;
        boolean skipfirst = true;

        Propagator prop = circState.getPropagator();

        Verifier verifier = (Verifier) verifierComponent.getFactory();
        Instance verifierInstance = Instance.getInstanceFor(verifierComponent);

        Loggable log = (Loggable) verifierComponent.getFeature(Loggable.class);

        while (true) {

            InstanceState verifierState = circState.getInstanceState(verifierInstance);
            Value val = log.getLogValue(circState, null);

            halted = verifier.isReadyOutput(verifierState);

            if (showTable) {
                if(!skipfirst)
                System.out.println(val);
                skipfirst = false;
            }

            if (halted) {
                retCode = 0; // normal exit
                break;
            }
            if (prop.isOscillating()) {
                retCode = 1; // abnormal exit
                break;
            }
            if (keyboardStates != null) {
                char[] buffer = stdinThread.getBuffer();
                if (buffer != null) {
                    for (InstanceState keyState : keyboardStates) {
                        Keyboard.addToBuffer(keyState, buffer);
                    }
                }
            }

            prop.tick();
            prop.propagate();

        }

        System.out.println("/block end");

        if (showTty) ensureLineTerminated();
        if (showHalt || retCode != 0) {
            if (retCode == 0) {
                System.out.println("halted due to halt"); //OK
            } else if (retCode == 1) {
                System.out.println("halted due to oscillation"); //OK
            }
        }

        return retCode;

    }

    // It's possible to avoid using the separate thread using System.in.available(),
    // but this doesn't quite work because on some systems, the keyboard input
    // is not interactively echoed until System.in.read() is invoked.
    private static class StdinThread extends Thread {
        private LinkedList<char[]> queue; // of char[]

        public StdinThread() {
            queue = new LinkedList<char[]>();
        }

        public char[] getBuffer() {
            synchronized (queue) {
                if (queue.isEmpty()) {
                    return null;
                } else {
                    return queue.removeFirst();
                }
            }
        }

        @Override
        public void run() {
            InputStreamReader stdin = new InputStreamReader(System.in);
            char[] buffer = new char[32];
            while (true) {
                try {
                    int nbytes = stdin.read(buffer);
                    if (nbytes > 0) {
                        char[] add = new char[nbytes];
                        System.arraycopy(buffer, 0, add, 0, nbytes);
                        synchronized (queue) {
                            queue.addLast(add);
                        }
                    }
                } catch (IOException e) { }
            }
        }
    }

}

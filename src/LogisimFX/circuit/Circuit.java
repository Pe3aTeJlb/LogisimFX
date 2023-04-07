/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.circuit;

import LogisimFX.circuit.appear.CircuitAppearance;
import LogisimFX.comp.Component;
import LogisimFX.comp.*;
import LogisimFX.data.*;
import LogisimFX.fpga.Reporter;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.instance.StdAttr;
import LogisimFX.localization.LC_util;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.wiring.Clock;
import LogisimFX.std.wiring.Pin;
import LogisimFX.std.wiring.Tunnel;
import LogisimFX.tools.SetAttributeAction;
import LogisimFX.util.*;

import java.util.List;
import java.util.*;

public class Circuit {

	private class EndChangedTransaction extends CircuitTransaction {

		private Component comp;
		private Map<Location, EndData> toRemove;
		private Map<Location, EndData> toAdd;

		EndChangedTransaction(Component comp, Map<Location, EndData> toRemove,
							  Map<Location, EndData> toAdd) {

			this.comp = comp;
			this.toRemove = toRemove;
			this.toAdd = toAdd;

		}

		@Override
		protected Map<Circuit, Integer> getAccessedCircuits() {
			return Collections.singletonMap(Circuit.this, READ_WRITE);
		}

		@Override
		protected void run(CircuitMutator mutator) {

			for (Location loc : toRemove.keySet()) {
				EndData removed = toRemove.get(loc);
				EndData replaced = toAdd.remove(loc);
				if (replaced == null) {
					wires.remove(comp, removed);
				} else if (!replaced.equals(removed)) {
					wires.replace(comp, removed, replaced);
				}
			}
			for (EndData end : toAdd.values()) {
				wires.add(comp, end);
			}
			((CircuitMutatorImpl) mutator).markModified(Circuit.this);

		}

	}

	private class MyComponentListener implements ComponentListener {
		public void endChanged(ComponentEvent e) {
			//locker.checkForWritePermission("ends changed");
			netList.clear();
			Component comp = e.getSource();
			HashMap<Location, EndData> toRemove = toMap(e.getOldData());
			HashMap<Location, EndData> toAdd = toMap(e.getData());
			EndChangedTransaction xn = new EndChangedTransaction(comp, toRemove, toAdd);
			locker.execute(xn);
			fireEvent(CircuitEvent.ACTION_INVALIDATE, comp);
		}

		private HashMap<Location, EndData> toMap(Object val) {
			HashMap<Location, EndData> map = new HashMap<Location, EndData>();
			if (val instanceof List) {
				@SuppressWarnings("unchecked")
				List<EndData> valList = (List<EndData>) val;
				int i = -1;
				for (EndData end : valList) {
					i++;
					if (end != null) {
						map.put(end.getLocation(), end);
					}
				}
			} else if (val instanceof EndData) {
				EndData end = (EndData) val;
				map.put(end.getLocation(), end);
			}
			return map;
		}

		public void componentInvalidated(ComponentEvent e) {
			fireEvent(CircuitEvent.ACTION_INVALIDATE, e.getSource());
		}

		@Override
		public void labelChanged(ComponentEvent e) {
			final var attrEvent = (AttributeEvent) e.getData();
			if (attrEvent.getSource() == null || attrEvent.getValue() == null) return;
			final var newLabel = (String) attrEvent.getValue();
			final var oldLabel = attrEvent.getOldValue() != null ? (String) attrEvent.getOldValue() : "";
			@SuppressWarnings("unchecked")
			Attribute<String> lattr = (Attribute<String>) attrEvent.getAttribute();
			if (!isCorrectLabel(getName(), newLabel, comps, attrEvent.getSource(), e.getSource().getFactory(), true)) {
				if (isCorrectLabel(
						getName(), oldLabel, comps, attrEvent.getSource(), e.getSource().getFactory(), false)) {
					attrEvent.getSource().setValue(lattr, oldLabel);
				} else {
					attrEvent.getSource().setValue(lattr, "");
				}
			}
		}

	}

	public static boolean isCorrectLabel(
			String circuitName,
			String name,
			Set<Component> components,
			AttributeSet me,
			ComponentFactory myFactory,
			Boolean showDialog) {
		if (myFactory instanceof Tunnel) return true;
		if (circuitName != null && !circuitName.isEmpty() && circuitName.equalsIgnoreCase(name) && myFactory instanceof Pin) {
			if (showDialog) {
				DialogManager.createErrorDialog("Error", LogisimFX.util.LC.get("ComponentLabelEqualCircuitName"));
			}
			return false;
		}
		return !(isExistingLabel(name, me, components, showDialog)
				|| isComponentName(name, components, showDialog));
	}

	private static boolean isExistingLabel(String name, AttributeSet me, Set<Component> comps, Boolean showDialog) {
		if (name.isEmpty()) return false;
		for (final var comp : comps) {
			if (!comp.getAttributeSet().equals(me) && !(comp.getFactory() instanceof Tunnel)) {
				final var Label =
						(comp.getAttributeSet().containsAttribute(StdAttr.LABEL))
								? comp.getAttributeSet().getValue(StdAttr.LABEL)
								: "";
				if (Label.equalsIgnoreCase(name)) {
					if (showDialog) {
						DialogManager.createErrorDialog("Error", LogisimFX.util.LC.get("UsedLabelNameError"));
					}
					return true;
				}
			}
		}
		// we do not have to check the wires as (1) Wire is a reserved keyword,
		// and (2) they cannot have a label
		return false;
	}

	private static boolean isComponentName(String name, Set<Component> comps, Boolean showDialog) {
		if (name.isEmpty()) return false;
		for (final var comp : comps) {
			if (comp.getFactory().getName().equalsIgnoreCase(name)) {
				if (showDialog) {
					DialogManager.createErrorDialog("Error", LogisimFX.util.LC.get("ComponentLabelNameError"));
				}
				return true;
			}
		}
		// we do not have to check the wires as (1) Wire is a reserved keyword,
		// and (2) they cannot have a label
		return false;
	}



	private MyComponentListener myComponentListener = new MyComponentListener();
	private CircuitAppearance appearance;
	private AttributeSet staticAttrs;
	private SubcircuitFactory subcircuitFactory;
	private EventSourceWeakSupport<CircuitListener> listeners
			= new EventSourceWeakSupport<CircuitListener>();
	private HashSet<Component> comps = new HashSet<Component>(); // doesn't include wires
	CircuitWires wires = new CircuitWires();
	// wires is package-protected for CircuitState and Analyze only.
	private ArrayList<Component> clocks = new ArrayList<Component>();
	private CircuitLocker locker;
	private WeakHashMap<Component, Circuit> circuitsUsingThis;

	private final Netlist netList;



	public Circuit(String name) {

		appearance = new CircuitAppearance(this);
		staticAttrs = CircuitAttributes.createBaseAttrs(this, name);
		subcircuitFactory = new SubcircuitFactory(this);
		locker = new CircuitLocker();
		circuitsUsingThis = new WeakHashMap<Component, Circuit>();
		netList = new Netlist(this);

	}

	CircuitLocker getLocker() {
		return locker;
	}

	public Collection<Circuit> getCircuitsUsingThis() {
		return circuitsUsingThis.values();
	}

	public void mutatorClear() {

		locker.checkForWritePermission("clear");

		Set<Component> oldComps = comps;
		comps = new HashSet<Component>();
		wires = new CircuitWires();
		clocks.clear();
		netList.clear();
		for (Component comp : oldComps) {
			if (comp.getFactory() instanceof SubcircuitFactory) {
				SubcircuitFactory sub = (SubcircuitFactory) comp.getFactory();
				sub.getSubcircuit().circuitsUsingThis.remove(comp);
			}
		}

		fireEvent(CircuitEvent.ACTION_CLEAR, oldComps);

	}

	@Override
	public String toString() {
		return staticAttrs.getValue(CircuitAttributes.NAME_ATTR);
	}

	public AttributeSet getStaticAttributes() {
		return staticAttrs;
	}

	//
	// Listener methods
	//
	public void addCircuitListener(CircuitListener what) {
		listeners.add(what);
	}

	public void removeCircuitListener(CircuitListener what) {
		listeners.remove(what);
	}

	void fireEvent(int action, Object data) {
		fireEvent(new CircuitEvent(action, this, data));
	}

	private void fireEvent(CircuitEvent event) {
		for (CircuitListener l : listeners) {
			l.circuitChanged(event);
		}
	}

	//
	// access methods
	//
	public String getName() {
		return staticAttrs.getValue(CircuitAttributes.NAME_ATTR);
	}

	public CircuitAppearance getAppearance() {
		return appearance;
	}

	public SubcircuitFactory getSubcircuitFactory() {
		return subcircuitFactory;
	}

	public Set<WidthIncompatibilityData> getWidthIncompatibilityData() {
		return wires.getWidthIncompatibilityData();
	}

	public BitWidth getWidth(Location p) {
		return wires.getWidth(p);
	}

	public Location getWidthDeterminant(Location p) {
		return wires.getWidthDeterminant(p);
	}

	public boolean hasConflict(Component comp) {
		return wires.points.hasConflict(comp);
	}

	public Component getExclusive(Location loc) {
		return wires.points.getExclusive(loc);
	}

	private Set<Component> getComponents() {
		return CollectionUtil.createUnmodifiableSetUnion(comps, wires.getWires());
	}

	public boolean contains(Component c) {
		return comps.contains(c) || wires.getWires().contains(c);
	}

	public Set<Wire> getWires() {
		return wires.getWires();
	}

	public Set<Component> getNonWires() {
		return comps;
	}

	public Collection<? extends Component> getComponents(Location loc) {
		return wires.points.getComponents(loc);
	}

	public Collection<? extends Component> getSplitCauses(Location loc) {
		return wires.points.getSplitCauses(loc);
	}

	public Collection<Wire> getWires(Location loc) {
		return wires.points.getWires(loc);
	}

	public Collection<? extends Component> getNonWires(Location loc) {
		return wires.points.getNonWires(loc);
	}

	public boolean isConnected(Location loc, Component ignore) {

		for (Component o : wires.points.getComponents(loc)) {
			if (o != ignore) return true;
		}

		return false;

	}

	public Set<Location> getSplitLocations() {
		return wires.points.getSplitLocations();
	}

	public Collection<Component> getAllContaining(Location pt) {

		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (comp.contains(pt)) ret.add(comp);
		}

		return ret;

	}

	public Collection<Component> getAllContaining(Location pt, Graphics g) {

		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (comp.contains(pt, g)) ret.add(comp);
		}

		return ret;

	}

	public Collection<Component> getAllWithin(Bounds bds) {

		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (bds.contains(comp.getBounds())) ret.add(comp);
		}

		return ret;

	}

	public Collection<Component> getAllWithin(Bounds bds, Graphics g) {

		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (bds.contains(comp.getBounds(g))) ret.add(comp);
		}

		return ret;

	}

	public WireSet getWireSet(Wire start) {
		return wires.getWireSet(start);
	}

	public Bounds getBounds() {

		Bounds wireBounds = wires.getWireBounds();
		Iterator<Component> it = comps.iterator();
		if (!it.hasNext()) return wireBounds;
		Component first = it.next();
		Bounds firstBounds = first.getBounds();
		int xMin = firstBounds.getX();
		int yMin = firstBounds.getY();
		int xMax = xMin + firstBounds.getWidth();
		int yMax = yMin + firstBounds.getHeight();
		while (it.hasNext()) {
			Component c = it.next();
			Bounds bds = c.getBounds();
			int x0 = bds.getX();
			int x1 = x0 + bds.getWidth();
			int y0 = bds.getY();
			int y1 = y0 + bds.getHeight();
			if (x0 < xMin) xMin = x0;
			if (x1 > xMax) xMax = x1;
			if (y0 < yMin) yMin = y0;
			if (y1 > yMax) yMax = y1;
		}
		Bounds compBounds = Bounds.create(xMin, yMin, xMax - xMin, yMax - yMin);
		if (wireBounds.getWidth() == 0 || wireBounds.getHeight() == 0) {
			return compBounds;
		} else {
			return compBounds.add(wireBounds);
		}

	}

	public Bounds getBounds(Graphics g) {

		Bounds ret = wires.getWireBounds();
		int xMin = ret.getX();
		int yMin = ret.getY();
		int xMax = xMin + ret.getWidth();
		int yMax = yMin + ret.getHeight();
		if (ret == Bounds.EMPTY_BOUNDS) {
			xMin = Integer.MAX_VALUE;
			yMin = Integer.MAX_VALUE;
			xMax = Integer.MIN_VALUE;
			yMax = Integer.MIN_VALUE;
		}
		for (Component c : comps) {
			Bounds bds = c.getBounds(g);
			if (bds != null && bds != Bounds.EMPTY_BOUNDS) {
				int x0 = bds.getX();
				int x1 = x0 + bds.getWidth();
				int y0 = bds.getY();
				int y1 = y0 + bds.getHeight();
				if (x0 < xMin) xMin = x0;
				if (x1 > xMax) xMax = x1;
				if (y0 < yMin) yMin = y0;
				if (y1 > yMax) yMax = y1;
			}
		}
		if (xMin > xMax || yMin > yMax) return Bounds.EMPTY_BOUNDS;

		return Bounds.create(xMin, yMin, xMax - xMin, yMax - yMin);

	}

	ArrayList<Component> getClocks() {
		return clocks;
	}

	//
	// action methods
	//
	public void setName(String name) {
		staticAttrs.setValue(CircuitAttributes.NAME_ATTR, name);
	}

	void mutatorAdd(Component c) {

		locker.checkForWritePermission("add");

		netList.clear();

		if (c instanceof Wire) {
			Wire w = (Wire) c;
			if (w.getEnd0().equals(w.getEnd1())) return;
			boolean added = wires.add(w);
			if (!added) return;
		} else {
			// add it into the circuit
			boolean added = comps.add(c);
			if (!added) return;

			if (c.getAttributeSet().containsAttribute(StdAttr.LABEL) && !(c.getFactory() instanceof Tunnel)) {
				final var labels = new HashSet<String>();
				for (final var comp : comps) {
					if (comp.equals(c) || comp.getFactory() instanceof Tunnel) continue;
					if (comp.getAttributeSet().containsAttribute(StdAttr.LABEL)) {
						final var label = comp.getAttributeSet().getValue(StdAttr.LABEL);
						if (StringUtil.isNotEmpty(label)) labels.add(label.toUpperCase());
					}
				}
				/* we also have to check for the entity name */
				if (getName() != null && !getName().isEmpty()) labels.add(getName());
				final var label = c.getAttributeSet().getValue(StdAttr.LABEL);
				if ((StringUtil.isNotEmpty(label) && labels.contains(label.toUpperCase()))
						|| !SyntaxChecker.isVariableNameAcceptable(label, false)) {
					c.getAttributeSet().setValue(StdAttr.LABEL, "");
				}
			}

			wires.add(c);
			ComponentFactory factory = c.getFactory();
			if (factory instanceof Clock) {
				clocks.add(c);
			} else if (factory instanceof SubcircuitFactory) {
				SubcircuitFactory subcirc = (SubcircuitFactory) factory;
				subcirc.getSubcircuit().circuitsUsingThis.put(c, this);
			}
			c.addComponentListener(myComponentListener);
		}

		fireEvent(CircuitEvent.ACTION_ADD, c);

	}

	void mutatorRemove(Component c) {

		locker.checkForWritePermission("remove");

		netList.clear();

		if (c instanceof Wire) {
			wires.remove(c);
		} else {
			wires.remove(c);
			comps.remove(c);
			ComponentFactory factory = c.getFactory();
			if (factory instanceof Clock) {
				clocks.remove(c);
			} else if (factory instanceof SubcircuitFactory) {
				SubcircuitFactory subcirc = (SubcircuitFactory) factory;
				subcirc.getSubcircuit().circuitsUsingThis.remove(c);
			}
			c.removeComponentListener(myComponentListener);
		}

		fireEvent(CircuitEvent.ACTION_REMOVE, c);

	}

	private void removeWrongLabels(String label) {
		var changed = false;
		for (final var comp : comps) {
			final var attrs = comp.getAttributeSet();
			if (attrs.containsAttribute(StdAttr.LABEL)) {
				final var compLabel = attrs.getValue(StdAttr.LABEL);
				if (label.equalsIgnoreCase(compLabel)) {
					attrs.setValue(StdAttr.LABEL, "");
					changed = true;
				}
			}
		}
		// we do not have to check the wires as (1) Wire is a reserved keyword,
		// and (2) they cannot have a label
		if (changed){
			DialogManager.createErrorDialog("Error!", LC_util.getInstance().get("ComponentLabelCollisionError"));
		}

	}

	//
	// Graphics methods
	//
	public void draw(ComponentDrawContext context, Collection<Component> hidden,
					 int upperLeftX, int upperLeftY, int bottomRightX, int bottomRightY) {

		wires.draw(context, hidden);

		if (hidden == null || hidden.size() == 0) {
			for (Component c : comps) {
				if (((c.getBounds().getX() >= upperLeftX && c.getBounds().getX() <= bottomRightX) ||
						(c.getBounds().getX() + c.getBounds().getWidth() >= upperLeftX && c.getBounds().getX() + c.getBounds().getWidth() <= bottomRightX))
						&&
						((c.getBounds().getY() >= upperLeftY && c.getBounds().getY() <= bottomRightY) ||
								(c.getBounds().getY() + c.getBounds().getHeight() >= upperLeftY && c.getBounds().getY() + c.getBounds().getHeight() <= bottomRightY))
				) {
					c.draw(context);
				}
			}
		} else {
			for (Component c : comps) {
				if (!hidden.contains(c)) {
					try {
						c.draw(context);
					} catch (RuntimeException e) {
						// this is a JAR developer error - display it and move on
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void draw(ComponentDrawContext context, Collection<Component> hidden) {

		wires.draw(context, hidden);

		if (hidden == null || hidden.size() == 0) {
			for (Component c : comps) {
				c.draw(context);
			}
		} else {
			for (Component c : comps) {
				if (!hidden.contains(c)) {
					try {
						c.draw(context);
					} catch (RuntimeException e) {
						// this is a JAR developer error - display it and move on
						e.printStackTrace();
					}
				}
			}
		}

	}

	//
	// helper methods for other classes in package
	//
	public static boolean isInput(Component comp) {
		return comp.getEnd(0).getType() != EndData.INPUT_ONLY;
	}


	private boolean isAnnotated;

	public Netlist getNetList() {
		return netList;
	}

	private static String getAnnotationName(Component comp) {
		String componentName;
		/* Pins are treated specially */
		if (comp.getFactory() instanceof Pin) {
			if (comp.getEnd(0).isOutput()) {
				if (comp.getEnd(0).getWidth().getWidth() > 1) {
					componentName = "Input_bus";
				} else {
					componentName = "Input";
				}
			} else {
				if (comp.getEnd(0).getWidth().getWidth() > 1) {
					componentName = "Output_bus";
				} else {
					componentName = "Output";
				}
			}
		} else {
			componentName = comp.getFactory().getHDLName(comp.getAttributeSet());
		}
		return componentName;
	}

	public void annotate(boolean clearExistingLabels) {
		/* If I am already completely annotated, return */
		if (isAnnotated) {
			// FIXME: hardcoded string
			Reporter.report.addInfo("Nothing to annotate!");
			return;
		}
		final var comps = new TreeSet<Component>((a, b) -> {
			final Location aloc = a.getLocation();
			final Location bloc = b.getLocation();
			if (aloc.getY() != bloc.getY()) {
				return aloc.getY() - bloc.getY();
			} else if (aloc.getX() != bloc.getX()) {
				return aloc.getX() - bloc.getX();
			} else {
				return a.hashCode() - b.hashCode();
			}
		});
		final var labelers = new HashMap<String, AutoLabel>();
		final var labelNames = new LinkedHashSet<String>();
		final var subCircuits = new LinkedHashSet<Circuit>();
		for (final var comp : getNonWires()) {
			if (comp.getFactory() instanceof Tunnel) continue;
			/* we are directly going to remove duplicated labels */
			final var attrs = comp.getAttributeSet();
			if (attrs.containsAttribute(StdAttr.LABEL)) {
				final var label = attrs.getValue(StdAttr.LABEL);
				if (!label.isEmpty()) {
					if (labelNames.contains(label.toUpperCase())) {
						final var act = new SetAttributeAction(this, LC.createStringBinding("changeComponentAttributesAction"));
						act.set(comp, StdAttr.LABEL, "");
						act.doIt(null);
						// FIXME: hardcoded string
						Reporter.report.addSevereWarning("Removed duplicated label " + this.getName() + "/" + label);
					} else {
						labelNames.add(label.toUpperCase());
					}
				}
			}
			/* now we only process those that require a label */
			if (comp.getFactory().requiresNonZeroLabel()) {
				if (clearExistingLabels) {
					/* in case of label cleaning, we clear first the old label */
					// FIXME: hardcoded string
					Reporter.report.addInfo("Cleared " + this.getName() + "/" + comp.getAttributeSet().getValue(StdAttr.LABEL));
					final var act = new SetAttributeAction(this, LC.createStringBinding("changeComponentAttributesAction"));
					act.set(comp, StdAttr.LABEL, "");
					act.doIt(null);
				}
				if (comp.getAttributeSet().getValue(StdAttr.LABEL).isEmpty()) {
					comps.add(comp);
					final var componentName = getAnnotationName(comp);
					if (!labelers.containsKey(componentName)) {
						labelers.put(componentName, new AutoLabel(componentName + "_0", this));
					}
				}
			}
			/* if the current component is a sub-circuit, recurse into it */
			if (comp.getFactory() instanceof SubcircuitFactory) {
				subCircuits.add(((SubcircuitFactory) comp.getFactory()).getSubcircuit());
			}
		}
		/* Now Annotate */
		var sizeMightHaveChanged = false;
		for (final var comp : comps) {
			final var componentName = getAnnotationName(comp);
			if (!labelers.containsKey(componentName) || !labelers.get(componentName).hasNext(this)) {
				// This should never happen!
				// FIXME: hardcoded string
				Reporter.report.addFatalError(
						"Annotate internal Error: Either there exists duplicate labels or the label syntax is incorrect!\nPlease try annotation on labeled components also\n");
				return;
			} else {
				final var newLabel = labelers.get(componentName).getNext(this, comp.getFactory());
				final var act = new SetAttributeAction(this, LC.createStringBinding("changeComponentAttributesAction"));
				act.set(comp, StdAttr.LABEL, newLabel);
				act.doIt(null);
				Reporter.report.addInfo("Labeled " + this.getName() + "/" + newLabel);
				if (comp.getFactory() instanceof Pin) {
					sizeMightHaveChanged = true;
				}
			}
		}
		if (sizeMightHaveChanged)
			// FIXME: hardcoded string
			Reporter.report.addSevereWarning(
					"Annotated one ore more pins in circuit \""
							+ this.getName()
							+ "\" this might have changed it's boxsize and might have impacted it's connections in circuits using this one!");
		isAnnotated = true;
		/* Now annotate all circuits below me */
		for (final var subs : subCircuits) {
			subs.annotate(clearExistingLabels);
		}
	}

	//
	// Annotation module for all components that require a non-zero-length label
	public void clearAnnotationLevel() {
		isAnnotated = false;
		netList.clear();
		for (final var comp : this.getNonWires()) {
			if (comp.getFactory() instanceof SubcircuitFactory) {
				((SubcircuitFactory) comp.getFactory()).getSubcircuit().clearAnnotationLevel();
			}
		}
	}

}

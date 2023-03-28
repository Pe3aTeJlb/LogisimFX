/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.*;
import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.SelectionActions;
import LogisimFX.newgui.MainFrame.ToolAttributeAction;
import LogisimFX.tools.key.KeyConfigurationEvent;
import LogisimFX.tools.key.KeyConfigurationResult;
import LogisimFX.tools.key.KeyConfigurator;
import LogisimFX.LogisimVersion;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitException;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Action;
import LogisimFX.proj.Dependencies;
import LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class AddTool extends Tool {

	private static int INVALID_COORD = Integer.MIN_VALUE;

	private static int SHOW_NONE    = 0;
	private static int SHOW_GHOST   = 1;
	private static int SHOW_ADD     = 2;
	private static int SHOW_ADD_NO  = 3;

	private static Cursor cursor = Cursor.CROSSHAIR;

	private class MyAttributeListener implements AttributeListener {
		public void attributeListChanged(AttributeEvent e) {
			bounds = null;
		}
		public void attributeValueChanged(AttributeEvent e) {
			bounds = null;
		}
	}

	private Class<? extends Library> descriptionBase;
	private FactoryDescription description;
	private boolean sourceLoadAttempted;
	private ComponentFactory factory;
	private AttributeSet attrs;
	private Bounds bounds;
	private boolean shouldSnap;
	private int lastX = INVALID_COORD;
	private int lastY = INVALID_COORD;
	private int state = SHOW_GHOST;
	private Action lastAddition;
	private boolean keyHandlerTried;
	private KeyConfigurator keyHandler;

	public AddTool(Class<? extends Library> base, FactoryDescription description) {
		this.descriptionBase = base;
		this.description = description;
		this.sourceLoadAttempted = false;
		this.shouldSnap = true;
		this.attrs = new FactoryAttributes(base, description);
		attrs.addAttributeListener(new MyAttributeListener());
		this.keyHandlerTried = false;
	}

	public AddTool(ComponentFactory source) {
		this.description = null;
		this.sourceLoadAttempted = true;
		this.factory = source;
		this.bounds = null;
		this.attrs = new FactoryAttributes(source);
		attrs.addAttributeListener(new MyAttributeListener());
		Boolean value = (Boolean) source.getFeature(ComponentFactory.SHOULD_SNAP, attrs);
		this.shouldSnap = value == null ? true : value.booleanValue();
	}

	private AddTool(AddTool base) {
		this.descriptionBase = base.descriptionBase;
		this.description = base.description;
		this.sourceLoadAttempted = base.sourceLoadAttempted;
		this.factory = base.factory;
		this.bounds = base.bounds;
		this.shouldSnap = base.shouldSnap;
		this.attrs = (AttributeSet) base.attrs.clone();
		attrs.addAttributeListener(new MyAttributeListener());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AddTool)) return false;
		AddTool o = (AddTool) other;
		if (this.description != null) {
			return this.descriptionBase == o.descriptionBase
				&& this.description.equals(o.description);
		} else {
			return this.factory.equals(o.factory);
		}
	}

	@Override
	public int hashCode() {
		FactoryDescription desc = description;
		return desc != null ? desc.hashCode() : factory.hashCode();
	}

	@Override
	public boolean sharesSource(Tool other) {
		if (!(other instanceof AddTool)) return false;
		AddTool o = (AddTool) other;
		if (this.sourceLoadAttempted && o.sourceLoadAttempted) {
			return this.factory.equals(o.factory);
		} else if (this.description == null) {
			return o.description == null;
		} else {
			return this.description.equals(o.description);
		}
	}

	public ComponentFactory getFactory(boolean forceLoad) {
		return forceLoad ? getFactory() : factory;
	}

	public ComponentFactory getFactory() {

		ComponentFactory ret = factory;
		if (ret != null || sourceLoadAttempted) {
			return ret;
		} else {
			ret = description.getFactory(descriptionBase);
			if (ret != null) {
				AttributeSet base = getBaseAttributes();
				Boolean value = (Boolean) ret.getFeature(ComponentFactory.SHOULD_SNAP, base);
				shouldSnap = value == null ? true : value.booleanValue();
			}
			factory = ret;
			sourceLoadAttempted = true;
			return ret;
		}

	}

	@Override
	public String getName() {
		FactoryDescription desc = description;
		return desc == null ? factory.getName() : desc.getName();
	}

	@Override
	public StringBinding getDisplayName() {
		FactoryDescription desc = description;
		return desc == null ? factory.getDisplayName() : desc.getDisplayName();
	}

	@Override
	public StringBinding getDescription() {

		StringBinding ret = null;
		FactoryDescription desc = description;

		if (desc != null) {
			ret = desc.getToolTip();
		} else {
			ComponentFactory source = getFactory();
			if (source != null) {
				ret = (StringBinding) source.getFeature(ComponentFactory.TOOL_TIP,
						getAttributeSet());
			}
		}

		if (ret == null) {
			ret = (StringBinding) LC.createStringBinding("addToolText").concat(getDisplayName());
		}

		return ret;

	}

	@Override
	public ImageView getIcon() {

		if (description != null && !description.isFactoryLoaded()) {
			return description.getIcon();
		}

		ComponentFactory source = getFactory();

		if (source != null) {
			return source.getIcon();
		}

		return null;

	}

	@Override
	public Tool cloneTool() {
		return new AddTool(this);
	}

	@Override
	public AttributeSet getAttributeSet() {
		return attrs;
	}

	@Override
	public boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver) {
		return this.attrs == attrs;
		/*
		return this.attrs == attrs && attrs instanceof FactoryAttributes
			&& !((FactoryAttributes) attrs).isFactoryInstantiated();*/
	}

	@Override
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {
		return getFactory().getDefaultAttributeValue(attr, ver);
	}

	@Override
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {
		// next "if" suggested roughly by Kevin Walsh of Cornell to take care of
		// repaint problems on OpenJDK under Ubuntu
		int x = lastX;
		int y = lastY;
		if (x == INVALID_COORD || y == INVALID_COORD) return;
		ComponentFactory source = getFactory();
		if (source == null) return;
		if (state == SHOW_GHOST) {
			source.drawGhost(context, Color.GRAY, x, y, getBaseAttributes());
		} else if (state == SHOW_ADD) {
			source.drawGhost(context, Color.BLACK, x, y, getBaseAttributes());
		}

	}

	private AttributeSet getBaseAttributes() {
		AttributeSet ret = attrs;
		if (ret instanceof FactoryAttributes) {
			ret = ((FactoryAttributes) ret).getBase();
		}
		return ret;
	}

	public void cancelOp() { }

	@Override
	public void select(LayoutCanvas canvas) {
		setState(canvas, SHOW_GHOST);
		bounds = null;
	}

	@Override
	public void deselect(LayoutCanvas canvas) {
		setState(canvas, SHOW_GHOST);
		moveTo(canvas, canvas.getGraphics(), INVALID_COORD, INVALID_COORD);
		bounds = null;
		lastAddition = null;
	}

	private synchronized void moveTo(LayoutCanvas canvas, Graphics g, int x, int y) {
		lastX = x;
		lastY = y;
	}

	@Override
	public void mouseEntered(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (state == SHOW_GHOST || state == SHOW_NONE) {
			setState(canvas, SHOW_GHOST);
			canvas.requestFocus();
		} else if (state == SHOW_ADD_NO) {
			setState(canvas, SHOW_ADD);
			canvas.requestFocus();
		}
	}

	@Override
	public void mouseExited(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (state == SHOW_GHOST) {
			moveTo(canvas, canvas.getGraphics(), INVALID_COORD, INVALID_COORD);
			setState(canvas, SHOW_NONE);
		} else if (state == SHOW_ADD) {
			moveTo(canvas, canvas.getGraphics(), INVALID_COORD, INVALID_COORD);
			setState(canvas, SHOW_ADD_NO);
		}
	}

	@Override
	public void mouseMoved(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (state != SHOW_NONE) {
			int x = 0;
			int y = 0;

			if (shouldSnap){
				x = e.snappedX;
				y = e.snappedY;
			}else{
				x = e.localX;
				y = e.localY;
			}
			moveTo(canvas, g, x, y);
			moveTo(canvas, g, x, y);
		}
	}

	@Override
	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		// verify the addition would be valid
		Circuit circ = canvas.getCircuit();
		if (!canvas.getProject().getLogisimFile().contains(circ)) {
			canvas.setErrorMessage(LC.createStringBinding("cannotModifyError"),null);
			return;
		}
		if (factory instanceof SubcircuitFactory) {
			SubcircuitFactory circFact = (SubcircuitFactory) factory;
			Dependencies depends = canvas.getProject().getDependencies();
			if (!depends.canAdd(circ, circFact.getSubcircuit())) {
				canvas.setErrorMessage(LC.createStringBinding("circularError"),null);
				return;
			}
		}

		int x = 0;
		int y = 0;

		if (shouldSnap){
			x = e.snappedX;
			y = e.snappedY;
		}else{
			x = e.localX;
			y = e.localY;
		}
		moveTo(canvas, g, x, y);
		moveTo(canvas, g, x, y);
		setState(canvas, SHOW_ADD);
	}

	@Override
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {
		if (state != SHOW_NONE) {
			int x = 0;
			int y = 0;

			if (shouldSnap){
				x = e.snappedX;
				y = e.snappedY;
			}else{
				x = e.localX;
				y = e.localY;
			}
			moveTo(canvas, g, x, y);
			moveTo(canvas, g, x, y);
		}
	}

	@Override
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) {

		Component added = null;
		if (state == SHOW_ADD) {

			Circuit circ = canvas.getCircuit();
			if (!canvas.getProject().getLogisimFile().contains(circ)) return;

			int x = 0;
			int y = 0;

			if (shouldSnap){
				x = e.snappedX;
				y = e.snappedY;
			}else{
				x = e.localX;
				y = e.localY;
			}
			moveTo(canvas, g, x, y);

			Location loc = Location.create(x, y);
			AttributeSet attrsCopy = (AttributeSet) attrs.clone();
			ComponentFactory source = getFactory();
			if (source == null) return;
			Component c = source.createComponent(loc, attrsCopy);

			if (circ.hasConflict(c)) {
				canvas.setErrorMessage(LC.createStringBinding("exclusiveError"),null);
				return;
			}

			Bounds bds = c.getBounds(g);
			if (bds.getX() < 0 || bds.getY() < 0) {
				canvas.setErrorMessage(LC.createStringBinding("negativeCoordError"),null);
				return;
			}

			try {
				CircuitMutation mutation = new CircuitMutation(circ);
				mutation.add(c);
				Action action = mutation.toAction(LC.createComplexStringBinding("addComponentAction", factory.getDisplayGetter().getValue()));
				canvas.getProject().doAction(action);
				lastAddition = action;
				added = c;
			} catch (CircuitException ex) {
				DialogManager.CreateStackTraceDialog("",ex.getCause().toString(),ex);
			}
			setState(canvas, SHOW_GHOST);
		} else if (state == SHOW_ADD_NO) {
			setState(canvas, SHOW_NONE);
		}

		Project proj = canvas.getProject();
		Tool next = determineNext(proj);

		if (next != null) {
			proj.setTool(next);
			Action act = SelectionActions.dropAll(canvas.getSelection());
			if (act != null) {
				proj.doAction(act);
			}
			if (added != null) canvas.getSelection().add(added);
		}

	}
	
	private Tool determineNext(Project proj) {

		String afterAdd = AppPreferences.ADD_AFTER.get();
		if (afterAdd.equals(AppPreferences.ADD_AFTER_UNCHANGED)) {
			return null;
		} else { // switch to Edit Tool
			Library base = proj.getLogisimFile().getLibrary("Base");
			if (base == null) {
				return null;
			} else {
				return base.getTool("Edit Tool");
			}
		}

	}
	
	@Override
	public void keyPressed(LayoutCanvas canvas, KeyEvent event) {
		processKeyEvent(canvas, event, KeyConfigurationEvent.KEY_PRESSED);

		if (!event.isConsumed() && !event.isShortcutDown()) {
			switch (event.getCode()) {
			case UP:    setFacing(canvas, Direction.NORTH); break;
			case DOWN:  setFacing(canvas, Direction.SOUTH); break;
			case LEFT:  setFacing(canvas, Direction.WEST); break;
			case RIGHT: setFacing(canvas, Direction.EAST); break;
			case BACK_SPACE:
				if (lastAddition != null && canvas.getProject().getLastAction() == lastAddition) {
					canvas.getProject().undoAction();
					lastAddition = null;
				}
			}
		}
	}
	
	@Override
	public void keyReleased(LayoutCanvas canvas, KeyEvent event) {
		processKeyEvent(canvas, event, KeyConfigurationEvent.KEY_RELEASED);
	}
	
	@Override
	public void keyTyped(LayoutCanvas canvas, KeyEvent event) {
		processKeyEvent(canvas, event, KeyConfigurationEvent.KEY_TYPED);
	}
	
	private void processKeyEvent(LayoutCanvas canvas, KeyEvent event, int type) {
		KeyConfigurator handler = keyHandler;
		if (!keyHandlerTried) {
			ComponentFactory source = getFactory();
			AttributeSet baseAttrs = getBaseAttributes();
			handler = (KeyConfigurator) source.getFeature(KeyConfigurator.class, baseAttrs);
			keyHandler = handler;
			keyHandlerTried = true;
		}

		if (handler != null) {
			AttributeSet baseAttrs = getBaseAttributes();
			KeyConfigurationEvent e = new KeyConfigurationEvent(type, baseAttrs, event, this);
			KeyConfigurationResult r = handler.keyEventReceived(e);
			if (r != null) {
				Action act = ToolAttributeAction.create(r);
				canvas.getProject().doAction(act);
			}
		}
	}
	
	private void setFacing(LayoutCanvas canvas, Direction facing) {
		ComponentFactory source = getFactory();
		if (source == null) return;
		AttributeSet base = getBaseAttributes();
		Object feature = source.getFeature(ComponentFactory.FACING_ATTRIBUTE_KEY, base);
		@SuppressWarnings("unchecked")
        Attribute<Direction> attr = (Attribute<Direction>) feature;
		if (attr != null) {
			Action act = ToolAttributeAction.create(this, attr, facing);
			canvas.getProject().doAction(act);
		}
	}

	@Override
	public Cursor getCursor() { return cursor; }

	private void setState(LayoutCanvas canvas, int value) {
		if (value == SHOW_GHOST) {
			if (canvas.getProject().getLogisimFile().contains(canvas.getCircuit())
					&& AppPreferences.ADD_SHOW_GHOSTS.getBoolean()) {
				state = SHOW_GHOST;
			} else {
				state = SHOW_NONE;
			}
		} else{
			state = value;
		}
	}

	private Bounds getBounds() {
		Bounds ret = bounds;
		if (ret == null) {
			ComponentFactory source = getFactory();
			if (source == null) {
				ret = Bounds.EMPTY_BOUNDS;
			} else {
				AttributeSet base = getBaseAttributes();
				ret = source.getOffsetBounds(base).expand(5);
			}
			bounds = ret;
		}
		return ret;
	}

}

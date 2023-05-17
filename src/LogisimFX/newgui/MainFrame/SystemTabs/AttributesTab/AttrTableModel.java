/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.Wire;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.draw.actions.ModelChangeAttributeAction;
import LogisimFX.draw.model.AttributeMapKey;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.tools.AbstractTool;
import LogisimFX.draw.tools.DrawingAttributeSet;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.SelectionAttributes;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.Selection;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.ToolAttributeAction;
import LogisimFX.proj.Project;
import LogisimFX.tools.SetAttributeAction;
import LogisimFX.tools.Tool;

import javafx.beans.binding.StringBinding;

import java.util.HashMap;
import java.util.Map;

public abstract class AttrTableModel {

    private AttributeSet attrs;

    public AttrTableModel(AttributeSet attrs){
        this.attrs = attrs;
    }

    public abstract StringBinding getTitle();

    public abstract StringBinding getViewedObjectName();

    public void setAttributeSet(AttributeSet value) {

        if (attrs != value) {
            attrs = value;
        }

    }

    public AttributeSet getAttributeSet() {
        return attrs;
    }

    public abstract void setValueRequested(Attribute<Object> attr, Object value)
            throws AttrTableSetException;


}

//Layout view
class AttrTableCircuitModel extends AttrTableModel{

    private Project proj;
    private Circuit circ;

    public AttrTableCircuitModel(Project proj, Circuit circ) {
        super(circ.getStaticAttributes());
        this.proj = proj;
        this.circ = circ;
    }

    public void setCircuit(Circuit circ){
        this.circ = circ;
        setAttributeSet(circ.getStaticAttributes());
    }

    @Override
    public StringBinding getTitle() {
        return LC.createComplexStringBinding("circuitAttrTitle", circ.getName());
    }

    @Override
    public StringBinding getViewedObjectName() {
        return circ.getSubcircuitFactory().getDisplayName();
    }

    @Override
    public void setValueRequested(Attribute<Object> attr, Object value) throws AttrTableSetException {

    }

}

class AttrTableComponentModel extends AttrTableModel{

    private Project proj;
    private Circuit circ;
    private Component comp;

    public AttrTableComponentModel(Project proj, Circuit circ, Component comp) {
        super(circ.getStaticAttributes());
        this.proj = proj;
        this.circ = circ;
        this.comp = comp;
    }

    @Override
    public StringBinding getTitle() {
        return comp.getFactory().getDisplayName();
    }

    @Override
    public StringBinding getViewedObjectName() {
        return comp.getFactory().getDisplayName();
    }

    @Override
    public void setValueRequested(Attribute<Object> attr, Object value) throws AttrTableSetException {

        if (!proj.getLogisimFile().contains(circ)) {
            String msg = LC.get("cannotModifyCircuitError");
            throw new AttrTableSetException(msg);
        } else {
            SetAttributeAction act = new SetAttributeAction(circ,
                    LC.createStringBinding("changeAttributeAction"));
            act.set(comp, attr, value);
            proj.doAction(act);
        }

    }

}

class AttrTableToolModel extends AttrTableModel{

    private Project proj;
    private Tool tool;

    public AttrTableToolModel(Project proj, Tool tool) {
        super(null);
        this.proj = proj;
        this.tool = tool;
        setAttributeSet(tool.getAttributeSet());
    }

    public void setTool(Tool tool){
        this.tool = tool;
        setAttributeSet(tool.getAttributeSet());
    }

    @Override
    public StringBinding getTitle() {
        return LC.createComplexStringBinding("toolAttrTitle", tool.getDisplayName().getValue());
    }

    @Override
    public StringBinding getViewedObjectName() {
        return tool.getDisplayName();
    }

    @Override
    public void setValueRequested(Attribute<Object> attr, Object value) {
        proj.doAction(ToolAttributeAction.create(tool, attr, value));
    }

}

class AttrTableSelectionModel extends AttrTableModel{

    private Project proj;
    private Selection selection;
    private StringBinding viewedObjectName;

    public AttrTableSelectionModel(Project proj) {

        super(null);
        this.proj = proj;
        selection = proj.getFrameController().getLayoutCanvas().getSelection();
        setAttributeSet(selection.getAttributeSet());

    }

    @Override
    public StringBinding getTitle() {

        ComponentFactory wireFactory = null;
        ComponentFactory factory = null;
        int factoryCount = 0;
        int totalCount = 0;
        boolean variousFound = false;

        Selection selection = proj.getFrameController().getLayoutCanvas().getSelection();
        for (Component comp : selection.getComponents()) {
            ComponentFactory fact = comp.getFactory();
            if (fact == factory) {
                factoryCount++;
            } else if (comp instanceof Wire) {
                wireFactory = fact;
                if (factory == null) {
                    factoryCount++;
                }
            } else if (factory == null) {
                factory = fact;
                factoryCount = 1;
            } else {
                variousFound = true;
            }
            if (!(comp instanceof Wire)) {
                totalCount++;
            }
        }

        if (factory == null) {
            factory = wireFactory;
        }

        if (variousFound) {
            viewedObjectName = LC.createStringBinding("selectionTitle");
            return (LC.createComplexStringBinding("selectionVarious", "" + totalCount));
        } else if (factoryCount == 0) {
            viewedObjectName = proj.getCurrentCircuit().getSubcircuitFactory().getDisplayName();
            String circName = proj.getCurrentCircuit().getName();
            return (LC.createComplexStringBinding("circuitAttrTitle", circName));
        } else if (factoryCount == 1) {
            viewedObjectName = factory.getDisplayName();
            return (LC.createComplexStringBinding("selectionOne", factory.getDisplayName().getValue()));
        } else {
            viewedObjectName = factory.getDisplayName();
            return (LC.createComplexStringBinding("selectionMultiple", factory.getDisplayName().getValue(),
                    "" + factoryCount));
        }

    }

    @Override
    public StringBinding getViewedObjectName() {
        return viewedObjectName;
    }

    @Override
    public void setValueRequested(Attribute<Object> attr, Object value) throws AttrTableSetException {

        Circuit circuit = proj.getCurrentCircuit();
        if (selection.isEmpty() && circuit != null) {
            AttrTableCircuitModel circuitModel = new AttrTableCircuitModel(proj, circuit);
            circuitModel.setValueRequested(attr, value);
        } else {
            SetAttributeAction act = new SetAttributeAction(circuit,
                    LC.createStringBinding("selectionAttributeAction"));
            for (Component comp : selection.getComponents()) {
                if (!(comp instanceof Wire)) {
                    act.set(comp, attr, value);
                }
            }
            proj.doAction(act);
        }

    }

}

//Appearance view

class AttrTableAbstractToolModel extends AttrTableModel{

    private DrawingAttributeSet defaults;
    private AbstractTool currentTool;

    public AttrTableAbstractToolModel(AbstractTool tool) {
        super(null);
        this.defaults = new DrawingAttributeSet();
        this.currentTool = tool;
        setAttributeSet(defaults.createSubset(tool));
    }

    public void setTool(AbstractTool value) {
        currentTool = value;
        setAttributeSet(defaults.createSubset(value));
    }

    @Override
    public StringBinding getTitle() {
        return LC.castToBind(currentTool.getName());
    }

    @Override
    public StringBinding getViewedObjectName() {
        return LC.castToBind(currentTool.getName());
    }

    @Override
    public void setValueRequested(Attribute<Object> attr, Object value) throws AttrTableSetException {
        defaults.setValue(attr, value);
    }

}

class AttrTableAppearanceSelectionModel extends AttrTableModel{

    private Project proj;
    private AppearanceCanvas canvas;
    private LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.Selection selection;
    private StringBinding viewedObjectName;

    public AttrTableAppearanceSelectionModel(Project proj) {
        super(null);
        this.proj = proj;
        this.canvas = proj.getFrameController().getAppearanceCanvas();
        setAttributeSet(new SelectionAttributes(canvas.getSelection()));
        selection = proj.getFrameController().getAppearanceCanvas().getSelection();
    }

    public void setAttrs(){
        setAttributeSet(new SelectionAttributes(canvas.getSelection()));
    }

    @Override
    public StringBinding getTitle() {

        Class<? extends CanvasObject> commonClass = null;
        int commonCount = 0;
        CanvasObject firstObject = null;
        int totalCount = 0;
        for (CanvasObject obj : selection.getSelected()) {
            if (firstObject == null) {
                firstObject = obj;
                commonClass = obj.getClass();
                commonCount = 1;
            } else if (obj.getClass() == commonClass) {
                commonCount++;
            } else {
                commonClass = null;
            }
            totalCount++;
        }

        if (firstObject == null) {
            return null;
        } else if (commonClass == null) {
            viewedObjectName =  LC.createStringBinding("selectionTitle");
            return LC.createComplexStringBinding("selectionVarious", "" + totalCount);
        } else if (commonCount == 1) {
            viewedObjectName = LC.castToBind(firstObject.getDisplayName());
            return LC.createComplexStringBinding("selectionOne", firstObject.getDisplayName());
        } else {
            viewedObjectName = LC.castToBind(firstObject.getDisplayName());
            return LC.createComplexStringBinding("selectionMultiple", firstObject.getDisplayName(),
                    "" + commonCount);
        }

    }

    @Override
    public StringBinding getViewedObjectName() {
        return viewedObjectName;
    }

    @Override
    public void setValueRequested(Attribute<Object> attr, Object value) {

        SelectionAttributes attrs = (SelectionAttributes) getAttributeSet();
        HashMap<AttributeMapKey, Object> oldVals;
        oldVals = new HashMap<>();
        HashMap<AttributeMapKey, Object> newVals;
        newVals = new HashMap<>();
        for (Map.Entry<AttributeSet, CanvasObject> ent : attrs.entries()) {
            AttributeMapKey key = new AttributeMapKey(attr, ent.getValue());
            oldVals.put(key, ent.getKey().getValue(attr));
            newVals.put(key, value);
        }


        proj.getFrameController().getAppearanceCanvas().doAction(
                new ModelChangeAttributeAction(
                        proj.getFrameController().getAppearanceCanvas().getModel(), oldVals, newVals)
        );

    }

}

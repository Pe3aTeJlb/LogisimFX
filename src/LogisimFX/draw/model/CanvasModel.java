/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.model;

import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.Selection;
import LogisimFX.draw.shapes.Text;
import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CanvasModel {

	// listener methods
	void addCanvasModelListener(CanvasModelListener l);
	void removeCanvasModelListener(CanvasModelListener l);
	
	// methods that don't change any data in the model
	void paint(Graphics g, Selection selection);
	List<CanvasObject> getObjectsFromTop();
	List<CanvasObject> getObjectsFromBottom();
	Collection<CanvasObject> getObjectsIn(Bounds bds);
	Collection<CanvasObject> getObjectsOverlapping(CanvasObject shape);

	// methods that alter the model
	void addObjects(int index, Collection<? extends CanvasObject> shapes);
	void addObjects(Map<? extends CanvasObject, Integer> shapes);
	void removeObjects(Collection<? extends CanvasObject> shapes);
	void translateObjects(Collection<? extends CanvasObject> shapes, int dx, int dy);
	void reorderObjects(List<ReorderRequest> requests);
	Handle moveHandle(HandleGesture gesture);
	void insertHandle(Handle desired, Handle previous);
	Handle deleteHandle(Handle handle);
	void setAttributeValues(Map<AttributeMapKey,Object> values);
	void setText(Text text, String value);

}

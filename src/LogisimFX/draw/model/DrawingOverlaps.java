/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.model;

import java.util.*;

class DrawingOverlaps {
	private Map<CanvasObject, List<CanvasObject>> map;
	private Set<CanvasObject> untested;

	public DrawingOverlaps() {
		map = new HashMap<CanvasObject, List<CanvasObject>>();
		untested = new HashSet<CanvasObject>();
	}

	public Collection<CanvasObject> getObjectsOverlapping(CanvasObject o) {
		ensureUpdated();

		List<CanvasObject> ret = map.get(o);
		if (ret == null || ret.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(ret);
		}
	}

	private void ensureUpdated() {
		for (CanvasObject o : untested) {
			ArrayList<CanvasObject> over = new ArrayList<CanvasObject>();
			for (CanvasObject o2 : map.keySet()) {
				if (o != o2 && o.overlaps(o2)) {
					over.add(o2);
					addOverlap(o2, o);
				}
			}
			map.put(o, over);
		}
		untested.clear();
	}

	private void addOverlap(CanvasObject a, CanvasObject b) {
		List<CanvasObject> alist = map.get(a);
		if (alist == null) {
			alist = new ArrayList<CanvasObject>();
			map.put(a, alist);
		}
		if (!alist.contains(b)) {
			alist.add(b);
		}
	}

	public void addShape(CanvasObject shape) {
		untested.add(shape);
	}

	public void removeShape(CanvasObject shape) {
		untested.remove(shape);
		List<CanvasObject> mapped = map.remove(shape);
		if (mapped != null) {
			for (CanvasObject o : mapped) {
				List<CanvasObject> reverse = map.get(o);
				if (reverse != null) {
					reverse.remove(shape);
				}
			}
		}
	}

	public void invalidateShape(CanvasObject shape) {
		removeShape(shape);
		untested.add(shape);
	}

	public void invalidateShapes(Collection<? extends CanvasObject> shapes) {
		for (CanvasObject o : shapes) {
			invalidateShape(o);
		}
	}
}

/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit.appear;

import LogisimFX.draw.model.AbstractCanvasObject;
import LogisimFX.draw.shapes.SvgReader;
import LogisimFX.data.Direction;
import LogisimFX.data.Location;
import LogisimFX.instance.Instance;
import org.w3c.dom.Element;

import java.util.Map;

public class AppearanceSvgReader {
	public static AbstractCanvasObject createShape(Element elt, Map<Location, Instance> pins) {
		String name = elt.getTagName();
		if (name.equals("circ-anchor") || name.equals("circ-origin")) {
			Location loc = getLocation(elt);
			AbstractCanvasObject ret = new AppearanceAnchor(loc);
			if (elt.hasAttribute("facing")) {
				Direction facing = Direction.parse(elt.getAttribute("facing"));
				ret.setValue(AppearanceAnchor.FACING, facing);
			}
			return ret;
		} else if (name.equals("circ-port")) {
			Location loc = getLocation(elt);
			String[] pinStr = elt.getAttribute("pin").split(",");
			Location pinLoc = Location.create(Integer.parseInt(pinStr[0].trim()),
					Integer.parseInt(pinStr[1].trim()));
			Instance pin = pins.get(pinLoc);
			if (pin == null) {
				return null;
			} else {
				return new AppearancePort(loc, pin);
			}
		} else {
			return SvgReader.createShape(elt);
		}
	}
	
	private static Location getLocation(Element elt) {
		double x = Double.parseDouble(elt.getAttribute("x"));
		double y = Double.parseDouble(elt.getAttribute("y"));
		double w = Double.parseDouble(elt.getAttribute("width"));
		double h = Double.parseDouble(elt.getAttribute("height"));
		int px = (int) Math.round(x + w / 2);
		int py = (int) Math.round(y + h / 2);
		return Location.create(px, py);
	}
}

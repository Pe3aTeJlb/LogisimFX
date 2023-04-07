/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.data.Attribute;
import LogisimFX.data.Attributes;
import LogisimFX.data.Direction;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttrTableSetException;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.std.LC;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class NegateAttribute extends Attribute<Boolean> {

	private static Attribute<Boolean> BOOLEAN_ATTR = Attributes.forBoolean("negateDummy");
	
	int index;
	private Direction side;
	
	public NegateAttribute(int index, Direction side) {

		super("negate" + index, null);
		this.index = index;
		this.side = side;

	}
	
	@Override
	public boolean equals(Object other) {

		if (other instanceof NegateAttribute) {
			NegateAttribute o = (NegateAttribute) other;
			return this.index == o.index && this.side == o.side;
		} else {
			return false;
		}

	}
	
	@Override
	public int hashCode() {
		return index * 31 + (side == null ? 0 : side.hashCode());
	}
	
	@Override
	public String getDisplayName() {

		String ret = LC.getFormatted("gateNegateAttr", "" + (index + 1));
		if (side != null) {
			ret += " (" + side.toVerticalDisplayString() + ")"; 
		}
		return ret;

	}

	@Override
	public String toDisplayString(Boolean value) {
		return BOOLEAN_ATTR.toDisplayString(value);
	}

	@Override
	public Boolean parse(String value) {
		return BOOLEAN_ATTR.parse(value);
	}

	@Override
	public Node getCell(Boolean value){

		Boolean[] vals = { Boolean.TRUE, Boolean.FALSE };

		StringConverter<Boolean> converter = new StringConverter<Boolean>() {

			@Override
			public String toString(Boolean object) {
				return toDisplayString(object);
			}

			@Override
			public Boolean fromString(String string) {
				return parse(string);
			}

		};

		ComboBox<Boolean> cell = new ComboBox<>();
		cell.setMaxWidth(Double.MAX_VALUE);
		cell.getItems().addAll(vals);
		cell.setConverter(converter);
		cell.setValue(value);
		cell.setOnAction(event -> {
			try {
				AttributeTable.setValueRequested( this, cell.getValue());
			} catch (AttrTableSetException e) {
				e.printStackTrace();
			}
		});
		return cell;

	}

}

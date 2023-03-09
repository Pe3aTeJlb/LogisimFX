/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.data;

import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttrTableSetException;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;

import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class BitWidth implements Comparable<BitWidth> {

	public static final BitWidth UNKNOWN = new BitWidth(0);
	public static final BitWidth ONE = new BitWidth(1);

	private static BitWidth[] prefab = null;

	public static final int MAXWIDTH = Value.MAX_WIDTH;
	public static final int MINWIDTH = 1;

	public static class Attribute extends LogisimFX.data.Attribute<BitWidth> {
		private BitWidth[] choices;

		public Attribute(String name, StringBinding disp) {
			super(name, disp);
			ensurePrefab();
			choices = prefab;
		}

		public Attribute(String name, StringBinding disp, int min, int max) {
			super(name, disp);
			choices = new BitWidth[max - min + 1];
			for (int i = 0; i < choices.length; i++) {
				choices[i] = BitWidth.create(min + i);
			}
		}

		@Override
		public BitWidth parse(String value) {
			return BitWidth.parse(value);
		}

		@Override
		public Node getCell(BitWidth value){

			ComboBox<Object> cell = new ComboBox<>();
			cell.setMaxWidth(Double.MAX_VALUE);
			cell.getItems().addAll(choices);
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

	final int width;

	private BitWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}
	
	public int getMask() {
		if (width == 0)       return 0;
		else if (width == 32) return -1;
		else                 return (1 << width) - 1;
	}

	@Override
	public boolean equals(Object other_obj) {
		if (!(other_obj instanceof BitWidth)) return false;
		BitWidth other = (BitWidth) other_obj;
		return this.width == other.width;
	}

	public int compareTo(BitWidth other) {
		return this.width - other.width;
	}

	@Override
	public int hashCode() {
		return width;
	}

	@Override
	public String toString() {
		return "" + width;
	}

	public static BitWidth create(int width) {
		ensurePrefab();
		if (width <= 0) {
			if (width == 0) {
				return UNKNOWN;
			} else {
				throw new IllegalArgumentException("width " + width
					+ " must be positive");
			}
		} else if (width - 1 < prefab.length) {
			return prefab[width - 1];
		} else {
			return new BitWidth(width);
		}
	}

	public static BitWidth parse(String str) {
		if (str == null || str.length() == 0) {
			throw new NumberFormatException("Width string cannot be null");
		}
		if (str.charAt(0) == '/') str = str.substring(1);
		return create(Integer.parseInt(str));
	}

	private static void ensurePrefab() {
		if (prefab == null) {
			prefab = new BitWidth[Math.min(32, Value.MAX_WIDTH)];
			prefab[0] = ONE;
			for (int i = 1; i < prefab.length; i++) {
				prefab[i] = new BitWidth(i + 1);
			}
		}
	}
}

/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.data;

import com.cburch.LogisimFX.newgui.MainFrame.AttributeTable;
import com.cburch.LogisimFX.util.JInputComponent;
import com.cburch.LogisimFX.util.StringGetter;
import com.connectina.swing.fontchooser.JFontChooser;
import javafx.beans.binding.StringBinding;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class Attributes {

	private Attributes() { }
	
	private static class ConstantGetter implements StringGetter {

		private String str;
		public ConstantGetter(String str) { this.str = str; }
		public String get() { return str; }

		@Override
		public String toString() { return get(); }

	}

	private static StringBinding getter(String s) { return new StringBinding() {
		@Override
		protected String computeValue() {
			return s;
		}
	}; }
	
	//
	// methods with display name == standard name
	//
	public static Attribute<String> forString(String name) {
		return forString(name, getter(name));
	}

	public static Attribute<?> forOption(String name, Object[] vals) {
		return forOption(name, getter(name), vals);
	}

	public static Attribute<Integer> forInteger(String name) {
		return forInteger(name, getter(name));
	}

	public static Attribute<Integer> forHexInteger(String name) {
		return forHexInteger(name, getter(name));
	}

	public static Attribute<Integer> forIntegerRange(String name, int start, int end) {
		return forIntegerRange(name, getter(name), start, end);
	}

	public static Attribute<Double> forDouble(String name) {
		return forDouble(name, getter(name));
	}

	public static Attribute<Boolean> forBoolean(String name) {
		return forBoolean(name, getter(name));
	}

	public static Attribute<Direction> forDirection(String name) {
		return forDirection(name, getter(name));
	}

	public static Attribute<BitWidth> forBitWidth(String name) {
		return forBitWidth(name, getter(name));
	}

	public static Attribute<BitWidth> forBitWidth(String name, int min, int max) {
		return forBitWidth(name, getter(name), min, max);
	}

	public static Attribute<Font> forFont(String name) {
		return forFont(name, getter(name));
	}

	public static Attribute<Location> forLocation(String name) {
		return forLocation(name, getter(name));
	}

	public static Attribute<Color> forColor(String name) {
		return forColor(name, getter(name));
	}

	//
	// methods with internationalization support
	//
	public static Attribute<String> forString(String name, StringBinding disp) {
		return new StringAttribute(name, disp);
	}

	public static <V> Attribute<V> forOption(String name, StringBinding disp, V[] vals) {
		return new OptionAttribute<V>(name, disp, vals);
	}

	public static Attribute<Integer> forInteger(String name, StringBinding disp) {
		return new IntegerAttribute(name, disp);
	}

	public static Attribute<Integer> forHexInteger(String name, StringBinding disp) {
		return new HexIntegerAttribute(name, disp);
	}

	public static Attribute<Integer> forIntegerRange(String name, StringBinding disp, int start, int end) {
		return new IntegerRangeAttribute(name, disp, start, end);
	}

	public static Attribute<Double> forDouble(String name, StringBinding disp) {
		return new DoubleAttribute(name, disp);
	}

	public static Attribute<Boolean> forBoolean(String name, StringBinding disp) {
		return new BooleanAttribute(name, disp);
	}

	public static Attribute<Direction> forDirection(String name, StringBinding disp) {
		return new DirectionAttribute(name, disp);
	}

	public static Attribute<BitWidth> forBitWidth(String name, StringBinding disp) {
		return new BitWidth.Attribute(name, disp);
	}

	public static Attribute<BitWidth> forBitWidth(String name, StringBinding disp, int min, int max) {
		return new BitWidth.Attribute(name, disp, min, max);
	}

	public static Attribute<Font> forFont(String name, StringBinding disp) {
		return new FontAttribute(name, disp);
	}

	public static Attribute<Location> forLocation(String name, StringBinding disp) {
		return new LocationAttribute(name, disp);
	}

	public static Attribute<Color> forColor(String name, StringBinding disp) {
		return new ColorAttribute(name, disp);
	}


	//Implementation

	private static class StringAttribute extends Attribute<String> {

		private StringAttribute(String name, StringBinding disp) {
			super(name, disp);
		}

		@Override
		public String parse(String value) {
			return value;
		}

	}

	private static class OptionComboRenderer<V> extends BasicComboBoxRenderer {

		Attribute<V> attr;

		OptionComboRenderer(Attribute<V> attr) {
			this.attr = attr;
		}
/*
		@Override
		public Component getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Component ret = super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
			if (ret instanceof JLabel) {
				@SuppressWarnings("unchecked")
				V val = (V) value;
				((JLabel) ret).setText(value == null ? "" : attr.toDisplayString(val));
			}
			return ret;
		}

 */

	}

	public static class OptionAttribute<V> extends Attribute<V> {

		private V[] vals;

		private OptionAttribute(String name, StringBinding disp, V[] vals) {
			super(name, disp);
			this.vals = vals;
		}

		@Override
		public String toDisplayString(V value) {
			if (value instanceof AttributeOptionInterface) {
				return ((AttributeOptionInterface) value).toDisplayString();
			} else {
				return value.toString();
			}
		}

		@Override
		public V parse(String value) {
			for (int i = 0; i < vals.length; i++) {
				if (value.equals(vals[i].toString())) {
					return vals[i];
				}
			}
			throw new NumberFormatException("value not among choices");
		}

		@Override
		public Node getCell(Object value){
			ComboBox<Object> cell = new ComboBox<>();
			cell.getItems().addAll(vals);
			cell.setValue(value);
			cell.setOnAction(event -> AttributeTable.printShit());
			return cell;

		}
/*
		@Override
		public Component getCellEditor(Object value) {
			JComboBox combo = new JComboBox(vals);
			combo.setRenderer(new OptionComboRenderer<V>(this));
			if (value == null) combo.setSelectedIndex(-1);
			else combo.setSelectedItem(value);
			return combo;
		}


 */

	}

	private static class IntegerAttribute extends Attribute<Integer> {

		private IntegerAttribute(String name, StringBinding disp) {
			super(name, disp);
		}

		@Override
		public Integer parse(String value) {
			return Integer.valueOf(value);
		}

	}

	private static class HexIntegerAttribute extends Attribute<Integer> {

		private HexIntegerAttribute(String name, StringBinding disp) {
			super(name, disp);
		}

		@Override
		public String toDisplayString(Integer value) {
			int val = value.intValue();
			return "0x" + Integer.toHexString(val);
		}

		@Override
		public String toStandardString(Integer value) {
			return toDisplayString(value);
		}

		@Override
		public Integer parse(String value) {
			value = value.toLowerCase();
			if (value.startsWith("0x")) {
				value = value.substring(2);
				return Integer.valueOf((int) Long.parseLong(value, 16));
			} else if (value.startsWith("0b")) {
				value = value.substring(2);
				return Integer.valueOf((int) Long.parseLong(value, 2));
			} else if (value.startsWith("0")) {
				value = value.substring(1);
				return Integer.valueOf((int) Long.parseLong(value, 8));
			} else {
				return Integer.valueOf((int) Long.parseLong(value, 10));
			}

		}

	}

	private static class DoubleAttribute extends Attribute<Double> {

		private DoubleAttribute(String name, StringBinding disp) {
			super(name, disp);
		}

		@Override
		public Double parse(String value) {
			return Double.valueOf(value);
		}

	}

	private static class BooleanAttribute extends OptionAttribute<Boolean> {

		private static Boolean[] vals = { Boolean.TRUE, Boolean.FALSE };

		private BooleanAttribute(String name, StringBinding disp) {
			super(name, disp, vals);
		}

		@Override
		public String toDisplayString(Boolean value) {
			if (value.booleanValue()) return LC.get("booleanTrueOption");
			else return LC.get("booleanFalseOption");
		}

		@Override
		public Boolean parse(String value) {
			Boolean b = Boolean.valueOf(value);
			return vals[b.booleanValue() ? 0 : 1];
		}

	}

	private static class IntegerRangeAttribute extends Attribute<Integer> {

		Integer[] options = null;
		int start;
		int end;
		private IntegerRangeAttribute(String name, StringBinding disp, int start, int end) {
			super(name, disp);
			this.start = start;
			this.end = end;
		}

		@Override
		public Integer parse(String value) {
			int v = (int) Long.parseLong(value);
			if (v < start) throw new NumberFormatException("integer too small");
			if (v > end) throw new NumberFormatException("integer too large");
			return Integer.valueOf(v);
		}

		//Todo
		@Override
		public Node getCell(Integer value) {
			return super.getCell(value);
		}

		/*
		@Override
		public Component getCellEditor(Integer value) {
			if (end - start + 1 > 32) {
				return super.getCellEditor(value);
			} else {
				if (options == null) {
					options = new Integer[end - start + 1];
					for (int i = start; i <= end; i++) {
						options[i - start] = Integer.valueOf(i);
					}
				}
				JComboBox combo = new JComboBox(options);
				if (value == null) combo.setSelectedIndex(-1);
				else combo.setSelectedItem(value);
				return combo;
			}
		}

 */

	}

	private static class DirectionAttribute extends OptionAttribute<Direction> {

		private static Direction[] vals = {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.EAST,
			Direction.WEST,
		};

		public DirectionAttribute(String name, StringBinding disp) {
			super(name, disp, vals);
		}

		@Override
		public String toDisplayString(Direction value) {
			return value == null ? "???" : value.toDisplayString();
		}

		@Override
		public Direction parse(String value) {
			return Direction.parse(value);
		}

	}

	private static class FontAttribute extends Attribute<Font> {

		private FontAttribute(String name, StringBinding disp) {
			super(name, disp);
		}

		@Override
		public String toDisplayString(Font f) {
			if (f == null) return "???";
			return f.getFamily()
				+ " " + f.getStyle()
				+ " " + f.getSize();
		}

		@Override
		public String toStandardString(Font f) {
			return f.getFamily()
				+ " " + f.getStyle()
				+ " " + f.getSize();
		}

		@Override
		public Font parse(String value) {

			System.out.println(value);
			String[] data = value.split(" ");

			FontPosture fp = FontPosture.REGULAR;
			FontWeight fw = FontWeight.NORMAL;

			if(data[1].equals("plain")){
				fp = FontPosture.REGULAR;
			}else if(data[1].equals("italic")){
				fp = FontPosture.ITALIC;
			}else if(data[1].equals("bold")){
				fw = FontWeight.BOLD;
				if(data.length == 4 && data[2].equals("italic")){
					fp = FontPosture.ITALIC;
				}

			}

			return Font.font(data[0], fw, fp, Double.parseDouble(data[data.length-1]));

		}

		public Node getCell(Font value){
			return new TextField();
		}
/*
		@Override
		public Component getCellEditor(Font value) {
			return new FontChooser(value);
		}

 */

	}

	private static class FontChooser {

		//FontChooser(Font initial) {
		//	super(initial);
		//}

		//public Object getValue() {
		//	return getSelectedFont();
		//}

		//public void setValue(Object value) {
		//	setSelectedFont((Font) value);
		//}

	}

	private static class LocationAttribute extends Attribute<Location> {

		public LocationAttribute(String name, StringBinding desc) {
			super(name, desc);
		}

		@Override
		public Location parse(String value) {
			return Location.parse(value);
		}

	}

	private static class ColorAttribute extends Attribute<Color> {

		public ColorAttribute(String name, StringBinding desc) {
			super(name, desc);
		}

		@Override
		public String toDisplayString(Color value) {
			return toStandardString(value);
		}

		@Override
		public String toStandardString(Color c) {
			String ret = "#" + hex(c.getRed()) + hex(c.getGreen()) + hex(c.getBlue());
			return c.getOpacity() == 255 ? ret : ret + hex(c.getOpacity());
		}

		private String hex(double value) {
			if (value >= 16) return Double.toHexString(value);
			else return "0" + Double.toHexString(value);
		}

		@Override
		public Color parse(String value) {
			if (value.length() == 9) {
				int r = Integer.parseInt(value.substring(1, 3), 16);
				int g = Integer.parseInt(value.substring(3, 5), 16);
				int b = Integer.parseInt(value.substring(5, 7), 16);
				int a = Integer.parseInt(value.substring(7, 9), 16);
				return new Color(r, g, b, a);
			} else {
				return Color.valueOf(value);
				//return Color.decode(value);
			}
		}

		@Override
		public ComboBoxBase<Color> getCell(Color value) {
			Color init = value == null ? Color.BLACK : value;
			return new ColorPicker(init);
		}

	}
	
	private static class ColorChooser extends ColorPicker {

		ColorChooser(Color initial) {
			if (initial != null) setValue(initial);
			//setOpacityVisible(true);
		}

		//public Color getValue() {
		//	return this.getValue();
		//}

		//public void setValue(Object value) {
		//	setColor((Color) value);
		//}

	}

}

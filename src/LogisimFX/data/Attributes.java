/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.data;

import LogisimFX.newgui.DialogManager;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttrTableSetException;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;

import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

public class Attributes {

	private Attributes() { }

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

	public static Attribute<Boolean> forFPGASupported(String name, StringBinding disp) {
		return new NoSaveBooleanAttribute(name, disp);
	}

	public static Attribute<Integer> integerForNoSave() {
		return new NoSaveIntegerAttribute();
	}

	public static Attribute<Integer> booleanForNoSave() {
		return new NoSaveIntegerAttribute();
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
				if(value != null)
				return value.toString();
				else return "";
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

			StringConverter<Object> converter = new StringConverter<Object>() {

				@Override
				public String toString(Object object) {
					return toDisplayString((V)object);
				}

				@Override
				public Object fromString(String string) {
					return parse(string);
				}

			};

			ComboBox<Object> cell = new ComboBox<>();
			cell.setMaxWidth(Double.MAX_VALUE);
			cell.setConverter(converter);
			cell.getItems().addAll(vals);
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
			if(value == null) return LC.get("booleanFalseOption");
			if (value.booleanValue()) return LC.get("booleanTrueOption");
			else return LC.get("booleanFalseOption");
		}

		@Override
		public Boolean parse(String value) {
			Boolean b = false;
			if(value.equals(LC.get("booleanTrueOption")) || value.equals(LC.get("booleanFalseOption"))){
				b = value.equals(LC.get("booleanTrueOption"));
			}else {
				b = Boolean.valueOf(value);
			}
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
			int v = Integer.parseInt(value);
			if (v < start) throw new NumberFormatException("integer too small");
			if (v > end) throw new NumberFormatException("integer too large");
			return v;
		}

		@Override
		public String toDisplayString(Integer value) {
			return value.toString();
		}

		@Override
		public Node getCell(Integer value) {

			if (end - start + 1 > 32) {

				TextField field = new TextField();

				field.setText(toDisplayString(value));

				field.setOnAction(event -> {
					try {
						AttributeTable.setValueRequested( this, parse(field.getText()));
					} catch (AttrTableSetException e) {
						e.printStackTrace();
					}
				});

				return field;

			} else {
				if (options == null) {
					options = new Integer[end - start + 1];
					for (int i = start; i <= end; i++) {
						options[i - start] = i;
					}
				}
				ComboBox<Integer> combo = new ComboBox<>();
				combo.getItems().addAll(options);
				if (value == null) combo.setValue(options[options.length-1]);
				else combo.setValue(value);
				combo.setOnAction(event -> {
					try {
						AttributeTable.setValueRequested( this, combo.getValue());
					} catch (AttrTableSetException e) {
						e.printStackTrace();
					}
				});
				return combo;
			}

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

			Button fontSelector = new Button(value.getName());
			fontSelector.setMaxWidth(Double.MAX_VALUE);
			fontSelector.setOnAction(event -> {

				Font f = DialogManager.CreateFontSelectorDialog(value);

				try {
					AttributeTable.setValueRequested( this, f);
				} catch (AttrTableSetException e) {
					e.printStackTrace();
				}

			});

			return fontSelector;
		}

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
			//String ret = "#" + hex(c.getRed()) + hex(c.getGreen()) + hex(c.getBlue());
			String ret = "#" + c.toString().substring(2);
			//return c.getOpacity() == 255 ? ret : ret + hex(c.getOpacity());
			return ret;
		}

		private String hex(double value) {
			if (value >= 16) return Double.toHexString(value);
			else return "0" + Double.toHexString(value);
		}

		@Override
		public Color parse(String value) {
			if (value.length() == 9) {
				double r = Integer.parseInt(value.substring(1, 3), 16)/255;
				double g = Integer.parseInt(value.substring(3, 5), 16)/255;
				double b = Integer.parseInt(value.substring(5, 7), 16)/255;
				double a = Integer.parseInt(value.substring(7, 9), 16)/255;
				return new Color(r, g, b, a);
			} else {
				return Color.valueOf(value);
			}
		}

		@Override
		public ComboBoxBase<Color> getCell(Color value) {

			Color init = value == null ? Color.BLACK : value;

			ColorPicker picker = new ColorPicker(init);
			picker.setMaxWidth(Double.MAX_VALUE);
			picker.setOnAction(event -> {
				Color c = picker.getValue();
				try {
					AttributeTable.setValueRequested( this,c);
				} catch (AttrTableSetException e) {
					e.printStackTrace();
				}
			});
			return picker;
		}

	}

	private static class NoSaveIntegerAttribute extends Attribute<Integer> {

		@Override
		public Integer parse(String value) {
			return Integer.valueOf(value);
		}

		@Override
		public boolean isToSave() {
			return false;
		}

	}

	private static class NoSaveBooleanAttribute extends Attribute<Boolean> {

		private NoSaveBooleanAttribute(String name, StringBinding disp) {
			super(name, disp);
		}

		@Override
		public boolean isToSave() {
			return false;
		}

		@Override
		public String toDisplayString(Boolean value) {
			if (value == null) return LC.get("FPGASupportUndef");
			if (value){
				return LC.get("FPGASupportYes");
			} else {
				return LC.get("FPGASupportNo");
			}
		}

		@Override
		public Boolean parse(String value) {
			return Boolean.parseBoolean(value);
		}

		@Override
		public Node getCell(Boolean value){

			Label cell = new Label();
			cell.setText(toDisplayString(value));
			cell.setMaxWidth(Double.MAX_VALUE);
			if (value == null){
				cell.setStyle("-fx-background-color: gray ;");
			} else if (value){
				cell.setStyle("-fx-background-color: green ;");
			} else {
				cell.setStyle("-fx-background-color: red ;");
			}

			return cell;

		}

	}

}

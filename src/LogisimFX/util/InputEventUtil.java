/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.util;

import java.awt.Event;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;

public class InputEventUtil {
	public static String CTRL    = "Ctrl";
	public static String SHIFT   = "Shift";
	public static String ALT     = "Alt";
	public static String BUTTON1 = "Button1";
	public static String BUTTON2 = "Button2";
	public static String BUTTON3 = "Button3";

	private InputEventUtil() { }

	public static int fromString(String str) {
		int ret = 0;
		StringTokenizer toks = new StringTokenizer(str);
		while (toks.hasMoreTokens()) {
			String s = toks.nextToken();
			if (s.equals(CTRL))         ret |= InputEvent.CTRL_DOWN_MASK;
			else if (s.equals(SHIFT))   ret |= InputEvent.SHIFT_DOWN_MASK;
			else if (s.equals(ALT))     ret |= InputEvent.ALT_DOWN_MASK;
			else if (s.equals(BUTTON1)) ret |= InputEvent.BUTTON1_DOWN_MASK;
			else if (s.equals(BUTTON2)) ret |= InputEvent.BUTTON2_DOWN_MASK;
			else if (s.equals(BUTTON3)) ret |= InputEvent.BUTTON3_DOWN_MASK;
			else throw new NumberFormatException("InputEventUtil");
		}
		return ret;
	}

	public static String toString(int mods) {
		ArrayList<String> arr = new ArrayList<String>();
		if ((mods & InputEvent.CTRL_DOWN_MASK)    != 0) arr.add(CTRL);
		if ((mods & InputEvent.ALT_DOWN_MASK)     != 0) arr.add(ALT);
		if ((mods & InputEvent.SHIFT_DOWN_MASK)   != 0) arr.add(SHIFT);
		if ((mods & InputEvent.BUTTON1_DOWN_MASK) != 0) arr.add(BUTTON1);
		if ((mods & InputEvent.BUTTON2_DOWN_MASK) != 0) arr.add(BUTTON2);
		if ((mods & InputEvent.BUTTON3_DOWN_MASK) != 0) arr.add(BUTTON3);

		Iterator<String> it = arr.iterator();
		if (it.hasNext()) {
			StringBuilder ret = new StringBuilder();
			ret.append(it.next());
			while (it.hasNext()) {
				ret.append(" ");
				ret.append(it.next());
			}
			return ret.toString();
		} else {
			return "";
		}
	}

	public static int fromDisplayString(String str) {
		int ret = 0;
		StringTokenizer toks = new StringTokenizer(str);
		while (toks.hasMoreTokens()) {
			String s = toks.nextToken();
			if (s.equals(LC.get("ctrlMod")))           ret |= InputEvent.CTRL_DOWN_MASK;
			else if (s.equals(LC.get("altMod")))       ret |= InputEvent.ALT_DOWN_MASK;
			else if (s.equals(LC.get("shiftMod")))     ret |= InputEvent.SHIFT_DOWN_MASK;
			else if (s.equals(LC.get("button1Mod")))   ret |= InputEvent.BUTTON1_DOWN_MASK;
			else if (s.equals(LC.get("button2Mod")))   ret |= InputEvent.BUTTON2_DOWN_MASK;
			else if (s.equals(LC.get("button3Mod")))   ret |= InputEvent.BUTTON3_DOWN_MASK;
			else throw new NumberFormatException("InputEventUtil");
		}
		return ret;
	}

	public static String toDisplayString(int mods) {
		ArrayList<String> arr = new ArrayList<String>();
		if ((mods & InputEvent.CTRL_DOWN_MASK)    != 0) arr.add(LC.get("ctrlMod"));
		if ((mods & InputEvent.ALT_DOWN_MASK)     != 0) arr.add(LC.get("altMod"));
		if ((mods & InputEvent.SHIFT_DOWN_MASK)   != 0) arr.add(LC.get("shiftMod"));
		if ((mods & InputEvent.BUTTON1_DOWN_MASK) != 0) arr.add(LC.get("button1Mod"));
		if ((mods & InputEvent.BUTTON2_DOWN_MASK) != 0) arr.add(LC.get("button2Mod"));
		if ((mods & InputEvent.BUTTON3_DOWN_MASK) != 0) arr.add(LC.get("button3Mod"));

		if (arr.isEmpty()) return "";

		Iterator<String> it = arr.iterator();
		if (it.hasNext()) {
			StringBuilder ret = new StringBuilder();
			ret.append(it.next());
			while (it.hasNext()) {
				ret.append(" ");
				ret.append(it.next());
			}
			return ret.toString();
		} else {
			return "";
		}
	}

	public static String toKeyDisplayString(int mods) {
		ArrayList<String> arr = new ArrayList<String>();
		if ((mods & Event.META_MASK)  != 0) arr.add(LC.get("metaMod"));
		if ((mods & Event.CTRL_MASK)  != 0) arr.add(LC.get("ctrlMod"));
		if ((mods & Event.ALT_MASK)   != 0) arr.add(LC.get("altMod"));
		if ((mods & Event.SHIFT_MASK) != 0) arr.add(LC.get("shiftMod"));

		Iterator<String> it = arr.iterator();
		if (it.hasNext()) {
			StringBuilder ret = new StringBuilder();
			ret.append(it.next());
			while (it.hasNext()) {
				ret.append(" ");
				ret.append(it.next());
			}
			return ret.toString();
		} else {
			return "";
		}
	}
}

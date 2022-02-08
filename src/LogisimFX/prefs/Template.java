/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.prefs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Template {

	public static Template createEmpty() {

		String circName = LC.get("newCircuitName");
		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buf.append("<project version=\"1.0\">");
		buf.append(" <circuit name=\"" + circName + "\" />");
		buf.append("</project>");

		return new Template(buf.toString());

	}
	
	public static Template create(InputStream in) {

		InputStreamReader reader = new InputStreamReader(in);
		char[] buf = new char[4096];
		StringBuilder dest = new StringBuilder();

		while (true) {
			try {
				int nbytes = reader.read(buf);
				if (nbytes < 0) break;
				dest.append(buf, 0, nbytes);
			} catch (IOException e) {
				break;
			}
		}

		return new Template(dest.toString());

	}
	
	private String contents;
	
	private Template(String contents) {
		this.contents = contents;
	}
	
	public InputStream createStream() {

		try {
			return new ByteArrayInputStream(contents.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("warning: UTF-8 is not supported"); //OK
			return new ByteArrayInputStream(contents.getBytes());
		}

	}

}

/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.newgui.AnalyzeFrame;

import javafx.beans.binding.StringBinding;

public  class ParserException extends Exception {

	private StringBinding message;
	private int start;
	private int length;
	
	public ParserException(StringBinding message, int start, int length) {
		super(message.getValue());
		this.message = message;
		this.start = start;
		this.length = length;
	}
	
	@Override
	public String getMessage() {
		return message.get();
	}
	
	public StringBinding getMessageGetter() {
		return message;
	}
	
	public int getOffset() {
		return start;
	}
	
	public int getEndOffset() {
		return start + length;
	}

}
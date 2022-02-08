/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.AnalyzeFrame;

import javafx.beans.binding.StringBinding;

public class Entry {

	public static final Entry ZERO = new Entry("0");
	public static final Entry ONE = new Entry("1");
	public static final Entry DONT_CARE = new Entry("x");
	public static final Entry BUS_ERROR = new Entry(LC.createStringBinding("busError"));
	public static final Entry OSCILLATE_ERROR = new Entry(LC.createStringBinding("oscillateError"));
	
	public static Entry parse(String description) {
		if (ZERO.description.equals(description)) return ZERO;
		if (ONE.description.equals(description)) return ONE;
		if (DONT_CARE.description.equals(description)) return DONT_CARE;
		if (BUS_ERROR.description.equals(description)) return BUS_ERROR;
		return null;
	}
	
	private String description;
	private StringBinding errorMessage;
	
	private Entry(String description) {
		this.description = description;
		this.errorMessage = null;
	}
	
	private Entry(StringBinding errorMessage) {
		this.description = "!!";
		this.errorMessage = errorMessage;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isError() {
		return errorMessage != null;
	}
	
	public String getErrorMessage() {
		return errorMessage == null ? null : errorMessage.get();
	}
	
	@Override
	public String toString() {
		return "Entry[" + description + "]";
	}

}

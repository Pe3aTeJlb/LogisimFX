/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import java.util.Collections;
import java.util.List;

class XmlReaderException extends Exception {
	private List<String> messages;
	
	public XmlReaderException(String message) {
		this(Collections.singletonList(message));
	}
	
	public XmlReaderException(List<String> messages) {
		this.messages = messages;
	}
	
	@Override
	public String getMessage() {
		return messages.get(0);
	}
	
	public List<String> getMessages() {
		return messages;
	}
}

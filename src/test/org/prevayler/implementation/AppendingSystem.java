package org.prevayler.implementation;

import java.io.Serializable;


class AppendingSystem implements Serializable {
	
	private String value = "";
	
	String value() {
		return value;
	}
	
	void append(String appendix) {
		value = value + appendix;
		if (appendix.equals("rollback")) throw new RuntimeException("Testing Rollback");
	}
	
}
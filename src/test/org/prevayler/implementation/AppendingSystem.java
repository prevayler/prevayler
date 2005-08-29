//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.Serializable;


class AppendingSystem implements Serializable {

	private static final long serialVersionUID = -1151588644550257284L;
	private String value = "";
	
	String value() {
		return value;
	}
	
	void append(String appendix) {
		value = value + appendix;
		if (appendix.equals("rollback")) throw new RuntimeException("Testing Rollback");
	}
	
}
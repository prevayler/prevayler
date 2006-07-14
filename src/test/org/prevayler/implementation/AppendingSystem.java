// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import java.io.Serializable;

class AppendingSystem implements Serializable {

    private static final long serialVersionUID = -1151588644550257284L;

    private String value = "";

    public String value() {
        return value;
    }

    public void append(String appendix) {
        value = value + appendix;
        if (appendix.equals("rollback"))
            throw new RuntimeException("Testing Rollback");
    }

}

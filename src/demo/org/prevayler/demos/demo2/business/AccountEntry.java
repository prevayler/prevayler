// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.business;

import java.io.Serializable;
import java.util.Date;

public class AccountEntry implements Serializable {

    private static final long serialVersionUID = 8394742481391868028L;

    private long amount;

    private Date timestamp;

    private AccountEntry() {
    }

    AccountEntry(long amount, Date timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String toString() {
        return timestampString() + "      Amount: " + amount;
    }

    private String timestampString() {
        return new java.text.SimpleDateFormat("yyyy/MM/dd  hh:mm:ss.SSS").format(timestamp);
    }
}

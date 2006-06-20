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

import org.prevayler.Transaction;

import java.util.Date;

public class AppendTransaction implements Transaction<StringBuffer> {

    private static final long serialVersionUID = -3830205386199825379L;

    public String toAdd;

    private AppendTransaction() {
        // Skaringa requires a default constructor, but XStream does not.
    }

    public AppendTransaction(String toAdd) {
        this.toAdd = toAdd;
    }

    public void executeOn(StringBuffer prevalentSystem, Date executionTime) {
        prevalentSystem.append(toAdd);
    }

}

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

import org.prevayler.TransactionWithQuery;

import java.util.Date;

public class AppendTransactionWithQuery implements TransactionWithQuery<StringBuilder, String, RuntimeException> {

    private static final long serialVersionUID = 7725358482908916942L;

    public String toAdd;

    private AppendTransactionWithQuery() {
        // Skaringa requires a default constructor, but XStream does not.
    }

    public AppendTransactionWithQuery(String toAdd) {
        this.toAdd = toAdd;
    }

    public String executeAndQuery(StringBuilder prevalentSystem, Date executionTime) {
        prevalentSystem.append(toAdd);
        return prevalentSystem.toString();
    }

}

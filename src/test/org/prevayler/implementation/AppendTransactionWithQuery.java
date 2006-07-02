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

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;

import java.io.Serializable;

public class AppendTransactionWithQuery implements GenericTransaction<StringBuilder, String, RuntimeException>, Serializable {

    private static final long serialVersionUID = 7725358482908916942L;

    public String toAdd;

    private AppendTransactionWithQuery() {
        // Skaringa requires a default constructor, but XStream does not.
    }

    public AppendTransactionWithQuery(String toAdd) {
        this.toAdd = toAdd;
    }

    public String executeOn(StringBuilder prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        prevalentSystem.append(toAdd);
        return prevalentSystem.toString();
    }

}

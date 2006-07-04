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

import static org.prevayler.Safety.Level.LEVEL_4_JOURNALING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

import java.io.Serializable;

@Safety(LEVEL_4_JOURNALING) public class AppendTransaction implements GenericTransaction<StringBuilder, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = -3830205386199825379L;

    public String toAdd;

    private AppendTransaction() {
        // Skaringa requires a default constructor, but XStream does not.
    }

    public AppendTransaction(String toAdd) {
        this.toAdd = toAdd;
    }

    public Void executeOn(StringBuilder prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        prevalentSystem.append(toAdd);
        return null;
    }

}

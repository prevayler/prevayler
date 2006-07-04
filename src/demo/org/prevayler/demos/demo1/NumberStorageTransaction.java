// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo1;

import static org.prevayler.Safety.Level.LEVEL_4_JOURNALING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

import java.io.Serializable;

/**
 * To change the state of the business objects, the client code must use a
 * Transaction like this one.
 */
@Safety(LEVEL_4_JOURNALING) class NumberStorageTransaction implements GenericTransaction<NumberKeeper, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = -2023934810496653301L;

    private int _numberToKeep;

    // Necessary for Skaringa XML serialization
    private NumberStorageTransaction() {
    }

    NumberStorageTransaction(int numberToKeep) {
        _numberToKeep = numberToKeep;
    }

    public Void executeOn(NumberKeeper prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        prevalentSystem.keep(_numberToKeep);
        return null;
    }

}

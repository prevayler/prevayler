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

import org.prevayler.Transaction;

import java.util.Date;

/**
 * To change the state of the business objects, the client code must use a
 * Transaction like this one.
 */
class NumberStorageTransaction implements Transaction {

    private static final long serialVersionUID = -2023934810496653301L;

    private int _numberToKeep;

    private NumberStorageTransaction() {
    } // Necessary for Skaringa XML serialization

    NumberStorageTransaction(int numberToKeep) {
        _numberToKeep = numberToKeep;
    }

    public void executeOn(Object prevalentSystem, Date ignored) {
        ((NumberKeeper) prevalentSystem).keep(_numberToKeep);
    }
}

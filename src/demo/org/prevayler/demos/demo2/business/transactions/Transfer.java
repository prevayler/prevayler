// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.demos.ReadWrite;
import org.prevayler.demos.demo2.business.Bank;

import java.io.Serializable;

@ReadWrite public class Transfer implements GenericTransaction<Bank, Void, Exception>, Serializable {

    private static final long serialVersionUID = -8656365915179217238L;

    private String _originAccountNumber;

    private String _destinationAccountNumber;

    private long _amount;

    // Necessary for Skaringa XML serialization
    private Transfer() {
    }

    public Transfer(String originAccountNumber, String destinationAccountNumber, long amount) {
        _originAccountNumber = originAccountNumber;
        _destinationAccountNumber = destinationAccountNumber;
        _amount = amount;
    }

    public Void executeOn(PrevalenceContext<? extends Bank> prevalenceContext) throws Exception {
        Bank bank = prevalenceContext.prevalentSystem();
        bank.transfer(_originAccountNumber, _destinationAccountNumber, _amount, prevalenceContext);
        return null;
    }

}

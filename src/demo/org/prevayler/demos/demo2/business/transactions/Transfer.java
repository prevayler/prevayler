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

import org.prevayler.Transaction;
import org.prevayler.demos.demo2.business.Bank;

import java.io.Serializable;
import java.util.Date;

public class Transfer implements Transaction<Bank, Void, Exception>, Serializable {

    private static final long serialVersionUID = -8656365915179217238L;

    private long _originAccountNumber;

    private long _destinationAccountNumber;

    private long _amount;

    // Necessary for Skaringa XML serialization
    private Transfer() {
    }

    public Transfer(long originAccountNumber, long destinationAccountNumber, long amount) {
        _originAccountNumber = originAccountNumber;
        _destinationAccountNumber = destinationAccountNumber;
        _amount = amount;
    }

    public Void executeOn(Bank bank, Date timestamp) throws Exception {
        bank.transfer(_originAccountNumber, _destinationAccountNumber, _amount, timestamp);
        return null;
    }

}

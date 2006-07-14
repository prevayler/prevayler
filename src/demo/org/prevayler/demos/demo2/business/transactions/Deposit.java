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

import org.prevayler.PrevalenceContext;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.Bank;

public class Deposit extends AccountTransaction {

    private static final long serialVersionUID = 991958966450346984L;

    private long _amount;

    // Necessary for Skaringa XML serialization
    private Deposit() {
    }

    public Deposit(String accountNumber, long amount) {
        super(accountNumber);
        _amount = amount;
    }

    @Override public void executeOn(Account account, PrevalenceContext<? extends Bank> prevalenceContext) throws Account.InvalidAmount {
        account.deposit(_amount, prevalenceContext);
    }

}

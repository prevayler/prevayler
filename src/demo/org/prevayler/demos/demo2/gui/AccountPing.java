// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.gui;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.ReadOnly;
import org.prevayler.demos.demo2.business.Bank;

@ReadOnly public class AccountPing implements GenericTransaction<Bank, Void, Bank.AccountNotFound> {

    private final String accountNumber;

    public AccountPing(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Void executeOn(Bank prevalentSystem, PrevalenceContext prevalenceContext) throws Bank.AccountNotFound {
        prevalenceContext.trigger(new AccountEvent(prevalentSystem.findAccount(accountNumber)));
        return null;
    }

}

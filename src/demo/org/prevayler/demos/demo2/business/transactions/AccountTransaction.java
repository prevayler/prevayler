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

import static org.prevayler.Safety.Level.LEVEL_4_JOURNALING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.Bank;

import java.io.Serializable;

@Safety(LEVEL_4_JOURNALING) abstract class AccountTransaction implements GenericTransaction<Bank, Void, Exception>, Serializable {

    private String _accountNumber;

    // Necessary for Skaringa XML serialization. This would normally be
    // private, but must be package visible (or protected) in this case in
    // order for subclasses to supply private default constructors and
    // actually compile.
    AccountTransaction() {
    }

    protected AccountTransaction(String accountNumber) {
        _accountNumber = accountNumber;
    }

    public Void executeOn(Bank bank, PrevalenceContext prevalenceContext) throws Exception {
        executeAndQuery(bank.findAccount(_accountNumber), prevalenceContext);
        return null;
    }

    protected abstract void executeAndQuery(Account account, PrevalenceContext prevalenceContext) throws Exception;

}

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

import org.prevayler.TransactionWithQuery;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.Bank;

import java.util.Date;

public class AccountDeletion implements TransactionWithQuery<Bank, Void, Bank.AccountNotFound> {

    private static final long serialVersionUID = -3401288850388764433L;

    private long _accountNumber;

    private AccountDeletion() {
    } // Necessary for Skaringa XML serialization

    public AccountDeletion(Account account) {
        _accountNumber = account.number();
    }

    public Void executeAndQuery(Bank bank, Date ignored) throws Bank.AccountNotFound {
        bank.deleteAccount(_accountNumber);
        return null;
    }

}

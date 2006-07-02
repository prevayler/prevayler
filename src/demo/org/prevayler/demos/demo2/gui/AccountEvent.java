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

import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.AccountEntry;

import java.util.List;

public class AccountEvent extends BankEvent {

    private final String number;

    private final String holder;

    private final AccountEntry[] history;

    private final String balance;

    public AccountEvent(Account account) {
        this.number = account.numberString();
        this.holder = account.holder();
        List<AccountEntry> transactionHistory = account.transactionHistory();
        this.history = transactionHistory.toArray(new AccountEntry[transactionHistory.size()]);
        this.balance = String.valueOf(account.balance());
    }

    public String getHolder() {
        return holder;
    }

    public String getNumber() {
        return number;
    }

    public AccountEntry[] getHistory() {
        return history;
    }

    public String getBalance() {
        return balance;
    }

}

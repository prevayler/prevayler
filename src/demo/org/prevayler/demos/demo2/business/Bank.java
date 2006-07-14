// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.business;

import org.prevayler.PrevalenceContext;
import org.prevayler.demos.demo2.gui.BankEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank implements java.io.Serializable {

    private static final long serialVersionUID = 9202866398345361997L;

    private long nextAccountNumber = 1;

    private Map<Long, Account> accountsByNumber = new HashMap<Long, Account>();

    public Account createAccount(String holder, PrevalenceContext<? extends Bank> prevalenceContext) throws Account.InvalidHolder {
        Account account = new Account(nextAccountNumber, holder, prevalenceContext);
        accountsByNumber.put(nextAccountNumber++, account);
        prevalenceContext.addEvent(new BankEvent());
        return account;
    }

    public void deleteAccount(String number, PrevalenceContext<? extends Bank> prevalenceContext) {
        accountsByNumber.remove(new Long(number));
        prevalenceContext.addEvent(new BankEvent());
    }

    public List<Account> accounts() {
        List<Account> accounts = new ArrayList<Account>(accountsByNumber.values());

        Collections.sort(accounts, new Comparator<Account>() {
            public int compare(Account acc1, Account acc2) {
                return acc1.number() < acc2.number() ? -1 : 1;
            }
        });

        return accounts;
    }

    public Account findAccount(String accountNumber) throws AccountNotFound {
        return findAccount(Long.parseLong(accountNumber));
    }

    private Account findAccount(long number) throws AccountNotFound {
        Account account = searchAccount(number);
        if (account == null)
            throw new AccountNotFound(number);
        return account;
    }

    public void transfer(String sourceNumber, String destinationNumber, long amount, PrevalenceContext<? extends Bank> prevalenceContext) throws AccountNotFound, Account.InvalidAmount {
        Account source = findAccount(sourceNumber);
        Account destination = findAccount(destinationNumber);

        source.withdraw(amount, prevalenceContext);
        if (amount == 666)
            throw new RuntimeException("Runtime Exception simulated for rollback demonstration purposes.");
        destination.deposit(amount, prevalenceContext);
    }

    private Account searchAccount(long number) {
        return accountsByNumber.get(new Long(number));
    }

    public class AccountNotFound extends Exception {
        private static final long serialVersionUID = 5107181766636463559L;

        AccountNotFound(long number) {
            super("Account not found: " + Account.numberString(number) + ".\nMight have been deleted.");
        }
    }

}

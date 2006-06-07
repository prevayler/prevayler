package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.Account;

import java.util.Date;

public class Deposit extends AccountTransaction {

    private static final long serialVersionUID = 991958966450346984L;

    private long _amount;

    private Deposit() {
    } // Necessary for Skaringa XML serialization

    public Deposit(Account account, long amount) {
        super(account);
        _amount = amount;
    }

    public void executeAndQuery(Account account, Date timestamp) throws Account.InvalidAmount {
        account.deposit(_amount, timestamp);
    }
}

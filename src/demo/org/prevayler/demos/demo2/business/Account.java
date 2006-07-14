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
import org.prevayler.demos.demo2.gui.AccountEvent;

import java.util.ArrayList;
import java.util.List;

public class Account implements java.io.Serializable {

    private static final long serialVersionUID = 3998522662411373397L;

    private long number;

    private String holder;

    private long balance = 0;

    private List<AccountEntry> transactionHistory = new ArrayList<AccountEntry>();

    private Account() {
    }

    Account(long number, String holder, PrevalenceContext<? extends Bank> prevalenceContext) throws InvalidHolder {
        this.number = number;
        setHolder(holder, prevalenceContext);
    }

    long number() {
        return number;
    }

    @Override public String toString() {
        // Returns something like "00123 - John Smith"
        return numberString() + " - " + holder;
    }

    public String numberString() {
        return numberString(number);
    }

    static String numberString(long number) {
        return (new java.text.DecimalFormat("00000").format(number));
    }

    public String holder() {
        return holder;
    }

    public void setHolder(String holder, PrevalenceContext<? extends Bank> prevalenceContext) throws InvalidHolder {
        verify(holder);
        this.holder = holder;
        triggerEvent(prevalenceContext);
    }

    public long balance() {
        return balance;
    }

    public void deposit(long amount, PrevalenceContext<? extends Bank> prevalenceContext) throws InvalidAmount {
        verify(amount);
        register(amount, prevalenceContext);
    }

    public void withdraw(long amount, PrevalenceContext<? extends Bank> prevalenceContext) throws InvalidAmount {
        verify(amount);
        register(-amount, prevalenceContext);
    }

    private void register(long amount, PrevalenceContext<? extends Bank> prevalenceContext) {
        balance += amount;
        transactionHistory.add(new AccountEntry(amount, prevalenceContext.executionTime()));
        triggerEvent(prevalenceContext);
    }

    private void verify(long amount) throws InvalidAmount {
        if (amount <= 0)
            throw new InvalidAmount("Amount must be greater than zero.");
        if (amount > 10000)
            throw new InvalidAmount("Amount maximum (10000) exceeded.");
    }

    public List<AccountEntry> transactionHistory() {
        return transactionHistory;
    }

    private void triggerEvent(PrevalenceContext<? extends Bank> prevalenceContext) {
        prevalenceContext.addEvent(new AccountEvent(this));
    }

    public class InvalidAmount extends Exception {
        private static final long serialVersionUID = 3343517565045905857L;

        InvalidAmount(String message) {
            super(message);
        }
    }

    private static void verify(String newHolder) throws InvalidHolder {
        if (newHolder == null || newHolder.equals("")) {
            throw new InvalidHolder();
        }
    }

    public static class InvalidHolder extends Exception {
        private static final long serialVersionUID = -3234126892127577122L;

        InvalidHolder() {
            super("Invalid holder name.");
        }
    }
}

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

@ReadWrite public class AccountDeletion implements GenericTransaction<Bank, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = -3401288850388764433L;

    private String _accountNumber;

    private AccountDeletion() {
    } // Necessary for Skaringa XML serialization

    public AccountDeletion(String accountNumber) {
        _accountNumber = accountNumber;
    }

    public Void executeOn(Bank bank, PrevalenceContext prevalenceContext) {
        bank.deleteAccount(_accountNumber, prevalenceContext);
        return null;
    }

}

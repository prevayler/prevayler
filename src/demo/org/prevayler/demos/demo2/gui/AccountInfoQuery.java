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
import org.prevayler.demos.ReadOnly;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.Bank;

import java.util.List;

@ReadOnly public class AccountInfoQuery implements GenericTransaction<Bank, AccountInfo[], RuntimeException> {

    public AccountInfo[] executeOn(PrevalenceContext<? extends Bank> prevalenceContext) throws RuntimeException {
        List<Account> accounts = prevalenceContext.prevalentSystem().accounts();
        AccountInfo[] infos = new AccountInfo[accounts.size()];
        int index = 0;
        for (Account account : accounts) {
            infos[index] = new AccountInfo(account);
            index++;
        }
        return infos;
    }

}

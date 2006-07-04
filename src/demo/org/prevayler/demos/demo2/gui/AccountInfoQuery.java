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

import static org.prevayler.Safety.Level.LEVEL_1_SHARED_LOCKING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.Bank;

import java.util.List;

@Safety(LEVEL_1_SHARED_LOCKING) public class AccountInfoQuery implements GenericTransaction<Bank, AccountInfo[], RuntimeException> {

    public AccountInfo[] executeOn(Bank prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) throws RuntimeException {
        List<Account> accounts = prevalentSystem.accounts();
        AccountInfo[] infos = new AccountInfo[accounts.size()];
        int index = 0;
        for (Account account : accounts) {
            infos[index] = new AccountInfo(account);
            index++;
        }
        return infos;
    }

}

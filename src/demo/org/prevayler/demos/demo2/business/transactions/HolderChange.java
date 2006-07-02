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

import org.prevayler.PrevalenceContext;
import org.prevayler.demos.demo2.business.Account;

public class HolderChange extends AccountTransaction {

    private static final long serialVersionUID = -5846094202312934631L;

    private String _newHolder;

    // Necessary for Skaringa XML serialization
    private HolderChange() {
    }

    public HolderChange(String accountNumber, String newHolder) {
        super(accountNumber);
        _newHolder = newHolder;
    }

    @Override public void executeAndQuery(Account account, PrevalenceContext prevalenceContext) throws Account.InvalidHolder {
        account.setHolder(_newHolder, prevalenceContext);
    }

}

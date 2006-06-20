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

import org.prevayler.demos.demo2.business.Account;

import java.util.Date;

public class HolderChange extends AccountTransaction {

    private static final long serialVersionUID = -5846094202312934631L;

    private String _newHolder;

    // Necessary for Skaringa XML serialization
    private HolderChange() {
    }

    public HolderChange(Account account, String newHolder) {
        super(account);
        _newHolder = newHolder;
    }

    @Override public void executeAndQuery(Account account, @SuppressWarnings("unused") Date ignored) throws Account.InvalidHolder {
        account.setHolder(_newHolder);
    }
}

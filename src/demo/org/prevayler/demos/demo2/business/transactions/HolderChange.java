package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.Account;

import java.util.Date;

public class HolderChange extends AccountTransaction {

    private static final long serialVersionUID = -5846094202312934631L;

    private String _newHolder;

    private HolderChange() {
    } // Necessary for Skaringa XML serialization

    public HolderChange(Account account, String newHolder) {
        super(account);
        _newHolder = newHolder;
    }

    public void executeAndQuery(Account account, Date ignored) throws Account.InvalidHolder {
        account.holder(_newHolder);
    }
}

package org.prevayler.demo.commands;

import org.prevayler.demo.*;
import java.io.Serializable;

public class AccountCreation extends BankCommand {

    private final String holder;
    
    public AccountCreation(String holder) {
        this.holder = holder;
    }

    protected Serializable execute(Bank bank) throws Account.InvalidHolder {
        return bank.createAccount(holder);
    }
}

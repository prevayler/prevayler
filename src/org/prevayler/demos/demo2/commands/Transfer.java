package org.prevayler.demos.demo2.commands;

import org.prevayler.demos.demo2.Bank;

public class Transfer extends BankCommand {

    private final long originAccountNumber;
    private final long destinationAccountNumber;
    private final long amount;

    public Transfer(long originAccountNumber, long destinationAccountNumber, long amount) {
        this.originAccountNumber = originAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
    }

    public java.io.Serializable execute(Bank bank) throws Exception {
        bank.transfer(originAccountNumber, destinationAccountNumber, amount);
        return null;
    }
}

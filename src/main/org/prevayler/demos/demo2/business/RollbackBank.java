package org.prevayler.demos.demo2.business;

public class RollbackBank extends Bank {
    public Account createAccount(String holder) throws Account.InvalidHolder {
        Account account = super.createAccount(holder);
        maybeFail();
        return account;
    }

    public void deleteAccount(long number) throws Bank.AccountNotFound {
        super.deleteAccount(number);
        maybeFail();
    }

    public void transfer(long sourceNumber, long destinationNumber, long amount) throws Bank.AccountNotFound, Account.InvalidAmount {
        super.transfer(sourceNumber, destinationNumber, amount);
        maybeFail();
    }

    private void maybeFail() {
        if (clock().time().getTime() % 2 == 0) {
            throw new RuntimeException("Transaction failed, system rolled back.");
        }
    }
}

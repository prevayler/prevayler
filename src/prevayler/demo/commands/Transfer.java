package prevayler.demo.commands;

import prevayler.demo.Bank;

public class Transfer extends BankCommand {

    private final long originAccountNumber;
    private final long destinationAccountNumber;
    private final long ammount;

    public Transfer(long originAccountNumber, long destinationAccountNumber, long ammount) {
        this.originAccountNumber = originAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.ammount = ammount;
    }

    public java.io.Serializable execute(Bank bank) throws Exception {
        bank.transfer(originAccountNumber, destinationAccountNumber, ammount);
        return null;
    }
}

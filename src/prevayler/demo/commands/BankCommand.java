package prevayler.demo.commands;

import prevayler.*;
import prevayler.demo.Bank;
import java.io.Serializable;

public abstract class BankCommand implements Command {

	public Serializable execute(PrevalentSystem bank) throws Exception {
		return execute((Bank)bank);
	}
    
	protected abstract Serializable execute(Bank bank) throws Exception;
}
package org.prevayler.demos.demo2.commands;

import org.prevayler.*;
import org.prevayler.demos.demo2.Bank;
import java.io.Serializable;

public abstract class BankCommand implements Command {

	public Serializable execute(PrevalentSystem bank) throws Exception {
		return execute((Bank)bank);
	}
    
	protected abstract Serializable execute(Bank bank) throws Exception;
}
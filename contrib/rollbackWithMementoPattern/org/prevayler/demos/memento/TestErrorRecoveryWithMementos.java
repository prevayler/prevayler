package org.prevayler.demos.memento;

import java.io.IOException;
import org.prevayler.util.memento.MementoTransaction;
import org.prevayler.util.memento.MementoManagerCommand;
import org.prevayler.demos.memento.commands.AccountCreation;
import org.prevayler.demos.memento.commands.AccountDeletion;
import org.prevayler.demos.memento.commands.Deposit;
import org.prevayler.demos.memento.commands.Transfer;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

/**
 * A simple test of the error recovery using mementos.
 * 
 * Dialogs will pop up asking whether to fail at specific points.
 * 
 * All these tests have to be moved to JUnit tests.
 * 
 * @author Johan Stuyts
 */
public class TestErrorRecoveryWithMementos {
	
	public static void main(String[] args) {
		try{

			run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
  private static Prevayler prevayler;
  private static Bank bank;
  
  static {
    try {
      prevayler = PrevaylerFactory.createPrevayler(new Bank(), "demoMemento");
      bank = (Bank)prevayler.prevalentSystem();
    } catch (Exception e) {
      System.out.println("FAILED TO CREATE PREVAYLER!");
    }
  }
  
	public static void run() throws IOException, ClassNotFoundException {
    Account account1 = null, account2 = null;
    
    System.out.println("*** Creating account 1");
    MementoTransaction command = new AccountCreation("Owner 1");
    account1 = (Account)execute(command);
    
    System.out.println("*** Creating account 2");
    command = new AccountCreation("Owner 2");
    account2 = (Account)execute(command);
    
    System.out.println("*** Depositing 500 into account 1");
    command = new Deposit(account1, 500);
    execute(command);

    System.out.println("*** Transferring 200 from account 1 into account 2");
    command = new Transfer(account1.number(), account2.number(), 200);
    execute(command);
        
    System.out.println("*** Deleting account 1");
    command = new AccountDeletion(account1);
    execute(command);
    
    System.out.println("*** Deleting account 1");
    command = new AccountDeletion(account2);
    execute(command);
    
    prevayler.takeSnapshot();
	}
  
  private static Object execute(MementoTransaction command) {
    try {
      return new MementoManagerCommand(command).executeAndQuery(prevayler.prevalentSystem(), prevayler.clock().time());
    } catch (Exception exception) {
      System.out.println("FAILURE!");
      exception.printStackTrace(System.out);
    } finally {
      System.out.println(bank.toString());
    }
    return null;
  }
}

package prevayler.demos.rollbackdemo;

import java.io.IOException;
import java.io.Serializable;
import prevayler.MementoCommand;
import prevayler.demos.rollbackdemo.commands.AccountCreation;
import prevayler.demos.rollbackdemo.commands.AccountDeletion;
import prevayler.demos.rollbackdemo.commands.Deposit;
import prevayler.demos.rollbackdemo.rollbackcommands.Transfer;
import prevayler.implementation.SnapshotPrevayler;

/**
 * A simple test of the error recovery.
 * 
 * All these tests have to be moved to JUnit tests.
 * 
 * @author Johan Stuyts
 */
public class Main {
	
	public static void main(String[] args) {
		try{

			run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
  private static SnapshotPrevayler prevayler;
  private static Bank bank;
  
  static {
    try {
      prevayler = new SnapshotPrevayler(new Bank());
      bank = (Bank)prevayler.system();
    } catch (Exception e) {
      out("FAILED TO CREATE PREVAYLER!");
    }
  }
  
	public static void run() throws IOException, ClassNotFoundException {
    Account account1 = null, account2 = null;
    
    out("*** Creating account 1");
    MementoCommand command = new AccountCreation("Owner 1");
    account1 = (Account)execute(command);
    
    out("*** Creating account 2");
    command = new AccountCreation("Owner 2");
    account2 = (Account)execute(command);
    
    out("*** Depositing 500 into account 1");
    command = new Deposit(account1, 500);
    execute(command);

    // This method will always work now. Should a failure be possible, the system is (possibly) left in an inconsistent state.
    out("*** Transferring 200 from account 1 into account 2 (deprecated method)");
    command = new prevayler.demos.rollbackdemo.commands.Transfer(account1.number(), account2.number(), 200);
    execute(command);
        
    out("*** Transferring 200 from account 1 into account 2 (new method capable of handling errors)");
    command = new Transfer(account1.number(), account2.number(), 200);
    execute(command);
        
    out("*** Deleting account 1");
    command = new AccountDeletion(account1);
    execute(command);
    
    out("*** Deleting account 1");
    command = new AccountDeletion(account2);
    execute(command);
    
    prevayler.takeSnapshot();
	}
  
  private static Serializable execute(MementoCommand command) {
    try {
      return prevayler.executeCommand(command);
    } catch (Exception exception) {
      out("FAILURE!");
      exception.printStackTrace(System.out);
    } finally {
      out(bank.toString());
    }
    return null;
  }

  private static void out(String message) {
    System.out.println(message);
  }
  
}

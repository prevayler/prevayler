// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
// Contributions: Ramon Tramontini dos Santos

package prevayler.implementation;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import prevayler.PrevalentSystem;
import prevayler.Memento;
import prevayler.MementoCollector;
import prevayler.MementoCommand;
import prevayler.MementoPrevayler;

/** Provides transparent persistence for business objects.
* This applies to any deterministic system implementing the PrevalentSystem interface.
* All commands to the system must be represented as objects implementing the Command interface and must be executed using Prevayler.executeCommand(Command).
* Take a look at the demo application included with the Prevayler distribution for examples.
* @see PrevaylerFactory
*/
public class SnapshotPrevayler implements MementoPrevayler {

	private final PrevalentSystem system;
	private final SystemClock clock;
	private final CommandOutputStream output;
  private final List mementos = new Vector();


	/** Returns a new Prevayler for the given PrevalentSystem.
	* "PrevalenceBase" shall be the directory where the snapshot and log files shall be created and read.
	* @param newSystem The newly started, "empty" PrevalentSystem that will be used as a starting point for every system startup, until the first snapshot is taken.
	*/
	public SnapshotPrevayler(PrevalentSystem newSystem) throws IOException, ClassNotFoundException {
		this(newSystem, "PrevalenceBase");
	}


	/** Returns a new Prevayler for the given PrevalentSystem.
	* @param newSystem The newly started, "empty" PrevalentSystem that will be used as a starting point for every system startup, until the first snapshot is taken.
	* @param directory The full path of the directory where the snapshot and log files shall be created and read.
	*/
	public SnapshotPrevayler(PrevalentSystem newSystem, String directory) throws IOException, ClassNotFoundException {
		newSystem.clock(new SystemClock());
		CommandInputStream input = new CommandInputStream(directory);

		PrevalentSystem savedSystem = input.readLastSnapshot();
		system = (savedSystem == null)
		    ? newSystem
		    : savedSystem;

		recoverCommands(input);

		output = input.commandOutputStream();
		clock = (SystemClock)system.clock();
		clock.resume();
	}


	/** Returns the underlying PrevalentSystem.
	*/
	public PrevalentSystem system() {
		return system;
	}

	/** Logs the received command for crash or shutdown recovery and executes it on the underlying PrevalentSystem.
	* @see system()
	* @return The serializable object that was returned by the execution of command.
	* @throws IOException if there is trouble writing the command to the log.
	* @throws Exception if command.execute() throws an exception.
	*/
	public synchronized Serializable executeCommand(MementoCommand command) throws Exception {
		clock.pause();  //To be deterministic, the system must know exactly at what time the command is being executed.
		try {
			output.writeCommand(new ClockRecoveryCommand(command, clock.time()));

			return command.execute(this, system);

		} catch (Exception exception) {
      // Something went wrong. Restore the mementos in reverse order.
      for (int i = 0; i < mementos.size(); i++) {
        Memento memento = (Memento)mementos.get(mementos.size() - 1 - i);
        
        memento.restore();
      }
      
      throw exception;
    } finally {
			clock.resume();
      mementos.clear();
		}
	}


	/** Produces a complete serialized image of the underlying PrevalentSystem.
	* This will accelerate future system startups. Taking a snapshot once a day is enough for most applications.
	* @see system()
	* @throws IOException if there is trouble writing to the snapshot file.
	*/
	public synchronized void takeSnapshot() throws IOException {
		clock.pause();
		try {
			output.writeSnapshot(system);
		} finally {
			clock.resume();
		}
	}
  
  public void addMemento(Memento memento) {
    mementos.add(memento);
  }


	private void recoverCommands(CommandInputStream input) throws IOException, ClassNotFoundException {
		MementoCommand command;
		while(true) {
			try {
				command = input.readCommand();
			} catch (EOFException eof) {
				break;
			}

			try {
				command.execute(this, system);
			} catch (Exception e) {
				//Don't do anything at all. Commands may throw exceptions normally.
			}
		}
	}
}


/** A command for executing another command at a specific moment in time.
*/
class ClockRecoveryCommand extends MementoCommand {

	private MementoCommand command;
	private long millis;
  private transient PrevalentSystem system;

	public ClockRecoveryCommand(MementoCommand command, Date date) {
		this.command = command;
		this.millis = date.getTime();
	}

	protected Serializable execute(MementoCollector collector) throws Exception {
		((SystemClock)system.clock()).recover(millis);
		return command.execute(collector, system);
	}
  
  protected void findObjects(PrevalentSystem system)
  {
    this.system = system;
  }
  
  protected void checkPrecondition()
  {
  }
  
  protected void createMementos(MementoCollector collector)
  {
  }
}

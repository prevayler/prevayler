// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler;

import java.util.Date;
import java.io.*;
import prevayler.implementation.*;

/** Provides transparent persistence and fault-tolerance for business objects.
* This applies to any deterministic system implementing the PrevalentSystem interface.
* All commands to the system must be represented as objects implementing the Command interface and must be executed using Prevayler.executeCommand(Command).
* Take a look at the demo application distributed with Prevayler for examples.
* @see PrevaylerFactory
*/
public class Prevayler {

    private PrevalentSystem system;
    private AlarmClock clock;
    private CommandOutputStream output;
    private Date lastTime;

    /** Creates a Prevayler for the received system, using the received CommandOutputStream.
    */
    Prevayler(PrevalentSystem system, CommandOutputStream output) {
        this.system = system;
        this.clock  = system.clock();
        this.output = output;
	}

    /** Logs the received command for crash recovery and shutdown recovery and executes it on the underlying PrevalentSystem.
    * @see system()
    * @throws IOException if there is trouble writing the command to the log.
    * @throws Exception if command.execute() throws exception.
    */
    public synchronized Serializable executeCommand(Command command) throws Exception {
        clock.pause();  //To be deterministic, the system must know exactly at what time the command is being executed.
        
        try {
            Date thisTime = clock.time();
            if (thisTime != lastTime) {
                output.writeCommand(new ClockRecoveryCommand(thisTime));
                lastTime = thisTime;
            }

            output.writeCommand(command);

            return command.execute(system);

        } finally {
            clock.resume();
        }
	}

    /** Produces a complete serialized image of the underlying PrevalentSystem for crash recovery and shutdown recovery.
    * This will accelerate future system startups. Taking a snapshot once a day is enough for most applications.
    * @see system()
    * @throws IOException if there is trouble writing the command to the log.
    */
    public synchronized void takeSnapshot() throws IOException {
        clock.pause();
        try {
            output.writeSnapshot(system);
        } finally {
            clock.resume();
        }
	}

    /** Returns the underlying PrevalentSystem.
    */
    public PrevalentSystem system() {
        return system;
    }
}

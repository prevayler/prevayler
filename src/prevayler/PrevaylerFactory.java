// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler;

import java.io.*;
import prevayler.implementation.CommandInputStream;
import prevayler.implementation.CommandOutputStream;

/** Used to create Prevaylers for PrevalentSystems.
*/
public class PrevaylerFactory {

    /** Returns a Prevayler for the PrevalentSystem returned by the received PrevalentSystemFactory.
    * @param factory The PrevalentSystemFactory that will be used to create and recreate the underlying PrevalentSystem at every system startup, until a snapshot is taken.
    * @param directory The full path of the directory where the snapshot and log files shall be created and read.
    */
    public Prevayler create(PrevalentSystemFactory factory, String directory) throws IOException, ClassNotFoundException {

        CommandInputStream input = new CommandInputStream(directory);

        PrevalentSystem system = (PrevalentSystem)input.readLastSnapshot();
        if (system == null) system = factory.create();

        this.recoverCommands(system, input);
        system.clock().resume();

        return new Prevayler(system, input.commandOutputStream());
	}


    private void recoverCommands(PrevalentSystem system, CommandInputStream input) throws IOException, ClassNotFoundException {
        Command command;
        while(true) {
            try {
                command = input.readCommand();
            } catch (EOFException eof) {
                break;
            }

            try {
                command.execute(system);
            } catch (Exception e) {
                //Don't do anything at all. Commands may throw exceptions normally.
            }
        }
	}
}

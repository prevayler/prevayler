// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler.implementation;

import java.io.*;
import prevayler.Command;

/** Provides an easy API for reading commands and snapshots.
*/
public class CommandInputStream {

    public CommandInputStream(String directory) throws IOException {
        fileFinder = new NumberFileFinder(directory);
    }

    public Object readLastSnapshot() throws IOException, ClassNotFoundException {
        File snapshotFile = fileFinder.lastSnapshot();
        if (snapshotFile == null) return null;

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(snapshotFile));
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }

    public Command readCommand() throws IOException, ClassNotFoundException {
        if (currentLogStream == null) currentLogStream = newLogStream();

        try {
            return (Command)currentLogStream.readObject();
        } catch (EOFException eof) {
            currentLogStream.close();
            currentLogStream = null;
            return readCommand();
        }
    }

    public CommandOutputStream commandOutputStream() {
        return new CommandOutputStream(fileFinder.fileCreator());
    }

    private ObjectInputStream newLogStream() throws IOException {
        File logFile = fileFinder.nextPendingLog();
        return new ObjectInputStream(new FileInputStream(logFile));
    }

    private NumberFileFinder fileFinder;
    private ObjectInputStream currentLogStream;
}

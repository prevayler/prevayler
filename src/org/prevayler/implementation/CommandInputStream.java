// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.*;
import org.prevayler.*;

/** Provides an easy API for reading commands and snapshots.
*/
class CommandInputStream {

    public CommandInputStream(String directory) throws IOException {
	fileFinder = new NumberFileFinder(directory);
	out("Recovering system state...");
    }

    public PrevalentSystem readLastSnapshot() throws IOException, ClassNotFoundException {
	File snapshotFile = fileFinder.lastSnapshot();
	if (snapshotFile == null) return null;
	out(snapshotFile);

	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(snapshotFile));
	try {
	    return (PrevalentSystem)ois.readObject();
	} finally {
	    ois.close();
	}
    }

    public Command readCommand() throws IOException, ClassNotFoundException {
	if (currentLogStream == null) currentLogStream = newLogStream();  //Throws EOFException if there are no more log streams.

	try {
	    return (Command)currentLogStream.readObject();
	} catch (EOFException eof) {
	    //No more commands in this file.
	} catch (ObjectStreamException osx) {
	    logStreamExceptionMessage(osx);
	} catch (RuntimeException rx) {
	    logStreamExceptionMessage(rx);    //Some stream corruptions cause runtime exceptions!
	}

	currentLogStream.close();
	currentLogStream = null;
	return readCommand();
    }

    public CommandOutputStream commandOutputStream() {
	return new CommandOutputStream(fileFinder.fileCreator());
    }

    private ObjectInputStream newLogStream() throws IOException {
	File logFile = fileFinder.nextPendingLog();  //Throws EOFException if there are no more pending log files.
	out(logFile);
	return new ObjectInputStream(new FileInputStream(logFile));
    }

    private void logStreamExceptionMessage(Exception exception) {
	out("   " + exception);
	out("   Some commands might have been lost. Looking for the next file..." );
    }

    private static void out(File file) {
	out("Reading " + file + "...");
    }

    private static void out(Object obj) {
	System.out.println(obj);
    }

    private NumberFileFinder fileFinder;
    private ObjectInputStream currentLogStream;
}

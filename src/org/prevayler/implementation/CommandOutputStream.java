// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.io.*;

/** Provides a simple API for writing commands and snapshots.
*/
class CommandOutputStream {

    /** This number determines the size of the log files produces by the system.
    */
    public static final long LOG_FILE_SIZE = 100000L;
    private final NumberFileCreator fileCreator;
	private ObjectOutputStream logStream;
    private ByteCountStream fileStream;

    public CommandOutputStream(NumberFileCreator fileCreator) {
	this.fileCreator = fileCreator;
    }

    public void writeCommand(Command command) throws IOException{
	ObjectOutputStream oos = logStream();
	try {
	    oos.writeObject(command);
	    oos.reset();    //You can comment this line if your free RAM is large compared to the size of each commandLog file. If you comment this line, commands will occupy much less space in the log file because their class descriptions will only be written once. Your application will therefore produce much fewer log files. If you comment this line, you must make sure that no command INSTANCE is used more than once in your application with different internal values. "Reset will disregard the state of any objects already written to the stream. The state is reset to be the same as a new ObjectOutputStream. ... Objects previously written to the stream will not be refered to as already being in the stream. They will be written to the stream again." - JDK1.2.2 API documentation.
	    oos.flush();
	} catch (IOException iox) {
	    closeLogStream();
	    throw iox;
	}
	}

    public synchronized void writeSnapshot(PrevalentSystem system) throws IOException{
	closeLogStream();    //After every snapshot, a new commandLog file must be started.

	File tempSnapshot = fileCreator.newTempSnapshot();

	ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(tempSnapshot));
	stream.writeObject(system);
	stream.close();

	File snapshot = fileCreator.newSnapshot();
	if (!tempSnapshot.renameTo(snapshot)) throw new IOException("Unable to rename " + tempSnapshot + " to " + snapshot);
    }

	private ObjectOutputStream logStream() throws IOException{
	if(logStream == null) {
	    fileStream = new ByteCountStream(fileCreator.newLog());
	    logStream = new ObjectOutputStream(fileStream);
		}

	if(fileStream.bytesWritten() >= LOG_FILE_SIZE) {
	    closeLogStream();
	    return logStream();  //Recursive call.
		}

	return logStream;
	}

	private void closeLogStream() throws IOException {
	if (logStream == null) return;

	logStream.close();
	logStream = null;
    }

}

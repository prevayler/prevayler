// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler.implementation;

import java.io.*;

/** Provides a simple API for writing commands and snapshots.
*/
public class CommandOutputStream {

    private NumberFileCreator fileCreator;
	private ObjectOutputStream logStream;
    private ByteCountStream fileStream;

    public CommandOutputStream(NumberFileCreator fileCreator) {
        this.fileCreator = fileCreator;
    }

    public void writeCommand(Serializable command) throws IOException{
        ObjectOutputStream oos = logStream();
        try {
            oos.writeObject(command);
        } catch (IOException iox) {
            oos.close();
            throw iox;
        }
		oos.flush();
	}

    public synchronized void writeSnapshot(Serializable system) throws IOException{
        closeLogStream();

        ObjectOutputStream snapshot = snapshotStream();
        try {
            snapshot.writeObject(system);
        } finally {
			snapshot.close();
		}
    }

	private ObjectOutputStream logStream() throws IOException{
        if(logStream == null) {
            fileStream = new ByteCountStream(fileCreator.newLog());
            logStream = new ObjectOutputStream(fileStream);
		}

        if(fileStream.bytesWritten() >= 100000L) {  //This number determines the size of your log files. You can change it at will.
            closeLogStream();
            return logStream();  //Recursive call.
		}

        return logStream;
	}

	private void closeLogStream() throws IOException {
        if (logStream != null) logStream.close();
        logStream = null;
    }

	private ObjectOutputStream snapshotStream() throws IOException{
        File file = fileCreator.newSnapshot();
        return new ObjectOutputStream(new FileOutputStream(file));
	}
}

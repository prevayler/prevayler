// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler.implementation;

import java.io.*;
import java.text.*;

/** Creates .log and .snapshot files using a number sequence for the file name.
*/
public class NumberFileCreator {

    public static final DecimalFormat SNAPSHOT_FORMAT = new DecimalFormat("000000000000000000000'.'snapshot");  //21 zeros (enough for a long number).
    public static final DecimalFormat LOG_FORMAT = new DecimalFormat("000000000000000000000'.'log");  //21 zeros (enough for a long number).

    private File directory;
    private long nextFileNumber;

    public NumberFileCreator(File directory, long firstFileNumber) {
        this.directory = directory;
        this.nextFileNumber = firstFileNumber;
	}

    public File newLog() throws IOException {
        File log = new File(directory, LOG_FORMAT.format(nextFileNumber));
        log.createNewFile();

        ++nextFileNumber;

        return log;
    }

    public File newSnapshot() throws IOException {
        File snapshot = new File(directory, SNAPSHOT_FORMAT.format(nextFileNumber - 1));
        snapshot.createNewFile();
        return snapshot;
    }
}

// Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT
//ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//FOR A PARTICULAR PURPOSE.
//Contributed by Carlos Villela.
package org.prevayler.implementation.monitor;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.prevayler.Monitor;

/**
 * A Monitor that logs output to System.out.
 * 
 * @author Carlos Villela
 * @since May 14, 2004
 */
public class SimpleMonitor implements Monitor {

    /**
     * @see org.prevayler.Monitor#snapshotTaken(long)
     */
    public void snapshotTaken(long version) {
        System.out.println("Snapshot " + version + " taken at " + new Date());
    }

    /**
     * @see org.prevayler.Monitor#handleExceptionWhileCreatingLogFile(java.io.IOException,
     *      java.io.File)
     */
    public void handleExceptionWhileCreatingLogFile(IOException iox, File logFile) {
        iox.printStackTrace();
        System.out
                .println("\nThe exception above was thrown while trying to create file "
                        + logFile
                        + " . Prevayler's default behavior is to display this message and block all transactions. You can change this behavior by extending the PersistentJournal class and overriding the method called: handleExceptionWhileCreating(IOException iox, File logFile).");
    }

    /**
     * @see org.prevayler.Monitor#handleExceptionWhileWritingLogFile(java.io.IOException,
     *      java.io.File)
     */
    public void handleExceptionWhileWritingLogFile(IOException iox, File logFile) {
        iox.printStackTrace();
        System.out
                .println("\nThe exception above was thrown while trying to write to file "
                        + logFile
                        + " . Prevayler's default behavior is to display this message and block all transactions. You can change this behavior by extending the PersistentJournal class and overriding the method called: handleExceptionWhileWriting(IOException iox, File logFile).");
    }

    /**
     * @see org.prevayler.Monitor#ignoringStreamCorruption(java.lang.Exception)
     */
    public void ignoringStreamCorruption(Exception exception, File file) {
        exception.printStackTrace();
        System.err.println("\n   Thrown while reading file: " + file + ")"
                + "\n   The above is a stream corruption that can be caused by:"
                + "\n      - A system crash while writing to the file (that is OK)."
                + "\n      - A corruption in the file system (that is NOT OK)."
                + "\n      - Tampering with the file (that is NOT OK).");

    }

    /**
     * @see org.prevayler.Monitor#journalInitialized(java.io.File,
     *      java.lang.ClassLoader, long, long)
     */
    public void journalInitialized(File directory, ClassLoader loader, long sizeThresholdInBytes, long ageThresholdInMillis) {
        System.out.println("Logger initialized on " + directory.getName() + ", with thresholds of " + sizeThresholdInBytes
                + " bytes, and " + ageThresholdInMillis + " milliseconds.");
        System.out.println("Using classloader: " + loader);
    }

    /**
     * @see org.prevayler.Monitor#readingTransactionLogFile(java.io.File,
     *      java.lang.ClassLoader)
     */
    public void readingTransactionLogFile(File file, ClassLoader loader) {
        System.out.println("Reading file: " + file.getName() + " using " + loader);
    }

    /**
     * @see org.prevayler.Monitor#lastSnapshotRecovered(long)
     */
    public void lastSnapshotRecovered(long version) {
        System.out.println("Last snapshot (version " + version + ") recovered successfully.");
    }

}
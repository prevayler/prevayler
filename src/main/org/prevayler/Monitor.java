// Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE.
// Contributed by Carlos Villela.
package org.prevayler;

import java.io.File;
import java.io.IOException;

/**
 * A Monitor for interesting Prevayler events.
 * 
 * @author Carlos Villela
 * @since May 14, 2004
 */
public interface Monitor {

    /**
     * A snapshot has been successfully taken.
     */
    void snapshotTaken(long version);

    /**
     * An exception happened while creating the transaction log file.
     * 
     * @param exception
     * @param file
     */
    void handleExceptionWhileCreatingLogFile(IOException exception, File file);

    /**
     * An exception happened while writing to the transaction log file.
     * 
     * @param exception
     * @param file
     */
    void handleExceptionWhileWritingLogFile(IOException exception, File file);

    /**
     * An exception happened while reading the transaction log file,
     * and the file is being ignored.
     * 
     * @param exception
     */
    void ignoringStreamCorruption(Exception exception, File file);

    /**
     * The TransactionLogger was initialized successfully.
     * 
     * @param directory
     * @param sizeThresholdInBytes
     * @param ageThresholdInMillis
     */
    void loggerInitialized(File directory, ClassLoader loader, long sizeThresholdInBytes, long ageThresholdInMillis);

    /**
     * Prevayler is reading a transaction log file.
     * 
     * @param file
     */
    void readingTransactionLogFile(File file, ClassLoader loader);

    /**
     * The last snapshot has been recovered successfully.
     */
    void lastSnapshotRecovered(long version);

}

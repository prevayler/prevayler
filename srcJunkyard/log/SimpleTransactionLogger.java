// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import org.prevayler.foundation.StopWatch;
import java.io.*;


/**
 * Serializes transactions to transactionLog files, along with their execution time and execution sequence number.
 * @see SnapshotPrevayler on the use of the org.prevayler.SafeTransactionLogs, org.prevayler.TransactionLogsThresholdBytes and org.prevayler.TransactionLogsThresholdMinutes system properties.
 * @see TransactionLogRecoverer
 */
class SimpleTransactionLogger {

  private final boolean _safeTransactionLogs = safeTransactionLogsSystemProperty();
  private final long _transactionLogsThresholdBytes = parseSystemProperty("org.prevayler.TransactionLogsThresholdBytes");
  private final long _transactionLogsThresholdMillis = 1000 * 60 * parseSystemProperty("org.prevayler.TransactionLogsThresholdMinutes");

  private final File _logFile;
  private final FileOutputStream _fileOutputStream;
  private final ObjectOutputStream _objectOutputStream;

  private long _executionTime;
  private long _executionSequence;

  private final StopWatch stopWatch = StopWatch.start();


  SimpleTransactionLogger(File logFile, boolean sequenceRestarted) throws IOException {
    _logFile = logFile;
    _fileOutputStream = new FileOutputStream(logFile);
    _objectOutputStream = new ObjectOutputStream(fileOutputStream);

    _objectOutputStream.writeObject(new Character(sequenceRestarted ? 'R' : 'C'));   //R - Restart. C - Continue.
    flushStreams(true);
  }


  void write(Transaction transaction) throws IOException {
    _objectOutputStream.writeObject(transaction);
  }


  void stamp(long executionTime, long sequence) {
    _executionTime = executionTime.getTime();
    _executionSequence = executionSequence;
  }


  void sync() throws IOException {
    _objectOutputStream.writeLong(executionTime);
    _objectOutputStream.writeLong(executionSequence);

    _flushStreams(safeTransactionLogs);
  }


  private void flushStreams(boolean sync) throws IOException {
    _objectOutputStream.flush();
    if (sync) _fileOutputStream.getFD().sync();
  }


  boolean isValid() {
    return !isExpired() && !isFull();
  }

  private boolean isExpired() {
    if (_transactionLogsThresholdMillis == 0) return false;
    return stopWatch.millisEllapsed() >= _transactionLogsThresholdMillis;
  }

  private boolean isFull() {
    if (_transactionLogsThresholdBytes == 0) return false;
    return _logFile.length() >= _transactionLogsThresholdBytes;
  }


  void close() throws IOException {
    _objectOutputStream.close();
  }


  File directory() {
    return _logFile.getParentFile();
  }


  static private long parseSystemProperty(String propertyName) {
  	String property = System.getProperty(propertyName);
  	if (property == null) return 0;
    try {
      return Long.parseLong(property);
    } catch (NumberFormatException nfx) {
      return 0;
    }
  }

  static private boolean safeTransactionLogsSystemProperty() {
    boolean safe = !"off".equalsIgnoreCase(System.getProperty("org.prevayler.SafeTransactionLogs"));
    safeTransactionLogsMessage(safe);
    return safe;
  }

  static private void safeTransactionLogsMessage(boolean safe) {
    if (!safe && _lastTimeWasSafe) {
      out("\n=====================================");
      out("The org.prevayler.SafeTransactionLogs system property is OFF.");
      out("Writes to the transactionLog files will be cached for greater performance. In the event of a system crash, some of the last executed transactions might be lost.");
      out("=====================================\n");
    }
    if (safe && !_lastTimeWasSafe) {
      out("\n=====================================");
      out("The org.prevayler.SafeTransactionLogs system property is ON.");
      out("Writes to the transactionLog files will be flushed to the underlying device before each transaction is executed.");
      out("=====================================\n");
    }
    _lastTimeWasSafe = safe;
  }
  static private boolean _lastTimeWasSafe = true;


  static private void out(Object message) {
    System.out.println(message);
  }
}

// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import org.prevayler.util.StopWatch;
import java.io.*;


/**
 * Serializes commands to commandLog files, along with their execution time and execution sequence number.
 * @see SnapshotPrevayler on the use of the org.prevayler.SafeCommandLogs, org.prevayler.CommandLogsThresholdBytes and org.prevayler.CommandLogsThresholdMinutes system properties.
 * @see CommandLogRecoverer
 */
class CommandLogger {

  private final boolean safeCommandLogs = safeCommandLogsSystemProperty();
  private final long commandLogsThresholdBytes = parseSystemProperty("org.prevayler.CommandLogsThresholdBytes");
  private final long commandLogsThresholdMillis = 1000 * 60 * parseSystemProperty("org.prevayler.CommandLogsThresholdMinutes");

  private final File logFile;
  private final FileOutputStream fileOutputStream;
  private final ObjectOutputStream objectOutputStream;

  private long executionTime;
  private long executionSequence;

  private final StopWatch stopWatch = StopWatch.start();


  CommandLogger(File logFile, boolean sequenceRestarted) throws IOException {
    this.logFile = logFile;
    fileOutputStream = new FileOutputStream(logFile);
    objectOutputStream = new ObjectOutputStream(fileOutputStream);

    objectOutputStream.writeChar(sequenceRestarted ? 'R' : 'C');   //R - Restart. C - Continue.
    flushStreams(true);
  }


  void writeCommand(Command command) throws IOException {
    objectOutputStream.writeObject(command);
  }


  void executionSequence (long executionTime, long executionSequence) {
    this.executionTime = executionTime;
    this.executionSequence = executionSequence;
  }


  void flushToDisk() throws IOException {
    objectOutputStream.writeLong(executionTime);
    objectOutputStream.writeLong(executionSequence);

    flushStreams(safeCommandLogs);
  }


  private void flushStreams(boolean sync) throws IOException {
    objectOutputStream.flush();
    if (sync) fileOutputStream.getFD().sync();
  }


  boolean isValid() {
    return !isExpired() && !isFull();
  }

  private boolean isExpired() {
    if (commandLogsThresholdMillis == 0) return false;
    return stopWatch.millisEllapsed() >= commandLogsThresholdMillis;
  }

  private boolean isFull() {
    if (commandLogsThresholdBytes == 0) return false;
    return logFile.length() >= commandLogsThresholdBytes;
  }


  void close() throws IOException {
    objectOutputStream.close();
  }


  File directory() {
    return logFile.getParentFile();
  }


  static private long parseSystemProperty(String propertyName) {
    try {
      return Long.parseLong(System.getProperty(propertyName));
    } catch (NumberFormatException nfx) {
      return 0;
    }
  }

  static private boolean safeCommandLogsSystemProperty() {
    boolean safe = !"off".equalsIgnoreCase(System.getProperty("org.prevayler.SafeCommandLogs"));
    safeCommandLogsMessage(safe);
    return safe;
  }

  static private void safeCommandLogsMessage(boolean safe) {
    if (!safe && lastTimeWasSafe) {
      out("\n=====================================");
      out("The org.prevayler.SafeCommandLogs system property is OFF.");
      out("Writes to the commandLog files will be cached for greater performance. In the event of a system crash, some of the last executed commands might be lost, though.");
      out("=====================================\n");
    }
    if (safe && !lastTimeWasSafe) {
      out("\n=====================================");
      out("The org.prevayler.SafeCommandLogs system property is ON.");
      out("Writes to the commandLog files will be flushed to the underlying device before each command is executed.");
      out("=====================================\n");
    }
    lastTimeWasSafe = safe;
  }
  static private boolean lastTimeWasSafe = true;


  static private void out(Object message) {
    System.out.println(message);
  }
}

// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.io.*;


/**
 * Reads commands from a commandLog file and "reexecutes" them on a PrevalentSystem. This process is controlled by each command's execution time and excution sequence number, read from the commandLog file after each command.
 * @see CommandLogger
 */
class CommandLogRecoverer {

  private final File logFile;
  private final ObjectInputStream logStream;

  private final boolean isExecutionSequenceRestarted;

  private final PrevalentSystem system;
  private final SystemClock clock;
  
  private Command pendingCommand;
  private long executionSequence;
  private long executionTime;


  CommandLogRecoverer(File logFile, PrevalentSystem system) throws IOException {
    out("Reading " + logFile + "...");

    this.logFile = logFile;
    logStream = new ObjectInputStream(new FileInputStream(logFile));
    this.system = system;
    this.clock = (SystemClock)system.clock();

    boolean isRestarted;
    try {
      isRestarted = (((Character)logStream.readObject()).charValue() == 'R');
    } catch (Exception e) {
      isRestarted = false;
    }
    isExecutionSequenceRestarted = isRestarted;
  }


  boolean isExecutionSequenceRestarted() {
    return isExecutionSequenceRestarted;
  }


  boolean recover(long nextExecutionSequence) throws EOFException, IOException, ClassNotFoundException {
    preparePendingCommand();
    if (nextExecutionSequence != executionSequence) return false;

    clock.recover(executionTime);
    try {

      pendingCommand.execute(system);

    } catch (Exception e) {
      //Don't do anything at all now, during recovery. This exception was already treated by the client when it was thrown the first time, during normal system execution.
    }

    pendingCommand = null;
    return true;
  }


  private void preparePendingCommand() throws EOFException, IOException, ClassNotFoundException {
    if (pendingCommand != null) return;   //Had already been prepared.

    try {

      pendingCommand = (Command)logStream.readObject();
      executionTime = logStream.readLong();
      executionSequence = logStream.readLong();

    } catch (StreamCorruptedException scx) {
      abort(scx);
    } catch (UTFDataFormatException utfx) {
      abort(utfx);
    } catch (RuntimeException rx) {    //Some stream corruptions cause runtime exceptions in JDK1.3.1!
      abort(rx);
    }
  }


  private void abort(Exception exception) throws EOFException {
    out("\n" + exception + " (File: " + logFile + ")");
    out("   The above is a stream corruption that can be caused by:");
    out("      - A system crash while writing to the commandLog file (that is OK).");
    out("      - A corruption in the file system (that is NOT OK).");
    out("      - Tampering with the commandLog file (that is NOT OK).");
    out("   Looking for the next command...\n" );

    throw new EOFException();
  }


  void close() throws IOException {
    logStream.close();
  }


  static private void out(String message) {
    System.out.println(message);
  }
}

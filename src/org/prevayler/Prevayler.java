// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

import java.io.Serializable;


/**
 * Provides transparent persistence for all business objects in a PrevalentSystem. All commands to the system must be represented as objects implementing the Command interface and must be executed using Prevayler.executeCommand(Command).
 * See the demo applications in org.prevayler.demos for examples.
 */
public interface Prevayler {

  /**
   * Returns the underlying PrevalentSystem.
   */
  public PrevalentSystem system();


  /**
   * Logs the received command for crash or shutdown recovery and executes it on the underlying PrevalentSystem.
   * @return The serializable object that was returned by the execution of command.
   * @throws IOException if there is trouble writing the command to one of the log files.
   * @throws Exception if the execution of command throws an Exception.
   */
  public Serializable executeCommand(Command command) throws Exception;
}

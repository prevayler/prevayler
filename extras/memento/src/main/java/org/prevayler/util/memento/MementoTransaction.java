// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util.memento;

import java.io.Serializable;

/**
 * An atomic transaction to be executed on a PrevalentSystem. Any operation which changes the
 * observable state of a PrevalentSystem must be encapsulated as a Command.
 * 
 * This command adds the capability to create mementos of objects before they get modified. In
 * the event of a failure, these mementos are restored and the system is left in the state it
 * was in before the command got executed.
 * 
 * The execution is split in four parts:
 * <ul>
 *  <li>Find the objects: find all the objects needed for checking the precondition, creating
 *  the mementos and execution, and store them in transient fields;</li>
 *  <li>Check the precondition: verify that it is safe to execute;</li>
 *  <li>Create the mementos: create a memento for each object that is possibly going to be modified;</li>
 *  <li>Execute: modify the objects.</li>
 * </ul>
 * 
 * @author Johan Stuyts
 * @version 2.0
 */
public abstract class MementoTransaction implements Serializable {
  /**
   * Executes this command on the received system. See prevayler.demos for examples.
   * The returned object has to be Serializable in preparation for future versions of
   * Prevayler that will provide fault-tolerance through system replicas.
   * 
   * @param collector The memento collector to which to add the mementos. A memento
   * collector instead of a Prevayler instance is passed, so the command will not easily
   * invoke subcommands through the prevayler (which is not allowed).
   * @param system The system on which to execute the command.
   * @return The object returned by the execution of this command. Most commands simply return null.
   */
  public Serializable execute(MementoCollector collector, Object prevalentSystem) throws Exception {
    findObjects(prevalentSystem);
    
    checkPrecondition();
    
    createMementos(collector);
    
    return execute(collector);
  }

  /**
   * Find the objects this command modifies.
   * 
   * @param system The prevalent system in which to find the objects.
   */
  protected abstract void findObjects(Object prevalentSystem) throws Exception;

  /**
   * Check the precondition.
   */
  protected abstract void checkPrecondition() throws Exception;

  /**
   * Create mementos for all objects which (possibly) get modified.
   * 
   * @param collector The memento collector to which to add the mementos.
   */
  protected abstract void createMementos(MementoCollector collector);

  /**
   * Execute the actual command.
   * 
   * @param collector The memento collector which can be used to execute subcommands.
   * @return The object returned by the execution of this command. Most commands simply return null.
   */
  protected abstract Serializable execute(MementoCollector collector) throws Exception;
}

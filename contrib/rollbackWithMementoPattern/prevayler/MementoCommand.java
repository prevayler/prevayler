// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler;

import java.io.Serializable;


/**
 * An atomic transaction to be executed on a PrevalentSystem. Any operation which changes the observable state of a PrevalentSystem must be encapsulated as a Command.
 * 
 * @author Johan Stuyts
 */
public abstract class MementoCommand implements Serializable {

  /**
   * Executes this command on the received system. See prevayler.demos for examples. The returned object has to be Serializable in preparation for future versions of Prevayler that will provide fault-tolerance through system replicas.
   * 
   * @param collector The memento collector to which to add the mementos. A memento collector instead of a prevayler is passed, so the command will not easily invoke subcommands through the prevayler (which is not allowed).
   * @param system The system on which to execute the command.
   * @return The object returned by the execution of this command. Most commands simply return null.
   */
  public Serializable execute(MementoCollector collector, PrevalentSystem system) throws Exception {
    findObjects(system);
    
    checkPrecondition();
    
    createMementos(collector);
    
    return execute(collector);
  }
  
  /**
   * Find the objects this command modifies.
   * 
   * @param system The prevalent system in which to find the objects.
   */
  protected abstract void findObjects(PrevalentSystem system) throws Exception;
  
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

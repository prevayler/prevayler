// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld, Paul Hammant
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo1;

import org.prevayler.Command;
import org.prevayler.PrevalentSystem;

import java.io.Serializable;

/**
 * To change the state of the business objects, the client code must use a Command like this one.
 */
class NumberStorageCommand implements Command {

  private final int numberToKeep;


  NumberStorageCommand(int numberToKeep) {
    this.numberToKeep = numberToKeep;
  }

  public Serializable execute(PrevalentSystem system) throws Exception {
    ((NumberKeeper)system).keep(numberToKeep);
    return null;
  }
}

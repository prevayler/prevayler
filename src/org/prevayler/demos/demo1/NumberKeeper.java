// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld, Paul Hammant
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo1;

import org.prevayler.implementation.AbstractPrevalentSystem;
import java.util.*;


/**
 * The NumberKeeper and all its references are the prevalent system.
 * i.e: They are the "business objects" and will be transparently persisted by Prevayler.
 */
class NumberKeeper extends AbstractPrevalentSystem {

  private final List numbers = new ArrayList();


  void keep(int nextNumber) {
    numbers.add(new Integer(nextNumber));
  }

  List numbers() {
    return numbers;
  }

  int lastNumber() {
    return numbers.isEmpty()
      ? 0
      : ((Integer)numbers.get(numbers.size() - 1)).intValue();
  }

}

// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util.memento;

/**
 * A memento is a snapshot of an object before it gets changed. A copy of all fields
 * under the objects control should be made.
 * 
 * @author Johan Stuyts
 * @version 2.0
 */
public abstract class Memento {
  /**
   * Restore the values of the object to the values in this memento.
   * 
   * It is very important that the implementation does not throw any checked and run-time exceptions.
   */
  protected abstract void restore();
  
  /**
   * Get the object to which this memento belongs.
   * 
   * @return The object to which this memento belongs.
   */
  protected abstract Object getOwner();
  
  /**
   * The hash code of this object. This is used in some sets and maps.
   * 
   * As it is very important that only the first memento of an object gets stored, this method
   * returns the identity hash code of the owner.
   * 
   * @return The hash code of this memento.
   */
  public int hashCode() {
    return System.identityHashCode(getOwner());
  }
  
  /**
   * Compare this memento with another. This is used in some sets and maps.
   * 
   * As it is very important that only the first memento of an object gets stored, this method
   * returns true if the class of the other object is the same (identity comparison) class as
   * the class of this memento, and the owner of the other memento is the same (identity 
   * comparison) as the owner of this memento.
   * 
   * @return true if the other memento is a memento for the same object, false otherwise.
   */
  public boolean equals(Object other) {
    boolean result = false;
    
    if (other.getClass() == getClass()) {
      Memento otherAsMemento;
      
      otherAsMemento = (Memento)other;
      
      result = otherAsMemento.getOwner() == getOwner();
    }
    
    return result;
  }
}

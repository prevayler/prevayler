// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler;

/**
 * A memento is a snapshot of an object before it gets changed.
 * 
 * @author Johan Stuyts
 */
public interface Memento {
  /**
   * Set the values of the object to the values in this memento.
   * 
   * It is very important that the implementation does not throw any checked and run-time exceptions.
   */
  void restore();
}

// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

/**
 * Provides transparent persistence for all business objects in a prevalent system. All transactions to the system must be represented as Transaction objects and must be executed using Prevayler.execute(Transaction).
 * See the demo applications in org.prevayler.demos for examples.
 */
public interface Prevayler {

	/**
	 * Returns the underlying prevalent system.
	 */
	public Object prevalentSystem();

	/**
	 * Logs the given transaction for crash or shutdown recovery and executes it on the underlying prevalent system.
	 * @throws IOException if there is trouble writing the transaction to disk or replicating it to remote Prevaylers.
	 */
	public void execute(Transaction transaction) throws java.io.IOException;

}

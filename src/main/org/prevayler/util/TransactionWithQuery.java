// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util;

import java.util.Date;

import org.prevayler.*;


/** A Transaction that returns a result or throws an Exception after executing. <br><br>A "PersonCreation" transaction, for example, may return the Person it created. Without this, to retrieve the newly created Person, the caller would have to issue a query like: "What was the last Person I created?". <br><br>Looking at the Prevayler demos is by far the best way to learn how to use this class.
 */
public abstract class TransactionWithQuery implements Transaction {

	private Object _result;
	private Exception _exception;


	/** Makes the given Prevayler execute this transaction, which in turn calls the executeAndQuery(Object prevalentSystem, Date timestamp) method implemented by the subclass.
	 * @return The object returned by the executeAndQuery(Object prevalentSystem, Date timestamp) method implemented by the subclass. E.g: The "Person" object created by a "PersonCreation" transaction.
	 * @throws Exception if the executeAndQuery(Object prevalentSystem, Date timestamp) method implemented by the subclass throws an Exception.
	 */
	public Object executeUsing(Prevayler prevayler) throws Exception {
		prevayler.execute(this);
        if (_exception != null) throw _exception;
		return _result;
	}


	/** Called by the Prevayler. Calls the protected executeAndQuery(Object prevalentSystem) method implemented by the subclass.
	 */
	public final void executeOn(Object prevalentSystem, Date timestamp) {
		try {
			_result = executeAndQuery(prevalentSystem, timestamp);
		} catch (RuntimeException rx) {
			throw rx;   //This is necessary when using the rollback feature.
		} catch (Exception ex) {
			_exception = ex;
		}
	}


	/** The subclass will implement this method doing its modifications on the given prevaylentSystem and also returning any Object or throwing any Exception it wants.
	 */
	protected abstract Object executeAndQuery(Object prevalentSystem, Date timestamp) throws Exception;

}
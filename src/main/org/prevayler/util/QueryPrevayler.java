//Prevayler(TM) - The Open-Source Prevalence Layer.
//Copyright (C) 2001 Klaus Wuestefeld.
//This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;

public class QueryPrevayler implements Prevayler {

	private final Prevayler _prevayler;
	private final Object _prevalentSystem;


	public QueryPrevayler(Prevayler prevayler) {
		_prevayler = prevayler;
		_prevalentSystem = prevayler.prevalentSystem();
	}

	public Object prevalentSystem() {
		return _prevalentSystem;
	}

	public void execute(Transaction transaction) {
		_prevayler.execute(transaction);
	}

	/** Performs query making sure that no other transaction or query is being executed by this Prevayler at the same time. This is acheived by synchronizing on prevalentSystem().
	*/
	public Object performAlone(Query query) throws Exception {
		synchronized (_prevalentSystem) {
			return query.performOn(_prevalentSystem);
		}
	}

	public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
		TransactionWithQueryExecuter executer = new TransactionWithQueryExecuter(transactionWithQuery);
		_prevayler.execute(executer);
		return executer.result();
	}

}

// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.replica;

import java.io.*;
import java.net.Socket;
import org.prevayler.Transaction;
import org.prevayler.implementation.*;

class RemoteConnection extends Thread implements TransactionSubscriber {

	private final TransactionPublisher _publisher;
	private Transaction _remoteTransaction;

	private final ObjectOutputStream _toRemote;
	private final ObjectInputStream _fromRemote;


	RemoteConnection(TransactionPublisher publisher, Socket remoteSocket) throws IOException {
		_publisher = publisher;
		_fromRemote = new ObjectInputStream(remoteSocket.getInputStream());
		_toRemote = new ObjectOutputStream(remoteSocket.getOutputStream());
		setDaemon(true);
		start();
	}


	public void run() {
		try {		
			long initialTransaction = ((Long)_fromRemote.readObject()).longValue();
			_publisher.addSubscriber(this, initialTransaction);
			_toRemote.writeObject(Protocol.TRANSACTIONS_UP_TO_DATE);
			while (true) publishRemoteTransaction();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	void publishRemoteTransaction() throws Exception {
		_remoteTransaction = (Transaction)_fromRemote.readObject();
		_publisher.publish(_remoteTransaction);
	}


	public void receive(Transaction transaction) {
		send(transaction == _remoteTransaction
			? (Object)Protocol.REMOTE_TRANSACTION
			: transaction
		);
	}


	private void send(Object message) {
		try {
			_toRemote.writeObject(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

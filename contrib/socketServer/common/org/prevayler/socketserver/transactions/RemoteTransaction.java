package org.prevayler.socketserver.transactions;

/*
 * prevayler.socketServer, a socket-based server (and client library)
 * to help create client-server Prevayler applications
 * 
 * Copyright (C) 2003 Advanced Systems Concepts, Inc.
 * 
 * Written by David Orme <daveo@swtworkbench.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

/**
 * A default implementation of IRemoteTransaction.  Instead of implementing the interface
 * yourself, you can inherit from this class.  If you don't want a single-rooted
 * Transaction hierarchy, however, just implement the interface.
 * 
 * @author DaveO
 */
public abstract class RemoteTransaction implements Transaction, TransactionWithQuery, IRemoteTransaction {

    protected Long senderID;
    
	/**
	 * Sets the connectionID.
	 * @param connectionID The connectionID to set
	 */
	public void setSenderID(Long connectionID) {
		this.senderID = connectionID;
	}

    public abstract Object executeAndQuery(Object prevalentSystem, Date timestamp) throws Exception;
    
    public void executeOn(Object prevalentSystem, Date timestamp) {
        try {
            executeAndQuery(prevalentSystem, timestamp);
        } catch (Exception e) {}
    }
}


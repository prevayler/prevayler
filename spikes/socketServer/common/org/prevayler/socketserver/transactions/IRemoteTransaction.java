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

/**
 * An interface to allow commands and callbacks to identify the client that
 * originally sent the command that initiated the callback.  This setter is
 * called automatically within CommandThread.  The client is responsible for
 * providing storage and any desired getter access method.
 * 
 * All Command objects that pass through the Prevayler server must implement
 * this interface.  An easy way to accomplish this is to have them inherit
 * from RemoteTransaction, which provides a default implementation of this interface.
 * 
 * @author DaveO
 */
public interface IRemoteTransaction {

	/**
	 * Sets the connectionID.
	 * @param connectionID The connectionID to set
	 */
	public void setSenderID(Long connectionID);

}


package org.prevayler.socketserver.example.transactions;

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
import org.prevayler.socketserver.example.server.Todo;
import org.prevayler.socketserver.example.server.TodoList;
import org.prevayler.socketserver.server.Notification;
import org.prevayler.socketserver.transactions.RemoteTransaction;

/**
 * Create a new Todo item
 * 
 * @author djo
 */
public class CreateTodoBean extends RemoteTransaction {
    
    private String desc;
    
    public CreateTodoBean(String desc) {
        this.desc = desc;
    }

	/**
     * @see org.prevayler.util.TransactionWithQuery#executeAndQuery(Object, Date)
	 */
	public Object executeAndQuery(Object prevalentSystem, Date timestamp) throws Exception {
        TodoList todoList = (TodoList) prevalentSystem;
        Todo todo = todoList.newTodo();
        todo.setDesc(desc);
        
        // Notify interested clients that the list just changed
        // Note that much more complex notification schemes can be devised
        // than this.
        Notification.submit(senderID, "ListChanged", todoList);
        return todo;
	}

}



package org.prevayler.socketserver.example.transactions;

/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

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
     * @see org.prevayler.util.TransactionWithQuery#executeAndQuery(Object)
	 */
	public Object executeAndQuery(Object prevalentSystem) throws Exception {
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



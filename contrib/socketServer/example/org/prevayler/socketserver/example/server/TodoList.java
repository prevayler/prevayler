package org.prevayler.socketserver.example.server;

/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;

import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.socketserver.server.Notification;

/**
 * A simple Todo list
 * @author djo
 */
public class TodoList implements Serializable {

    private TreeMap todoList;
    
    private int nextID = 0;
    
    public TodoList() {
        // Init ourselves
        todoList = new TreeMap();
    }
    
    public Todo[] toArray() {
        Todo[] results = new Todo[todoList.size()];
        int i = 0;
        Iterator iter = todoList.keySet().iterator();
        while (iter.hasNext()) {
			results[i] = (Todo) todoList.get(iter.next());
            ++i;
		}
        return results;
    }
    
    public Todo newTodo() {
        int id = nextID;
        ++nextID;
        Todo todo = new Todo(id);
        todoList.put(new Integer(id), todo);
        return todo;
    }
    
    public Todo get(int id) {
        return (Todo) todoList.get(new Integer(id));
    }
}

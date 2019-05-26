package org.prevayler.socketserver.example.server;

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


import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * A simple Todo list
 *
 * @author djo
 */
public class TodoList implements Serializable {

  private static final long serialVersionUID = 611510576162358132L;

  private TreeMap<Integer, Todo> todoList;

  private int nextID = 0;

  public TodoList() {
    // Init ourselves
    todoList = new TreeMap<Integer, Todo>();
  }

  public Todo[] toArray() {
    Todo[] results = new Todo[todoList.size()];
    int i = 0;
    Iterator<Integer> iter = todoList.keySet().iterator();
    while (iter.hasNext()) {
      results[i] = todoList.get(iter.next());
      ++i;
    }
    return results;
  }

  public Todo newTodo() {
    int id = nextID;
    ++nextID;
    Todo todo = new Todo(id);
    todoList.put(id, todo);
    return todo;
  }

  public Todo get(int id) {
    return todoList.get(id);
  }
}

package junkyard;

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


import java.beans.IntrospectionException;
import java.beans.Introspector;

import org.prevayler.socketserver.example.server.Todo;
import org.prevayler.socketserver.example.server.TodoList;
import org.prevayler.socketserver.util.Log;

/**
 * A setter Transaction for Todo JavaBeans
 * 
 * @author djo
 */
public class TodoBeanSetter extends BeanSetter {
    
    // Init the propertyDescriptors array for Todo objects
    {
        propertyDescriptors = null;
        try {
            propertyDescriptors = Introspector.getBeanInfo(Todo.class).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            Log.error(e, "Unable to get beanInfo for Todo class");
        }
    }
    
	/**
	 * Method BeanSetter.  A Prevayler command to set a Todo object
     * field's value.
     * 
	 * @param id The id of the Todo object to change
	 * @param field The field to change
	 * @param value The new value
	 */
    public TodoBeanSetter(int id, String field, Object value) {
        super(id, field, value);
    }

    /**
     * @see org.prevayler.socketserver.example.transactions.BeanSetter#lookup(Object)
     */
    protected Object lookup(Object prevalentSystem) throws Exception {
        TodoList todoList = (TodoList) prevalentSystem;
        return todoList.get(id);
    }
}


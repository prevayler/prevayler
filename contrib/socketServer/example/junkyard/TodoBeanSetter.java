package junkyard;

/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
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


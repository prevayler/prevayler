/*
 * AddTask.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.util.Date;

import org.prevayler.Transaction;


/**
 * Adds a task to the system.
 *
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class AddTask implements Transaction {

    private final Task task;
    
    /**
     * Creates a new AddTask Transaction.
     * 
     * @param task the task to add
     */
    public AddTask(Task task) {
        this.task = task;
    }
    
    /**
     * @see org.prevayler.Transaction#executeOn(java.lang.Object, java.util.Date)
     */
    public void executeOn(Object prevalentSystem, Date executionTime) {
        TaskList system = (TaskList) prevalentSystem;
        system.addTask(task);
    }

}

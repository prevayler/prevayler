/*
 * AddTask.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.util.Date;

import org.prevayler.TransactionWithQuery;

/**
 * Adds a task to the system.
 * 
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class AddTask implements TransactionWithQuery {

    private final Task task;

    /**
     * Creates a new AddTask Transaction.
     * 
     * @param task
     *            the task to add
     */
    public AddTask(String description, int priority) {
        this.task = new Task(description, priority);
    }

    /**
     * @see org.prevayler.TransactionWithQuery#executeAndQuery(java.lang.Object,
     *      java.util.Date)
     */
    // START SNIPPET: execute
    public Object executeAndQuery(Object prevalentSystem, Date executionTime) throws Exception {

        TaskList system = (TaskList) prevalentSystem;
        system.addTask(task);

        return task;
    }
    // END SNIPPET: execute

}

/*
 * RemoveTask.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.util.Date;

import org.prevayler.Transaction;


/**
 * TODO: Document
 *
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class RemoveTask implements Transaction {

    private Task task;

    /**
     * @param dishes
     */
    public RemoveTask(Task task) {
        this.task = task;
    }

    /**
     * @see org.prevayler.Transaction#executeOn(java.lang.Object, java.util.Date)
     */
    public void executeOn(Object prevalentSystem, Date executionTime) {
        TaskList system = (TaskList) prevalentSystem;
        system.removeTask(task);
    }
}

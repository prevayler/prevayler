/*
 * RemoveTask.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.util.Date;

import org.prevayler.Transaction;

// START SNIPPET: removetask
public class RemoveTask implements Transaction<TaskList> {

    private Task task;

    public RemoveTask(Task task) {
        this.task = task;
    }

    public void executeOn(TaskList prevalentSystem, Date executionTime) {
    	
        prevalentSystem.removeTask(task);
    }
}
// END SNIPPET: removetask

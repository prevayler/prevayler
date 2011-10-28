/*
 * AddTask.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.util.Date;

import org.prevayler.TransactionWithQuery;

// START SNIPPET: addtask
public class AddTask implements TransactionWithQuery<TaskList,Task> {

    private final Task task;

    public AddTask(String description, int priority) {
        this.task = new Task(description, priority);
    }

    public Task executeAndQuery(TaskList prevalentSystem, Date executionTime) throws Exception {
        
    	prevalentSystem.addTask(task);
        return task;
    }
}
// END SNIPPET: addtask

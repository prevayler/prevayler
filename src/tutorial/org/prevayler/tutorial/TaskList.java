/*
 * TaskList.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple list of tasks to be performed.
 * 
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class TaskList implements Serializable {

    private List tasks = new ArrayList();

    /**
     * @param task
     *            the task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * @param task
     *            the task to remove.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * @return
     */
    public List getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}

/*
 * Task.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.io.Serializable;

/**
 * A simple task to be performed.
 * 
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class Task implements Serializable {

    public static final int MAX_PRIORITY = 10;
    public static final int MED_PRIORITY = 5;
    public static final int MIN_PRIORITY = 0;

    private String description;
    private int priority;

    /**
     * Creates a new Task.
     */
    public Task() {
    }

    /**
     * Creates a new Task.
     * 
     * @param description
     *            description of this task.
     * @param priority
     *            priority of this task.
     */
    public Task(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority
     *            The priority to set.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            Task t = (Task) obj;
            return (String.valueOf(description).equals(String
                    .valueOf(t.description)))
                    && (priority == t.priority);
        }
        return false;
    }
}

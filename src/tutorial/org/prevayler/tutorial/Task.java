/*
 * Task.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.io.Serializable;

// START SNIPPET: task
public class Task implements Serializable {

    // END SNIPPET: task

    public static final int MAX_PRIORITY = 10;

    public static final int MED_PRIORITY = 5;

    public static final int MIN_PRIORITY = 0;

    // START SNIPPET: fields
    private String description;

    private int priority;

    // END SNIPPET: fields

    // START SNIPPET: ctors
    public Task() {
    }

    public Task(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }

    //  END SNIPPET: ctors

    // START SNIPPET: equals
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            Task t = (Task) obj;
            return (String.valueOf(description).equals(String.valueOf(t.description))) && (priority == t.priority);
        }
        return false;
    }

    // END SNIPPET: equals

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

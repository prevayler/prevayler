/*
 * Main.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.util.Iterator;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

/**
 * 2-Minute Tutorial Main-Class.
 * 
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // START SNIPPET: creating
        // Let's create a new TaskList. This is our 'prevalent system'.
        TaskList list = new TaskList();

        // Create a new prevayler. /tasklist-base is the tx-log directory.
        Prevayler prevayler = PrevaylerFactory.createPrevayler(list, "/tasklist-base");

        /*
         * IMPORTANT: Your prevalent system is going to be empty after a
         * snapshot if you don't reassign it here.
         */
        list = (TaskList) prevayler.prevalentSystem();
        // END SNIPPET: creating

        System.out.println("Tasks: " + list.getTasks().size() + ", adding ");

        // START SNIPPET: adding
        Task dishes = (Task) prevayler.execute(new AddTask("do the dishes", Task.MAX_PRIORITY));
        Task dog = (Task) prevayler.execute(new AddTask("walk the dog", Task.MED_PRIORITY));
        Task laundry = (Task) prevayler.execute(new AddTask("do the laundry", Task.MIN_PRIORITY));
        // END SNIPPET: adding

        // START SNIPPET: iterating
        for (Iterator i = list.getTasks().iterator(); i.hasNext();) {
            Task t = (Task) i.next();
            System.out.println("Task: " + t.getDescription() + ", " + t.getPriority());
        }
        // END SNIPPET: iterating

        System.out.println("Tasks: " + list.getTasks().size() + ", removing...");

        // START SNIPPET: removing
        prevayler.execute(new RemoveTask(dishes));
        prevayler.execute(new RemoveTask(dog));
        prevayler.execute(new RemoveTask(laundry));
        // END SNIPPET: removing
        
        System.out.println("Tasks: " + list.getTasks().size());

        // START SNIPPET: snapshotting
        prevayler.takeSnapshot();
        // END SNIPPET: snapshotting
    }
}

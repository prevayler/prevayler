/*
 * Main.java
 * Copyright (c) 2004 The Prevayler Team
 * 
 * See distributed LICENSE file for licensing details.
 */
package org.prevayler.tutorial;

import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

/**
 * 2-Minute Tutorial Main-Class.
 * 
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class Main {

    public static void main(String[] args) throws IOException,
            ClassNotFoundException {

        // Let's create a new TaskList. This is our 'prevalent system'.
        TaskList list = new TaskList();

        // Create a new prevayler. /tasklist-base is the tx-log directory.
        Prevayler prevayler = PrevaylerFactory.createPrevayler(list,
                "/tasklist-base");

        /*
         * IMPORTANT: Your prevalent system is going to be empty after a
         * snapshot if you don't reassign it here.
         */
        list = (TaskList) prevayler.prevalentSystem();

        // create some tasks...
        Task dishes = new Task("do the dishes", Task.MAX_PRIORITY);
        Task dog = new Task("walk the dog", Task.MED_PRIORITY);        
        Task laundry = new Task("do the laundry", Task.MIN_PRIORITY);

        System.out.println("Tasks: " + list.getTasks().size());
        
        prevayler.execute(new AddTask(dishes));
        prevayler.execute(new AddTask(dog));
        prevayler.execute(new AddTask(laundry));

        System.out.println("Tasks: " + list.getTasks().size());
        prevayler.execute(new RemoveTask(dishes));
        prevayler.execute(new RemoveTask(dog));
        prevayler.execute(new RemoveTask(laundry));

        System.out.println("Tasks: " + list.getTasks().size());

        prevayler.takeSnapshot();
    }
}

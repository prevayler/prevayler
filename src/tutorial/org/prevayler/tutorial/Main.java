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
 * TODO: Document
 *
 * @author Carlos Villela
 * @since Mar 7, 2004
 */
public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        TaskList list = new TaskList();
        Prevayler prevayler = PrevaylerFactory.createPrevayler(list, "/tasklist-base");
        
        Task dishes = new Task("do the dishes", Task.MAX_PRIORITY);

        System.out.println("Tasks: " + list.getTasks().size());
        prevayler.execute(new AddTask(dishes));

        System.out.println("Tasks: " + list.getTasks().size());
        prevayler.execute(new RemoveTask(dishes));

        System.out.println("Tasks: " + list.getTasks().size());
    }
}

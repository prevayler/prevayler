/*
 * Created on 24/08/2002
 *
 * Prevayler(TM) - The Open-Source Prevalence Layer.
 * Copyright (C) 2001 Carlos Villela.
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package org.prevayler.demos.jxpath.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a simple software project.
 * 
 * @author Carlos Villela
 */
public class Project
    implements Serializable {

    private int id;
    private String name;
    private List tasks;

    /**
     * Creates a new Project object.
     */
    public Project() {
        tasks = new ArrayList();
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the tasks.
     * @return Task[]
     */
    public List getTasks() {

        return tasks;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the tasks.
     * @param tasks The tasks to set
     */
    public void setTasks(List tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the id.
     * @return int
     */
    public int getId() {

        return id;
    }

    /**
     * Sets the id.
     * @param id The id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Project Id: " + id
             + "\n      Name: " + name
             + "\n     Tasks:...\n" + tasks;
    }
}

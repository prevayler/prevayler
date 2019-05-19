/*
 * Created on 24/08/2002
 *
 * Prevayler(TM) - The Open-Source Prevalence Layer.
 * Copyright (C) 2001 Carlos Villela.
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package org.prevayler.demos.jxpath.commands;

import org.prevayler.Transaction;
import org.prevayler.demos.jxpath.ProjectManagementSystem;
import org.prevayler.demos.jxpath.model.Project;
import org.prevayler.demos.jxpath.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Carlos Villela
 */
public class AddTask implements Transaction<ProjectManagementSystem> {

  private static final long serialVersionUID = 8755505355908225763L;
  private Task task;
  private int projectId = Integer.MIN_VALUE;

  public void executeOn(ProjectManagementSystem system, Date ignored) {
    if (task != null && projectId != Integer.MIN_VALUE) {

      List<Project> projects = system.getProjects();
      Iterator<Project> i = projects.iterator();
      while (i.hasNext()) {
        Project p = i.next();
        if (p.getId() == projectId) {
          List<Task> tasks = p.getTasks();
          if (tasks == null) {
            tasks = new ArrayList<Task>();
          }
          tasks.add(task);
          p.setTasks(tasks);
          break;
        }
      }
    } else {
      throw new RuntimeException("No project to add -- please call setProject()");
    }
  }

  /**
   * Returns the projectId.
   *
   * @return int
   */
  public int getProjectId() {

    return projectId;
  }

  /**
   * Returns the task.
   *
   * @return Task
   */
  public Task getTask() {

    return task;
  }

  /**
   * Sets the projectId.
   *
   * @param projectId The projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * Sets the task.
   *
   * @param task The task to set
   */
  public void setTask(Task task) {
    this.task = task;
  }
}
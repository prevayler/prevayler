/*
 * Created on 24/08/2002
 *
 * Prevayler(TM) - The Open-Source Prevalence Layer.
 * Copyright (C) 2001 Carlos Villela.
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */

package org.prevayler.demos.jxpath.commands;

import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.demos.jxpath.ProjectManagementSystem;
import org.prevayler.demos.jxpath.model.Project;

/**
 * Adds a Project to the system.
 * 
 * @author Carlos Villela
 */
public class AddProject implements Transaction {

	private Project project;

	public void executeOn(Object system, Date ignored) {
		if(project != null) {
			ProjectManagementSystem pms = (ProjectManagementSystem) system;	
			pms.getProjects().add(project);
		} else {
			throw new RuntimeException("No project to add -- please call setProject()");
		}
	}

	/**
	 * Returns the project.
	 * @return Project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 * @param project The project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

}

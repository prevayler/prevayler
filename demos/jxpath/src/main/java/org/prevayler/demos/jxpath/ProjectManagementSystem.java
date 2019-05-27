/*
 * Created on 24/08/2002
 *
 * Prevayler(TM) - The Open-Source Prevalence Layer.
 * Copyright (C) 2001 Carlos Villela.
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */

package org.prevayler.demos.jxpath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.prevayler.demos.jxpath.model.Project;

/**
 * @author Carlos Villela
 */
public class ProjectManagementSystem implements Serializable {
  private static final long serialVersionUID = -7896364970243693794L;
  private List<Project> projects;

  public ProjectManagementSystem() {
    projects = new ArrayList<Project>();
  }

  public List<Project> getProjects() {
    return projects;
  }
}

/*
 * Created on 24/08/2002
 *
 * Prevayler(TM) - The Open-Source Prevalence Layer.
 * Copyright (C) 2001 Carlos Villela.
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package org.prevayler.demos.jxpath;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.commons.jxpath.JXPathContext;
import org.prevayler.demos.jxpath.commands.AddProject;
import org.prevayler.demos.jxpath.commands.AddTask;
import org.prevayler.demos.jxpath.model.Project;
import org.prevayler.demos.jxpath.model.Task;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;


/**
 * Main class for the JXPath+Prevayler demo.
 * 
 * @author Carlos Villela
 */
public class Main {

	/**
	 * Shows usage information.
	 */
	public static void usage() {
		System.out.println(
			"Usage: Main <list|proj|task|snap> <parameters>\n\n"
				+ "Parameters:\n"
				+ "    list:   <xpath expression>\n"
				+ "    proj:   <id> <name>\n"
				+ "    task:   <id> <name> <start> <end> <projectId>\n"
				+ "    snap\n\n"
				+ "Note: dates should be entered in a locale-sensitive format (your locale is "
				+ System.getProperty("user.language")
				+ "_"
				+ System.getProperty("user.country")
				+ ").");
		System.exit(0);
	}

	/**
	 * Main method -- please call me :)
	 * 
	 * @param args command-line parameters
	 */
	public static void main(String[] args) {

		try {

			Prevayler prevayler =
				PrevaylerFactory.createPrevayler(new ProjectManagementSystem(), "demoJXPath");

			if (args.length < 1) {
				usage();
			} else if ("list".equalsIgnoreCase(args[0]) && args.length >= 2) {
				list(prevayler, args[1]);
			} else if ("proj".equalsIgnoreCase(args[0]) && args.length >= 3) {
				addProject(prevayler, args[1], args[2]);
			} else if ("task".equalsIgnoreCase(args[0]) && args.length >= 6) {
				addTask(prevayler, args[1], args[2], args[3], args[4], args[5]);
			} else if ("snap".equalsIgnoreCase(args[0])) {
				prevayler.takeSnapshot();
			} else {
				usage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lists an object graph using an XPath expression.
	 * 
	 * @param prevayler PrevalentSystem to query
	 * @param xpathExp XPath expression to use
	 */
	private static void list(Prevayler prevayler, String xpathExp) {
		System.out.println("Executing XPath expression...");

		ProjectManagementSystem pms =
			(ProjectManagementSystem) prevayler.prevalentSystem();
		JXPathContext context = JXPathContext.newContext(pms);
		Iterator i = context.iterate(xpathExp);

		while (i.hasNext()) {

			Object obj = (Object) i.next();
			System.out.println(obj.toString());
		}
	}

	/**
	 * Adds a project.
	 * 
	 * @param prevayler PrevalentSystem to change
	 * @param id id of the project
	 * @param name name of the project
	 */
	private static void addProject(
		Prevayler prevayler,
		String id,
		String name)
		throws Exception {
		System.out.println(
			"Adding project '" + name + "' (id '" + id + "')...");

		Project p = new Project();
		p.setId(Integer.parseInt(id));
		p.setName(name);

		AddProject cmd = new AddProject();
		cmd.setProject(p);
		prevayler.execute(cmd);
	}

	/**
	 * Adds a task.
	 * 
	 * @param prevayler PrevalentSystem to change
	 * @param id id of the tasl
	 * @param name name of the task
	 * @param start start date of the task
	 * @param end end date of the task
	 * @param projectId project id to add this task to
	 */
	private static void addTask(
		Prevayler prevayler,
		String id,
		String name,
		String start,
		String end,
		String projectId)
		throws Exception {
		System.out.println(
			"Adding task '" + id + "' to project '" + projectId + "'...");

		Task t = new Task();
		t.setId(Integer.parseInt(id));
		t.setName(name);
		t.setStart(SimpleDateFormat.getInstance().parse(start));
		t.setEnd(SimpleDateFormat.getInstance().parse(end));
		System.out.println("Start: " + t.getStart());
		System.out.println("End:   " + t.getEnd());

		AddTask cmd = new AddTask();
		cmd.setTask(t);
		cmd.setProjectId(Integer.parseInt(projectId));
		prevayler.execute(cmd);
	}
}
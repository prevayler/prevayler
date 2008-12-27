/*
 * Created on 24/08/2002
 *
 * Prevayler(TM) - The Open-Source Prevalence Layer.
 * Copyright (C) 2001 Carlos Villela.
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */

package org.prevayler.demos.jxpath.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a project's task to be completed
 * 
 * @author Carlos Villela
 */
public class Task implements Serializable {

	private int id;
	private String name;
	private Date start;
	private Date end;

	/**
	 * Returns the end.
	 * @return Date
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the start.
	 * @return Date
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Sets the end.
	 * @param end The end to set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the start.
	 * @param start The start to set
	 */
	public void setStart(Date start) {
		this.start = start;
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
            return "\n   Task Id: " + id
                 + "\n      Name: " + name
                 + "\nDate start: " + start
                 + "\n  Date end: " + end + "\n";
        }
}

package org.prevayler.socketserver.example.server;

/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.Serializable;

/**
 * A Todo business object
 * @author djo
 */
public class Todo implements Serializable {

    public Todo(int id) {
        this.id = id;
        desc = "";
    }
    
    private int id;

    /**
     * Returns the id.
     * @return int
     */
    public int getId() {
        return id;
    }

    private String desc;

	/**
	 * Returns the desc.
	 * @return String
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Sets the desc.
	 * @param desc The desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

}

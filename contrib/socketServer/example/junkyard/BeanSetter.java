package junkyard;

/*
 * prevayler.socketServer, a socket-based server (and client library)
 * to help create client-server Prevayler applications
 * 
 * Copyright (C) 2003 Advanced Systems Concepts, Inc.
 * 
 * Written by David Orme <daveo@swtworkbench.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.beans.PropertyDescriptor;
import java.util.Date;

import org.prevayler.socketserver.transactions.RemoteTransaction;

/**
 * A generic parent Transaction for setting properties of beans.
 * 
 * @author djo
 */
public abstract class BeanSetter extends RemoteTransaction {

    // Declare instance fields.  All subclasses should treat these fields
    // as read-only.
    protected int id;
    protected String field;
    protected Object value;
    
	/**
	 * Method BeanSetter.  Construct a BeanSetter transasction.
	 * @param id
	 * @param field
	 * @param value
	 */
    public BeanSetter(int id, String field, Object value) {
        this.id = id;
        this.field = field;
        this.value = value;
    }

    // Declare the static cache for the PropertyDescriptor info
    // This object must be initialized in the static initializer of child classes
    protected static PropertyDescriptor[] propertyDescriptors;

	/**
	 * Method callSetter.
	 * @param object
	 * @param field
	 * @param value
	 * @throws Exception
	 */
    protected void callSetter(Object object) throws Exception {
        for (int i = 0; i < propertyDescriptors.length; i++) {
            if (propertyDescriptors[i].getName().equals(field)) {
                propertyDescriptors[i].getWriteMethod().invoke(object, new Object[] {value});
                break;
            }
        }
    }
    
    /**
     * Look-up the object on which the property value will be set
     * @param prevalentSystem the system on which to look up the object
     */
    protected abstract Object lookup(Object prevalentSystem) throws Exception;
    
    /**
	 * @see org.prevayler.util.TransactionWithQuery#executeAndQuery(Object, Date)
	 */
	public Object executeAndQuery(Object prevalentSystem, Date timestamp) throws Exception {
        Object subject = lookup(prevalentSystem);
        callSetter(subject);
		return null;
	}
}


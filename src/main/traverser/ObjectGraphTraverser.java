/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */

package traverser;

import java.lang.reflect.*;
import java.util.*;

public class ObjectGraphTraverser {
    
    public interface Visitor {
        boolean visit(ObjectPath path);
    }

	protected final LinkedList<ObjectPath> _queue = new LinkedList<ObjectPath>();
    private Set<Object> _alreadyVisited = new HashSet<Object>();

	public void traverse(Object rootObject, Visitor visitor) {
		queueUpForTraversing(new ObjectPath(rootObject));
		while (!_queue.isEmpty())
			traverseObject(_queue.removeFirst(), visitor);
	}

	protected void traverseObject(ObjectPath path, Visitor visitor) {
        Object object = path._object;
        
        if (_alreadyVisited.contains(object)) return;
        _alreadyVisited.add(object);
        
        if (!visitor.visit(path)) return;

        Class clazz = object.getClass();
		traverseFields(path, clazz);
	}

	protected void traverseFields(ObjectPath path, Class clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (isStatic(field)) continue;
			field.setAccessible(true);

			Object object = path._object;
			Object value = getValue(field, object);
			queueUpForTraversing(path.createBranch(field.getName(), value));
		}

		Class superclass = clazz.getSuperclass();
		if (superclass == null) return;
		traverseFields(path, superclass);
	}

    private boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

	private Object getValue(Field field, Object object) {
		try {
			return field.get(object);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception thrown.", e);
		}
	}

	protected void traverseArray(ObjectPath path, Object array) {
		for (int i = 0; i < Array.getLength(array); i++) {
			queueUpForTraversing(path.createBranch("" + i, Array.get(array, i)));
		}
	}

	protected void queueUpForTraversing(ObjectPath path) {
		Object object = path._object;
		
		if (object == null) return;
        Class clazz = object.getClass();
		if (isSecondClass(clazz)) return;

        if (clazz.isArray()) {
            traverseArray(path, object);
            return;
        }

        _queue.add(path);
	}

    protected boolean isSecondClass(Class clazz){
        if (clazz.isPrimitive()) return true;
//        if (clazz == Date.class) return true;
//        if (clazz == String.class) return true;
        return clazz.isArray() && isSecondClass(clazz.getComponentType());
    }

}
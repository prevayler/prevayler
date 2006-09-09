// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package traverser.demo;

import traverser.*;
import traverser.ObjectGraphTraverser.*;

import java.util.*;

public class ClassCountingVisitor implements Visitor {

    private Map<Class, Long> _countByClass = new HashMap<Class, Long>();

    public boolean visit(ObjectPath path) {
        System.out.println(path);
        Class clazz = path._object.getClass();
        _countByClass.put(clazz, countFor(clazz) + 1);
        return true;
    }

    private long countFor(Class clazz) {
        Long result = _countByClass.get(clazz);
        if (result == null) return 0;
        return result;
    }
    
    public void printOut() {
        System.out.println("\n\nInstance Count by Class:\n");
        for (Class clazz : _countByClass.keySet())
            System.out.println(clazz.getName() + ": " + _countByClass.get(clazz));
    }

}

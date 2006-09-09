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

import java.util.*;

public class Main {

    public static void main(String[] args) {
        ClassCountingVisitor visitor = new ClassCountingVisitor();
        new ObjectGraphTraverser().traverse(exampleGraph(), visitor);
        visitor.printOut();
    }

    private static Object exampleGraph() {
        HashSet<Object> result = new HashSet<Object>();
        result.add(new Object());
        result.add(new Object[]{new Object(), new LinkedList(), new Random(), new Date(), new GregorianCalendar(), "foo", "bar"});
        return result;
    }

}

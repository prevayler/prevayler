package org.prevayler.baptism;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import junit.framework.TestCase;

/**
 * Shows how the baptism problem works. Used in the documentation.
 * 
 * @author Carlos Villela
 * @since Apr 5, 2004
 */
public class BaptismProblemTest extends TestCase {

    public void testThatSerializationNeverSerializesTheSameObjectAgain()
            throws IOException, ClassNotFoundException {

        // START SNIPPET: ex1

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(stream);

        Calendar c = Calendar.getInstance(); // a mutable object

        out.writeObject(c); // object is written to the stream
        out.writeObject(c); // object is already written; this references it
        c.setTimeInMillis(0); // mutate the object's state
        out.writeObject(c); // object, again, is referenced

        // END SNIPPET: ex1

        // START SNIPPET: ex2
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
                stream.toByteArray()));

        Calendar c1 = (Calendar) in.readObject();
        Calendar c2 = (Calendar) in.readObject();
        Calendar c3 = (Calendar) in.readObject();
        // END SNIPPET: ex2

        // START SNIPPET: ex3
        assertSame(c1, c2);
        assertSame(c2, c3);
        
        assertFalse(c.equals(c1));
        assertFalse(c.equals(c2));
        assertFalse(c.equals(c3));
        
        assertFalse(0 == c3.getTimeInMillis());
        // END SNIPPET: ex3
    }
}
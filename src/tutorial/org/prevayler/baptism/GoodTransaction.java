package org.prevayler.baptism;

import java.util.Calendar;
import java.util.Date;

import org.prevayler.Transaction;

/**
 * Simple transaction that demonstrates how to avoid the baptism problem.
 * 
 * @author Carlos Villela
 * @since Apr 6, 2004
 */
// START SNIPPET: good
public class GoodTransaction implements Transaction {

    private long millis;
    private String id;

    public GoodTransaction(String id, long millis) {
        this.id = id;
        this.millis = millis;
    }

    public void executeOn(Object prevalentSystem, Date executionTime) {
        MyPrevalentSystem system = (MyPrevalentSystem) prevalentSystem;
        Calendar c = system.lookupCalendar(this.id);
        c.setTimeInMillis(this.millis);
    }

}
//END SNIPPET: good

// this is here just to get the example to compile.
class MyPrevalentSystem {
    public Calendar lookupCalendar(String id) {
        return null;
    }
}
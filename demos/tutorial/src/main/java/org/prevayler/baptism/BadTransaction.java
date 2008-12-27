package org.prevayler.baptism;

import java.util.Calendar;
import java.util.Date;

import org.prevayler.Transaction;

/**
 * Simple transaction that demonstrates how to fall into the baptism problem.
 * 
 * @author Carlos Villela
 * @since Apr 6, 2004
 */
// START SNIPPET: bad
public class BadTransaction implements Transaction {

    private Calendar calendar;

    private long millis;

    public BadTransaction(Calendar calendar, long millis) {
        this.calendar = calendar;
        this.millis = millis;
    }

    public void executeOn(Object prevalentSystem, Date executionTime) {
        calendar.setTimeInMillis(this.millis);
    }
}
// END FSNIPPET: bad

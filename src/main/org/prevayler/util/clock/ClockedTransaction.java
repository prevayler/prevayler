package org.prevayler.util.clock;

import java.util.Date;
import java.io.ObjectOutputStream;
import java.io.IOException;

import org.prevayler.util.clock.ClockedSystem;
import org.prevayler.util.TransactionWithQuery;
import org.prevayler.Prevayler;

public abstract class ClockedTransaction extends TransactionWithQuery {
    static final long serialVersionUID = -7058059425197071976L;

    private Date timeOfExecution;

    protected final Object executeAndQuery(Object prevalentSystem) throws Exception {
        ClockedSystem clockedSystem = (ClockedSystem) prevalentSystem;
        clockedSystem.advanceClockTo(timeOfExecution != null ? timeOfExecution : new Date());
        return executeClocked(clockedSystem);
    }

    protected abstract Object executeClocked(ClockedSystem clockedSystem) throws Exception;

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (timeOfExecution == null) {
            timeOfExecution = new Date();
        }
        out.defaultWriteObject();
    }
}

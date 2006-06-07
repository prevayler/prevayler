package org.prevayler.implementation;

import org.prevayler.Query;

import java.util.Date;

public class NullQuery implements Query {

    public Object query(Object prevalentSystem, Date executionTime) throws Exception {
        return null;
    }

}

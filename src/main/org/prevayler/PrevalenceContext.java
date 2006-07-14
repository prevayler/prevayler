// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PrevalenceContext<S> {

    private final S _prevalentSystem;

    private final Date _executionTime;

    private final long _systemVersion;

    private final boolean _readOnly;

    private List<Object> _events;

    public PrevalenceContext(S prevalentSystem, Date executionTime, long systemVersion, boolean readOnly) {
        _prevalentSystem = prevalentSystem;
        _executionTime = executionTime;
        _systemVersion = systemVersion;
        _readOnly = readOnly;
        _events = null;
    }

    public S prevalentSystem() {
        return _prevalentSystem;
    }

    /**
     * The time at which the current transaction is being executed. Every
     * transaction executes completely within a single moment in time.
     * Logically, a prevalent system's time does not pass during the execution
     * of a Transaction.
     */
    public Date executionTime() {
        return _executionTime;
    }

    public long executionTimeMillis() {
        return _executionTime.getTime();
    }

    public long systemVersion() {
        return _systemVersion;
    }

    public boolean readOnly() {
        return _readOnly;
    }

    /**
     * Trigger an event to be dispatched after the current transaction
     * completes.
     */
    public <E> void addEvent(E event) {
        if (_events == null) {
            _events = new ArrayList<Object>();
        }
        _events.add(event);
    }

    public List<Object> events() {
        return _events == null ? Collections.emptyList() : Collections.unmodifiableList(_events);
    }

}

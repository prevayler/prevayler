// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;

import java.util.Date;

public class QueryingClock implements Clock {

    private final Prevayler<?> _prevayler;

    public QueryingClock(Prevayler<?> prevayler) {
        _prevayler = prevayler;
    }

    public Date time() {
        return _prevayler.execute(new ClockQuery());
    }

}

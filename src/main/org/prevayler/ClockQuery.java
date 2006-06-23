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

import java.util.Date;

@ReadOnly public class ClockQuery implements Transaction<Object, Date, RuntimeException> {

    public Date executeOn(@SuppressWarnings("unused") Object prevalentSystem, Date executionTime) {
        return executionTime;
    }

}

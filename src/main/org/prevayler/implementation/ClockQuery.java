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

import static org.prevayler.Safety.READ_ONLY;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

import java.util.Date;

@Safety(READ_ONLY) public class ClockQuery implements GenericTransaction<Object, Date, RuntimeException> {

    public Date executeOn(@SuppressWarnings("unused") Object prevalentSystem, PrevalenceContext prevalenceContext) {
        return prevalenceContext.executionTime();
    }

}

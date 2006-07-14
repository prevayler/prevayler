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

import static org.prevayler.Safety.Journaling.TRANSIENT;
import static org.prevayler.Safety.Locking.NONE;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

@Safety(journaling = TRANSIENT, locking = NONE) public class NullQuery implements GenericTransaction<Object, Void, RuntimeException> {

    public Void executeOn(@SuppressWarnings("unused") PrevalenceContext<?> prevalenceContext) {
        return null;
    }

}
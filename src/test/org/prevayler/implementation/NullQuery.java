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

import static org.prevayler.Safety.Level.LEVEL_1_SHARED_LOCKING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

@Safety(LEVEL_1_SHARED_LOCKING) public class NullQuery implements GenericTransaction<Object, Void, RuntimeException> {

    public Void executeOn(@SuppressWarnings("unused") Object prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        return null;
    }

}

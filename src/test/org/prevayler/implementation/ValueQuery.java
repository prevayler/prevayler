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

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.ReadOnly;

@ReadOnly public class ValueQuery implements GenericTransaction<AppendingSystem, String, RuntimeException> {

    public String executeOn(AppendingSystem prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        return prevalentSystem.value();
    }

}

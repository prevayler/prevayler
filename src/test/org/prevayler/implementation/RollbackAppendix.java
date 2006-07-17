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

import org.prevayler.*;
import org.prevayler.demos.*;

import java.io.*;

@Rollback class RollbackAppendix implements GenericTransaction<AppendingSystem, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String appendix;

    RollbackAppendix(String appendix) {
        this.appendix = appendix;
    }

    public Void executeOn(PrevalenceContext<? extends AppendingSystem> prevalenceContext) {
        prevalenceContext.prevalentSystem().append(appendix);
        return null;
    }

}

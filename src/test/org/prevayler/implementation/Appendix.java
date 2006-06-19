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

import org.prevayler.Transaction;

import java.util.Date;

class Appendix implements Transaction {

    private static final long serialVersionUID = 7925676108189989759L;

    private final String appendix;

    public void executeOn(Object prevalentSystem, Date ignored) {
        ((AppendingSystem) prevalentSystem).append(appendix);
    }

    Appendix(String appendix) {
        this.appendix = appendix;
    }
}

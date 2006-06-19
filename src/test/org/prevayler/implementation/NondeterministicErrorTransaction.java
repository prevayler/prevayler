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

import java.util.Date;

public class NondeterministicErrorTransaction extends Appendix {

    private static final long serialVersionUID = 1L;

    private static int _timeToDetonation = 0;

    public static synchronized void armBomb(int timeToDetonation) {
        _timeToDetonation = timeToDetonation;
    }

    private static synchronized void triggerBomb() {
        if (_timeToDetonation > 0 && --_timeToDetonation == 0) {
            throw new Bomb("BOOM!");
        }
    }

    public NondeterministicErrorTransaction(String toAdd) {
        super(toAdd);
    }

    public void executeOn(Object prevalentSystem, Date executionTime) {
        triggerBomb();
        super.executeOn(prevalentSystem, executionTime);
    }

}

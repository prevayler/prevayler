// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.clock;

import java.util.Date;

/**
 * A Clock that uses the local machine clock (System.currentTimeMillis()) as its
 * time source.
 */
public class MachineClock extends BrokenClock {

    /**
     * @return The local machine time.
     */
    @Override public synchronized Date time() {
        update();
        return super.time();
    }

    private synchronized void update() {
        long newTime = System.currentTimeMillis();
        if (newTime != _millis)
            advanceTo(new Date(newTime));
    }

}

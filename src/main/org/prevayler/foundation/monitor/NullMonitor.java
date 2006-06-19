// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.monitor;

import java.io.File;

/**
 * A Null Monitor, that does no logging at all.
 */
public class NullMonitor implements Monitor {

    /**
     * Does nothing.
     */
    public void notify(Class clazz, String message, File file, Exception exception) {
    }

    /**
     * Does nothing.
     */
    public void notify(Class clazz, String message) {
    }

    /**
     * Does nothing.
     */
    public void notify(Class clazz, String message, Exception ex) {
    }

    /**
     * Does nothing.
     */
    public void notify(Class clazz, String message, File file) {
    }

}

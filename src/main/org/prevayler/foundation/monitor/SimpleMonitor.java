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

import java.io.PrintStream;

/**
 * A Monitor that logs output to a PrintStream (System.out by default).
 */
public class SimpleMonitor extends LoggingMonitor {

    private final PrintStream _stream;

    public SimpleMonitor() {
        this(System.out);
    }

    /**
     * @param stream
     *            The stream to be used for logging.
     */
    public SimpleMonitor(PrintStream stream) {
        _stream = stream;
    }

    protected void info(Class clazz, String message) {
        _stream.println("\n" + message);
    }

    protected void error(Class clazz, String message, Exception ex) {
        _stream.println("\n" + message);
        ex.printStackTrace(_stream);
    }
}

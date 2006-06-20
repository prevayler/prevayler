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
 * Abstract Monitor for Logging implementation monitors to extend.
 */
public abstract class LoggingMonitor implements Monitor {

    public void notify(Class clazz, String message) {
        if (isInfoEnabled(clazz))
            info(clazz, message);
    }

    public void notify(Class clazz, String message, Exception ex) {
        error(clazz, message, ex);
    }

    public void notify(Class clazz, String message, File file) {
        if (isInfoEnabled(clazz))
            info(clazz, message + "\nFile: " + file);
    }

    public void notify(Class clazz, String message, File file, Exception ex) {
        error(clazz, message + "\nFile: " + file, ex);
    }

    protected abstract void info(Class clazz, String Message);

    protected abstract void error(Class clazz, String message, Exception ex);

    /**
     * default returns true. Override as needed.
     */
    protected boolean isInfoEnabled(@SuppressWarnings("unused") Class clazz) {
        return true;
    }
}

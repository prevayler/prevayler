// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation;

import java.io.*;

public class InvalidDirectoryException extends IOException {

    private static final long serialVersionUID = 1L;

    public InvalidDirectoryException() {
        super();
    }

    public InvalidDirectoryException(String message) {
        super(message);
    }

    public InvalidDirectoryException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    public InvalidDirectoryException(Throwable cause) {
        super();
        initCause(cause);
    }

}

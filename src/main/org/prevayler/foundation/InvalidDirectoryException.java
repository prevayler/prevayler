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

import org.prevayler.PrevaylerIOException;

public class InvalidDirectoryException extends PrevaylerIOException {

    private static final long serialVersionUID = 1L;

    public InvalidDirectoryException() {
        super();
    }

    public InvalidDirectoryException(String message) {
        super(message);
    }

    public InvalidDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDirectoryException(Throwable cause) {
        super(cause);
    }

}

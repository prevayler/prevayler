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

import org.prevayler.PrevalenceError;

public class ErrorInEarlierTransactionError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public ErrorInEarlierTransactionError() {
        super();
    }

    public ErrorInEarlierTransactionError(String message) {
        super(message);
    }

    public ErrorInEarlierTransactionError(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorInEarlierTransactionError(Throwable cause) {
        super(cause);
    }

}

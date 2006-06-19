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

import java.io.IOException;
import java.io.OutputStream;

public abstract class Guided {

    private final Turn _turn;

    protected Guided(Turn turn) {
        _turn = turn;
    }

    public void startTurn() {
        _turn.start();
    }

    public void endTurn() {
        _turn.end();
    }

    public void abortTurn(String message, Throwable cause) {
        _turn.abort(message, cause);
    }

    public abstract void writeTo(OutputStream stream) throws IOException;

}

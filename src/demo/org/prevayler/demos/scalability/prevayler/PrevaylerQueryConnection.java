// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.scalability.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.demos.scalability.QueryConnection;

import java.util.List;

class PrevaylerQueryConnection implements QueryConnection {

    private final Prevayler<QuerySystem> prevayler;

    PrevaylerQueryConnection(Prevayler<QuerySystem> prevayler) {
        this.prevayler = prevayler;
    }

    public List queryByName(String name) {
        return prevayler.execute(new QueryByName(name));
    }

}

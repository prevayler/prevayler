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

import static org.prevayler.Safety.READ_ONLY;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

import java.util.List;

@Safety(READ_ONLY) public class QueryByName implements GenericTransaction<QuerySystem, List, RuntimeException> {

    private final String name;

    public QueryByName(String name) {
        this.name = name;
    }

    public List executeOn(QuerySystem prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        return prevalentSystem.queryByName(name);
    }

}

// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler;

import java.util.Date;

/**
 * Represents a query that can be executed on a Prevalent System. This is just a
 * mild convenience, mostly for backward compatibility.
 * 
 * @see org.prevayler.Prevayler#execute(Query)
 */
@SuppressWarnings("deprecation") @Deprecated public interface Query<S, R, E extends Exception> {

    public R query(S prevalentSystem, Date executionTime) throws E;

}

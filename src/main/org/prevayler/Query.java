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

/**
 * Represents a query that can be executed on a Prevalent System. This is just a mild
 * convenience, mostly for backward compatibility. Query is a class, not an interface,
 * because annotations are only inherited from superclasses, not interfaces. 
 * 
 * @see org.prevayler.Prevayler#execute(Transaction)
 */
@ReadOnly public abstract class Query<T, R, E extends Exception> implements Transaction<T, R, E> {
}

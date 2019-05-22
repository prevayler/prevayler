//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.io.Serializable;

/**
 * Formal common base type for {@link Transaction} and {@link TransactionWithQuery}.
 * Used to limit the valid types (via ...<code>? extends TransactionBase</code>...)
 * in classes/methods/parameters referring to either of the two types using generics
 *      
 * @see Transaction
 * @see TransactionWithQuery
 */
public interface TransactionBase extends Serializable {
}

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
 * The same as TransactionWithQuery except it does not throw Exception when
 * executed.
 * 
 * @see Transaction
 */
public interface SureTransactionWithQuery<T, R> extends Transaction<T, R, RuntimeException> {
}

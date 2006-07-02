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
 * A Transaction that also returns a result or throws an Exception after
 * executing. <br>
 * <br>
 * A "PersonCreation" Transaction, for example, may return the Person it
 * created. Without this, to retrieve the newly created Person, the caller would
 * have to issue a Query like: "What was the last Person I created?". <br>
 * <br>
 * Looking at the Prevayler demos is by far the best way to learn how to use
 * this class.
 */
public interface GenericTransaction<S, R, E extends Exception> {

    /**
     * Performs the necessary modifications on the given prevalentSystem and
     * also returns an Object or throws an Exception. This method is called by
     * Prevayler.execute(TransactionWithQuery) to execute this
     * TransactionWithQuery on the given Prevalent System. See
     * org.prevayler.demos for usage examples.
     */
    public R executeOn(S prevalentSystem, PrevalenceContext prevalenceContext) throws E;

}

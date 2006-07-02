// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.journal;

import org.prevayler.implementation.TransactionGuide;
import org.prevayler.implementation.publishing.TransactionSubscriber;

public interface Journal<S> {

    public <R, E extends Exception> void append(TransactionGuide<S, R, E> guide);

    public void update(TransactionSubscriber<S> subscriber, long initialTransaction);

    public void close();

    public long nextTransaction();

}

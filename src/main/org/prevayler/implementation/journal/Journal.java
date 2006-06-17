//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.journal;

import org.prevayler.implementation.TransactionGuide;
import org.prevayler.implementation.publishing.TransactionSubscriber;

public interface Journal {

    public void append(TransactionGuide guide);

    public void update(TransactionSubscriber subscriber, long initialTransaction);

    public void close();

    public long nextTransaction();

}

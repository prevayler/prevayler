/*
 * Copyright (c) 2003 Jay Sachs. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name "Prevayler" nor the names of its contributors
 *    may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.prevayler.contrib.facade;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * A TransactionHint contains hints of other things to do inside the
 * transaction.  For instance, set a timestamp or call some specified
 * method from within the Prevayler transaction.  Basically, this allows
 * for a generic way to specify actions internal to the Prevayler
 * transaction, with full knowledge of being within the context of the
 * Prevayler transaction, all without hardcoding anything in the generic
 * proxy transactions.  Methods that are transactionally proxied don't
 * (and shouldn't) really realize they are part of a transaction so they
 * can't take advantage of certain contextual info such as the
 * Prevayler-managed Date object that is passed to each transaction.
 * This class provides access to that context.
 *
 * <p>Currently there exists only a preExecute() method which executes
 * immediately before the transactionally proxied method is invoked.
 * It might be legitimate to add a postExecute() method, but I haven't
 * found the need for it yet, so I didn't add it.</p>
 *
 * <h3>Caveat:</h3>
 * <p>Could make for non-deterministic behavior if each time
 * the app is started, it has a different transaction hint
 * defined;  maybe one set of transactions gets timestamped,
 * but then another set doesn't.  Is this acceptable?</p>
 * 
 * <h3>Answer:</h3>
 * <p>Well, the same could be said of hard coded transactions.  Transactions
 * could be modified internally, recompiled, and re-run without
 * the application knowing the difference.  The only difference
 * here is the externalization of the issue to a method separate
 * from the transactionally proxied method itself.
 * Might not be a real issue?</p>
 *
 * @since 0_2
 * @author Jacob Kjome [hoju@visi.com]
 */
public interface TransactionHint extends Serializable {

    /**
     * A default empty transaction hint which should be used if
     * no other transaction hint is specified
     */
    public static final TransactionHint NOOP_TRANSACTION_HINT =
        new TransactionHint() {
            public void preExecute(Object p_prevalentSystem, Method p_method, Object[] p_args, Date p_timestamp) throws Exception {} 
        };
        
    /**
     * This method executes just before the transactional method is invoked,
     * so it is a good place to put any generic code that needs to be executed
     * before any transaction
     */
    public void preExecute(Object p_prevalentSystem, Method p_method, Object[] p_args, Date p_timestamp) throws Exception;

}

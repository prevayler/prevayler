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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.prevayler.Prevayler;


/**
 * The main class to create transaction facades.
 *
 * @since 0_1
 * @author Jay Sachs [jay@contravariant.org]
 * @author Jacob Kjome [hoju@visi.com]
 */
public class PrevaylerTransactionsFacade
{

    /**
     * defaults to using a {@link TransactionHint.NOOP_TRANSACTION_HINT}
     */
    public static Object create(Class p_intf, Prevayler p_prevayler)
    {
        return create(p_intf,
                      p_prevayler,
                      TransactionType.SIMPLE_DETERMINER,
                      TransactionHint.NOOP_TRANSACTION_HINT);
    }

    /**
     * @since 0_2
     */
    public static Object create(final Class p_intf,
                                final Prevayler p_prevayler,
                                final TransactionType.Determiner p_determiner,
                                final TransactionHint p_hint)
    {
        return Proxy.newProxyInstance
            (p_intf.getClassLoader(),
             new Class[] { p_intf },
             new InvocationHandler ()
             {
                 public Object invoke(Object p_proxy,
                                      Method p_method,
                                      Object [] p_args)
                     throws Throwable
                 {
                     return p_determiner.determineTransactionType(p_method)
                         .execute(p_prevayler, p_method, p_args, p_hint);
                 }
             });
    }
}

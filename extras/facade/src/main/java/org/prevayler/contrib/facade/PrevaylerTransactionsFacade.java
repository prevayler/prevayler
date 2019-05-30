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

import org.prevayler.Prevayler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * The main class to create transaction facades.
 *
 * @author Jay Sachs [jay@contravariant.org]
 * @author Jacob Kjome [hoju@visi.com]
 * @since 0_1
 */
public class PrevaylerTransactionsFacade {

  /**
   * defaults to using a {@link TransactionHint#NOOP_TRANSACTION_HINT}
   */
  public static <P> P create(Class<P> p_intf, Prevayler<? extends P> p_prevayler) {
    return create(p_intf,
        p_prevayler,
        TransactionType.SIMPLE_DETERMINER,
        TransactionHint.NOOP_TRANSACTION_HINT);
  }

  /**
   * @since 0_2
   */
  public static <P> P create(final Class<P> p_intf,
                             final Prevayler<? extends P> p_prevayler,
                             final TransactionType.Determiner p_determiner,
                             final TransactionHint<? super P> p_hint) {
    return p_intf.cast(Proxy.newProxyInstance
        (p_intf.getClassLoader(),
            new Class<?>[]{p_intf},
            new InvocationHandler() {
              public Object invoke(Object p_proxy,
                                   Method p_method,
                                   Object[] p_args)
                  throws Throwable {
                return p_determiner.determineTransactionType(p_method)
                    .execute(p_prevayler, p_method, p_args, p_hint);
              }
            }));
  }
}

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

import java.lang.reflect.Method;


/**
 * This transaction type determiner uses a combination of heuristics to determine
 * the proper transaction type (in the exact order given below):
 * <ol>
 *   <li>Methods returning void will be of type {@link TransactionType.TRANSACTION} unless the method name contains the word "[t|T]ransient", in which case it will be of type {@link TransactionType.NOOP} (see #3 for description)</li>
 *   <li>Methods starting with (or matching) the prefixes "fetch", "find", "get", and "retrieve" will be of type {@link TransactionType.QUERY}</li>
 *   <li>Methods that fall through check #2, and are all-lowercase, will be of type {@link TransactionType.NOOP}.  This means Prevayler will not be invoked at all, which is useful for cases where a method on the interface such as "root()" provides unsynchronized access to the prevalent system for use by external query mechanisms.  It also avoids double transactions if a transactional method calls the system access method.</li>
 *   <li>Methods that fall through check #3 will be of type {@link TransactionType.TRANSACTION_WITH_QUERY}</li>
 * </ol>
 * 
 * @since 0_1
 * @author Jay Sachs [jay@contravariant.org]
 * @author Jacob Kjome [hoju@visi.com]
 */
public class MethodNameTransactionTypeDeterminer
    implements TransactionType.Determiner
{
    public TransactionType determineTransactionType(Method p_method)
    {
        String name = p_method.getName();
        if (p_method.getReturnType() == Void.TYPE)
        {
            if (name.indexOf("transient") != -1 || name.indexOf("Transient") != -1) {
                //This is a guess, but a reasonable (and documented) one.  See more below about TransactionType.NOOP.
                return TransactionType.NOOP;
            }
            //This is, hardly, a guess but a certainty
            return TransactionType.TRANSACTION;
        }
        else if (name.startsWith("fetch")
            || name.startsWith("find")
            || name.startsWith("get")
            || name.startsWith("retrieve"))
        {
            //This is a guess, but a reasonable (and documented) one
            return TransactionType.QUERY;
        }
        else
        {
            if (name.equals(name.toLowerCase())) {
                //Avoid invoking prevayler at all for methods providing
                //access to the root of the system, useful for external
                //unsynchronized query access and avoiding double transactions
                //in the case that a transactional method calls the system
                //access method (eg. root(), system(), etc...).
                //This is a guess, but a reasonable (and documented) one.
                return TransactionType.NOOP;
            }

            //The catch-all fallback.  Can't go wrong here!
            return TransactionType.TRANSACTION_WITH_QUERY;
        }
    }

}

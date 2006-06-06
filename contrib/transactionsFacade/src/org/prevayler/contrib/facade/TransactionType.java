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

import org.prevayler.Prevayler;


/**
 * A "smart" enumerated type enumerating the three kinds of
 * transactions that Prevayler supports plus one that is,
 * effectively, "no transaction".  Also includes the transaction type
 * <code>Determiner</code> interface and two of the most basic implementations.
 *
 * @since 0_1
 * @author Jay Sachs [jay@contravariant.org]
 * @author Jacob Kjome [hoju@visi.com]
 */
public abstract class TransactionType
{
    /**
     * An interface describing a strategy for choosing a
     * <code>TransactionType</code> for a given method.
     *
     * @since 0_1
     * @author Jay Sachs [jay@contravariant.org]
     */
    public interface Determiner
    {
        /**
         * Given a <code>Method</code>, determine the appropriate
         * <code>TransactionType</code> to be used in implemented a
         * Prevayler for that method.
         *
         * @param p_method the <code>Method</code> in question
         *
         * @return the <code>TransactionType</code> appropriate for
         * that method.
         */
        TransactionType determineTransactionType(Method p_method);
    }

    /**
     * A minimal transaction type determiner which uses no heuristics. It
     * always returns a transaction type of {@link #TRANSACTION_WITH_QUERY}.
     */
    public static final Determiner SIMPLE_DETERMINER = new Determiner() {
    	public TransactionType determineTransactionType(Method p_method)
        {
            return TRANSACTION_WITH_QUERY;
        }
    };

    /**
     * A basic transaction type determiner which bases the transaction type on
     * the return type of methods: {@link #TRANSACTION} for void methods and
     * {@link #QUERY} otherwise.
     */
    public static final Determiner RETURN_TYPE_DETERMINER = new Determiner() {
    	public TransactionType determineTransactionType(Method p_method)
        {
            return (p_method.getReturnType() != Void.TYPE)
                ? QUERY
                : TRANSACTION;
        }
    };

    /**
     * @since 0_2
     */
    public abstract Object execute(Prevayler p_prevayler,
                                   Method p_method,
                                   Object[] p_args,
                                   TransactionHint p_hint)
        throws Exception;

    public String toString()
    {
        return "TransactionType{" + m_name + "}";
    }

    private TransactionType(String p_name)
    {
        m_name = p_name;
    }

    private final String m_name;

    public static final TransactionType QUERY =
        new TransactionType("QUERY")
        {

            /**
             * @since 0_2
             */
            public Object execute(Prevayler p_prevayler,
                                  Method p_method,
                                  Object[] p_args,
                                  TransactionHint p_hint)
                throws Exception
            {
                return p_prevayler.execute(new ProxyQuery(p_method, p_args, p_hint));
            }
        };

    public static final TransactionType TRANSACTION_WITH_QUERY =
        new TransactionType("TRANSACTION_WITH_QUERY")
        {

            /**
             * @since 0_2
             */
            public Object execute(Prevayler p_prevayler,
                                  Method p_method,
                                  Object[] p_args,
                                  TransactionHint p_hint)
                throws Exception
            {
                return p_prevayler.execute
                    (new ProxyTransactionWithQuery(p_method, p_args, p_hint));
            }
        };

    public static final TransactionType TRANSACTION =
        new TransactionType("TRANSACTION")
        {

            /**
             * @since 0_2
             */
            public Object execute(Prevayler p_prevayler,
                                  Method p_method,
                                  Object[] p_args,
                                  TransactionHint p_hint)
                throws Exception
            {
                p_prevayler.execute
                    (new ProxyTransaction(p_method, p_args, p_hint));
                return null;
            }
        };

    /**
     * @since 0_2
     */
    public static final TransactionType NOOP =
        new TransactionType("NOOP")
        {
            public Object execute(Prevayler p_prevayler,
                                  Method p_method,
                                  Object[] p_args,
                                  TransactionHint p_hint)
                throws Exception
            {
                return null;
            }
        };
}
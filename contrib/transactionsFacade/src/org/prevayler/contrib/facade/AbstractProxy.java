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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * @since 0_1
 * @author Jay Sachs [jay@contravariant.org]
 * @author Jacob Kjome [hoju@visi.com]
 */
public abstract class AbstractProxy
    implements Serializable
{

    /**
     * @since 0_2
     */
    protected AbstractProxy(Method p_method, Object[] p_args, TransactionHint p_hint)
    {
        m_methodInfo = new MethodInfo(p_method);
        m_args = p_args;
        m_hint = p_hint;
    }

    private final MethodInfo m_methodInfo;
    private final Object[] m_args;
    private final TransactionHint m_hint;

    /**
     * @since 0_2
     */
    protected Object execute(Object p_prevalentSystem, Date p_timestamp)
        throws Exception
    {
        try
        {
            m_hint.preExecute(p_prevalentSystem, getMethod(), m_args, p_timestamp);
            return getMethod().invoke(p_prevalentSystem, m_args);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (InvocationTargetException e)
        {
            Throwable e2 = e.getTargetException();
            if (e2 instanceof Exception)
            {
                throw (Exception) e2;
            }
            else
            {
                throw (Error) e2;
            }
        }
    }

    private Method getMethod()
        throws Exception
    {
        return m_methodInfo.getMethod();
    }

    private final long serialVersionUID = 0L;
}

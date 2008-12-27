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

import org.prevayler.PrevaylerFactory;
import junit.framework.TestCase;
import java.io.Serializable;


/**
 * @author Jay Sachs [jay@contravariant.org]
 * @author Jacob Kjome [hoju@visi.com]
 */
public class SmokeTest
    extends TestCase
{

    public static class RuntimeOops extends RuntimeException
    {
        private static final long serialVersionUID = 6697889650105894039L;
    }

    public static class CheckedOops extends Exception
    {
        private static final long serialVersionUID = -6402219678153693704L;
    }

    public static interface Repository {
        void storeSomething(String data);
        void oopsRuntime(String data);
        void oopsChecked(String data) throws CheckedOops;
        String getData();
    }

    public static class RepositoryImpl
        implements Repository, Serializable
    {
        private static final long serialVersionUID = -4401226206133056516L;

        public void storeSomething(String p_data)
        {
            m_data = p_data;
        }

        public void oopsRuntime(String p_data)
        {
            m_data = p_data;
            throw new RuntimeOops();
        }

        public void oopsChecked(String p_data)
            throws CheckedOops
        {
            m_data = p_data;
            throw new CheckedOops();
        }

        public String getData()
        {
            return m_data;
        }

        private String m_data;
    }

    public void testAndSeeIfItSmokes()
        throws Exception
    {
        Repository repo = (Repository)
            PrevaylerTransactionsFacade.create
            (Repository.class,
             PrevaylerFactory.createTransientPrevayler(new RepositoryImpl()));

        assertEquals(null, repo.getData());
        final String data = "someData";
        repo.storeSomething(data);
        assertEquals(data, repo.getData());

        try
        {
            repo.oopsRuntime("other data");
            fail("should have thrown RuntimeOops");
        }
        catch (RuntimeOops expected)
        {
            // ok
        }

        assertEquals(data, repo.getData());

        try
        {
            repo.oopsChecked("more other data");
            fail("should have thrown CheckedOops");
        }
        catch (CheckedOops expected)
        {
            //expected.printStackTrace();
            // ok
        }

        //checked exception doesn't trigger rollback
        //it is assumed that checked exceptions are
        //expected and should be delbt with manually
        //where runtime exceptions are unexpected. As
        //such, internal code may not be prepared to
        //deal with them so Prevayler rolls back
        assertEquals("more other data", repo.getData());
    }
}

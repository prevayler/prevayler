// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.gui;

import net.sourceforge.javamatch.engine.MatchEngine;
import net.sourceforge.javamatch.engine.MatchException;
import net.sourceforge.javamatch.engine.MatchResult;
import net.sourceforge.javamatch.engine.ResultItem;
import net.sourceforge.javamatch.query.Maximum;
import net.sourceforge.javamatch.query.QuerySet;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.demos.ReadOnly;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.Bank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ReadOnly public class JavaMatchQuery implements GenericTransaction<Bank, List<Object[]>, MatchException> {

    public List<Object[]> executeOn(Bank prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) throws MatchException {
        List<Object[]> results = new ArrayList<Object[]>();

        // create the match engine
        MatchEngine matchEngine = new MatchEngine();

        // create the match query
        QuerySet query = new QuerySet();
        query.addPreferred(new Maximum("balance()"));
        query.addPreferred(new Maximum("transactionHistory().size()"));

        // execute the match query
        MatchResult matchResult = matchEngine.executeQuery(query, prevalentSystem.accounts());

        // retrieve matching results
        for (Iterator resultIterator = matchResult.getResultIterator(); resultIterator.hasNext();) {
            ResultItem curResultItem = (ResultItem) resultIterator.next();
            Account matchedAccount = (Account) curResultItem.getMatchedObject();
            // create a row in the table that displays the matching results
            Object[] rowData = new Object[4];
            rowData[0] = new Float(curResultItem.getMatchValue());
            rowData[1] = matchedAccount;
            rowData[2] = new Long(matchedAccount.balance());
            rowData[3] = new Integer(matchedAccount.transactionHistory().size());
            results.add(rowData);
        }

        return results;
    }

}

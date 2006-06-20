// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.scalability.jdbc;

import org.prevayler.demos.scalability.TransactionConnection;

public class JDBCTransactionSubject extends JDBCScalabilitySubject<TransactionConnection> {

    public JDBCTransactionSubject(String jdbcDriverClassName, String connectionURL, String user, String password) {
        super(jdbcDriverClassName, connectionURL, user, password);
    }

    public TransactionConnection createTestConnection() {
        return new JDBCTransactionConnection(createConnection());
    }

}

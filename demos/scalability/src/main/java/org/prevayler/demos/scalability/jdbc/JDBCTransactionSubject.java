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

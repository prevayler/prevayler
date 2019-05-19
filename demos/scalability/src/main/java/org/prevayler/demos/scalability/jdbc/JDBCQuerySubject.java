package org.prevayler.demos.scalability.jdbc;

import org.prevayler.demos.scalability.QueryConnection;

public class JDBCQuerySubject extends JDBCScalabilitySubject<QueryConnection> {

  public JDBCQuerySubject(String jdbcDriverClassName, String connectionURL, String user, String password) {
    super(jdbcDriverClassName, connectionURL, user, password);
  }

  public QueryConnection createTestConnection() {
    return new JDBCQueryConnection(createConnection());
  }

}

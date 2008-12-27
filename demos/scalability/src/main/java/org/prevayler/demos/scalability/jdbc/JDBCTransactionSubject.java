package org.prevayler.demos.scalability.jdbc;

public class JDBCTransactionSubject extends JDBCScalabilitySubject {

	public JDBCTransactionSubject(String jdbcDriverClassName, String connectionURL, String user, String password) {
		super(jdbcDriverClassName, connectionURL, user, password);
	}


	public Object createTestConnection() {
		return new JDBCTransactionConnection(createConnection());
	}

}

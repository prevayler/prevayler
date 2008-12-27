package org.prevayler.demos.scalability.jdbc;

public class JDBCQuerySubject extends JDBCScalabilitySubject {

	public JDBCQuerySubject(String jdbcDriverClassName, String connectionURL, String user, String password) {
		super(jdbcDriverClassName, connectionURL, user, password);
	}

	public Object createTestConnection() {
		return new JDBCQueryConnection(createConnection());
	}

}

// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability.jdbc;

import org.prevayler.demos.scalability.*;
import java.sql.*;

// Contributions by Stefan Ortmanns.
abstract class JDBCScalabilitySubject implements ScalabilityTestSubject {

	protected final String connectionURL;
	protected final String user;
	protected final String password;

	{System.gc();}


	protected JDBCScalabilitySubject(String jdbcDriverClassName, String connectionURL, String user, String password) {
		try {
			Class.forName(jdbcDriverClassName);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Exception loading JDBC driver class: " + jdbcDriverClassName);
		}

		this.connectionURL = connectionURL;
		this.user = user;
		this.password = password;
	}

	public String name() {
		return "JDBC";
	}

	public void replaceAllRecords(RecordIterator newRecords) {
		((JDBCScalabilityConnection)createTestConnection()).replaceAllRecords(newRecords);
	}

	protected Connection createConnection() {
		try {

			return DriverManager.getConnection(connectionURL, user, password);

		} catch (SQLException sqlx) {
			sqlx.printStackTrace();
			throw new RuntimeException("Exception while trying to connect: " + sqlx);
		}
	}
}

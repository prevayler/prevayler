// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability.jdbc;

import org.prevayler.demos.scalability.*;
import java.sql.*;


// Contributions by Stefan Ortmanns.
abstract class JDBCScalabilityConnection {

	protected final Connection connection;
	private final PreparedStatement insertStatement;

	protected JDBCScalabilityConnection(Connection connection) {
		this.connection = connection;
		try {
			connection.setAutoCommit(false);
		} catch (SQLException sqlx) {
			dealWithSQLException(sqlx, "setting AutoCommit to false");
		}

		insertStatement = prepare("insert into " + table() + " (ID,NAME,STRING1,BIGDECIMAL1,BIGDECIMAL2,DATE1,DATE2) values(?,?,?,?,?,?,?)");
	}


	protected abstract String table();


	protected void insert(Record recordToInsert) {
		try {
			insertStatement.setLong(1,recordToInsert.getId());
			insertStatement.setString(2,recordToInsert.getName());
			insertStatement.setString(3,recordToInsert.getString1());
			insertStatement.setBigDecimal(4,recordToInsert.getBigDecimal1());
			insertStatement.setBigDecimal(5,recordToInsert.getBigDecimal2());
			insertStatement.setDate(6,new java.sql.Date(recordToInsert.getDate1().getTime()));
			insertStatement.setDate(7,new java.sql.Date(recordToInsert.getDate2().getTime()));
			insertStatement.execute();
		} catch (SQLException sqlx) {
			dealWithSQLException(sqlx, "inserting record");
		}
	}


 	protected PreparedStatement prepare(String statement) {
		try {
			return connection.prepareStatement(statement);
		} catch (SQLException sqlx) {
			dealWithSQLException(sqlx, "preparing statement: " + statement);
			return null;
		}
	}


	void replaceAllRecords(RecordIterator newRecords) {
		try {
			connection.createStatement().execute("delete from " + table());
		} catch (SQLException sqlx) {
			dealWithSQLException(sqlx, "deleting all records from " + table());
		}

		while (newRecords.hasNext()) {
			insert(newRecords.next());
		}

		try {
			connection.commit();
		} catch (SQLException sqlx) {
			dealWithSQLException(sqlx, "commiting insertion of test records");
		}
	}


	static protected void dealWithSQLException(SQLException sqlx, String duringOperation) {
		sqlx.printStackTrace();
		throw new RuntimeException("SQLException " + duringOperation + ".");
	}
}
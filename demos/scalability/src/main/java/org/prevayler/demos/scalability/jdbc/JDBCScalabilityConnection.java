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


	void replaceAllRecords(int records) {
		RecordIterator newRecords = new RecordIterator(records);

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
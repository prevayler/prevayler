package org.prevayler.demos.scalability.jdbc;

import org.prevayler.demos.scalability.QueryConnection;
import org.prevayler.demos.scalability.Record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


// Contributions by Stefan Ortmanns.
class JDBCQueryConnection extends JDBCScalabilityConnection implements QueryConnection {

  private final PreparedStatement selectStatement;


  JDBCQueryConnection(Connection connection) {
    super(connection);

    selectStatement = prepare("select ID,STRING1,BIGDECIMAL1,BIGDECIMAL2,DATE1,DATE2 from " + table() + " where NAME=?");
  }


  protected String table() {
    return "QUERY_TEST";
  }


  public List<Record> queryByName(String name) {
    List<Record> list = new ArrayList<Record>();
    try {
      selectStatement.setString(1, name);
      ResultSet resultSet = selectStatement.executeQuery();

      while (resultSet.next()) {
        list.add(new Record(resultSet.getLong(1), name, resultSet.getString(2), resultSet.getBigDecimal(3), resultSet.getBigDecimal(4), resultSet.getDate(5), resultSet.getDate(6)));
      }

    } catch (SQLException sqlex) {
      dealWithSQLException(sqlex, "selecting record from " + table());
    }

    return list;
  }
}
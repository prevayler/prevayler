// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability.jdbc;

import org.prevayler.demos.scalability.*;
import java.sql.*;
import java.util.*;


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


	public List queryByName(String name) {
		ArrayList list = new ArrayList();
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
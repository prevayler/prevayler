// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability.gui;

import org.prevayler.test.scalability.*;
import org.prevayler.test.scalability.prevayler.*;
import org.prevayler.test.scalability.jdbc.*;

/** @author Paulo Augusto Peccin
*/
public class ScalabilityTestFrame {

/*
Run Prevayler Commands Test
	Number of Threads (TextFields)
		Min
		Max
	CommandLog Files (Table)
		Columns: Directory, Number of Files
	Instructions (texto com scroll): Having several log files, especially on different physical disks, greatly improves scalability for this test. This test will first create one million objects. It will then start running the test rounds. Each round takes approx. 60 seconds to run. In every round, several threads are started and the total number of commands these threads manage to execute is measured. Each command creates an object, deletes an object and updates an object. See org.prevayler.test.scalability.prevayler.PrevaylerCommandsTest.java for implementation details.
	Run (botão) abre modal: Close all other unnecessary processes (including your database). <OK> <Cancel>

Run JDBC Commands Test
	Number of Threads
		Min
		Max
	JDBC driver class name, ConnectionURL, User, Password
	Instructions: A COMMANDS_TEST table is required on your database with the following fields:
			ID DECIMAL,
			NAME CHAR(8),
			LONG_1 DECIMAL,
			LONG_2 DECIMAL,
			LONG_3 DECIMAL,
			DECIMAL_1 DECIMAL,
			DECIMAL_2 DECIMAL,
			DATE_1 DATE,
			DATE_2 DATE,
			DATE_3 DATE.
		This test will first delete all records in this table and insert one million records. It will then start running the test rounds. Each round takes approx. 60 seconds to run. In every round, several threads are started and the total number of commands (transactions) these threads manage to execute is measured. Each command (transaction) inserts a record, deletes a record and updates a record. See org.prevayler.test.scalability.jdbc.JDBCCommandsTest.java for implementation details.
	Run abre modal: Close all other unnecessary processes. <OK> <Cancel>

Run Prevayler Queries Test
	Number of Threads
		Min
		Max
	Instructions: This test will first create one million objects. It will then start running the test rounds. Each round takes approx. 60 seconds to run. In every round, several threads are started and the total number of queries these threads manage to execute is measured. Each query retrieves an average of 100 objects (out of one million) by attribute equality. See org.prevayler.test.scalability.prevayler.PrevaylerQueriesTest.java for implementation details.
	Run abre modal: Close all other unnecessary processes (including your database). <OK> <Cancel>

Run JDBC Queries Test
	Number of Threads
		Min
		Max
	JDBC driver class name, ConnectionURL, User, Password
	Instructions:  A QUERIES_TEST table is required on your database with the following fields:
			ID DECIMAL,
			NAME CHAR(8),
			LONG_1 DECIMAL,
			LONG_2 DECIMAL,
			LONG_3 DECIMAL,
			DECIMAL_1 DECIMAL,
			DECIMAL_2 DECIMAL,
			DATE_1 DATE,
			DATE_2 DATE,
			DATE_3 DATE.
	This test will first delete all records in this table and insert one million records. It will then start running the test rounds. Each round takes approx. 60 seconds to run. In every round, several threads are started and the total number of queries these threads manage to execute is measured. Each query retrieves an average of 100 records (out of one million) by field equality. See org.prevayler.test.scalability.jdbc.JDBCQueriesTest.java for implementation details.
	Run abre modal: Close all other unnecessary processes. <OK> <Cancel>

Publish Results:
	(Campos para preencher)
	Your Name:
	Your City:
	Your Country:
	Your eMail (optional):
	( ) Virtual Machine or ( ) Native Compiler: (RadioButton e campo para preencher)
	Operating System and version:
	Processor Type:
	Number of Processors:
	Processor speed:
	( ) MHz ( ) GHz
	<Generate eMail...>(Botão)
		(Gerar um textinho:)
		Please send the following eMail to XXXXXXX@YYYYYYY.ZZZ
			(Dados acima mais os resultados dos testes)

*/

}

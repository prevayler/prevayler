package org.prevayler.demos.scalability;

import org.prevayler.demos.scalability.prevayler.*;
import org.prevayler.demos.scalability.jdbc.*;
import org.prevayler.foundation.serialization.JavaSerializer;

import java.io.*;
import java.util.*;

public class Main {

	static private final Properties properties = new Properties();


	static public void main(String[] args) {

		out("\n=============================================================");
		out(  "             Prevayler vs JDBC Scalability Tests             ");
		out(  "=============================================================\n");
		out("If you have any trouble running the tests, just write to");
		out("prevayler-scalability@lists.sourceforge.net and we will be glad to help.\n");

		try {
			out("Reading the properties file:\n" + propertiesFile().getAbsolutePath());
			out("You can edit this file to configure the tests for the next run.\n");

			properties.load(new FileInputStream(propertiesFile()));

			if (isPrevaylerQueryChosen()) runPrevaylerQuery();
			if (isPrevaylerTransactionChosen()) runPrevaylerTransaction();
			if (isJdbcQueryChosen()) runJdbcQuery();
			if (isJdbcTransactionChosen()) runJdbcTransaction();

			out("\n\n\nFor better results, edit the properties file:");
			out(propertiesFile().getAbsolutePath());
			out("\nYou can publish your best results by mail to:");
			out("prevayler-scalability@lists.sourceforge.net. Please include info about your");
			out("processors (quantity, type, speed), compiler, VM, operating system and DBMS.");
			out("");
			out("Scalability test results are published on www.prevayler.org.");
			out("See you there.\n");
			out("Klaus Wuestefeld and Daniel Santos.\n\n");

		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (OutOfMemoryError err) {
			ScalabilityTestRun.outOfMemory();
		}

	}


	static private void runPrevaylerQuery() throws Exception {
		new QueryTestRun(
			new PrevaylerQuerySubject(),
			numberOfObjects(),
			prevaylerQueryThreadsMin(),
			prevaylerQueryThreadsMax()
		);
	}

	static private void runPrevaylerTransaction() throws Exception {
		PrevaylerTransactionSubject subject = new PrevaylerTransactionSubject(prevaylerTransactionLogDirectory(), prevaylerJournalSerializer());
		new TransactionTestRun(
			subject,
			numberOfObjects(),
			prevaylerTransactionThreadsMin(),
			prevaylerTransactionThreadsMax()
		);
		if (isPrevaylerTransactionConsistencyChecked()) {
			out("Checking transaction log consistency.");
			if (!subject.isConsistent()) throw new RuntimeException("Transaction log consistency check failed.");
			out("Transaction log OK.\n");
		}
	}

	static private void runJdbcQuery() {
		new QueryTestRun(
			new JDBCQuerySubject(jdbcDriverClassName(), jdbcConnectionURL(), jdbcUser(), jdbcPassword()),
			numberOfObjects(),
			jdbcQueryThreadsMin(),
			jdbcQueryThreadsMax()
		);
	}

	static private void runJdbcTransaction() {
		new TransactionTestRun(
			new JDBCTransactionSubject(jdbcDriverClassName(), jdbcConnectionURL(), jdbcUser(), jdbcPassword()),
			numberOfObjects(),
			jdbcTransactionThreadsMin(),
			jdbcTransactionThreadsMax()
		);
	}


	static private File propertiesFile() throws IOException {
		File result = new File("ScalabilityTest.properties");
		if (!result.exists()) {
			out("Creating the properties file.");
			createPropertiesFile(result);
		}
		return result;
	}

	static private void createPropertiesFile(File file) throws IOException {
		PrintStream stream = new PrintStream(new FileOutputStream(file));
		stream.println(
			"###########################################################\n" +
			"#                                                         #\n" +
			"#      PREVAYLER VS JDBC SCALABILITY TEST PROPERTIES      #\n" +
			"#                                                         #\n" +
			"###########################################################\n" +
			"\n" +
			"NumberOfObjects = ONE_HUNDRED_THOUSAND\n" +
			"# NumberOfObjects = ONE_MILLION\n" +
			"# NumberOfObjects = TEN_MILLION\n" +
			"# NumberOfObjects = TWENTY_MILLION\n" +
			"#\n" +
			"# The results are only valid if both Prevayler and the\n" +
			"# database can run the tests without paging memory to disk.\n" +
			"#\n" +
			"# Running the tests with one hundred thousand objects\n" +
			"# (default option) requires approx. 128MB free RAM.\n" +
			"# The VM must be started with a sufficient maximum heap\n" +
			"# size or you will get an OutOfMemoryError.\n" +
			"#\n" +
			"# Example for Linux and Windows:  java -Xmx128000000 ...\n" +
			"#\n" +
			"# (This can be set with the scalability.jvmarg property\n" +
			"# in build.properties; see sample.build.properties for\n" +
			"# examples.)\n" +
			"#\n" +
			"# Running the tests with one million objects requires\n" +
			"# approx. 940MB free RAM.\n" +
			"# Running the tests with ten million objects requires\n" +
			"# approx. 9.4GB free RAM and a 64bit VM.\n" +
			"#\n" +
			"# IMPORTANT: Remember to shutdown all other non-vital\n" +
			"# processes before running the tests. Even the database\n" +
			"# process should be down while running the Prevayler tests\n" +
			"# that do not use it.\n" +
			"\n" +	
			"\n" +	
			"###########################################################\n" +
			"# PREVAYLER QUERY TEST\n" +
			"\n" +
			"RunPrevaylerQueryTest = YES\n" +
			"# RunPrevaylerQueryTest = NO\n" +
			"\n" +
			"PrevaylerQueryThreadsMinimum = 1\n" +
			"PrevaylerQueryThreadsMaximum = 5\n" +
			"# More threads can produce better results on\n" +
			"# multi-processor machines.\n" +
			"\n" +
			"\n" +
			"###########################################################\n" +
			"# PREVAYLER TRANSACTION TEST\n" +
			"\n" +
			"RunPrevaylerTransactionTest = YES\n" +
			"# RunPrevaylerTransactionTest = NO\n" +
			"\n" +
			"PrevaylerTransactionThreadsMinimum = 1\n" +
			"PrevaylerTransactionThreadsMaximum = 5\n" +
			"#\n" +
			"# More threads can produce better results on machines with\n" +
			"# multiple disks.\n" +
			"\n" +
			"TransactionTestCheckConsistency = YES\n" +
			"# TransactionTestCheckConsistency = NO\n" +
			"#\n" +
			"# Verifies the integrity of the journal files produced in\n" +
			"# your particular environment.\n" +
			"\n" +
			"TransactionLogDirectory = TransactionTest\n" +
			"#\n" +
			"# The full path name can be used. Example for Windows:\n" +
			"# TransactionLogDirectory1 = c:\\\\temp\\\\TransactionTest\n" +
			"# The back-slash (\\) is the escape character so you must\n" +
			"# use two back-slashes (\\\\).\n" +
			"\n" +
			"PrevaylerJournalSerializer = " + JavaSerializer.class.getName() + "\n" +
			"\n" +
			"\n" +
			"###########################################################\n" +
			"# JDBC QUERY TEST\n" +
			"\n" +
			"RunJdbcQueryTest = NO\n" +
			"# RunJdbcQueryTest = YES\n" +
			"\n" +
			"JdbcQueryThreadsMinimum = 1\n" +
			"JdbcQueryThreadsMaximum = 5\n" +
			"# More threads can produce better results on some machines.\n" +
			"\n" +
			"\n" +
			"###########################################################\n" +
			"# JDBC TRANSACTION TEST\n" +
			"\n" +
			"RunJdbcTransactionTest = NO\n" +
			"# RunJdbcTransactionTest = YES\n" +
			"\n" +
			"JdbcTransactionThreadsMinimum = 1\n" +
			"JdbcTransactionThreadsMaximum = 5\n" +
			"# More threads can produce better results on some machines.\n" +
			"\n" +
			"\n" +
			"###########################################################\n" +
			"# JDBC CONNECTION\n" +
			"# (necessary to run the JDBC tests)\n" +
			"\n" +
			"JdbcDriverClassName =\n" +
			"JdbcConnectionURL =\n" +
			"JdbcUser =\n" +
			"JdbcPassword =\n" +
			"# These two tables are necessary for the JDBC tests:\n" +
			"# QUERY_TEST and TRANSACTION_TEST.\n" +
			"# Both tables have the same column structure:\n" +
			"#    ID DECIMAL,\n" +
			"#    NAME VARCHAR2(8),\n" +
			"#    STRING1 VARCHAR2(1000),\n" +
			"#    BIGDECIMAL1 DECIMAL,\n" +
			"#    BIGDECIMAL2 DECIMAL,\n" +
			"#    DATE1 DATE,\n" +
			"#    DATE2 DATE.\n" +
			"\n" +
			"# IMPORTANT: For best results, create indices on the\n" +
			"# QUERY_TEST.NAME and TRANSACTION_TEST.ID columns.\n" +
			"# Do not create indices on any other column.\n"
		);
	}


	static private int numberOfObjects() {
		String property = property("NumberOfObjects");
		if ("ONE_HUNDRED_THOUSAND".equals(property)) return   100000;
		if ("ONE_MILLION"         .equals(property)) return  1000000;
		if ("TEN_MILLION"         .equals(property)) return 10000000;
		if ("TWENTY_MILLION"      .equals(property)) return 20000000;
		throw new RuntimeException("NumberOfObjects property must be equal to ONE_HUNDRED_THOUSAND, ONE_MILLION, TEN_MILLION or TWENTY_MILLION.");
	}

	static private boolean isPrevaylerQueryChosen() {
		return booleanProperty("RunPrevaylerQueryTest");
	}

	static private int prevaylerQueryThreadsMin() {
		return intProperty("PrevaylerQueryThreadsMinimum");
	}

	static private int prevaylerQueryThreadsMax() {
		return intProperty("PrevaylerQueryThreadsMaximum");
	}


	static private boolean isPrevaylerTransactionChosen() {
		return booleanProperty("RunPrevaylerTransactionTest");
	}

	static private int prevaylerTransactionThreadsMin() {
		return intProperty("PrevaylerTransactionThreadsMinimum");
	}

	static private int prevaylerTransactionThreadsMax() {
		return intProperty("PrevaylerTransactionThreadsMaximum");
	}

	static private boolean isPrevaylerTransactionConsistencyChecked() {
		return booleanProperty("TransactionTestCheckConsistency");
	}
	
	static private String prevaylerTransactionLogDirectory() {
		String result = property("TransactionLogDirectory");
		out("\n\nPrevayler TransactionLog Directory: " + result);
		return result;
	}

	static private String prevaylerJournalSerializer() {
		String result = properties.getProperty("PrevaylerJournalSerializer");
		if (result == null) result = JavaSerializer.class.getName();
		out("\n\nPrevayler Journal Serializer: " + result);
		return result;
	}

	static private boolean isJdbcQueryChosen() {
		return booleanProperty("RunJdbcQueryTest");
	}

	static private int jdbcQueryThreadsMin() {
		return intProperty("JdbcQueryThreadsMinimum");
	}

	static private int jdbcQueryThreadsMax() {
		return intProperty("JdbcQueryThreadsMaximum");
	}


	static private boolean isJdbcTransactionChosen() {
		return booleanProperty("RunJdbcTransactionTest");
	}

	static private int jdbcTransactionThreadsMin() {
		return intProperty("JdbcTransactionThreadsMinimum");
	}

	static private int jdbcTransactionThreadsMax() {
		return intProperty("JdbcTransactionThreadsMaximum");
	}


	static private String jdbcDriverClassName() {
		return property("JdbcDriverClassName");
	}

	static private String jdbcConnectionURL() {
		return property("JdbcConnectionURL");
	}

	static private String jdbcUser() {
		return property("JdbcUser");
	}

	static private String jdbcPassword() {
		return property("JdbcPassword");
	}


	static private String property(String name) {
		String result = properties.getProperty(name);
		if (result == null) throw new RuntimeException("Property " + name + " not found.");
		return result;
	}

	static private int intProperty(String name) {
		try {
			return Integer.valueOf(property(name)).intValue();
		} catch (NumberFormatException nfx) {
			out("NumberFormatException reading property " + name);
			throw nfx;
		}
	}

	static private boolean booleanProperty(String name) {
		boolean result = "yes".equalsIgnoreCase(property(name));
		if (result) return true;
		out("\n\n\n" + name + " property is set to " + property(name) + ".");
		out("This test will be skipped (see properties file).");
		return false;
	}


	static private void out(Object message) {
		System.out.println(message);
	}
}

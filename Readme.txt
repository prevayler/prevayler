Prevayler 2.00
==================
Prevayler(TM) is the free-software prevalence layer for Java. It is ridiculously simple (therefore robust) and provides transparent persistence for native Java objects. Allows object queries to run 3 to 4 orders of magnitude faster than using a database through JDBC.

This release (2.00) features transparent system replication.


Requirements
================
Prevayler requires Java 1.2 or greater.
The GUIs for some of Prevayler's demo applications require Java 1.3 or greater.

This version of Prevayler was tested using JDK1.3.1_01.


Build Procedure
===================
To build and run Prevayler you compile the source code directly. This ensures maximum JDK compatibility.


Main Prevayler Classes
==========================
The following classes have the main() method and can be run directly. They are self explanatory.

1) org.prevayler.demos.demo1.Main - A tiny application that generates random numbers and stores them using Prevayler.

2) org.prevayler.demos.demo2.Main - A bank application using Prevayler. Requires Java 1.3 or greater because of the GUI classes used.

3) org.prevayler.demos.demo2.MainTransient - Exactly the same bank application running only in RAM.

4) org.prevayler.demos.demo2.MainReplicaServer - Exactly the same bank application with transparent replication enabled.

5) org.prevayler.demos.demo2.MainReplica - An application that connects to the MainReplicaSerever above and transparently replicates the bank application.

6) org.prevayler.test.scalability.Main - Runs manipulation and query scalability tests against Prevayler and any JDBC database. Just follow the instructions.

7) org.prevayler.test.Main - The functional tests used to test Prevayler during development.


More Learning Resources
===========================
1) Documentation included in this distribution. See docs directory.

2) The SocketServer example in the contrib directory

3) Source Code - Prevayler's implementation (org.prevayler.implementation) has less than 300 Java statements.

4) http://www.prevayler.org - Access to all other Prevayler resources: wiki site, mailing lists, etc.


Contact Information
=======================
If you have any trouble or doubt running Prevayler, please mail us at prevayler-discussion@lists.sourceforge.net .

Ideas and suggestions on how to simplify the project are welcome too.

Thanks,
The Prevayler Team.

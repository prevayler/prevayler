
===============================================
PREVAYLER (TM)
The Free Software Prevalence Layer for Java
===============================================

Prevalence is the simplest, fastest and most transparent architecture for business object persistence, load-balancing and fault-tolerance.

Prevayler is the original free software prevalence layer implementation for Java.


Free Software License
=========================
The Prevayler library is free software. See license.txt in the "docs" directory for more details.


Features
============
Prevayler implements ACID transactions and transparent business object persistence. It does not yet implement replication, which will enable load-balancing and fault-tolerance.


Performance Scalability
===========================
10000 (ten thousand) times faster than Oracle. 3000 (three thousand) times faster than MySQL.

These are typical results for the Prevayler query scalability test running against DBMSs using JDBC, even when the DBMSs use local databases fully cached in RAM.

You can compile and run the test for yourself. Its source code is available for inspection in the "src" directory starting at: org.prevayler.demos.scalability.Main. It runs transaction-processing and query scalability tests against Prevayler and any JDBC database. Just follow the instructions displayed on the console.


Required Java Platform
==========================
Prevayler is written and compiled against the APIs of the Java 2 Platform Standard Edition version 1.4.


Running Prevayler
=====================
Prevayler is a prevalence framework. It is compiled in the "prevaylerX.XX.XXX.jar" file available in this distribution.

To run Prevayler, you must write an application of your own or use the following demo applications.


Demo Applications
=====================
The Prevayler demo applications are available in the "src" directory. They have the main() method and can be compiled and run directly. They are self explanatory:

- org.prevayler.demos.demo1.Main - A tiny application that finds prime numbers and stores them using Prevayler.

- org.prevayler.demos.demo2.Main - A bank application using Prevayler.

- org.prevayler.demos.demo2.MainTransient - Exactly the same bank application running only in RAM.

- org.prevayler.demos.demo2.MainReplicaServer - Exactly the same bank application with transparent replication enabled.

- org.prevayler.demos.demo2.MainReplica - An application that connects to the MainReplicaSerever above and transparently replicates the bank application.


Learning Prevayler 1-2-3
============================
The prevalence concepts are ridiculously simple. Unlike using a database, though, writing an application using a prevalence layer actually requires you to know OO.

1- Read the "Object Prevalence Skeptical FAQ" in the "docs" directory.
2- Run the demos above and understand their source code. Use the javadoc in the "docs/api/beginners" directory as a reference.
3- Write a little application of your own using the Bank demo as an example.

The source code to Prevayler is available in the "src" directory. You are dearly invited to visit it. The whole Prevayler implementation is only a few hundred lines of code.


Tutorial
============
A more elaborate Prevayler tutorial including a web interface (JSP) can be found at: www.prevayler.org/presto .


Contact Information
=======================
All Prevayler resources are available from www.prevayler.org .

If you know Java but have any trouble or doubt running Prevayler, please join our discussion lists.

Ideas, contributions and suggestions are welcome too.

Thanks,
The Prevayler team.


----------------------------------------------------
Copyleft 2003 Klaus Wuestefeld
"PREVAYLER" is a trademark by Klaus Wuestefeld.

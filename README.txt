===============================================
PREVAYLER (TM)
The Free Software Prevalence Layer for Java
===============================================

Prevalence is the simplest, fastest and most transparent architecture for
business object persistence, load-balancing and fault-tolerance.

Prevayler is the original free software prevalence layer implementation for
Java.


Free Software License
=========================
The Prevayler library is free software, distributed under a BSD license.
See LICENSE.TXT in this directory for more details.


Features
============
Prevayler implements ACID transactions and transparent business object
persistence. It does not yet implement replication, which will enable
load-balancing and fault-tolerance.


Performance Scalability
===========================
10000 (ten thousand) times faster than Oracle. 3000 (three thousand) times
faster than MySQL.

These are typical results for the Prevayler query scalability test running
against DBMSs using JDBC, even when the DBMSs use local databases fully cached
in RAM.

You can compile and run the test for yourself. Its source code is available for
inspection in the "demos/scalability" directory starting at:

    org.prevayler.demos.scalability.Main

It runs transaction-processing and query scalability tests against Prevayler
and any JDBC database. Just follow the instructions displayed on the console.


Required Java Platform
==========================
Prevayler 2.5 and below is written against the APIs of the Java 2 Platform
Standard Edition version 1.4. Prior to Prevayler 2.5 the output classes are
targeting Java 1.4, while Prevayler 2.5 targets Java 1.6.

As of Prevayler 2.6 both code and compiled output will target Java 1.6.


Running Prevayler
=====================
Prevayler is a prevalence framework. It is compiled in the
"prevayler-XYZ-N.N.jar" files available in this distribution.

* prevayler-core-${version}.jar
* prevayler-factory-${version}.jar

    The core and factory modules are required for basic Prevayler operation.
    Using these two alone does not introduce any external dependencies.

* prevayler-xstream-${version}.jar

    The xstream module is an optional extension for using XML for
    journal and snapshot serialization, requiring the external XStream library.

* prevayler-log4j-${version}.jar

    The log4j module is an optional extension implementing Prevayler's Monitor
    interface using Log4J. It naturally requires Log4J.

* prevayler-mirror-${version}.jar

    The mirror module is an experimental extension implementing simple
    master-slave replication. It is not yet very robust, but some users are
    already using it.

To run Prevayler, you must write an application of your own or use the
following demo applications.


Demo Applications
=====================
The Prevayler demo applications are available in the "demos" directory. They
have the main() method and can be compiled and run directly.

* demos/demo1: org.prevayler.demos.demo1.Main

    A tiny application that finds prime numbers and stores them using
    Prevayler.

* demos/jxpath: org.prevayler.demos.jxpath.Main

    An example of using JXPath to query Java objects. See demos/jxpath/README
    for more details.

* demos/tutorial: org.prevayler.tutorial.Main

    A very simple quick-start example.

* demos/scalability: org.prevayler.demos.scalability.Main

    Mentioned previously.


Learning Prevayler 1-2-3
============================
The prevalence concepts are ridiculously simple. Unlike using a database,
though, writing an application using a prevalence layer actually requires you
to know OO.

1. Read the "Object Prevalence Skeptical FAQ" in the "docs" directory.

2. Run the demos above and understand their source code. Use the javadoc in the
"apidocs" directory as a reference.

3. Write a little application of your own using the Bank demo as an example.

The source code to Prevayler is available in the "src" directory. You are
dearly invited to visit it. The core Prevayler implementation is only a couple
thousand lines of code.


Prevayler Pet Store
=======================
A more elaborate Prevayler tutorial including a web interface (JSP) can be
found at: http://sourceforge.net/projects/presto


Contact Information
=======================
All Prevayler resources are available from: http://www.prevayler.org/

If you know Java but have any trouble or doubt running Prevayler, please join
our discussion lists.

Ideas, contributions and suggestions are welcome too.

Thanks,
The Prevayler team.


----------------------------------------------------
Copyright 2001-2011 by Klaus Wuestefeld and the Prevayler team
"PREVAYLER" is a trademark of Klaus Wuestefeld.


(under construction  :)


PREVAYLER 2.00
==================
Welcome to release 2.00 of Prevayler, the free software Prevalence Layer for Java.


Prevalence Layers in General
================================
================================

Features
============
These are the features a Prevalence Layer can provide. Not all of them are implemented in Prevayler yet.
 - Transparent Persistence - business objects, Plain Old Java Objects
 - ACID Transactions
  - Atomicity
  - Consistency - Transaction Filtering
  - Isolation
  - Durability - Persistence
 - Transparent Replication. Enables Fault-Tolerance and Load-Balancing.


Performance Scalability
===========================
 - Query Performance Scalability - Object Prevalence Layers will allow simple object queries to run 3 to 4 orders of magnitude faster than using a database through JDBC. Complex queries will run even faster.
 - Transaction Performance Scalability - Transaction processing is in the same order 


Architecture
================
How it works. The logging. The snapshots. Replication. Enables Fault-Tolerance and Load-Balancing.



Prevayler in Particular
===========================
===========================

License
===========
The Prevayler library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. The Prevayler library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. link???


Required Java Platform
==========================
Prevayler is written and compiled against the APIs of version 1.3 of the Java 2 Platform Standard Edition, and targeted to run on either version 1.3 or 1.4 of the Java 2 Platform Standard Edition.


Features
============
Transaction Filtering.


Performance Scalability
===========================
Tests run in the Sun labs, Prevayler was more than 10000 times faster than Oracle through JDBC.
You can run the test for yourself: org.prevayler.demos.scalability.Main - Runs manipulation and query scalability tests against Prevayler and any JDBC database. Just follow the instructions.


Known Bugs
==============
Hundreds of organizations use Prevayler world-wide worldwide ???. Till now, no bugs have been found in a Prevayler production release. This is because Prevayler's implementation is only a few hundred lines of code.


Getting Started
===================
Where is the code???
jar file ???
javadoc link ???


Prevayler Demos
===================
The following classes have the main() method and can be run directly. They are self explanatory.

1) org.prevayler.demos.demo1.Main - A tiny application that generates random numbers and stores them using Prevayler.

2) org.prevayler.demos.demo2.Main - A bank application using Prevayler.

3) org.prevayler.demos.demo2.MainTransient - Exactly the same bank application running only in RAM.

4) org.prevayler.demos.demo2.MainReplicaServer - Exactly the same bank application with transparent replication enabled.

5) org.prevayler.demos.demo2.MainReplica - An application that connects to the MainReplicaSerever above and transparently replicates the bank application.


Tutorial
============
A more elaborate Prevayler tutorial including a web interface (JSP) can be found here: ???.


More Learning Resources
===========================
1) Source Code - Prevayler's implementation (org.prevayler.implementation) has less than 300 Java statements.

2) http://www.prevayler.org - Access to all other Prevayler resources: wiki site, mailing lists, special CVS contribs, etc.


Contact Information
=======================
If you have any trouble or doubt running Prevayler, please mail us at prevayler-discussion@lists.sourceforge.net.

Ideas and suggestions on how to simplify the project are welcome too.

Thanks,
The Prevayler Team.
http://www.prevayler.org


------------------
"PREVAYLER" is a trademark by Klaus Wuestefeld.
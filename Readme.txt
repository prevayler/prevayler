I) Compatibility
================

Prevayler requires Java 1.2 or greater.
This version of Prevayler was tested with JDK1.3.1_01.
The GUIs for some of Prevayler's demo applications require Java 1.3.


II) Building Procedure
======================

To build and use Prevayler you compile the source code directly. This ensures maximum JDK compatibility.


III) Main Prevayler Classes
===========================

The following classes have the main() method and can be run directly. They are self explanatory.

1) org.prevayler.demos.demo1.Main - A very simple demo that calculates prime numbers and stores them using Prevayler.

2) org.prevayler.demo.PrevaylerDemo - A demo bank application using Prevayler. It requires JDK1.3 or greater because of the GUI classes it uses.

3) org.prevayler.test.scalability.ScalabilityTest - Runs manipulation and query scalability tests against Prevayler and any JDBC database. Just follow the instructions.

4) org.prevayler.test.FullTest - The functional tests used to test Prevayler during develoment.

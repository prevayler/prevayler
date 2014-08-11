package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.RecordIterator;

interface ScalabilitySystem extends java.io.Serializable {

  void replaceAllRecords(RecordIterator newRecords);

}

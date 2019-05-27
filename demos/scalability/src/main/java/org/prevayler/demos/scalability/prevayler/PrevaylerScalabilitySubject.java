package org.prevayler.demos.scalability.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.demos.scalability.ScalabilityTestSubject;

abstract class PrevaylerScalabilitySubject<S extends ScalabilitySystem, C> implements ScalabilityTestSubject<C> {

  protected Prevayler<S> prevayler;


  {
    System.gc();
  }


  public String name() {
    return "Prevayler";
  }


  public void replaceAllRecords(int records) {
    try {

      prevayler.execute(new AllRecordsReplacement<S>(records));

    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException("Unexpected Exception: " + ex);
    }
  }

}

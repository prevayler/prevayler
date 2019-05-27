package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.Record;
import org.prevayler.demos.scalability.RecordIterator;

import java.util.*;

class QuerySystem implements ScalabilitySystem {

  private static final long serialVersionUID = -8181198939095700706L;
  private Map<String, List<Record>> recordsByName = new HashMap<String, List<Record>>();


  public List<Record> queryByName(String name) {
    return recordsByName.get(name);
  }


  public void replaceAllRecords(RecordIterator newRecords) {
    recordsByName.clear();

    while (newRecords.hasNext()) {
      put(newRecords.next());
    }

    makeReadOnly();
  }


  private void put(Record newRecord) {
    List<Record> records = queryByName(newRecord.getName());
    if (records == null) {
      records = new ArrayList<Record>();
      recordsByName.put(newRecord.getName(), records);
    }

    records.add(newRecord);
  }


  /**
   * This is necessary so that the clients cannot alter the Lists they receive as query results.
   */
  private void makeReadOnly() {
    Iterator<Map.Entry<String, List<Record>>> entries = recordsByName.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, List<Record>> entry = entries.next();
      entry.setValue(Collections.unmodifiableList(entry.getValue()));
    }
  }
}

package org.prevayler.demos.demo1;

import java.util.ArrayList;
import java.util.List;


/**
 * The NumberKeeper and all its references are the prevalent system.
 * i.e: They are the "business objects" and will be transparently persisted by Prevayler.
 */
class NumberKeeper implements java.io.Serializable {

  private static final long serialVersionUID = 2253937139530882022L;
  private final List<Integer> numbers = new ArrayList<Integer>();


  void keep(int nextNumber) {
    numbers.add(nextNumber);
  }

  List<Integer> numbers() {
    return numbers;
  }

  int lastNumber() {
    return numbers.isEmpty()
        ? 0
        : numbers.get(numbers.size() - 1);
  }

}

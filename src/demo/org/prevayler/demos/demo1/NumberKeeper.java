// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo1;

import java.util.ArrayList;
import java.util.List;

/**
 * The NumberKeeper and all its references are the prevalent system. i.e: They
 * are the "business objects" and will be transparently persisted by Prevayler.
 */
class NumberKeeper implements java.io.Serializable {

    private static final long serialVersionUID = 2253937139530882022L;

    private final List<Integer> numbers = new ArrayList<Integer>();

    public void keep(int nextNumber) {
        numbers.add(nextNumber);
    }

    public List numbers() {
        return numbers;
    }

    public int lastNumber() {
        return numbers.isEmpty() ? 0 : numbers.get(numbers.size() - 1);
    }

}

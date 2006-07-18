// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.backward;

import org.prevayler.*;
import org.prevayler.implementation.*;

import java.io.*;
import java.util.*;

@SuppressWarnings("deprecation") public class BackwardAppendix implements Transaction, Serializable {

    private static final long serialVersionUID = 7925676108189989759L;

    private final String appendix;

    public BackwardAppendix(String appendix) {
        this.appendix = appendix;
    }

    public void executeOn(Object prevalentSystem, @SuppressWarnings("unused") Date executionTime) {
        ((AppendingSystem) prevalentSystem).append(appendix);
    }

}

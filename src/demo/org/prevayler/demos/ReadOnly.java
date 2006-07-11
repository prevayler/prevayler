// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.prevayler.Safety.Journaling.TRANSIENT;
import static org.prevayler.Safety.Locking.SHARED;

import org.prevayler.Safety;

import java.lang.annotation.Retention;

@Retention(RUNTIME) @Safety(journaling = TRANSIENT, locking = SHARED) public @interface ReadOnly {
}

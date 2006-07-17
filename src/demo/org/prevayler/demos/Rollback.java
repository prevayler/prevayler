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

import static java.lang.annotation.RetentionPolicy.*;
import static org.prevayler.Safety.Journaling.*;
import static org.prevayler.Safety.Locking.*;

import org.prevayler.*;

import java.lang.annotation.*;

@Retention(RUNTIME) @Safety(journaling = ROLLBACK_ON_RUNTIME_EXCEPTION, locking = EXCLUSIVE) public @interface Rollback {
}

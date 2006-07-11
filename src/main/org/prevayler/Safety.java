// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Annotation indicating the safety level required by a
 * {@link GenericTransaction}.
 */
@Retention(RUNTIME) public @interface Safety {

    public static enum Journaling {

        /**
         * Transaction is not journaled.
         */
        TRANSIENT,

        /**
         * Transaction is journaled. Implies at least exclusive locking. The
         * original transaction object is executed if available.
         */
        PERSISTENT,

        /**
         * Transaction is journaled, and is deserialized before execution.
         * Implies at least exclusive locking.
         */
        DESERIALIZE_BEFORE_EXECUTION,

        /**
         * Transaction is journaled and deep-copied before execution, and rolled
         * back if it throws a RuntimeException.
         */
        ROLLBACK_ON_RUNTIME_EXCEPTION

    }

    public static enum Locking {

        /**
         * Transaction executes with no locks.
         */
        NONE,

        /**
         * Transaction executes with shared (read) lock alone.
         */
        SHARED,

        /**
         * Transaction executes with exclusive (write) lock alone.
         */
        EXCLUSIVE,

        /**
         * Transaction executes with both prevalent system lock and exclusive
         * (write) lock.
         */
        PREVALENT_SYSTEM

    }

    public Journaling journaling() default Journaling.ROLLBACK_ON_RUNTIME_EXCEPTION;

    public Locking locking() default Locking.PREVALENT_SYSTEM;

}

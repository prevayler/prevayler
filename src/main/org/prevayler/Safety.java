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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Inherited @Retention(RUNTIME) @Target(TYPE) public @interface Safety {

    /**
     * Transaction is not journaled, and executes with no locking. <b>Currently
     * treated the same as LEVEL_1_SHARED_LOCKING.</b>
     */
    public static final int LEVEL_0_NO_LOCKING = 0;

    /**
     * Transaction is not journaled, and executes with shared (read) lock alone.
     */
    public static final int LEVEL_1_SHARED_LOCKING = 1;

    /**
     * Transaction is not journaled, and executes with exclusive (write) lock
     * alone. <b>Currently treated the same as LEVEL_1_SHARED_LOCKING.</b>
     */
    public static final int LEVEL_2_EXCLUSIVE_LOCKING = 2;

    /**
     * Transaction is not journaled, and executes with both prevalent system
     * lock and exclusive (write) lock. <b>Currently treated the same as
     * LEVEL_1_SHARED_LOCKING.</b>
     */
    public static final int LEVEL_3_SYSTEM_LOCKING = 3;

    /**
     * Transaction is journaled, and executes with both prevalent system lock
     * and exclusive (write) lock. <b>Currently treated the same as
     * LEVEL_6_CENSORING.</b>
     */
    public static final int LEVEL_4_JOURNALING = 4;

    /**
     * Transaction is deep-copied and journaled, and executes with both
     * prevalent system lock and exclusive (write) lock. <b>Currently treated
     * the same as LEVEL_6_CENSORING.</b>
     */
    public static final int LEVEL_5_DEEP_COPYING = 5;

    /**
     * Transaction is censored, deep-copied, and journaled, and executes with
     * both prevalent system lock and exclusive (write) lock. <b>Censoring is
     * currently applied if and only if a censor is configured.</b>
     */
    public static final int LEVEL_6_CENSORING = 6;

    /**
     * Transaction does not modify the prevalent system at all, same as Level 1.
     * <b>This is just a suggestion.</b>
     */
    public static final int READ_ONLY = LEVEL_1_SHARED_LOCKING;

    /**
     * Transaction might modify the prevalent system, same as Level 5. <b>This
     * is just a suggestion.</b>
     */
    public static final int READ_WRITE = LEVEL_5_DEEP_COPYING;

    // This must be called "value" so that the simplified annotation syntax
    // @Safety(#) can be used.
    public int value();

}

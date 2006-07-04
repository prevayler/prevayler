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

/**
 * Annotation indicating the safety level required by a
 * {@link GenericTransaction}.
 */
@Inherited @Retention(RUNTIME) @Target(TYPE) public @interface Safety {

    public static enum Level {

        /**
         * Transaction is not journaled, and executes with no locking.
         * <b>Currently treated the same as LEVEL_1_SHARED_LOCKING.</b>
         */
        LEVEL_0_NO_LOCKING(0),

        /**
         * Transaction is not journaled, and executes with shared (read) lock
         * alone.
         */
        LEVEL_1_SHARED_LOCKING(1),

        /**
         * Transaction is not journaled, and executes with exclusive (write)
         * lock alone. <b>Currently treated the same as LEVEL_1_SHARED_LOCKING.</b>
         */
        LEVEL_2_EXCLUSIVE_LOCKING(2),

        /**
         * Transaction is not journaled, and executes with both prevalent system
         * lock and exclusive (write) lock. <b>Currently treated the same as
         * LEVEL_1_SHARED_LOCKING.</b>
         */
        LEVEL_3_SYSTEM_LOCKING(3),

        /**
         * Transaction is journaled, and executes with both prevalent system
         * lock and exclusive (write) lock. <b>Currently treated the same as
         * LEVEL_6_CENSORING.</b>
         */
        LEVEL_4_JOURNALING(4),

        /**
         * Transaction is deep-copied and journaled, and executes with both
         * prevalent system lock and exclusive (write) lock. <b>Currently
         * treated the same as LEVEL_6_CENSORING.</b>
         */
        LEVEL_5_DEEP_COPYING(5),

        /**
         * Transaction is censored, deep-copied, and journaled, and executes
         * with both prevalent system lock and exclusive (write) lock.
         * <b>Censoring is currently applied if and only if a censor is
         * configured.</b>
         */
        LEVEL_6_CENSORING(6);

        private final int _level;

        private Level(int level) {
            _level = level;
        }

        public boolean isNoLocking() {
            return _level == 0;
        }

        public boolean isSharedLocking() {
            return _level == 1;
        }

        public boolean isExclusiveLocking() {
            return _level >= 2;
        }

        public boolean isSystemLocking() {
            return _level >= 3;
        }

        public boolean isJournaling() {
            return _level >= 4;
        }

        public boolean isDeepCopying() {
            return _level >= 5;
        }

        public boolean isCensoring() {
            return _level == 6;
        }

        public static Level safetyLevel(GenericTransaction<?, ?, ?> transaction) {
            Safety safety = transaction.getClass().getAnnotation(Safety.class);
            return safety == null ? LEVEL_6_CENSORING : safety.value();
        }

    }

    // This must be called "value" so that the simplified annotation syntax
    // @Safety(#) can be used.
    public Level value();

}

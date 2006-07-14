// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.GenericTransaction;
import org.prevayler.Safety;
import org.prevayler.Safety.Journaling;
import org.prevayler.Safety.Locking;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;

public final class SafetyCache {

    private static final class Entry {

        public final Journaling journaling;

        public final Locking locking;

        public Entry() {
            this(null, null);
        }

        public Entry(Safety safety) {
            this(safety.journaling(), safety.locking());
        }

        private Entry(Journaling journaling, Locking locking) {
            this.journaling = journaling;
            this.locking = locking;
        }

        public Entry combine(Entry other) {
            if (journaling == null) {
                return other;
            } else {
                return new Entry(max(journaling, other.journaling), max(locking, other.locking));
            }
        }
    }

    private static <T extends Enum<T>> T max(T a, T b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a.compareTo(b) > 0 ? a : b;
        }
    }

    private static final Entry UNSPECIFIED = new Entry();

    private static final ConcurrentHashMap<Class<?>, Entry> CACHE = new ConcurrentHashMap<Class<?>, Entry>();

    static {
        CACHE.put(Object.class, UNSPECIFIED);
        CACHE.put(Enum.class, UNSPECIFIED);
        CACHE.put(Annotation.class, UNSPECIFIED);
        CACHE.put(Safety.class, UNSPECIFIED);
        registerCircularAnnotation(Documented.class);
        registerCircularAnnotation(Retention.class);
        registerCircularAnnotation(Target.class);
    }

    /**
     * Register an annotation which is annotated by itself, to prevent stack
     * overflow. For example, in the JDK, the Documented, Retention, and Target
     * annotations are mutually circular -- each one annotates both of the
     * others.
     */
    public static void registerCircularAnnotation(Class<? extends Annotation> annotationClass) {
        CACHE.put(annotationClass, UNSPECIFIED);
    }

    public static boolean isAnnotated(Object object) {
        return get(object.getClass()) != UNSPECIFIED;
    }

    public static Journaling getJournaling(GenericTransaction<?, ?, ?> transaction) {
        return get(transaction.getClass()).journaling;
    }

    public static Locking getLocking(GenericTransaction<?, ?, ?> transaction) {
        return get(transaction.getClass()).locking;
    }

    private static Entry get(Class<?> key) {
        Entry entry = CACHE.get(key);
        if (entry == null) {
            entry = compute(key);
            CACHE.put(key, entry);
        }
        return entry;
    }

    private static Entry compute(Class<?> key) {
        Entry computed = UNSPECIFIED;
        if (key.isAnnotationPresent(Safety.class)) {
            computed = computed.combine(new Entry(key.getAnnotation(Safety.class)));
        }
        if (key.getSuperclass() != null) {
            computed = computed.combine(get(key.getSuperclass()));
        }
        for (Annotation anno : key.getDeclaredAnnotations()) {
            computed = computed.combine(get(anno.annotationType()));
        }
        for (Class<?> intf : key.getInterfaces()) {
            computed = computed.combine(get(intf));
        }
        return computed;
    }

    private SafetyCache() {
    }

}

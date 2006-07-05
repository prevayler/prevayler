// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Cool things that are often needed. In particular, implements <a
 * href="http://www-128.ibm.com/developerworks/java/library/j-jtp05236.html">well-behaved</a>
 * uninterruptible versions of wait, sleep, and join.
 */
public class Cool {

    /**
     * Wait on the given object. As with Object.wait(), this may return even if
     * there was no notification on the object; there are various spurious
     * conditions where wait() is allowed to return. Therefore wait() should
     * always be called within a while loop checking for the condition being
     * waited for. In addition, this method will return if the thread is
     * interrupted, but unlike Object.wait() it will not throw
     * InterruptedException and will not clear the current thread's interrupt
     * status.
     */
    public static void wait(Object object) {
        boolean interrupted = Thread.interrupted();
        try {
            object.wait();
        } catch (InterruptedException e) {
            interrupted = true;
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Sleep for the given time. This method will return early if the thread is
     * interrupted, but unlike Thread.sleep() it will not throw
     * InterruptedException and will not clear the current thread's interrupt
     * status.
     */
    public static void sleep(long milliseconds) {
        boolean interrupted = Thread.interrupted();
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            interrupted = true;
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Join the given thread. This method will not return early, even if the
     * current thread is interrupted. It will not throw InterruptedException and
     * will not clear the current thread's interrupt status.
     */
    public static void join(Thread thread) {
        boolean interrupted = Thread.interrupted();
        try {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                    continue;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Call the current thread's uncaught exception handler. This is useful at
     * the top level of a thread, where run() is not allowed to throw checked
     * exceptions.
     */
    public static void uncaught(Throwable thrown) {
        Thread thread = Thread.currentThread();
        UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();
        if (handler != null) {
            try {
                handler.uncaughtException(thread, thrown);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Rethrow the given throwable as if it were an unchecked exception.
     * Equivalent to:
     * 
     * <pre>
     * throw Cool.&lt;RuntimeException&gt; loophole(t);
     * </pre>
     * 
     * @see #loophole(Throwable)
     */
    public static RuntimeException unchecked(Throwable t) {
        throw Cool.<RuntimeException> loophole(t);
    }

    /**
     * Rethrow the given throwable as if it were some arbitrary other type,
     * circumventing checked exception declarations.
     * <p>
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4708394">Bug
     * 4708394</a> discusses this technique. No casting or other typechecking
     * is performed at runtime, so the following code compiles successfully and
     * throws the IOException from the method without declaring IOException as
     * being thrown:
     * 
     * <pre>
     * public void example() {
     *     throw Cool.&lt;RuntimeException&gt; loophole(new IOException());
     * }
     * </pre>
     * 
     * The fact that T is also returned is just for syntactic convenience in the
     * caller; no value is returned, since this method itself throws the given
     * throwable.
     */
    @SuppressWarnings("unchecked") public static <T extends Throwable> T loophole(Throwable t) throws T {
        throw (T) t;
    }

}

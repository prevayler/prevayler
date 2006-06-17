package org.prevayler.foundation;

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

}

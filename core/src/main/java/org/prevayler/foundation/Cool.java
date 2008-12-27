package org.prevayler.foundation;


/** Cool things that are often needed.
 */
public class Cool {

	public static void wait(Object object) {
		try {
			object.wait();
		} catch (InterruptedException e) {
			unexpected(e);
		}
	}

	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			unexpected(e);
		}
	}

    public static void unexpected(Exception e) {
        throw new RuntimeException("Unexpected Exception was thrown.", e);
    }

    public static void startDaemon(Runnable runnable) {
        Thread daemon = new Thread(runnable);
        daemon.setDaemon(true);
        daemon.start();
    }

}

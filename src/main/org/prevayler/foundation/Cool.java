package org.prevayler.foundation;

/** Cool things that are often needed.
 */
public class Cool {

	public static void wait(Object object) {
		try {
			object.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}

}

package org.prevayler.socketserver.client;

/**
 * Exception that is thrown when a callback is asked to be freed more than once
 * @author djo
 */
public class CallbackAlreadyFreedException extends Exception {
	/**
	 * Constructor CallbackAlreadyFreedException.
	 * @param string
	 */
	public CallbackAlreadyFreedException(String string) {
        super(string);
	}


}

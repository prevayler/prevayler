package org.prevayler.implementation.publishing.censorship;

import org.prevayler.PrevalenceError;

public class FoodTasterError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public FoodTasterError() {
        super();
    }

    public FoodTasterError(String message) {
        super(message);
    }

    public FoodTasterError(String message, Throwable cause) {
        super(message, cause);
    }

    public FoodTasterError(Throwable cause) {
        super(cause);
    }

}

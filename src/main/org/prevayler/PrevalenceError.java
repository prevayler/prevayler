package org.prevayler;

public class PrevalenceError extends Error {

    // Exception-handling notes:
    //
    // Don't expect checked and unchecked Exceptions to have different semantic
    // importance
    //
    // Never drop or wrap an Error
    //
    // Never throw a new checked Exception
    //
    // Throw a PrevalenceError for any abstraction-violating occurrence in
    // operation
    //
    // Throw a RuntimeException for any configuration problems
    //
    // Never allow the system to continue operating in an inconsistent state
    //
    // Never hang

    private static final long serialVersionUID = 1L;

    public PrevalenceError() {
        super();
    }

    public PrevalenceError(String message) {
        super(message);
    }

    public PrevalenceError(String message, Throwable cause) {
        super(message, cause);
    }

    public PrevalenceError(Throwable cause) {
        super(cause);
    }

}

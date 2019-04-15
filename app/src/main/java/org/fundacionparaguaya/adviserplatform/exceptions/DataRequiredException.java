package org.fundacionparaguaya.adviserplatform.exceptions;

public class DataRequiredException extends RuntimeException {
    public DataRequiredException() {
    }

    public DataRequiredException(String message) {
        super(message);
    }

    public DataRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataRequiredException(Throwable cause) {
        super(cause);
    }

}

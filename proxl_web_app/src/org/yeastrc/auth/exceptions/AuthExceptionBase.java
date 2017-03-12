package org.yeastrc.auth.exceptions;

/**
 * This is the base of Authentication Exceptions
 *
 */
public class AuthExceptionBase extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public AuthExceptionBase() {
        super();
    }

    public AuthExceptionBase(String message) {
        super(message);
    }

    public AuthExceptionBase(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthExceptionBase(Throwable cause) {
        super(cause);
    }

 
    protected AuthExceptionBase(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

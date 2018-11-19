package org.yeastrc.auth.exceptions;

/**
 * This exception is for when a record is not found in auth_shared_object table
 *
 */
public class AuthSharedObjectRecordNotFoundException extends AuthExceptionBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

    public AuthSharedObjectRecordNotFoundException() {
        super();
    }

    public AuthSharedObjectRecordNotFoundException(String message) {
        super(message);
    }

    public AuthSharedObjectRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthSharedObjectRecordNotFoundException(Throwable cause) {
        super(cause);
    }

 
    protected AuthSharedObjectRecordNotFoundException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

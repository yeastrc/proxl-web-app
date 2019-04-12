package org.yeastrc.xlink.www.exceptions;

/**
 * Extends RuntimeException so compatible with Spring DB Transactions
 * 
 * For when/if Converted to Spring
 *
 */
public class ProxlWebappDatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProxlWebappDatabaseException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDatabaseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDatabaseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDatabaseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDatabaseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}

package org.yeastrc.xlink.www.exceptions;

/**
 * Data not found from DB query.  
 * 
 * Required for Guava LoadingCache since not valid to return null to Guava Cache
 *
 */
public class ProxlWebappDataNotFoundException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProxlWebappDataNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDataNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDataNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDataNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDataNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}

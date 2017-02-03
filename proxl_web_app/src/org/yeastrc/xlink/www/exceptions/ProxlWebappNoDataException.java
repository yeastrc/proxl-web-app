package org.yeastrc.xlink.www.exceptions;

/**
 * Thrown when there is no data.  
 * 
 * Primary used in development when config file throw_exception_no_data.properties has throw_exception_no_data=true
 *
 */
public class ProxlWebappNoDataException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProxlWebappNoDataException() {
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappNoDataException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappNoDataException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappNoDataException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappNoDataException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}

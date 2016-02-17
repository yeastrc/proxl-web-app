package org.yeastrc.xlink.www.exceptions;

/**
 * Used when the data in the database is out "Out of sync" with itself.
 * 
 * An example is an annotation type id records that has a search_programs_per_search_id 
 *   that is not in the database
 *
 */
public class ProxlWebappDBDataOutOfSyncException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProxlWebappDBDataOutOfSyncException() {
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDBDataOutOfSyncException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDBDataOutOfSyncException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDBDataOutOfSyncException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlWebappDBDataOutOfSyncException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}

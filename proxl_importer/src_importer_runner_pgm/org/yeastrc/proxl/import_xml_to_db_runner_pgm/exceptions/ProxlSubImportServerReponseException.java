package org.yeastrc.proxl.import_xml_to_db_runner_pgm.exceptions;

/**
 * Thrown for server response status code not ok (200) error.  Already reported to user
 *
 */
public class ProxlSubImportServerReponseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProxlSubImportServerReponseException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportServerReponseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportServerReponseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportServerReponseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportServerReponseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}

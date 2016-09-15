package org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions;

/**
 * Thrown for server response status code not ok (200) error.  Already reported to user
 *
 */
public class ProxlSubImportReportedErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProxlSubImportReportedErrorException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportReportedErrorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportReportedErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportReportedErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlSubImportReportedErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}

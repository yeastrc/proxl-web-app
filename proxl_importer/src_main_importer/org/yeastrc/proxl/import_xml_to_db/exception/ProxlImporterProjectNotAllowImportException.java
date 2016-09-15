package org.yeastrc.proxl.import_xml_to_db.exception;

public class ProxlImporterProjectNotAllowImportException extends Exception {

	public static enum NotAllowedReason {
		
		PROJECT_NOT_IN_DATABASE,
		PROJECT_LOCKED,
		PROJECT_NOT_ENABLED,
		PROJECT_MARKED_FOR_DELETION
		
	}
	
	private NotAllowedReason notAllowedReason;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ProxlImporterProjectNotAllowImportException() {
		// TODO Auto-generated constructor stub
	}

	public ProxlImporterProjectNotAllowImportException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxlImporterProjectNotAllowImportException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlImporterProjectNotAllowImportException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxlImporterProjectNotAllowImportException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public NotAllowedReason getNotAllowedReason() {
		return notAllowedReason;
	}

	public void setNotAllowedReason(NotAllowedReason notAllowedReason) {
		this.notAllowedReason = notAllowedReason;
	}

}

package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service AddExistingUserToProjectService
 *
 */
public class AddExistingUserToProjectResult {

	private boolean status;
	private boolean duplicateInsertError;

	public boolean isDuplicateInsertError() {
		return duplicateInsertError;
	}

	public void setDuplicateInsertError(boolean duplicateInsertError) {
		this.duplicateInsertError = duplicateInsertError;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}

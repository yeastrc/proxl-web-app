package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.dto.XLinkUserDTO;

/**
 * This is returned from the web service UserInviteService
 *
 */
public class UserInviteResult {

	private boolean status;
	
	private boolean addedExistingUser;
	private boolean lastNameNotFoundError;
	private boolean lastNameDuplicateError;
	
	//  For invite from Manage Users page ( no project id sent )
	private boolean emailAddressDuplicateError;

	private boolean duplicateInsertError;
	
	private boolean emailAddressInvalidSendError;

	private boolean emailSent;
	
	private XLinkUserDTO existingUserThatWasAdded;


	
	
	public boolean isEmailAddressInvalidSendError() {
		return emailAddressInvalidSendError;
	}

	public void setEmailAddressInvalidSendError(boolean emailAddressInvalidSendError) {
		this.emailAddressInvalidSendError = emailAddressInvalidSendError;
	}

	private boolean unableToSendEmailError;


	public boolean isUnableToSendEmailError() {
		return unableToSendEmailError;
	}

	public void setUnableToSendEmailError(boolean unableToSendEmailError) {
		this.unableToSendEmailError = unableToSendEmailError;
	}

	
	public boolean isEmailAddressDuplicateError() {
		return emailAddressDuplicateError;
	}

	public void setEmailAddressDuplicateError(boolean emailAddressDuplicateError) {
		this.emailAddressDuplicateError = emailAddressDuplicateError;
	}

	public XLinkUserDTO getExistingUserThatWasAdded() {
		return existingUserThatWasAdded;
	}

	public void setExistingUserThatWasAdded(XLinkUserDTO existingUserThatWasAdded) {
		this.existingUserThatWasAdded = existingUserThatWasAdded;
	}

	public boolean isEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public boolean isLastNameNotFoundError() {
		return lastNameNotFoundError;
	}

	public void setLastNameNotFoundError(boolean lastNameNotFoundError) {
		this.lastNameNotFoundError = lastNameNotFoundError;
	}

	public boolean isLastNameDuplicateError() {
		return lastNameDuplicateError;
	}

	public void setLastNameDuplicateError(boolean lastNameDuplicateError) {
		this.lastNameDuplicateError = lastNameDuplicateError;
	}


	public boolean isAddedExistingUser() {
		return addedExistingUser;
	}

	public void setAddedExistingUser(boolean addedExistingUser) {
		this.addedExistingUser = addedExistingUser;
	}
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

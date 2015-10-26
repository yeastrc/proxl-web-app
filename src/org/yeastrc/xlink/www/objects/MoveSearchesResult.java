package org.yeastrc.xlink.www.objects;


/**
 * This is returned from ProjectMoveSearchesService
 */
public class MoveSearchesResult {

	private boolean status;
	private boolean moveToProjectMarkedForDeletion;
	private boolean moveToProjectDisabled;

	
	public boolean isMoveToProjectMarkedForDeletion() {
		return moveToProjectMarkedForDeletion;
	}

	public void setMoveToProjectMarkedForDeletion(
			boolean moveToProjectMarkedForDeletion) {
		this.moveToProjectMarkedForDeletion = moveToProjectMarkedForDeletion;
	}

	public boolean isMoveToProjectDisabled() {
		return moveToProjectDisabled;
	}

	public void setMoveToProjectDisabled(boolean moveToProjectDisabled) {
		this.moveToProjectDisabled = moveToProjectDisabled;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}

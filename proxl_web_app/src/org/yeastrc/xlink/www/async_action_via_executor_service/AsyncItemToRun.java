package org.yeastrc.xlink.www.async_action_via_executor_service;

import org.yeastrc.xlink.www.constants.AsyncItemToRunPriorityConstants;

/**
 * 
 */
public class AsyncItemToRun {

	private AsyncItemToRun( Runnable runnable, int priority) {
		this.runnable = runnable;
		this.priority = priority;
	}

	/**
	 * Strongly prefer that getInstance( Runnable runnable, int priority) is called.
	 * @param runnable
	 * @return
	 */
	public static AsyncItemToRun getInstance( Runnable runnable ) {
		return new AsyncItemToRun( runnable, AsyncItemToRunPriorityConstants.PRIORITY_WHEN_NOT_SPECIFIED );
	}
	public static AsyncItemToRun getInstance( Runnable runnable, int priority) {
		return new AsyncItemToRun( runnable, priority );
	}
	private Runnable runnable;
	private int priority;
	
	public Runnable getRunnable() {
		return runnable;
	}
	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

}

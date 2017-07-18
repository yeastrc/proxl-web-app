package org.yeastrc.xlink.www.async_action_via_executor_service;

import org.yeastrc.xlink.www.constants.AsyncItemToRunPriorityConstants;

public class AsyncItemToRunFactory {

	/**
	 * Strongly prefer that createAsyncItemToRun( Runnable runnable, int priority) is called.
	 * Uses priority AsyncItemToRunPriorityConstants.PRIORITY_WHEN_NOT_SPECIFIED
	 * 
	 * @param runnable
	 * @return
	 */
	public static AsyncItemToRun createAsyncItemToRun( Runnable runnable ) {
		return new AsyncItemToRun( runnable, AsyncItemToRunPriorityConstants.PRIORITY_WHEN_NOT_SPECIFIED );
	}
	public static AsyncItemToRun createAsyncItemToRun( Runnable runnable, int priority) {
		return new AsyncItemToRun( runnable, priority );
	}
}

package org.yeastrc.xlink.www.async_action_via_executor_service;


/**
 * 
 */
public class AsyncItemToRun {

	AsyncItemToRun( Runnable runnable, int priority) {
		this.runnable = runnable;
		this.priority = priority;
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

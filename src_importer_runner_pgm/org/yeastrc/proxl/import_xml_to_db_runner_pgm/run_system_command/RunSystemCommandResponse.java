package org.yeastrc.proxl.import_xml_to_db_runner_pgm.run_system_command;

/**
 * commandExitCode, stdOut, and stdErr from running a system command
 * 
 * commandSuccessful = true if commandExitCode == zero
 */
public class RunSystemCommandResponse {

	private boolean commandSuccessful;
	
	private int commandExitCode;

	private boolean shutdownRequested;
	
	


	public int getCommandExitCode() {
		return commandExitCode;
	}
	public void setCommandExitCode(int commandExitCode) {
		this.commandExitCode = commandExitCode;
	}
	public boolean isCommandSuccessful() {
		return commandSuccessful;
	}
	public void setCommandSuccessful(boolean commandSuccessful) {
		this.commandSuccessful = commandSuccessful;
	}
	public boolean isShutdownRequested() {
		return shutdownRequested;
	}
	public void setShutdownRequested(boolean shutdownRequested) {
		this.shutdownRequested = shutdownRequested;
	}
}

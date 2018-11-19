package org.yeastrc.proxl.import_xml_to_db.objects;

import java.io.File;
import java.util.List;

public class ImportResults {

	/**
	 * was import successful
	 */
	private boolean importSuccessStatus;
	

	/**
	 * was help requested
	 */
	private boolean helpRequestedStatus;
	
	
	/**
	 * suggested program exit code
	 */
	private int programExitCode;
	
	/**
	 * inserted search id
	 */
	private int searchId;
	
	private File importedProxlXMLFile;
	
	private List<File> scanFileList;
	

	/**
	 * was import successful
	 * @return
	 */
	public boolean isImportSuccessStatus() {
		return importSuccessStatus;
	}

	public void setImportSuccessStatus(boolean importSuccessStatus) {
		this.importSuccessStatus = importSuccessStatus;
	}

	/**
	 * was help requested
	 * @return
	 */
	public boolean isHelpRequestedStatus() {
		return helpRequestedStatus;
	}

	public void setHelpRequestedStatus(boolean helpRequestedStatus) {
		this.helpRequestedStatus = helpRequestedStatus;
	}



	/**
	 * suggested program exit code
	 * @return
	 */
	public int getProgramExitCode() {
		return programExitCode;
	}

	public void setProgramExitCode(int programExitCode) {
		this.programExitCode = programExitCode;
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public File getImportedProxlXMLFile() {
		return importedProxlXMLFile;
	}

	public void setImportedProxlXMLFile(File importedProxlXMLFile) {
		this.importedProxlXMLFile = importedProxlXMLFile;
	}

	public List<File> getScanFileList() {
		return scanFileList;
	}

	public void setScanFileList(List<File> scanFileList) {
		this.scanFileList = scanFileList;
	}

}

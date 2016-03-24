package org.yeastrc.proxl.import_xml_to_db.objects;

import java.io.File;
import java.util.List;

public class ImportResults {

	private int searchId;
	
	private File importedProxlXMLFile;
	
	private List<File> scanFileList;
	
	

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

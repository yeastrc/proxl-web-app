package org.yeastrc.proxl.import_xml_to_db.objects;

import java.io.File;

import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;

public class ScanFileFileContainer {

	private File scanFile;
	
	/**
	 * When running the Import from the Run Importer Process
	 */
	private ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord;

	public File getScanFile() {
		return scanFile;
	}

	public void setScanFile(File scanFile) {
		this.scanFile = scanFile;
	}

	public ProxlXMLFileImportTrackingSingleFileDTO getScanFileDBRecord() {
		return scanFileDBRecord;
	}

	public void setScanFileDBRecord(
			ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord) {
		this.scanFileDBRecord = scanFileDBRecord;
	}

}

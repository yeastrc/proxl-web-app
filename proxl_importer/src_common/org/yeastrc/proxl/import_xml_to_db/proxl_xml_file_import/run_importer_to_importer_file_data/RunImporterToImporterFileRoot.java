package org.yeastrc.proxl.import_xml_to_db.proxl_xml_file_import.run_importer_to_importer_file_data;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Root of object graph passed from Run Importer Program to Importer Program by serializing to a file.
 *
 */
@XmlRootElement(name="runImporterToImporterFileRoot")
public class RunImporterToImporterFileRoot {

	private int importTrackingRunId;
	
	private int projectId;
	

	//  TODO  Not currently used
	
//	private String outputImportResultFileName;
	
	
	
	private String outputDataErrorsFileName;


	private boolean skipPopulatingPathOnSearch;
	
	
//	/**
//	 * Not currently used
//	 * 
//	 * The string sent to the importer on the System.in to request it to shut down
//	 */
//	private byte[] systemInStringForShutdown;
//
//	/**
//	 * Not currently used
//	 * 
//	 * The string sent to the importer on the System.in to request it to shut down
//	 * @return
//	 */
//	public byte[] getSystemInStringForShutdown() {
//		return systemInStringForShutdown;
//	}
//
//	/**
//	 * Not currently used
//	 * 
//	 * The string sent to the importer on the System.in to request it to shut down
//	 * @param systemInStringForShutdown
//	 */
//	public void setSystemInStringForShutdown(byte[] systemInStringForShutdown) {
//		this.systemInStringForShutdown = systemInStringForShutdown;
//	}
	
	

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getOutputDataErrorsFileName() {
		return outputDataErrorsFileName;
	}

	public void setOutputDataErrorsFileName(String outputDataErrorsFileName) {
		this.outputDataErrorsFileName = outputDataErrorsFileName;
	}

	public boolean isSkipPopulatingPathOnSearch() {
		return skipPopulatingPathOnSearch;
	}

	public void setSkipPopulatingPathOnSearch(boolean skipPopulatingPathOnSearch) {
		this.skipPopulatingPathOnSearch = skipPopulatingPathOnSearch;
	}

	public int getImportTrackingRunId() {
		return importTrackingRunId;
	}

	public void setImportTrackingRunId(int importTrackingRunId) {
		this.importTrackingRunId = importTrackingRunId;
	}

}

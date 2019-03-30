package org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils;

import java.io.File;
import java.io.IOException;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.www.exceptions.ProxlWebappFileUploadFileSystemException;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadWebConstants;
/**
 * 
 *
 */
public class Proxl_XML_Importer_Work_Directory_And_SubDirs_Web {

	private static final Logger log = LoggerFactory.getLogger(  Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.class );
	//  private constructor
	private Proxl_XML_Importer_Work_Directory_And_SubDirs_Web() { }
	/**
	 * @return newly created instance
	 */
	public static Proxl_XML_Importer_Work_Directory_And_SubDirs_Web getInstance() { 
		return new Proxl_XML_Importer_Work_Directory_And_SubDirs_Web(); 
	}
	/**
	 * @return
	 */
	public String getDirForUploadFileTempDir( ) {
		String dirName = ProxlXMLFileUploadWebConstants.UPLOAD_FILE_TEMP_BASE_DIR;
		return dirName;
	}
	
	/**
	 * @param authUserId
	 * @param uploadKey
	 * @param uploadTempBase
	 * @return null if subdir already exists
	 * @throws ProxlWebappFileUploadFileSystemException 
	 * @throws IOException 
	 */
	public File createSubDirForUploadFileTempDir( int authUserId, long uploadKey, File uploadTempBase ) throws ProxlWebappFileUploadFileSystemException, IOException {
		File subdir = getSubDirForUploadFileTempDir( authUserId, uploadKey, uploadTempBase );
		if ( subdir.exists() ) {
			//  Subdir already exists so need new uploadKey to create unique subdir
			return null;
		}
		if ( ! subdir.mkdir() ) {
			String msg = "Failed to make temp upload subdir: " + subdir.getCanonicalPath();
			log.error( msg );
			throw new ProxlWebappFileUploadFileSystemException( msg );
		}
		return subdir;
	}
	
	/**
	 * @param authUserId
	 * @param uploadKey
	 * @param uploadTempBase
	 * @return
	 * @throws ProxlWebappFileUploadFileSystemException 
	 * @throws IOException 
	 */
	public File getSubDirForUploadFileTempDir( int authUserId, long uploadKey, File uploadTempBase ) throws ProxlWebappFileUploadFileSystemException, IOException {
		String subdirName = ProxlXMLFileUploadWebConstants.UPLOAD_FILE_TEMP_SUB_DIR_PREFIX 
				+ authUserId + "_" + uploadKey;
		File subdir = new File( uploadTempBase, subdirName );
		return subdir;
	}
	
	/**
	 * @param fileIndex
	 * @param uploadTempBase
	 * @return
	 */
	public File getDataFile( int fileIndex, File uploadTempBase ) {
		String dataFileName = ProxlXMLFileUploadWebConstants.UPLOAD_FILE_DATA_FILE_PREFIX
				+ fileIndex
				+ ProxlXMLFileUploadWebConstants.UPLOAD_FILE_DATA_FILE_SUFFIX;
		File dataFile = new File( uploadTempBase, dataFileName );
		return dataFile;
	}
	
	/**
	 * @param fileIndex
	 * @param uploadTempBase
	 * @return
	 * @throws ProxlWebappFileUploadFileSystemException 
	 */
	public File getUploadFile( String scanFileSuffix, int fileIndex, ProxlXMLFileImportFileType fileType, File uploadTempBase ) throws ProxlWebappFileUploadFileSystemException {
		String uploadFilename = null;
		if ( fileType == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
			uploadFilename = ProxlXMLFileUploadWebConstants.UPLOAD_PROXL_XML_FILE_TEMP_FILENAME_PREFIX
					+ fileIndex
					+ ProxlXMLFileUploadWebConstants.UPLOAD_PROXL_XML_FILE_TEMP_FILENAME_SUFFIX;
		} else if ( fileType == ProxlXMLFileImportFileType.SCAN_FILE ) {
			uploadFilename = ProxlXMLFileUploadWebConstants.UPLOAD_SCAN_FILE_TEMP_FILENAME_PREFIX
					+ fileIndex + scanFileSuffix;
		} else {
			String msg = "getUploadFile(...): Unknown value for fileType: " + fileType;
			log.error( msg );
			throw new ProxlWebappFileUploadFileSystemException( msg );
		}
		File uploadFile = new File( uploadTempBase, uploadFilename );
		return uploadFile;
	}
}

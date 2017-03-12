package org.yeastrc.xlink.www.proxl_xml_file_import.utils;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
/**
 * 
 *
 */
public class DeleteDirectoryAndContentsUtil {

	private static final Logger log = Logger.getLogger( DeleteDirectoryAndContentsUtil.class );
	//  private constructor
	private DeleteDirectoryAndContentsUtil() { }
	/**
	 * @return newly created instance
	 */
	public static DeleteDirectoryAndContentsUtil getInstance() { 
		return new DeleteDirectoryAndContentsUtil(); 
	}
	
	/**
	 * @param directory
	 * @throws ProxlWebappInternalErrorException
	 * @throws IOException
	 */
	public void deleteDirectoryAndContents( File directory ) throws ProxlWebappInternalErrorException, IOException {
		File[] directoryContents = directory.listFiles();
		for ( File directoryItem : directoryContents ) {
			if ( directoryItem.isDirectory() ) {
				// recursive call
				deleteDirectoryAndContents( directoryItem );
			}
			if ( ! directoryItem.delete() ) {
				String msg = "Failed to delete file: " + directoryItem.getCanonicalPath();
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
		}
		if ( ! directory.delete() ) {
			String msg = "Failed to delete directory: " + directory.getCanonicalPath();
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
	}
}

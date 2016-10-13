package org.yeastrc.proxl.import_xml_to_db_runner_pgm.delete_directory_and_contents;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * 
 *
 */
public class DeleteDirectoryAndContents {

	private static final Logger log = Logger.getLogger(DeleteDirectoryAndContents.class);

	//  private constructor
	private DeleteDirectoryAndContents() { }
	
	/**
	 * @return newly created instance
	 */
	public static DeleteDirectoryAndContents getInstance() { 
		return new DeleteDirectoryAndContents(); 
	}

	/**
	 * @param directoryToDelete
	 * @throws IOException 
	 */
	public void deleteDirectoryAndContents( File directoryToDelete ) {
		
		deleteDirectoryAndContentsInternal( directoryToDelete );
	}
	
	
	/**
	 * Recursively called to delete sub directories
	 * 
	 * @param directoryToDelete
	 * @throws IOException 
	 */
	private void deleteDirectoryAndContentsInternal( File directoryToDelete ) {
		
		File[] dirContents = directoryToDelete.listFiles();
		
		for ( File dirItem : dirContents ) {
			
			if ( dirItem.isDirectory() ) {
				
				deleteDirectoryAndContentsInternal( dirItem );

				if ( ! dirItem.delete() ) {
					
					String msg = "Failed to delete directory: " + dirItem.getAbsolutePath();
					log.error( msg );
					
				}
			} else {
				
				if ( ! dirItem.delete() ) {
					
					String msg = "Failed to delete file: " + dirItem.getAbsolutePath();
					log.error( msg );
					
				}
			}
		}
		
		if ( ! directoryToDelete.delete() ) {
			
			String msg = "Failed to delete directory: " + directoryToDelete.getAbsolutePath();
			log.error( msg );
			
		}
	}
}

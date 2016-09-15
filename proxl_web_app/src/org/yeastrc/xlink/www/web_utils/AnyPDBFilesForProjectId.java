package org.yeastrc.xlink.www.web_utils;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.searcher.PDBFileSearcher;

public class AnyPDBFilesForProjectId {

	private static final Logger log = Logger.getLogger(AnyPDBFilesForProjectId.class);
	

	//  private constructor
	private AnyPDBFilesForProjectId() { }
	
	/**
	 * @return newly created instance
	 */
	public static AnyPDBFilesForProjectId getInstance() { 
		return new AnyPDBFilesForProjectId(); 
	}
	
	
	/**
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	public boolean anyPDBFilesForProjectId( int projectId ) throws Exception {
		
		boolean anyPDBFilesForProjectId = PDBFileSearcher.getInstance().anyPDBFilesForProjectId( projectId );
		
		return anyPDBFilesForProjectId;
	}
	
}

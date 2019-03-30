package org.yeastrc.xlink.www.web_utils;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.searcher.PDBFileSearcher;

public class AnyPDBFilesForProjectId {

	private static final Logger log = LoggerFactory.getLogger( AnyPDBFilesForProjectId.class);
	//  private constructor
	private AnyPDBFilesForProjectId() { }
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

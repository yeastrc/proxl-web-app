package org.yeastrc.proxl.import_xml_to_db.project_importable_validation;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ProjectLimitedInfoDAO;
import org.yeastrc.xlink.dao.ProjectLockedChecker;

/**
 * Is importing allowed for the project.
 * Importing is not allowed if the project is locked or disabled or marked for deletion
 *
 */
public class IsImportingAllowForProject {

	private static final Logger log = Logger.getLogger( IsImportingAllowForProject.class );
	

	/**
	 * private constructor
	 */
	private IsImportingAllowForProject(){}
	
	public static IsImportingAllowForProject getInstance() {
		
		return new IsImportingAllowForProject();
	}
	
	/**
	 * Is importing allowed for the project.
	 * Importing is not allowed if the project is locked or disabled or marked for deletion
	 * 
	 * @param projectId
	 * @return true if importing allowed
	 * @throws Exception 
	 */
	public boolean isImportingAllowForProject( int projectId ) throws Exception {
		

		try {

			
			Boolean projectLocked = ProjectLockedChecker.getInstance().isProjectLocked( projectId );
			
    		if ( projectLocked == null ) {
    			
    			String msg = "!!!!!!!!!!!     The Project Id specified (" + projectId + ") is not in the database" ;
    			
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( "!!!!");
    			System.err.println( msg );
    			System.err.println( "!!!!");
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( " " );
    			
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( "!!!!");
    			System.out.println( msg );
    			System.out.println( "!!!!");
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( " " );

    			System.err.println( " " );
    			
    			return false;
    			
    		} else if ( projectLocked ) {
    			
    			String msg = "!!!!!!!!!!!!!!      The Project Id specified (" + projectId + ") is locked so no data can be imported for it." ;
    			
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( "!!!!");
    			System.err.println( msg );
    			System.err.println( "!!!!");
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( " " );
    			
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( "!!!!");
    			System.out.println( msg );
    			System.out.println( "!!!!");
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( " " );
    			
    			System.err.println( " " );
    			
    			return false;
    		}
    		
    		
    		

			
			Boolean projectEnabled = ProjectLimitedInfoDAO.getInstance().isProjectEnabled( projectId );
			
    		if ( projectEnabled == null ) {
    			
    			String msg = "!!!!!!!!!!!     The Project Id specified (" + projectId + ") is not in the database" ;
    			
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( "!!!!");
    			System.err.println( msg );
    			System.err.println( "!!!!");
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( " " );
    			
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( "!!!!");
    			System.out.println( msg );
    			System.out.println( "!!!!");
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( " " );

    			System.err.println( " " );
    			
    			return false;
    		
    		} else if ( ! projectEnabled ) {
    			
    			String msg = "!!!!!!!!!!!!!!      The Project Id specified (" + projectId + ") is disabled so no data can be imported for it." ;
    			
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( "!!!!");
    			System.err.println( msg );
    			System.err.println( "!!!!");
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( " " );
    			
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( "!!!!");
    			System.out.println( msg );
    			System.out.println( "!!!!");
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( " " );
    			
    			System.err.println( " " );
    			
    			return false;
    		}
			

			Boolean projectMarkedForDeletion = ProjectLimitedInfoDAO.getInstance().isProjectMarkedForDeletion( projectId );
			
    		if ( projectMarkedForDeletion == null ) {
    			
    			String msg = "!!!!!!!!!!!     The Project Id specified (" + projectId + ") is not in the database" ;
    			
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( "!!!!");
    			System.err.println( msg );
    			System.err.println( "!!!!");
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( " " );
    			
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( "!!!!");
    			System.out.println( msg );
    			System.out.println( "!!!!");
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( " " );

    			System.err.println( " " );
    			
    			return false;
    		
    		} else if ( projectMarkedForDeletion ) {
    			
    			String msg = "!!!!!!!!!!!!!!      The Project Id specified (" + projectId + ") is marked for deletion so no data can be imported for it." ;
    			
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( "!!!!");
    			System.err.println( msg );
    			System.err.println( "!!!!");
    			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.err.println( " " );
    			
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( "!!!!");
    			System.out.println( msg );
    			System.out.println( "!!!!");
    			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    			System.out.println( " " );
    			
    			System.err.println( " " );
    			
    			return false;
    		}
			
    		
		} catch( Exception e ) {
			
			String msg = "Error getting project";
			
			log.error( msg, e );
			
			System.err.println( msg );
			System.err.println( "Error: " + e.getMessage() );
			
			throw e;

		}
				

		return true;
		
	}
	
}

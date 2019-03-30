package org.yeastrc.proxl.import_xml_to_db.project_importable_validation;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterProjectNotAllowImportException;
import org.yeastrc.xlink.dao.ProjectLimitedInfoDAO;
import org.yeastrc.xlink.dao.ProjectLockedChecker;

/**
 * Is importing allowed for the project.
 * Importing is not allowed if the project is locked or disabled or marked for deletion
 *
 */
public class IsImportingAllowForProject {

	private static final Logger log = LoggerFactory.getLogger(  IsImportingAllowForProject.class );
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
	 * Throws ProxlImporterProjectNotAllowImportException if not allowed to impoort to project
	 * 
	 * @param projectId
	 * @throws ProxlImporterProjectNotAllowImportException if specific  
	 * @throws Exception if system error
	 */
	public void isImportingAllowForProject( int projectId ) 
			throws ProxlImporterProjectNotAllowImportException, Exception {
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
    			ProxlImporterProjectNotAllowImportException proxlImporterProjectNotAllowImportException =
    					new ProxlImporterProjectNotAllowImportException( msg );
    			proxlImporterProjectNotAllowImportException.setNotAllowedReason(
    					ProxlImporterProjectNotAllowImportException.NotAllowedReason.PROJECT_NOT_IN_DATABASE );
    			throw proxlImporterProjectNotAllowImportException;
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
    			ProxlImporterProjectNotAllowImportException proxlImporterProjectNotAllowImportException =
    					new ProxlImporterProjectNotAllowImportException( msg );
    			proxlImporterProjectNotAllowImportException.setNotAllowedReason(
    					ProxlImporterProjectNotAllowImportException.NotAllowedReason.PROJECT_LOCKED );
    			throw proxlImporterProjectNotAllowImportException;
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
    			ProxlImporterProjectNotAllowImportException proxlImporterProjectNotAllowImportException =
    					new ProxlImporterProjectNotAllowImportException( msg );
    			proxlImporterProjectNotAllowImportException.setNotAllowedReason(
    					ProxlImporterProjectNotAllowImportException.NotAllowedReason.PROJECT_NOT_IN_DATABASE );
    			throw proxlImporterProjectNotAllowImportException;
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
    			ProxlImporterProjectNotAllowImportException proxlImporterProjectNotAllowImportException =
    					new ProxlImporterProjectNotAllowImportException( msg );
    			proxlImporterProjectNotAllowImportException.setNotAllowedReason(
    					ProxlImporterProjectNotAllowImportException.NotAllowedReason.PROJECT_NOT_ENABLED );
    			throw proxlImporterProjectNotAllowImportException;
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
    			ProxlImporterProjectNotAllowImportException proxlImporterProjectNotAllowImportException =
    					new ProxlImporterProjectNotAllowImportException( msg );
    			proxlImporterProjectNotAllowImportException.setNotAllowedReason(
    					ProxlImporterProjectNotAllowImportException.NotAllowedReason.PROJECT_NOT_IN_DATABASE );
    			throw proxlImporterProjectNotAllowImportException;
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
    			ProxlImporterProjectNotAllowImportException proxlImporterProjectNotAllowImportException =
    					new ProxlImporterProjectNotAllowImportException( msg );
    			proxlImporterProjectNotAllowImportException.setNotAllowedReason(
    					ProxlImporterProjectNotAllowImportException.NotAllowedReason.PROJECT_MARKED_FOR_DELETION );
    			throw proxlImporterProjectNotAllowImportException;
    		}
		} catch ( ProxlImporterProjectNotAllowImportException e ) {
			throw e;
		} catch( Exception e ) {
			String msg = "Error getting project";
			log.error( msg, e );
			System.err.println( msg );
			System.err.println( "Error: " + e.getMessage() );
			throw e;
		}
		//  Project has no reason for not importing so just return
	}
}

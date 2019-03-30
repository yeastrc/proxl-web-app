package org.yeastrc.xlink.base.file_import_proxl_xml_scans.populate_dto_from_result;

import java.sql.ResultSet;
import java.sql.SQLException;


import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;

/**
 * 
 *
 */
public class ProxlXMLFileImportTracking_PopulateDTO {

//	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportTracking_PopulateDTO.class);
	

	//  private constructor
	private ProxlXMLFileImportTracking_PopulateDTO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_PopulateDTO getInstance() { 
		return new ProxlXMLFileImportTracking_PopulateDTO(); 
	}
	

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public ProxlXMLFileImportTrackingDTO populateResultObject(ResultSet rs) throws SQLException {
		
		ProxlXMLFileImportTrackingDTO returnItem = new ProxlXMLFileImportTrackingDTO();
		
		returnItem.setId( rs.getInt( "id" ) );
		
		returnItem.setProjectId( rs.getInt( "project_id" ) );
		returnItem.setPriority( rs.getInt( "priority" ) );
		returnItem.setAuthUserId( rs.getInt( "auth_user_id" ) );

		returnItem.setStatus( ProxlXMLFileImportStatus.fromValue( rs.getInt( "status_id" ) ) );
		
		returnItem.setRemoteUserIpAddress( rs.getString( "remote_user_ip_address" ) );
		
		int markedForDeletionInt = rs.getInt( "marked_for_deletion" );
		
		if ( markedForDeletionInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {

			returnItem.setMarkedForDeletion( true );
		} else {
			returnItem.setMarkedForDeletion( false );
		}
		
		returnItem.setSearchName( rs.getString( "search_name" ) );
		returnItem.setSearchPath( rs.getString( "search_path" ) );
		
		returnItem.setInsertRequestURL( rs.getString( "insert_request_url" ) );
		
		returnItem.setRecordInsertDateTime( rs.getTimestamp("record_insert_date_time" ) );
		returnItem.setImportStartDateTime( rs.getTimestamp( "import_start_date_time" ) );
		returnItem.setImportEndDateTime( rs.getTimestamp( "import_end_date_time" ) );
		returnItem.setLastUpdatedDateTime( rs.getTimestamp( "last_updated_date_time" ) );
		
		int DeletedByAuthUserId = rs.getInt( "deleted_by_auth_user_id" );
		
		if ( ! rs.wasNull() ) {
			returnItem.setDeletedByAuthUserId( DeletedByAuthUserId );
		}
		returnItem.setDeletedDateTime( rs.getDate( "last_updated_date_time" ) );
		 
		
		return returnItem;
	}

}

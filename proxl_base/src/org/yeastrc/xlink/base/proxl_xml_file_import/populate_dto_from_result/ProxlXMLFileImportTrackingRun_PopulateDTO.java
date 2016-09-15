package org.yeastrc.xlink.base.proxl_xml_file_import.populate_dto_from_result;

import java.sql.ResultSet;
import java.sql.SQLException;



import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
//import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;

/**
 * table proxl_xml_file_import_tracking_run
 *
 */
public class ProxlXMLFileImportTrackingRun_PopulateDTO {

//	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingRun_PopulateDTO.class);
	

	//  private constructor
	private ProxlXMLFileImportTrackingRun_PopulateDTO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingRun_PopulateDTO getInstance() { 
		return new ProxlXMLFileImportTrackingRun_PopulateDTO(); 
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public ProxlXMLFileImportTrackingRunDTO populateResultObject(ResultSet rs) throws SQLException {
		
		ProxlXMLFileImportTrackingRunDTO returnItem = new ProxlXMLFileImportTrackingRunDTO();
		
		returnItem.setId( rs.getInt( "id" ) );

		int currentRunInt = rs.getInt( "current_run" );
		
		if ( currentRunInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {

			returnItem.setCurrentRun( true );
		} else {
			returnItem.setCurrentRun( false );
		}
		
		returnItem.setProxlXmlFileImportTrackingId( rs.getInt( "proxl_xml_file_import_tracking_id" ) );
		
		returnItem.setRunStatus( ProxlXMLFileImportStatus.fromValue( rs.getInt( "status_id" ) ) );
		
		//  importer_sub_status_id
		
		returnItem.setImportResultText( rs.getString( "import_result_text" ) );
		returnItem.setDataErrorText( rs.getString( "data_error_text" ) );
		
		returnItem.setStartDateTime( rs.getDate( "start_date_time" ) );
		returnItem.setLastUpdatedDateTime( rs.getDate( "last_updated_date_time" ) );
		
		return returnItem;
	}
	
}

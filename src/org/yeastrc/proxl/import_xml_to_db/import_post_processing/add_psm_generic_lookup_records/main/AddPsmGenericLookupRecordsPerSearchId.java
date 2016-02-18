package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_psm_generic_lookup_records.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PsmFilterableAnnotationGenericLookupDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmFilterableAnnotationGenericLookupDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.utils.XLinkUtils;



public class AddPsmGenericLookupRecordsPerSearchId {


	private static final Logger log = Logger.getLogger(AddPsmGenericLookupRecordsPerSearchId.class);

	// private constructor
	private AddPsmGenericLookupRecordsPerSearchId() { }
	
	public static AddPsmGenericLookupRecordsPerSearchId getInstance() { 
		return new AddPsmGenericLookupRecordsPerSearchId(); 
	}
	


	private static final String SQL_MAIN = 
			"SELECT psm_annotation.*, psm.search_id, psm.type, psm.reported_peptide_id"
			+ " FROM psm_annotation " 
			+ " INNER JOIN psm ON psm_annotation.psm_id = psm.id "
					
			+ " WHERE  psm.search_id = ? "
			+ " AND filterable_descriptive_type = '" + FilterableDescriptiveAnnotationType.FILTERABLE + "'"; 
			
	
	public void addPsmGenericLookupRecordsPerSearchId( int searchId ) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SQL_MAIN;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				PsmFilterableAnnotationGenericLookupDTO item = new PsmFilterableAnnotationGenericLookupDTO();
				
				item.setPsmAnnotationId( rs.getInt( "id" ) );
				item.setPsmId( rs.getInt( "psm_id" ) );
				item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
				item.setValueDouble( rs.getDouble( "value_double" ) );
				item.setValueString( rs.getString( "value_string" ) );
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );


				String typeString = rs.getString( "type" );
				int typeNumber = XLinkUtils.getTypeNumber( typeString );
				
				item.setType( typeNumber );
				
				
				PsmFilterableAnnotationGenericLookupDAO.getInstance().saveToDatabase( item , conn );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getBestAnnotationValue(), sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
}

package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.enum_classes.AnnotationValueLocation;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Table psm_annotation
 *
 */
public class PsmAnnotationDAO {
	
	private static final Logger log = LoggerFactory.getLogger( PsmAnnotationDAO.class);

	private PsmAnnotationDAO() { }
	public static PsmAnnotationDAO getInstance() { return new PsmAnnotationDAO(); }
	
	/**
	 * Get the given psm_annotation from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PsmAnnotationDTO getItem( int id ) throws Exception {
		
		PsmAnnotationDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM psm_annotation WHERE id = ?";
		
		

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet(rs);
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return item;
	}
	
	
	
	/**
	 * @param rs
	 * @return
	 * @throws Exception 
	 */
	public PsmAnnotationDTO populateFromResultSet(ResultSet rs)
			throws Exception {
	
		
		PsmAnnotationDTO item;
		item = new PsmAnnotationDTO();
		
		AnnotationValueLocation annotationValueLocation = AnnotationValueLocation.fromValue( rs.getString( "value_location" )  );
		
		item.setId( rs.getInt( "id" ) );
		item.setPsmId( rs.getInt( "psm_id" ) );
		item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.fromValue( rs.getString( "filterable_descriptive_type" )  ) );
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		item.setAnnotationValueLocation( annotationValueLocation );
		item.setValueDouble( rs.getDouble( "value_double" ) );
		item.setValueString( rs.getString( "value_string" ) );
		
		if ( annotationValueLocation == AnnotationValueLocation.LARGE_VALUE_TABLE ) {
			
			//  Get valueString from large value table instead
			
			String valueString = PsmAnnotationLargeValueDAO.getInstance().getValueString( item.getId() );
			item.setValueString( valueString );
		}
		
		return item;
	}
	
}

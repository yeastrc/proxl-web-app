package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.enum_classes.AnnotationValueLocation;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Table srch__rep_pept__annotation
 *
 */
public class SearchReportedPeptideAnnotationDAO {
	
	private static final Logger log = LoggerFactory.getLogger( SearchReportedPeptideAnnotationDAO.class);

	private SearchReportedPeptideAnnotationDAO() { }
	public static SearchReportedPeptideAnnotationDAO getInstance() { return new SearchReportedPeptideAnnotationDAO(); }
	
	/**
	 * Get the given srch__rep_pept__annotation from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchReportedPeptideAnnotationDTO getItem( int id ) throws Exception {
		
		SearchReportedPeptideAnnotationDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM srch__rep_pept__annotation WHERE id = ?";


		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
	 * Get the given srch__rep_pept__annotation from the database
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @param peptideFilterableAnnotationTypeId
	 * @return
	 * @throws Exception
	 */
	public SearchReportedPeptideAnnotationDTO getItemForSearchIdReportedPeptideIdAnnotationId( 
			
			int searchId,
			int reportedPeptideId,
			int peptideFilterableAnnotationTypeId ) throws Exception {
		
		SearchReportedPeptideAnnotationDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * FROM srch__rep_pept__annotation WHERE search_id = ? AND reported_peptide_id = ? AND annotation_type_id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			pstmt.setInt( 3, peptideFilterableAnnotationTypeId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
	public SearchReportedPeptideAnnotationDTO populateFromResultSet( ResultSet rs ) throws Exception {
		
		SearchReportedPeptideAnnotationDTO item;
		item = new SearchReportedPeptideAnnotationDTO();

		AnnotationValueLocation annotationValueLocation = AnnotationValueLocation.fromValue( rs.getString( "value_location" )  );
		
		item.setId( rs.getInt( "id" ) );
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
		
		item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.fromValue( rs.getString( "filterable_descriptive_type" ) ) );
		
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		item.setAnnotationValueLocation( annotationValueLocation );

		item.setValueDouble( rs.getDouble( "value_double" ) );
		item.setValueString( rs.getString( "value_string" ) );

		if ( annotationValueLocation == AnnotationValueLocation.LARGE_VALUE_TABLE ) {
			
			//  Get valueString from large value table instead
			
			String valueString = SearchReportedPeptideAnnotationLargeValueDAO.getInstance().getValueString( item.getId() );
			item.setValueString( valueString );
		}
		
		return item;
	}

}

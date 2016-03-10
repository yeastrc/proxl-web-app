package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.AnnotationMinMaxFilterableValues;

/**
 * 
 *
 */
public class PsmMinMaxForSearchIdAnnotationTypeIdSearcher {

	private PsmMinMaxForSearchIdAnnotationTypeIdSearcher() { }
	public static PsmMinMaxForSearchIdAnnotationTypeIdSearcher getInstance() { return new PsmMinMaxForSearchIdAnnotationTypeIdSearcher(); }

	private static final Logger log = Logger.getLogger(PsmMinMaxForSearchIdAnnotationTypeIdSearcher.class);
	

	
			
	/**
	 * @param searchId
	 * @param annotationTypeId
	 * @return
	 * @throws Exception
	 */
	public AnnotationMinMaxFilterableValues getPsmMinMaxForSearchIdAnnotationTypeIdSearcher( int searchId, int annotationTypeId ) throws Exception {
		
		
		AnnotationMinMaxFilterableValues result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
	
		final String sql = "SELECT MIN(value_double) AS min, MAX(value_double) AS max "
				+ " FROM  psm_filterable_annotation__generic_lookup "
				+ " WHERE search_id = ? AND annotation_type_id = ? ";


		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			
			int pstmtCounter = 0;

			pstmtCounter++;
			pstmt.setInt( pstmtCounter, searchId );
			pstmtCounter++;
			pstmt.setInt( pstmtCounter, annotationTypeId );

			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				double minValue = rs.getDouble( "min" );
				double maxValue = rs.getDouble( "max" );
				
				result = new AnnotationMinMaxFilterableValues();
				
				result.setMinValue( minValue );
				result.setMaxValue( maxValue );
			}


		} catch ( Exception e ) {

			log.error( "ERROR getPsmMinMaxForSearchIdAnnotationTypeIdSearcher(...):  SQL: " + sql, e );

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
		
		return result;		
	}
	
}

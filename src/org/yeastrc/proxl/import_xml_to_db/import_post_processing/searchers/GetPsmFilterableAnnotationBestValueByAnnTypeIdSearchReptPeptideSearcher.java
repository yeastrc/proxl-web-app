package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * 
 * Get "best" value from psm_annotation for annotation_type_id, search_id and reported_peptide_id
 */
public class GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher {
	
	private static final Logger log = Logger.getLogger(GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher.class);
	
	private GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher() { }
	private static final GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher _INSTANCE = new GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher();
	public static GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher getInstance() { return _INSTANCE; }
	
	

	/**
	 * Get "best" value from psm_annotation for annotation_type_id, search_id and reported_peptide_id
	 * 
	 * @param annotation_type_id
	 * @param search_id
	 * @param reported_peptide_id
	 * @param filterDirection - for annotation_type based on annotation_type_id
	 * @return null if no record found for selection criteria
	 * @throws Exception
	 */
	public BestFilterableAnnotationValue getBestAnnotationValue( int annotation_type_id, int search_id, int reported_peptide_id, FilterDirectionType filterDirectionType ) throws Exception {
		
		BestFilterableAnnotationValue result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String orderDirection = null;
		

		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			
			orderDirection = "DESC";  //  Largest best so sort so largest is first
					
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			
			orderDirection = "ASC";  //  Smallest best so sort so smallest is first
			
		} else {
			
			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
		}

		
		final String sql = 
				"SELECT value_double, value_string FROM psm_annotation " 
						+ " INNER JOIN psm ON psm_annotation.psm_id = psm.id "
						+ " WHERE annotation_type_id = ? "
						+ " AND  psm.search_id = ? AND psm.reported_peptide_id = ? " 
						+ " ORDER BY value_double " + orderDirection + " LIMIT 1 ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, annotation_type_id );
			pstmt.setInt( 2, search_id );
			pstmt.setInt( 3, reported_peptide_id );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {

				result = new BestFilterableAnnotationValue();
				
				
				result.setBestValue( rs.getDouble( "value_double" ) );
				result.setBestValueString( rs.getString( "value_string" ) );
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
		
		return result;
	}
	
	
}

package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.SearcherGeneralConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * 
 * Get psm count from psm_filterable_annotation__generic_lookup for default filter value, annotation_type_id, search_id and reported_peptide_id
 */
public class GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher {
	
	private static final Logger log = Logger.getLogger(GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher.class);
	
	private GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher() { }
	private static final GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher _INSTANCE = new GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher();
	public static GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher getInstance() { return _INSTANCE; }
	
	

	private static final String SQL_MAIN = 
			"SELECT COUNT(*) AS count FROM psm_filterable_annotation__generic_lookup " 
					+ " WHERE psm_filterable_annotation__generic_lookup.annotation_type_id = ? "
					+ " AND  psm_filterable_annotation__generic_lookup.search_id = ? "
					+ " AND psm_filterable_annotation__generic_lookup.reported_peptide_id = ? " 
					+ " AND  psm_filterable_annotation__generic_lookup.value_double ";

	/**
	 * Get psm count from psm_filterable_annotation__generic_lookup for default filter value, annotation_type_id, search_id and reported_peptide_id
	 * 
	 * @param annotation_type_id
	 * @param search_id
	 * @param reported_peptide_id
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForCutoffValue( int annotation_type_id, int search_id, int reported_peptide_id, double cutoffValue, FilterDirectionType filterDirectionType ) throws Exception {
		
		
		int result = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = null;
		
		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			
			sql = SQL_MAIN + SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER + " ? ";
					
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			
			sql = SQL_MAIN + SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER + " ? ";
			
		} else {
			
			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
		}
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, annotation_type_id );
			pstmt.setInt( 2, search_id );
			pstmt.setInt( 3, reported_peptide_id );
			pstmt.setDouble( 4, cutoffValue );
			
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				result = rs.getInt( "count" );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getPsmCountForDefaultFilterValue(), sql: " + sql;
			
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

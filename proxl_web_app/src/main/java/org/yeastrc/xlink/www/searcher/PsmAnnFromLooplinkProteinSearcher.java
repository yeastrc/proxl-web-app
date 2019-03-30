package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * Retrieve PSM Filterable annotation data for specified looplink protein seq id and positions 
 *
 */
public class PsmAnnFromLooplinkProteinSearcher {
	
	private static final Logger log = LoggerFactory.getLogger(  PsmAnnFromLooplinkProteinSearcher.class );
	private PsmAnnFromLooplinkProteinSearcher() { }
	private static final PsmAnnFromLooplinkProteinSearcher _INSTANCE = new PsmAnnFromLooplinkProteinSearcher();
	public static PsmAnnFromLooplinkProteinSearcher getInstance() { return _INSTANCE; }
	
	private final String SQL = 
			"SELECT psm_fltrbl_tbl.psm_id, psm_fltrbl_tbl.annotation_type_id, psm_fltrbl_tbl.value_double "
			+ " FROM "
			+ " srch_rep_pept__prot_seq_id_pos_looplink AS srpnipl "
			+ " INNER JOIN psm_filterable_annotation__generic_lookup AS psm_fltrbl_tbl "
			+ 	" ON srpnipl.search_id = psm_fltrbl_tbl.search_id AND srpnipl.reported_peptide_id  = psm_fltrbl_tbl.reported_peptide_id "
			+ " WHERE  "
			+ "  srpnipl.search_id = ? "
			+ " AND srpnipl.protein_sequence_version_id = ? "
			+ " AND srpnipl.protein_sequence_position_1 = ? "
			+ " AND srpnipl.protein_sequence_position_2 = ?  ";
	/**
	 * @param searchId
	 * @param proteinId
	 * @param proteinPosition1
	 * @param proteinPosition2
	 * @return Map<PSM_ID,Map<AnnTypeId,Value>
	 * @throws Exception
	 */
	public Map<Integer,Map<Integer,Double>> searchOnSearchProteinLooplink( 
			int searchId,
			int proteinId,
			int proteinPosition1,
			int proteinPosition2 ) throws Exception {
		Map<Integer,Map<Integer,Double>> result = new HashMap<>();
		final String sql = SQL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, proteinId );
			paramCounter++;
			pstmt.setInt( paramCounter, proteinPosition1 );
			paramCounter++;
			pstmt.setInt( paramCounter, proteinPosition2 );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				Integer psm_id = rs.getInt( "psm_id" );
				Integer annotation_type_id = rs.getInt( "annotation_type_id" );
				Double value_double = rs.getDouble( "value_double" );
				Map<Integer,Double> resultPerPsmID = result.get( psm_id );
				if ( resultPerPsmID == null ) {
					resultPerPsmID = new HashMap<>();
					result.put( psm_id,resultPerPsmID );
				}
				resultPerPsmID.put(annotation_type_id, value_double);
			}
		} catch (Exception e ) {
			String msg = "Error: SQL: " + sql;
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

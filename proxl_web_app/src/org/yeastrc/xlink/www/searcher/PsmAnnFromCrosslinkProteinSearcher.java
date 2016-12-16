package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Retrieve PSM Filterable annotation data for specified crosslink protein seq id and positions 
 *
 */
public class PsmAnnFromCrosslinkProteinSearcher {

	private static final Logger log = Logger.getLogger( PsmAnnFromCrosslinkProteinSearcher.class );

	private PsmAnnFromCrosslinkProteinSearcher() { }
	private static final PsmAnnFromCrosslinkProteinSearcher _INSTANCE = new PsmAnnFromCrosslinkProteinSearcher();
	public static PsmAnnFromCrosslinkProteinSearcher getInstance() { return _INSTANCE; }

	
	private final String SQL = 
			"SELECT psm_fltrbl_tbl.psm_id, psm_fltrbl_tbl.annotation_type_id, psm_fltrbl_tbl.value_double "
			+ " FROM "
			+ " srch_rep_pept__prot_seq_id_pos_crosslink AS srpnipc_1 "
			+ 	" INNER JOIN "
			+ " srch_rep_pept__prot_seq_id_pos_crosslink AS srpnipc_2 "
			+ 	" ON srpnipc_1.reported_peptide_id = srpnipc_2.reported_peptide_id"
			+ 		" AND  srpnipc_1.search_reported_peptide_peptide_id "
			+ 				" != srpnipc_2.search_reported_peptide_peptide_id "

			+ " INNER JOIN psm_filterable_annotation__generic_lookup AS psm_fltrbl_tbl "
			+ 	" ON srpnipc_1.search_id = psm_fltrbl_tbl.search_id AND srpnipc_1.reported_peptide_id  = psm_fltrbl_tbl.reported_peptide_id "
			
			+ " WHERE  "
			+ 	  " srpnipc_1.search_id = ? AND srpnipc_1.protein_sequence_id = ? AND srpnipc_1.protein_sequence_position = ? "
			+ " AND srpnipc_2.search_id = ? AND srpnipc_2.protein_sequence_id = ? AND srpnipc_2.protein_sequence_position = ?  ";


	/**
	 * @param searchId
	 * @param protein1Id
	 * @param protein2Id
	 * @param protein1Position
	 * @param protein2Position
	 * @return Map<PSM_ID,Map<AnnTypeId,Value>
	 * @throws Exception
	 */
	public Map<Integer,Map<Integer,Double>> searchOnSearchProteinCrosslink( 
			int searchId,
			int protein1Id,
			int protein2Id,
			int protein1Position,
			int protein2Position ) throws Exception {

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
			pstmt.setInt( paramCounter, protein1Id );
			paramCounter++;
			pstmt.setInt( paramCounter, protein1Position );

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, protein2Id );
			paramCounter++;
			pstmt.setInt( paramCounter, protein2Position );

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

package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;

/**
 *
 */
public class ProteinSequenceVersionIdForNrseqProteinIdSearcher {

	private static final Logger log = Logger.getLogger(ProteinSequenceVersionIdForNrseqProteinIdSearcher.class);
	private ProteinSequenceVersionIdForNrseqProteinIdSearcher() { }
	private static final ProteinSequenceVersionIdForNrseqProteinIdSearcher _INSTANCE = new ProteinSequenceVersionIdForNrseqProteinIdSearcher();
	public static ProteinSequenceVersionIdForNrseqProteinIdSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get Protein Sequence Version Id for NRSEQ Protein Id.  Return null if not found
	 * 
	 * @param nrseqProteinId
	 * @return
	 * @throws Exception
	 */
	public Integer getProteinSequenceVersionIdForNrseqProteinIdSearcher( int nrseqProteinId ) throws Exception {
		Integer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT protein_sequence_version_id FROM z_mapping__nrseq_prot_id__prot_seq_version_id WHERE nrseq_protein_id = ? ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, nrseqProteinId );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				result = rs.getInt( "protein_sequence_version_id" );
			}
			if ( rs.next() ) {
				String msg = "getProteinSequenceVersionIdForNrseqProteinIdSearcher(), Unexpected more than 1 record.  "
						+ "nrseqProteinId: " + nrseqProteinId
						+ ", sql: " + sql;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
		} catch ( Exception e ) {
			String msg = "getCutoffsAppliedOnImportDTOForSearchId(), sql: " + sql;
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

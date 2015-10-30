package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLooplinkLookupDTO;

public class SearchLooplinkLookupDAO {
	
	private static final Logger log = Logger.getLogger(SearchLooplinkLookupDAO.class);

	private SearchLooplinkLookupDAO() { }
	public static SearchLooplinkLookupDAO getInstance() { return new SearchLooplinkLookupDAO(); }

	
	/**
	 * Save the associated data to the database
	 * @param prpl
	 * @throws Exception
	 */
	public void save( SearchLooplinkLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO search_looplink_lookup "
				+ "( search_id, nrseq_id, protein_position_1, protein_position_2, "
				+ " bestPSMQValue, bestPeptideQValue, num_psm_at_pt_01_q_cutoff, num_peptides_at_pt_01_q_cutoff, num_unique_peptides_at_pt_01_q_cutoff ) "
				+ ""
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId() );
			counter++;
			pstmt.setInt( counter,  item.getProteinPosition1() );
			counter++;
			pstmt.setInt( counter,  item.getProteinPosition2() );

			counter++;
			pstmt.setDouble( counter, item.getBestPSMQValue() );
			
			counter++;

			if ( item.getBestPeptideQValue() != null ) {
				pstmt.setDouble( counter, item.getBestPeptideQValue() );
			} else {
				pstmt.setNull( counter, java.sql.Types.DOUBLE );
			}
			
			counter++;
			pstmt.setInt( counter,  item.getNumPsmAtPt01QCutoff() );
			counter++;
			pstmt.setInt( counter,  item.getNumPeptidesAtPt01QCutoff() );
			counter++;
			pstmt.setInt( counter,  item.getNumUniquePeptidesAtPt01QCutoff() );

			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
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
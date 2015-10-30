package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCrosslinkLookupDTO;

public class SearchCrosslinkLookupDAO {

	private static final Logger log = Logger.getLogger(SearchCrosslinkLookupDAO.class);
			
	private SearchCrosslinkLookupDAO() { }
	public static SearchCrosslinkLookupDAO getInstance() { return new SearchCrosslinkLookupDAO(); }

	/**
	 * Save the associated data to the database
	 * @param prpl
	 * @throws Exception
	 */
	public void save( SearchCrosslinkLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO search_crosslink_lookup "

				+ " ( search_id, nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
				+   " bestPSMQValue, bestPeptideQValue, num_psm_at_pt_01_q_cutoff, num_linked_peptides_at_pt_01_q_cutoff, num_unique_peptides_linked_at_pt_01_q_cutoff )"

				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId1() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId2() );
			counter++;
			pstmt.setInt( counter,  item.getProtein1Position() );
			counter++;
			pstmt.setInt( counter,  item.getProtein2Position() );
			
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
			pstmt.setInt( counter,  item.getNumLinkedPeptidesAtPt01QCutoff() );
			counter++;
			pstmt.setInt( counter,  item.getNumUniqueLinkedPeptidesAtPt01QCutoff() );
			
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
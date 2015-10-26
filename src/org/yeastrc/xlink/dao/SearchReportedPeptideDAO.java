package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideDTO;

public class SearchReportedPeptideDAO {
	
	private static final Logger log = Logger.getLogger(SearchReportedPeptideDAO.class);

	private SearchReportedPeptideDAO() { }
	public static SearchReportedPeptideDAO getInstance() { return new SearchReportedPeptideDAO(); }
	
	/**
	 *
	CREATE TABLE search_reported_peptide (
		search_id INT UNSIGNED NOT NULL,
		reported_peptide_id INT UNSIGNED NOT NULL,
		unified_reported_peptide_id  INT UNSIGNED NOT NULL,
		q_value DOUBLE NULL DEFAULT NULL
	);


	 * @param psm
	 * @throws Exception
	 */
	public void saveToDatabase( SearchReportedPeptideDTO prp ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT INTO search_reported_peptide ( search_id, reported_peptide_id, q_value ) VALUES (?, ?, ?)";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, prp.getSearchId() );
			pstmt.setInt( 2, prp.getReportedPeptideId() );
			
			if ( prp.getqValue() != null ) {
				pstmt.setDouble( 3, prp.getqValue() );
			} else {
				
				pstmt.setNull( 3, java.sql.Types.DOUBLE );
			}
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert SearchReportedPeptideDTO: " + prp + ".  SQL: " + sql;
			
			log.error( msg );
			
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
	
	
	/**
	 *	For saving Xquest data, insert duplicates are ignored
	 *
	 * @param psm
	 * @throws Exception
	 */
	public void saveToDatabaseIgnoreDuplicates( SearchReportedPeptideDTO prp ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO search_reported_peptide ( search_id, reported_peptide_id, q_value ) VALUES (?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, prp.getSearchId() );
			pstmt.setInt( 2, prp.getReportedPeptideId() );

			if ( prp.getqValue() != null ) {
				pstmt.setDouble( 3, prp.getqValue() );
			} else {
				
				pstmt.setNull( 3, java.sql.Types.DOUBLE );
			}
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert SearchReportedPeptideDTO: " + prp + ".  SQL: " + sql;
			
			log.error( msg );
			
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

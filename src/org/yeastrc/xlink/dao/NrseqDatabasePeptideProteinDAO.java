package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NrseqDatabasePeptideProteinDTO;

public class NrseqDatabasePeptideProteinDAO {
	
	private static final Logger log = Logger.getLogger(NrseqDatabasePeptideProteinDAO.class);

	private NrseqDatabasePeptideProteinDAO() { }
	public static NrseqDatabasePeptideProteinDAO getInstance() { return new NrseqDatabasePeptideProteinDAO(); }

	public void save( NrseqDatabasePeptideProteinDTO prpp ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO nrseq_database_peptide_protein (nrseq_database_id, peptide_id, nrseq_id, is_unique) VALUES (?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, prpp.getNrseqDatabaseId() );
			pstmt.setInt( 2, prpp.getPeptideId() );
			pstmt.setInt( 3, prpp.getNrseqId() );
			
			if( prpp.isUnique() )
				pstmt.setString( 4, "Y" );
			else
				pstmt.setString( 4, "N" );
			
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

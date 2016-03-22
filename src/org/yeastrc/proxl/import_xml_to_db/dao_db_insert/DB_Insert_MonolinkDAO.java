package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.MonolinkDTO;

public class DB_Insert_MonolinkDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_MonolinkDAO.class);

	private DB_Insert_MonolinkDAO() { }
	public static DB_Insert_MonolinkDAO getInstance() { return new DB_Insert_MonolinkDAO(); }
	

	
	public void save( MonolinkDTO monolink ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO monolink (psm_id, nrseq_id, protein_position, peptide_id, peptide_position) " +
				"VALUES (?, ?, ?, ?, ?)";

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, monolink.getPsm().getId() );
			pstmt.setInt( 2, monolink.getProtein().getNrseqId() );
			pstmt.setInt( 3, monolink.getProteinPosition() );
			pstmt.setInt( 4, monolink.getPeptideId() );
			pstmt.setInt( 5, monolink.getPeptidePosition() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				monolink.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert monolink" );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
	}
}

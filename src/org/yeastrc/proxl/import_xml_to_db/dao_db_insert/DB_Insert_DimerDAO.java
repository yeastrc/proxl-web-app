package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.DimerDTO;

public class DB_Insert_DimerDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_DimerDAO.class);

	private DB_Insert_DimerDAO() { }
	public static DB_Insert_DimerDAO getInstance() { return new DB_Insert_DimerDAO(); }
	

	/**
	 * @param dimer
	 * @throws Exception
	 */
	public void save( DimerDTO dimer ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO dimer (psm_id, nrseq_id_1, nrseq_id_2, peptide_1_id, peptide_2_id) VALUES (?, ?, ?, ?, ?)";

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, dimer.getPsm().getId() );
			pstmt.setInt( 2, dimer.getProtein1().getNrseqId() );
			pstmt.setInt( 3, dimer.getProtein2().getNrseqId() );
			pstmt.setInt( 4, dimer.getPeptide1Id() );
			pstmt.setInt( 5, dimer.getPeptide2Id() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				dimer.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert dimer" );
			
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

package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnlinkedDTO;

public class DB_Insert_UnlinkedDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_UnlinkedDAO.class);

	private DB_Insert_UnlinkedDAO() { }
	public static DB_Insert_UnlinkedDAO getInstance() { return new DB_Insert_UnlinkedDAO(); }
	

	/**
	 * @param unlinked
	 * @throws Exception
	 */
	public void save( UnlinkedDTO unlinked ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO unlinked (psm_id, nrseq_id, peptide_id) VALUES (?, ?, ?)";

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1, unlinked.getPsm().getId() );
			pstmt.setInt( 2, unlinked.getProtein().getNrseqId() );
			pstmt.setInt( 3, unlinked.getPeptideId() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				unlinked.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert unlinked" );
			
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

package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LooplinkDTO;

public class DB_Insert_LooplinkDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_LooplinkDAO.class);

	private DB_Insert_LooplinkDAO() { }
	public static DB_Insert_LooplinkDAO getInstance() { return new DB_Insert_LooplinkDAO(); }
	

	private static final String INSERT_SQL = 
			"INSERT INTO looplink (psm_id, nrseq_id, protein_position_1, protein_position_2, peptide_id, "
			+ "peptide_position_1, peptide_position_2, linker_mass) " 
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			
			
	public void save( LooplinkDTO looplink ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = INSERT_SQL;

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1, looplink.getPsm().getId() );
			pstmt.setInt( 2, looplink.getProtein().getNrseqId() );
			pstmt.setInt( 3, looplink.getProteinPosition1() );
			pstmt.setInt( 4, looplink.getProteinPosition2() );
			pstmt.setInt( 5, looplink.getPeptideId() );
			pstmt.setInt( 6, looplink.getPeptidePosition1() );
			pstmt.setInt( 7, looplink.getPeptidePosition2() );
			pstmt.setBigDecimal( 8, looplink.getLinkerMass() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				looplink.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert looplink" );
			
			
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

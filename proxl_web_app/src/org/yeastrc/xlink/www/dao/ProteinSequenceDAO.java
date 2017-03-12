package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;

/**
 * 
 * table protein_sequence
 */
public class ProteinSequenceDAO {

	private static final Logger log = Logger.getLogger(ProteinSequenceDAO.class);
	private ProteinSequenceDAO() { }
	public static ProteinSequenceDAO getInstance() { return new ProteinSequenceDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceDTO getProteinSequenceDTOFromDatabase( int id ) throws Exception {
		ProteinSequenceDTO protein_sequence = new ProteinSequenceDTO();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT sequence FROM protein_sequence WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find protein_sequence with id " + id );
			protein_sequence.setId( id );
			protein_sequence.setSequence( rs.getString( 1 ) );
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return protein_sequence;
	}
}

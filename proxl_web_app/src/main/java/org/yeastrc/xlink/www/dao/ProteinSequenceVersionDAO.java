package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ProteinSequenceVersionDTO;

/**
 * table protein_sequence_version
 *
 */
public class ProteinSequenceVersionDAO {

	private static final Logger log = LoggerFactory.getLogger( ProteinSequenceVersionDAO.class);
	private ProteinSequenceVersionDAO() { }
	public static ProteinSequenceVersionDAO getInstance() { return new ProteinSequenceVersionDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceVersionDTO getFromId( int id ) throws Exception {
		ProteinSequenceVersionDTO protein_sequence = new ProteinSequenceVersionDTO();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT protein_sequence_id, isotope_label_id FROM protein_sequence_version WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find protein_sequence_version with id " + id );
			protein_sequence.setId( id );
			protein_sequence.setproteinSequenceId( rs.getInt( "protein_sequence_id" ) );
			protein_sequence.setIsotopeLabelId( rs.getInt( "isotope_label_id" ) );
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

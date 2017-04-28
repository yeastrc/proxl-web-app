package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;

/**
 * table srch_rep_pept__peptide
 *
 */
public class SrchRepPeptPeptideDAO {

	private static final Logger log = Logger.getLogger( SrchRepPeptPeptideDAO.class );
	private SrchRepPeptPeptideDAO() { }
	public static SrchRepPeptPeptideDAO getInstance() { return new SrchRepPeptPeptideDAO(); }
	
	private static final String SEARCH_SQL =
			"SELECT  * "
			+ " FROM srch_rep_pept__peptide"
			+ " WHERE id = ? ";
	/**
	 * get records from srch_rep_pept__peptide for id
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public SrchRepPeptPeptideDTO getForId( int id ) throws Exception {
		SrchRepPeptPeptideDTO result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SEARCH_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, id );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				result = new SrchRepPeptPeptideDTO();
				result.setId( rs.getInt( "id" ) );
				result.setSearchId( rs.getInt( "search_id" ) );
				result.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				result.setPeptideId( rs.getInt( "peptide_id" ) );
				result.setPeptidePosition_1( rs.getInt( "peptide_position_1" ) );
				result.setPeptidePosition_2( rs.getInt( "peptide_position_2" ) );
			}
		} catch ( Exception e ) {
			String msg = "getForSearchIdReportedPeptideId(), sql: " + sql;
			log.error( msg, e );
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
		return result;
	}

}

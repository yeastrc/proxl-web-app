package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideIsotopeLabelDTO;

/**
 * table srch_rep_pept__peptide_isotope_label
 *
 */
public class SrchRepPeptPeptideIsotopeLabelDAO {

	private static final Logger log = LoggerFactory.getLogger(  SrchRepPeptPeptideIsotopeLabelDAO.class );
	private SrchRepPeptPeptideIsotopeLabelDAO() { }
	public static SrchRepPeptPeptideIsotopeLabelDAO getInstance() { return new SrchRepPeptPeptideIsotopeLabelDAO(); }
	
	private static final String SEARCH_SQL =
			"SELECT  * "
			+ " FROM srch_rep_pept__peptide_isotope_label"
			+ " WHERE srch_rep_pept__peptide_id = ? ";
	/**
	 * get records from srch_rep_pept__peptide_isotope_label for srchRepPeptPeptideId
	 * @param srchRepPeptPeptideId
	 * @return List of SrchRepPeptPeptideIsotopeLabelDTO
	 * @throws Exception
	 */
	public List<SrchRepPeptPeptideIsotopeLabelDTO> getListFor_srchRepPeptPeptideId( int srchRepPeptPeptideId ) throws Exception {
		
		List<SrchRepPeptPeptideIsotopeLabelDTO> results = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SEARCH_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, srchRepPeptPeptideId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				SrchRepPeptPeptideIsotopeLabelDTO result = new SrchRepPeptPeptideIsotopeLabelDTO();
				result = new SrchRepPeptPeptideIsotopeLabelDTO();
				result.setSrchRepPeptPeptideId( rs.getInt( "srch_rep_pept__peptide_id" ) );
				result.setIsotopeLabelId( rs.getInt( "isotope_label_id" ) );
				results.add( result );
			}
		} catch ( Exception e ) {
			String msg = "getListFor_srchRepPeptPeptideId(), sql: " + sql;
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
		return results;
	}

}

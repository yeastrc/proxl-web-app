package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
/**
 * table srch_rep_pept__pept__dynamic_mod
 *
 */
public class SrchRepPeptPeptDynamicModSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( SrchRepPeptPeptDynamicModSearcher.class);
	private SrchRepPeptPeptDynamicModSearcher() { }
	public static SrchRepPeptPeptDynamicModSearcher getInstance() { return new SrchRepPeptPeptDynamicModSearcher(); }
	
	private final String getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId_SQL =
			"SELECT * FROM srch_rep_pept__pept__dynamic_mod WHERE search_reported_peptide_peptide_id = ?";
	/**
	 * Get srch_rep_pept__pept__dynamic_mod record by search_reported_peptide_peptide_id
	 * @param srchRepPeptPeptideId
	 * @return
	 * @throws Exception 
	 */
	public List<SrchRepPeptPeptDynamicModDTO> getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( int srchRepPeptPeptideId ) throws Exception {
		List<SrchRepPeptPeptDynamicModDTO> results = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, srchRepPeptPeptideId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				SrchRepPeptPeptDynamicModDTO result = new SrchRepPeptPeptDynamicModDTO();
				result.setId( rs.getInt( "id" ) );
				result.setSearchReportedPeptidepeptideId( rs.getInt( "search_reported_peptide_peptide_id" ) );
				result.setPosition( rs.getInt( "position" ) );
				result.setMass( rs.getDouble( "mass" ) );
				{
					int isMonolinkInt = rs.getInt( "is_monolink" );
					if ( isMonolinkInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {
						result.setMonolink( true );
					} else {
						result.setMonolink( false );
					}
				}
				{
					int Is_N_TerminalInt = rs.getInt( "is_n_terminal" );
					if ( Is_N_TerminalInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {
						result.setIs_N_Terminal(true);
					} else {
						result.setIs_N_Terminal( false );
					}
				}
				{
					int Is_C_TerminalInt = rs.getInt( "is_c_terminal" );
					if ( Is_C_TerminalInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {
						result.setIs_C_Terminal( true );
					} else {
						result.setIs_C_Terminal( false );
					}
				}
				results.add(result);
			}
		} catch ( Exception e ) {
			String msg = "Exception in getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( ... ): sql: " + sql;
			log.error( msg );
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

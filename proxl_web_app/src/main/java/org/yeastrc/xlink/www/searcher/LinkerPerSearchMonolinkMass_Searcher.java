package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;

/**
 * 
 *
 */
public class LinkerPerSearchMonolinkMass_Searcher {

	private static final Logger log = LoggerFactory.getLogger( LinkerPerSearchMonolinkMass_Searcher.class);
	private LinkerPerSearchMonolinkMass_Searcher() { }
	private static final LinkerPerSearchMonolinkMass_Searcher _INSTANCE = new LinkerPerSearchMonolinkMass_Searcher();
	public static LinkerPerSearchMonolinkMass_Searcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get records in linker_per_search_monolink_mass_tbl for search id
	 * 
	 * @param searchId
	 * @return List<LinkerPerSearchMonolinkMassDTO>
	 * @throws Exception
	 */
	public List<LinkerPerSearchMonolinkMassDTO> getForSearchId( int searchId  ) throws Exception {
		
		List<LinkerPerSearchMonolinkMassDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM linker_per_search_monolink_mass_tbl WHERE search_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				LinkerPerSearchMonolinkMassDTO result = new LinkerPerSearchMonolinkMassDTO();
				result.setId( rs.getInt( "id" ) );
				result.setSearchLinkerId( rs.getInt( "search_linker_id" ) );
				result.setSearchId( rs.getInt( "search_id" ) );
				result.setMonolinkMassDouble( rs.getDouble( "monolink_mass_double" ) );
				result.setMonolinkMassString( rs.getString( "monolink_mass_string" ) );
				results.add( result );
			}
		} catch ( Exception e ) {
			String msg = "Exception in getForSearchId( ... ): sql: " + sql;
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

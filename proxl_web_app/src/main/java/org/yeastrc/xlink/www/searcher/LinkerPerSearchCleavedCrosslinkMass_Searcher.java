package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;

public class LinkerPerSearchCleavedCrosslinkMass_Searcher {

	private static final Logger log = Logger.getLogger(LinkerPerSearchCleavedCrosslinkMass_Searcher.class);
	private LinkerPerSearchCleavedCrosslinkMass_Searcher() { }
	private static final LinkerPerSearchCleavedCrosslinkMass_Searcher _INSTANCE = new LinkerPerSearchCleavedCrosslinkMass_Searcher();
	public static LinkerPerSearchCleavedCrosslinkMass_Searcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get records in linker_per_search_cleaved_crosslink_mass for search id
	 * 
	 * @param searchId
	 * @return List<LinkerPerSearchCleavedCrosslinkMassDTO>
	 * @throws Exception
	 */
	public List<LinkerPerSearchCleavedCrosslinkMassDTO> getForSearchId( int searchId  ) throws Exception {
		
		List<LinkerPerSearchCleavedCrosslinkMassDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM linker_per_search_cleaved_crosslink_mass WHERE search_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				LinkerPerSearchCleavedCrosslinkMassDTO result = new LinkerPerSearchCleavedCrosslinkMassDTO();
				result.setId( rs.getInt( "id" ) );
				result.setLinkerId( rs.getInt( "linker_id" ) );
				result.setSearchId( rs.getInt( "search_id" ) );
				result.setCleavedCrosslinkMassDouble( rs.getDouble( "cleaved_crosslink_mass_double" ) );
				result.setCleavedCrosslinkMassString( rs.getString( "cleaved_crosslink_mass_string" ) );
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

package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class SearchModMassDistinctSearcher {

	private static final Log log = LogFactory.getLog(SearchModMassDistinctSearcher.class);
	
	private SearchModMassDistinctSearcher() { }
	private static final SearchModMassDistinctSearcher _INSTANCE = new SearchModMassDistinctSearcher();
	public static SearchModMassDistinctSearcher getInstance() { return _INSTANCE; }

	private static final String sqlStartTwoOrMoreSearchIds = "SELECT DISTINCT "; 
	
	private static final String sqlMain = 
			
			" dynamic_mod_mass FROM search__dynamic_mod_mass_lookup "

			+ " WHERE search_id IN (? ";

	private static final String sqlEnd =  ") ORDER BY dynamic_mod_mass ";
	
	private static final String sqlSingleSearchId = "SELECT " + sqlMain + sqlEnd;

	private static final String sqlTwoSearchIds = sqlStartTwoOrMoreSearchIds + sqlMain + ",?" + sqlEnd;
	
	private static final String sqlStartMoreThanTwoSearchIds = sqlStartTwoOrMoreSearchIds + sqlMain;
			
	/**
	 * Get distinct DynamicMod Masses for the searchIds 
	 * 
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<Double> getDistinctDynamicModMassesForSearchId( int[] searchIds ) throws Exception {
		
		List<Double> results = new ArrayList<Double>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = sqlSingleSearchId;
		
		if ( searchIds.length == 2 ) {
			
			sql = sqlTwoSearchIds;
		
		} else if ( searchIds.length > 2 ) {
			
			StringBuilder sqlSB = new StringBuilder( sqlStartMoreThanTwoSearchIds );

			for ( int index = 1; index < searchIds.length; index++ ) {
				
				sqlSB.append( ",?");
			}
			
			sqlSB.append( sqlEnd );
			
			sql = sqlSB.toString();
		}
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			for ( Integer searchId : searchIds ) {

				counter++;
				pstmt.setInt( counter, searchId );
			}
			
			rs = pstmt.executeQuery();

			while( rs.next() )
				results.add( rs.getDouble( "dynamic_mod_mass" ) );
			
		} catch ( Exception e ) {
			
			String msg = "getDistinctDynamicModMassesForSearchId(), sql: " + sql;
			
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

package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;

public class Search_Linker_CrosslinkMass_Searcher {
	
	private Search_Linker_CrosslinkMass_Searcher() { }
	private static final Search_Linker_CrosslinkMass_Searcher _INSTANCE = new Search_Linker_CrosslinkMass_Searcher();
	public static Search_Linker_CrosslinkMass_Searcher getInstance() { return _INSTANCE; }
	
	private static final String sql = 
			"SELECT id, search_linker_id, crosslink_mass_double, crosslink_mass_string"
			+ " FROM linker_per_search_crosslink_mass_tbl WHERE search_id = ?";
	

	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<LinkerPerSearchCrosslinkMassDTO> getSearch_Linker_CrosslinkMass( int searchId ) throws Exception {
		
		List<LinkerPerSearchCrosslinkMassDTO> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				LinkerPerSearchCrosslinkMassDTO item = new LinkerPerSearchCrosslinkMassDTO();
				item.setId( rs.getInt( "id" ) );
				item.setSearchLinkerId( rs.getInt( "search_linker_id" ) );
				item.setCrosslinkMassDouble( rs.getDouble( "crosslink_mass_double" ) );
				item.setCrosslinkMassString( rs.getString( "crosslink_mass_string" ) );
				resultList.add( item );
			}
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
		
		return resultList;
	}
}

package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLinkerDTO;

public class SearchLinkerSearcher {
	
	private SearchLinkerSearcher() { }
	private static final SearchLinkerSearcher _INSTANCE = new SearchLinkerSearcher();
	public static SearchLinkerSearcher getInstance() { return _INSTANCE; }

	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<SearchLinkerDTO> getSearchLinkerDTOForSearch( int searchId ) throws Exception {
		
		List<SearchLinkerDTO> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = "SELECT id, linker_abbr, spacer_arm_length, spacer_arm_length_string FROM search_linker_tbl WHERE search_id = ?";
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				SearchLinkerDTO item = new SearchLinkerDTO();
				item.setId( rs.getInt( "id" ) );
				item.setSearchId( searchId );
				item.setLinkerAbbr( rs.getString( "linker_abbr" ) );
				double spacerArmLength = rs.getDouble( "spacer_arm_length" );
				if ( ! rs.wasNull() ) {
					item.setSpacerArmLength( spacerArmLength );	
				}
				item.setSpacerArmLengthString( rs.getString( "spacer_arm_length_string" ) );
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
	
	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<String> getLinkerAbbreviationsForSearch( int searchId ) throws Exception {
		
		List<String> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = "SELECT linker_abbr FROM search_linker_tbl WHERE search_id = ?";
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				resultList.add(  rs.getString( "linker_abbr" ) );
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

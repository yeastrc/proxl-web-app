package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.LinkerDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerDTO;

public class LinkersForSearchIdsSearcher {
	
	private static final Logger log = Logger.getLogger(LinkersForSearchIdsSearcher.class);

	private LinkersForSearchIdsSearcher() { }
	public static LinkersForSearchIdsSearcher getInstance() { return new LinkersForSearchIdsSearcher(); }

	/**
	 * Get a list of linkers for the collection of search ids
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public List<LinkerDTO> getLinkersForSearchIds( Collection<Integer> searchIds ) throws Exception {

		List<LinkerDTO>  resultList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sqlMain = " SELECT DISTINCT linker_id AS linker_id "
				 + " FROM search_linker "
				 + " WHERE search_id IN (";
		
		final String sqlEnd = ")";
		
		String sqlSearchIdsString = null;
		
		for ( Integer searchId : searchIds ) {
			
			if ( sqlSearchIdsString == null ) {
				
				sqlSearchIdsString = searchId.toString();
			} else {
				
				sqlSearchIdsString += "," + searchId.toString();
			}
		}
		
		final String sql = sqlMain + sqlSearchIdsString + sqlEnd;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();
			
			LinkerDAO linkerDAO = LinkerDAO.getInstance();
			
			while ( rs.next() ) {
				
				int linkerId = rs.getInt( "linker_id" );
				
				LinkerDTO linkerDTO = linkerDAO.getLinkerDTOForId( linkerId );
				
				if ( linkerDTO == null ) {
					
					String msg = "linker id '" + linkerId + "' retrieved from 'table search_linker' is not found in table 'linker'";
					
					log.error(msg);
					
					throw new Exception(msg);
				}
				
				resultList.add( linkerDTO );
				
			}
			

		} catch ( Exception e ) {
			
			log.error( "ERROR: sql: " + sql, e );
			
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
		
		
		return resultList;
	}
	
	
}

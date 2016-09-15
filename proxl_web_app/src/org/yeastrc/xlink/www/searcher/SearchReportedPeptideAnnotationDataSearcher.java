package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchReportedPeptideAnnotationDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;



public class SearchReportedPeptideAnnotationDataSearcher {

	private static final Logger log = Logger.getLogger(SearchReportedPeptideAnnotationDataSearcher.class);
	
	private SearchReportedPeptideAnnotationDataSearcher() { }
	private static final SearchReportedPeptideAnnotationDataSearcher _INSTANCE = new SearchReportedPeptideAnnotationDataSearcher();
	public static SearchReportedPeptideAnnotationDataSearcher getInstance() { return _INSTANCE; }
	
	
	

	private static final String SQL_MAIN = 
			"SELECT * "
					
			+ " FROM srch__rep_pept__annotation  ";

	private static final String SQL_WHERE_START =  " WHERE search_id = ? AND reported_peptide_id = ? AND annotation_type_id IN  ";
			
	
	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @param srchPgmFilterableReportedPeptideAnnotationTypeDTOList
	 * @return
	 * @throws Exception
	 */
	public List<SearchReportedPeptideAnnotationDTO> getSearchReportedPeptideAnnotationDTOList( int searchId, int reportedPeptideId, Collection<Integer> annotationTypeIds  ) throws Exception {

		List<SearchReportedPeptideAnnotationDTO> results = new ArrayList<SearchReportedPeptideAnnotationDTO>();
		
		
		if ( annotationTypeIds == null || annotationTypeIds.isEmpty() ) {
			
			return results;
		}
		
		

		StringBuilder sqlSB = new StringBuilder( 1000 );
		

		//////////////////////
		
		/////   Start building the SQL
		
		
		

		sqlSB.append( SQL_MAIN );
		

		///////////
		
		sqlSB.append( SQL_WHERE_START );
		
		//////////
		

		// Add type ids to  WHERE
		
		boolean first = true;

		sqlSB.append( " ( " );
		
		for ( Integer annotationTypeId : annotationTypeIds ) {

			if ( first ) {
				
				first = false;
			} else {
				
				sqlSB.append( ", " );
			}
				
			sqlSB.append( annotationTypeId );
		}

		sqlSB.append( " ) " );


		String sql = sqlSB.toString();
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );

			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				SearchReportedPeptideAnnotationDTO item = SearchReportedPeptideAnnotationDAO.getInstance().populateFromResultSet( rs );
				
				results.add( item );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getSearchReportedPeptideAnnotationDTOList( ... ): sql: " + sql;
			
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

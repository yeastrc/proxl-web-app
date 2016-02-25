package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**

 *
 */
public class ScoreCountFromPsmTblSearcher {

	private static final Log log = LogFactory.getLog(ScoreCountFromPsmTblSearcher.class);
	
	private ScoreCountFromPsmTblSearcher() { }
	private static final ScoreCountFromPsmTblSearcher _INSTANCE = new ScoreCountFromPsmTblSearcher();
	public static ScoreCountFromPsmTblSearcher getInstance() { return _INSTANCE; }
	

	
	/**
	 * @param selectedDBLinkTypes - NULL for all
	 * @param searchId
	 * @param annotationTypeId
	 * @return
	 * @throws Exception
	 */
	public int getScoreCount( 
			List<String> selectedDBLinkTypes, 
			int searchId,
			int annotationTypeId
			) throws Exception {
		
		
		int getScoreCount = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		StringBuilder sqlSB = new StringBuilder( 10000 );

		sqlSB.append( "SELECT COUNT(*) AS count FROM psm_filterable_annotation__generic_lookup WHERE search_id = ? AND annotation_type_id = ? " );

		if ( selectedDBLinkTypes != null && ! selectedDBLinkTypes.isEmpty() ) {

			sqlSB.append( " AND psm_type IN ( " );

			boolean firstWebLinkType = true;

			for ( String selectedDBLinkType : selectedDBLinkTypes ) {
				
				if ( firstWebLinkType ) {
					
					firstWebLinkType = false;
				} else {
					
					sqlSB.append( " , " );
				}
				sqlSB.append( " '" );
				sqlSB.append( selectedDBLinkType );
				sqlSB.append( "' " );
			}
			
			sqlSB.append( " ) " );

		}
		
		String sql = sqlSB.toString();
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, annotationTypeId );

			rs = pstmt.executeQuery();

			if( rs.next() ) {
				getScoreCount = rs.getInt( "count" );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getScoreCount(...), sql: " + sql;
			
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
		
		return getScoreCount;
	}
	
	
	/**
	 * @param selectedDBLinkTypes - NULL for all
	 * @param searchId
	 * @param annotationTypeId
	 * @param psmScoreCutoff - Not NULL if set
	 * @return
	 * @throws Exception
	 */
	public List<Double> getScoreValues( 
			List<String> selectedDBLinkTypes, 
			int searchId,
			int annotationTypeId,
			Double psmScoreCutoff
			) throws Exception {
		
		
		List<Double> scoreValueList = new ArrayList<Double>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		StringBuilder sqlSB = new StringBuilder( 10000 );

		sqlSB.append( "SELECT value_double FROM psm_filterable_annotation__generic_lookup WHERE search_id = ?  AND annotation_type_id = ? " );

		if ( selectedDBLinkTypes != null && ! selectedDBLinkTypes.isEmpty() ) {

			sqlSB.append( " AND psm_filterable_annotation__generic_lookup.psm_type IN ( " );

			boolean firstWebLinkType = true;

			for ( String selectedDBLinkType : selectedDBLinkTypes ) {
				
				if ( firstWebLinkType ) {
					
					firstWebLinkType = false;
				} else {
					
					sqlSB.append( " , " );
				}
				sqlSB.append( " '" );
				sqlSB.append( selectedDBLinkType );
				sqlSB.append( "' " );
			}
			
			sqlSB.append( " ) " );

		}
		
		if ( psmScoreCutoff != null ) {
			
			sqlSB.append( " AND value_double <= ? " );
		}
		
		String sql = sqlSB.toString();
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, annotationTypeId );

			if ( psmScoreCutoff != null ) {
			
				paramCounter++;
				pstmt.setDouble( paramCounter, psmScoreCutoff );
			}
				
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				scoreValueList.add( rs.getDouble( "value_double" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getScoreValues(), sql: " + sql;
			
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
		
		
		
		return scoreValueList;
	}
	
	
	
}

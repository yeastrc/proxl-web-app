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
public class QValuesFromPsmTblSearcher {

	private static final Log log = LogFactory.getLog(QValuesFromPsmTblSearcher.class);
	
	private QValuesFromPsmTblSearcher() { }
	private static final QValuesFromPsmTblSearcher _INSTANCE = new QValuesFromPsmTblSearcher();
	public static QValuesFromPsmTblSearcher getInstance() { return _INSTANCE; }
	

	
	/**
	 * @param selectedDBLinkTypes - NULL for all
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public int getQValuesCount( 
			List<String> selectedDBLinkTypes, 
			int searchId
			) throws Exception {
		
		
		int getQValuesCount = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		StringBuilder sqlSB = new StringBuilder( 10000 );

		sqlSB.append( "SELECT COUNT(*) AS count FROM psm WHERE search_id = ? " );

		if ( selectedDBLinkTypes != null && ! selectedDBLinkTypes.isEmpty() ) {

			sqlSB.append( " AND psm.type IN ( " );

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

			rs = pstmt.executeQuery();

			if( rs.next() ) {
				getQValuesCount = rs.getInt( "count" );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getQValuesCount(), sql: " + sql;
			
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
		
		return getQValuesCount;
	}
	
	
	/**
	 * @param selectedDBLinkTypes - NULL for all
	 * @param searchId
	 * @param psmQValueCutoff - Not NULL if set
	 * @return
	 * @throws Exception
	 */
	public List<Double> getQValues( 
			List<String> selectedDBLinkTypes, 
			int searchId,
			Double psmQValueCutoff
			) throws Exception {
		
		
		List<Double> qValueList = new ArrayList<Double>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		StringBuilder sqlSB = new StringBuilder( 10000 );

		sqlSB.append( "SELECT q_value FROM psm WHERE search_id = ? " );

		if ( selectedDBLinkTypes != null && ! selectedDBLinkTypes.isEmpty() ) {

			sqlSB.append( " AND psm.type IN ( " );

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
		
		if ( psmQValueCutoff != null ) {
			
			sqlSB.append( " AND q_value <= ? " );
		}
		
		String sql = sqlSB.toString();
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			if ( psmQValueCutoff != null ) {
			
				paramCounter++;
				pstmt.setDouble( paramCounter, psmQValueCutoff );
			}
				
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				qValueList.add( rs.getDouble( "q_value" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getQValues(), sql: " + sql;
			
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
		
		
		
		return qValueList;
	}
	
	
	
}

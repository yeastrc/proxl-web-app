package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PsmAnnotationDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
/**
 * table psm_annotation
 *
 */
public class PsmAnnotationDataSearcher {
	
	private static final Logger log = Logger.getLogger(PsmAnnotationDataSearcher.class);
	private PsmAnnotationDataSearcher() { }
	private static final PsmAnnotationDataSearcher _INSTANCE = new PsmAnnotationDataSearcher();
	public static PsmAnnotationDataSearcher getInstance() { return _INSTANCE; }
	
	private static final String SQL_MAIN = "SELECT psm_annotation.* FROM psm_annotation  "
			+ " WHERE psm_annotation.psm_id = ? AND psm_annotation.annotation_type_id IN  ";
	/**
	 * @param psmId
	 * @param annotationTypeDTOList
	 * @return
	 * @throws Exception
	 */
	public List<PsmAnnotationDTO> getPsmAnnotationDTOList( int psmId, Collection<Integer> annotationTypeIdList  ) throws Exception {
		List<PsmAnnotationDTO> results = new ArrayList<PsmAnnotationDTO>();
		if ( annotationTypeIdList == null || annotationTypeIdList.isEmpty() ) {
			return results;
		}
		StringBuilder sqlSB = new StringBuilder( 1000 );
		//////////////////////
		/////   Start building the SQL
		sqlSB.append( SQL_MAIN );
		//////////
		// Add type ids to  WHERE
		boolean first = true;
		sqlSB.append( " ( " );
		for ( Integer annotationTypeId : annotationTypeIdList ) {
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
			pstmt.setInt( paramCounter, psmId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				PsmAnnotationDTO item = PsmAnnotationDAO.getInstance().populateFromResultSet( rs );
				results.add( item );
			}
		} catch ( Exception e ) {
			String msg = "Exception in getPsmAnnotationDTOList( ... ): sql: " + sql;
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

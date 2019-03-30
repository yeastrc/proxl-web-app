package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dao.PsmPerPeptideAnnotationDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmPerPeptideAnnotationDTO;

/**
 * table psm_per_peptide_annotation
 *
 */
public class PsmPerPeptideAnnotationDataSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( PsmPerPeptideAnnotationDataSearcher.class);
	private PsmPerPeptideAnnotationDataSearcher() { }
	private static final PsmPerPeptideAnnotationDataSearcher _INSTANCE = new PsmPerPeptideAnnotationDataSearcher();
	public static PsmPerPeptideAnnotationDataSearcher getInstance() { return _INSTANCE; }
	
	private static final String SQL_MAIN = "SELECT psm_per_peptide_annotation.* FROM psm_per_peptide_annotation  "
			+ " WHERE psm_per_peptide_annotation.psm_id = ? AND psm_per_peptide_annotation.srch_rep_pept__peptide_id = ? "
			+ " AND psm_per_peptide_annotation.annotation_type_id IN  ";
	/**
	 * @param psmId
	 * @param annotationTypeDTOList
	 * @return
	 * @throws Exception
	 */
	public List<PsmPerPeptideAnnotationDTO> getPsmPerPeptideAnnotationDTOList( int psmId, int srchRepPeptPeptideId, Collection<Integer> annotationTypeIdList  ) throws Exception {
		List<PsmPerPeptideAnnotationDTO> results = new ArrayList<PsmPerPeptideAnnotationDTO>();
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
			paramCounter++;
			pstmt.setInt( paramCounter, srchRepPeptPeptideId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				PsmPerPeptideAnnotationDTO item = PsmPerPeptideAnnotationDAO.getInstance().populateFromResultSet( rs );
				results.add( item );
			}
		} catch ( Exception e ) {
			String msg = "Exception in getPsmPerPeptideAnnotationDTOList( ... ): sql: " + sql;
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

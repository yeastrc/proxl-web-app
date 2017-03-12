package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.CutoffsAppliedOnImportDTO;

/**
 * Return a list of cutoffs_applied_on_import for a search id
 *
 *
 */
public class CutoffsAppliedOnImportSearcher {

	private static final Logger log = Logger.getLogger(CutoffsAppliedOnImportSearcher.class);
	private CutoffsAppliedOnImportSearcher() { }
	private static final CutoffsAppliedOnImportSearcher _INSTANCE = new CutoffsAppliedOnImportSearcher();
	public static CutoffsAppliedOnImportSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Only populate cutoff_value_string and annotation_type_id
	 * 
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<CutoffsAppliedOnImportDTO> getCutoffsAppliedOnImportDTOForSearchId( int searchId ) throws Exception {
		List<CutoffsAppliedOnImportDTO> results = new ArrayList<CutoffsAppliedOnImportDTO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT annotation_type_id, cutoff_value_double, cutoff_value_string FROM cutoffs_applied_on_import WHERE search_id = ? ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO = new CutoffsAppliedOnImportDTO();
				cutoffsAppliedOnImportDTO.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
				cutoffsAppliedOnImportDTO.setCutoffValueDouble( rs.getDouble( "cutoff_value_double" ) );
				cutoffsAppliedOnImportDTO.setCutoffValueString( rs.getString( "cutoff_value_string" ) );
				results.add(cutoffsAppliedOnImportDTO);
			}
		} catch ( Exception e ) {
			String msg = "getCutoffsAppliedOnImportDTOForSearchId(), sql: " + sql;
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

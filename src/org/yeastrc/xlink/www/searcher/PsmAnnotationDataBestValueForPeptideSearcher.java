package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;



/**
 * 
 * FROM unified_rp__rep_pept__search__best_psm_value_generic_lookup
 */
public class PsmAnnotationDataBestValueForPeptideSearcher {

	private static final Logger log = Logger.getLogger(PsmAnnotationDataBestValueForPeptideSearcher.class);
	
	private PsmAnnotationDataBestValueForPeptideSearcher() { }
	private static final PsmAnnotationDataBestValueForPeptideSearcher _INSTANCE = new PsmAnnotationDataBestValueForPeptideSearcher();
	public static PsmAnnotationDataBestValueForPeptideSearcher getInstance() { return _INSTANCE; }
	
	
	

	private static final String SQL_MAIN = 
			"SELECT * "
					
			+ " FROM unified_rp__rep_pept__search__best_psm_value_generic_lookup  "

			+ " WHERE search_id = ? AND reported_peptide_id = ? AND annotation_type_id IN  ";
			
	
	
	/**
	 * FROM unified_rp__rep_pept__search__best_psm_value_generic_lookup
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @param annotationTypeDTOList
	 * @return
	 * @throws Exception
	 */
	public List<PsmAnnotationDTO> getPsmAnnotationDataBestValueForPeptideList( int searchId, int reportedPeptideId, List<AnnotationTypeDTO> annotationTypeDTOList  ) throws Exception {
		
		if ( annotationTypeDTOList == null || annotationTypeDTOList.isEmpty() ) {
			
			throw new IllegalArgumentException( "annotationTypeDTOList cannot be null or empty" );
		}
		
		
		List<PsmAnnotationDTO> results = new ArrayList<PsmAnnotationDTO>();
		
		

		StringBuilder sqlSB = new StringBuilder( 1000 );
		

		//////////////////////
		
		/////   Start building the SQL
		
		
		

		sqlSB.append( SQL_MAIN );
		
		
		//////////
		

		// Add type ids to  WHERE
		
		boolean first = true;

		sqlSB.append( " ( " );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : annotationTypeDTOList ) {

			if ( first ) {
				
				first = false;
			} else {
				
				sqlSB.append( ", " );
			}
				
			sqlSB.append( srchPgmFilterablePsmAnnotationTypeDTO.getId() );
		}

		sqlSB.append( " ) " );


		String sql = sqlSB.toString();
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );

			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				

				PsmAnnotationDTO item;
				item = new PsmAnnotationDTO();
				
				item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
				item.setValueDouble( rs.getDouble( "best_psm_value_for_ann_type_id" ) );
				item.setValueString( rs.getString( "best_psm_value_string_for_ann_type_id" ) );
				
				
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

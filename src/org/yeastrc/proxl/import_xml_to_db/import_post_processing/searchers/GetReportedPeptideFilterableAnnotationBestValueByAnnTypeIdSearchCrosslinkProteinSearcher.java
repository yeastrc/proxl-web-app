package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCrosslinkGenericLookupDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;


//CREATE TABLE IF NOT EXISTS `srch__rep_pept__annotation` (
//		  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//		  `search_id` INT(10) UNSIGNED NOT NULL,
//		  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
//		  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
//		  `value` DOUBLE NOT NULL,
//		  `value_string` VARCHAR(200) NOT NULL,
		
/**
 * 
 * Get "best" value from srch__rep_pept__annotation for annotation_type_id, search_id and searchCrosslinkGenericLookupDTO
 */
public class GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher {
	
	private static final Logger log = Logger.getLogger(GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher.class);
	
	private GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher() { }
	private static final GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher _INSTANCE = new GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher();
	public static GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher getInstance() { return _INSTANCE; }
	

	private static final String MAX_FCN = "MAX";
	private static final String MIN_FCN = "MIN";
	
	private static final String MAIN_SQL = 
			"(value_double) AS value_double "
					+ " FROM crosslink__rep_pept__search__generic_lookup AS crpsgl "
					+ " INNER JOIN srch__rep_pept__annotation AS srpa "
					+ 	" ON crpsgl.search_id =  srpa.search_id "
					+ 		" AND crpsgl.reported_peptide_id =  srpa.reported_peptide_id "

			 + " WHERE  "
			 + " crpsgl.search_id = ? AND srpa.search_id = ? "
			 + " AND  srpa. annotation_type_id = ?  "
			 + " AND crpsgl.nrseq_id_1 = ? AND crpsgl.nrseq_id_2 = ? "
			 + " AND crpsgl.protein_1_position  = ? AND crpsgl.protein_2_position  = ? ";
	
	// WAS
//	private static final String MAIN_SQL = 
//			"(value_double) AS value_double FROM srch__rep_pept__annotation " 
//					+ " INNER JOIN psm "
//					+ 	" ON srch__rep_pept__annotation.search_id = psm.search_id "
//					+ 	"    AND srch__rep_pept__annotation.reported_peptide_id = psm.reported_peptide_id  "
//					
//					+ " INNER JOIN crosslink ON crosslink.psm_id = psm.id "
//					
//					+ " WHERE srch__rep_pept__annotation.annotation_type_id = ? "
//					+ " AND  psm.search_id = ? "
//					+ " AND  crosslink.nrseq_id_1 = ? AND crosslink.nrseq_id_2 = ?  "
//					+ " AND crosslink.protein_1_position  = ? AND crosslink.protein_2_position  = ? ";
	
	private static final String SQL_FOR_MAX = "SELECT " + MAX_FCN + MAIN_SQL;
	private static final String SQL_FOR_MIN = "SELECT " + MIN_FCN + MAIN_SQL;
	
	
	

	/**
	 * Get "best" value from srch__rep_pept__annotation for annotation_type_id, search_id and searchCrosslinkGenericLookupDTO
	 * 
	 * @param annotation_type_id
	 * @param searchCrosslinkGenericLookupDTO
	 * @param filterDirection - for peptide_filterable_annotation_type based on annotation_type_id
	 * @return null if no record found for selection criteria
	 * @throws Exception
	 */
	public BestFilterableAnnotationValue getBestAnnotationValue( int annotation_type_id, SearchCrosslinkGenericLookupDTO searchCrosslinkGenericLookupDTO, FilterDirectionType filterDirectionType ) throws Exception {
		
		BestFilterableAnnotationValue result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = null;


		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			
			sql = SQL_FOR_MAX;  //  Largest best so sort so largest is first
					
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			
			sql = SQL_FOR_MIN;  //  Smallest best so sort so smallest is first
			
		} else {
			
			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
		}

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, annotation_type_id );
			paramCounter++;
			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getNrseqId1() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getNrseqId2() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getProtein1Position() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getProtein2Position() );
			
			//  WAS
			
//			paramCounter++;
//			pstmt.setInt( paramCounter, annotation_type_id );
//			paramCounter++;
//			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getSearchId() );
//			paramCounter++;
//			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getNrseqId1() );
//			paramCounter++;
//			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getNrseqId2() );
//			paramCounter++;
//			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getProtein1Position() );
//			paramCounter++;
//			pstmt.setInt( paramCounter, searchCrosslinkGenericLookupDTO.getProtein2Position() );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {

				result = new BestFilterableAnnotationValue();
				
				
				result.setBestValue( rs.getDouble( "value_double" ) );
				result.setBestValueString( Double.toString( result.getBestValue() ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getBestAnnotationValue(), sql: " + sql;
			
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
		
		return result;
	}
	
	
}

package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLooplinkGenericLookupDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * 
 * Get "best" value from psm_annotation for annotation_type_id, search_id and reported_peptide_id
 */
public class GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher {
	
	private static final Logger log = Logger.getLogger(GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher.class);
	
	private GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher() { }
	private static final GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher _INSTANCE = new GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher();
	public static GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher getInstance() { return _INSTANCE; }
	
	

	/**
	 * Get "best" value from psm_annotation for annotation_type_id, searchLooplinkGenericLookupDTO
	 * 
	 * @param annotation_type_id
	 * @param searchLooplinkGenericLookupDTO
	 * @param filterDirection - for annotation_type based on annotation_type_id
	 * @return null if no record found for selection criteria
	 * @throws Exception
	 */
	public BestFilterableAnnotationValue getBestAnnotationValue( int annotation_type_id, SearchLooplinkGenericLookupDTO searchLooplinkGenericLookupDTO, FilterDirectionType filterDirectionType ) throws Exception {
		
		BestFilterableAnnotationValue result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
//		String orderDirection = null;
//		
//
//		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
//			
//			orderDirection = "DESC";  //  Largest best so sort so largest is first
//					
//		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
//			
//			orderDirection = "ASC";  //  Smallest best so sort so smallest is first
//			
//		} else {
//			
//			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
//		}


		String minMaxOfValue = null;
		

		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			
			minMaxOfValue = "MAX";  //  Largest best so sort so largest is first
					
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			
			minMaxOfValue = "MIN";  //  Smallest best so sort so smallest is first
			
		} else {
			
			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
		}

		
		final String sql = 
				"SELECT "
				+ minMaxOfValue 
				+ "(value_double) AS value_double FROM psm_filterable_annotation__generic_lookup " 
						+ " INNER JOIN looplink ON looplink.psm_id "
						+ 		" = psm_filterable_annotation__generic_lookup.psm_id "
						+ " WHERE annotation_type_id = ? "
						+ " AND  psm_filterable_annotation__generic_lookup.search_id = ? "
						+ " AND  looplink.nrseq_id = ?  "
						+ " AND looplink.protein_position_1  = ? AND looplink.protein_position_2  = ? ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, annotation_type_id );
			paramCounter++;
			pstmt.setInt( paramCounter, searchLooplinkGenericLookupDTO.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchLooplinkGenericLookupDTO.getNrseqId() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchLooplinkGenericLookupDTO.getProteinPosition1() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchLooplinkGenericLookupDTO.getProteinPosition2() );
			
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

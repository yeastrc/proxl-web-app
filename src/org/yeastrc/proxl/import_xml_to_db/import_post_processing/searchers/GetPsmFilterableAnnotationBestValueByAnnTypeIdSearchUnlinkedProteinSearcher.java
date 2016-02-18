package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchUnlinkedGenericLookupDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * 
 * Get "best" value from psm_annotation for annotation_type_id, search_id and reported_peptide_id
 */
public class GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher {
	
	private static final Logger log = Logger.getLogger(GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher.class);
	
	private GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher() { }
	private static final GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher _INSTANCE = new GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher();
	public static GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher getInstance() { return _INSTANCE; }
	
	

	/**
	 * Get "best" value from psm_annotation for annotation_type_id, searchUnlinkedGenericLookupDTO
	 * 
	 * @param annotation_type_id
	 * @param searchUnlinkedGenericLookupDTO
	 * @param filterDirection - for annotation_type based on annotation_type_id
	 * @return null if no record found for selection criteria
	 * @throws Exception
	 */
	public BestFilterableAnnotationValue getBestAnnotationValue( int annotation_type_id, SearchUnlinkedGenericLookupDTO searchUnlinkedGenericLookupDTO, FilterDirectionType filterDirectionType ) throws Exception {
		
		BestFilterableAnnotationValue result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String orderDirection = null;
		

		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			
			orderDirection = "DESC";  //  Largest best so sort so largest is first
					
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			
			orderDirection = "ASC";  //  Smallest best so sort so smallest is first
			
		} else {
			
			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
		}

		
		final String sql = 
				"SELECT value_double, value_string FROM psm_annotation " 
						+ " INNER JOIN psm ON psm_annotation.psm_id = psm.id "
						+ " INNER JOIN unlinked ON unlinked.psm_id = psm.id "
						+ " WHERE annotation_type_id = ? "
						+ " AND  psm.search_id = ? "
						+ " AND  unlinked.nrseq_id = ? "

						+ " ORDER BY value_double " + orderDirection + " LIMIT 1 ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, annotation_type_id );
			paramCounter++;
			pstmt.setInt( paramCounter, searchUnlinkedGenericLookupDTO.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchUnlinkedGenericLookupDTO.getNrseqId() );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {

				result = new BestFilterableAnnotationValue();
				
				
				result.setBestValue( rs.getDouble( "value_double" ) );
				result.setBestValueString( rs.getString( "value_string" ) );
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

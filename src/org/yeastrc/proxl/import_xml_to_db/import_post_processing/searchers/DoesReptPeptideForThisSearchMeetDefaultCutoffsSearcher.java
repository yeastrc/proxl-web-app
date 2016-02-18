package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.SearcherGeneralConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;

/**
 * 
 * Does the reported peptide for this search id meet the default peptide cutoffs
 * 
 */
public class DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher {
	
	private static final Logger log = Logger.getLogger(DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher.class);
	
	private DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher() { }
	private static final DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher _INSTANCE = new DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher();
	public static DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher getInstance() { return _INSTANCE; }
	
	


	/**
	 * Does the reported peptide for this search id meet the default peptide cutoffs
	 * 
	 * @param annotationTypeDTOList - Reported Peptide Filterable Annotation List
	 * @param search_id
	 * @param reported_peptide_id
	 * @return
	 * @throws Exception
	 */
	public Yes_No__NOT_APPLICABLE_Enum doesReptPeptideForThisSearchMeetDefaultCutoffs( 
			
			List<AnnotationTypeDTO> annotationTypeDTOList,

			int search_id, 
			int reported_peptide_id ) throws Exception {
		
		
		Yes_No__NOT_APPLICABLE_Enum result = Yes_No__NOT_APPLICABLE_Enum.NO;

		//  Build list of only Default Filters, This is the only list that will be used in the rest of this method
		
		List<AnnotationTypeDTO> annotationTypeDTO_Default_Filters_Only_List =
				new ArrayList<>( annotationTypeDTOList.size() );
				
		for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTOList ) {
			
			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}

			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				annotationTypeDTO_Default_Filters_Only_List.add( annotationTypeDTO );
			}
		}
		
		if ( annotationTypeDTO_Default_Filters_Only_List.isEmpty() ) {
			
			//  No Default Filters so no records can be found
			
			return Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;  //  EARLY EXIT
		}
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		StringBuilder sqlSB = new StringBuilder( 10000 );
		

		sqlSB.append( "SELECT COUNT(*) AS count FROM  " ); 

		{

			for ( int counter = 1; counter <= annotationTypeDTO_Default_Filters_Only_List.size(); counter++ ) {

				if ( counter > 1 ) {

					sqlSB.append( " INNER JOIN " );
				}

				sqlSB.append( " srch__rep_pept__annotation AS tbl_" );
				sqlSB.append( Integer.toString( counter ) );


				if ( counter > 1 ) {

					sqlSB.append( " ON "  );

					sqlSB.append( "tbl_" );
					sqlSB.append( Integer.toString( counter - 1 ) );
					sqlSB.append( ".psm_id " );

					sqlSB.append( " = " );

					sqlSB.append( "tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".psm_id" );
				}

			}

		}
				
		sqlSB.append( " WHERE ( " );


		{
			int counter = 0; 

			for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTO_Default_Filters_Only_List ) {

				counter++;

				if ( counter > 1 ) {

					sqlSB.append( " ) AND ( " );
				}


				sqlSB.append( "tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".search_id = ? AND " );

				sqlSB.append( "tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".reported_peptide_id = ? AND " );


				sqlSB.append( "tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".annotation_type_id = ? AND " );

				sqlSB.append( "tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".value_double " );

				if ( annotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

					sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

				} else {

					sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

				}

				sqlSB.append( "? " );
			}
		}
		
		sqlSB.append( " ) " );
		
		String sql = sqlSB.toString();
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			
			int pstmtCounter = 0;

			
			for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTO_Default_Filters_Only_List ) {
			
				pstmtCounter++;
				pstmt.setInt( pstmtCounter, search_id );
				pstmtCounter++;
				pstmt.setInt( pstmtCounter, reported_peptide_id );

				pstmtCounter++;
				pstmt.setInt( pstmtCounter, annotationTypeDTO.getId() );

				pstmtCounter++;
				pstmt.setDouble( pstmtCounter, annotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValue() );
			}
			
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				int count = rs.getInt( "count" );
				
				if ( count > 0 ) {
					
					result = Yes_No__NOT_APPLICABLE_Enum.YES;
				} else {
					
					result = Yes_No__NOT_APPLICABLE_Enum.NO;
				}
			}
			
		} catch ( Exception e ) {
			
			String msg = "doesReptPeptideForThisSearchMeetDefaultCutoffs(), sql: " + sql;
			
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

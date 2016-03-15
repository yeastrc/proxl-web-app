package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.SearcherGeneralConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * 
 * Get psm count from psm_annotation for default filter value, annotation_type_id, search_id and reported_peptide_id
 */
public class GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher {
	
	private static final Logger log = Logger.getLogger(GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher.class);
	
	private GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher() { }
	private static final GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher _INSTANCE = new GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher();
	public static GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher getInstance() { return _INSTANCE; }
	
	


	/**
	 * Get psm count from psm_annotation for All the Default Filters, search_id, and reported_peptide_id
	 * 
	 * @param srchPgmFilterablePsmAnnotationTypeDTOList - PSM Filterable Annotation List
	 * @param search_id
	 * @param reported_peptide_id
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForAllDefaultFilterValues( 
			
			List<AnnotationTypeDTO> psmFilterableFilterableAnnotationTypeDTO_Default_Filters_Only_List, 
			int search_id, 
			int reported_peptide_id ) throws Exception {
		
		
		int result = 0;

		

		if ( psmFilterableFilterableAnnotationTypeDTO_Default_Filters_Only_List.isEmpty() ) {
			
			//  No Default Filters so no records can be found
			
			return 0;  //  EARLY EXIT
		}
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		StringBuilder sqlSB = new StringBuilder( 10000 );
		

		sqlSB.append( "SELECT COUNT(*) AS count FROM  " ); 

		{

			for ( int counter = 1; counter <= psmFilterableFilterableAnnotationTypeDTO_Default_Filters_Only_List.size(); counter++ ) {

				if ( counter > 1 ) {

					sqlSB.append( " INNER JOIN " );
				}

				sqlSB.append( " psm_filterable_annotation__generic_lookup AS tbl_" );
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

			for ( AnnotationTypeDTO annotationTypeDTO : psmFilterableFilterableAnnotationTypeDTO_Default_Filters_Only_List ) {

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

				sqlSB.append( " ? " );
			}
		}
		
		sqlSB.append( " ) " );
		
		String sql = sqlSB.toString();
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			
			int pstmtCounter = 0;

			
			for ( AnnotationTypeDTO annotationTypeDTO : psmFilterableFilterableAnnotationTypeDTO_Default_Filters_Only_List ) {
			
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

				result = rs.getInt( "count" );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getPsmCountForAllDefaultFilterValues(), sql: " + sql;
			
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

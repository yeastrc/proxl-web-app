package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * Get count of PSMs where only those PSMs are associated with their scan ids 
 * for the current search and meet the current Peptide and PSM cutoffs 
 *
 */
public class PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher {

	private PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher() { }
	public static PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher getInstance() { return new PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher(); }

	private static final Logger log = Logger.getLogger(PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.class);
	

			

	private final String SQL_FIRST_PART = 
			
			"SELECT COUNT(*) AS count FROM ( "
			
			+ 	"SELECT psm.scan_id " 
			
			+ 	"FROM psm  "
			+ 	"INNER JOIN psm AS psm_other ON psm.scan_id = psm_other.scan_id ";
	
	

	private final String SQL_WHERE_MAIN = 

			" WHERE psm.reported_peptide_id = ? AND psm.search_id = ?  "
			+ 	"AND psm_other.search_id = ?  ";
	
	

	private final String SQL_LAST_PART = 

			 	"GROUP BY psm.scan_id "
			+ 	"HAVING COUNT(*) < 2"
		
			+ ") AS unique_psms";

	
	/**
	 * @param reportedPeptideId
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForUniquePSM_SearchIdReportedPeptideId( int reportedPeptideId, int searchId, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		
		
		int numPsms = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();


		
//
//		////////////
//		
//		//  All cutoffs are default?
//		
////		boolean onlyDefaultPeptideCutoffs = true;
//		
//		boolean onlyDefaultPsmCutoffs = true;
//		
//		
//		
//		//   Check if all Peptide Cutoffs are default values
//		
////		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
////			
////			if ( ! item.annotationValueMatchesDefault() ) {
////				
////				onlyDefaultPeptideCutoffs = false;
////				break;
////			}
////		}
//
//		//   Check if all Psm Cutoffs are default values
//		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( SQL_FIRST_PART );

		

		//  Add inner join for each PSM cutoff for "psm" alias

		{
			for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

				sqlSB.append( " INNER JOIN " );


				//  If slow, use psm_filterable_annotation__generic_lookup and put more limits in query on search, reported peptide, and maybe link type

				sqlSB.append( " psm_annotation AS psm_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );

				sqlSB.append( " ON "  );

				sqlSB.append( " psm.id = "  );

				sqlSB.append( "psm_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".psm_id" );

			}
		}

		

		//  Add inner join for each PSM cutoff for "psm_other" alias

		{
			for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

				sqlSB.append( " INNER JOIN " );


				//  If slow, use psm_filterable_annotation__generic_lookup and put more limits in query on search, reported peptide, and maybe link type

				sqlSB.append( " psm_annotation AS psm_fltrbl_tbl_othr_psm_" );
				sqlSB.append( Integer.toString( counter ) );

				sqlSB.append( " ON "  );

				sqlSB.append( " psm_other.id = "  );

				sqlSB.append( "psm_fltrbl_tbl_othr_psm_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".psm_id" );

			}
		}
		

		{
			
			//  Add inner join for each Peptide cutoff for peptides joined to the "psm_other" alias
			
			for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {
				
				sqlSB.append( " INNER JOIN " );
				
				sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );

				sqlSB.append( " ON "  );

				sqlSB.append( " psm_other.search_id = "  );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".search_id" );

				sqlSB.append( " AND " );


				sqlSB.append( " psm_other.reported_peptide_id = "  );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".reported_peptide_id" );

			}
		}

		
		////////////////

		sqlSB.append( SQL_WHERE_MAIN );

		////////////////
		

		{

			int counter = 0; 

			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {


				AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

				counter++;

				sqlSB.append( " AND " );

				sqlSB.append( " ( " );

				sqlSB.append( "psm_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".annotation_type_id = ? AND " );

				sqlSB.append( "psm_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".value_double " );

				if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
					
					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

					sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

				} else {

					sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

				}

				sqlSB.append( " ? " );

				sqlSB.append( " ) " );
			}
		}

		{

			int counter = 0; 

			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {


				AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

				counter++;

				sqlSB.append( " AND " );

				sqlSB.append( " ( " );

				sqlSB.append( "psm_fltrbl_tbl_othr_psm_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".annotation_type_id = ? AND " );

				sqlSB.append( "psm_fltrbl_tbl_othr_psm_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".value_double " );

				if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
					
					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

					sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

				} else {

					sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

				}

				sqlSB.append( " ? " );

				sqlSB.append( " ) " );
			}
		}
		

		//  Process Peptide Cutoffs for WHERE

		{
			int counter = 0; 
			
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

				AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
				
				counter++;

				sqlSB.append( " AND " );
				
				sqlSB.append( " ( " );


				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".search_id = ? AND " );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".annotation_type_id = ? AND " );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".value_double " );

				if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
					
					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

					sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

				} else {

					sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

				}

				sqlSB.append( "? " );

				sqlSB.append( " ) " );
			}
		}
		

		
		////////////////
		
		sqlSB.append( SQL_LAST_PART );

		////////////////
		
		
		String sql = sqlSB.toString();
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );


			int paramCounter = 0;
			
			
			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId ); // psm.reported_peptide_id

			paramCounter++;
			pstmt.setInt( paramCounter, searchId ); // psm.search_id

			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId ); // psm_other.search_id



			// Process PSM Cutoffs for WHERE  for "psm" alias


			{
				
//				if ( ! onlyDefaultPsmCutoffs ) {
					
					//  PSM Cutoffs are not the default 
					

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {

						AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

						paramCounter++;
						pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

						paramCounter++;
						pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
					}
//				}
			}
			

			// Process PSM Cutoffs for WHERE  for "psm_other" alias


			{
				
//				if ( ! onlyDefaultPsmCutoffs ) {
					
					//  PSM Cutoffs are not the default 
					

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {

						AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

						paramCounter++;
						pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

						paramCounter++;
						pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
					}
//				}
			}
			



			// Process Peptide Cutoffs for WHERE for Reported Peptides joined to the "psm_oth" alias


			{

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
					
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );

					paramCounter++;
					pstmt.setInt( paramCounter, srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );

					paramCounter++;
					pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
				}
			}
			
			
			
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				numPsms = rs.getInt( "count" );
			}


		} catch ( Exception e ) {

			log.error( "ERROR:  SQL: " + sql, e );

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
		
		return numPsms;		
	}
	
}

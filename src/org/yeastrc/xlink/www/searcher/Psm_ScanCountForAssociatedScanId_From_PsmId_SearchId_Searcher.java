package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * Is only this PSM associated with it's scan ids 
 * for the current search and meet the current Peptide and PSM cutoffs 
 *
 */
public class Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher {

	private Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher() { }
	public static Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher getInstance() { return new Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher(); }

	private static final Logger log = Logger.getLogger(Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher.class);
	

	
	private static final String SQL_MAIN = 
		
			"SELECT COUNT(*) AS count FROM ( " 
			
			;
			
			
			
			
	private static final String SQL_PSM_SUBSELECT =
			
			 	" SELECT psm.id AS psm_id, search_id, reported_peptide_id  FROM psm ";
	
	private static final String SQL_PSM_SUBSELECT_WHERE_START =
			" WHERE psm.scan_id = ? AND psm.id <> ?  AND psm.search_id = ?  ";
		

			
			
	
	/**
	 * @param psmDTO
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public int scanCountForAssociatedScanId( PsmDTO psmDTO, int searchId, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		
		if ( psmDTO == null ) {
			
			throw new IllegalArgumentException( "psmDTO cannot be null" );
		}
		
		if ( psmDTO.getScanId() == null ) {
			
			return 0;
		}
		
		
		int numPsms = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		

		

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();



		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( SQL_MAIN );
		
		sqlSB.append( SQL_PSM_SUBSELECT );
		
		

		//  Add inner join for each PSM cutoff

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

		////   Start PSM Subselect WHERE
		
		sqlSB.append( SQL_PSM_SUBSELECT_WHERE_START );
		
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


		sqlSB.append( " ) AS psm_search_results " );  //  Close  PSM Subselect
		
		
		//  End PSM Subselect
		
		
		//  Peptide Cutoffs

		{
			
			//  Add inner join for each Peptide cutoff
			
			for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {
				
				sqlSB.append( " INNER JOIN " );
				
				sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );

				sqlSB.append( " ON "  );

				sqlSB.append( " psm_search_results.search_id = "  );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".search_id" );

				sqlSB.append( " AND " );


				sqlSB.append( " psm_search_results.reported_peptide_id = "  );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".reported_peptide_id" );

			}
		}

		
		//   Defer adding main " WHERE " until add a condition
		
		boolean isMainWhereAdded = false;
		

		//  Process Peptide Cutoffs for WHERE

		{
			int counter = 0; 
			
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

				AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
				
				counter++;

				if ( ! isMainWhereAdded ) {
					
					isMainWhereAdded = true;

					/////   Main WHERE
					
					sqlSB.append( " WHERE " );
					
					
				}
				
				if ( counter > 1 ) {

					sqlSB.append( " AND " );
				}
				
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
		
		//  If add more SQL to main WHERE clause, add this code before it:

//		if ( ! isMainWhereAdded ) {
//			
//			isMainWhereAdded = true;
//
//			/////   Main WHERE
//			
//			sqlSB.append( " WHERE " );
//		}
		
		
		
		
		
		String sql = sqlSB.toString();
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			//  PSM Subselect

			paramCounter++;
			pstmt.setInt( paramCounter, psmDTO.getScanId() );

			paramCounter++;
			pstmt.setInt( paramCounter, psmDTO.getId() );

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			
			//  PSM cutoffs

			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {

				AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

				paramCounter++;
				pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				paramCounter++;
				pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
			}


			// Process Peptide Cutoffs for WHERE


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

package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;




/**
 * 
 *
 */
public class MergedSearchPeptideSearcher {

	private static final Logger log = Logger.getLogger( MergedSearchPeptideSearcher.class );
			
			
	public static MergedSearchPeptideSearcher getInstance() { return new MergedSearchPeptideSearcher(); }
	
	// private constructor
	private MergedSearchPeptideSearcher( ) { }
	
	

//	String sql = "SELECT DISTINCT ndpp.peptide_id "
//			+ ""
//			+ " FROM nrseq_database_peptide_protein AS ndpp "
//			
//			+ "INNER JOIN psm_peptide ON ndpp.peptide_id = psm_peptide.peptide_id "
//			
//			+ "INNER JOIN psm ON psm_peptide.psm_id = psm.id "
//			
//			+ "INNER JOIN search_reported_peptide "
//			+ 		" ON ( psm.search_id = search_reported_peptide.search_id "
//			+ 			" AND psm.reported_peptide_id = search_reported_peptide.reported_peptide_id ) "
//			
//			+ "WHERE ndpp.nrseq_id = ? AND psm.search_id IN (#SEARCHES#) AND psm.q_value <= ? "
//			+ 		" AND  ( search_reported_peptide.q_value <= ? OR search_reported_peptide.q_value IS NULL ) ";
	

	private final String SQL_FIRST_PART = 
			

			"SELECT DISTINCT subquery_result.peptide_id "
		
			+ " FROM "
		
			+ " ( ";
	

	private final String SQL_LAST_PART = 
			
		  " ) AS subquery_result   ";
		
	
	private final String SQL_EACH_UNION_FIRST_PART_PART = 
			
			"SELECT DISTINCT ndpp.peptide_id "

			+ " FROM nrseq_database_peptide_protein AS ndpp "
			
			+ "INNER JOIN psm_peptide ON ndpp.peptide_id = psm_peptide.peptide_id "
			
			+ "INNER JOIN psm ON psm_peptide.psm_id = psm.id ";


	private final String SQL_SUB_PER_UNION_SELECT_WHERE_START = 

			" WHERE ndpp.nrseq_id = ? AND psm.search_id = ?  ";


	/**
	 * Internal class for holding the cutoffs for a specific search
	 *
	 */
	private static class CutoffsPerSearchHolder {
		
		int searchId;
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList;
		
		boolean onlyDefaultPsmCutoffs;

		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
	}
	
	
	
	/**
	 * Get all peptides identified for the given protein in the given search, regardless of the type
	 * of peptide (i.e., regardless of whether or not it's a crosslink, looplink, monolink, dimer or none of the above)
	 * @param protein
	 * @return
	 * @throws Exception
	 */
	public Collection<PeptideDTO> getPeptides( NRProteinDTO protein, Collection<SearchDTO> searchesParam, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		Collection<PeptideDTO> peptides = new HashSet<PeptideDTO>();
		

		
		List<SearchDTO> searches = new ArrayList<>( searchesParam );
		
		Collections.sort( searches ); //  ensure in id order
		


		
		//  Copy cutoff values to lists (need to guarantee order since process same objects in multiple places)

		List<CutoffsPerSearchHolder> cutoffsPerSearchHolderList = new ArrayList<>( searches.size() );
		
		//  Process cutoffs per search
		
		for ( SearchDTO searchDTO : searches ) {
			
			int searchId = searchDTO.getId();
		
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
			
			if ( searcherCutoffValuesSearchLevel == null ) {
				

				searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
				
//				String msg = "Unable to get cutoffs for search id: " + searchId;
//				log.error( msg );
//				throw new ProxlWebappDataException(msg);
			}


			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerSearchList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesPerSearchList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();



			//  All cutoffs for search id are default?
			

			Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
				
			boolean onlyDefaultPsmCutoffs = true;
			

			for ( SearcherCutoffValuesAnnotationLevel entry  : psmCutoffValuesPerSearchList ) {

				if ( ! entry.annotationValueMatchesDefault() ) {
					
					onlyDefaultPsmCutoffs = false;
					break;
				}
			}
			


			//   Check if any Peptide Cutoffs are default filters
			
			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesPerSearchList ) {

				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
					
					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
					
					defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
					break;
				}
			}
			
			
			//   Check if all Peptide Cutoffs are default values
			
			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesPerSearchList ) {
				
				if ( ! item.annotationValueMatchesDefault() ) {
					
					defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
					break;
				}
			}

			
			CutoffsPerSearchHolder cutoffsPerSearchHolder = new CutoffsPerSearchHolder();
			
			cutoffsPerSearchHolder.searchId = searchId;
			
			cutoffsPerSearchHolder.peptideCutoffValuesList = peptideCutoffValuesPerSearchList;
			cutoffsPerSearchHolder.psmCutoffValuesList = psmCutoffValuesPerSearchList;
			
			cutoffsPerSearchHolder.onlyDefaultPsmCutoffs = onlyDefaultPsmCutoffs;
			
			cutoffsPerSearchHolder.defaultPeptideCutoffs = defaultPeptideCutoffs;
			
			cutoffsPerSearchHolderList.add( cutoffsPerSearchHolder );
		}

		////////////
		
		//  All cutoffs are default?

		boolean onlyDefaultPsmCutoffsAllSearches = true;
		


		//   Check if all Psm Cutoffs are default values
		
		for ( CutoffsPerSearchHolder item : cutoffsPerSearchHolderList ) {
			
			if ( ! item.onlyDefaultPsmCutoffs ) {
				
				onlyDefaultPsmCutoffsAllSearches = false;
				break;
			}
		}
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( SQL_FIRST_PART );
		
		boolean firstCutoffsPerSearchHolder = true;
		

		for ( CutoffsPerSearchHolder cutoffsPerSearchHolder : cutoffsPerSearchHolderList ) {

			if ( firstCutoffsPerSearchHolder ) {
				
				firstCutoffsPerSearchHolder = false;
			} else {
				
				sqlSB.append( " UNION " );
			}
			
			sqlSB.append( SQL_EACH_UNION_FIRST_PART_PART );
			

			{

				//  Non-Default PSM cutoffs so have to query on the cutoffs


				int tableIndexCounter = 0;

//				if ( ! cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {


					//  Add inner join for each PSM cutoff

					for ( int index = 1; index <= cutoffsPerSearchHolder.psmCutoffValuesList.size(); index++ ) {


						tableIndexCounter++;

						sqlSB.append( " INNER JOIN " );

						sqlSB.append( " psm_annotation AS psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );

						sqlSB.append( " ON "  );

						sqlSB.append( " psm.id = "  );

						sqlSB.append( "psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
						sqlSB.append( ".psm_id" );

					}
//				}

			}

		
			{
//				if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

					//  Non-Default PSM cutoffs so have to query on the cutoffs


					//  Add inner join for each Peptide cutoff

					int tableIndexCounter = 0;


					for ( int index = 1; index <= cutoffsPerSearchHolder.peptideCutoffValuesList.size(); index++ ) {

						tableIndexCounter++;

						sqlSB.append( " INNER JOIN " );

						sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );

						sqlSB.append( " ON "  );

						sqlSB.append( " psm.search_id = "  );

						sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
						sqlSB.append( ".search_id" );

						sqlSB.append( " AND " );


						sqlSB.append( " psm.reported_peptide_id = "  );

						sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
						sqlSB.append( ".reported_peptide_id" );

					}
//				}
			}


			//////////

			sqlSB.append( SQL_SUB_PER_UNION_SELECT_WHERE_START );

			//////////



			// Process PSM Cutoffs for WHERE

			{


//				if ( cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {
//
//					//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
//
//
//					sqlSB.append( " AND " );
//
//				Table "unified_rp__rep_pept__search__generic_lookup" is not part of this SQL so cannot use this comparison
//				
//					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );
//
//
//				} else {


					//  Non-Default PSM cutoffs so have to query on the cutoffs

					int counter = 0; 

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : cutoffsPerSearchHolder.psmCutoffValuesList ) {


						AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

						counter++;

						sqlSB.append( " AND " );

						sqlSB.append( " ( " );


//						sqlSB.append( "psm_fltrbl_tbl_" );
//						sqlSB.append( Integer.toString( counter ) );
//						sqlSB.append( ".search_id = ? AND " );

						sqlSB.append( "psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".annotation_type_id = ? AND " );

						sqlSB.append( "psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".value_double " );

						if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType()
								== FilterDirectionType.ABOVE ) {

							sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

						} else {

							sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

						}

						sqlSB.append( " ? " );

						sqlSB.append( " ) " );
					}
//				}
			}

			//  Process Peptide Cutoffs for WHERE

			{
				

//				if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE ) {
//
//					//  No WHERE criteria for defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
//					
//					//     There are no Peptide cutoffs to apply
//					
//					
//				
//				} else if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {
//
//					//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes
//
//					sqlSB.append( " AND " );
//
//
//					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.peptide_meets_default_cutoffs = '" );
//					sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
//					sqlSB.append( "' " );
//
//					
//				} else if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

					
					//  Non-Default Peptide cutoffs so have to query on the cutoffs

					int counter = 0; 

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : cutoffsPerSearchHolder.peptideCutoffValuesList ) {

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

						if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
								== FilterDirectionType.ABOVE ) {

							sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

						} else {

							sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

						}

						sqlSB.append( "? " );

						sqlSB.append( " ) " );
					}
//				}
			}
		}
		
		sqlSB.append( SQL_LAST_PART );
		
		
		
		
		String sql = sqlSB.toString();
		
		//////////////////////////////////
		
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			
//			Collection<Integer> searchIds = new HashSet<Integer>();
//			for( SearchDTO search : searchs )
//				searchIds.add( search.getId() );
//			
//			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			


			for ( CutoffsPerSearchHolder cutoffsPerSearchHolder : cutoffsPerSearchHolderList ) {



				paramCounter++;
				pstmt.setInt( paramCounter, protein.getNrseqId() );
				
				
				paramCounter++;
				pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );
				
				

				// Process PSM Cutoffs for WHERE


				{

//					if ( ! cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {

						//  PSM Cutoffs are not the default 

						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : cutoffsPerSearchHolder.psmCutoffValuesList ) {

							AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

//							paramCounter++;
//							pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );

							paramCounter++;
							pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

							paramCounter++;
							pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
						}

//					}
				}




				// Process Peptide Cutoffs for WHERE


				{

//					if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {
						
						//  Non-Default Peptide cutoffs so have to query on the cutoffs

						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : cutoffsPerSearchHolder.peptideCutoffValuesList ) {

							AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

							paramCounter++;
							pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );

							paramCounter++;
							pstmt.setInt( paramCounter, srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );

							paramCounter++;
							pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
						}

//					}
				}
			}
			
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				peptides.add( PeptideDAO.getInstance().getPeptideDTOFromDatabase( rs.getInt( 1 ) ) );
			}

		} catch ( Exception e ) {

			String msg = "Exception in getPeptides( Collection<SearchDTO> searches, ... ), sql: " + sql;
			
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

		return peptides;
	}
	
}

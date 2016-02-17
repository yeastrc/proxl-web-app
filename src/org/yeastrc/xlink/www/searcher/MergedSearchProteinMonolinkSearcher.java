package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinMonolink;



/**
 * 
 *
 */
public class MergedSearchProteinMonolinkSearcher {

	private static final Logger log = Logger.getLogger( MergedSearchProteinMonolinkSearcher.class );
			
			
	private MergedSearchProteinMonolinkSearcher() { }
	private static final MergedSearchProteinMonolinkSearcher _INSTANCE = new MergedSearchProteinMonolinkSearcher();
	public static MergedSearchProteinMonolinkSearcher getInstance() { return _INSTANCE; }

	
	private final String SEARCH_ID_GROUP_SEPARATOR = ","; //  separator as search ids are combined by the group by

	
//	String sql = "SELECT nrseq_id, protein_position, min(bestPSMQValue), min(bestPeptideQValue) "
//			+ "FROM search_monolink_lookup WHERE search_id IN (#SEARCHES#) AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
//			+ "GROUP BY nrseq_id, protein_position "
//			+ "ORDER BY nrseq_id, protein_position";
	
	
	

	private final String SQL_FIRST_PART = 
			
			"SELECT subquery_result.nrseq_id, "
			+ " subquery_result.protein_position, "
			
			+ " GROUP_CONCAT( DISTINCT subquery_result.search_id SEPARATOR '" + SEARCH_ID_GROUP_SEPARATOR + "' ) AS search_ids, "

			+ " SUM( subquery_result.num_psm_at_default_cutoff ) AS num_psm_at_default_cutoff "
//			+ "   , "
//			+ " SUM( subquery_result.num_linked_peptides_at_default_cutoff ) AS num_linked_peptides_at_default_cutoff "
//			+ " SUM( subquery_result.num_unique_peptides_linked_at_default_cutoff ) AS num_unique_peptides_linked_at_default_cutoff "
			
			+ " FROM "
			
			+ " ( ";
			

	  
	private final String SQL_LAST_PART = 
			
		  " ) AS subquery_result  GROUP BY nrseq_id, protein_position ";
		

	private final String SQL_EACH_UNION_FIRST_PART_PART = 
			

			"SELECT search_monolink_generic_lookup.search_id,"
					+ " search_monolink_generic_lookup.nrseq_id, "
					+ " search_monolink_generic_lookup.protein_position, "

					+ " search_monolink_generic_lookup.num_psm_at_default_cutoff "
//					+ "   , "
//					+ " search_monolink_generic_lookup.num_linked_peptides_at_default_cutoff, "
//					+ " search_monolink_generic_lookup.num_unique_peptides_linked_at_default_cutoff "

					+ " FROM search_monolink_generic_lookup";

			


	private final String SQL_SUB_PER_UNION_SELECT_WHERE_START = 
					
			" WHERE search_monolink_generic_lookup.search_id = ?   ";
		

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
	 * @param searchesParam
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws Exception
	 */
	public List<MergedSearchProteinMonolink> search( Collection<SearchDTO> searchesParam, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		List<MergedSearchProteinMonolink> links = new ArrayList<MergedSearchProteinMonolink>();
		

		
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
		
		Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffsAllSearches = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
		


		//   Check if all Psm Cutoffs are default values
		
		for ( CutoffsPerSearchHolder item : cutoffsPerSearchHolderList ) {
			
			if ( ! item.onlyDefaultPsmCutoffs ) {
				
				onlyDefaultPsmCutoffsAllSearches = false;
				break;
			}
		}
		



		//   Check if any Peptide Cutoffs are default values
		
		for ( CutoffsPerSearchHolder item : cutoffsPerSearchHolderList ) {
			
			if ( item.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {
				
				defaultPeptideCutoffsAllSearches = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		

		//   Check if any Peptide Cutoffs are NOT default values
		
		for ( CutoffsPerSearchHolder item : cutoffsPerSearchHolderList ) {
			
			if ( item.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {
				
				defaultPeptideCutoffsAllSearches = Yes_No__NOT_APPLICABLE_Enum.NO;
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



				if ( ! cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {


					//  Non-Default PSM cutoffs so have to query on the cutoffs

					//  Add inner join for each PSM cutoff


					int counter = 0;
					
					for ( int index = 1; index <= cutoffsPerSearchHolder.psmCutoffValuesList.size(); index++ ) {


						counter++;

						sqlSB.append( " INNER JOIN " );


						sqlSB.append( " search_monolink_best_psm_value_generic_lookup AS best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );

						sqlSB.append( " ON "  );

						
						sqlSB.append( " search_monolink_generic_lookup.search_id = "  );

						sqlSB.append( "best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".search_id" );

						sqlSB.append( " AND " );

						sqlSB.append( " search_monolink_generic_lookup.nrseq_id = "  );

						sqlSB.append( "best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".nrseq_id" );


						sqlSB.append( " AND " );

						sqlSB.append( " search_monolink_generic_lookup.protein_position = "  );

						sqlSB.append( "best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".protein_position" );


					}
				}

			}

			{
				if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

					//  Non-Default PSM cutoffs so have to query on the cutoffs


					//  Add inner join for each Peptide cutoff

					int counter = 0;


					for ( int index = 1; index <= cutoffsPerSearchHolder.peptideCutoffValuesList.size(); index++ ) {

						counter++;

						sqlSB.append( " INNER JOIN " );

						sqlSB.append( " search_monolink_best_peptide_value_generic_lookup AS best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );

						sqlSB.append( " ON "  );

						
						sqlSB.append( " search_monolink_generic_lookup.search_id = "  );

						sqlSB.append( "best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".search_id" );

						sqlSB.append( " AND " );

						sqlSB.append( " search_monolink_generic_lookup.nrseq_id = "  );

						sqlSB.append( "best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".nrseq_id" );

						sqlSB.append( " AND " );

						sqlSB.append( " search_monolink_generic_lookup.protein_position = "  );

						sqlSB.append( "best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".protein_position" );


					}
				}
			}
		

			//////////

			sqlSB.append( SQL_SUB_PER_UNION_SELECT_WHERE_START );

			//////////




			// Process PSM Cutoffs for WHERE

			{


				if ( cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {

					//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero


					sqlSB.append( " AND " );


					sqlSB.append( " search_monolink_generic_lookup.num_psm_at_default_cutoff > 0 " );


				} else {


					//  Non-Default PSM cutoffs so have to query on the cutoffs

					int counter = 0; 

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : cutoffsPerSearchHolder.psmCutoffValuesList ) {


						AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

						counter++;

						sqlSB.append( " AND " );

						sqlSB.append( " ( " );


						sqlSB.append( "best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".search_id = ? AND " );

						sqlSB.append( "best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".annotation_type_id = ? AND " );

						sqlSB.append( "best_psm_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".best_psm_value_for_ann_type_id " );

						if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
								== FilterDirectionType.ABOVE ) {

							sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

						} else {

							sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

						}

						sqlSB.append( " ? " );

						sqlSB.append( " ) " );
					}
				}
			}

			//  Process Peptide Cutoffs for WHERE

			{
				

				if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE ) {

					//  No WHERE criteria for defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
					
					//     There are no Peptide cutoffs to apply
					
					
				
				} else if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {

					//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

					sqlSB.append( " AND " );


					sqlSB.append( " search_monolink_generic_lookup.num_linked_peptides_at_default_cutoff > 0 " );

					
				} else if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

					
					//  Non-Default Peptide cutoffs so have to query on the cutoffs

					int counter = 0; 

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : cutoffsPerSearchHolder.peptideCutoffValuesList ) {

						AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

						counter++;

						sqlSB.append( " AND " );

						sqlSB.append( " ( " );


						sqlSB.append( "best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".search_id = ? AND " );

						sqlSB.append( "best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".annotation_type_id = ? AND " );

						sqlSB.append( "best_rep_pept_value_tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".best_peptide_value_for_ann_type_id " );

						if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
								== FilterDirectionType.ABOVE ) {

							sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

						} else {

							sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

						}

						sqlSB.append( "? " );

						sqlSB.append( " ) " );
					}
				}
			}
		}
		
		sqlSB.append( SQL_LAST_PART );
		
		
		
		
		String sql = sqlSB.toString();
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
//			Collection<Integer> searchIds = new HashSet<Integer>();
//			for( SearchDTO search : searches )
//				searchIds.add( search.getId() );
//			
//			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
						
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			


			for ( CutoffsPerSearchHolder cutoffsPerSearchHolder : cutoffsPerSearchHolderList ) {

				paramCounter++;
				pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );
				
				

				// Process PSM Cutoffs for WHERE


				{

					if ( ! cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {

						//  PSM Cutoffs are not the default 

						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : cutoffsPerSearchHolder.psmCutoffValuesList ) {

							AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

							paramCounter++;
							pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );

							paramCounter++;
							pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

							paramCounter++;
							pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
						}

					}
				}




				// Process Peptide Cutoffs for WHERE


				{

					if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {
						
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

					}
				}
			}
			
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				
				MergedSearchProteinMonolink link = new MergedSearchProteinMonolink();
				
//				link.setPsmCutoff( psmCutoff );
//				link.setPeptideCutoff( peptideCutoff );
				
				
				link.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( "nrseq_id" ) ) ) );

				
				link.setProteinPosition( rs.getInt( "protein_position" ) );
				
				link.setSearches( searches );
				
				link.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
				
				//  These counts are only valid for PSM and Peptide at default cutoffs

				if ( onlyDefaultPsmCutoffsAllSearches 
						&& ( defaultPeptideCutoffsAllSearches == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
								||  defaultPeptideCutoffsAllSearches == Yes_No__NOT_APPLICABLE_Enum.YES ) ) {

					link.setNumPsms( rs.getInt( "num_psm_at_default_cutoff" ) );
//					link.setNumLinkedPeptides( rs.getInt( "num_linked_peptides_at_default_cutoff" ) );
//					link.setNumUniqueLinkedPeptides( rs.getInt( "num_unique_peptides_linked_at_default_cutoff" ) );
				}

//				link.setBestPSMQValue( rs.getDouble( 3 ) );
//				
//				link.setBestPeptideQValue( rs.getDouble( 4 ) );
//				if ( rs.wasNull() ) {
//					link.setBestPeptideQValue( null );
//				}
				
				
				//  TODO This probably should change
//				
//				// add search-level info for the protein monolinks:
//				Map<SearchDTO, SearchProteinMonolink> searchMonolinks = new TreeMap<SearchDTO, SearchProteinMonolink>();
//				
//				for( SearchDTO search : searches ) {
//					SearchProteinMonolink tlink = SearchProteinMonolinkSearcher.getInstance().search(search, 
//																										 psmCutoff, 
//																										 peptideCutoff, 
//																										 link.getProtein().getNrProtein(),
//																										 link.getProteinPosition()
//																										);
//
//					if( tlink != null )
//						searchMonolinks.put( search, tlink );
//				}
//				
//				link.setSearchProteinMonolinks( searchMonolinks );
				
				
				links.add( link );
			}
			
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
		
		return links;
	}
	
}

package org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher;
import org.yeastrc.xlink.dao.SearchUnlinkedBestPSMValueGenericLookupDAO;
import org.yeastrc.xlink.dao.SearchUnlinkedBestPeptideValueGenericLookupDAO;
import org.yeastrc.xlink.dao.SearchUnlinkedGenericLookupDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchUnlinkedBestPSMValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchUnlinkedBestPeptideValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchUnlinkedGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_utils.CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords;
import org.yeastrc.xlink.searcher_result_objects.NumPeptidesPSMsForProteinCriteriaResult;
import org.yeastrc.xlink.searchers.AnnotationTypesForSearchIdPSMPeptideTypeSearcher;
import org.yeastrc.xlink.searchers.NumPeptidesPSMsForProteinCriteriaSearcher;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

/**
 * 
 *
 */
public class PopulateSearchUnlinkedGenericLookupTable {

	private static final Logger log = Logger.getLogger( PopulateSearchUnlinkedGenericLookupTable.class );
	
	/**
	 * private constructor
	 */
	private PopulateSearchUnlinkedGenericLookupTable() {  }
	
	public static PopulateSearchUnlinkedGenericLookupTable getInstance() { 
		return new PopulateSearchUnlinkedGenericLookupTable(); 
	}


	private String PRIMARY_SELECT_SQL = 
			
			"SELECT unlinked.nrseq_id "
			
			+ " FROM unlinked "
			+ " INNER JOIN psm ON unlinked.psm_id = psm.id "
			
			+ " WHERE psm.search_id = ? "
			+ " GROUP BY unlinked.nrseq_id ";



	/**
	 * For the given search id, will populate the search_unlinked_lookup table
	 * 
	 * @param searchId
	 * @throws Exception 
	 */
	public void populateSearchUnlinkedGenericLookupTable( int searchId ) throws Exception {
		
		SearchDTO searchDTO = SearchDAO.getInstance().getSearch( searchId );


		//  Get Annotation Type records for PSM and Peptide
		
		
		//  Get  Annotation Type records for PSM
		
		List<AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOList =
				AnnotationTypesForSearchIdPSMPeptideTypeSearcher.getInstance().get_PSM_Filterable_ForSearchId( searchId );
		

		

		//  Get  Annotation Type records for Reported Peptides
		
		List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList =
				AnnotationTypesForSearchIdPSMPeptideTypeSearcher.getInstance().get_Peptide_Filterable_ForSearchId( searchId );
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement st = null;
		ResultSet rs = null;
		
		String sql = PRIMARY_SELECT_SQL;

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

//			st = conn.createStatement();
//			st.execute( disableKeysSQL );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );


			rs = pstmt.executeQuery();

			
			SearchUnlinkedGenericLookupDAO searchUnlinkedGenericLookupDAO = SearchUnlinkedGenericLookupDAO.getInstance();

			while( rs.next() ) {

				SearchUnlinkedGenericLookupDTO item = new SearchUnlinkedGenericLookupDTO();

				item.setSearchId( searchId );
				item.setNrseqId( rs.getInt( "nrseq_id" ) );

				
				//     Get PSM and Peptide counts at default cutoff values
				
				
				//  Get a searcherCutoffValuesSearchLevel object for the default cutoff values 
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
						CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords.getInstance()
						.createSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords( 
								searchId, 
								srchPgm_Filterable_Psm_AnnotationType_DTOList, 
								srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );



				NumPeptidesPSMsForProteinCriteriaResult numPeptidesPSMsForProteinCriteriaResult =
						NumPeptidesPSMsForProteinCriteriaSearcher.getInstance()
						.getNumPeptidesPSMsForUnlinked(
								item.getSearchId(),
								searcherCutoffValuesSearchLevel,
								item.getNrseqId(),
								YRC_NRSEQUtils.getDatabaseIdFromName( searchDTO.getFastaFilename() ) );
				
				
				int numPsmAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
				int numLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();

				int numUniqueLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();

				item.setNumPsmAtDefaultCutoff( numPsmAtDefaultCutoff );
				item.setNumLinkedPeptidesAtDefaultCutoff( numLinkedPeptidesAtDefaultCutoff );
				item.setNumUniqueLinkedPeptidesAtDefaultCutoff( numUniqueLinkedPeptidesAtDefaultCutoff );
				
				searchUnlinkedGenericLookupDAO.save( item );

//				List<SearchUnlinkedBestPSMValueGenericLookupDTO> insertedBestPSMValueRecords =
				populateUnlinkedBestPSMValue( item, srchPgm_Filterable_Psm_AnnotationType_DTOList );
				
//				List<SearchUnlinkedBestPeptideValueGenericLookupDTO> insertedBestPeptideValueRecords = 
				populateUnlinkedBestPeptideValue( item, srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );


			}

//			st.execute( enableKeysSQL );


		} catch ( Exception e ) {

			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' \n sql: " + sql
//					+ "\n disableKeysSQL: " + disableKeysSQL
//					+ "\n enableKeysSQL: " + enableKeysSQL
					, e );

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

			if( st != null ) {
				try { st.close(); } catch( Throwable t ) { ; }
				st = null;
			}

			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}

		}
		
		
	}
	
	
	/**
	 * @param searchUnlinkedGenericLookupDTO
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @throws Exception
	 */
	private List<SearchUnlinkedBestPSMValueGenericLookupDTO> populateUnlinkedBestPSMValue( 
			
			SearchUnlinkedGenericLookupDTO searchUnlinkedGenericLookupDTO,
			
			List<AnnotationTypeDTO>	srchPgm_Filterable_Psm_AnnotationType_DTOList
			
			) throws Exception {
		
		
		List<SearchUnlinkedBestPSMValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOList.size() );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
			GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher.getInstance()
			.getBestAnnotationValue( 
					srchPgmFilterablePsmAnnotationTypeDTO.getId(), 
					searchUnlinkedGenericLookupDTO, 
					srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );
			
			if ( bestFilterableAnnotationValue != null ) {

				SearchUnlinkedBestPSMValueGenericLookupDTO item = new SearchUnlinkedBestPSMValueGenericLookupDTO();

				item.setSearchUnlinkedGenericLookup( searchUnlinkedGenericLookupDTO.getId() );
				
				item.setSearchId( searchUnlinkedGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchUnlinkedGenericLookupDTO.getNrseqId() );

				item.setPsmFilterableAnnotationTypeId( srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				double bestPsmValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPsmValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();

				item.setBestPsmValueForAnnTypeId( bestPsmValueForAnnTypeId );
				item.setBestPsmValueStringForAnnTypeId( bestPsmValueStringForAnnTypeId );

				SearchUnlinkedBestPSMValueGenericLookupDAO.getInstance().save( item );
				
				results.add( item );
			}
		}
		
		return results;
	}
	

	
	/**
	 * @param searchUnlinkedGenericLookupDTO
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @throws Exception 
	 */
	private List<SearchUnlinkedBestPeptideValueGenericLookupDTO> populateUnlinkedBestPeptideValue( 
			
			SearchUnlinkedGenericLookupDTO searchUnlinkedGenericLookupDTO,
			

			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
			
			) throws Exception {
		
		

		List<SearchUnlinkedBestPeptideValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList.size() );
		
		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {

			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher.getInstance()
					.getBestAnnotationValue( 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId(), 
							searchUnlinkedGenericLookupDTO, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );


			if ( bestFilterableAnnotationValue != null ) {

				SearchUnlinkedBestPeptideValueGenericLookupDTO item = new SearchUnlinkedBestPeptideValueGenericLookupDTO();

				item.setSearchUnlinkedGenericLookup( searchUnlinkedGenericLookupDTO.getId() );

				item.setSearchId( searchUnlinkedGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchUnlinkedGenericLookupDTO.getNrseqId() );

				item.setPeptideFilterableAnnotationTypeId( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );


				double bestPeptideValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPeptideValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();



				item.setBestPeptideValueForAnnTypeId( bestPeptideValueForAnnTypeId );
				item.setBestPeptideValueStringForAnnTypeId( bestPeptideValueStringForAnnTypeId );



				SearchUnlinkedBestPeptideValueGenericLookupDAO.getInstance().save( item );

				results.add( item );
			}
		}
		
		return results;
	}
	

}

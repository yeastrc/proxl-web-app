package org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchDimerBestPSMValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchDimerBestPeptideValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchDimerGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchDimerBestPSMValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchDimerBestPeptideValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchDimerGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.number_peptides_psms.NumPeptidesPSMsForProteinCriteriaResult;
import org.yeastrc.xlink.number_peptides_psms.NumPeptidesPSMsForProteinCriteria;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_utils.CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords;
import org.yeastrc.xlink.searchers.AnnotationTypesForSearchIdPSMPeptideTypeSearcher;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

/**
 * 
 *
 */
public class PopulateSearchDimerGenericLookupTable {

	private static final Logger log = Logger.getLogger( PopulateSearchDimerGenericLookupTable.class );
	
	/**
	 * private constructor
	 */
	private PopulateSearchDimerGenericLookupTable() {  }
	
	public static PopulateSearchDimerGenericLookupTable getInstance() { 
		return new PopulateSearchDimerGenericLookupTable(); 
	}


	private String PRIMARY_SELECT_SQL = 
			
			"SELECT dimer.nrseq_id_1, dimer.nrseq_id_2 "
			
			+ " FROM dimer "
			+ " INNER JOIN psm ON dimer.psm_id = psm.id "
			
			+ " WHERE psm.search_id = ? "
			+ " GROUP BY dimer.nrseq_id_1, dimer.nrseq_id_2";



	/**
	 * For the given search id, will populate the search_dimer_lookup table
	 * 
	 * @param searchId
	 * @throws Exception 
	 */
	public void populateSearchDimerGenericLookupTable( int searchId ) throws Exception {
		
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
	    
	    
	    
	    
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

		int processedRecordCount = 0;
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

//			st = conn.createStatement();
//			st.execute( disableKeysSQL );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );


			rs = pstmt.executeQuery();

			
			DB_Insert_SearchDimerGenericLookupDAO db_Insert_SearchDimerGenericLookupDAO = DB_Insert_SearchDimerGenericLookupDAO.getInstance();

			while( rs.next() ) {

				processedRecordCount++;

				SearchDimerGenericLookupDTO item = new SearchDimerGenericLookupDTO();

				item.setSearchId( searchId );
				item.setNrseqId1( rs.getInt( "nrseq_id_1" ) );
				item.setNrseqId2( rs.getInt( "nrseq_id_2" ) );

				
				//     Get PSM and Peptide counts at default cutoff values
				
				
				//  Get a searcherCutoffValuesSearchLevel object for the default cutoff values 
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
						CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords.getInstance()
						.createSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords( 
								searchId, 
								srchPgm_Filterable_Psm_AnnotationType_DTOList, 
								srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );


				NumPeptidesPSMsForProteinCriteriaResult numPeptidesPSMsForProteinCriteriaResult =
						NumPeptidesPSMsForProteinCriteria.getInstance()
						.getNumPeptidesPSMsForDimer(
								item.getSearchId(),
								searcherCutoffValuesSearchLevel,
								item.getNrseqId1(),
								item.getNrseqId2(),
								YRC_NRSEQUtils.getDatabaseIdFromName( searchDTO.getFastaFilename() ) );

				int numPsmAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
				int numLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();

				int numUniqueLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();

				
				item.setNumPsmAtDefaultCutoff( numPsmAtDefaultCutoff );
				item.setNumLinkedPeptidesAtDefaultCutoff( numLinkedPeptidesAtDefaultCutoff );
				item.setNumUniqueLinkedPeptidesAtDefaultCutoff( numUniqueLinkedPeptidesAtDefaultCutoff );
				
				db_Insert_SearchDimerGenericLookupDAO.save( item );
				
//				List<SearchDimerBestPSMValueGenericLookupDTO> insertedBestPSMValueRecords =
				populateDimerBestPSMValue( item, srchPgm_Filterable_Psm_AnnotationType_DTOList );
				
//				List<SearchDimerBestPeptideValueGenericLookupDTO> insertedBestPeptideValueRecords = 
				populateDimerBestPeptideValue( item, srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );

				if ( log.isInfoEnabled() ) {

					if ( ( processedRecordCount % 100000 ) == 0 ) {

						log.info( "populateSearchDimerGenericLookupTable: processed " + processedRecordCount + " records." );
					}
				}
			}

//			st.execute( enableKeysSQL );


		} catch ( Exception e ) {

			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' \n sql: " + sql
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
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();

		if ( log.isInfoEnabled() ) {

			log.info( "populateSearchDimerGenericLookupTable: Record Count Total: " + processedRecordCount );
		}
	}
	
	
	/**
	 * @param searchDimerGenericLookupDTO
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @throws Exception
	 */
	private List<SearchDimerBestPSMValueGenericLookupDTO> populateDimerBestPSMValue( 
			
			SearchDimerGenericLookupDTO searchDimerGenericLookupDTO,
			
			List<AnnotationTypeDTO>	srchPgm_Filterable_Psm_AnnotationType_DTOList
			
			) throws Exception {
		
		
		List<SearchDimerBestPSMValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOList.size() );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
			GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher.getInstance()
			.getBestAnnotationValue( srchPgmFilterablePsmAnnotationTypeDTO.getId(), 
					searchDimerGenericLookupDTO, 
					srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );
			
			if ( bestFilterableAnnotationValue != null ) {

				SearchDimerBestPSMValueGenericLookupDTO item = new SearchDimerBestPSMValueGenericLookupDTO();

				item.setSearchDimerGenericLookup( searchDimerGenericLookupDTO.getId() );
				
				item.setSearchId( searchDimerGenericLookupDTO.getSearchId() );

				item.setNrseqId1( searchDimerGenericLookupDTO.getNrseqId1() );
				item.setNrseqId2( searchDimerGenericLookupDTO.getNrseqId2() );

				item.setPsmFilterableAnnotationTypeId( srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				double bestPsmValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPsmValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();

				item.setBestPsmValueForAnnTypeId( bestPsmValueForAnnTypeId );
				item.setBestPsmValueStringForAnnTypeId( bestPsmValueStringForAnnTypeId );

				DB_Insert_SearchDimerBestPSMValueGenericLookupDAO.getInstance().save( item );
				
				results.add( item );
			}
		}
		
		return results;
	}
	

	
	/**
	 * @param searchDimerGenericLookupDTO
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @throws Exception 
	 */
	private List<SearchDimerBestPeptideValueGenericLookupDTO> populateDimerBestPeptideValue( 
			
			SearchDimerGenericLookupDTO searchDimerGenericLookupDTO,
			

			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
			
			) throws Exception {
		
		

		List<SearchDimerBestPeptideValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList.size() );
		
		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {

			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher.getInstance()
					.getBestAnnotationValue( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId(), 
							searchDimerGenericLookupDTO, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );


			if ( bestFilterableAnnotationValue != null ) {

				SearchDimerBestPeptideValueGenericLookupDTO item = new SearchDimerBestPeptideValueGenericLookupDTO();


				item.setSearchDimerGenericLookup( searchDimerGenericLookupDTO.getId() );
				
				item.setSearchId( searchDimerGenericLookupDTO.getSearchId() );

				item.setNrseqId1( searchDimerGenericLookupDTO.getNrseqId1() );
				item.setNrseqId2( searchDimerGenericLookupDTO.getNrseqId2() );

				item.setPeptideFilterableAnnotationTypeId( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );


				double bestPeptideValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPeptideValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();



				item.setBestPeptideValueForAnnTypeId( bestPeptideValueForAnnTypeId );
				item.setBestPeptideValueStringForAnnTypeId( bestPeptideValueStringForAnnTypeId );



				DB_Insert_SearchDimerBestPeptideValueGenericLookupDAO.getInstance().save( item );

				results.add( item );
			}
		}
		
		return results;
	}
	

}

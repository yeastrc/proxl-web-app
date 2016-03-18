package org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchCrosslinkBestPeptideValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchCrosslinkGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchCrosslinkBestPSMValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchCrosslinkBestPeptideValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchCrosslinkGenericLookupDTO;
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
public class PopulateSearchCrosslinkGenericLookupTable {

	private static final Logger log = Logger.getLogger( PopulateSearchCrosslinkGenericLookupTable.class );
	
	/**
	 * private constructor
	 */
	private PopulateSearchCrosslinkGenericLookupTable() {  }
	
	public static PopulateSearchCrosslinkGenericLookupTable getInstance() { 
		return new PopulateSearchCrosslinkGenericLookupTable(); 
	}

//	private String PRIMARY_SELECT_SQL = 
//			
//			"SELECT crosslink.nrseq_id_1, crosslink.nrseq_id_2, crosslink.protein_1_position, crosslink.protein_2_position "
//			
//			+ " FROM crosslink "
//			+ " INNER JOIN psm ON crosslink.psm_id = psm.id "
//			
//			+ " WHERE psm.search_id = ? "
//			+ " GROUP BY crosslink.nrseq_id_1, crosslink.nrseq_id_2, crosslink.protein_1_position, crosslink.protein_2_position";


	private String PRIMARY_SELECT_SQL = 
			
			"SELECT DISTINCT crosslink.nrseq_id_1, crosslink.nrseq_id_2, crosslink.protein_1_position, crosslink.protein_2_position "
			
			+ " FROM crosslink "
			+ " INNER JOIN psm ON crosslink.psm_id = psm.id "
			
			+ " WHERE psm.search_id = ? ";



	/**
	 * For the given search id, will populate the search_crosslink_lookup table
	 * 
	 * @param searchId
	 * @throws Exception 
	 */
	public void populateSearchCrosslinkGenericLookupTable( int searchId ) throws Exception {
		
		

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

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

//			st = conn.createStatement();
//			st.execute( disableKeysSQL );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );


			rs = pstmt.executeQuery();

			
			DB_Insert_SearchCrosslinkGenericLookupDAO db_Insert_SearchCrosslinkGenericLookupDAO = DB_Insert_SearchCrosslinkGenericLookupDAO.getInstance();

			while( rs.next() ) {

				SearchCrosslinkGenericLookupDTO item = new SearchCrosslinkGenericLookupDTO();

				item.setSearchId( searchId );
				item.setNrseqId1( rs.getInt( "nrseq_id_1" ) );
				item.setNrseqId2( rs.getInt( "nrseq_id_2" ) );
				item.setProtein1Position( rs.getInt( "protein_1_position" ) );
				item.setProtein2Position( rs.getInt( "protein_2_position" ) );

				
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
						.getNumPeptidesPSMsForCrosslink(
								item.getSearchId(),
								searcherCutoffValuesSearchLevel,
								item.getNrseqId1(),
								item.getNrseqId2(),
								item.getProtein1Position(),
								item.getProtein2Position(),
								YRC_NRSEQUtils.getDatabaseIdFromName( searchDTO.getFastaFilename() ) );
				
				
				int numPsmAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
				int numLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();

				int numUniqueLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();

				
				
				item.setNumPsmAtDefaultCutoff( numPsmAtDefaultCutoff );
				item.setNumLinkedPeptidesAtDefaultCutoff( numLinkedPeptidesAtDefaultCutoff );
				item.setNumUniqueLinkedPeptidesAtDefaultCutoff( numUniqueLinkedPeptidesAtDefaultCutoff );
				
				db_Insert_SearchCrosslinkGenericLookupDAO.save( item );
				
//				List<SearchCrosslinkBestPSMValueGenericLookupDTO> insertedBestPSMValueRecords =
				populateCrosslinkBestPSMValue( item, srchPgm_Filterable_Psm_AnnotationType_DTOList );
				
//				List<SearchCrosslinkBestPeptideValueGenericLookupDTO> insertedBestPeptideValueRecords = 
				populateCrosslinkBestPeptideValue( item, srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );

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

	}
	
	
	/**
	 * @param searchCrosslinkGenericLookupDTO
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @throws Exception
	 */
	private List<SearchCrosslinkBestPSMValueGenericLookupDTO> populateCrosslinkBestPSMValue( 
			
			SearchCrosslinkGenericLookupDTO searchCrosslinkGenericLookupDTO,
			
			List<AnnotationTypeDTO>	srchPgm_Filterable_Psm_AnnotationType_DTOList
			
			) throws Exception {
		
		
		List<SearchCrosslinkBestPSMValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOList.size() );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
			GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher.getInstance()
			.getBestAnnotationValue( srchPgmFilterablePsmAnnotationTypeDTO.getId(), 
					searchCrosslinkGenericLookupDTO, 
					srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );
			
			if ( bestFilterableAnnotationValue != null ) {

				SearchCrosslinkBestPSMValueGenericLookupDTO item = new SearchCrosslinkBestPSMValueGenericLookupDTO();

				item.setSearchCrosslinkGenericLookup( searchCrosslinkGenericLookupDTO.getId() );
				
				item.setSearchId( searchCrosslinkGenericLookupDTO.getSearchId() );

				item.setNrseqId1( searchCrosslinkGenericLookupDTO.getNrseqId1() );
				item.setNrseqId2( searchCrosslinkGenericLookupDTO.getNrseqId2() );
				item.setProtein1Position( searchCrosslinkGenericLookupDTO.getProtein1Position() );
				item.setProtein2Position( searchCrosslinkGenericLookupDTO.getProtein2Position() );

				item.setPsmFilterableAnnotationTypeId( srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				double bestPsmValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPsmValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();

				item.setBestPsmValueForAnnTypeId( bestPsmValueForAnnTypeId );
				item.setBestPsmValueStringForAnnTypeId( bestPsmValueStringForAnnTypeId );

				DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO.getInstance().save( item );
				
				results.add( item );
			}
		}
		
		return results;
	}
	

	
	/**
	 * @param searchCrosslinkGenericLookupDTO
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @throws Exception 
	 */
	private List<SearchCrosslinkBestPeptideValueGenericLookupDTO> populateCrosslinkBestPeptideValue( 
			
			SearchCrosslinkGenericLookupDTO searchCrosslinkGenericLookupDTO,
			

			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
			
			) throws Exception {
		
		

		List<SearchCrosslinkBestPeptideValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList.size() );
		
		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {

			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchCrosslinkProteinSearcher.getInstance()
					.getBestAnnotationValue( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId(), 
							searchCrosslinkGenericLookupDTO, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );


			if ( bestFilterableAnnotationValue != null ) {

				SearchCrosslinkBestPeptideValueGenericLookupDTO item = new SearchCrosslinkBestPeptideValueGenericLookupDTO();


				item.setSearchCrosslinkGenericLookup( searchCrosslinkGenericLookupDTO.getId() );
				
				item.setSearchId( searchCrosslinkGenericLookupDTO.getSearchId() );

				item.setNrseqId1( searchCrosslinkGenericLookupDTO.getNrseqId1() );
				item.setNrseqId2( searchCrosslinkGenericLookupDTO.getNrseqId2() );
				item.setProtein1Position( searchCrosslinkGenericLookupDTO.getProtein1Position() );
				item.setProtein2Position( searchCrosslinkGenericLookupDTO.getProtein2Position() );

				item.setPeptideFilterableAnnotationTypeId( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );


				double bestPeptideValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPeptideValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();



				item.setBestPeptideValueForAnnTypeId( bestPeptideValueForAnnTypeId );
				item.setBestPeptideValueStringForAnnTypeId( bestPeptideValueStringForAnnTypeId );



				DB_Insert_SearchCrosslinkBestPeptideValueGenericLookupDAO.getInstance().save( item );

				results.add( item );
			}
		}
		
		return results;
	}
	

}

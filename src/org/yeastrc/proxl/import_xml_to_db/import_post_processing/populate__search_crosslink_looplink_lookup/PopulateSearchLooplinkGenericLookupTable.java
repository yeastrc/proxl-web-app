package org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher;
import org.yeastrc.xlink.dao.SearchLooplinkBestPSMValueGenericLookupDAO;
import org.yeastrc.xlink.dao.SearchLooplinkBestPeptideValueGenericLookupDAO;
import org.yeastrc.xlink.dao.SearchLooplinkGenericLookupDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchLooplinkBestPSMValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchLooplinkBestPeptideValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchLooplinkGenericLookupDTO;
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
public class PopulateSearchLooplinkGenericLookupTable {

	private static final Logger log = Logger.getLogger( PopulateSearchLooplinkGenericLookupTable.class );
	
	/**
	 * private constructor
	 */
	private PopulateSearchLooplinkGenericLookupTable() {  }
	
	public static PopulateSearchLooplinkGenericLookupTable getInstance() { 
		return new PopulateSearchLooplinkGenericLookupTable(); 
	}


	private String PRIMARY_SELECT_SQL = 
			
			"SELECT looplink.nrseq_id, looplink.protein_position_1, looplink.protein_position_2 "
			
			+ " FROM looplink "
			+ " INNER JOIN psm ON looplink.psm_id = psm.id "
			
			+ " WHERE psm.search_id = ? "
			+ " GROUP BY looplink.nrseq_id, looplink.protein_position_1, looplink.protein_position_2 ";



	/**
	 * For the given search id, will populate the search_looplink_lookup table
	 * 
	 * @param searchId
	 * @throws Exception 
	 */
	public void populateSearchLooplinkGenericLookupTable( int searchId ) throws Exception {
		
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

			
			SearchLooplinkGenericLookupDAO searchLooplinkGenericLookupDAO = SearchLooplinkGenericLookupDAO.getInstance();

			while( rs.next() ) {

				SearchLooplinkGenericLookupDTO item = new SearchLooplinkGenericLookupDTO();

				item.setSearchId( searchId );
				item.setNrseqId( rs.getInt( "nrseq_id" ) );
				item.setProteinPosition1( rs.getInt( "protein_position_1" ) );
				item.setProteinPosition2( rs.getInt( "protein_position_2" ) );

				
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
						.getNumPeptidesPSMsForLooplink(
								item.getSearchId(),
								searcherCutoffValuesSearchLevel,
								item.getNrseqId(),
								item.getProteinPosition1(),
								item.getProteinPosition2(),
								YRC_NRSEQUtils.getDatabaseIdFromName( searchDTO.getFastaFilename() ) );
				
				
				int numPsmAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
				int numLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();

				int numUniqueLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();


				
				item.setNumPsmAtDefaultCutoff( numPsmAtDefaultCutoff );
				item.setNumLinkedPeptidesAtDefaultCutoff( numLinkedPeptidesAtDefaultCutoff );
				item.setNumUniqueLinkedPeptidesAtDefaultCutoff( numUniqueLinkedPeptidesAtDefaultCutoff );
				
				searchLooplinkGenericLookupDAO.save( item );
				


//				List<SearchLooplinkBestPSMValueGenericLookupDTO> insertedBestPSMValueRecords =
				populateLooplinkBestPSMValue( item, srchPgm_Filterable_Psm_AnnotationType_DTOList );
				
//				List<SearchLooplinkBestPeptideValueGenericLookupDTO> insertedBestPeptideValueRecords = 
				populateLooplinkBestPeptideValue( item, srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );

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
	 * @param searchLooplinkGenericLookupDTO
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @throws Exception
	 */
	private List<SearchLooplinkBestPSMValueGenericLookupDTO> populateLooplinkBestPSMValue( 
			
			SearchLooplinkGenericLookupDTO searchLooplinkGenericLookupDTO,
			
			List<AnnotationTypeDTO>	srchPgm_Filterable_Psm_AnnotationType_DTOList
			
			) throws Exception {
		
		
		List<SearchLooplinkBestPSMValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOList.size() );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
			GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher.getInstance()
			.getBestAnnotationValue( 
					srchPgmFilterablePsmAnnotationTypeDTO.getId(), 
					searchLooplinkGenericLookupDTO, 
					srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );
			
			if ( bestFilterableAnnotationValue != null ) {

				SearchLooplinkBestPSMValueGenericLookupDTO item = new SearchLooplinkBestPSMValueGenericLookupDTO();

				item.setSearchLooplinkGenericLookup( searchLooplinkGenericLookupDTO.getId() );
				
				item.setSearchId( searchLooplinkGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchLooplinkGenericLookupDTO.getNrseqId() );
				item.setProteinPosition1( searchLooplinkGenericLookupDTO.getProteinPosition1() );
				item.setProteinPosition2( searchLooplinkGenericLookupDTO.getProteinPosition2() );

				item.setPsmFilterableAnnotationTypeId( srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				double bestPsmValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPsmValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();

				item.setBestPsmValueForAnnTypeId( bestPsmValueForAnnTypeId );
				item.setBestPsmValueStringForAnnTypeId( bestPsmValueStringForAnnTypeId );

				SearchLooplinkBestPSMValueGenericLookupDAO.getInstance().save( item );
				
				results.add( item );
			}
		}
		
		return results;
	}
	

	
	/**
	 * @param searchLooplinkGenericLookupDTO
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @throws Exception 
	 */
	private List<SearchLooplinkBestPeptideValueGenericLookupDTO> populateLooplinkBestPeptideValue( 
			
			SearchLooplinkGenericLookupDTO searchLooplinkGenericLookupDTO,
			

			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
			
			) throws Exception {
		
		

		List<SearchLooplinkBestPeptideValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList.size() );
		
		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {


			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchLooplinkProteinSearcher.getInstance()
					.getBestAnnotationValue( 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId(),
							searchLooplinkGenericLookupDTO, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );


			if ( bestFilterableAnnotationValue != null ) {

				SearchLooplinkBestPeptideValueGenericLookupDTO item = new SearchLooplinkBestPeptideValueGenericLookupDTO();


				item.setSearchLooplinkGenericLookup( searchLooplinkGenericLookupDTO.getId() );
				
				item.setSearchId( searchLooplinkGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchLooplinkGenericLookupDTO.getNrseqId() );
				item.setProteinPosition1( searchLooplinkGenericLookupDTO.getProteinPosition1() );
				item.setProteinPosition2( searchLooplinkGenericLookupDTO.getProteinPosition2() );

				item.setPeptideFilterableAnnotationTypeId( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );


				double bestPeptideValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPeptideValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();



				item.setBestPeptideValueForAnnTypeId( bestPeptideValueForAnnTypeId );
				item.setBestPeptideValueStringForAnnTypeId( bestPeptideValueStringForAnnTypeId );



				SearchLooplinkBestPeptideValueGenericLookupDAO.getInstance().save( item );

				results.add( item );
			}
		}
		
		return results;
	}
	

}
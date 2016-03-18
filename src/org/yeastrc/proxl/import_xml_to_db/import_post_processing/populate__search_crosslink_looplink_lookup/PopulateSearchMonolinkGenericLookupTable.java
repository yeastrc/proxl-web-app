package org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchMonolinkBestPSMValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchMonolinkBestPeptideValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchMonolinkGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchMonolinkProteinSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchMonolinkProteinSearcher;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchMonolinkBestPSMValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchMonolinkBestPeptideValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchMonolinkGenericLookupDTO;
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
public class PopulateSearchMonolinkGenericLookupTable {

	private static final Logger log = Logger.getLogger( PopulateSearchMonolinkGenericLookupTable.class );
	
	/**
	 * private constructor
	 */
	private PopulateSearchMonolinkGenericLookupTable() {  }
	
	public static PopulateSearchMonolinkGenericLookupTable getInstance() { 
		return new PopulateSearchMonolinkGenericLookupTable(); 
	}


	private String PRIMARY_SELECT_SQL = 
			
			"SELECT monolink.nrseq_id, monolink.protein_position "
			
			+ " FROM monolink "
			+ " INNER JOIN psm ON monolink.psm_id = psm.id "
			
			+ " WHERE psm.search_id = ? "
			+ " GROUP BY monolink.nrseq_id, monolink.protein_position ";



	/**
	 * For the given search id, will populate the search_monolink_lookup table
	 * 
	 * @param searchId
	 * @throws Exception 
	 */
	public void populateSearchMonolinkGenericLookupTable( int searchId ) throws Exception {
		

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

			
			DB_Insert_SearchMonolinkGenericLookupDAO db_Insert_SearchMonolinkGenericLookupDAO = DB_Insert_SearchMonolinkGenericLookupDAO.getInstance();

			while( rs.next() ) {

				SearchMonolinkGenericLookupDTO item = new SearchMonolinkGenericLookupDTO();

				item.setSearchId( searchId );
				item.setNrseqId( rs.getInt( "nrseq_id" ) );
				item.setProteinPosition( rs.getInt( "protein_position" ) );

				
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
						.getNumPeptidesPSMsForMonolink(
								item.getSearchId(),
								searcherCutoffValuesSearchLevel,
								item.getNrseqId(),
								item.getProteinPosition(),
								YRC_NRSEQUtils.getDatabaseIdFromName( searchDTO.getFastaFilename() ) );
				
				
				int numPsmAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
				int numLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();

				int numUniqueLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();

				
				item.setNumPsmAtDefaultCutoff( numPsmAtDefaultCutoff );
				item.setNumLinkedPeptidesAtDefaultCutoff( numLinkedPeptidesAtDefaultCutoff );
				item.setNumUniqueLinkedPeptidesAtDefaultCutoff( numUniqueLinkedPeptidesAtDefaultCutoff );
				
				db_Insert_SearchMonolinkGenericLookupDAO.save( item );

//				List<SearchMonolinkBestPSMValueGenericLookupDTO> insertedBestPSMValueRecords =
				populateMonolinkBestPSMValue( item, srchPgm_Filterable_Psm_AnnotationType_DTOList );
				
//				List<SearchMonolinkBestPeptideValueGenericLookupDTO> insertedBestPeptideValueRecords = 
				populateMonolinkBestPeptideValue( item, srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );


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
	 * @param searchMonolinkGenericLookupDTO
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @throws Exception
	 */
	private List<SearchMonolinkBestPSMValueGenericLookupDTO> populateMonolinkBestPSMValue( 
			
			SearchMonolinkGenericLookupDTO searchMonolinkGenericLookupDTO,
			
			List<AnnotationTypeDTO>	srchPgm_Filterable_Psm_AnnotationType_DTOList
			
			) throws Exception {
		
		
		List<SearchMonolinkBestPSMValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOList.size() );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
			GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchMonolinkProteinSearcher.getInstance()
			.getBestAnnotationValue( 
					srchPgmFilterablePsmAnnotationTypeDTO.getId(), 
					searchMonolinkGenericLookupDTO, 
					srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );
			
			if ( bestFilterableAnnotationValue != null ) {

				SearchMonolinkBestPSMValueGenericLookupDTO item = new SearchMonolinkBestPSMValueGenericLookupDTO();

				item.setSearchMonolinkGenericLookup( searchMonolinkGenericLookupDTO.getId() );
				
				item.setSearchId( searchMonolinkGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchMonolinkGenericLookupDTO.getNrseqId() );
				item.setProteinPosition( searchMonolinkGenericLookupDTO.getProteinPosition() );

				item.setPsmFilterableAnnotationTypeId( srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				double bestPsmValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPsmValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();

				item.setBestPsmValueForAnnTypeId( bestPsmValueForAnnTypeId );
				item.setBestPsmValueStringForAnnTypeId( bestPsmValueStringForAnnTypeId );

				DB_Insert_SearchMonolinkBestPSMValueGenericLookupDAO.getInstance().save( item );
				
				results.add( item );
			}
		}
		
		return results;
	}
	

	
	/**
	 * @param searchMonolinkGenericLookupDTO
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @throws Exception 
	 */
	private List<SearchMonolinkBestPeptideValueGenericLookupDTO> populateMonolinkBestPeptideValue( 
			
			SearchMonolinkGenericLookupDTO searchMonolinkGenericLookupDTO,
			

			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
			
			) throws Exception {
		
		

		List<SearchMonolinkBestPeptideValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList.size() );
		
		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {

			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchMonolinkProteinSearcher.getInstance()
					.getBestAnnotationValue( 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId(), 
							searchMonolinkGenericLookupDTO, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );


			if ( bestFilterableAnnotationValue != null ) {

				SearchMonolinkBestPeptideValueGenericLookupDTO item = new SearchMonolinkBestPeptideValueGenericLookupDTO();

				item.setSearchMonolinkGenericLookup( searchMonolinkGenericLookupDTO.getId() );

				item.setSearchId( searchMonolinkGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchMonolinkGenericLookupDTO.getNrseqId() );
				item.setProteinPosition( searchMonolinkGenericLookupDTO.getProteinPosition() );

				item.setPeptideFilterableAnnotationTypeId( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );


				double bestPeptideValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPeptideValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();



				item.setBestPeptideValueForAnnTypeId( bestPeptideValueForAnnTypeId );
				item.setBestPeptideValueStringForAnnTypeId( bestPeptideValueStringForAnnTypeId );



				DB_Insert_SearchMonolinkBestPeptideValueGenericLookupDAO.getInstance().save( item );

				results.add( item );
			}
		}
		
		return results;
	}
	

}

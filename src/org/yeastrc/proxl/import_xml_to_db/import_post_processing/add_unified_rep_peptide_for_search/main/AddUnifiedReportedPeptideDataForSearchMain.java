package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.populate_per_annotation.PopUnifRepPepLvlFltrblAnnBySrchReptPept;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.populate_per_annotation.PopUnfRpPptLvPsmFltAnSmBSrcRpPpt;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.dao.SearchDynamicModMassDAO;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetHasDynamicModificationsForPsmIdSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetHasMonolinksForPsmIdSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideRecordsSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.PsmPeptideSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.main.InsertIfNotInDBUnifiedReportedPeptideAndChildren;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.objects.UnifiedReportedPeptideObj;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.objects.UnifiedRpSinglePeptideDynamicMod;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.objects.UnifiedRpSinglePeptideObj;
import org.yeastrc.xlink.dao.CrosslinkDAO;
import org.yeastrc.xlink.dao.DynamicModDAO;
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dao.MatchedPeptideDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dao.SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.DynamicModDTO;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searchers.AnnotationTypesForSearchIdPSMPeptideTypeSearcher;
import org.yeastrc.xlink.searchers.PeptideUniqueSearcher;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

/**
 * Add Unified Reported Peptide Data for the passed in search ID
 *
 * Also populates the table search__reported_peptide__dynamic_mod_lookup
 */
public class AddUnifiedReportedPeptideDataForSearchMain {


	private static final Logger log = Logger.getLogger(AddUnifiedReportedPeptideDataForSearchMain.class);

	
	/**
	 * Singleton
	 */
	private static AddUnifiedReportedPeptideDataForSearchMain _instance = new AddUnifiedReportedPeptideDataForSearchMain();
	
	// private constructor
	private AddUnifiedReportedPeptideDataForSearchMain() { }
	
	public static AddUnifiedReportedPeptideDataForSearchMain getInstance() { 
		return _instance; 
	}
	
	

	
	
	private static final int REPORTED_PEPTIDE_ID_BATCH_SIZE = 50;
	
	
	/**
	 * This is primarily provided to assist with writing the conversion to generic
	 */
	private IGetUnifiedReportedPeptideFromSearchIdReportedPeptideId getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject;
	
	
	/**
	 * This is primarily provided to assist with writing the conversion to generic
	 * 
	 * @param getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject
	 */
	public void setConversionOnlyObjects(
			IGetUnifiedReportedPeptideFromSearchIdReportedPeptideId getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject) {
		this.getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject = getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject;
	}

	
	
	/**
	 * Add UnifiedReportedPeptide records and children for searchId
	 * 
	 * Also populates the table search__reported_peptide__dynamic_mod_lookup
	 * 
	 * @param searchId
	 * @throws Exception
	 */
	public void addUnifiedReportedPeptideDataForSearch( int searchId ) throws Exception {

		log.info( "Starting inserting unified reported peptide data" );
		
		
		List<AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOList =
				AnnotationTypesForSearchIdPSMPeptideTypeSearcher.getInstance().get_Peptide_Filterable_ForSearchId( searchId );
		
		List<AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOList = 
				AnnotationTypesForSearchIdPSMPeptideTypeSearcher.getInstance().get_PSM_Filterable_ForSearchId( searchId );
		
		

		Set<Double> uniqueDynamicModMasses = new HashSet<>();
		
		int searchReportedPeptideOffset = 0;
		
		while ( true ) {
			
			boolean foundReportedPeptideRecordsToProcess =
					add_to_new__unified__table_ForReportedPeptideIdBlock( 
							
							searchId, 
							searchReportedPeptideOffset, 
							REPORTED_PEPTIDE_ID_BATCH_SIZE, 
							uniqueDynamicModMasses, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTOList, 
							srchPgmFilterablePsmAnnotationTypeDTOList, 
							null /* startReportedPeptideId */, 
							null /* endReportedPeptideId */ );
				
			if ( ! foundReportedPeptideRecordsToProcess ) {
				
				//  No Reported Peptides found for current offset so exit loop 
				//     since all Reported Peptides for this Search Id have been processed
				
				break;  // Exit Loop
			}
			
			searchReportedPeptideOffset += REPORTED_PEPTIDE_ID_BATCH_SIZE;

			//  assume batch size is some multiple of the modulo value
			if (  searchReportedPeptideOffset % 10000 == 0 ) {
				
				log.info( "Processed " + searchReportedPeptideOffset + " searc_reported_peptide records for  unified reported peptide data" );
			}
		}
		
		log.info( "Done inserting unified reported peptide data" );
		
		log.info( "Starting inserting search dynamic mod masses" );

		for ( Double dynamicModMass : uniqueDynamicModMasses ) {

			SearchDynamicModMassDAO.getInstance().saveSearchDynamicModMass( searchId, dynamicModMass );
		}
		
		log.info( "Done inserting search dynamic mod masses" );

	}
	
	
	
	
	/**
	 * Add UnifiedReportedPeptide records and children for searchId and other params.
	 * 
	 * Also populates the table search__reported_peptide__dynamic_mod_lookup
	 * 
	 * !!  If only have searchId, call method addUnifiedReportedPeptideDataForSearch( int searchId ) !!
	 * 
	 * This method is Public to allow calling for conversion 
	 * 
	 * @param searchId
	 * @param offset
	 * @param limit
	 * @param uniqueDynamicModMasses - updated with Dynamic Mod Masses processed  
	 * @param startReportedPeptideId	// optional, null if no value
	 * @param endReportedPeptideId		// optional, null if no value
	 * @return
	 * @throws Exception
	 */
	public boolean add_to_new__unified__table_ForReportedPeptideIdBlock( 
			
			int searchId, 
			int offset, 
			int limit, 
			
			Set<Double> uniqueDynamicModMasses, //  updated in this method
			
			List<AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOList,
			
			List<AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOList,
			
			Integer startReportedPeptideId,	// optional, null if no value 
			Integer endReportedPeptideId 	// optional, null if no value
			) throws Exception {
		
		SearchDTO search = SearchDAO.getInstance().getSearch(searchId);

		if ( search == null ) {

			throw new Exception("No search record for searchId: " + searchId );
		}

		int fastaFileDatabaseId = 
				YRC_NRSEQUtils.getDatabaseIdFromName( search.getFastaFilename() );

		
		List<SearchReportedPeptideDTO> searchReportedPeptideList = 
				GetReportedPeptideRecordsSearcher.getInstance()
				.getSearchReportedPeptideDTOListFromSearchIdReportedPeptideStartAndEndIds( searchId, offset, limit, startReportedPeptideId, endReportedPeptideId );


		if ( searchReportedPeptideList == null || searchReportedPeptideList.isEmpty() ) {
			
			return false; //  EARLY EXIT:  No records found so exit and return false
		}
		

		if ( log.isDebugEnabled() ) {

			log.debug( "Processing Reported Peptide Id block:  first reported peptide id: " + searchReportedPeptideList.get(0).getReportedPeptideId()
					+ ", last reported peptide id: " + searchReportedPeptideList.get( searchReportedPeptideList.size() - 1 ).getReportedPeptideId() );
		}
		
		
		InsertIfNotInDBUnifiedReportedPeptideAndChildren insertIfNotInDBUnifiedReportedPeptideAndChildren
			= InsertIfNotInDBUnifiedReportedPeptideAndChildren.getInstance();
		
		for ( SearchReportedPeptideDTO searchReportedPeptide : searchReportedPeptideList ) {
			
			int reportedPeptideId = searchReportedPeptide.getReportedPeptideId();
			
			ReportedPeptideDTO reportedPeptideDTO = ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reportedPeptideId );

			try {

				PsmDTO psmDTO =
						PsmDAO.getInstance().getOnePsmDTOForSearchIdAndReportedPeptideId( reportedPeptideId, searchId );

				if ( psmDTO == null ) {

					String msg = "No PSM record for searchId: " + searchId + ", reportedPeptideId: " + reportedPeptideId;
					log.error( msg );
					throw new Exception( msg );
				}

				int psmId = psmDTO.getId();
				
				int samplePsmId = psmId;


				int psmNumAtDefaultCutoff = 
						GetPsmCountForAllAnnTypeIdsSearchReptPeptideDefaultCutoffSearcher.getInstance()
						.getPsmCountForAllDefaultFilterValues( srchPgmFilterablePsmAnnotationTypeDTOList, searchId, reportedPeptideId );
				
				
				//  Determine if this peptide meets the default peptide cutoffs (will be stored in DB)
				
				Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs = 
						DoesReptPeptideForThisSearchMeetDefaultCutoffsSearcher.getInstance()
						.doesReptPeptideForThisSearchMeetDefaultCutoffs( 
								srchPgmFilterableReportedPeptideAnnotationTypeDTOList, 
								searchId,
								reportedPeptideDTO.getId() );
				
				
				boolean hasDynamicModifications = GetHasDynamicModificationsForPsmIdSearcher.getInstance().getHasDynamicModificationsForPsmId( psmId );
				boolean hasMonolinks = GetHasMonolinksForPsmIdSearcher.getInstance().getHasMonolinksForPsmId( psmId );


				List<Integer> peptideIdsForPsmId =
						PsmPeptideSearcher.getInstance().getPeptideIdsFromPsmId( samplePsmId );
				
				if ( peptideIdsForPsmId.isEmpty() ) {

					throw new Exception( "No psm_peptide found for searchId: " + searchId
							+ ", reportedPeptideId: " + reportedPeptideId );
				}
				
				boolean allRelatedPeptidesUniqueForSearch = true;
				
				for ( Integer peptideId : peptideIdsForPsmId ) {

					if ( ! PeptideUniqueSearcher.getInstance().isPeptideUniqueForDatabaseId( peptideId, fastaFileDatabaseId ) ) {
					
						allRelatedPeptidesUniqueForSearch = false;
					}
				}
				

				UnifiedReportedPeptideObj unifiedReportedPeptideObj = new UnifiedReportedPeptideObj();

				UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = null;

				if ( getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject != null ) {
					
					//  For Conversion Only
					
					unifiedReportedPeptideDTO = 
							getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject
							.getUnifiedReportedPeptideFromSearchIdReportedPeptideId( searchId, reportedPeptideId );
					
					if ( unifiedReportedPeptideDTO == null ) {
						
						String msg =  "ERROR: getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject.getUnifiedReportedPeptideFromSearchIdReportedPeptideId( searchId, reportedPeptideId ) returned null for search id:  "
								+ searchId + ", reportedPeptideId: " + reportedPeptideId
								+ ".";
						
						log.error( msg );
						
						throw new ProxlImporterDataException(msg);
						
					}
					
					
				} else {
					

					unifiedReportedPeptideObj.setLinkType( psmDTO.getType() );

					List<UnifiedRpSinglePeptideObj> singlePeptides = get_singlePeptides( psmDTO );
					unifiedReportedPeptideObj.setSinglePeptides( singlePeptides );

					//  Commit any uncommitted inserts to the database first.
					//  Required since locking tables this call to insert unified reported peptide and children
					
					ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
					
					
					//  Standard processing.  Save the unifiedReportedPeptideObj and children
				
					unifiedReportedPeptideDTO = 
							insertIfNotInDBUnifiedReportedPeptideAndChildren
							.insertIfNotInDBUnifiedReportedPeptideAndChildren( unifiedReportedPeptideObj );
				
				}
				
				
				
				
				UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO = new UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO();

				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setSearchId( searchId );
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setUnifiedReportedPeptideId( unifiedReportedPeptideDTO.getId() );
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setReportedPeptideId( reportedPeptideId );
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setLinkType( psmDTO.getType() );
				
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setHasDynamicModifications( hasDynamicModifications );
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setHasMonolinks( hasMonolinks );
				
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setAllRelatedPeptidesUniqueForSearch( allRelatedPeptidesUniqueForSearch );
								
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setPsmNumAtDefaultCutoff( psmNumAtDefaultCutoff );
				
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setPeptideMeetsDefaultCutoffs( peptideMeetsDefaultCutoffs );
				
				
				unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.setSamplePsmId( samplePsmId );

				
				DB_Insert_UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO.getInstance().saveToDatabase( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO );

				//  Populate Unified Reported Peptide Annotations Lookup table
				
				PopUnifRepPepLvlFltrblAnnBySrchReptPept.getInstance()
				.insertAnnotationSpecificRecordsForSearchIdReportedPeptideId( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO, srchPgmFilterableReportedPeptideAnnotationTypeDTOList );
				
				
				//  Populate PSM roll ups table for PSM Filterable Annotations for this Search / Reported Peptide
				
				PopUnfRpPptLvPsmFltAnSmBSrcRpPpt.getInstance()
				.insertAnnotationSpecificRecordsForSearchIdReportedPeptideId( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO, srchPgmFilterablePsmAnnotationTypeDTOList );
				
				
				if ( getUnifiedReportedPeptideFromSearchIdReportedPeptideIdObject == null ) {

					//  Do only if not conversion
					
					saveToSearchReportedPeptideDynamicModMasses( unifiedReportedPeptideObj, unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO, uniqueDynamicModMasses );
				}
				
				
				
				
				
				if ( log.isDebugEnabled() ) {

					log.info( "Processed One Reported Peptide / Search Pair.  searchId: " + searchId
							+ ", reported peptide id: " + reportedPeptideDTO.getId()
							+ ", reported peptide: '" + reportedPeptideDTO.getSequence() + "'"
							+ ", Unified Peptide: '" + unifiedReportedPeptideDTO.getUnifiedSequence() + "'"
							+ ", Unified Peptide id: '" + unifiedReportedPeptideDTO.getId() + "'"
							+ " ");
				}

			} catch ( Exception e ) {


				String msg = "Exception processing searchId: " + searchId
						+ ", reported peptide id: " + reportedPeptideDTO.getId()
						+ ", reported peptide: '" + reportedPeptideDTO.getSequence() + "'"
						+ ".";

				log.error( msg, e );
				throw new Exception( msg, e );
			}

			
		}
		
		
		return true; // found records to process
		
	}
	
	
	/**
	 * @param unifiedReportedPeptideObj
	 * @param unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO
	 * @param uniqueDynamicModMasses
	 * @throws Exception
	 */
	private void saveToSearchReportedPeptideDynamicModMasses( 
			UnifiedReportedPeptideObj unifiedReportedPeptideObj, 
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO,
			Set<Double> uniqueDynamicModMasses ) throws Exception {
		
		//  First get set of unique dynamic mods
		
		Set<Double> dynamicModMasses = new HashSet<>();
		
		for ( UnifiedRpSinglePeptideObj unifiedRpSinglePeptideObj : unifiedReportedPeptideObj.getSinglePeptides() ) {
			
			for ( UnifiedRpSinglePeptideDynamicMod unifiedRpSinglePeptideDynamicMod : unifiedRpSinglePeptideObj.getDynamicModList() ) {
				
				dynamicModMasses.add( unifiedRpSinglePeptideDynamicMod.getMass() );
			}
		}

		SearchReportedPeptideDynamicModLookupDTO item = new SearchReportedPeptideDynamicModLookupDTO();
		
		item.setSearchId( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getSearchId() );
		item.setReportedPeptideId( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getReportedPeptideId() );
		item.setLinkType( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getLinkType() );
		
//		item.setBestPsmQValue( bestPsmQValue );

		for ( double dynamicModMass : dynamicModMasses ) {
		
			item.setDynamicModMass( dynamicModMass );
			SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			
			uniqueDynamicModMasses.add( dynamicModMass );
		}		
	}
	
	
	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception 
	 */
	private List<UnifiedRpSinglePeptideObj> get_singlePeptides( PsmDTO psmDTO ) throws Exception {
		
		List<UnifiedRpSinglePeptideObj> singlePeptides = new ArrayList<>();

		int linkType = psmDTO.getType();
		
		int psmId = psmDTO.getId();
		
		
		if ( linkType == XLinkUtils.TYPE_CROSSLINK ) {
			
			CrosslinkDTO crosslinkDTO = CrosslinkDAO.getInstance().getARandomCrosslinkDTOForPsmId( psmId );
			
			if ( crosslinkDTO == null ) {

				String msg = "ERROR: crosslinkDTO not found for psmId: " + psmId;
				log.error( msg );
				throw new Exception(msg);
			}
			
			UnifiedRpSinglePeptideObj singlePeptide = null;
			MatchedPeptideDTO matchedPeptide = null;
			
			//  Peptide 1
			
			int[] linkPositions1 = { crosslinkDTO.getPeptide1Position() };
			matchedPeptide = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForId( crosslinkDTO.getPeptide1MatchedPeptideId() );
			singlePeptide = getSinglePeptide( matchedPeptide, linkPositions1 );
			singlePeptides.add( singlePeptide );

			//  Peptide 2

			int[] linkPositions2 = { crosslinkDTO.getPeptide2Position() };
			matchedPeptide = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForId( crosslinkDTO.getPeptide2MatchedPeptideId() );
			singlePeptide = getSinglePeptide( matchedPeptide, linkPositions2 );
			singlePeptides.add( singlePeptide );
			
			
		} else if ( linkType == XLinkUtils.TYPE_LOOPLINK ) {
			

			LooplinkDTO looplinkDTO = LooplinkDAO.getInstance().getARandomLooplinkDTOForPsmId( psmId );
			
			if ( looplinkDTO == null ) {

				String msg = "ERROR: looplinkDTO not found for psmId: " + psmId;
				log.error( msg );
				throw new Exception(msg);
			}
			

			UnifiedRpSinglePeptideObj singlePeptide = null;

			List<MatchedPeptideDTO> matchedPeptideList = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId( psmId );
			
			if ( matchedPeptideList.size() != 1 ) {
				
				String msg = "ERROR: matchedPeptideList.size must be 1 for looplink.  psmId: " + psmId;
				log.error( msg );
				throw new Exception(msg);
			}
			
			MatchedPeptideDTO matchedPeptide = matchedPeptideList.get( 0 );
			
			int[] linkPositions = { looplinkDTO.getPeptidePosition1(), looplinkDTO.getPeptidePosition2() };
			singlePeptide = getSinglePeptide( matchedPeptide, linkPositions );
			singlePeptides.add( singlePeptide );

			
		} else if ( linkType == XLinkUtils.TYPE_DIMER 
				|| linkType == XLinkUtils.TYPE_UNLINKED ) {
			
			UnifiedRpSinglePeptideObj singlePeptide = null;

			List<MatchedPeptideDTO> matchedPeptideList = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId( psmId );
			
			if ( matchedPeptideList.isEmpty() ) {
				
				String msg = "ERROR: matchedPeptideList is empty.  psmId: " + psmId;
				log.error( msg );
				throw new Exception(msg);
			}
			
			for ( MatchedPeptideDTO matchedPeptide : matchedPeptideList ) {

				singlePeptide = getSinglePeptide( matchedPeptide, null /* linkPositions */ );
				singlePeptides.add( singlePeptide );			
			}
			
		} else {

			String msg = "ERROR: unknown link type: " + linkType;
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		
		
		
		return singlePeptides;
	}
	
	
	/**
	 * @param matchedPeptideId
	 * @param linkPositions
	 * @return
	 * @throws Exception
	 */
	private UnifiedRpSinglePeptideObj getSinglePeptide( MatchedPeptideDTO matchedPeptide, int[] linkPositions ) throws Exception {

		
		PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( matchedPeptide.getPeptide_id() );
		List<DynamicModDTO> dynamMods = DynamicModDAO.getInstance().getDynamicModDTOForMatchedPeptideId( matchedPeptide.getId() ); 
		
		
		UnifiedRpSinglePeptideObj singlePeptide = new UnifiedRpSinglePeptideObj();
		singlePeptide.setLinkPositions( linkPositions );
		singlePeptide.setPeptideDTO( peptideDTO );
		List<UnifiedRpSinglePeptideDynamicMod> unifiedRpSinglePeptideDynamicModList = new ArrayList<>( dynamMods.size() );
		singlePeptide.setDynamicModList(unifiedRpSinglePeptideDynamicModList);
		
		for ( DynamicModDTO dynamMod : dynamMods ) {
			
			UnifiedRpSinglePeptideDynamicMod unifiedRpSinglePeptideDynamicMod = new UnifiedRpSinglePeptideDynamicMod();
			unifiedRpSinglePeptideDynamicModList.add(unifiedRpSinglePeptideDynamicMod);
			
			unifiedRpSinglePeptideDynamicMod.setPosition( dynamMod.getPosition() );
			unifiedRpSinglePeptideDynamicMod.setMass( dynamMod.getMass() );
		}
		
		return singlePeptide;
	}
	
}

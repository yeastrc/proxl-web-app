package org.yeastrc.xlink.www.qc_data.digestion_statistics_merged.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.cleavage_missed.GetMissedCleavageSites;
import org.yeastrc.xlink.base.cleavage_missed.GetMissedCleavageSites.GetMissedCleavageSitesResult;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.digestion_statistics_merged.objects.QC_MissingCleavageReportedPeptidesCount_Merged_Results;
import org.yeastrc.xlink.www.qc_data.digestion_statistics_merged.objects.QC_MissingCleavageReportedPeptidesCount_Merged_Results.QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged;
import org.yeastrc.xlink.www.qc_data.digestion_statistics_merged.objects.QC_MissingCleavageReportedPeptidesCount_Merged_Results.QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged;
import org.yeastrc.xlink.www.qc_data.utils.QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;


/**
 * 
 *
 */
public class QC_MissingCleavageReportedPeptidesCount_Merged {

	private static final Logger log = LoggerFactory.getLogger( QC_MissingCleavageReportedPeptidesCount_Merged.class);

	/**
	 * private constructor
	 */
	private QC_MissingCleavageReportedPeptidesCount_Merged(){}
	public static QC_MissingCleavageReportedPeptidesCount_Merged getInstance( ) throws Exception {
		QC_MissingCleavageReportedPeptidesCount_Merged instance = new QC_MissingCleavageReportedPeptidesCount_Merged();
		return instance;
	}
	
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public QC_MissingCleavageReportedPeptidesCount_Merged_Results getQC_MissingCleavageReportedPeptidesCount_Merged( 
			//  One and only 1 of requestJSONBytes and requestJSONString can be not null
			byte[] requestJSONBytes,  //  Contents of POST to webservice.  Only used here for caching
			String requestJSONString,  //  Contents of JSON field in POST to download.  Only used here for caching
			ForDownload_Enum forDownload,
			QCPageQueryJSONRoot qcPageQueryJSONRoot, 
			List<SearchDTO> searches ) throws Exception {

		if ( requestJSONBytes == null && requestJSONString == null ) {
			throw new IllegalArgumentException( "requestJSONBytes == null && requestJSONString == null" );
		}

		if ( requestJSONBytes != null && requestJSONString != null ) {
			throw new IllegalArgumentException( "requestJSONBytes != null && requestJSONString != null" );
		}

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( qcPageQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = qcPageQueryJSONRoot.getMods();
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = qcPageQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( qcPageQueryJSONRoot.getLinkTypes() == null || qcPageQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		}
		

		Map<Integer, PerSearchIdTempData> perSearchIdTempData_BySearchId = new HashMap<>();
		
		List<String> linkTypesList = new ArrayList<String>( qcPageQueryJSONRoot.getLinkTypes().length );

		for ( String linkTypeFromWeb : qcPageQueryJSONRoot.getLinkTypes() ) {
			String linkType = null;
			if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.CROSS_TYPE_STRING;
			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.LOOP_TYPE_STRING;
			} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;
			} else {
				String msg = "linkType is invalid, linkTypeFromWeb: " + linkTypeFromWeb;
				log.error( msg );
				throw new Exception( msg );
			}
			linkTypesList.add( linkType );
		}
		
		boolean foundData = false;
		
		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();
		
		for ( SearchDTO searchDTO : searches ) {
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
			
			//  Get cutoffs for this project search id
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			//  Get static mods for search id
			List<StaticModDTO> staticModDTOList = StaticModDAO.getInstance().getStaticModDTOForSearchId( searchId );
			
			///////////////////////////////////////////////
			//  Get peptides for this search from the DATABASE

			//  Change to use QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds 
			//     to get list filtered on 

			List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
					QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds.getInstance()
					.get_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds(
							searchDTO, searcherCutoffValuesSearchLevel, 
							linkTypesForDBQuery,
							modsForDBQuery, 
							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO,
							qcPageQueryJSONRoot.getIncludeProteinSeqVIdsDecodedArray() );
			
//			List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
//					PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
//							searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
//							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

			if ( ! wrappedLinksPerForSearch.isEmpty() ) {
				foundData = true;
			}

			Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType = new HashMap<>();
			
			for ( String linkType : linkTypesList ) {
				PerLinkTypeTempData perLinkTypeTempData = new PerLinkTypeTempData();
//				perLinkTypeTempData.linkType = linkType;
				perLinkTypeTempData_ByLinkType.put( linkType, perLinkTypeTempData );
			}
			
			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
				
				String linkType = null;
				boolean missedCleavagesFound = false;
				
				//  srchRepPeptPeptideDTOList: associated SrchRepPeptPeptideDTO for the link, one per associated peptide, populated per link type
				
				//  copied from SearchPeptideCrosslink, this way not load PeptideDTO in SearchPeptideCrosslink
				
				SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
				srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( searchId );
				srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( reportedPeptideId );
				SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
						Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
						.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
				List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();
				
				if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
					//  Process a crosslink
					linkType = XLinkUtils.CROSS_TYPE_STRING;
				} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
					//  Process a looplink
					linkType = XLinkUtils.LOOP_TYPE_STRING;
				} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
					//  Process a unlinked
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;
				} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
					//  Process a dimer
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;  //  Lump in with unlinked reported peptides
				} else {
					String msg = 
							"Link type unkown"
							+ " for reportedPeptideId: " + reportedPeptideId
							+ ", searchId: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}

				// get object from map for link type
				PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
				if ( perLinkTypeTempData == null ) {
					String msg = "In updating for link type, link type not found: " + linkType;
					log.error( msg );
					throw new Exception(msg);
				}
				
				//  process srchRepPeptPeptideDTOList (Each peptide mapped to the reported peptide)
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					// get PeptideDTO, caching locally in peptideDTO_MappedById
					PeptideDTO peptide = peptideDTO_MappedById.get( srchRepPeptPeptideDTO.getPeptideId() );
					if ( peptide == null ) {
						peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( srchRepPeptPeptideDTO.getPeptideId() );
						//  To directly retrieve from DB:  PeptideDAO.getInstance().getPeptideDTOFromDatabaseActual( id )
						if ( peptide == null ) {
							String msg = 
									"PeptideDTO not found in DB for id: " + srchRepPeptPeptideDTO.getPeptideId()
									+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", for reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						peptideDTO_MappedById.put( srchRepPeptPeptideDTO.getPeptideId(), peptide );
					}
					
					//  Get positions of links since linked positions are not missed cleavage sites
					Set<Integer> positionsOfLinks = new HashSet<>();
					if ( srchRepPeptPeptideDTO.getPeptidePosition_1() != null
							&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						positionsOfLinks.add( srchRepPeptPeptideDTO.getPeptidePosition_1() );
					}
					if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
							&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						positionsOfLinks.add( srchRepPeptPeptideDTO.getPeptidePosition_2() );
					}
					
					// Add Dynamic Mods to positionsOfLinks
					Set<Integer> dynamicModsPeptidePositions = 
							getDynamicModsPeptidePositionsForSrchRepPeptPeptide( srchRepPeptPeptideDTO.getId() );
					positionsOfLinks.addAll( dynamicModsPeptidePositions );

					// Add Static Mods to positionsOfLinks
					Set<Integer> staticModsPeptidePositions = 
							getStaticModsPeptidePositionsForPeptideSequenceStaticModList( peptide.getSequence(), staticModDTOList );
					if ( staticModsPeptidePositions != null ) {
						positionsOfLinks.addAll( staticModsPeptidePositions );
					}
					
					//  Get list of missed cleavage sites
					GetMissedCleavageSitesResult getMissedCleavageSitesResult =
							GetMissedCleavageSites.getInstance()
							.getMissedTrypsinCleavageSites( peptide.getSequence(), positionsOfLinks );

//					List<Integer> cleavageSiteList = getMissedCleavageSitesResult.getCleavageSiteList(); // Total Cleavage sites, not used
					List<Integer> missedCleavageSiteList = getMissedCleavageSitesResult.getMissedCleavageSiteList();

					int missedCleavageCount = missedCleavageSiteList.size();
					perLinkTypeTempData.addToMissedCleavageCount( missedCleavageCount );
					
					if ( ! missedCleavageSiteList.isEmpty() ) {
						missedCleavagesFound = true;  // have missed cleavage sites for this peptide and thus this reported peptide
					}
				}
				
				//  Per Reported Peptide and PSM Count tracking
				perLinkTypeTempData.incrementTotalReportedPeptideCount();
				int numPSMsForReportedPeptide = webReportedPeptide.getNumPsms();
				perLinkTypeTempData.addToTotalPSMCount( numPSMsForReportedPeptide );
				if ( missedCleavagesFound ) {
					//  At least 1 missed cleavage site found for this reported peptide
					perLinkTypeTempData.incrementMissedCleavageReportedPeptideCount();
					perLinkTypeTempData.addToMissedCleavagePSMCount( numPSMsForReportedPeptide );
				}
			}

			PerSearchIdTempData perSearchIdTempData = new PerSearchIdTempData();
			perSearchIdTempData.perLinkTypeTempData_ByLinkType = perLinkTypeTempData_ByLinkType;
			
			perSearchIdTempData_BySearchId.put(searchId, perSearchIdTempData );
		}

		List<String> linkTypesDisplayOrderList = new ArrayList<String>( linkTypesList.size() );
		
		if ( linkTypesList.contains( XLinkUtils.CROSS_TYPE_STRING ) ) {
			linkTypesDisplayOrderList.add( XLinkUtils.CROSS_TYPE_STRING );
		}
		if ( linkTypesList.contains( XLinkUtils.LOOP_TYPE_STRING ) ) {
			linkTypesDisplayOrderList.add( XLinkUtils.LOOP_TYPE_STRING );
		}
		if ( linkTypesList.contains( XLinkUtils.UNLINKED_TYPE_STRING ) ) {
			linkTypesDisplayOrderList.add( XLinkUtils.UNLINKED_TYPE_STRING );
		}
		
		QC_MissingCleavageReportedPeptidesCount_Merged_Results result = 
				createResult( searches, perSearchIdTempData_BySearchId, linkTypesDisplayOrderList );
		
		result.setSearchIds( searchIdsListDeduppedSorted );
		result.setFoundData( foundData );
		
		return result;
	}
	

	/**
	 * @param searches
	 * @param perSearchIdTempData_BySearchId
	 * @param linkTypesDisplayOrderList
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private QC_MissingCleavageReportedPeptidesCount_Merged_Results createResult( 
			List<SearchDTO> searches, 
			Map<Integer, PerSearchIdTempData> perSearchIdTempData_BySearchId,
			List<String> linkTypesDisplayOrderList ) throws ProxlWebappInternalErrorException {
		
		List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> peptideCountPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() + 2 );
		List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> perPeptideCountPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() + 2 );
		List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> psmCountPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() + 2 );

		// method also adds combined entry at end
		populateCountPerLinkTypeList( linkTypesDisplayOrderList, peptideCountPerLinkTypeList, searches);
		populateCountPerLinkTypeList( linkTypesDisplayOrderList, perPeptideCountPerLinkTypeList, searches);
		populateCountPerLinkTypeList( linkTypesDisplayOrderList, psmCountPerLinkTypeList, searches);

		for ( SearchDTO searchDTO : searches ) {
			Integer searchId = searchDTO.getSearchId();
			PerSearchIdTempData perSearchIdTempData = perSearchIdTempData_BySearchId.get( searchId );
			if ( perSearchIdTempData == null ) {
				String msg = "Internal error, search id not found in perSearchIdTempData_BySearchId. searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}

			//  Process per link type
			Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType = perSearchIdTempData.perLinkTypeTempData_ByLinkType;
			
			for ( int linkTypeIndex = 0; linkTypeIndex < linkTypesDisplayOrderList.size(); linkTypeIndex++ ) {
				String linkType = linkTypesDisplayOrderList.get( linkTypeIndex );
				PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
				if ( perLinkTypeTempData == null ) {
					String msg = "Internal error, linkType not found in perLinkTypeTempData_ByLinkType. linkType: " + linkType;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				
				// Reported Peptide Count entry add
				addPerSearchIdEntryForLinkType( 
						peptideCountPerLinkTypeList, 
						searchDTO, 
						linkTypeIndex, 
						perLinkTypeTempData.missedCleavageReportedPeptideCount, 
						perLinkTypeTempData.totalReportedPeptideCount );
				
				// Per Reported Peptide Count entry add
				addPerSearchIdEntryForLinkType( 
						perPeptideCountPerLinkTypeList, 
						searchDTO, 
						linkTypeIndex, 
						perLinkTypeTempData.missedCleavageCount,
						perLinkTypeTempData.totalReportedPeptideCount );
				
				 // PSM Count entry add
				addPerSearchIdEntryForLinkType( 
						psmCountPerLinkTypeList, 
						searchDTO, 
						linkTypeIndex, 
						perLinkTypeTempData.missedCleavagePSMCount,
						perLinkTypeTempData.totalPSMCount );
			}
		}

		QC_MissingCleavageReportedPeptidesCount_Merged_Results result = new QC_MissingCleavageReportedPeptidesCount_Merged_Results();

		result.setPeptideCountPerLinkTypeList( peptideCountPerLinkTypeList );
		result.setPerPeptideCountPerLinkTypeList( perPeptideCountPerLinkTypeList );
		result.setPsmCountPerLinkTypeList( psmCountPerLinkTypeList );
		
		return result;
	}
	
	
	/**
	 * @param countPerLinkTypeList
	 * @param searchId
	 * @param linkTypeIndex
	 * @param count
	 */
	public void addPerSearchIdEntryForLinkType(
			List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> countPerLinkTypeList, 
			SearchDTO searchDTO,
			int linkTypeIndex, 
			int count,
			int totalCount ) {
		
		QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged countPerLinkTypeEntry = countPerLinkTypeList.get( linkTypeIndex );
		QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged countsPerSearchId = new QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged();
		countsPerSearchId.setSearchId( searchDTO.getSearchId() );
		countsPerSearchId.setCount( count );
		countsPerSearchId.setTotalCount( totalCount );
		countPerLinkTypeEntry.getCountPerSearchIdMap_KeyProjectSearchId().put( searchDTO.getProjectSearchId(), countsPerSearchId );
	}


	/**
	 * method also adds combined entry at end
	 * 
	 * @param linkTypesDisplayOrderList
	 * @param countPerLinkTypeList
	 * @param searches
	 */
	private void populateCountPerLinkTypeList( 
			List<String> linkTypesDisplayOrderList, 
			List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> countPerLinkTypeList, 
			List<SearchDTO> searches ) {
		
		for ( String linkType : linkTypesDisplayOrderList ) {
			QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged qc_SummaryCountsResultsPerLinkType_Merged = new QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged();
			qc_SummaryCountsResultsPerLinkType_Merged.setLinkType( linkType );
			qc_SummaryCountsResultsPerLinkType_Merged.setCountPerSearchIdMap_KeyProjectSearchId( new HashMap<>() );
			countPerLinkTypeList.add( qc_SummaryCountsResultsPerLinkType_Merged );
		}

	}
	
	
	/**
	 * @param peptideSequence
	 * @param staticModDTOList
	 * @return positions ("1" based), null if staticModDTOList is null or empty
	 * @throws Exception
	 */
	private Set<Integer> getStaticModsPeptidePositionsForPeptideSequenceStaticModList( String peptideSequence, List<StaticModDTO> staticModDTOList ) throws Exception {
		if ( staticModDTOList == null || staticModDTOList.isEmpty() ) {
			return null;  //  EARLY EXIT 
		}
		Set<Integer> results = new HashSet<>();
		int staticModIndex = -2; // arbitrary initial value
		for ( StaticModDTO staticModDTO : staticModDTOList ) {
			int startSearchIndex = 0;
			while ( ( staticModIndex = peptideSequence.indexOf( staticModDTO.getResidue(), startSearchIndex ) ) != -1 ) {
				int staticModPosition = staticModIndex + 1; // add "1" to index to get position which is "1" based
				results.add( staticModPosition );
				startSearchIndex = staticModIndex + 1; // move startSearchIndex to after found staticModIndex
			}
		}
		return results;
	}
	
	/**
	 * @param srchRepPeptPeptideId
	 * @return positions ("1" based)
	 * @throws Exception
	 */
	private Set<Integer> getDynamicModsPeptidePositionsForSrchRepPeptPeptide( int srchRepPeptPeptideId ) throws Exception {
		Set<Integer> results = new HashSet<>();
		List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = 
				SrchRepPeptPeptDynamicModSearcher.getInstance()
				.getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideId );
		
		for ( SrchRepPeptPeptDynamicModDTO dynamicModRecord : srchRepPeptPeptDynamicModDTOList ) {
				results.add( dynamicModRecord.getPosition() );
		}
		
		return results;
	}



	/**
	 * 
	 *
	 */
	private class PerSearchIdTempData {
		Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType;
	}
	
	/**
	 * 
	 *
	 */
	private class PerLinkTypeTempData {

		//  Per Reported peptide counts
		private int totalReportedPeptideCount;
		private int missedCleavageReportedPeptideCount;

		//  Per PSM counts
		private int totalPSMCount;
		private int missedCleavagePSMCount;

		//  Total Cleavage points counts
		private int missedCleavageCount;

		
//		private String linkType;
		
		//  Per Reported peptide counts
		public void incrementTotalReportedPeptideCount() {
			totalReportedPeptideCount++;
		}
		public void incrementMissedCleavageReportedPeptideCount() {
			missedCleavageReportedPeptideCount++;
		}

		//  Per PSM counts
		public void addToTotalPSMCount( int countToAdd ) {
			totalPSMCount += countToAdd;
		}
		public void addToMissedCleavagePSMCount( int countToAdd ) {
			missedCleavagePSMCount += countToAdd;
		}

		//  Total Cleavage points counts
		public void addToMissedCleavageCount( int countToAdd ) {
			missedCleavageCount += countToAdd;
		}
		

	}

}

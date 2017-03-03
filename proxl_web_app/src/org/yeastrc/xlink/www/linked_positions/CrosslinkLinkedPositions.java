package org.yeastrc.xlink.www.linked_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosCrosslinkDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_LinkedPosition_Searcher;

/**
 *  Build lists of various objects for crosslink data from linked positions tables
 *  
 *  Objects of classes SearchProteinCrosslinkWrapper
 *
 */
public class CrosslinkLinkedPositions {
	
	private static final Logger log = Logger.getLogger(CrosslinkLinkedPositions.class);
	
	private CrosslinkLinkedPositions() { }
	private static final CrosslinkLinkedPositions _INSTANCE = new CrosslinkLinkedPositions();
	public static CrosslinkLinkedPositions getInstance() { return _INSTANCE; }
	
	private static class RepPept_Stage_1_Wrapper {
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = new ArrayList<>();;
	}
	
	private static enum PeptidePsm { PEPTIDE, PSM }
	
	private static String[] linkTypesCrosslink = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public List<SearchProteinCrosslinkWrapper> getSearchProteinCrosslinkWrapperList( 
			SearchDTO search, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		
		int searchId = search.getSearchId();
		List<WebReportedPeptideWrapper> wrappedPeptidelinks =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						search, searcherCutoffValuesSearchLevel, linkTypesCrosslink, null /* modMassSelections */, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );
		
		//  Build a structure of SrchRepPeptProtSeqIdPosCrosslinkDTO
		//  Mapped on Reported Peptide Id, searchReportedPeptidepeptideId (PK table srch_rep_pept__peptide)
		
		Cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId =
				Cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId.getInstance();
		
		//  Process into Map of protein 1, position 1, protein 2, position 2 objects
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2 = new HashMap<>();
		for ( WebReportedPeptideWrapper wrappedPeptidelink : wrappedPeptidelinks ) {
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_ReqParams reqParams = new SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_ReqParams();
			reqParams.setSearchId( searchId );
			reqParams.setReportedPeptideId( reportedPeptideId );
			SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result result =
					cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId.getSrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result( reqParams );
			List<SrchRepPeptProtSeqIdPosCrosslinkDTO> srchRepPeptProtSeqIdPosCrosslinkDTOList =
					result.getSrchRepPeptProtSeqIdPosCrosslinkDTOList();
			Map<Integer,List<SrchRepPeptProtSeqIdPosCrosslinkDTO>> protIdPosMap_On_SrchRepPeptPeptId = new HashMap<>();
			for ( SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO : srchRepPeptProtSeqIdPosCrosslinkDTOList ) {
				Integer searchReportedPeptidepeptideId = srchRepPeptProtSeqIdPosCrosslinkDTO.getSearchReportedPeptidepeptideId();
				List<SrchRepPeptProtSeqIdPosCrosslinkDTO> protIdPosList =
						protIdPosMap_On_SrchRepPeptPeptId.get( searchReportedPeptidepeptideId );
				if ( protIdPosList == null ) {
					protIdPosList = new ArrayList<>();
					protIdPosMap_On_SrchRepPeptPeptId.put( searchReportedPeptidepeptideId, protIdPosList );
				}
				protIdPosList.add( srchRepPeptProtSeqIdPosCrosslinkDTO );
			}
			if ( protIdPosMap_On_SrchRepPeptPeptId.size() != 2 ) {
				//  Did not find entries in srch_rep_pept__prot_seq_id_pos_crosslink related to both entries in srch_rep_pept__peptide so skip
				continue;  //  EARLY CONTINUE
			}
			Iterator<Map.Entry<Integer,List<SrchRepPeptProtSeqIdPosCrosslinkDTO>>> protIdPosMap_On_SrchRepPeptPeptId_Iterator =
					protIdPosMap_On_SrchRepPeptPeptId.entrySet().iterator();
			Map.Entry<Integer,List<SrchRepPeptProtSeqIdPosCrosslinkDTO>> protIdPosMap_On_SrchRepPeptPeptId_Entry_A =
					protIdPosMap_On_SrchRepPeptPeptId_Iterator.next();
			Map.Entry<Integer,List<SrchRepPeptProtSeqIdPosCrosslinkDTO>> protIdPosMap_On_SrchRepPeptPeptId_Entry_B =
					protIdPosMap_On_SrchRepPeptPeptId_Iterator.next();
//			Integer searchReportedPeptidepeptideId_Entry_A = protIdPosMap_On_SrchRepPeptPeptId_Entry_A.getKey();
//			Integer searchReportedPeptidepeptideId_Entry_B = protIdPosMap_On_SrchRepPeptPeptId_Entry_B.getKey();
			for ( SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO_Entry_A_Item : protIdPosMap_On_SrchRepPeptPeptId_Entry_A.getValue() ) {
				for ( SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO_Entry_B_Item : protIdPosMap_On_SrchRepPeptPeptId_Entry_B.getValue() ) {
					SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1 = srchRepPeptProtSeqIdPosCrosslinkDTO_Entry_A_Item;
					SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2 = srchRepPeptProtSeqIdPosCrosslinkDTO_Entry_B_Item;
					//  Order so:  ( id1 < id2 ) or ( id1 == id2 and pos1 <= pos2 )
					if ( ( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequenceId() > srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequenceId() )
							|| ( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequenceId() == srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequenceId()
									&& srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequencePosition() > srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequencePosition() ) ) {
						//  Swap order for consistency of displayed data and to match order the crosslink records were inserted in
						srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1 = srchRepPeptProtSeqIdPosCrosslinkDTO_Entry_B_Item;
						srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2 = srchRepPeptProtSeqIdPosCrosslinkDTO_Entry_A_Item;
					}
					//  Process into Map of protein 1, position 1, protein 2, position 2 objects
					// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2 = new HashMap<>();
					Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 =
							repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2.get( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequenceId() );
					if ( repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 == null ) {
						repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 = new HashMap<>();
						repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2.put( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequenceId(), repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 );
					}
					
					Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt2Pos2 =
							repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2.get( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequencePosition() );
					if ( repPept_Stage_1_Wrapper_MappedProt2Pos2 == null ) {
						repPept_Stage_1_Wrapper_MappedProt2Pos2 = new HashMap<>();
						repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2.put( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_1.getProteinSequencePosition(), repPept_Stage_1_Wrapper_MappedProt2Pos2 );
					}
					
					Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2 =
							repPept_Stage_1_Wrapper_MappedProt2Pos2.get( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequenceId() );
					if ( repPept_Stage_1_Wrapper_MappedPos2 == null ) {
						repPept_Stage_1_Wrapper_MappedPos2 = new HashMap<>();
						repPept_Stage_1_Wrapper_MappedProt2Pos2.put( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequenceId(), repPept_Stage_1_Wrapper_MappedPos2 );
					}
					
					RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = repPept_Stage_1_Wrapper_MappedPos2.get( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequencePosition() );
					if ( repPept_Stage_1_Wrapper == null ) {
						repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
						repPept_Stage_1_Wrapper_MappedPos2.put( srchRepPeptProtSeqIdPosCrosslinkDTO_Item_2.getProteinSequencePosition(), repPept_Stage_1_Wrapper );
					}
					
					boolean reportedPeptideIdAlreadyInList = false;
					for ( WebReportedPeptideWrapper itemInList : repPept_Stage_1_Wrapper.webReportedPeptideWrapperList ) {
						if ( itemInList.getWebReportedPeptide().getReportedPeptideId() == reportedPeptideId.intValue() ) {
							reportedPeptideIdAlreadyInList = true;
						}
					}
					
					if ( reportedPeptideIdAlreadyInList ) {
						continue;  //  EARLY CONTINUE    skip since this reported peptide is already in this list
					}
					
					repPept_Stage_1_Wrapper.webReportedPeptideWrapperList.add( wrappedPeptidelink );
				}
			}
		}
		////////////////////////////
		Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map = new HashMap<>();
		List<SearchProteinCrosslinkWrapper> wrappedLinks = new ArrayList<>();
		//  Process Map of protein 1, position 1, protein 2, position 2 objects
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2 = new HashMap<>();
		for ( Map.Entry<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2_Entry :
			repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2.entrySet() ) {
			Integer proteinSeqId_1 = repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2_Entry.getKey();
			Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 =
					repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2_Entry.getValue();
			
			for ( Map.Entry<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2_Entry :
				repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2.entrySet() ) {
				Integer proteinPosition_1 = repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2_Entry.getKey();
				Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt2Pos2 =
						repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2_Entry.getValue();
				
				for ( Map.Entry<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt2Pos2_Entry :
					repPept_Stage_1_Wrapper_MappedProt2Pos2.entrySet() ) {
					Integer proteinSeqId_2 = repPept_Stage_1_Wrapper_MappedProt2Pos2_Entry.getKey();
					Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2 =
							repPept_Stage_1_Wrapper_MappedProt2Pos2_Entry.getValue();
					
					for ( Map.Entry<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2_Entry :
						repPept_Stage_1_Wrapper_MappedPos2.entrySet() ) {
						Integer proteinPosition_2 = repPept_Stage_1_Wrapper_MappedPos2_Entry.getKey();
						RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
								repPept_Stage_1_Wrapper_MappedPos2_Entry.getValue();
						
						SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper =
								populateSearchProteinCrosslinkWrapper(
										search, 
										searcherCutoffValuesSearchLevel,
										proteinSeqId_1, 
										proteinPosition_1, 
										proteinSeqId_2,
										proteinPosition_2, 
										searchProtein_KeyOn_PROT_SEQ_ID_Map,
										repPept_Stage_1_Wrapper );
						if ( searchProteinCrosslinkWrapper == null ) {
							//  !!!!!!!   This isn't really a Protein that meets the cutoffs
							continue;  //  EARY LOOP ENTRY EXIT
						}
						wrappedLinks.add( searchProteinCrosslinkWrapper );
					}
				}
			}
		}
		return wrappedLinks;
	}
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param protein1
	 * @param protein2
	 * @param position1
	 * @param position2
	 * @return
	 * @throws Exception
	 */
	public SearchProteinCrosslinkWrapper getSearchProteinCrosslinkWrapperForSearchCutoffsProtIdsPositions( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			ProteinSequenceObject protein1, 
			ProteinSequenceObject protein2, 
			int position1, 
			int position2 ) throws Exception {
		
		Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map = new HashMap<>();
		RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
		List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkAnnDataWrapper_List = 
				SearchPeptideCrosslink_LinkedPosition_Searcher.getInstance()
				.searchOnSearchProteinCrosslink( 
						search, 
						searcherCutoffValuesSearchLevel, 
						protein1.getProteinSequenceId(), 
						protein2.getProteinSequenceId(), 
						position1, 
						position2 );
		for ( SearchPeptideCrosslinkAnnDataWrapper searchPeptideCrosslinkAnnDataWrapper : searchPeptideCrosslinkAnnDataWrapper_List ) {
			SearchPeptideCrosslink searchPeptideCrosslink = searchPeptideCrosslinkAnnDataWrapper.getSearchPeptideCrosslink();
			WebReportedPeptide webReportedPeptide = new WebReportedPeptide();
			webReportedPeptide.setSearch( search );
			webReportedPeptide.setSearchId( search.getSearchId() );
			webReportedPeptide.setReportedPeptideId( searchPeptideCrosslink.getReportedPeptideId() );
			webReportedPeptide.setNumPsms( searchPeptideCrosslink.getNumPsms() );
			webReportedPeptide.setSearchPeptideCrosslink( searchPeptideCrosslink );
			WebReportedPeptideWrapper wrappedPeptidelink = new WebReportedPeptideWrapper();
			wrappedPeptidelink.setWebReportedPeptide( webReportedPeptide );
			wrappedPeptidelink.setPeptideAnnotationDTOMap( searchPeptideCrosslinkAnnDataWrapper.getPeptideAnnotationDTOMap() );
			wrappedPeptidelink.setPsmAnnotationDTOMap( searchPeptideCrosslinkAnnDataWrapper.getPsmAnnotationDTOMap() );
			repPept_Stage_1_Wrapper.webReportedPeptideWrapperList.add( wrappedPeptidelink );
		}
		SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper = 
				populateSearchProteinCrosslinkWrapper(
						search, 
						searcherCutoffValuesSearchLevel, 
						protein1.getProteinSequenceId(), 
						position1, 
						protein2.getProteinSequenceId(), 
						position2, 
						searchProtein_KeyOn_PROT_SEQ_ID_Map, 
						repPept_Stage_1_Wrapper );
		return searchProteinCrosslinkWrapper;
	}
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param searchId
	 * @param searchProtein_KeyOn_PROT_SEQ_ID_Map
	 * @param proteinSeqId_1
	 * @param proteinPosition_1
	 * @param proteinSeqId_2
	 * @param proteinPosition_2
	 * @param repPept_Stage_1_Wrapper
	 * @return
	 * @throws Exception
	 */
	private SearchProteinCrosslinkWrapper populateSearchProteinCrosslinkWrapper(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			Integer proteinSeqId_1, 
			Integer proteinPosition_1,
			Integer proteinSeqId_2, 
			Integer proteinPosition_2,
			Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map,
			RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper) throws Exception {
		
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = repPept_Stage_1_Wrapper.webReportedPeptideWrapperList;
		Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOMap = new HashMap<>();
		Map<Integer, AnnotationDataBaseDTO> bestPeptideAnnotationDTOMap = new HashMap<>();
		int numPsms = 0;
		int numLinkedPeptides = 0;
		int numUniqueLinkedPeptides = 0;
		Set<Integer> reportedPeptideIds = new HashSet<>();
		Set<Integer> reportedPeptideIdsRelatedPeptidesUnique = new HashSet<>();
		
		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : webReportedPeptideWrapperList ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			numLinkedPeptides++;
			reportedPeptideIds.add( reportedPeptideId );
			Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request =
					new Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request();
			related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request.setSearchId( search.getSearchId() );
			related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request.setReportedPeptideId( reportedPeptideId );
			Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Result relatedResult =
					Cached_Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId.getInstance()
					.getRelated_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Result( related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request );
			boolean areRelatedPeptidesUnique = relatedResult.isRelated_peptides_unique();
			if ( areRelatedPeptidesUnique ) {
				numUniqueLinkedPeptides++;
				reportedPeptideIdsRelatedPeptidesUnique.add( reportedPeptideId );
			}
			numPsms += webReportedPeptide.getNumPsms();
			updateBestAnnotationValues( 
					bestPsmAnnotationDTOMap, 
					webReportedPeptideWrapper.getPsmAnnotationDTOMap(), 
					PeptidePsm.PSM,
					searcherCutoffValuesSearchLevel );
			updateBestAnnotationValues( 
					bestPeptideAnnotationDTOMap, 
					webReportedPeptideWrapper.getPeptideAnnotationDTOMap(),
					PeptidePsm.PEPTIDE,
					searcherCutoffValuesSearchLevel );
		}
		SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper = new SearchProteinCrosslinkWrapper();
		SearchProteinCrosslink searchProteinCrosslink = new SearchProteinCrosslink();
		searchProteinCrosslinkWrapper.setSearchProteinCrosslink( searchProteinCrosslink );
		searchProteinCrosslinkWrapper.setPsmAnnotationDTOMap( bestPsmAnnotationDTOMap );
		searchProteinCrosslinkWrapper.setPeptideAnnotationDTOMap( bestPeptideAnnotationDTOMap );
		searchProteinCrosslink.setSearch( search );
		searchProteinCrosslink.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
		SearchProtein searchProtein_1 = searchProtein_KeyOn_PROT_SEQ_ID_Map.get( proteinSeqId_1 );
		if ( searchProtein_1 == null ) {
			searchProtein_1 = new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( proteinSeqId_1 ) );
			searchProtein_KeyOn_PROT_SEQ_ID_Map.put( proteinSeqId_1, searchProtein_1 );
		}
		SearchProtein searchProtein_2 = null;
		if ( proteinSeqId_1.intValue() == proteinSeqId_2.intValue() ) {
			searchProtein_2 = searchProtein_1;
		} else {
			searchProtein_2 = searchProtein_KeyOn_PROT_SEQ_ID_Map.get( proteinSeqId_2 );
			if ( searchProtein_2 == null ) {
				searchProtein_2 = new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( proteinSeqId_2 ) );
				searchProtein_KeyOn_PROT_SEQ_ID_Map.put( proteinSeqId_2, searchProtein_2 );
			}
		}
		searchProteinCrosslink.setProtein1( searchProtein_1 );
		searchProteinCrosslink.setProtein2( searchProtein_2 );
		searchProteinCrosslink.setProtein1Position( proteinPosition_1 );
		searchProteinCrosslink.setProtein2Position( proteinPosition_2 );
		searchProteinCrosslink.setNumPsms( numPsms );
		searchProteinCrosslink.setNumLinkedPeptides( numLinkedPeptides );
		searchProteinCrosslink.setNumUniqueLinkedPeptides( numUniqueLinkedPeptides );
		searchProteinCrosslink.setAssociatedReportedPeptideIds( reportedPeptideIds );
		searchProteinCrosslink.setAssociatedReportedPeptideIdsRelatedPeptidesUnique( reportedPeptideIdsRelatedPeptidesUnique );
		if ( searchProteinCrosslink.getNumPsms() <= 0 ) {
			//  !!!!!!!   Number of PSMs is zero this this isn't really a Protein that meets the cutoffs
			return null;  //  EARY EXIT
		}
		return searchProteinCrosslinkWrapper;
	}
	private void updateBestAnnotationValues( 
			Map<Integer, AnnotationDataBaseDTO> bestAnnotationDTOMap, 
			Map<Integer, AnnotationDataBaseDTO> entryAnnotationDTOMap, 
			PeptidePsm peptidePsm,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws ProxlWebappDataException {
		for ( Map.Entry<Integer, AnnotationDataBaseDTO> entryAnnotationDTOMap_Entry : entryAnnotationDTOMap.entrySet() ) {
			Integer annotationTypeId = entryAnnotationDTOMap_Entry.getKey();
			AnnotationDataBaseDTO entryAnnotationDTO = entryAnnotationDTOMap_Entry.getValue();
			//  Reformat value string to look like what went into best fields in DB
			entryAnnotationDTO.setValueString( Double.toString( entryAnnotationDTO.getValueDouble() ) );
			AnnotationDataBaseDTO bestAnnotationDTO = bestAnnotationDTOMap.get( annotationTypeId );
			if ( bestAnnotationDTO == null ) {
				bestAnnotationDTOMap.put( annotationTypeId, entryAnnotationDTO );
			} else {
				SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel = null;
				if ( peptidePsm == PeptidePsm.PEPTIDE ) {
					searcherCutoffValuesAnnotationLevel = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffs( annotationTypeId );
				} else {
					searcherCutoffValuesAnnotationLevel = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffs( annotationTypeId );
				}
				if ( searcherCutoffValuesAnnotationLevel == null ) {
					String msg = "searcherCutoffValuesAnnotationLevel == null for annotationTypeId: " + annotationTypeId
							+ ", peptidePsm: " + peptidePsm;
					log.error(msg);
					throw new ProxlWebappDataException(msg);
				}
				AnnotationTypeDTO annotationTypeDTO = searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO();
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
				if ( annotationTypeFilterableDTO == null ) {
					String msg = "annotationTypeFilterableDTO == null for annotationTypeId: " + annotationTypeId;
					log.error(msg);
					throw new ProxlWebappDataException(msg);
				}
				FilterDirectionType filterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
				if ( filterDirectionType == FilterDirectionType.ABOVE ) {
					if ( entryAnnotationDTO.getValueDouble() > bestAnnotationDTO.getValueDouble() ) {
						//  entry has a better value than best so replace best with entry
						bestAnnotationDTOMap.put( annotationTypeId, entryAnnotationDTO );
					}
				} else {
					if ( entryAnnotationDTO.getValueDouble() < bestAnnotationDTO.getValueDouble() ) {
						//  entry has a better value than best so replace best with entry
						bestAnnotationDTOMap.put( annotationTypeId, entryAnnotationDTO );
					}
				}
			}
		}
	}
}

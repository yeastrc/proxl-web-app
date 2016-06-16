package org.yeastrc.xlink.www.linked_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SrchRepPeptNrseqIdPosCrosslinkDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher.Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher.ReturnOnlyReportedPeptidesWithMonolinks;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideNrseqPositionCrosslinkSearcher;

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
	
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public List<SearchProteinCrosslinkWrapper> getSearchProteinCrosslinkWrapperList( SearchDTO search, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
	
		int searchId = search.getId();
		
		String[] linkTypesCrosslink = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };

		List<WebReportedPeptideWrapper> wrappedPeptidelinks =
				PeptideWebPageSearcher.getInstance()
				.searchOnSearchIdPsmCutoffPeptideCutoff( search, searcherCutoffValuesSearchLevel, linkTypesCrosslink, null /* modMassSelections */, ReturnOnlyReportedPeptidesWithMonolinks.NO );
	
		
		//  Build a structure of SrchRepPeptNrseqIdPosDTO
		//  Mapped on Reported Peptide Id, searchReportedPeptidepeptideId (PK table srch_rep_pept__peptide)
		
		SearchReportedPeptideNrseqPositionCrosslinkSearcher searchReportedPeptideNrseqPositionSearcher =
				SearchReportedPeptideNrseqPositionCrosslinkSearcher.getInstance();
		
		//  Process into Map of protein 1, position 1, protein 2, position 2 objects
		
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		
		Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2 = new HashMap<>();
		
		
		for ( WebReportedPeptideWrapper wrappedPeptidelink : wrappedPeptidelinks ) {
			
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			List<SrchRepPeptNrseqIdPosCrosslinkDTO> srchRepPeptNrseqIdPosCrosslinkDTOList = searchReportedPeptideNrseqPositionSearcher.getSrchRepPeptNrseqIdPosCrosslinkDTOList( searchId, reportedPeptideId );
			
			Map<Integer,List<SrchRepPeptNrseqIdPosCrosslinkDTO>> protIdPosMap_On_SrchRepPeptPeptId = new HashMap<>();
			
			for ( SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO : srchRepPeptNrseqIdPosCrosslinkDTOList ) {
				
				Integer searchReportedPeptidepeptideId = srchRepPeptNrseqIdPosCrosslinkDTO.getSearchReportedPeptidepeptideId();

				List<SrchRepPeptNrseqIdPosCrosslinkDTO> protIdPosList =
						protIdPosMap_On_SrchRepPeptPeptId.get( searchReportedPeptidepeptideId );
				
				if ( protIdPosList == null ) {
					
					protIdPosList = new ArrayList<>();
					
					protIdPosMap_On_SrchRepPeptPeptId.put( searchReportedPeptidepeptideId, protIdPosList );
				}
				
				protIdPosList.add( srchRepPeptNrseqIdPosCrosslinkDTO );
			}

			if ( protIdPosMap_On_SrchRepPeptPeptId.size() != 2 ) {
				
				//  Did not find entries in srch_rep_pept__nrseq_id_pos related to both entries in srch_rep_pept__peptide so skip
				
				continue;  //  EARLY CONTINUE
			}

			Iterator<Map.Entry<Integer,List<SrchRepPeptNrseqIdPosCrosslinkDTO>>> protIdPosMap_On_SrchRepPeptPeptId_Iterator =
					protIdPosMap_On_SrchRepPeptPeptId.entrySet().iterator();
			
			Map.Entry<Integer,List<SrchRepPeptNrseqIdPosCrosslinkDTO>> protIdPosMap_On_SrchRepPeptPeptId_Entry_A =
					protIdPosMap_On_SrchRepPeptPeptId_Iterator.next();

			Map.Entry<Integer,List<SrchRepPeptNrseqIdPosCrosslinkDTO>> protIdPosMap_On_SrchRepPeptPeptId_Entry_B =
					protIdPosMap_On_SrchRepPeptPeptId_Iterator.next();

//			Integer searchReportedPeptidepeptideId_Entry_A = protIdPosMap_On_SrchRepPeptPeptId_Entry_A.getKey();
//			Integer searchReportedPeptidepeptideId_Entry_B = protIdPosMap_On_SrchRepPeptPeptId_Entry_B.getKey();
			
			for ( SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO_Entry_A_Item : protIdPosMap_On_SrchRepPeptPeptId_Entry_A.getValue() ) {
			
				for ( SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO_Entry_B_Item : protIdPosMap_On_SrchRepPeptPeptId_Entry_B.getValue() ) {
				
					SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO_Item_1 = srchRepPeptNrseqIdPosCrosslinkDTO_Entry_A_Item;
					SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO_Item_2 = srchRepPeptNrseqIdPosCrosslinkDTO_Entry_B_Item;
					
					//  Order so:  ( id1 < id2 ) or ( id1 == id2 and pos1 <= pos2 )
					
					if ( ( srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqId() > srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqId() )
							|| ( srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqId() == srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqId()
									&& srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqPosition() > srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqPosition() ) ) {

						//  Swap order for consistency of displayed data and to match order the crosslink records were inserted in
						
						srchRepPeptNrseqIdPosCrosslinkDTO_Item_1 = srchRepPeptNrseqIdPosCrosslinkDTO_Entry_B_Item;
						srchRepPeptNrseqIdPosCrosslinkDTO_Item_2 = srchRepPeptNrseqIdPosCrosslinkDTO_Entry_A_Item;
					}
					

					//  Process into Map of protein 1, position 1, protein 2, position 2 objects
					
					// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2 = new HashMap<>();
					
					Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 =
							repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2.get( srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqId() );
					
					if ( repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 == null ) {
						
						repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 = new HashMap<>();
						repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2.put( srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqId(), repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 );
					}

					Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt2Pos2 =
							repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2.get( srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqPosition() );
					
					if ( repPept_Stage_1_Wrapper_MappedProt2Pos2 == null ) {
						
						repPept_Stage_1_Wrapper_MappedProt2Pos2 = new HashMap<>();
						repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2.put( srchRepPeptNrseqIdPosCrosslinkDTO_Item_1.getNrseqPosition(), repPept_Stage_1_Wrapper_MappedProt2Pos2 );
					}


					Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2 =
							repPept_Stage_1_Wrapper_MappedProt2Pos2.get( srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqId() );
					
					if ( repPept_Stage_1_Wrapper_MappedPos2 == null ) {
						
						repPept_Stage_1_Wrapper_MappedPos2 = new HashMap<>();
						repPept_Stage_1_Wrapper_MappedProt2Pos2.put( srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqId(), repPept_Stage_1_Wrapper_MappedPos2 );
					}

					RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = repPept_Stage_1_Wrapper_MappedPos2.get( srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqPosition() );

					if ( repPept_Stage_1_Wrapper == null ) {
						
						repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
						repPept_Stage_1_Wrapper_MappedPos2.put( srchRepPeptNrseqIdPosCrosslinkDTO_Item_2.getNrseqPosition(), repPept_Stage_1_Wrapper );
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
		
		Map<Integer, SearchProtein> searchProtein_KeyOn_NRSEQ_ID_Map = new HashMap<>();
		


		List<SearchProteinCrosslinkWrapper> wrappedLinks = new ArrayList<>();

		
		
		//  Process Map of protein 1, position 1, protein 2, position 2 objects

		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		
		// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2 = new HashMap<>();
		
		for ( Map.Entry<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2_Entry :
			repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2.entrySet() ) {
		
			Integer proteinId_1 = repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2_Entry.getKey();
			
			Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2 =
					repPept_Stage_1_Wrapper_MappedProt1Pos1Prot2Pos2_Entry.getValue();

			for ( Map.Entry<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2_Entry :
				repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2.entrySet() ) {

				Integer proteinPosition_1 = repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2_Entry.getKey();

				Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt2Pos2 =
						repPept_Stage_1_Wrapper_MappedPos1Prot2Pos2_Entry.getValue();

				for ( Map.Entry<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt2Pos2_Entry :
					repPept_Stage_1_Wrapper_MappedProt2Pos2.entrySet() ) {

					Integer proteinId_2 = repPept_Stage_1_Wrapper_MappedProt2Pos2_Entry.getKey();

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
										proteinId_1, 
										proteinPosition_1, 
										proteinId_2,
										proteinPosition_2, 
										searchProtein_KeyOn_NRSEQ_ID_Map,
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
			NRProteinDTO protein1, 
			NRProteinDTO protein2, 
			int position1, 
			int position2 ) throws Exception {
		

		Map<Integer, SearchProtein> searchProtein_KeyOn_NRSEQ_ID_Map = new HashMap<>();
		
		RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
		
		List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkAnnDataWrapper_List = 
				SearchPeptideCrosslink_LinkedPosition_Searcher.getInstance()
				.searchOnSearchProteinCrosslink( 
						search.getId(), 
						searcherCutoffValuesSearchLevel, 
						protein1.getNrseqId(), 
						protein2.getNrseqId(), 
						position1, 
						position2 );

		for ( SearchPeptideCrosslinkAnnDataWrapper searchPeptideCrosslinkAnnDataWrapper : searchPeptideCrosslinkAnnDataWrapper_List ) {
		
			SearchPeptideCrosslink searchPeptideCrosslink = searchPeptideCrosslinkAnnDataWrapper.getSearchPeptideCrosslink();
			
			WebReportedPeptide webReportedPeptide = new WebReportedPeptide();
			
			webReportedPeptide.setSearch( search );
			webReportedPeptide.setSearchId( search.getId() );
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
						protein1.getNrseqId(), 
						position1, 
						protein2.getNrseqId(), 
						position2, 
						searchProtein_KeyOn_NRSEQ_ID_Map, 
						repPept_Stage_1_Wrapper );
		
		return searchProteinCrosslinkWrapper;
	}


	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param searchId
	 * @param searchProtein_KeyOn_NRSEQ_ID_Map
	 * @param proteinId_1
	 * @param proteinPosition_1
	 * @param proteinId_2
	 * @param proteinPosition_2
	 * @param repPept_Stage_1_Wrapper
	 * @return
	 * @throws Exception
	 */
	private SearchProteinCrosslinkWrapper populateSearchProteinCrosslinkWrapper(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			Integer proteinId_1, 
			Integer proteinPosition_1,
			Integer proteinId_2, 
			Integer proteinPosition_2,
			Map<Integer, SearchProtein> searchProtein_KeyOn_NRSEQ_ID_Map,
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
			
			boolean areRelatedPeptidesUnique =
					Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher.getInstance()
					.get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId( search.getId(), reportedPeptideId );
			
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


		SearchProtein searchProtein_1 = searchProtein_KeyOn_NRSEQ_ID_Map.get( proteinId_1 );


		if ( searchProtein_1 == null ) {

			searchProtein_1 = new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( proteinId_1 ) );

			searchProtein_KeyOn_NRSEQ_ID_Map.put( proteinId_1, searchProtein_1 );
		}

		SearchProtein searchProtein_2 = null;

		if ( proteinId_1.intValue() == proteinId_2.intValue() ) {

			searchProtein_2 = searchProtein_1;

		} else {

			searchProtein_2 = searchProtein_KeyOn_NRSEQ_ID_Map.get( proteinId_2 );

			if ( searchProtein_2 == null ) {
				searchProtein_2 = new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( proteinId_2 ) );

				searchProtein_KeyOn_NRSEQ_ID_Map.put( proteinId_2, searchProtein_2 );
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

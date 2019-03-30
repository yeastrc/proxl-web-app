package org.yeastrc.xlink.www.linked_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosDimerDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosUnlinkedDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideProteinSequencePositionDimerSearcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideProteinSequencePositionUnlinkedSearcher;

/**
 *  Build lists of various objects for unlinked and dimer data from peptide to protein mapping tables
 *  
 *  Objects of classes SearchProteinDimerWrapper
 *
 */
public class UnlinkedDimerPeptideProteinMapping {
	
	private static final Logger log = LoggerFactory.getLogger( UnlinkedDimerPeptideProteinMapping.class);
	
	private UnlinkedDimerPeptideProteinMapping() { }
	private static final UnlinkedDimerPeptideProteinMapping _INSTANCE = new UnlinkedDimerPeptideProteinMapping();
	public static UnlinkedDimerPeptideProteinMapping getInstance() { return _INSTANCE; }
	
	private static class RepPept_Stage_1_Wrapper {
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = new ArrayList<>();;
	}
	private static enum PeptidePsm { PEPTIDE, PSM }
	
	/**
	 * Result from UnlinkedDimerPeptideProteinMapping
	 *
	 */
	public static class UnlinkedDimerPeptideProteinMappingResult {
		private List<SearchProteinDimerWrapper> searchProteinDimerWrapperList;
		private List<SearchProteinUnlinkedWrapper> searchProteinUnlinkedWrapperList;
		public List<SearchProteinDimerWrapper> getSearchProteinDimerWrapperList() {
			return searchProteinDimerWrapperList;
		}
		public void setSearchProteinDimerWrapperList(
				List<SearchProteinDimerWrapper> searchProteinDimerWrapperList) {
			this.searchProteinDimerWrapperList = searchProteinDimerWrapperList;
		}
		public List<SearchProteinUnlinkedWrapper> getSearchProteinUnlinkedWrapperList() {
			return searchProteinUnlinkedWrapperList;
		}
		public void setSearchProteinUnlinkedWrapperList(
				List<SearchProteinUnlinkedWrapper> searchProteinUnlinkedWrapperList) {
			this.searchProteinUnlinkedWrapperList = searchProteinUnlinkedWrapperList;
		}
	}
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public UnlinkedDimerPeptideProteinMappingResult getSearchProteinUnlinkedAndDimerWrapperLists( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param ) throws Exception {
		
		int searchId = search.getSearchId();
		String[] linkTypesUnlinkedDimer = { PeptideViewLinkTypesConstants.UNLINKED_PSM }; // Get Unlinked and Dimer records
		List<WebReportedPeptideWrapper> wrappedPeptidelinks =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						search, searcherCutoffValuesSearchLevel, linkTypesUnlinkedDimer, null /* modMassSelections */, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );
		//  DIMER
		//  Build a structure of SrchRepPeptProtSeqIdPosDTO
		//  Mapped on Reported Peptide Id, searchReportedPeptidepeptideId (PK table srch_rep_pept__peptide)
		//  Process into Map of protein 1, protein 2  objects
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2 = new HashMap<>();
		//  UNLINKED
		//  Build a structure of SrchRepPeptProtSeqIdPosDTO
		//  Mapped on Reported Peptide Id, searchReportedPeptidepeptideId (PK table srch_rep_pept__peptide)
		//  Process into Map of protein objects
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		Map<Integer,RepPept_Stage_1_Wrapper> unlinked__repPept_Stage_1_Wrapper_MappedProt = new HashMap<>();
		//   Process wrappedPeptidelinks which contains Dimer and Unlinked Reported Peptide Records
		for ( WebReportedPeptideWrapper wrappedPeptidelink : wrappedPeptidelinks ) {
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
				processDimerReportedPeptideRecord(
						dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2,
						wrappedPeptidelink, 
						reportedPeptideId,
						searchId );
			} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
				processUnlinkedReportedPeptideRecord(
						unlinked__repPept_Stage_1_Wrapper_MappedProt,
						wrappedPeptidelink, 
						reportedPeptideId,
						searchId );
			} else {
				String msg = "Unexpected link type in webReportedPeptide.  link type: " + webReportedPeptide.getLinkType();
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
		}
		////////////////////////////
		Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map = new HashMap<>();
		List<SearchProteinDimerWrapper> searchProteinDimerWrapperList = getSearchProteinDimerWrapperList(
				search,
				searcherCutoffValuesSearchLevel,
				linkedPositions_FilterExcludeLinksWith_Param,
				dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2,
				searchProtein_KeyOn_PROT_SEQ_ID_Map );
		List<SearchProteinUnlinkedWrapper> searchProteinUnlinkedWrapperList = 
				getSearchProteinUnlinkedWrapperList( 
						search, 
						searcherCutoffValuesSearchLevel, 
						linkedPositions_FilterExcludeLinksWith_Param,
						unlinked__repPept_Stage_1_Wrapper_MappedProt, 
						searchProtein_KeyOn_PROT_SEQ_ID_Map );
		UnlinkedDimerPeptideProteinMappingResult unlinkedDimerPeptideProteinMappingResult = new UnlinkedDimerPeptideProteinMappingResult();
		unlinkedDimerPeptideProteinMappingResult.searchProteinDimerWrapperList = searchProteinDimerWrapperList;
		unlinkedDimerPeptideProteinMappingResult.searchProteinUnlinkedWrapperList = searchProteinUnlinkedWrapperList;
		return unlinkedDimerPeptideProteinMappingResult;
	}
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2
	 * @param searchProtein_KeyOn_PROT_SEQ_ID_Map
	 * @return
	 * @throws Exception
	 */
	private List<SearchProteinDimerWrapper> getSearchProteinDimerWrapperList(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param,
			Map<Integer, Map<Integer, RepPept_Stage_1_Wrapper>> dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2,
			Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map)
			throws Exception {
		
		List<SearchProteinDimerWrapper> wrappedLinks = new ArrayList<>();
		//  Process Map of protein 1, position 1, protein 2, position 2 objects
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Prot2 = new HashMap<>();
		for ( Map.Entry<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProt1Prot2_Entry :
			dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2.entrySet() ) {
			Integer proteinId_1 = repPept_Stage_1_Wrapper_MappedProt1Prot2_Entry.getKey();
			Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedProt2 =
					repPept_Stage_1_Wrapper_MappedProt1Prot2_Entry.getValue();
			for ( Map.Entry<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedProt2_Entry :
				repPept_Stage_1_Wrapper_MappedProt2.entrySet() ) {
				Integer proteinId_2 = repPept_Stage_1_Wrapper_MappedProt2_Entry.getKey();
				RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
						repPept_Stage_1_Wrapper_MappedProt2_Entry.getValue();
				SearchProteinDimerWrapper searchProteinDimerWrapper =
						populateSearchProteinDimerWrapper(
								search, 
								searcherCutoffValuesSearchLevel,
								linkedPositions_FilterExcludeLinksWith_Param,
								proteinId_1, 
								proteinId_2,
								searchProtein_KeyOn_PROT_SEQ_ID_Map,
								repPept_Stage_1_Wrapper );
				if ( searchProteinDimerWrapper == null ) {
					//  !!!!!!!   This isn't really a Protein that meets the cutoffs
					continue;  //  EARY LOOP ENTRY EXIT
				}
				wrappedLinks.add( searchProteinDimerWrapper );
			}
		}
		return wrappedLinks;
	}
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param unlinked__repPept_Stage_1_Wrapper_MappedProt
	 * @param searchProtein_KeyOn_PROT_SEQ_ID_Map
	 * @return
	 * @throws Exception
	 */
	private List<SearchProteinUnlinkedWrapper> getSearchProteinUnlinkedWrapperList(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param,
			Map<Integer, RepPept_Stage_1_Wrapper> unlinked__repPept_Stage_1_Wrapper_MappedProt,
			Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map)
			throws Exception {
		
		List<SearchProteinUnlinkedWrapper> wrappedLinks = new ArrayList<>();
		//  Process Map of protein 1, position 1, protein 2, position 2 objects
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Prot2 = new HashMap<>();
		for ( Map.Entry<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedProt_Entry :
			unlinked__repPept_Stage_1_Wrapper_MappedProt.entrySet() ) {
			Integer proteinId = repPept_Stage_1_Wrapper_MappedProt_Entry.getKey();
			RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
					repPept_Stage_1_Wrapper_MappedProt_Entry.getValue();
			SearchProteinUnlinkedWrapper searchProteinUnlinkedWrapper =
					populateSearchProteinUnlinkedWrapper(
							search, 
							searcherCutoffValuesSearchLevel,
							linkedPositions_FilterExcludeLinksWith_Param,
							proteinId, 
							searchProtein_KeyOn_PROT_SEQ_ID_Map,
							repPept_Stage_1_Wrapper );
			if ( searchProteinUnlinkedWrapper == null ) {
				//  !!!!!!!   This isn't really a Protein that meets the cutoffs
				continue;  //  EARY LOOP ENTRY EXIT
			}
			wrappedLinks.add( searchProteinUnlinkedWrapper );
		}
		return wrappedLinks;
	}
	
	/**
	 * process Dimer ReportedPeptide Record
	 * 
	 * @param dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2
	 * @param wrappedPeptidelink
	 * @param reportedPeptideId
	 * @param searchId
	 * @throws Exception 
	 */
	private void processDimerReportedPeptideRecord (
			Map<Integer, Map<Integer, RepPept_Stage_1_Wrapper>> dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2,
			WebReportedPeptideWrapper wrappedPeptidelink,
			Integer reportedPeptideId,
			int searchId
			) throws Exception {
		
		List<SrchRepPeptProtSeqIdPosDimerDTO> srchRepPeptProtSeqIdPosDimerDTOList = 
				SearchReportedPeptideProteinSequencePositionDimerSearcher.getInstance()
				.getSrchRepPeptProtSeqIdPosDimerDTOList( searchId, reportedPeptideId );
		//  Copy the returned data into a map keyed on SrchRepPeptPeptId to group each half of the dimer
		Map<Integer,List<SrchRepPeptProtSeqIdPosDimerDTO>> protIdPosMap_On_SrchRepPeptPeptId = new HashMap<>();
		for ( SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO : srchRepPeptProtSeqIdPosDimerDTOList ) {
			Integer searchReportedPeptidePeptideId = srchRepPeptProtSeqIdPosDimerDTO.getSearchReportedPeptidepeptideId();
			List<SrchRepPeptProtSeqIdPosDimerDTO> protIdPosList = // Get List for searchReportedPeptidePeptideId
					protIdPosMap_On_SrchRepPeptPeptId.get( searchReportedPeptidePeptideId );
			if ( protIdPosList == null ) {  //  Add entry in map for searchReportedPeptidepeptideId
				protIdPosList = new ArrayList<>();
				protIdPosMap_On_SrchRepPeptPeptId.put( searchReportedPeptidePeptideId, protIdPosList );
			}
			//  Add srchRepPeptProtSeqIdPosDimerDTO to list for searchReportedPeptidepeptideId
			protIdPosList.add( srchRepPeptProtSeqIdPosDimerDTO );
		}
		if ( protIdPosMap_On_SrchRepPeptPeptId.size() != 2 ) {
			//  Did not find entries in srch_rep_pept__prot_seq_id_dimer related to both entries in srch_rep_pept__peptide so skip
			return;  //  EARLY RETURN
		}
		//  Pull out the lists of proteins for each half of the dimer
		Iterator<Map.Entry<Integer,List<SrchRepPeptProtSeqIdPosDimerDTO>>> protIdPosMap_On_SrchRepPeptPeptId_Iterator =
				protIdPosMap_On_SrchRepPeptPeptId.entrySet().iterator();
		Map.Entry<Integer,List<SrchRepPeptProtSeqIdPosDimerDTO>> protIdPosMap_On_SrchRepPeptPeptId_Entry_A =
				protIdPosMap_On_SrchRepPeptPeptId_Iterator.next();
		Map.Entry<Integer,List<SrchRepPeptProtSeqIdPosDimerDTO>> protIdPosMap_On_SrchRepPeptPeptId_Entry_B =
				protIdPosMap_On_SrchRepPeptPeptId_Iterator.next();
//		Integer searchReportedPeptidepeptideId_Entry_A = protIdPosMap_On_SrchRepPeptPeptId_Entry_A.getKey();
//		Integer searchReportedPeptidepeptideId_Entry_B = protIdPosMap_On_SrchRepPeptPeptId_Entry_B.getKey();
		//  Process the lists of proteins for each half of the dimer, creating all cross joined entries of A and B
		for ( SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO_Entry_A_Item : protIdPosMap_On_SrchRepPeptPeptId_Entry_A.getValue() ) {
			for ( SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO_Entry_B_Item : protIdPosMap_On_SrchRepPeptPeptId_Entry_B.getValue() ) {
				SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO_Item_1 = srchRepPeptProtSeqIdPosDimerDTO_Entry_A_Item;
				SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO_Item_2 = srchRepPeptProtSeqIdPosDimerDTO_Entry_B_Item;
				//  Order so:  ( id1 <= id2 )
				if ( ( srchRepPeptProtSeqIdPosDimerDTO_Item_1.getProteinSequenceVersionId() > srchRepPeptProtSeqIdPosDimerDTO_Item_2.getProteinSequenceVersionId() ) ) {
					//  Swap order for consistency of displayed data and to match order the dimer records were inserted in
					srchRepPeptProtSeqIdPosDimerDTO_Item_1 = srchRepPeptProtSeqIdPosDimerDTO_Entry_B_Item;
					srchRepPeptProtSeqIdPosDimerDTO_Item_2 = srchRepPeptProtSeqIdPosDimerDTO_Entry_A_Item;
				}
				//  Process into Map of protein 1, protein 2 objects
				// Map<Integer,Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>>> repPept_Stage_1_Wrapper_MappedProt1Prot2 = new HashMap<>();
				Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedProt2 =
						dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2.get( srchRepPeptProtSeqIdPosDimerDTO_Item_1.getProteinSequenceVersionId() );
				if ( repPept_Stage_1_Wrapper_MappedProt2 == null ) {
					repPept_Stage_1_Wrapper_MappedProt2 = new HashMap<>();
					dimer__repPept_Stage_1_Wrapper_MappedProt1Prot2.put( srchRepPeptProtSeqIdPosDimerDTO_Item_1.getProteinSequenceVersionId(), repPept_Stage_1_Wrapper_MappedProt2 );
				}
				RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
						repPept_Stage_1_Wrapper_MappedProt2.get( srchRepPeptProtSeqIdPosDimerDTO_Item_2.getProteinSequenceVersionId() );
				if ( repPept_Stage_1_Wrapper == null ) {
					repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
					repPept_Stage_1_Wrapper_MappedProt2.put( srchRepPeptProtSeqIdPosDimerDTO_Item_2.getProteinSequenceVersionId(), repPept_Stage_1_Wrapper );
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
	
	/**
	 * process Unlinked ReportedPeptide Record
	 * 
	 * @param unlinked__repPept_Stage_1_Wrapper_MappedProt
	 * @param wrappedPeptidelink
	 * @param reportedPeptideId
	 * @param protIdPosMap_On_SrchRepPeptPeptId
	 * @throws Exception 
	 */
	private void processUnlinkedReportedPeptideRecord (
			Map<Integer, RepPept_Stage_1_Wrapper> unlinked__repPept_Stage_1_Wrapper_MappedProt,
			WebReportedPeptideWrapper wrappedPeptidelink,
			Integer reportedPeptideId,
			int searchId
			) throws Exception {
		
		List<SrchRepPeptProtSeqIdPosUnlinkedDTO> srchRepPeptProtSeqIdPosUnlinkedDTOList = 
				SearchReportedPeptideProteinSequencePositionUnlinkedSearcher.getInstance()
				.getSrchRepPeptProtSeqIdPosUnlinkedDTOList( searchId, reportedPeptideId );
		for ( SrchRepPeptProtSeqIdPosUnlinkedDTO srchRepPeptProtSeqIdPosUnlinkedDTO_Item : srchRepPeptProtSeqIdPosUnlinkedDTOList ) {
			//  Process into Map of protein objects
			// Map<Integer, RepPept_Stage_1_Wrapper> unlinked__repPept_Stage_1_Wrapper_MappedProt,
			RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
					unlinked__repPept_Stage_1_Wrapper_MappedProt.get( srchRepPeptProtSeqIdPosUnlinkedDTO_Item.getProteinSequenceVersionId() );
			if ( repPept_Stage_1_Wrapper == null ) {
				repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
				unlinked__repPept_Stage_1_Wrapper_MappedProt.put( srchRepPeptProtSeqIdPosUnlinkedDTO_Item.getProteinSequenceVersionId(), repPept_Stage_1_Wrapper );
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
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param searchId
	 * @param searchProtein_KeyOn_PROT_SEQ_ID_Map
	 * @param proteinId_1
	 * @param proteinId_2
	 * @param repPept_Stage_1_Wrapper
	 * @return
	 * @throws Exception
	 */
	private SearchProteinDimerWrapper populateSearchProteinDimerWrapper(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param,
			Integer proteinId_1, 
			Integer proteinId_2, 
			Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map,
			RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper) throws Exception {
		
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = repPept_Stage_1_Wrapper.webReportedPeptideWrapperList;
		Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOMap = new HashMap<>();
		Map<Integer, AnnotationDataBaseDTO> bestPeptideAnnotationDTOMap = new HashMap<>();
		int numPsms = 0;
		Set<Integer> reportedPeptideIds = new HashSet<>();
		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : webReportedPeptideWrapperList ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();

			// did the user request to removal of links with only Non-Unique PSMs?
			if( linkedPositions_FilterExcludeLinksWith_Param.isRemoveNonUniquePSMs()  ) {
				//  Update webReportedPeptide object to remove non-unique PSMs
				webReportedPeptide.updateNumPsmsToNotInclude_NonUniquePSMs();
				if ( webReportedPeptide.getNumPsms() <= 0 ) {
					// The number of PSMs after update is now zero
					//  Skip to next entry in list, dropping this entry from output list
					continue;  // EARLY CONTINUE
				}
			}
			
			reportedPeptideIds.add( reportedPeptideId );
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
		SearchProteinDimerWrapper searchProteinDimerWrapper = new SearchProteinDimerWrapper();
		SearchProteinDimer searchProteinDimer = new SearchProteinDimer();
		searchProteinDimerWrapper.setSearchProteinDimer( searchProteinDimer );
		searchProteinDimerWrapper.setPsmAnnotationDTOMap( bestPsmAnnotationDTOMap );
		searchProteinDimerWrapper.setPeptideAnnotationDTOMap( bestPeptideAnnotationDTOMap );
		searchProteinDimer.setSearch( search );
		searchProteinDimer.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
		SearchProtein searchProtein_1 = searchProtein_KeyOn_PROT_SEQ_ID_Map.get( proteinId_1 );
		if ( searchProtein_1 == null ) {
			searchProtein_1 = new SearchProtein( search, ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinId_1 ) );
			searchProtein_KeyOn_PROT_SEQ_ID_Map.put( proteinId_1, searchProtein_1 );
		}
		SearchProtein searchProtein_2 = null;
		if ( proteinId_1.intValue() == proteinId_2.intValue() ) {
			searchProtein_2 = searchProtein_1;
		} else {
			searchProtein_2 = searchProtein_KeyOn_PROT_SEQ_ID_Map.get( proteinId_2 );
			if ( searchProtein_2 == null ) {
				searchProtein_2 = new SearchProtein( search, ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinId_2 ) );
				searchProtein_KeyOn_PROT_SEQ_ID_Map.put( proteinId_2, searchProtein_2 );
			}
		}
		searchProteinDimer.setProtein1( searchProtein_1 );
		searchProteinDimer.setProtein2( searchProtein_2 );
		if ( numPsms <= 0 ) {
			//  !!!!!!!   Number of PSMs is zero this this isn't really a Protein that meets the cutoffs
			return null;  //  EARY EXIT
		}
		return searchProteinDimerWrapper;
	}
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param searchId
	 * @param searchProtein_KeyOn_PROT_SEQ_ID_Map
	 * @param proteinId
	 * @param repPept_Stage_1_Wrapper
	 * @return
	 * @throws Exception
	 */
	private SearchProteinUnlinkedWrapper populateSearchProteinUnlinkedWrapper(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param,
			Integer proteinId,  
			Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map,
			RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper) throws Exception {
		
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = repPept_Stage_1_Wrapper.webReportedPeptideWrapperList;
		Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOMap = new HashMap<>();
		Map<Integer, AnnotationDataBaseDTO> bestPeptideAnnotationDTOMap = new HashMap<>();
		int numPsms = 0;
		Set<Integer> reportedPeptideIds = new HashSet<>();
		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : webReportedPeptideWrapperList ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();

			// did the user request to removal of links with only Non-Unique PSMs?
			if( linkedPositions_FilterExcludeLinksWith_Param.isRemoveNonUniquePSMs()  ) {
				//  Update webReportedPeptide object to remove non-unique PSMs
				webReportedPeptide.updateNumPsmsToNotInclude_NonUniquePSMs();
				if ( webReportedPeptide.getNumPsms() <= 0 ) {
					// The number of PSMs after update is now zero
					//  Skip to next entry in list, dropping this entry from output list
					continue;  // EARLY CONTINUE
				}
			}
			
			reportedPeptideIds.add( reportedPeptideId );
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
		SearchProteinUnlinkedWrapper searchProteinUnlinkedWrapper = new SearchProteinUnlinkedWrapper();
		SearchProteinUnlinked searchProteinUnlinked = new SearchProteinUnlinked();
		searchProteinUnlinkedWrapper.setSearchProteinUnlinked( searchProteinUnlinked );
		searchProteinUnlinkedWrapper.setPsmAnnotationDTOMap( bestPsmAnnotationDTOMap );
		searchProteinUnlinkedWrapper.setPeptideAnnotationDTOMap( bestPeptideAnnotationDTOMap );
		searchProteinUnlinked.setSearch( search );
		searchProteinUnlinked.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
		SearchProtein searchProtein = searchProtein_KeyOn_PROT_SEQ_ID_Map.get( proteinId );
		if ( searchProtein == null ) {
			searchProtein = new SearchProtein( search, ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinId ) );
			searchProtein_KeyOn_PROT_SEQ_ID_Map.put( proteinId, searchProtein );
		}
		searchProteinUnlinked.setProtein( searchProtein );
		if ( numPsms <= 0 ) {
			//  !!!!!!!   Number of PSMs is zero this this isn't really a Protein that meets the cutoffs
			return null;  //  EARY EXIT
		}
		return searchProteinUnlinkedWrapper;
	}
	
	/**
	 * @param bestAnnotationDTOMap
	 * @param entryAnnotationDTOMap
	 * @param peptidePsm
	 * @param searcherCutoffValuesSearchLevel
	 * @throws ProxlWebappDataException
	 */
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

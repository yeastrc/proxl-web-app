package org.yeastrc.xlink.www.linked_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.yeastrc.xlink.www.dto.SrchRepPeptNrseqIdPosMonolinkDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolink;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolinkWrapper;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher.Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher.ReturnOnlyReportedPeptidesWithMonolinks;
import org.yeastrc.xlink.www.searcher.SearchPeptideMonolink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideNrseqPositionMonolinkSearcher;

/**
 *  Build lists of various objects for monolink data from linked positions tables
 *  
 *  Objects of classes SearchProteinMonolinkWrapper
 *
 */
public class MonolinkLinkedPositions {
	
	private static final Logger log = Logger.getLogger(MonolinkLinkedPositions.class);

	private MonolinkLinkedPositions() { }
	private static final MonolinkLinkedPositions _INSTANCE = new MonolinkLinkedPositions();
	public static MonolinkLinkedPositions getInstance() { return _INSTANCE; }
	
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
	public List<SearchProteinMonolinkWrapper> getSearchProteinMonolinkWrapperList( SearchDTO search, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
	
		int searchId = search.getId();
		
		String[] linkTypesAll = null; //  Get All Link Types

		List<WebReportedPeptideWrapper> wrappedPeptidelinks =
				PeptideWebPageSearcher.getInstance()
				.searchOnSearchIdPsmCutoffPeptideCutoff( search, searcherCutoffValuesSearchLevel, linkTypesAll, null /* modMassSelections */, ReturnOnlyReportedPeptidesWithMonolinks.YES );
	
		
		//  Build a structure of SrchRepPeptNrseqIdPosDTO
		//  Mapped on Reported Peptide Id, searchReportedPeptidepeptideId (PK table srch_rep_pept__peptide)
		
		SearchReportedPeptideNrseqPositionMonolinkSearcher searchReportedPeptideNrseqPositionSearcher =
				SearchReportedPeptideNrseqPositionMonolinkSearcher.getInstance();
		
		//  Process into Map of protein, position objects
		
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		
		Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProtPos = new HashMap<>();
		
		
		for ( WebReportedPeptideWrapper wrappedPeptidelink : wrappedPeptidelinks ) {
			
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			List<SrchRepPeptNrseqIdPosMonolinkDTO> srchRepPeptNrseqIdPosMonolinkDTOList = searchReportedPeptideNrseqPositionSearcher.getSrchRepPeptNrseqIdPosMonolinkDTOList( searchId, reportedPeptideId );
			
			for ( SrchRepPeptNrseqIdPosMonolinkDTO srchRepPeptNrseqIdPosMonolinkDTO : srchRepPeptNrseqIdPosMonolinkDTOList ) {
			

				//  Process into Map of protein, position objects

				Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos =
						repPept_Stage_1_Wrapper_MappedProtPos.get( srchRepPeptNrseqIdPosMonolinkDTO.getNrseqId() );

				if ( repPept_Stage_1_Wrapper_MappedPos == null ) {

					repPept_Stage_1_Wrapper_MappedPos = new HashMap<>();
					repPept_Stage_1_Wrapper_MappedProtPos.put( srchRepPeptNrseqIdPosMonolinkDTO.getNrseqId(), repPept_Stage_1_Wrapper_MappedPos );
				}


				RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = repPept_Stage_1_Wrapper_MappedPos.get( srchRepPeptNrseqIdPosMonolinkDTO.getNrseqPosition() );

				if ( repPept_Stage_1_Wrapper == null ) {

					repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
					repPept_Stage_1_Wrapper_MappedPos.put( srchRepPeptNrseqIdPosMonolinkDTO.getNrseqPosition(), repPept_Stage_1_Wrapper );
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
		
		
		////////////////////////////
		
		Map<Integer, SearchProtein> searchProtein_KeyOn_NRSEQ_ID_Map = new HashMap<>();
		


		List<SearchProteinMonolinkWrapper> wrappedLinks = new ArrayList<>();

		
		
		//  Process Map of protein, position objects

		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//

		// Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProtPos

		for ( Map.Entry<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedProtPos_Entry :
			repPept_Stage_1_Wrapper_MappedProtPos.entrySet() ) {

			Integer proteinId = repPept_Stage_1_Wrapper_MappedProtPos_Entry.getKey();

			Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos =
					repPept_Stage_1_Wrapper_MappedProtPos_Entry.getValue();

			for ( Map.Entry<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos_Entry :
				repPept_Stage_1_Wrapper_MappedPos.entrySet() ) {

				Integer proteinPosition = repPept_Stage_1_Wrapper_MappedPos_Entry.getKey();

				RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
						repPept_Stage_1_Wrapper_MappedPos_Entry.getValue();


				SearchProteinMonolinkWrapper searchProteinMonolinkWrapper =

						populateSearchProteinMonolinkWrapper(
								search, 
								searcherCutoffValuesSearchLevel,
								proteinId, 
								proteinPosition,  
								searchProtein_KeyOn_NRSEQ_ID_Map,
								repPept_Stage_1_Wrapper );


				if ( searchProteinMonolinkWrapper == null ) {

					//  !!!!!!!   This isn't really a Protein that meets the cutoffs

					continue;  //  EARY LOOP ENTRY EXIT
				}

				wrappedLinks.add( searchProteinMonolinkWrapper );

			}
		}


		return wrappedLinks;
	}
	
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param protein
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public SearchProteinMonolinkWrapper getSearchProteinMonolinkWrapperForSearchCutoffsProtIdsPositions( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			NRProteinDTO protein, 
			int position ) throws Exception {
		

		Map<Integer, SearchProtein> searchProtein_KeyOn_NRSEQ_ID_Map = new HashMap<>();
		
		RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
		
		List<SearchPeptideMonolinkAnnDataWrapper> searchPeptideMonolinkAnnDataWrapper_List = 
				SearchPeptideMonolink_LinkedPosition_Searcher.getInstance()
				.searchOnSearchProteinMonolink( 
						search.getId(), 
						searcherCutoffValuesSearchLevel, 
						protein.getNrseqId(), 
						position );

		for ( SearchPeptideMonolinkAnnDataWrapper searchPeptideMonolinkAnnDataWrapper : searchPeptideMonolinkAnnDataWrapper_List ) {
		
			SearchPeptideMonolink searchPeptideMonolink = searchPeptideMonolinkAnnDataWrapper.getSearchPeptideMonolink();
			
			WebReportedPeptide webReportedPeptide = new WebReportedPeptide();
			
			webReportedPeptide.setSearch( search );
			webReportedPeptide.setSearchId( search.getId() );
			webReportedPeptide.setReportedPeptideId( searchPeptideMonolink.getReportedPeptideId() );
			
			webReportedPeptide.setNumPsms( searchPeptideMonolink.getNumPsms() );
			
			webReportedPeptide.setSearchPeptideMonolink( searchPeptideMonolink );
			
			
			WebReportedPeptideWrapper wrappedPeptidelink = new WebReportedPeptideWrapper();
			
			wrappedPeptidelink.setWebReportedPeptide( webReportedPeptide );
			
			wrappedPeptidelink.setPeptideAnnotationDTOMap( searchPeptideMonolinkAnnDataWrapper.getPeptideAnnotationDTOMap() );
			wrappedPeptidelink.setPsmAnnotationDTOMap( searchPeptideMonolinkAnnDataWrapper.getPsmAnnotationDTOMap() );

			repPept_Stage_1_Wrapper.webReportedPeptideWrapperList.add( wrappedPeptidelink );

		}
		
		SearchProteinMonolinkWrapper searchProteinMonolinkWrapper = 
				populateSearchProteinMonolinkWrapper(
						search, 
						searcherCutoffValuesSearchLevel, 
						protein.getNrseqId(), 
						position, 
						searchProtein_KeyOn_NRSEQ_ID_Map, 
						repPept_Stage_1_Wrapper );
		
		return searchProteinMonolinkWrapper;
	}


	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param searchId
	 * @param searchProtein_KeyOn_NRSEQ_ID_Map
	 * @param proteinId
	 * @param proteinPosition
	 * @param repPept_Stage_1_Wrapper
	 * @return
	 * @throws Exception
	 */
	private SearchProteinMonolinkWrapper populateSearchProteinMonolinkWrapper(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			Integer proteinId, 
			Integer proteinPosition,
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
		
		

		SearchProteinMonolinkWrapper searchProteinMonolinkWrapper = new SearchProteinMonolinkWrapper();

		SearchProteinMonolink searchProteinMonolink = new SearchProteinMonolink();
		searchProteinMonolinkWrapper.setSearchProteinMonolink( searchProteinMonolink );

		searchProteinMonolinkWrapper.setPsmAnnotationDTOMap( bestPsmAnnotationDTOMap );
		searchProteinMonolinkWrapper.setPeptideAnnotationDTOMap( bestPeptideAnnotationDTOMap );

		
		
		searchProteinMonolink.setSearch( search );
		searchProteinMonolink.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );


		SearchProtein searchProtein = searchProtein_KeyOn_NRSEQ_ID_Map.get( proteinId );


		if ( searchProtein == null ) {

			searchProtein = new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( proteinId ) );

			searchProtein_KeyOn_NRSEQ_ID_Map.put( proteinId, searchProtein );
		}

		searchProteinMonolink.setProtein( searchProtein );


		searchProteinMonolink.setProteinPosition( proteinPosition );


		searchProteinMonolink.setNumPsms( numPsms );
		searchProteinMonolink.setNumPeptides( numLinkedPeptides );
		searchProteinMonolink.setNumUniquePeptides( numUniqueLinkedPeptides );
		
		searchProteinMonolink.setAssociatedReportedPeptideIds( reportedPeptideIds );
		searchProteinMonolink.setAssociatedReportedPeptideIdsRelatedPeptidesUnique( reportedPeptideIdsRelatedPeptidesUnique );


		if ( searchProteinMonolink.getNumPsms() <= 0 ) {

			//  !!!!!!!   Number of PSMs is zero this this isn't really a Protein that meets the cutoffs

			return null;  //  EARY EXIT
		}

		
		return searchProteinMonolinkWrapper;
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

package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosCommonIF;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosCrosslinkDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosDimerDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosUnlinkedDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideProteinSequencePositionDimerSearcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideProteinSequencePositionUnlinkedSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

/**
 * Common across All Protein All pages and downloads
 *
 */
public class ProteinsAllCommonAll {

	private static final Logger log = LoggerFactory.getLogger(  ProteinsAllCommonAll.class );
	private ProteinsAllCommonAll() { }
	public static ProteinsAllCommonAll getInstance() { 
		return new ProteinsAllCommonAll(); 
	}

	private static enum PeptidePsm { PEPTIDE, PSM }
	
	/**
	 * returned from getProteinSingleEntryList(...)
	 */
	public static class ProteinsAllCommonAllResult {
		
		private List<ProteinSingleEntry> proteinSingleEntryList;
		private Set<SearchProtein> searchProteinUnfilteredForSearch;
		
		public List<ProteinSingleEntry> getProteinSingleEntryList() {
			return proteinSingleEntryList;
		}
		public void setProteinSingleEntryList(List<ProteinSingleEntry> proteinSingleEntryList) {
			this.proteinSingleEntryList = proteinSingleEntryList;
		}
		public Set<SearchProtein> getSearchProteinUnfilteredForSearch() {
			return searchProteinUnfilteredForSearch;
		}
		public void setSearchProteinUnfilteredForSearch(Set<SearchProtein> searchProteinUnfilteredForSearch) {
			this.searchProteinUnfilteredForSearch = searchProteinUnfilteredForSearch;
		}
	}

	/**
	 * @param onlyReturnThisproteinSequenceVersionId
	 * @param search
	 * @param searchId
	 * @param proteinQueryJSONRoot
	 * @param excludeTaxonomy_Ids_Set_UserInput
	 * @param excludeproteinSequenceVersionIds_Set_UserInput
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	public ProteinsAllCommonAllResult getProteinSingleEntryList(
			Integer onlyReturnThisproteinSequenceVersionId, 
			SearchDTO search, 
			int searchId,
			ProteinQueryJSONRoot proteinQueryJSONRoot, 
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput,
			Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel
			) throws Exception, ProxlWebappDataException {
		
		Set<SearchProtein> searchProteinUnfilteredForSearch = new HashSet<SearchProtein>();

		//   Get Link Type 
		String[] linkTypesInForm = proteinQueryJSONRoot.getLinkTypes();
		/////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( linkTypesInForm );

		//////////////////////////////////////////////////////////////
		//  Get Peptides from DATABASE
		List<WebReportedPeptideWrapper> wrappedPeptidelinks =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						search, 
						searcherCutoffValuesSearchLevel, 
						linkTypesForDBQuery, 
						null /* modMassSelections */, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

		//////////////////////////////////////////////////////////////////
		
		// Filter out links if requested, and Update PSM counts if "remove non-unique PSMs" selected 
		
		if( proteinQueryJSONRoot != null && proteinQueryJSONRoot.isRemoveNonUniquePSMs() ) { // Uncomment inner 'if' if add more conditions here.
			///////  Output Lists, Results After Filtering
			List<WebReportedPeptideWrapper> wrappedlinksAfterFilter = new ArrayList<>( wrappedPeptidelinks.size() );

			///  Filter links
			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedPeptidelinks ) {
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				
				//  Comment out this 'if' since only single condition in outer 'if'
				// did the user request to removal of links with only Non-Unique PSMs?
//				if( proteinQueryJSONRoot != null && proteinQueryJSONRoot.isRemoveNonUniquePSMs()  ) {
					//  Update webReportedPeptide object to remove non-unique PSMs
					webReportedPeptide.updateNumPsmsToNotInclude_NonUniquePSMs();
					if ( webReportedPeptide.getNumPsms() <= 0 ) {
						// The number of PSMs after update is now zero
						//  Skip to next entry in list, dropping this entry from output list
						continue;  // EARLY CONTINUE
					}
//				}
				wrappedlinksAfterFilter.add( webReportedPeptideWrapper );
			}

			wrappedPeptidelinks = wrappedlinksAfterFilter;
		}
		
		//  Process Reported Peptide records to get Proteins
		
		Cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId =
				Cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId.getInstance();
		Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId =
				Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId.getInstance();
		
		ProteinWithBestPeptideAndPSMAnnDataContainer proteinWithBestPeptideAndPSMAnnDataContainer = new ProteinWithBestPeptideAndPSMAnnDataContainer();
		
		for ( WebReportedPeptideWrapper wrappedPeptidelink : wrappedPeptidelinks ) {
			
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
				SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_ReqParams reqParams = new SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_ReqParams();
				reqParams.setSearchId( searchId );
				reqParams.setReportedPeptideId( reportedPeptideId );
				SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result result =
						cached_SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId.getSrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result( reqParams );
				List<SrchRepPeptProtSeqIdPosCrosslinkDTO> srchRepPeptProtSeqIdPosCrosslinkDTOList = result.getSrchRepPeptProtSeqIdPosCrosslinkDTOList();
				proteinWithBestPeptideAndPSMAnnDataContainer.addProteinEntriesForReportedPeptide(
						onlyReturnThisproteinSequenceVersionId,
						wrappedPeptidelink, 
						srchRepPeptProtSeqIdPosCrosslinkDTOList, 
						searcherCutoffValuesSearchLevel, 
						search ) ;
			} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
				SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams reqParams = new SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams();
				reqParams.setSearchId( searchId );
				reqParams.setReportedPeptideId( reportedPeptideId );
				SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result result =
						cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId.getSrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result( reqParams );
				List<SrchRepPeptProtSeqIdPosLooplinkDTO> srchRepPeptProtSeqIdPosLooplinkDTOList =
						result.getSrchRepPeptProtSeqIdPosLooplinkDTOList();
				proteinWithBestPeptideAndPSMAnnDataContainer.addProteinEntriesForReportedPeptide(
						onlyReturnThisproteinSequenceVersionId,
						wrappedPeptidelink, 
						srchRepPeptProtSeqIdPosLooplinkDTOList, 
						searcherCutoffValuesSearchLevel, 
						search ) ;
			} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
				List<SrchRepPeptProtSeqIdPosUnlinkedDTO> srchRepPeptProtSeqIdPosUnlinkedDTOList = 
						SearchReportedPeptideProteinSequencePositionUnlinkedSearcher.getInstance()
						.getSrchRepPeptProtSeqIdPosUnlinkedDTOList( searchId, reportedPeptideId );
				proteinWithBestPeptideAndPSMAnnDataContainer.addProteinEntriesForReportedPeptide(
						onlyReturnThisproteinSequenceVersionId,
						wrappedPeptidelink, 
						srchRepPeptProtSeqIdPosUnlinkedDTOList, 
						searcherCutoffValuesSearchLevel, 
						search ) ;
			} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
				List<SrchRepPeptProtSeqIdPosDimerDTO> srchRepPeptProtSeqIdPosDimerDTOList = 
						SearchReportedPeptideProteinSequencePositionDimerSearcher.getInstance()
						.getSrchRepPeptProtSeqIdPosDimerDTOList( searchId, reportedPeptideId );
				proteinWithBestPeptideAndPSMAnnDataContainer.addProteinEntriesForReportedPeptide(
						onlyReturnThisproteinSequenceVersionId,
						wrappedPeptidelink, 
						srchRepPeptProtSeqIdPosDimerDTOList, 
						searcherCutoffValuesSearchLevel, 
						search ) ;
			} else {
				String msg = "Reported peptide is none of expected link types.  Reported Peptide Id: " + reportedPeptideId
						+ ", search id: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
		}
		List<ProteinSingleEntry> proteinSingleEntryListBeforeFilter = proteinWithBestPeptideAndPSMAnnDataContainer.getProteinSingleEntryList();

		// all possible proteins included in this search for this type
		for ( ProteinSingleEntry proteinSingleEntry : proteinSingleEntryListBeforeFilter ) {
			searchProteinUnfilteredForSearch.add( proteinSingleEntry.getSearchProtein() );
		}

		//////////////////////////////////////////////////////////////////
		// Filter out links if requested
		if( proteinQueryJSONRoot.isFilterNonUniquePeptides() 
				|| proteinQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT 
				|| proteinQueryJSONRoot.isFilterOnlyOnePeptide()
				|| ( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) ||
				( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) ) {
		
			///  Filter Protein Entries
			for ( ProteinSingleEntry proteinSingleEntry : proteinSingleEntryListBeforeFilter ) {
				// did they request to removal of non unique peptides?
				if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
					if( proteinSingleEntry.getNumUniquePeptides() < 1 ) {
						//  Drop this entry from output list
						proteinWithBestPeptideAndPSMAnnDataContainer.removeForproteinSequenceVersionId( proteinSingleEntry.getProteinSequenceVersionId() );
						continue;  // EARLY CONTINUE
					}
				}
				// did they request to removal of links with less than a specified number of PSMs?
				if( proteinQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT  ) {
					int psmCountForSearchId = proteinSingleEntry.getNumPsms();
					if ( psmCountForSearchId < proteinQueryJSONRoot.getMinPSMs() ) {
						//  Drop this entry from output list
						proteinWithBestPeptideAndPSMAnnDataContainer.removeForproteinSequenceVersionId( proteinSingleEntry.getProteinSequenceVersionId() );
						continue;  // EARLY CONTINUE
					}
				}
				// did they request to removal of links with only one Reported Peptide?
				if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
					int peptideCountForSearchId = proteinSingleEntry.getNumPeptides();
					if ( peptideCountForSearchId <= 1 ) {
						//  Drop this entry from output list
						proteinWithBestPeptideAndPSMAnnDataContainer.removeForproteinSequenceVersionId( proteinSingleEntry.getProteinSequenceVersionId() );
						continue;  // EARLY CONTINUE
					}
				}
				// did user request removal of certain taxonomy IDs?
				if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
					boolean excludeOnProtein =
							ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
							.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
									excludeTaxonomy_Ids_Set_UserInput, 
									proteinSingleEntry.getSearchProtein().getProteinSequenceVersionObject(), 
									searchId );
					if ( excludeOnProtein ) {
						//  Drop this entry from output list
						proteinWithBestPeptideAndPSMAnnDataContainer.removeForproteinSequenceVersionId( proteinSingleEntry.getProteinSequenceVersionId() );
						continue;  // EARLY CONTINUE
					}
				}
				// did user request removal of certain protein IDs?
				if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
					int proteinId = proteinSingleEntry.getSearchProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId ) ) {
						//  Drop this entry from output list
						proteinWithBestPeptideAndPSMAnnDataContainer.removeForproteinSequenceVersionId( proteinSingleEntry.getProteinSequenceVersionId() );
						continue;  // EARLY CONTINUE
					}
				}								
			}
		}
		
		List<ProteinSingleEntry> proteinSingleEntryList = proteinWithBestPeptideAndPSMAnnDataContainer.getProteinSingleEntryList();
		
		ProteinsAllCommonAllResult proteinsAllCommonAllResult = new ProteinsAllCommonAllResult();
		
		proteinsAllCommonAllResult.setProteinSingleEntryList( proteinSingleEntryList );
		proteinsAllCommonAllResult.setSearchProteinUnfilteredForSearch( searchProteinUnfilteredForSearch );

		return proteinsAllCommonAllResult;
	}

	

    /**
     * 
     *
     */
    public class ProteinWithBestPeptideAndPSMAnnDataContainer  {

    	private Map<Integer, ProteinSingleEntry> entryMap = new HashMap<>();
    	
    	public List<ProteinSingleEntry> getProteinSingleEntryList() {
    		List<ProteinSingleEntry> proteinSingleEntryList = new ArrayList<>( entryMap.size() );
    		for ( Map.Entry<Integer, ProteinSingleEntry> entry : entryMap.entrySet() ) {
    			proteinSingleEntryList.add( entry.getValue() );
    		}
    		return proteinSingleEntryList;
    	}
    	
    	/**
    	 * @param proteinSequenceVersionId
    	 */
    	public void removeForproteinSequenceVersionId( int proteinSequenceVersionId ) {
    		
    		entryMap.remove( proteinSequenceVersionId );
    	}
    	

    	/**
    	 * @param onlyReturnThisproteinSequenceVersionId
    	 * @param wrappedPeptidelink
    	 * @param protSeqCommonList
    	 * @param searcherCutoffValuesSearchLevel
    	 * @param search
    	 * @throws Exception
    	 */
    	public void addProteinEntriesForReportedPeptide(  
    			Integer onlyReturnThisproteinSequenceVersionId,
    			WebReportedPeptideWrapper wrappedPeptidelink,
    			List<? extends SrchRepPeptProtSeqIdPosCommonIF> protSeqCommonList,
    			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
    			SearchDTO search
    			) throws Exception {
    		
			Set<Integer> processedproteinSequenceVersionId = new HashSet<Integer>();
			for ( SrchRepPeptProtSeqIdPosCommonIF srchRepPeptProtSeqIdPosCommon : protSeqCommonList ) {
				int proteinSequenceVersionId = srchRepPeptProtSeqIdPosCommon.getProteinSequenceVersionId();
				if ( onlyReturnThisproteinSequenceVersionId == null 
						|| onlyReturnThisproteinSequenceVersionId == proteinSequenceVersionId ) {
					if ( processedproteinSequenceVersionId.add( proteinSequenceVersionId ) ) {
						// Only process a proteinSequenceVersionId once for a wrappedPeptidelink
						addProteinEntry( proteinSequenceVersionId, wrappedPeptidelink, searcherCutoffValuesSearchLevel, search );
					}
				}
			}
    	}
    	
    	/**
    	 * @param proteinSequenceVersionId
    	 * @param wrappedPeptidelink
    	 * @throws Exception 
    	 */
    	private void addProteinEntry( 
    			Integer proteinSequenceVersionId, 
    			WebReportedPeptideWrapper wrappedPeptidelink,
    			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
    			SearchDTO search ) throws Exception {
    		
    		ProteinSingleEntry entry = entryMap.get( proteinSequenceVersionId );
    		if ( entry == null ) {
    			entry = new ProteinSingleEntry();
    			entry.init( proteinSequenceVersionId, wrappedPeptidelink, search );
    			entryMap.put( proteinSequenceVersionId, entry );
    		} else {
    			entry.updateForWebReportedPeptideWrapper( wrappedPeptidelink, searcherCutoffValuesSearchLevel, search );
    		}
    	}
    	
    }
	
	
    /**
     * 
     *
     */
    public class ProteinSingleEntry extends SortDisplayRecordsWrapperBase {

    	private int proteinSequenceVersionId;
    	
    	private SearchProtein searchProtein;

    	private int numPsms = 0;
    	private int numPeptides = 0;
    	private int numUniquePeptides = 0;
    	
		Set<Integer> reportedPeptideIds = new HashSet<>();
		Set<Integer> reportedPeptideIdsRelatedPeptidesUnique = new HashSet<>();
    	
    	//  Referenced in base class SortDisplayRecordsWrapperBase via abstract get and set methods

		private List<String> psmAnnotationValueList;
		private List<String> peptideAnnotationValueList;
		
		
    	//  In base class SortDisplayRecordsWrapperBase
    	
//    	/**
//    	 * PSM annotation data 
//    	 * 
//    	 * Map keyed on annotation type id of annotation data 
//    	 */
//    	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;
//    	/**
//    	 * Peptide annotation data
//    	 * 
//    	 * Map keyed on annotation type id of annotation data 
//    	 */
//    	private Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap;
    	

    	/**
    	 * @param wrappedPeptidelink
    	 * @param searcherCutoffValuesSearchLevel
    	 * @throws Exception 
    	 */
    	public void init( 
    			int proteinSequenceVersionId,
    			WebReportedPeptideWrapper wrappedPeptidelink,
    			SearchDTO search ) throws Exception {
    		
			this.proteinSequenceVersionId = proteinSequenceVersionId;
			this.searchProtein = new SearchProtein( search, ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinSequenceVersionId ) );

    		this.setPeptideAnnotationDTOMap( wrappedPeptidelink.getPeptideAnnotationDTOMap() );
			this.setPsmAnnotationDTOMap( wrappedPeptidelink.getPsmAnnotationDTOMap() );

			int searchId = search.getSearchId();
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			reportedPeptideIds.add( reportedPeptideId );
			
			numPsms = webReportedPeptide.getNumPsms();
	    	numPeptides = 1;
	    	
	    	boolean areRelatedPeptidesUnique = areRelatedPeptidesUniqueForWebReportedPeptideWrapperSearch( reportedPeptideId, searchId );
	    	if ( areRelatedPeptidesUnique ) {
	    		numUniquePeptides = 1;
				reportedPeptideIdsRelatedPeptidesUnique.add( reportedPeptideId );
	    	}
    	}
    	
    	/**
    	 * @param reportedPeptideId
    	 * @param searchId
    	 * @return
    	 * @throws Exception
    	 */
    	private boolean areRelatedPeptidesUniqueForWebReportedPeptideWrapperSearch( int reportedPeptideId, int searchId ) throws Exception {
    		
			Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request =
					new Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request();
			related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request.setSearchId( searchId );
			related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request.setReportedPeptideId( reportedPeptideId );
			Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Result relatedResult =
					Cached_Related_peptides_unique_for_search_For_SearchId_ReportedPeptideId.getInstance()
					.getRelated_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Result( related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Request );
			boolean areRelatedPeptidesUnique = relatedResult.isRelated_peptides_unique();
			return areRelatedPeptidesUnique;
    	}

    	/**
    	 * @param wrappedPeptidelink
    	 * @param searcherCutoffValuesSearchLevel
    	 * @throws Exception 
    	 */
    	public void updateForWebReportedPeptideWrapper( 
    			WebReportedPeptideWrapper webReportedPeptideWrapper,
    			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
    			SearchDTO search ) throws Exception {

			int searchId = search.getSearchId();
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			reportedPeptideIds.add( reportedPeptideId );
			
			numPsms += webReportedPeptide.getNumPsms();
	    	numPeptides += 1;
	    	
	    	boolean areRelatedPeptidesUnique = areRelatedPeptidesUniqueForWebReportedPeptideWrapperSearch( reportedPeptideId, searchId );
	    	if ( areRelatedPeptidesUnique ) {
	    		numUniquePeptides += 1;
				reportedPeptideIdsRelatedPeptidesUnique.add( reportedPeptideId );
	    	}
	    	
			updateBestAnnotationValues( 
					this.getPsmAnnotationDTOMap(), 
					webReportedPeptideWrapper.getPsmAnnotationDTOMap(), 
					PeptidePsm.PSM,
					searcherCutoffValuesSearchLevel );
			updateBestAnnotationValues( 
					this.getPeptideAnnotationDTOMap(), 
					webReportedPeptideWrapper.getPeptideAnnotationDTOMap(),
					PeptidePsm.PEPTIDE,
					searcherCutoffValuesSearchLevel );

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

		public int getProteinSequenceVersionId() {
			return proteinSequenceVersionId;
		}
		public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
			this.proteinSequenceVersionId = proteinSequenceVersionId;
		}
		@Override
		public List<String> getPsmAnnotationValueList() {
			return psmAnnotationValueList;
		}
		@Override
		public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
			this.psmAnnotationValueList = psmAnnotationValueList;
		}
		@Override
		public List<String> getPeptideAnnotationValueList() {
			return peptideAnnotationValueList;
		}
		@Override
		public void setPeptideAnnotationValueList(List<String> peptideAnnotationValueList) {
			this.peptideAnnotationValueList = peptideAnnotationValueList;
		}
		@Override
		public int getFinalSortOrderKey() {
			return proteinSequenceVersionId;
		}
		public SearchProtein getSearchProtein() {
			return searchProtein;
		}
		public void setSearchProtein(SearchProtein searchProtein) {
			this.searchProtein = searchProtein;
		}
		public int getNumPsms() {
			return numPsms;
		}
		public void setNumPsms(int numPsms) {
			this.numPsms = numPsms;
		}
		public int getNumPeptides() {
			return numPeptides;
		}
		public void setNumPeptides(int numPeptides) {
			this.numPeptides = numPeptides;
		}
		public int getNumUniquePeptides() {
			return numUniquePeptides;
		}
		public void setNumUniquePeptides(int numUniquePeptides) {
			this.numUniquePeptides = numUniquePeptides;
		}
		public Set<Integer> getReportedPeptideIds() {
			return reportedPeptideIds;
		}
		public Set<Integer> getReportedPeptideIdsRelatedPeptidesUnique() {
			return reportedPeptideIdsRelatedPeptidesUnique;
		}
		
    }
	
}

package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataInSortOrder;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.SearchPeptideCommonLinkAnnDataWrapperIF;
import org.yeastrc.xlink.www.objects.SearchPeptideCommonLinkWebserviceResultIF;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideAnnotationDataSearcher;



/**
 * Common code for the web services getting peptide level link data:
 * ReportedPeptides_Crosslink_Service, ReportedPeptides_Looplink_Service, ReportedPeptides_Monolink_Service
 * 
 *
 */
public class SearchPeptideWebserviceCommonCode {

	private static final Logger log = Logger.getLogger( SearchPeptideWebserviceCommonCode.class );
	
	private static final SearchPeptideWebserviceCommonCode instance = new SearchPeptideWebserviceCommonCode();

	private SearchPeptideWebserviceCommonCode() { }
	public static SearchPeptideWebserviceCommonCode getInstance() { return instance; }


	
	/**
	 * Result from getPeptideAndPSMDataForLinksAndSortLinks(...) method
	 *
	 */
	public static class SearchPeptideWebserviceCommonCodeGetDataResult {
		
		//  Passed to the put... method in this class 
		
		private List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List;
		private Map<Integer, AnnotationTypeDTO> peptideDescriptiveAnnotationTypesForSearchId;
		private List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList;
		
		//  Passed to the calling class
		
		private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;
		private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;
		
		public List<AnnotationDisplayNameDescription> getPeptideAnnotationDisplayNameDescriptionList() {
			return peptideAnnotationDisplayNameDescriptionList;
		}
		public List<AnnotationDisplayNameDescription> getPsmAnnotationDisplayNameDescriptionList() {
			return psmAnnotationDisplayNameDescriptionList;
		}
	}
	
	/**
	 * @param searchId
	 * @param searchPeptideCommonLinkWrappedList
	 * @param searcherCutoffValuesSearchLevel
	 * @param peptideAnnotationTypeIdsToDisplay
	 * @return
	 * @throws Exception
	 */
	public SearchPeptideWebserviceCommonCodeGetDataResult getPeptideAndPSMDataForLinksAndSortLinks( 
			
			int searchId, 
			List<? extends SearchPeptideCommonLinkAnnDataWrapperIF> searchPeptideCommonLinkWrappedList,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			List<Integer> peptideAnnotationTypeIdsToDisplay 
			
			) throws Exception {
		
		Collection<Integer> searchIdsCollection = new ArrayList<>( 1 );
		searchIdsCollection.add( searchId );
		
		//  Create list of Best PSM annotation names to display as column headers

		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		
		final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );

		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {

			psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
		}
		
		/////////////

		//  Get  Annotation Type records for Reported Peptides
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationTypesForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsCollection );

		Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationTypesForSearchId =
				peptideFilterableAnnotationTypesForSearchIds.get( searchId );


		if ( peptideFilterableAnnotationTypesForSearchId == null ) {
			
			peptideFilterableAnnotationTypesForSearchId = new HashMap<>();
			
//			String msg = "peptideFilterableAnnotationTypesForSearchId == null for searchId: " + searchId;
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
		}
		

		Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideDescriptiveAnnotationTypesForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Descriptive_ForSearchIds( searchIdsCollection );

		Map<Integer, AnnotationTypeDTO> peptideDescriptiveAnnotationTypesForSearchId =
				peptideDescriptiveAnnotationTypesForSearchIds.get( searchId );

		if ( peptideDescriptiveAnnotationTypesForSearchId == null ) {
			peptideDescriptiveAnnotationTypesForSearchId = new HashMap<>();
//			String msg = "peptideDescriptiveAnnotationTypesForSearchId == null for searchId: " + searchId;
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
		}
				
		/////////////

		//   Get Peptide Annotation Types Map of Lists which are Sorted on Sort Order 


		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_SortOrder_MainMap =
				GetAnnotationTypeDataInSortOrder.getInstance()
				.getPeptide_AnnotationTypeDataInSortOrder( searchIdsCollection );
		
		if ( peptideAnnotationTypeDTO_SortOrder_MainMap.size() != 1 ) {
			String msg = "getPeptide_AnnotationTypeDataInSortOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List = peptideAnnotationTypeDTO_SortOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
				

		/////////////
		
		//   Get Peptide Annotation Types Map of Lists which are Sorted on Display Order 
		
		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
				.getPeptideAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsCollection );
		

		if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.size() != 1 ) {
			
			String msg = "getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList = peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
						

		if ( peptideAnnotationTypeIdsToDisplay != null ) {

			//   Alter reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List for User Input Include displayed data 

			//   Remove from AnnotationTypeDTO List not on Include AnnTypeId  
			//   ( the entries in annTypeIdDisplayPsm.getInclAnnTypeId() )

			Iterator<AnnotationTypeDTO> annotationTypeDTO_ListIter = 
					reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList.iterator();
			
			while ( annotationTypeDTO_ListIter.hasNext() ) {
					
				AnnotationTypeDTO annotationTypeDTO = annotationTypeDTO_ListIter.next();				
				
				boolean found = false;
				for ( int includeAnnTypeId : peptideAnnotationTypeIdsToDisplay ) {
					
					if ( includeAnnTypeId == annotationTypeDTO.getId() ) {
						
						found = true;
						break;
					}
				}
				
				if ( ! found ) {
					annotationTypeDTO_ListIter.remove();
				}
			}
		}

		/////////////////////////////////////////
		
		///   Create sets of annotation type ids that were searched for but are not displayed by default.
		///   Those annotation values will be displayed after the default, in name order
		
		Set<Integer> annotationTypesToAddToOutputAnnotationData = new HashSet<>();
		

		/////////////////////////////////////////
		
		//  Do ONLY if no "Include" Ann Type Ids
		
		if ( peptideAnnotationTypeIdsToDisplay == null ) {

			//  Add to Annotation Types displayed the annotation types the user "queries" on (has cutoff values)
			
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerAnnotationIdList =
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();

			for (  SearcherCutoffValuesAnnotationLevel peptideCutoffEntry : peptideCutoffValuesPerAnnotationIdList ) {

				int annTypeId = peptideCutoffEntry.getAnnotationTypeId();
				annotationTypesToAddToOutputAnnotationData.add( annTypeId );
			}
		}

		if ( peptideAnnotationTypeIdsToDisplay != null ) {

			//  Add Included ann type ids
			
			//   Add to annotationTypeIds  the Added annotation type ids
			//   ( the entries in peptideAnnotationTypeIdsToDisplay )

			for ( int includeAnnTypeId : peptideAnnotationTypeIdsToDisplay ) {

				annotationTypesToAddToOutputAnnotationData.add( includeAnnTypeId );
			}
		}

		// Remove annotation type ids that are in default display

		for ( AnnotationTypeDTO item : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList ) {

			annotationTypesToAddToOutputAnnotationData.remove( item.getId() );
		}

		//  Get AnnotationTypeDTO for ids not in default display and sort in name order
		
		List<AnnotationTypeDTO> peptideAnnotationTypesToAddFromQuery = new ArrayList<>();
		
		if ( ! annotationTypesToAddToOutputAnnotationData.isEmpty() ) {
			
			//   Add in Peptide annotation types the user searched for
			
			
			for ( Integer peptideAnnotationTypeToAdd : annotationTypesToAddToOutputAnnotationData ) {
			
				AnnotationTypeDTO annotationTypeDTO = peptideFilterableAnnotationTypesForSearchId.get( peptideAnnotationTypeToAdd );

				if ( annotationTypeDTO == null ) {
					
					
				}
				
				peptideAnnotationTypesToAddFromQuery.add( annotationTypeDTO );
			}
			
			// sort on ann type name
			Collections.sort( peptideAnnotationTypesToAddFromQuery, new Comparator<AnnotationTypeDTO>() {

				@Override
				public int compare(AnnotationTypeDTO o1,
						AnnotationTypeDTO o2) {

					return o1.getName().compareTo( o2.getName() );
				}
			} );
		}
		
		//   Add the searched for but not in default display AnnotationTypeDTO 
		//   to the default display list.
		//   The annotation data will be loaded from the DB in the searcher since they were searched for
		
		for ( AnnotationTypeDTO annotationTypeDTO : peptideAnnotationTypesToAddFromQuery ) {
			
			reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList.add( annotationTypeDTO );
		}
		

		/////////////////////////////////////////
		
		//  Do ONLY if "Include" Ann Type Ids
		
		if ( peptideAnnotationTypeIdsToDisplay != null 
				&& ( ! peptideAnnotationTypeIdsToDisplay.isEmpty() ) ) {
			
			//  Change the psm_AnnotationTypeDTO_DefaultDisplay_List:
			//  Only contain the annotation type ids in  annTypeIdDisplayPsm.getInclAnnTypeId()
			//  Sorted in the order of annTypeIdDisplayPsm.getInclAnnTypeId()
			
			List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_OnlyIncludedAnnTypeIds = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList.size() );
			
			for ( int includedAnnotationTypeId : peptideAnnotationTypeIdsToDisplay ) {
				
				AnnotationTypeDTO foundAnnotationTypeDTO = null;
				
				for ( AnnotationTypeDTO listItem : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList ) {
					
					if ( listItem.getId() == includedAnnotationTypeId ) {
						
						foundAnnotationTypeDTO = listItem;
						break;
					}
				}

				if ( foundAnnotationTypeDTO == null ) {
					
					String msg = "No AnnotationTypeDTO found for includedAnnotationTypeId: " + includedAnnotationTypeId;
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}
				
				reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_OnlyIncludedAnnTypeIds.add( foundAnnotationTypeDTO );
			}
			
			
			reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList = reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_OnlyIncludedAnnTypeIds;
		}
		
		
		
		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List =
				reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List_workingList;

		/////////////////////

		//  Get set of Peptide annotation type ids for getting annotation data
		
		Set<Integer> annotationTypeIdsForAnnotationDataRetrieval = new HashSet<>();

		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {
			
			annotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeDTO.getId() );
		}
		
		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
			
			annotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeDTO.getId() );
		}
		
		//  Get Annotation data
		
		for ( SearchPeptideCommonLinkAnnDataWrapperIF searchPeptideCommonLinkAnnDataWrapperIFItem : searchPeptideCommonLinkWrappedList ) {
		
			int reportedPeptideId = searchPeptideCommonLinkAnnDataWrapperIFItem.getReportedPeptideId();

			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = new HashMap<>();

			//  Process annotation type ids to get annotation data
			{
				List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDataList = 
						SearchReportedPeptideAnnotationDataSearcher.getInstance()
						.getSearchReportedPeptideAnnotationDTOList( searchId, reportedPeptideId, annotationTypeIdsForAnnotationDataRetrieval );

				for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideFilterableAnnotationDataItem : searchReportedPeptideFilterableAnnotationDataList ) {

					peptideAnnotationDTOMap.put( searchReportedPeptideFilterableAnnotationDataItem.getAnnotationTypeId(), searchReportedPeptideFilterableAnnotationDataItem );
				}
			}
			searchPeptideCommonLinkAnnDataWrapperIFItem.setPeptideAnnotationDTOMap( peptideAnnotationDTOMap );
		}


		//  Sort Peptide records on sort order, then best PSM values

		Collections.sort( searchPeptideCommonLinkWrappedList, new Comparator<SearchPeptideCommonLinkAnnDataWrapperIF>() {

			@Override
			public int compare(SearchPeptideCommonLinkAnnDataWrapperIF o1, SearchPeptideCommonLinkAnnDataWrapperIF o2) {

				//  Process the Peptide annotation types (sorted on sort order), comparing the values

				for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {

					Integer typeId = srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();

					AnnotationDataBaseDTO o1_SearchReportedPeptideAnnotationDTO = o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_SearchReportedPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o1Value = o1_SearchReportedPeptideAnnotationDTO.getValueDouble();

					AnnotationDataBaseDTO o2_SearchReportedPeptideAnnotationDTO = o2.getPeptideAnnotationDTOMap().get( typeId );
					if ( o2_SearchReportedPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o2Value = o2_SearchReportedPeptideAnnotationDTO.getValueDouble();
					
					if ( o1Value != o2Value ) {

						if ( o1Value < o2Value ) {

							return -1;
						} else {
							return 1;
						}
					}
				}
				
				//  If everything matches, process the PSM annotation types (sorted on some order), comparing the values

				for ( AnnotationTypeDTO psmAnnotationTypeDTO : psmCutoffsAnnotationTypeDTOList ) {

					Integer typeId = psmAnnotationTypeDTO.getId();

					AnnotationDataBaseDTO o1_PsmPeptideAnnotationDTO = o1.getPsmAnnotationDTOMap().get( typeId );
					if ( o1_PsmPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o1Value = o1_PsmPeptideAnnotationDTO.getValueDouble();

					AnnotationDataBaseDTO o2_PsmPeptideAnnotationDTO = o2.getPsmAnnotationDTOMap().get( typeId );
					if ( o2_PsmPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o2Value = o2_PsmPeptideAnnotationDTO.getValueDouble();

					if ( o1Value != o2Value ) {
						if ( o1Value < o2Value ) {
							return -1;
						} else {
							return 1;
						}
					}
				}

				//  If everything matches, sort on reported peptide id

				try {
					return o1.getReportedPeptideId() - o2.getReportedPeptideId();
				} catch (Exception e) {
					throw new RuntimeException( e );
				}
			}
		});

		//  Put column headers data into output webservice for Peptides

		List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = 
				new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );

		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();
			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );

			peptideAnnotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
		}

		//  Put column headers data into output webservice for PSM

		List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = 
				new ArrayList<>( psmCutoffsAnnotationTypeDTOList.size() );

		for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOList ) {
			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();
			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );

			psmAnnotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
		}

		//  Create method result object, populate, and return
		
		SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult = new SearchPeptideWebserviceCommonCodeGetDataResult();
			
		searchPeptideWebserviceCommonCodeGetDataResult.reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List = reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List;
		searchPeptideWebserviceCommonCodeGetDataResult.peptideDescriptiveAnnotationTypesForSearchId = peptideDescriptiveAnnotationTypesForSearchId;
		searchPeptideWebserviceCommonCodeGetDataResult.psmCutoffsAnnotationTypeDTOList = psmCutoffsAnnotationTypeDTOList;
		
		searchPeptideWebserviceCommonCodeGetDataResult.peptideAnnotationDisplayNameDescriptionList = peptideAnnotationDisplayNameDescriptionList;
		searchPeptideWebserviceCommonCodeGetDataResult.psmAnnotationDisplayNameDescriptionList = psmAnnotationDisplayNameDescriptionList;

		return searchPeptideWebserviceCommonCodeGetDataResult;
	}
	
	

	/**
	 * This method takes a LinkWrappedItem and the results of the get...(...) method 
	 * and updates the LinkWebserviceResultItem 
	 * 
	 * @param searchPeptideWebserviceCommonCodeGetDataResult - result from method getPeptideAndPSMDataForLinks in this class
	 * 
	 * @param searchPeptideCommonLinkWrappedItem -  Incoming data
	 * @param searchPeptideCommonLinkWebserviceResultItem - Outgoing data
	 * @throws Exception 
	 */
	public void putPeptideAndPSMDataOnWebserviceResultLinkOject( 

			SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult,  //  Incoming data

			SearchPeptideCommonLinkAnnDataWrapperIF searchPeptideCommonLinkWrappedItem, //  Incoming data
			SearchPeptideCommonLinkWebserviceResultIF searchPeptideCommonLinkWebserviceResultItem //  Outgoing data
			
			) throws Exception {

		{
			//  Get Peptide annotation values

			List<String> peptideAnnotationValues = new ArrayList<>( searchPeptideWebserviceCommonCodeGetDataResult.reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );

			for ( AnnotationTypeDTO annotationTypeDTO : searchPeptideWebserviceCommonCodeGetDataResult.reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				Integer annotationTypeId = annotationTypeDTO.getId();
			
				AnnotationDataBaseDTO peptideAnnotationDTO = 
						searchPeptideCommonLinkWrappedItem.getPeptideAnnotationDTOMap().get( annotationTypeId );

				String annotationValueString = null;
				
				if ( peptideAnnotationDTO != null ) {

					annotationValueString = peptideAnnotationDTO.getValueString();

				} else {
					
					if ( ! searchPeptideWebserviceCommonCodeGetDataResult.peptideDescriptiveAnnotationTypesForSearchId.containsKey( annotationTypeId ) ) {
						
						String msg = "ERROR.  Cannot find AnnotationDTO for type id: " + annotationTypeDTO.getId();
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					//  Allow Peptide Descriptive Annotations to be missing 
					
					annotationValueString = "";
				}
				peptideAnnotationValues.add( annotationValueString );
			}

			searchPeptideCommonLinkWebserviceResultItem.setPeptideAnnotationValueList( peptideAnnotationValues );
		}
		

		{
			//  Get PSM annotation values

			List<String> psmAnnotationValues = new ArrayList<>( searchPeptideWebserviceCommonCodeGetDataResult.psmCutoffsAnnotationTypeDTOList.size() );

			for ( AnnotationTypeDTO annotationTypeDTO : searchPeptideWebserviceCommonCodeGetDataResult.psmCutoffsAnnotationTypeDTOList ) {

				AnnotationDataBaseDTO psmAnnotationDTO = 
						searchPeptideCommonLinkWrappedItem.getPsmAnnotationDTOMap().get( annotationTypeDTO.getId() );

				if ( psmAnnotationDTO == null ) {

					String msg = "ERROR.  Cannot AnnotationDTO for type id: " + annotationTypeDTO.getId();
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}

				psmAnnotationValues.add( psmAnnotationDTO.getValueString() );
			}

			searchPeptideCommonLinkWebserviceResultItem.setPsmAnnotationValueList( psmAnnotationValues );
		}

	}
}

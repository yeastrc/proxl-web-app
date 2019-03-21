package org.yeastrc.xlink.www.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Only used by Structure page
 *
 */
@Path("/imageViewer")
public class Viewer_UDR_Data_Service {
	
	private static final Logger log = Logger.getLogger(Viewer_UDR_Data_Service.class);
	
	private static final String CROSSLINK_LINK_TYPE = XLinkUtils.CROSS_TYPE_STRING;
	private static final String LOOPLINK_LINK_TYPE = XLinkUtils.LOOP_TYPE_STRING;
	
	public static class Viewer_UDR_Data_Service_Result {
		private List<Viewer_UDR_Data_Service_Result_Item> udrItemList;
		private List<String> bestPeptideValuesNames;
		private List<String> bestPSMValuesNames;
		public List<Viewer_UDR_Data_Service_Result_Item> getUdrItemList() {
			return udrItemList;
		}
		public void setUdrItemList(List<Viewer_UDR_Data_Service_Result_Item> udrItemList) {
			this.udrItemList = udrItemList;
		}
		public List<String> getBestPeptideValuesNames() {
			return bestPeptideValuesNames;
		}
		public void setBestPeptideValuesNames(List<String> bestPeptideValuesNames) {
			this.bestPeptideValuesNames = bestPeptideValuesNames;
		}
		public List<String> getBestPSMValuesNames() {
			return bestPSMValuesNames;
		}
		public void setBestPSMValuesNames(List<String> bestPSMValuesNames) {
			this.bestPSMValuesNames = bestPSMValuesNames;
		}
	}
	
	public static class Viewer_UDR_Data_Service_Result_Item {
		private String linkType;
		private int proteinSeqId_1;
		private int proteinPos_1;
		private int proteinSeqId_2;
		private int proteinPos_2;
		private int numPSMs;
		private int numPeptides;
		private int numUniquePeptides;
		private List<String> bestPeptideValues;
		private List<String> bestPSMValues;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public int getProteinSeqId_1() {
			return proteinSeqId_1;
		}
		public void setProteinSeqId_1(int proteinSeqId_1) {
			this.proteinSeqId_1 = proteinSeqId_1;
		}
		public int getProteinPos_1() {
			return proteinPos_1;
		}
		public void setProteinPos_1(int proteinPos_1) {
			this.proteinPos_1 = proteinPos_1;
		}
		public int getProteinSeqId_2() {
			return proteinSeqId_2;
		}
		public void setProteinSeqId_2(int proteinSeqId_2) {
			this.proteinSeqId_2 = proteinSeqId_2;
		}
		public int getProteinPos_2() {
			return proteinPos_2;
		}
		public void setProteinPos_2(int proteinPos_2) {
			this.proteinPos_2 = proteinPos_2;
		}
		public int getNumPSMs() {
			return numPSMs;
		}
		public void setNumPSMs(int numPSMs) {
			this.numPSMs = numPSMs;
		}
		public int getNumPeptides() {
			return numPeptides;
		}
		public void setNumPeptides(int numPeptides) {
			this.numPeptides = numPeptides;
		}
		public List<String> getBestPeptideValues() {
			return bestPeptideValues;
		}
		public void setBestPeptideValues(List<String> bestPeptideValues) {
			this.bestPeptideValues = bestPeptideValues;
		}
		public List<String> getBestPSMValues() {
			return bestPSMValues;
		}
		public void setBestPSMValues(List<String> bestPSMValues) {
			this.bestPSMValues = bestPSMValues;
		}
		public int getNumUniquePeptides() {
			return numUniquePeptides;
		}
		public void setNumUniquePeptides(int numUniquePeptides) {
			this.numUniquePeptides = numUniquePeptides;
		}
	}

	/**
	 * @param projectSearchIdList
	 * @param psmPeptideCutoffsForProjectSearchIds_JSONString
	 * @param minPSMs
	 * @param filterNonUniquePeptidesString
	 * @param filterOnlyOnePeptideString
	 * @param removeNonUniquePSMsString
	 * @param excludeTaxonomy
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getUDRData") 
	public Viewer_UDR_Data_Service_Result getViewerData(
			@QueryParam( "projectSearchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "minPSMs" ) Integer minPSMs,
			@QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
			@QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
			@QueryParam( "removeNonUniquePSMs" ) String removeNonUniquePSMsString,
			@QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
			@Context HttpServletRequest request )
	throws Exception {
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchId is null or empty, projectSearchId = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( projectSearchIdList.size() != 1 ) {
			String msg = "Provided projectSearchId is more than one search id, projectSearchId = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( minPSMs == null ) {
			String msg = "Provided minPSMs is null or minPSMs is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}

		boolean filterNonUniquePeptides = false;
		if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
			filterNonUniquePeptides = true;
		boolean filterOnlyOnePeptide = false;
		if( "on".equals( filterOnlyOnePeptideString ) )
			filterOnlyOnePeptide = true;
		boolean removeNonUniquePSMs = false;
		if( "on".equals( removeNonUniquePSMsString ) )
			removeNonUniquePSMs = true;

		LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param();
		linkedPositions_FilterExcludeLinksWith_Param.setRemoveNonUniquePSMs( removeNonUniquePSMs );

		try {
			// Get the session first.  
//			HttpSession session = request.getSession();


			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIdList: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			//////////////////   Only 1 project_search id is supported for now:
			
			int projectSearchId = projectSearchIdList.get(0);
			
			Viewer_UDR_Data_Service_Result webserviceResult = new Viewer_UDR_Data_Service_Result();
			List<Viewer_UDR_Data_Service_Result_Item> udrItemList = new ArrayList<>();
			webserviceResult.setUdrItemList( udrItemList );
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			
			if ( search == null ) {
				String msg = "No search record found for projectSearchId: " + projectSearchId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			Integer searchId = search.getSearchId();
			
			Set<Integer> searchIds = new HashSet<>();
			searchIds.add( searchId );
			
			CutoffValuesRootLevel cutoffValuesRootLevel = null;
			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForProjectSearchIds_JSONString, CutoffValuesRootLevel.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', IOException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIds, cutoffValuesRootLevel );
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			////////////
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput = new HashSet<>();
			
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			//  Get Annotation Type records for PSM and Peptide
			//  Get  Annotation Type records for PSM
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
					srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
				//  No records were found, probably an error   TODO
				srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
			}
			//  Get  Annotation Type records for Reported Peptides
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
				//  No records were found, allowable for Reported Peptides
				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			////////////////////////////////////////////////
			//////     Code common to Crosslinks and Looplinks
			//  Create list of Peptide and Best PSM annotation names to display as column headers
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
				peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
				psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			
			/////////////////////////////////////////////////////////////
			//////////////////   Get Crosslinks data from DATABASE  from database
			List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
					CrosslinkLinkedPositions.getInstance()
					.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
			
			////////////////////////////////////
			//////////////////   Get Looplinks data from DATABASE   from database
			List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
					LooplinkLinkedPositions.getInstance()
					.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
			
			// all possible proteins included in this search for this type
			Collection<SearchProtein> prProteins = new HashSet<SearchProtein>();
			for ( SearchProteinCrosslinkWrapper  searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
				SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				prProteins.add( searchProteinCrosslink.getProtein1() );
				prProteins.add( searchProteinCrosslink.getProtein2() );
			}
			for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
				SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
				prProteins.add( searchProteinLooplink.getProtein() );
			}
			//////////////////////////////////////////////////////////////////
			// Filter out links if requested
			if( filterNonUniquePeptides 
					|| minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT
					|| filterOnlyOnePeptide 
					|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 ) ||
					( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) ) {
				///////  Output Lists, Results After Filtering
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );
				List<SearchProteinLooplinkWrapper> wrappedLooplinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );
				///  Filter CROSSLINKS
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides  ) {
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with less than a specified number of PSMs?
					if( minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId < minPSMs ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						int peptideCountForSearchId = link.getNumLinkedPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein_1 =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein1().getProteinSequenceVersionObject(), 
										searchId );
						boolean excludeOnProtein_2 =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein2().getProteinSequenceVersionObject(), 
										searchId );
						if ( excludeOnProtein_1 || excludeOnProtein_2 ) {
//						int taxonomyId_1 = link.getProtein1().getProteinSequenceVersionObject().getTaxonomyId();
//						int taxonomyId_2 = link.getProtein2().getProteinSequenceVersionObject().getTaxonomyId();
//						
//						if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_1 ) 
//								|| excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_2 ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
						int proteinId_1 = link.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						int proteinId_2 = link.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId_1 ) 
								|| excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId_2 ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}		
					wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
				}
				///  Filter LOOPLINKS
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink link = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides  ) {
						if( link.getNumUniquePeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with less than a specified number of PSMs?
					if( minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId < minPSMs ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						int peptideCountForSearchId = link.getNumPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein().getProteinSequenceVersionObject(), 
										searchId );
						if ( excludeOnProtein ) {
//						int taxonomyId = link.getProtein().getProteinSequenceVersionObject().getTaxonomyId();
//						
//						if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
						int proteinId = link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}									
					wrappedLooplinksAfterFilter.add( searchProteinLooplinkWrapper );
				}
				//  Copy new filtered lists to original input variable names to overlay them
				wrappedCrosslinks = wrappedCrosslinksAfterFilter;
				wrappedLooplinks = wrappedLooplinksAfterFilter;
			}
			//////////////////////////////////////////////////////////////////
			///////////////////////////
			///   Process Crosslinks and Looplinks to get annotations and sort.
			//              Process Crosslinks or Looplinks, depending on which page is being displayed
//			List<SearchProteinCrosslink> crosslinks = null;
			if ( ! wrappedCrosslinks.isEmpty() ) {
//			if ( ! Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {
//
//
//				//  NOT Crosslink page:    Struts config action mapping:    NOT parameter="crosslink"
//				
//				//  Simply unwrap the crosslinks  
//				
//				crosslinks = new ArrayList<>( wrappedCrosslinks.size() );
//
//				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
//					
//					crosslinks.add( searchProteinCrosslinkWrapper.getSearchProteinCrosslink() );
//				}
//				
//				
//			} else {
				//	For  Struts config action mapping:     parameter="crosslink"
				//  Order so:  ( protein_1 name < protein_2 name) or ( protein_1 name == protein_2 name and pos1 <= pos2 )
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					int protein1CompareProtein2 = searchProteinCrosslink.getProtein1().getName().compareToIgnoreCase( searchProteinCrosslink.getProtein2().getName() );
					if ( protein1CompareProtein2 > 0 
							|| ( protein1CompareProtein2 == 0
								&& searchProteinCrosslink.getProtein1Position() > searchProteinCrosslink.getProtein2Position() ) ) {
						//  Protein_1 name > protein_2 name or ( Protein_1 name == protein_2 name and pos1 > pos2 )
						//  so re-order
						SearchProtein searchProteinTemp = searchProteinCrosslink.getProtein1();
						searchProteinCrosslink.setProtein1( searchProteinCrosslink.getProtein2() );
						searchProteinCrosslink.setProtein2( searchProteinTemp );
						int linkPositionTemp = searchProteinCrosslink.getProtein1Position();
						searchProteinCrosslink.setProtein1Position( searchProteinCrosslink.getProtein2Position() );
						searchProteinCrosslink.setProtein2Position( linkPositionTemp );
					}
				}
				//  Sort "wrappedCrosslinks" since removed ORDER BY from SQL
//				Collections.sort( wrappedCrosslinks, new Comparator<SearchProteinCrosslinkWrapper>() {
//
//					@Override
//					public int compare(SearchProteinCrosslinkWrapper o1,
//							SearchProteinCrosslinkWrapper o2) {
//
//						if ( o1.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId()
//								!= o2.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
//							
//							return o1.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
//									- o2.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
//						}
//						
//						if ( o1.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId()
//								!= o2.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
//							
//							return o1.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
//									- o2.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
//						}
//
//						if ( o1.getSearchProteinCrosslink().getProtein1Position()
//								!= o2.getSearchProteinCrosslink().getProtein1Position() ) {
//							
//							return o1.getSearchProteinCrosslink().getProtein1Position() 
//									- o2.getSearchProteinCrosslink().getProtein1Position();
//						}
//
//						return o1.getSearchProteinCrosslink().getProtein2Position() 
//								- o2.getSearchProteinCrosslink().getProtein2Position();
//					}
//				} );
				////   Crosslinks  - Prepare for Web display
				//      Get Annotation data and Sort by Annotation data
				SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortCrosslinksResult =
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
						.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
								searchId, 
								wrappedCrosslinks, 
								peptideCutoffsAnnotationTypeDTOList, 
								psmCutoffsAnnotationTypeDTOList );
//				request.setAttribute( "peptideAnnotationDisplayNameDescriptionList", sortCrosslinksResult.getPeptideAnnotationDisplayNameDescriptionList() );
//				
//				request.setAttribute( "psmAnnotationDisplayNameDescriptionList", sortCrosslinksResult.getPsmAnnotationDisplayNameDescriptionList() );
				List<String> bestPeptideValuesNames = new ArrayList<>();
				List<String> bestPSMValuesNames = new ArrayList<>();
				webserviceResult.setBestPeptideValuesNames( bestPeptideValuesNames );
				webserviceResult.setBestPSMValuesNames( bestPSMValuesNames );
				for ( AnnotationDisplayNameDescription annotationDisplayNameDescription : sortCrosslinksResult.getPeptideAnnotationDisplayNameDescriptionList() ) {
					bestPeptideValuesNames.add( annotationDisplayNameDescription.getDisplayName() );
				}
				for ( AnnotationDisplayNameDescription annotationDisplayNameDescription : sortCrosslinksResult.getPsmAnnotationDisplayNameDescriptionList() ) {
					bestPSMValuesNames.add( annotationDisplayNameDescription.getDisplayName() );
				}
//				crosslinks = new ArrayList<>( wrappedCrosslinks.size() );
				//  Copy of out wrapper for processing below
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					Viewer_UDR_Data_Service_Result_Item item = new Viewer_UDR_Data_Service_Result_Item();
					item.setLinkType( CROSSLINK_LINK_TYPE );
					item.setProteinSeqId_1( searchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					item.setProteinPos_1( searchProteinCrosslink.getProtein1Position() );
					item.setProteinSeqId_2( searchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					item.setProteinPos_2( searchProteinCrosslink.getProtein2Position() );
					item.setNumPeptides( searchProteinCrosslink.getNumLinkedPeptides() );
					item.setNumUniquePeptides( searchProteinCrosslink.getNumUniqueLinkedPeptides() );
					item.setNumPSMs( searchProteinCrosslink.getNumPsms() );
					List<String> bestPeptideValues = new ArrayList<>();
					List<String> bestPSMValues = new ArrayList<>();
					item.setBestPeptideValues( bestPeptideValues );
					item.setBestPSMValues( bestPSMValues );
					for ( String annValue : searchProteinCrosslink.getPeptideAnnotationValueList() ) {
						bestPeptideValues.add( annValue );
					}
					for ( String annValue : searchProteinCrosslink.getPsmAnnotationValueList() ) {
						bestPSMValues.add( annValue );
					}
					udrItemList.add( item );
//					crosslinks.add( searchProteinCrosslink );
				}
//			}
			}
			/////////////////////////////////////////////////////////////
			//////////////////   Get Looplinks data
			if ( ! wrappedLooplinks.isEmpty() ) {
//			List<SearchProteinLooplink> looplinks = null;
//			if ( ! Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {
//
//				//  NOT Looplink page:    Struts config action mapping:    NOT parameter="looplink"
//				
//				//  Simply unwrap the looplinks  
//				
//				looplinks = new ArrayList<>( wrappedLooplinks.size() );
//
//				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
//					
//					looplinks.add( searchProteinLooplinkWrapper.getSearchProteinLooplink() );
//				}
//
//			} else {
				//	For  Struts config action mapping:     parameter="looplink"
				////   Looplinks  - Prepare for Web display
				//      Get Annotation data and Sort by Annotation data
//				looplinks = new ArrayList<>( wrappedLooplinks.size() );
//				Collections.sort( wrappedLooplinks, new Comparator<SearchProteinLooplinkWrapper>() {
//
//					@Override
//					public int compare(SearchProteinLooplinkWrapper o1,
//							SearchProteinLooplinkWrapper o2) {
//
//						if ( o1.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId()
//								!= o2.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
//							
//							return o1.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
//									- o2.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
//						}
//						
//						if ( o1.getSearchProteinLooplink().getProteinPosition1()
//								!= o2.getSearchProteinLooplink().getProteinPosition1() ) {
//							
//							return o1.getSearchProteinLooplink().getProteinPosition1() 
//									- o2.getSearchProteinLooplink().getProteinPosition1();
//						}
//
//						return o1.getSearchProteinLooplink().getProteinPosition2() 
//								- o2.getSearchProteinLooplink().getProteinPosition2();
//					}
//				} );
				//      Get Annotation data and Sort by Annotation data
				SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortLooplinksResult =
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
						.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
								searchId, 
								wrappedLooplinks, 
								peptideCutoffsAnnotationTypeDTOList, 
								psmCutoffsAnnotationTypeDTOList );
//				request.setAttribute( "peptideAnnotationDisplayNameDescriptionList", sortLooplinksResult.getPeptideAnnotationDisplayNameDescriptionList() );
//				
//				request.setAttribute( "psmAnnotationDisplayNameDescriptionList", sortLooplinksResult.getPsmAnnotationDisplayNameDescriptionList() );
				List<String> bestPeptideValuesNames = new ArrayList<>();
				List<String> bestPSMValuesNames = new ArrayList<>();
				webserviceResult.setBestPeptideValuesNames( bestPeptideValuesNames );
				webserviceResult.setBestPSMValuesNames( bestPSMValuesNames );
				for ( AnnotationDisplayNameDescription annotationDisplayNameDescription : sortLooplinksResult.getPeptideAnnotationDisplayNameDescriptionList() ) {
					bestPeptideValuesNames.add( annotationDisplayNameDescription.getDisplayName() );
				}
				for ( AnnotationDisplayNameDescription annotationDisplayNameDescription : sortLooplinksResult.getPsmAnnotationDisplayNameDescriptionList() ) {
					bestPSMValuesNames.add( annotationDisplayNameDescription.getDisplayName() );
				}
				/////////////////////////////////
				//  Copy of out wrapper for output
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					Viewer_UDR_Data_Service_Result_Item item = new Viewer_UDR_Data_Service_Result_Item();
					item.setLinkType( LOOPLINK_LINK_TYPE );
					item.setProteinSeqId_1( searchProteinLooplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					item.setProteinPos_1( searchProteinLooplink.getProteinPosition1() );
					item.setProteinSeqId_2( searchProteinLooplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					item.setProteinPos_2( searchProteinLooplink.getProteinPosition2() );
					item.setNumPeptides( searchProteinLooplink.getNumPeptides() );
					item.setNumUniquePeptides( searchProteinLooplink.getNumUniquePeptides() );
					item.setNumPSMs( searchProteinLooplink.getNumPsms() );
					List<String> bestPeptideValues = new ArrayList<>();
					List<String> bestPSMValues = new ArrayList<>();
					item.setBestPeptideValues( bestPeptideValues );
					item.setBestPSMValues( bestPSMValues );
					for ( String annValue : searchProteinLooplink.getPeptideAnnotationValueList() ) {
						bestPeptideValues.add( annValue );
					}
					for ( String annValue : searchProteinLooplink.getPsmAnnotationValueList() ) {
						bestPSMValues.add( annValue );
					}
					udrItemList.add( item );
//					looplinks.add( searchProteinLooplink );
				}
//			}
			}
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}

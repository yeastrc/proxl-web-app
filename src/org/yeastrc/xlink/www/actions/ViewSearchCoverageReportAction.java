package org.yeastrc.xlink.www.actions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;













import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinDimerSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinUnlinkedSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetCutoffPageDisplayRoot;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.forms.SearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.searcher.ProteinCoverageSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;





public class ViewSearchCoverageReportAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewSearchCoverageReportAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		try {

			// our form
			SearchViewProteinsForm form = (SearchViewProteinsForm)actionForm;
			request.setAttribute( "searchViewCrosslinkProteinForm", form );

			request.setAttribute( "strutsActionForm", form );


			// Get the session first.  
//			HttpSession session = request.getSession();

			
			int searchId = form.getSearchId();

			
			
			//   Get the project id for this search
			
			Collection<Integer> searchIds = new HashSet<>();
			
			searchIds.add( searchId );
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIds );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
				log.error( msg );

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			
			request.setAttribute( "projectId", projectId ); 

			
			
			///////////////////////
			
			
			
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			


			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );


			
			///    Done Processing Auth Check and Auth Level

			
			//////////////////////////////


			//  Jackson JSON Mapper object for JSON deserialization and serialization
			
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			
			
			//  Populate request objects for Standard Header Display
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );

			
			
			SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
			request.setAttribute( "search", search );

			//  Populate request objects for Standard Search Display
			
			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			

			
			boolean showStructureLink = true;
			
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				
				
			} else {
				
				//  Public access user:
				
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			


			
			//   Get Query JSON from the form and if not empty, deserialize it
			

			String queryJSONFromForm = form.getQueryJSON();
			
			ProteinQueryJSONRoot proteinQueryJSONRoot = null;
			
			if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {

				try {
					proteinQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, ProteinQueryJSONRoot.class );
					
				} catch ( JsonParseException e ) {
					
					String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				
				} catch ( JsonMappingException e ) {
					
					String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
					
				} catch ( IOException e ) {
					
					String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				}
				
			} else {
				
				
				//  Query JSON in the form is empty so create an empty object that will be populated.
				
				
				proteinQueryJSONRoot = new ProteinQueryJSONRoot();
				

				//  TODO  only do this if not generic
				
				CutoffValuesRootLevel cutoffValuesRootLevel =
						GetDefaultPsmPeptideCutoffs.getInstance()
						.getDefaultPsmPeptideCutoffs( searchIds );
				
				proteinQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );
				
			}


			///////////////////////////
			
			//////   Build the list of proteins to display on the page for "Exclude protein(s):" selector 
			
			//  TODO  TEMP Commented out
			
//			// all possible proteins included in this search for this type
//
//			Collection<Integer> types = new HashSet<Integer>();
//			types.add( XLinkUtils.TYPE_CROSSLINK );
//			types.add( XLinkUtils.TYPE_LOOPLINK );
//			types.add( XLinkUtils.TYPE_DIMER );
//			types.add( XLinkUtils.TYPE_MONOLINK );
//			types.add( XLinkUtils.TYPE_UNLINKED );
//
//			Collection<SearchProtein> prProteins = SearchProteinSearcher.getInstance().getProteinsWithLinkType(search, types, psmQValueCutoff, peptideQValueCutoff);
//			Collection<SearchProtein> prProteins2 = new HashSet<SearchProtein>();
//			prProteins2.addAll( prProteins );
//
//			// build a collection of protein IDs to include
//			for( SearchProtein prp : prProteins2 ) {
//
//				// did they request removal of certain taxonomy IDs?
//				if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
//					for( int tid : form.getExcludeTaxonomy() ) {
//						if( tid == prp.getNrProtein().getTaxonomyId() ) {
//							prProteins.remove( prp );
//							break;
//						}
//					}
//				}
//			}
//
//			List<SearchProtein> sortedProteins = new ArrayList<SearchProtein>();
//			sortedProteins.addAll( prProteins );
//			Collections.sort( sortedProteins, new SortSearchProtein() );
//
//			request.setAttribute( "proteins", sortedProteins );

			
			
			
			
			request.setAttribute( "queryString",  request.getQueryString() );
			request.setAttribute( "mergedQueryString", request.getQueryString().replaceAll( "searchId=", "searchIds=" ) );


			// build list of taxonomies to show in exclusion list
			request.setAttribute("taxonomies", SearchTaxonomySearcher.getInstance().getTaxonomies( search ) );



			/////////////////////////////////////////////////////////////////////////////
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
			
			
			
			
			

			//  TODO   If form.psmQValueCutoff has a value, then this is old and needs to be re-mapped to the generic "q-value" annotation

			

//			CutoffPageDisplayRoot cutoffPageDisplayRoot =
			
			GetCutoffPageDisplayRoot.getInstance().getCutoffPageDisplayRootSingleSearchId( searchId, request );

			
			CutoffValuesRootLevel cutoffValuesRootLevel = proteinQueryJSONRoot.getCutoffs();
			


//			String searchIdAsString = Integer.toString( searchId );
			
//			CutoffValuesSearchLevel cutoffValuesSearchLevel = cutoffValuesRootLevel.getSearches().get( searchIdAsString );
			
			
//			if ( cutoffValuesSearchLevel == null ) {
//				
//				String msg = "Unable to get cutoffValuesSearchLevel for search id: " + searchIdAsString;
//				log.error( msg );
//				throw new ProxlWebappDataException(msg);
//			}
			

			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIds, cutoffValuesRootLevel ); 
			
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			

			
			
			////////////////////////////

			// Get the protein coverage report data
			
			ProteinCoverageSearcher pcs = new ProteinCoverageSearcher();

			pcs.setExcludedProteinIds( proteinQueryJSONRoot.getExcludeProtein() );
			pcs.setExcludedTaxonomyIds( proteinQueryJSONRoot.getExcludeTaxonomy() );
			
			pcs.setFilterNonUniquePeptides( proteinQueryJSONRoot.isFilterNonUniquePeptides() );
			pcs.setFilterOnlyOnePSM( proteinQueryJSONRoot.isFilterOnlyOnePSM() );
			pcs.setFilterOnlyOnePeptide( proteinQueryJSONRoot.isFilterOnlyOnePeptide() );
			
			pcs.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );

			
			Collection<SearchDTO> searches = new ArrayList<SearchDTO>();
			searches.add( search );

			pcs.setSearches( searches );

			List<ProteinCoverageData> pcd = pcs.getProteinCoverageData();
			request.setAttribute( "proteinCoverageData", pcd );


			//  TODO  Build list of proteins for the protein Exclusion list
			
			{
				

				
				List<SearchProtein> proteins = new ArrayList<>();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

				Set<Integer> proteinIds = new HashSet<>();
				
				{

					List<SearchProteinCrosslinkWrapper> wrappedCrosslinksProteins = 
							SearchProteinCrosslinkSearcher.getInstance().searchOnSearchIdandCutoffs( search, searcherCutoffValuesSearchLevel );

					List<SearchProteinLooplinkWrapper> wrappedLooplinksProteins = 
							SearchProteinLooplinkSearcher.getInstance().searchOnSearchIdandCutoffs( search, searcherCutoffValuesSearchLevel );

					for( SearchProteinCrosslinkWrapper item : wrappedCrosslinksProteins ) {

						proteinIds.add( item.getSearchProteinCrosslink().getProtein1().getNrProtein().getNrseqId() );

						proteinIds.add( item.getSearchProteinCrosslink().getProtein2().getNrProtein().getNrseqId() );
					}

					for( SearchProteinLooplinkWrapper item : wrappedLooplinksProteins ) {

						proteinIds.add( item.getSearchProteinLooplink().getProtein().getNrProtein().getNrseqId() );
					}

					{
						List<SearchProteinDimerWrapper> wrappedDimerLinks = 
								SearchProteinDimerSearcher.getInstance()
								.searchOnSearchIdandCutoffs( search, searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() ) );

						for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {

							SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();

							proteinIds.add( dimer.getProtein1().getNrProtein().getNrseqId() );

							proteinIds.add( dimer.getProtein2().getNrProtein().getNrseqId() );
						}
					}

					{
						List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
								SearchProteinUnlinkedSearcher.getInstance()
								.searchOnSearchIdandCutoffs( search, searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() ) );

						for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {

							SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();

							proteinIds.add( unlinked.getProtein().getNrProtein().getNrseqId() );
						}
					}

					for ( int proteinId : proteinIds ) {

						proteins.add( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( proteinId ) ) );
					}
				}
				
				Collections.sort( proteins, new SortSearchProtein() );


				request.setAttribute( "proteins", proteins );
				
			}
			
			
			

			/////////////////////
			
			//  clear out form so value doesn't go back on the page in the form

			form.setQueryJSON( "" );

			/////////////////////
			
			////  Put Updated queryJSON on the page
			
			{
			
				try {
					
					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( proteinQueryJSONRoot );

					//  Set queryJSON in request attribute to put on page outside of form
					
					request.setAttribute( "queryJSONToForm", queryJSONToForm );

					//  Create URI Encoded JSON for passing to Image and Structure pages in hash 
					
					String queryJSONToFormURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( queryJSONToForm );

					request.setAttribute( "queryJSONToFormURIEncoded", queryJSONToFormURIEncoded );
					
				} catch ( JsonProcessingException e ) {
					
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				
				} catch ( Exception e ) {
					
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				}
			
			}
			

			return mapping.findForward( "Success" );

		} catch ( ProxlWebappDataException e ) {

			String msg = "Exception processing request data";
			
			log.error( msg, e );

			return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			

		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}
	
    public class SortSearchProtein implements Comparator<SearchProtein> {
        public int compare(SearchProtein o1, SearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }

}

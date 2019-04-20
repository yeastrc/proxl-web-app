package org.yeastrc.xlink.www.webservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.SequenceCoverageData;
import org.yeastrc.xlink.www.objects.SequenceCoverageRange;
import org.yeastrc.xlink.www.protein_coverage.ProteinSequenceCoverage;
import org.yeastrc.xlink.www.protein_coverage.ProteinSequenceCoverageFactory;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerSequenceCoverageService_Results_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerSequenceCoverageService_Results_CachedResultManager.ViewerSequenceCoverageService_Results_CachedResultManager_Result;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;


@Path("/sequenceCoverage")
public class ViewerSequenceCoverageService {
	
	private static final Logger log = LoggerFactory.getLogger( ViewerSequenceCoverageService.class);

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	public static final int VERSION_FOR_CACHING = 1;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDataForProtein") 
	public byte[] getSequenceCoverageDataForProtein( 
			@QueryParam( "projectSearchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "proteinSequenceVersionId" ) List<Integer> proteinSequenceVersionIdList,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchId is null or empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( proteinSequenceVersionIdList == null || proteinSequenceVersionIdList.isEmpty() ) {
			String msg = "Provided proteinId is null or empty";
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
		try {
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			String requestQueryString = request.getQueryString();
			
			List<Integer> projectSearchIdListDedupedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdListDedupedSorted );
			
			ViewerSequenceCoverageService_Results_CachedResultManager_Result cachedResultManager_Result =
					ViewerSequenceCoverageService_Results_CachedResultManager.getSingletonInstance()
					.retrieveDataFromCache( projectSearchIdListDedupedSorted, requestQueryString );
			
			if ( cachedResultManager_Result != null ) {

				byte[] resultsAsBytes = cachedResultManager_Result.getChartJSONAsBytes();
				if ( resultsAsBytes != null ) {
					
					//  Use JSON cached to disk
					return resultsAsBytes;  //  EARLY EXIT
				}
			}
			
			//   Get PSM and Peptide Cutoff data from JSON
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
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
							searchIdsSet, cutoffValuesRootLevel ); 
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();

			Map<Integer, Double> coverages = new HashMap<Integer, Double>();
			Map<Integer, List<SequenceCoverageRange>> ranges = new HashMap<Integer, List<SequenceCoverageRange>>();
			SequenceCoverageData scd = new SequenceCoverageData();
			
			List<ProteinSequenceVersionObject> proteinSequenceVersionObjectList = new ArrayList<>( proteinSequenceVersionIdList.size() );
			for ( Integer proteinId : proteinSequenceVersionIdList ) {
				ProteinSequenceVersionObject protein = ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinId );
				proteinSequenceVersionObjectList.add(protein);
			}
			
			Map<Integer, ProteinSequenceCoverage> proteinSequenceCoveragesKeyedOnProtSeqIdMap = 
					ProteinSequenceCoverageFactory.getInstance()
					.getProteinSequenceCoveragesForProteins( proteinSequenceVersionObjectList, searchList, searcherCutoffValuesRootLevel );
			
			for ( Map.Entry<Integer, ProteinSequenceCoverage> entry : proteinSequenceCoveragesKeyedOnProtSeqIdMap.entrySet() ) { 
				Integer proteinSequenceVersionId = entry.getKey();
				ProteinSequenceCoverage cov = entry.getValue();
				coverages.put( proteinSequenceVersionId, cov.getSequenceCoverage() );
				Set<Range<Integer>> coverageRanges = cov.getRanges();
				List<SequenceCoverageRange> sequenceCoverageRangesTempList = new ArrayList<SequenceCoverageRange>( coverageRanges.size() );
				for( Range<Integer> r : cov.getRanges() ) {
					SequenceCoverageRange scr = new SequenceCoverageRange();
					scr.setStart( r.lowerEndpoint() );
					scr.setEnd( r.upperEndpoint() );
					sequenceCoverageRangesTempList.add( scr );
				}
				Collections.sort( sequenceCoverageRangesTempList, new Comparator<SequenceCoverageRange>() {
					@Override
					public int compare(SequenceCoverageRange o1, SequenceCoverageRange o2) {
						return o1.getStart() - o2.getStart();
					}
				} );
				List<SequenceCoverageRange> sequenceCoverageRangesOutputList = new ArrayList<SequenceCoverageRange>( coverageRanges.size() );
				SequenceCoverageRange prevSequenceCoverageRange = null;
				for ( SequenceCoverageRange sequenceCoverageRange : sequenceCoverageRangesTempList ) {
					if ( prevSequenceCoverageRange == null ) {
						prevSequenceCoverageRange = sequenceCoverageRange;
					} else {
						if ( ( prevSequenceCoverageRange.getEnd() + 1 ) == sequenceCoverageRange.getStart() ) {
							//  adjoining ranges so combine them
							prevSequenceCoverageRange.setEnd( sequenceCoverageRange.getEnd() );
						} else {
							//  NON adjoining ranges so add prev to list and move current to prev
							sequenceCoverageRangesOutputList.add( prevSequenceCoverageRange );
							prevSequenceCoverageRange = sequenceCoverageRange;
						}
					}
				}
				if ( prevSequenceCoverageRange != null ) {
					//  Add last entry
					sequenceCoverageRangesOutputList.add( prevSequenceCoverageRange );
				}
				ranges.put( proteinSequenceVersionId, sequenceCoverageRangesOutputList );
			}
			
			scd.setCoverages( coverages );
			scd.setRanges( ranges );


			byte[] resultJSONasBytes = getResultsByteArray( scd );
			
			ViewerSequenceCoverageService_Results_CachedResultManager.getSingletonInstance()
			.saveDataToCache( projectSearchIdListDedupedSorted, resultJSONasBytes, requestQueryString );
			
			return resultJSONasBytes;
			
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}

	/**
	 * @param resultsObject
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	private byte[] getResultsByteArray( Object resultsObject ) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, resultsObject );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException. " ;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}
}

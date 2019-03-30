package org.yeastrc.xlink.www.webservices;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.ProteinSequenceAnnotationDTO;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.constants.WebServiceURLConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProteinSequenceAnnotationSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Result;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * Get Protein PDR Data for Tooltip
 *
 */
@Path("/proteinNameForTooltip")
public class ProteinNameForTooltipService {

	private static final Logger log = LoggerFactory.getLogger( ProteinNameForTooltipService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getData") 
	public ProteinNameForTooltipServiceWebserviceResponse proteinNameForTooltip( 
			@QueryParam( "searchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "proteinSequenceVersionId" ) Integer  proteinSequenceVersionId,
			@Context HttpServletRequest request )
	throws Exception {
		
		String proteinListingWebserviceBaseUrl = 
				ConfigSystemCaching.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.PROTEIN_LISTING_FROM_SEQUENCE_TAXONOMY_WEBSERVICE_URL_KEY );
		if ( proteinListingWebserviceBaseUrl == null || proteinListingWebserviceBaseUrl.isEmpty() ) {
			return new ProteinNameForTooltipServiceWebserviceResponse();
		}
		if ( ! proteinListingWebserviceBaseUrl.endsWith( "/" ) ) {
			proteinListingWebserviceBaseUrl += "/";
		}
		String proteinListingWebserviceUrl = 
				proteinListingWebserviceBaseUrl + WebServiceURLConstants.PROTEIN_LISTING_WEBSERVICE_EXTENSION; 
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided searchIds is null or empty";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( proteinSequenceVersionId == null ) {
			String msg = "Provided proteinSequenceVersionId is null";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
				log.error( msg );
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
			
			ProteinSequenceVersionObject proteinSequenceVersionObject = 
					ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinSequenceVersionId );
			String proteinSequence = proteinSequenceVersionObject.getProteinSequenceObject().getSequence();
			if ( proteinSequence == null || proteinSequence.isEmpty() ) {
				String msg = "Provided proteinSequenceVersionId is not in database:  " + proteinSequenceVersionId;
				log.error( msg );
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			
			Set<Integer> searchIdsSet = new HashSet<Integer>( );
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				Integer searchId = MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
				if ( searchId == null ) {
					String msg = "Failed to find searchId for projectSearchId:  " + projectSearchId;
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg )
							.build()
							);
				}
				searchIdsSet.add( searchId );
			}
			//   Get a taxonomy id for the protein sequence id and search id to send to the PDR
			boolean foundTaxonomyIdZero = false;
			final int taxonomyIdSmallestNonZeroInitialValue = Integer.MAX_VALUE;
			int taxonomyIdSmallestNonZero = taxonomyIdSmallestNonZeroInitialValue;
			//  Get all taxonomy ids for protein sequence id and search id
			for ( Integer searchId : searchIdsSet ) {
				TaxonomyIdsForProtSeqIdSearchId_Request taxonomyIdsForProtSeqIdSearchId_Request =
						new TaxonomyIdsForProtSeqIdSearchId_Request();
				taxonomyIdsForProtSeqIdSearchId_Request.setSearchId( searchId );
				taxonomyIdsForProtSeqIdSearchId_Request.setProteinSequenceVersionId( proteinSequenceVersionObject.getProteinSequenceVersionId() );
				TaxonomyIdsForProtSeqIdSearchId_Result taxonomyIdsForProtSeqIdSearchId_Result =
						Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId.getInstance()
						.getTaxonomyIdsForProtSeqIdSearchId_Result( taxonomyIdsForProtSeqIdSearchId_Request );
				Set<Integer> taxonomyIds = taxonomyIdsForProtSeqIdSearchId_Result.getTaxonomyIds();
				
				if ( taxonomyIds.isEmpty() ) {
					//  did not find any taxonomy id so skip to next search id
					continue;  //  EARLY CONTINUE
				}
				for ( int taxonomyIdInList : taxonomyIds ) {
					if ( taxonomyIdInList == 0 ) {
						foundTaxonomyIdZero = true;
					} else if ( taxonomyIdSmallestNonZero > taxonomyIdInList ) {
						taxonomyIdSmallestNonZero = taxonomyIdInList;
					}
				}
			}
			if ( ( ! foundTaxonomyIdZero ) && taxonomyIdSmallestNonZero == taxonomyIdSmallestNonZeroInitialValue ) {
				String msg = "Failed to find a taxonomy id for proteinSequenceVersionId: " + proteinSequenceVersionId
						+ ", all search ids: " + searchIdsSet;
				log.error( msg );
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			int taxonomyId = taxonomyIdSmallestNonZero;
			if ( ( foundTaxonomyIdZero ) && taxonomyIdSmallestNonZero == taxonomyIdSmallestNonZeroInitialValue ) {
				taxonomyId = 0;
			}
			//  Use taxonomyId to send to PDR webservice
			ProteinListingResponse proteinListingOutputResponse = null;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				HttpPost httpPost = new HttpPost( proteinListingWebserviceUrl );
				List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				nvps.add(new BasicNameValuePair("taxonomyId", Integer.toString( taxonomyId ) ) );
				nvps.add(new BasicNameValuePair("proteinSequence", proteinSequence ) );
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				HttpResponse httpResponse = httpclient.execute(httpPost);
				try {
					if ( log.isDebugEnabled() ) {
						log.debug( "httpResponse.getStatusLine(): " + httpResponse.getStatusLine() );
					}
					HttpEntity httpEntity = httpResponse.getEntity();
					// do something useful with the response body
					// and ensure it is fully consumed
					InputStream inputStreamFromHTTP = null;
					try {
						JAXBContext jaxbContext = JAXBContext.newInstance( ProteinListingResponse.class );
						Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
						inputStreamFromHTTP = httpEntity.getContent();
						Object unmarshalledObject = null;
						try {
							unmarshalledObject = unmarshaller.unmarshal( inputStreamFromHTTP );
						} catch ( Exception e ) {
							throw e;
						}
						if ( ! ( unmarshalledObject instanceof ProteinListingResponse ) ) {
							String msg = "Object unmarshalled "
									+ " cannot be cast to ProteinListingOutputResponse.  unmarshalledObject.getClass().getCanonicalName(): " + unmarshalledObject.getClass().getCanonicalName();
							log.error( msg );
							throw new ProxlWebappDataException(msg);
						}
						proteinListingOutputResponse = (ProteinListingResponse) unmarshalledObject;
					} finally {
						if ( inputStreamFromHTTP != null ) {
							try {
								inputStreamFromHTTP.close();
							} catch ( Throwable t ) {
								String msg = "Failed to close input stream from HTTP";
								log.error( msg, t );
							}
						}
					}
					EntityUtils.consume(httpEntity);
				} finally {
				}
			} finally {
				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.getConnectionManager().shutdown();
			}
			ProteinNameForTooltipServiceWebserviceResponse proteinNameForTooltipServiceWebserviceResponse = new ProteinNameForTooltipServiceWebserviceResponse();
			if ( proteinListingOutputResponse != null ) {
				proteinNameForTooltipServiceWebserviceResponse.setDataFound( proteinListingOutputResponse.isDataFound() );
				proteinNameForTooltipServiceWebserviceResponse.setName( proteinListingOutputResponse.getName() );
				proteinNameForTooltipServiceWebserviceResponse.setDescription( proteinListingOutputResponse.getDescription() );
				proteinNameForTooltipServiceWebserviceResponse.setSource( proteinListingOutputResponse.getSource() );
			}
			// add in local annotations for this protein sequence
			Collection<ProteinSequenceAnnotationDTO> annotations = ProteinSequenceAnnotationSearcher.getInstance().getProteinSequenceAnnotationsForSearchAndProtein( searchIdsSet, proteinSequenceVersionId );
			proteinNameForTooltipServiceWebserviceResponse.setAnnotations( annotations );
			return proteinNameForTooltipServiceWebserviceResponse;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}	
	}
	
	/**
	 * This has to match the returned object from the PDR webservice
	 *
	 */
	@XmlRootElement(name="ProteinListingResponse")
	public static class ProteinListingResponse {
		private boolean dataFound;
		private String name = "Not Found";
		private String description = "Not Found";
		private String source = "Not Found";
		public boolean isDataFound() {
			return dataFound;
		}
		public void setDataFound(boolean dataFound) {
			this.dataFound = dataFound;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
	}
	
	/**
	 *  Returned from proteinNameForTooltip(...) method 
	 *
	 */
	public static class ProteinNameForTooltipServiceWebserviceResponse {
		private boolean dataFound;
		private String name = "Not Found";
		private String description = "Not Found";
		private String source = "Not Found";
		private Collection<ProteinSequenceAnnotationDTO> annotations = new ArrayList<>();
		public Collection<ProteinSequenceAnnotationDTO> getAnnotations() {
			return annotations;
		}
		public void setAnnotations(Collection<ProteinSequenceAnnotationDTO> annotations) {
			this.annotations = annotations;
		}
		public boolean isDataFound() {
			return dataFound;
		}
		public void setDataFound(boolean dataFound) {
			this.dataFound = dataFound;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
	}
}

package org.yeastrc.xlink.www.webservices;


import java.io.InputStream;
import java.util.ArrayList;
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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.constants.WebServiceURLConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.TaxonomyIdsForProtSeqIdSearchIdSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


/**
 * Get Protein PDR Data for Tooltip
 *
 */
@Path("/proteinNameForTooltip")
public class ProteinNameForTooltipService {

	private static final Logger log = Logger.getLogger(ProteinNameForTooltipService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getData") 
	public ProteinNameForTooltipServiceWebserviceResponse proteinNameForTooltip( 
			@QueryParam( "searchId" ) List<Integer> searchIdsParam,
			@QueryParam( "proteinSequenceId" ) Integer  proteinSequenceId,
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
		
		
		

		if ( searchIdsParam == null || searchIdsParam.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIdsParam;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		

		if ( proteinSequenceId == null ) {

			String msg = "Provided proteinSequenceId is null";

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

			//  Dedup SearchIds

			Set<Integer> searchIdsSet = new HashSet<Integer>( );
			
			for ( int searchId : searchIdsParam ) {

				searchIdsSet.add( searchId );
			}

			//   Get the project id for this search
						
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIdsSet ) {

					msg += searchId + ", ";
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
			
			

			ProteinSequenceObject proteinSequenceObject = 
					ProteinSequenceObjectFactory.getProteinSequenceObject( proteinSequenceId );
			
			String proteinSequence = proteinSequenceObject.getSequence();

			if ( proteinSequence == null || proteinSequence.isEmpty() ) {

				String msg = "Provided proteinSequenceId is not in database:  " + proteinSequenceId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			
			
			//   Get a taxonomy id for the protein sequence id and search id to send to the PDR
			
			
			boolean foundTaxonomyIdZero = false;
			

			final int taxonomyIdSmallestNonZeroInitialValue = Integer.MAX_VALUE;
			
			
			int taxonomyIdSmallestNonZero = taxonomyIdSmallestNonZeroInitialValue;

			//  Get all taxonomy ids for protein sequence id and search id

			
			for ( Integer searchId : searchIdsSet ) {
			
				Set<Integer> taxonomyIds = TaxonomyIdsForProtSeqIdSearchIdSearcher.getInstance()
						.getTaxonomyIdsSingleSearch( proteinSequenceObject, searchId );

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

				String msg = "Failed to find a taxonomy id for proteinSequenceId: " + proteinSequenceId
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
					System.out.println(httpResponse.getStatusLine());
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
	private static class ProteinListingResponse {
		
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
	private static class ProteinNameForTooltipServiceWebserviceResponse {
		
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
	
}

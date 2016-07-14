package org.yeastrc.xlink.www.webservices;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.default_page_view.DefaultPageViewSaveOrUpdate;
import org.yeastrc.xlink.www.default_page_view.GetDefaultURLFromPageURL;
import org.yeastrc.xlink.www.dto.DefaultPageViewGenericDTO;
import org.yeastrc.xlink.www.objects.GenericWebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProteinSequenceIdForNrseqProteinIdSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;



@Path("/nrseqDataMapping")
public class NrseqDataMappingService {

	private static final Logger log = Logger.getLogger(NrseqDataMappingService.class);
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinSequenceIdsForNrseqProteinIds")
	public Map<String,String> saveOrUpdateDefaultPageView( 
			@FormParam("nrseqProteinId") List<Integer> nrseqProteinIdList, 
			@Context HttpServletRequest request ) throws Exception {


		try {

			if ( nrseqProteinIdList == null || nrseqProteinIdList.isEmpty() ) {

				String msg = "Provided nrseqProteinId is null or empty";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			
			Map<String,String> nrseqProteinIdToProteinSequenceIdMap = new HashMap<>();
			
			for ( Integer nrseqProteinId : nrseqProteinIdList ) {
				
				Integer proteinSequenceId =
						ProteinSequenceIdForNrseqProteinIdSearcher.getInstance()
						.getProteinSequenceIdForNrseqProteinIdSearcher( nrseqProteinId );
				
				if ( proteinSequenceId == null ) {

					String msg = "No protein sequence id found for nrseqProteinId: " + nrseqProteinId;

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        ); 
				}
				
				nrseqProteinIdToProteinSequenceIdMap.put( nrseqProteinId.toString(), proteinSequenceId.toString() );
			}

			return nrseqProteinIdToProteinSequenceIdMap;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}
	
	
	

	
	
}

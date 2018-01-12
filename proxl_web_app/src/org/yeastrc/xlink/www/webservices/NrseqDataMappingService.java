package org.yeastrc.xlink.www.webservices;

import java.util.HashMap;
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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.searcher.ProteinSequenceVersionIdForNrseqProteinIdSearcher;

@Path("/nrseqDataMapping")
public class NrseqDataMappingService {

	private static final Logger log = Logger.getLogger(NrseqDataMappingService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinSequenceVersionIdsForNrseqProteinIds")
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
			Map<String,String> nrseqProteinIdToproteinSequenceVersionIdMap = new HashMap<>();
			for ( Integer nrseqProteinId : nrseqProteinIdList ) {
				Integer proteinSequenceVersionId =
						ProteinSequenceVersionIdForNrseqProteinIdSearcher.getInstance()
						.getProteinSequenceVersionIdForNrseqProteinIdSearcher( nrseqProteinId );
				if ( proteinSequenceVersionId == null ) {
					String msg = "No protein sequence id found for nrseqProteinId: " + nrseqProteinId;
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        ); 
				}
				nrseqProteinIdToproteinSequenceVersionIdMap.put( nrseqProteinId.toString(), proteinSequenceVersionId.toString() );
			}
			return nrseqProteinIdToproteinSequenceVersionIdMap;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}

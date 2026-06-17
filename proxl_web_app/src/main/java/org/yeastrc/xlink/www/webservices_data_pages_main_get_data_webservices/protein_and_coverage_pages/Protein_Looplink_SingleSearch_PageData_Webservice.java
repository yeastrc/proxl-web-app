package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice.CrosslinkLooplink;

/**
 * Protein Looplink Single Search Page Main List of Proteins and statistics above the List
 *
 */
@Path("/proteinPage-Looplink-SingleSearch-MainDisplay") 
public class Protein_Looplink_SingleSearch_PageData_Webservice {

//	private static final Logger log = LoggerFactory.getLogger( Protein_crosslink_SingleSearch_PageData_Webservice.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public byte[]
		webserviceMethod( 
				byte[] requestJSONBytes,
				@Context HttpServletRequest request )
	throws Exception {
		
		return Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice.getNewInstance()
				.processRequest( requestJSONBytes, CrosslinkLooplink.LOOPLINK, request );
	}
		
}

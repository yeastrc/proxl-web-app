package org.yeastrc.xlink.www.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@Path("/noOperation") 
public class NoOperationService {

	private static final Logger log = Logger.getLogger(NoOperationService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public NoOperationResult getResult( @Context HttpServletRequest request )
	throws Exception {

		try {


			NoOperationResult noOperationResult = new NoOperationResult();
			
			return noOperationResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}


	public static class NoOperationResult {
		
		private boolean result = true;
		
		private boolean otherData;

		@XmlTransient //  still sent since Jackson does not read JAXB annotations without additional setup
		public boolean isOtherData() {
			return otherData;
		}

		public void setOtherData(boolean otherData) {
			this.otherData = otherData;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}
		
		
	}
	
}

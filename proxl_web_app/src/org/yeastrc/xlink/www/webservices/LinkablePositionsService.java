package org.yeastrc.xlink.www.webservices;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;



import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProteinSequenceDAO;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;
import org.yeastrc.xlink.www.objects.ProteinPositionPair;

@Path("/linkablePositions")
public class LinkablePositionsService {

	private static final Logger log = Logger.getLogger(LinkablePositionsService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLinkablePositionsBetweenProteins") 
	public Set<ProteinPositionPair> getLinkablePositionsBetweenChains( 
														@QueryParam("proteins") List<Integer> proteins,
														@QueryParam("linkers")List<String> linkers,
														@Context HttpServletRequest request )
	throws Exception {
		
		try {

			Set<ProteinPositionPair> positionPairs = new HashSet<ProteinPositionPair>();

			for( int protein1 : proteins ) {
				for( int protein2 : proteins ) {

					String sequence1 = null;
					String sequence2 = null;


					ProteinSequenceDTO proteinSequenceDTO_1 = 
							ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( protein1 );
					ProteinSequenceDTO proteinSequenceDTO_2 = 
							ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( protein2 );

					if ( proteinSequenceDTO_1 != null ) {
						sequence1 = proteinSequenceDTO_1.getSequence();
					}

					if ( proteinSequenceDTO_2 != null ) {
						sequence2 = proteinSequenceDTO_2.getSequence();
					}

					for( String l : linkers ) {
						ILinker linker = GetLinkerFactory.getLinkerForAbbr( l );
						if( linker == null ) {
							throw new Exception( "Invalid linker: " + l );
						}

						for( int position1 : linker.getLinkablePositions( sequence1 ) ) {
							for( int position2 : linker.getLinkablePositions( sequence2, sequence1, position1 ) ) {					
								positionPairs.add( new ProteinPositionPair( protein1, position1, protein2, position2 ) );					
							}

						}

					}

				}
			}

			return positionPairs;

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
		
}

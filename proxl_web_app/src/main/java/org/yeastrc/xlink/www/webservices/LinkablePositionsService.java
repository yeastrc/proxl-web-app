package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
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
import org.yeastrc.xlink.linkable_positions.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker_Builtin_Linker;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProteinSequenceDAO;
import org.yeastrc.xlink.www.dao.ProteinSequenceVersionDAO;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;
import org.yeastrc.xlink.www.dto.ProteinSequenceVersionDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
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
			if ( linkers == null || linkers.isEmpty() ) {

				String msg = "No 'linkers' parameter";
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE ) // This string will be passed to the client
						.build()
						);
			}

			Set<ProteinPositionPair> positionPairs = new HashSet<ProteinPositionPair>();
			
			List<ILinker_Builtin_Linker> linkerObjects = new ArrayList<>( linkers.size() );
			for( String linkerAbbr : linkers ) {
				
				ILinker_Builtin_Linker linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbr );
				
				//  linker == null is now a valid response that needs to be handled.
				
				if( linker == null ) {

					//  No ILinker linker for linkerAbbr so no Linkable positions will be computed
					
					return positionPairs;  //  EARLY RETURN
					
//					throw new Exception( "Invalid linker: " + linkerAbbr );
				}
				
				if ( linker != null ) {
					linkerObjects.add( linker );
				}
			}

			for( int proteinId1 : proteins ) {
				for( int proteinId2 : proteins ) {
					// get sequence for protein sequence version ids
					
					//  protein sequence version id 1
					ProteinSequenceVersionDTO proteinSequenceVersionDTO_1 = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId1 );
					if ( proteinSequenceVersionDTO_1 == null ) {
						String msg = "No proteinSequenceVersionDTO found for proteinId 1: " + proteinId1;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					String proteinSequence_1 = null;
					ProteinSequenceDTO proteinSequenceDTO_1 = 
							ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO_1.getproteinSequenceId() );
					if ( proteinSequenceDTO_1 != null ) {
						proteinSequence_1 = proteinSequenceDTO_1.getSequence();
					}
					
					//  protein sequence version id 2
					ProteinSequenceVersionDTO proteinSequenceVersionDTO_2 = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId2 );
					if ( proteinSequenceVersionDTO_2 == null ) {
						String msg = "No proteinSequenceVersionDTO found for proteinId 2: " + proteinId2;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					String proteinSequence_2 = null;
					ProteinSequenceDTO proteinSequenceDTO_2 = 
							ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO_2.getproteinSequenceId() );
					if ( proteinSequenceDTO_2 != null ) {
						proteinSequence_2 = proteinSequenceDTO_2.getSequence();
					}

					for ( ILinker_Builtin_Linker linkerObject : linkerObjects ) {
						for( int position1 : linkerObject.getLinkablePositions( proteinSequence_1 ) ) {
							for( int position2 : linkerObject.getLinkablePositions( proteinSequence_2, proteinSequence_1, position1 ) ) {					
								positionPairs.add( new ProteinPositionPair( proteinId1, position1, proteinId2, position2 ) );					
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

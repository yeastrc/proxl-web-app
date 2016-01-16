package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;




//import org.apache.log4j.Logger;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;
import org.yeastrc.xlink.www.objects.ChainProteinPair;
import org.yeastrc.xlink.www.objects.ProteinPositionPair;

@Path("/linkablePositions")
public class LinkablePositionsService {

	//private static final Logger log = Logger.getLogger(LinkablePositionsService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLinkablePositionsBetweenProteins") 
	public Set<ProteinPositionPair> getLinkablePositionsBetweenChains( List<ChainProteinPair> linkers,
														  @Context HttpServletRequest request )
	throws Exception {
		
		/*
		Set<ProteinPositionPair> positionPairs = new HashSet<ProteinPositionPair>();
		String sequence1 = YRC_NRSEQUtils.getSequence( protein1 );
		String sequence2 = YRC_NRSEQUtils.getSequence( protein2 );
		
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
		*/
		
		return null;
	}
		
}

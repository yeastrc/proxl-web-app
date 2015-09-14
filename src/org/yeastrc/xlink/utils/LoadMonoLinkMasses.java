package org.yeastrc.xlink.utils;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.LinkerMonolinkMassDAO;
import org.yeastrc.xlink.dto.LinkerMonolinkMassDTO;

/**
 * 
 *
 */
public class LoadMonoLinkMasses {

	/**
	 * @param linkerAbbreviation
	 * @return
	 * @throws Exception
	 */
	public static List<Double> loadMonoLinkMassesForlinkerId( int linkerId ) throws Exception {
		

		/**
		 * list of masses in linker_monolink_mass table for the 'linker id' passed in  
		 */
		List<Double> monoLinkMasses = new ArrayList<Double>();
		
		List<LinkerMonolinkMassDTO>  massList = LinkerMonolinkMassDAO.getInstance().getLinkerMonolinkMassDTOForLinkerId( linkerId );
		
		
		if ( massList == null || massList.isEmpty() ) {
			
			String msg = "No 'linker_monolink_mass' records found for 'linkerId': " + linkerId;
			throw new Exception( msg );
		}
		
		for ( LinkerMonolinkMassDTO mass : massList ) {
			
			monoLinkMasses.add( mass.getMass() );
		}
		
		return monoLinkMasses;
	}
}



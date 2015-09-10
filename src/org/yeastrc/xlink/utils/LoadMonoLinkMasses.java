package org.yeastrc.xlink.utils;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.LinkerDAO;
import org.yeastrc.xlink.dao.LinkerMonolinkMassDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
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


//CREATE TABLE `linker` (
//		  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
//		  `abbr` varchar(255) NOT NULL,
//		  `name` varchar(255) DEFAULT NULL,
//		  PRIMARY KEY (`id`),
//		  UNIQUE KEY `abbr` (`abbr`)
//		) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
//
//
//CREATE TABLE `linker_monolink_mass` (
//		  `linker_id` int(10) unsigned NOT NULL,
//		  `mass` double NOT NULL,
//		  KEY `linker_id` (`linker_id`),
//		  KEY `mass` (`mass`),
//		  CONSTRAINT `linker_monolink_mass_ibfk_1` FOREIGN KEY (`linker_id`) REFERENCES `linker` (`id`) ON DELETE CASCADE
//		) ENGINE=InnoDB DEFAULT CHARSET=latin1;

package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Protein;

/**
 * Singleton instance
 *
 */
public class GetProteinsForPeptide {


	private static final Logger log = Logger.getLogger(GetProteinsForPeptide.class);

	private static final GetProteinsForPeptide instance = new GetProteinsForPeptide();
	
	private GetProteinsForPeptide() { }
	
	/**
	 * @return Singleton instance
	 */
	public static GetProteinsForPeptide getInstance() { return instance; }
	
	
	private Map<String, List<ProteinImporterContainer>> peptideSequenceToListProteinImporterContainer_Map = new HashMap<>();

	private Map<String, ProteinImporterContainer> proteinSequenceToProteinImporterContainer_Map = new HashMap<>();

	private MatchedProteins matchedProteinsFromProxlXML;
	
	/**
	 * Return ProteinImporterContainer objects for the peptide
	 * 
	 * @param peptideDTO - 
	 * @return 
	 * @throws Exception
	 */
	public Collection<ProteinImporterContainer>  getProteinsForPeptides(

			String peptideSequence

			) throws Exception {

		if ( matchedProteinsFromProxlXML == null ) {

			String msg = "matchedProteinsFromProxlXML is null, it hasn't been set";
			log.error( msg );
			throw new IllegalStateException(msg);
		}


		List<ProteinImporterContainer> cachedproteinMatches = 
				peptideSequenceToListProteinImporterContainer_Map.get( peptideSequence );

		if ( cachedproteinMatches != null ) {

			return cachedproteinMatches;
		}

		List<ProteinImporterContainer> proteinMatches = new ArrayList<>();

		List<Protein> proteinList = matchedProteinsFromProxlXML.getProtein();

		for ( Protein proteinFromProxlXMLFile : proteinList ) {

			if ( proteinFromProxlXMLFile.getSequence().indexOf( peptideSequence ) != -1 ) {

				ProteinImporterContainer proteinImporterContainer =
						proteinSequenceToProteinImporterContainer_Map.get( proteinFromProxlXMLFile.getSequence() );

				if ( proteinImporterContainer == null ) {

					proteinImporterContainer = ProteinImporterContainer.getInstance( proteinFromProxlXMLFile );
					proteinSequenceToProteinImporterContainer_Map.put( 
							proteinFromProxlXMLFile.getSequence(), proteinImporterContainer );
				}

				proteinMatches.add( proteinImporterContainer );
			}
		}

		return proteinMatches;
	}

	
	
	public void setMatchedProteinsFromProxlXML(
			MatchedProteins matchedProteinsFromProxlXML) {
		this.matchedProteinsFromProxlXML = matchedProteinsFromProxlXML;
	}


}
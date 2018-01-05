package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.utils.PeptideProteinSequenceForProteinInference;
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
	
	//  Cached list of proteins for peptide sequence (key is peptide sequence)
	private Map<String, List<ProteinImporterContainer>> peptideSequenceToListProteinImporterContainer_Map = new HashMap<>();
	
	private Map<String, ProteinImporterContainer> proteinSequenceToProteinImporterContainer_Map = new HashMap<>();
	
	//  From input Proxl XML file, matched proteins section
	private MatchedProteins matchedProteinsFromProxlXML;

	//  Built from matchedProteinsFromProxlXML
	private List<InternalHolder_ProteinsFromMatchedProteins> proteinsFromMatchedProteinsList;
	

	
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

		// Create copy of peptide sequence for protein inference where I and L are replaced with J
		String peptideSequenceForProteinInference = 
				PeptideProteinSequenceForProteinInference.getSingletonInstance().
				convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( peptideSequence );
		
		List<ProteinImporterContainer> cachedproteinMatches = 
				peptideSequenceToListProteinImporterContainer_Map.get( peptideSequenceForProteinInference );
		if ( cachedproteinMatches != null ) {
			return cachedproteinMatches;
		}
		List<ProteinImporterContainer> proteinMatches = new ArrayList<>();
		
		for ( InternalHolder_ProteinsFromMatchedProteins holder : proteinsFromMatchedProteinsList ) {
			Protein proteinFromProxlXMLFile = holder.proteinFromProxlXML;
			String proteinSequenceForProteinInference = holder.proteinSequenceForProteinInference;
			
			//  Search protein sequence that has been converted where I and L are replaced with J
			if ( proteinSequenceForProteinInference.indexOf( peptideSequenceForProteinInference ) != -1 ) {
				
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
	
	/**
	 * Add matchedProteins From ProxlXML
	 * @param matchedProteinsFromProxlXML
	 */
	public void setMatchedProteinsFromProxlXML( MatchedProteins matchedProteinsFromProxlXML ) {
		if ( matchedProteinsFromProxlXML == null ) {
			throw new IllegalArgumentException( "setMatchedProteinsFromProxlXML(...): matchedProteinsFromProxlXML == null" );
		}
		this.matchedProteinsFromProxlXML = matchedProteinsFromProxlXML;
		
		populateList_InternalHolder_ProteinsFromMatchedProteins_from_matchedProteinsFromProxlXML();
	}
	
	/**
	 * Create holder list with protein sequences altered for protein inference, replacing I and L with J
	 */
	private void populateList_InternalHolder_ProteinsFromMatchedProteins_from_matchedProteinsFromProxlXML() {

		// Create holder list with protein sequences altered for protein inference, replacing I and L with J

		List<Protein> proteinList = matchedProteinsFromProxlXML.getProtein();
		List<InternalHolder_ProteinsFromMatchedProteins> proteinsFromMatchedProteinsList = new ArrayList<>( proteinList.size() );
		for ( Protein proteinFromProxlXMLFile : proteinList ) {
			String proteinSequenceForProteinInference = 
					PeptideProteinSequenceForProteinInference.getSingletonInstance().
					convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( proteinFromProxlXMLFile.getSequence() );
			InternalHolder_ProteinsFromMatchedProteins holder = new InternalHolder_ProteinsFromMatchedProteins();
			proteinsFromMatchedProteinsList.add( holder );
			holder.proteinFromProxlXML = proteinFromProxlXMLFile;
			holder.proteinSequenceForProteinInference = proteinSequenceForProteinInference;
		}
		this.proteinsFromMatchedProteinsList = proteinsFromMatchedProteinsList;
	}
	
	/**
	 * Internal holder of:
	 * 
	 *  Protein from Proxl XML 
	 *  protein sequence altered for protein inference, replacing I and L with J 
	 *
	 */
	private static class InternalHolder_ProteinsFromMatchedProteins {
		
		Protein proteinFromProxlXML;
		String proteinSequenceForProteinInference;
	}
	
}
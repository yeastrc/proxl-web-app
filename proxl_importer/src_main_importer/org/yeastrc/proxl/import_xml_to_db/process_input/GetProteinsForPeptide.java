package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.utils.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile;
import org.yeastrc.proxl.import_xml_to_db.utils.PeptideProteinSequenceForProteinInference;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.base.constants.IsotopeLabelsConstants;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;

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
	
	//  Cached list of proteins for peptide sequence for protein inference and isotope label id 
	//    (key is peptide sequence and isotope label id)
	private Map<PeptideOrProteinSequence_IsotopeLabelId, List<ProteinImporterContainer>> peptideSequenceAndIsotopeLabelIdToListProteinImporterContainer_Map = new HashMap<>();
	
	private Map<PeptideOrProteinSequence_IsotopeLabelId, ProteinImporterContainer> proteinSequenceToProteinImporterContainer_Map = new HashMap<>();
	
	//  From input Proxl XML file, matched proteins section
	private MatchedProteins matchedProteinsFromProxlXML;

	//  Built from matchedProteinsFromProxlXML
	private Map<Integer, List<InternalHolder_ProteinsFromMatchedProteins>> proteinsFromMatchedProteinsList_KeyedOn_IsotopeLabelId = new HashMap<>();
	

	
	/**
	 * Return ProteinImporterContainer objects for the peptide
	 * 
	 * @return 
	 * @throws Exception
	 */
	public Collection<ProteinImporterContainer>  getProteinsForPeptide(
			Peptide peptideFromProxlXMLFile,
			ReportedPeptide reportedPeptide
			) throws Exception {
		
		if ( matchedProteinsFromProxlXML == null ) {
			String msg = "matchedProteinsFromProxlXML is null, it hasn't been set";
			log.error( msg );
			throw new IllegalStateException(msg);
		}

		// Create copy of peptide sequence for protein inference where I and L are replaced with J
		String peptideSequenceForProteinInference = 
				PeptideProteinSequenceForProteinInference.getSingletonInstance().
				convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( peptideFromProxlXMLFile.getSequence() );
		
		GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result =
				GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.getInstance()
				.getIsotopeLabelIdFor_Peptide_FromProxlXMLFile( peptideFromProxlXMLFile );

		int peptide_IsotopeLabelId = result.getIsotopeLabelId();
		String peptide_IsotopeLabelString = result.getIsotopeLabelString();

		PeptideOrProteinSequence_IsotopeLabelId peptideSequenceIsotopeLabelId = new PeptideOrProteinSequence_IsotopeLabelId();
		peptideSequenceIsotopeLabelId.peptideOrProteinSequence_ForProteinInference = peptideSequenceForProteinInference;
		peptideSequenceIsotopeLabelId.isotopeLabelId = peptide_IsotopeLabelId;
		
		List<ProteinImporterContainer> cachedproteinMatches = 
				peptideSequenceAndIsotopeLabelIdToListProteinImporterContainer_Map.get( peptideSequenceIsotopeLabelId );
		if ( cachedproteinMatches != null ) {
			return cachedproteinMatches;
		}
		
		List<InternalHolder_ProteinsFromMatchedProteins> proteinsFromMatchedProteinsList = 
				proteinsFromMatchedProteinsList_KeyedOn_IsotopeLabelId.get( peptide_IsotopeLabelId );
		
		if ( proteinsFromMatchedProteinsList == null ) {
			if ( peptide_IsotopeLabelString != null ) {
				String msg = "No proteins were found in <matched_proteins> with isotope label '" + peptide_IsotopeLabelString + "'.  "
						+ "Processing Peptide with isotope label '" + peptide_IsotopeLabelString + "' and sequence: "
						+ peptideFromProxlXMLFile.getSequence()
						+ ", reported peptide string: "
						+ reportedPeptide.getReportedPeptideString();
				log.error( msg );
				throw new ProxlBaseDataException( msg );
			} else {
				String msg = "No proteins were found in <matched_proteins> with no isotope label.  "
						+ "Processing Peptide with sequence: "
						+ peptideFromProxlXMLFile.getSequence()
						+ ", reported peptide string: "
						+ reportedPeptide.getReportedPeptideString();
				log.error( msg );
				throw new ProxlBaseDataException( msg );
			}
		}
		
		List<ProteinImporterContainer> proteinMatches = new ArrayList<>();
		
		for ( InternalHolder_ProteinsFromMatchedProteins holder : proteinsFromMatchedProteinsList ) {
			Protein proteinFromProxlXMLFile = holder.proteinFromProxlXML;
			String proteinSequenceForProteinInference = holder.proteinSequenceForProteinInference;
			int protein_IsotopeLabelId = holder.protein_IsotopeLabelId;
			
			if ( peptide_IsotopeLabelId != protein_IsotopeLabelId ) {
				// Isotope labels don't match so skip.  There is a isotope label id for no label
				continue;  // EARLY CONTINUE
			}
			
			//  Search protein sequence that has been converted where I and L are replaced with J
			if ( proteinSequenceForProteinInference.indexOf( peptideSequenceForProteinInference ) != -1 ) {
				
				PeptideOrProteinSequence_IsotopeLabelId proteinSequence_IsotopeLabelId = new PeptideOrProteinSequence_IsotopeLabelId();
				proteinSequence_IsotopeLabelId.peptideOrProteinSequence_ForProteinInference =  proteinFromProxlXMLFile.getSequence();
				proteinSequence_IsotopeLabelId.isotopeLabelId = protein_IsotopeLabelId;
				
				ProteinImporterContainer proteinImporterContainer =
						proteinSequenceToProteinImporterContainer_Map.get( proteinSequence_IsotopeLabelId );
				if ( proteinImporterContainer == null ) {
					//  getIsotopeLabelIdFor_Protein_FromProxlXMLFile also called in here.  May want to pass in protein_IsotopeLabelId instead 
					proteinImporterContainer = ProteinImporterContainer.getInstance( proteinFromProxlXMLFile );
					proteinSequenceToProteinImporterContainer_Map.put( proteinSequence_IsotopeLabelId, proteinImporterContainer );
				}
				proteinMatches.add( proteinImporterContainer );
			}
		}
		
		peptideSequenceAndIsotopeLabelIdToListProteinImporterContainer_Map.put( peptideSequenceIsotopeLabelId, proteinMatches );
		
		return proteinMatches;
	}
	
	/**
	 * Add matchedProteins From ProxlXML
	 * @param matchedProteinsFromProxlXML
	 * @throws Exception 
	 */
	public void setMatchedProteinsFromProxlXML( MatchedProteins matchedProteinsFromProxlXML ) throws Exception {
		if ( matchedProteinsFromProxlXML == null ) {
			throw new IllegalArgumentException( "setMatchedProteinsFromProxlXML(...): matchedProteinsFromProxlXML == null" );
		}
		this.matchedProteinsFromProxlXML = matchedProteinsFromProxlXML;
		
		populateList_InternalHolder_ProteinsFromMatchedProteins_from_matchedProteinsFromProxlXML();
	}
	
	/**
	 * Create holder list with protein sequences altered for protein inference, replacing I and L with J
	 * @throws Exception 
	 */
	private void populateList_InternalHolder_ProteinsFromMatchedProteins_from_matchedProteinsFromProxlXML() throws Exception {

		// Create holder list with protein sequences altered for protein inference, replacing I and L with J

		List<Protein> proteinList = matchedProteinsFromProxlXML.getProtein();
		
		//  Special instance of proteinsFromMatchedProteinsList for no labels
		List<InternalHolder_ProteinsFromMatchedProteins> proteinsFromMatchedProteinsList_IsotopeLabelId_None = new ArrayList<>( proteinList.size() );
		proteinsFromMatchedProteinsList_KeyedOn_IsotopeLabelId.put( IsotopeLabelsConstants.ID_NONE, proteinsFromMatchedProteinsList_IsotopeLabelId_None );
		
		
		
		for ( Protein proteinFromProxlXMLFile : proteinList ) {
			String proteinSequenceForProteinInference = 
					PeptideProteinSequenceForProteinInference.getSingletonInstance().
					convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( proteinFromProxlXMLFile.getSequence() );

			GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result =
					GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.getInstance()
					.getIsotopeLabelIdFor_Protein_FromProxlXMLFile( proteinFromProxlXMLFile );

			int protein_IsotopeLabelId = result.getIsotopeLabelId();

			InternalHolder_ProteinsFromMatchedProteins holder = new InternalHolder_ProteinsFromMatchedProteins();
			holder.proteinFromProxlXML = proteinFromProxlXMLFile;
			holder.proteinSequenceForProteinInference = proteinSequenceForProteinInference;
			holder.protein_IsotopeLabelId = protein_IsotopeLabelId;

			if ( protein_IsotopeLabelId == IsotopeLabelsConstants.ID_NONE ) {
				// Optimize for no isotope label
				
				proteinsFromMatchedProteinsList_IsotopeLabelId_None.add( holder );
				
			} else {

				List<InternalHolder_ProteinsFromMatchedProteins> proteinsFromMatchedProteinsList = 
						proteinsFromMatchedProteinsList_KeyedOn_IsotopeLabelId.get( protein_IsotopeLabelId );
			
				if ( proteinsFromMatchedProteinsList == null ) {
					proteinsFromMatchedProteinsList = new ArrayList<>();
					proteinsFromMatchedProteinsList_KeyedOn_IsotopeLabelId.put( protein_IsotopeLabelId, proteinsFromMatchedProteinsList);
				}
				proteinsFromMatchedProteinsList.add( holder );
			}
		}
	}
	
	/**
	 * Key for Maps peptideSequenceToListProteinImporterContainer_Map and proteinSequenceToProteinImporterContainer_Map
	 *
	 */
	private static class PeptideOrProteinSequence_IsotopeLabelId {
		
		private String peptideOrProteinSequence_ForProteinInference;
		private int isotopeLabelId;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + isotopeLabelId;
			result = prime * result + ((peptideOrProteinSequence_ForProteinInference == null) ? 0 : peptideOrProteinSequence_ForProteinInference.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PeptideOrProteinSequence_IsotopeLabelId other = (PeptideOrProteinSequence_IsotopeLabelId) obj;
			if (isotopeLabelId != other.isotopeLabelId)
				return false;
			if (peptideOrProteinSequence_ForProteinInference == null) {
				if (other.peptideOrProteinSequence_ForProteinInference != null)
					return false;
			} else if (!peptideOrProteinSequence_ForProteinInference.equals(other.peptideOrProteinSequence_ForProteinInference))
				return false;
			return true;
		}
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
		int protein_IsotopeLabelId; 
	}
	
}
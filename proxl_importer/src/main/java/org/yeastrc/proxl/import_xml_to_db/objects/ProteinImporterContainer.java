package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.proxl.import_xml_to_db.dto.AnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceV2DTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceVersionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchProteinSequenceVersionAnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.utils.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile;
import org.yeastrc.proxl.import_xml_to_db.utils.PeptideProteinSequenceForProteinInference;
import org.yeastrc.proxl.import_xml_to_db.utils.ProteinAnnotationNameTruncationUtil;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;

/**
 * Encapsulates a Protein for the Importer
 * 
 * equals(...) comparisons and hashcode() Based On:
 * 
 *    proteinSequenceVersionDTO.isotopeLabelId
 *    proteinSequenceV2DTO
 *    
 *
 * Holds:
 * 
 * a ProteinSequenceVersionDTO
 * 
 * a ProteinSequenceDTO - Used for equals(...) comparisons and hashcode()
 * 
 * a list of AnnotationDTO
 * 
 * a list of SearchProteinSequenceAnnotationDTO
 *
 */

public class ProteinImporterContainer {

	private ProteinSequenceV2DTO proteinSequenceV2DTO;
	
	private String proteinSequenceForProteinInference;

	private ProteinSequenceVersionDTO proteinSequenceVersionDTO;
	
	private List<AnnotationDTO> annotationDTOList;

	private List<SearchProteinSequenceVersionAnnotationDTO> searchProteinSequenceVersionAnnotationDTOList;
	
	private Protein proteinFromProxlXMLFile;
	
	private int searchId;
	
	private boolean dataInObjectSavedToDB;
	
	/**
	 * get ProteinImporterContainer from protein in Proxl XML File
	 * @throws Exception 
	 *
	 */
	public static ProteinImporterContainer getInstance( Protein proteinFromProxlXMLFile ) throws Exception {
		
		ProteinImporterContainer proteinImporterContainer = new ProteinImporterContainer();
	
		proteinImporterContainer.proteinSequenceV2DTO = new ProteinSequenceV2DTO( proteinFromProxlXMLFile.getSequence() );
		
		proteinImporterContainer.proteinFromProxlXMLFile = proteinFromProxlXMLFile;
		
		proteinImporterContainer.proteinSequenceForProteinInference =
				PeptideProteinSequenceForProteinInference.getSingletonInstance()
				.convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( proteinFromProxlXMLFile.getSequence() );

		proteinImporterContainer.proteinSequenceVersionDTO = new ProteinSequenceVersionDTO();
		
		GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result =
				GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.getInstance().getIsotopeLabelIdFor_Protein_FromProxlXMLFile( proteinFromProxlXMLFile );
		proteinImporterContainer.proteinSequenceVersionDTO.setIsotopeLabelId( result.getIsotopeLabelId() );
		
		return proteinImporterContainer;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + proteinSequenceVersionDTO.getIsotopeLabelId();
		result = prime * result + ((proteinSequenceV2DTO == null) ? 0 : proteinSequenceV2DTO.hashCode());
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
		ProteinImporterContainer other = (ProteinImporterContainer) obj;
		if (proteinSequenceVersionDTO.getIsotopeLabelId() != other.proteinSequenceVersionDTO.getIsotopeLabelId())
			return false;
		if (proteinSequenceV2DTO == null) {
			if (other.proteinSequenceV2DTO != null)
				return false;
		} else if (!proteinSequenceV2DTO.equals(other.proteinSequenceV2DTO))
			return false;
		return true;
	}

	
	///////////////////////////////

	public ProteinSequenceV2DTO getProteinSequenceDTO() {
		return proteinSequenceV2DTO;
	}

	public void setProteinSequenceDTO(ProteinSequenceV2DTO proteinSequenceV2DTO) {
		this.proteinSequenceV2DTO = proteinSequenceV2DTO;
	}

	public List<AnnotationDTO> getAnnotationDTOList() {
		
		if ( annotationDTOList == null ) {

			List<ProteinAnnotation> proteinAnnotationList = proteinFromProxlXMLFile.getProteinAnnotation();

			if ( proteinAnnotationList != null ) {

				annotationDTOList = new ArrayList<>();

				for ( ProteinAnnotation proteinAnnotation : proteinAnnotationList ) {

					AnnotationDTO annotationDTO = new AnnotationDTO();

					String proteinAnnotationNameTruncated = ProteinAnnotationNameTruncationUtil.truncateProteinAnnotationName( proteinAnnotation.getName() );
					
					annotationDTO.setName( proteinAnnotationNameTruncated );
					annotationDTO.setDescription( proteinAnnotation.getDescription() );

					if ( proteinAnnotation.getNcbiTaxonomyId() != null ) {

						annotationDTO.setTaxonomy( proteinAnnotation.getNcbiTaxonomyId().intValue() );
					}

					annotationDTOList.add( annotationDTO );
				}
			}
		}
		
		return annotationDTOList;
	}

	public void setAnnotationDTOList(List<AnnotationDTO> annotationDTOList) {
		this.annotationDTOList = annotationDTOList;
	}

	public List<SearchProteinSequenceVersionAnnotationDTO> getSearchProteinSequenceAnnotationDTOList() {
		return searchProteinSequenceVersionAnnotationDTOList;
	}

	public void setSearchProteinSequenceAnnotationDTOList(
			List<SearchProteinSequenceVersionAnnotationDTO> searchProteinSequenceVersionAnnotationDTOList) {
		this.searchProteinSequenceVersionAnnotationDTOList = searchProteinSequenceVersionAnnotationDTOList;
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public boolean isDataInObjectSavedToDB() {
		return dataInObjectSavedToDB;
	}

	public void setDataInObjectSavedToDB(boolean dataInObjectSavedToDB) {
		this.dataInObjectSavedToDB = dataInObjectSavedToDB;
	}

	public String getProteinSequenceForProteinInference() {
		return proteinSequenceForProteinInference;
	}


	public ProteinSequenceVersionDTO getProteinSequenceVersionDTO() {
		return proteinSequenceVersionDTO;
	}


	public void setProteinSequenceVersionDTO(ProteinSequenceVersionDTO proteinSequenceVersionDTO) {
		this.proteinSequenceVersionDTO = proteinSequenceVersionDTO;
	}

}

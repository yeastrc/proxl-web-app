package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.proxl.import_xml_to_db.dto.AnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchProteinSequenceAnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.utils.ProteinAnnotationNameTruncationUtil;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;

/**
 * Encapsulates a Protein for the Importer
 *
 * Holds:
 * 
 * a ProteinSequenceDTO - Used for equals(...) comparisons and hashcode()
 * 
 * a list of AnnotationDTO
 * 
 * a list of SearchProteinSequenceAnnotationDTO
 *
 */

public class ProteinImporterContainer {

	private ProteinSequenceDTO proteinSequenceDTO;

	private List<AnnotationDTO> annotationDTOList;

	private List<SearchProteinSequenceAnnotationDTO> searchProteinSequenceAnnotationDTOList;
	
	private Protein proteinFromProxlXMLFile;
	
	private int searchId;
	
	private boolean dataInObjectSavedToDB;
	
	/**
	 * get ProteinImporterContainer from protein in Proxl XML File
	 *
	 */
	public static ProteinImporterContainer getInstance( Protein proteinFromProxlXMLFile ) {
		
		ProteinImporterContainer proteinImporterContainer = new ProteinImporterContainer();
	
		
		proteinImporterContainer.proteinSequenceDTO = new ProteinSequenceDTO( proteinFromProxlXMLFile.getSequence() );
		
		proteinImporterContainer.proteinFromProxlXMLFile = proteinFromProxlXMLFile;
		
		
		return proteinImporterContainer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((proteinSequenceDTO == null) ? 0 : proteinSequenceDTO
						.hashCode());
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
		if (proteinSequenceDTO == null) {
			if (other.proteinSequenceDTO != null)
				return false;
		} else if (!proteinSequenceDTO.equals(other.proteinSequenceDTO))
			return false;
		return true;
	}

	public ProteinSequenceDTO getProteinSequenceDTO() {
		return proteinSequenceDTO;
	}

	public void setProteinSequenceDTO(ProteinSequenceDTO proteinSequenceDTO) {
		this.proteinSequenceDTO = proteinSequenceDTO;
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

	public List<SearchProteinSequenceAnnotationDTO> getSearchProteinSequenceAnnotationDTOList() {
		return searchProteinSequenceAnnotationDTOList;
	}

	public void setSearchProteinSequenceAnnotationDTOList(
			List<SearchProteinSequenceAnnotationDTO> searchProteinSequenceAnnotationDTOList) {
		this.searchProteinSequenceAnnotationDTOList = searchProteinSequenceAnnotationDTOList;
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


}

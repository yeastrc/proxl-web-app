package org.yeastrc.proxl.import_xml_to_db.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants;
import org.yeastrc.proxl.import_xml_to_db.dto.AnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceV2DTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceVersionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchProteinSequenceVersionAnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;

/**
 * DAO for class ProteinImporterContainer which is not tied to a specific table
 *
 */
public class ProteinImporterContainerDAO {
	
	private static final Logger log = Logger.getLogger(ProteinImporterContainerDAO.class);
	private ProteinImporterContainerDAO() { }
	public static ProteinImporterContainerDAO getInstance() { return new ProteinImporterContainerDAO(); }
	
	/**
	 * @param proteinImporterContainer
	 * @throws Exception
	 */
	public void saveProteinImporterContainerIfNeeded( ProteinImporterContainer proteinImporterContainer ) throws Exception {
		if ( proteinImporterContainer.getSearchId() == 0 ) {
			String msg = "proteinImporterContainer.getSearchId() == 0, search id is not set.";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( proteinImporterContainer.isDataInObjectSavedToDB() ) {
			//  Exit since data already saved
			return;  //  EARLY RETURN
		}
		ProteinSequenceV2DTO proteinSequenceV2DTO = proteinImporterContainer.getProteinSequenceDTO();
		if ( proteinSequenceV2DTO.getId() == DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants.DB_AUTO_INC_FIELD_INITIAL_VALUE_FOR_NOT_INSERTED_YET ) {
			//  Protein sequence id zero indicates it has not been saved so save it to get the id.  New object returned. 
			proteinSequenceV2DTO = 
					ProteinSequenceV2DAO.getInstance().getProteinSequenceDTO_InsertIfNotInDB( proteinSequenceV2DTO.getSequence() );
			proteinImporterContainer.setProteinSequenceDTO( proteinSequenceV2DTO );
		}
		ProteinSequenceVersionDTO proteinSequenceVersionDTO = proteinImporterContainer.getProteinSequenceVersionDTO();
		if ( proteinSequenceVersionDTO.getId() == DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants.DB_AUTO_INC_FIELD_INITIAL_VALUE_FOR_NOT_INSERTED_YET ) {
			//  Protein sequence version id zero indicates it has not been saved so save it to get the id.  New object returned. 
			proteinSequenceVersionDTO.setProteinSequenceId( proteinSequenceV2DTO.getId() );
			proteinSequenceVersionDTO = 
					ProteinSequenceVersionDAO.getInstance().getProteinSequenceVersionDTO_InsertIfNotInDB( proteinSequenceVersionDTO );
			proteinImporterContainer.setProteinSequenceVersionDTO( proteinSequenceVersionDTO );
		}
		
		List<AnnotationDTO> annotationDTOList = proteinImporterContainer.getAnnotationDTOList();
		for ( AnnotationDTO annotationDTO : annotationDTOList ) {
			if ( annotationDTO.getId() == 0 ) {
				AnnotationDAO.getInstance().getAnnotationId_InsertIfNotInDB( annotationDTO );
			}
		}
		List<SearchProteinSequenceVersionAnnotationDTO> searchProteinSequenceVersionAnnotationDTOList = new ArrayList<>();
		proteinImporterContainer.setSearchProteinSequenceAnnotationDTOList( searchProteinSequenceVersionAnnotationDTOList );
		for ( AnnotationDTO annotationDTO : annotationDTOList ) {
			SearchProteinSequenceVersionAnnotationDTO searchProteinSequenceVersionAnnotationDTO = new SearchProteinSequenceVersionAnnotationDTO();
			searchProteinSequenceVersionAnnotationDTO.setSearchId( proteinImporterContainer.getSearchId() );
			searchProteinSequenceVersionAnnotationDTO.setProteinSequenceVersionId( proteinSequenceVersionDTO.getId() );
			searchProteinSequenceVersionAnnotationDTO.setAnnotationId( annotationDTO.getId() );
			SearchProteinSequenceVersionAnnotationDAO.getInstance().saveToDatabase( searchProteinSequenceVersionAnnotationDTO );
			searchProteinSequenceVersionAnnotationDTOList.add( searchProteinSequenceVersionAnnotationDTO );
		}
		proteinImporterContainer.setDataInObjectSavedToDB( true );
	}
}

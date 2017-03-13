package org.yeastrc.proxl.import_xml_to_db.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants;
import org.yeastrc.proxl.import_xml_to_db.dto.AnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchProteinSequenceAnnotationDTO;
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
		ProteinSequenceDTO proteinSequenceDTO = proteinImporterContainer.getProteinSequenceDTO();
		if ( proteinSequenceDTO.getId() == DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants.DB_AUTO_INC_FIELD_INITIAL_VALUE_FOR_NOT_INSERTED_YET ) {
			//  Protein sequence id zero indicates it has not been saved so save it to get the id.  New object returned. 
			proteinSequenceDTO = ProteinSequenceDAO.getInstance().getProteinSequenceDTO_InsertIfNotInDB( proteinSequenceDTO.getSequence() );
			proteinImporterContainer.setProteinSequenceDTO( proteinSequenceDTO );
		}
		List<AnnotationDTO> annotationDTOList = proteinImporterContainer.getAnnotationDTOList();
		for ( AnnotationDTO annotationDTO : annotationDTOList ) {
			if ( annotationDTO.getId() == 0 ) {
				AnnotationDAO.getInstance().getAnnotationId_InsertIfNotInDB( annotationDTO );
			}
		}
		List<SearchProteinSequenceAnnotationDTO> searchProteinSequenceAnnotationDTOList = new ArrayList<>();
		proteinImporterContainer.setSearchProteinSequenceAnnotationDTOList( searchProteinSequenceAnnotationDTOList );
		for ( AnnotationDTO annotationDTO : annotationDTOList ) {
			SearchProteinSequenceAnnotationDTO searchProteinSequenceAnnotationDTO = new SearchProteinSequenceAnnotationDTO();
			searchProteinSequenceAnnotationDTO.setSearchId( proteinImporterContainer.getSearchId() );
			searchProteinSequenceAnnotationDTO.setProteinSequenceId( proteinSequenceDTO.getId() );
			searchProteinSequenceAnnotationDTO.setAnnotationId( annotationDTO.getId() );
			SearchProteinSequenceAnnotationDAO.getInstance().saveToDatabase( searchProteinSequenceAnnotationDTO );
			searchProteinSequenceAnnotationDTOList.add( searchProteinSequenceAnnotationDTO );
		}
		proteinImporterContainer.setDataInObjectSavedToDB( true );
	}
}

package org.yeastrc.proxl.import_xml_to_db.peptide_protein_position;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideProteinPositionDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * 
 *
 */
public class CreateAndSave_PeptideProteinPositionDTO_Unlinked_Dimer {

	private static final Logger log = Logger.getLogger(CreateAndSave_PeptideProteinPositionDTO_Unlinked_Dimer.class);

	//  private constructor
	private CreateAndSave_PeptideProteinPositionDTO_Unlinked_Dimer() { }
	
	public static CreateAndSave_PeptideProteinPositionDTO_Unlinked_Dimer getInstance() { return new CreateAndSave_PeptideProteinPositionDTO_Unlinked_Dimer(); }
	
	
	/**
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param peptideDTO
	 * @param proteinImporterContainer
	 * @throws Exception
	 */
	public void createAndSave_PeptideProteinPositionDTO_Unlinked_Dimer(
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId,
			PeptideDTO peptideDTO,
			ProteinImporterContainer proteinImporterContainer) throws Exception {
		
		
		String proteinSequence = proteinImporterContainer.getProteinSequenceDTO().getSequence();
		
		int fromIndex = 0;
		
		int peptideIndex = 0;
				
		while ( ( peptideIndex = proteinSequence.indexOf( peptideDTO.getSequence(), fromIndex ) ) >= 0 ) {
		
			int proteinStartPosition = peptideIndex + 1;  //  Positions are 1 based
			int proteinEndPosition = proteinStartPosition + peptideDTO.getSequence().length() - 1;

			PeptideProteinPositionDTO peptideProteinPositionDTO = new PeptideProteinPositionDTO();

			peptideProteinPositionDTO.setSearchId( searchId );
			peptideProteinPositionDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			peptideProteinPositionDTO.setPeptideId( peptideDTO.getId() );
			peptideProteinPositionDTO.setProteinSequenceId( proteinImporterContainer.getProteinSequenceDTO().getId() );
			peptideProteinPositionDTO.setProteinStartPosition( proteinStartPosition );
			peptideProteinPositionDTO.setProteinEndPosition( proteinEndPosition );

			PeptideProteinPositionDTO_SaveToDB_NoDups.getInstance().peptideProteinPositionDTO_SaveToDB_NoDups( peptideProteinPositionDTO );
			
			fromIndex = peptideIndex + 1;  // for next indexOf call

		}
	}
	
	

}

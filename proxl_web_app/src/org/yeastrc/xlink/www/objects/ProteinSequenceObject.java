package org.yeastrc.xlink.www.objects;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.ProteinSequenceDAO;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;

/**
 * 
 *
 */
public class ProteinSequenceObject {

	private static final Logger log = Logger.getLogger( ProteinSequenceObject.class );
	
	/**
	 * constructor
	 */
	public ProteinSequenceObject() {}

	/**
	 * constructor
	 */
	public ProteinSequenceObject( int proteinSequenceId ) {
		
		this.proteinSequenceId = proteinSequenceId;
	}

	private int proteinSequenceId;
	private ProteinSequenceDTO proteinSequenceDTO;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getSequence() throws Exception {
		
		if ( proteinSequenceDTO == null ) {
			
			proteinSequenceDTO = ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase(proteinSequenceId);
			
			if ( proteinSequenceDTO == null ) {
				
				String msg = "Failed to retrieve protein_sequence record for id: " + proteinSequenceId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
		}
		
		return proteinSequenceDTO.getSequence();
	}
	
	
	public int getProteinSequenceId() {
		return proteinSequenceId;
	}
	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
	public ProteinSequenceDTO getProteinSequenceDTO() {
		return proteinSequenceDTO;
	}
	public void setProteinSequenceDTO(ProteinSequenceDTO proteinSequenceDTO) {
		this.proteinSequenceDTO = proteinSequenceDTO;
	}

}

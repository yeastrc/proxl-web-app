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
	public ProteinSequenceObject( int proteinSequenceVersionId ) {
		
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

	private int proteinSequenceVersionId;
	private ProteinSequenceDTO proteinSequenceDTO;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getSequence() throws Exception {
		
		if ( proteinSequenceDTO == null ) {
			
			proteinSequenceDTO = ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase(proteinSequenceVersionId);
			
			if ( proteinSequenceDTO == null ) {
				
				String msg = "Failed to retrieve protein_sequence record for id: " + proteinSequenceVersionId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
		}
		
		return proteinSequenceDTO.getSequence();
	}
	
	
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}
	public ProteinSequenceDTO getProteinSequenceDTO() {
		return proteinSequenceDTO;
	}
	public void setProteinSequenceDTO(ProteinSequenceDTO proteinSequenceDTO) {
		this.proteinSequenceDTO = proteinSequenceDTO;
	}

}

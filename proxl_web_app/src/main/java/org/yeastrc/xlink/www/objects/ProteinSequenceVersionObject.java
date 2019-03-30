package org.yeastrc.xlink.www.objects;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.ProteinSequenceVersionDAO;
import org.yeastrc.xlink.www.dto.ProteinSequenceVersionDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;

/**
 * 
 * Table protein_sequence_version and children
 * 
 * 
 */
public class ProteinSequenceVersionObject {

	private static final Logger log = LoggerFactory.getLogger(  ProteinSequenceVersionObject.class );
	/**
	 * constructor
	 */
	public ProteinSequenceVersionObject() {}

	/**
	 * constructor
	 */
	public ProteinSequenceVersionObject( int proteinSequenceVersionId ) {
		
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

	private int proteinSequenceVersionId;
	private ProteinSequenceVersionDTO proteinSequenceVersionDTO;
	private ProteinSequenceObject proteinSequenceObject;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceVersionDTO getProteinSequenceVersionDTO() throws Exception {
		
		if ( proteinSequenceVersionDTO == null ) {
			
			proteinSequenceVersionDTO = ProteinSequenceVersionDAO.getInstance().getFromId( proteinSequenceVersionId );
			
			if ( proteinSequenceVersionDTO == null ) {
				
				String msg = "Failed to retrieve protein_sequence_version record for id: " + proteinSequenceVersionId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
		}
		
		return proteinSequenceVersionDTO;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceObject getProteinSequenceObject() throws Exception {
		
		if ( proteinSequenceObject == null ) {
			
			ProteinSequenceVersionDTO proteinSequenceVersionDTOLocal = this.getProteinSequenceVersionDTO();
			
			proteinSequenceObject =
					ProteinSequenceObjectFactory.getProteinSequenceObject( proteinSequenceVersionDTOLocal.getproteinSequenceId() );
		}
		
		return proteinSequenceObject;
	}
	
	

	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}

	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

	public static Logger getLog() {
		return log;
	}

	public void setProteinSequenceVersionDTO(ProteinSequenceVersionDTO proteinSequenceVersionDTO) {
		this.proteinSequenceVersionDTO = proteinSequenceVersionDTO;
	}
	

}

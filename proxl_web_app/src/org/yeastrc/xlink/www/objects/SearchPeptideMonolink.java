package org.yeastrc.xlink.www.objects;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;


/**
 * 
 *
 */
public class SearchPeptideMonolink extends SearchPeptide_BaseCommon {
	

	private static final Logger log = Logger.getLogger(SearchPeptideMonolink.class);


	/**
	 * Constructor
	 */
	public SearchPeptideMonolink() { super(); }

	public PeptideDTO getPeptide() throws Exception {
		try {
			if( this.peptide == null ) {
				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( this.getPeptideId() );
				this.setPeptide( peptideDTO );
			}
			
			return peptide;

		} catch ( Exception e ) {
			String msg = "Exception in getPeptide()";
			log.error( msg, e );
			throw e;
		}
	}
	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}
	public int getPeptidePosition() throws Exception {
		try {
			if ( peptidePosition == -1 ) {
				String msg = "Peptide Position Not Set.  Search Id: " + this.getSearchId()
						+ ", reported peptide id: " + this.getReportedPeptideId();
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}

			return peptidePosition;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition(int peptidePosition) {
		this.peptidePosition = peptidePosition;
	}

	public int getPeptideId() {
		return peptideId;
	}

	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}


//	private boolean populatePeptidesCalled;
	
	private int peptideId = -999;
	
	private PeptideDTO peptide;
	private int peptidePosition = -1;


}

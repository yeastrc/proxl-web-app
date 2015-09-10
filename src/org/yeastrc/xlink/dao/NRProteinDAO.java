package org.yeastrc.xlink.dao;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.NRProteinDTO;

public class NRProteinDAO {
	
	private static final Logger log = Logger.getLogger(NRProteinDAO.class);

	private NRProteinDAO() { }
	private static final NRProteinDAO _INSTANCE = new NRProteinDAO();
	public static NRProteinDAO getInstance() { return _INSTANCE; }
	
	public NRProteinDTO getNrProtein( int id ) throws Exception {
	
		try {

			NRProteinDTO protein = new NRProteinDTO();
			protein.setNrseqId( id );

			return protein;


		} catch ( Exception e ) {

			log.error( "ERROR: NRProteinDAO:getNrProtein ", e );

			throw e;
		}
	}
	
	
}

package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmPerPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * Table psm_per_peptide
 *
 */
public class PsmPerPeptideDAO {

	private static final Logger log = LoggerFactory.getLogger( PsmPerPeptideDAO.class);
	private PsmPerPeptideDAO() { }
	public static PsmPerPeptideDAO getInstance() { return new PsmPerPeptideDAO(); }
	
	/**
	 * Query on the 2 foreign keys
	 * 
	 * @param psmId
	 * @param srchRepPeptPeptideId
	 * @return
	 * @throws Exception
	 */
	public PsmPerPeptideDTO getOnePsmPerPeptideDTOForPsmIdAndSrchRepPeptPeptideId( int psmId,  int srchRepPeptPeptideId ) throws Exception {
		PsmPerPeptideDTO psm_per_peptide = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM psm_per_peptide WHERE psm_id = ? AND srch_rep_pept__peptide_id = ? LIMIT 1";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			pstmt.setInt( 2, srchRepPeptPeptideId );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				psm_per_peptide = populateFromResultSet( rs );
			}
			if ( rs.next() ) {
				throw new ProxlWebappInternalErrorException( "Should Not be possible. Found > 1 record for psm_id: " + psmId + ", srch_rep_pept__peptide_id: " + srchRepPeptPeptideId );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			throw e;
		} finally {
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return psm_per_peptide;
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public PsmPerPeptideDTO populateFromResultSet( ResultSet rs) throws SQLException {
		
		PsmPerPeptideDTO psm_per_peptide = new PsmPerPeptideDTO();
		
		psm_per_peptide.setId( rs.getInt( "id" ) );
		psm_per_peptide.setPsmId( rs.getInt( "psm_id" ) );
		psm_per_peptide.setSrchRepPeptPeptideId( rs.getInt( "srch_rep_pept__peptide_id" ) );
		
		int scanId = rs.getInt( "scan_id" );
		if ( ! rs.wasNull() ) {
			psm_per_peptide.setScanId( scanId );
		}
		int charge = rs.getInt( "charge" );
		if ( ! rs.wasNull() ) {
			psm_per_peptide.setCharge( charge );
		}
		
		psm_per_peptide.setLinkerMass( rs.getBigDecimal( "linker_mass" ) );  //  Can be NULL
		int scanNumber = rs.getInt( "scan_number" );
		if ( ! rs.wasNull() ) {
			psm_per_peptide.setScanNumber( scanNumber );
		}
		int searchScanFilenameId = rs.getInt( "search_scan_filename_id" );
		if ( ! rs.wasNull() ) {
			psm_per_peptide.setSearchScanFilenameId( searchScanFilenameId );;
		}
		return psm_per_peptide;
	}
}

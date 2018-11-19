package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepIsotopeLabelLookupDTO;

/**
 * 
 *
 */
public class UnifiedRepPepIsotopeLabelLookupDAO {
	
	private static final Logger log = Logger.getLogger(UnifiedRepPepIsotopeLabelLookupDAO.class);

	private UnifiedRepPepIsotopeLabelLookupDAO() { }
	public static UnifiedRepPepIsotopeLabelLookupDAO getInstance() { return new UnifiedRepPepIsotopeLabelLookupDAO(); }

	


	/**
	 * @param rp_matched_peptide_id
	 * @return 
	 * @throws Exception
	 */
	public List<UnifiedRepPepIsotopeLabelLookupDTO> getUnifiedRepPepIsotopeLabelLookupDTOForMatchedPeptideId( int rp_matched_peptide_id ) throws Exception {


		List<UnifiedRepPepIsotopeLabelLookupDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM unified_rep_pep_isotope_label_lookup WHERE rp_matched_peptide_id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, rp_matched_peptide_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				UnifiedRepPepIsotopeLabelLookupDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select UnifiedRpDynamicModDTO, rp_matched_peptide_id: " + rp_matched_peptide_id + ", sql: " + sql;
			
			log.error( msg, e );
			
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
		
		return results;
	}
	

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private UnifiedRepPepIsotopeLabelLookupDTO populateResultObject(ResultSet rs) throws SQLException {
		
		UnifiedRepPepIsotopeLabelLookupDTO returnItem = new UnifiedRepPepIsotopeLabelLookupDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setRpMatchedPeptideId( rs.getInt( "rp_matched_peptide_id" ) );
		returnItem.setIsotopeLabelId( rs.getInt( "isotope_label_id" ) );
		
		return returnItem;
	}
	
	
}
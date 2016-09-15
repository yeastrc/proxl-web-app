package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;

/**
 * 
 *
 */
public class UnifiedRepPepDynamicModLookupDAO {
	
	private static final Logger log = Logger.getLogger(UnifiedRepPepDynamicModLookupDAO.class);

	private UnifiedRepPepDynamicModLookupDAO() { }
	public static UnifiedRepPepDynamicModLookupDAO getInstance() { return new UnifiedRepPepDynamicModLookupDAO(); }

	


	/**
	 * @param rp_matched_peptide_id
	 * @return 
	 * @throws Exception
	 */
	public List<UnifiedRepPepDynamicModLookupDTO> getUnifiedRpDynamicModDTOForMatchedPeptideId( int rp_matched_peptide_id ) throws Exception {


		List<UnifiedRepPepDynamicModLookupDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM unified_rep_pep_dynamic_mod_lookup WHERE rp_matched_peptide_id = ? ORDER BY position, mod_order";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, rp_matched_peptide_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				UnifiedRepPepDynamicModLookupDTO item = populateResultObject( rs );
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
	private UnifiedRepPepDynamicModLookupDTO populateResultObject(ResultSet rs) throws SQLException {
		
		UnifiedRepPepDynamicModLookupDTO returnItem = new UnifiedRepPepDynamicModLookupDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setRpMatchedPeptideId( rs.getInt( "rp_matched_peptide_id" ) );
		returnItem.setPosition( rs.getInt( "position" ) );
		returnItem.setMass( rs.getDouble( "mass" ) );
		returnItem.setMassRounded( rs.getDouble( "mass_rounded" ) );
		returnItem.setMassRoundedString( rs.getString( "mass_rounded_string" ) );
		returnItem.setMassRoundingPlaces( rs.getInt( "mass_rounding_places" ) );
		returnItem.setModOrder( rs.getInt( "mod_order" ) );
		
		return returnItem;
	}
	
	
}
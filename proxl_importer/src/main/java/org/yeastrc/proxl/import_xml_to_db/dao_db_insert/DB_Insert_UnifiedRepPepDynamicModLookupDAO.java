package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;

public class DB_Insert_UnifiedRepPepDynamicModLookupDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_UnifiedRepPepDynamicModLookupDAO.class);

	private DB_Insert_UnifiedRepPepDynamicModLookupDAO() { }
	public static DB_Insert_UnifiedRepPepDynamicModLookupDAO getInstance() { return new DB_Insert_UnifiedRepPepDynamicModLookupDAO(); }
	

//	/**
//	 * @param dmod
//	 * @throws Exception
//	 */
//	public void save( UnifiedRepPepDynamicModLookupDTO dmod ) throws Exception {
//		
//		Connection conn = null;
//
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//			
//			save( dmod, conn );
//			
//		} catch ( Exception e ) {
//			
//			
//			throw e;
//			
//
//		} finally {
//			
//			// be sure database handles are closed
//		
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//	}
	

	private static String SAVE_SQL = "INSERT INTO unified_rep_pep_dynamic_mod_lookup "
			+ " (rp_matched_peptide_id, position, mass, mass_rounded, mass_rounded_string, mass_rounding_places, mod_order ) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?)";

	
	/**
	 * @param dmod
	 * @throws Exception
	 */
	public void save( UnifiedRepPepDynamicModLookupDTO dmod, Connection conn ) throws Exception {
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = SAVE_SQL;
		

		try {
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  dmod.getRpMatchedPeptideId() );
			counter++;
			pstmt.setInt( counter,  dmod.getPosition() );
			counter++;
			pstmt.setDouble( counter,  dmod.getMass() );
			counter++;
			pstmt.setDouble( counter,  dmod.getMassRounded() );
			counter++;
			pstmt.setString( counter,  dmod.getMassRoundedString() );
			counter++;
			pstmt.setInt( counter,  dmod.getMassRoundingPlaces() );
			counter++;
			pstmt.setInt( counter,  dmod.getModOrder() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				dmod.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert UnifiedRpDynamicModDTO" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR: sql: " + sql;
			
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
			
		}
		
	}
}

package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepIsotopeLabelLookupDTO;

/**
 * table unified_rep_pep_isotope_label_lookup 
 *
 */
public class DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO.class);

	private DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO() { }
	public static DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO getInstance() { return new DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO(); }
	

	private static String SAVE_SQL = "INSERT INTO unified_rep_pep_isotope_label_lookup "
			+ " (rp_matched_peptide_id, isotope_label_id ) "
			+ " VALUES (?, ?)";

	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( UnifiedRepPepIsotopeLabelLookupDTO item, Connection conn ) throws Exception {
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = SAVE_SQL;
		

		try {
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getRpMatchedPeptideId() );
			counter++;
			pstmt.setInt( counter,  item.getIsotopeLabelId() );

			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert UnifiedRepPepIsotopeLabelLookupDTO" );
			
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

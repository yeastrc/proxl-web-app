package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;



/**
 * table unified_reported_peptide_lookup
 *
 */
public class DB_Insert_UnifiedReportedPeptideLookupDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_UnifiedReportedPeptideLookupDAO.class);

	private DB_Insert_UnifiedReportedPeptideLookupDAO() { }
	public static DB_Insert_UnifiedReportedPeptideLookupDAO getInstance() { return new DB_Insert_UnifiedReportedPeptideLookupDAO(); }
	

	/**
	 * @param unifiedReportedPeptide
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedReportedPeptideLookupDTO unifiedReportedPeptide, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO unified_reported_peptide_lookup (unified_sequence, link_type, has_dynamic_modifictions ) VALUES (?,?,?)";

		try {
			
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, unifiedReportedPeptide.getUnifiedSequence() );
			pstmt.setString( 2, unifiedReportedPeptide.getLinkTypeString() );
			pstmt.setBoolean( 3, unifiedReportedPeptide.isHasMods() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				unifiedReportedPeptide.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert unified_reported_peptide_lookup for " + unifiedReportedPeptide.getUnifiedSequence() );
			
			
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
			
		}
		
		
	}
}

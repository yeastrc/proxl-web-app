package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmDTO;

/**
 * table psm
 *
 */
public class DB_Insert_PsmDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_PsmDAO.class);

	private DB_Insert_PsmDAO() { }
	public static DB_Insert_PsmDAO getInstance() { return new DB_Insert_PsmDAO(); }


	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void saveToDatabase(PsmDTO item ) throws Exception {

		Connection dbConnection = null;

		try {

//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			dbConnection = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			saveToDatabase( item, dbConnection );
			
		} finally {

//			if (dbConnection != null) {
//				try {
//					dbConnection.close();
//				} catch (Exception ex) {
//					// ignore
//				}
//			}
		}
	}
	
	private static final String INSERT_SQL =
			
			"INSERT INTO psm "
			+ "( search_id, scan_id, charge, linker_mass, reported_peptide_id,"
			+ " scan_number, search_scan_filename_id,"
			+ " precursor_retention_time, precursor_m_z ) "
			+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	
	/**
	 * @param psm
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( PsmDTO psm, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;
		

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

						
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, psm.getSearchId() );

			counter++;
			
			if ( psm.getScanId() != null ) {
				pstmt.setInt( counter, psm.getScanId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			
			counter++;
			pstmt.setInt( counter, psm.getCharge() );

			counter++;
			pstmt.setBigDecimal( counter, psm.getLinkerMass() );

			counter++;
			pstmt.setInt( counter, psm.getReportedPeptideId() );
			

			counter++;
			
			if ( psm.getScanNumber() != null ) {
				pstmt.setInt( counter, psm.getScanNumber() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			

			counter++;
			
			if ( psm.getSearchScanFilenameId()!= null ) {
				pstmt.setInt( counter, psm.getSearchScanFilenameId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			counter++;
			pstmt.setBigDecimal( counter, psm.getPrecursor_RetentionTime() );

			counter++;
			pstmt.setBigDecimal( counter, psm.getPrecursor_MZ() );

			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				psm.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert psm..." );
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
	}
}

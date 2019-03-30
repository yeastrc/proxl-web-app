package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.dto.PsmPerPeptideDTO;

/**
 * table psm_per_peptide
 *
 */
public class DB_Insert_PsmPerPeptideDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_PsmPerPeptideDAO.class);

	private DB_Insert_PsmPerPeptideDAO() { }
	public static DB_Insert_PsmPerPeptideDAO getInstance() { return new DB_Insert_PsmPerPeptideDAO(); }


	/**
	 * This will INSERT the given PsmPerPeptideDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmPerPeptideDTO item ) throws Exception {
		
		Connection dbConnection = null;
		try {
//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			dbConnection = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			saveToDatabase( item, dbConnection );

		} finally {
//			if( dbConnection != null ) {
//				try { dbConnection.close(); } catch( Throwable t ) { ; }
//				dbConnection = null;
//			}
		}
	}
		
	private final static String INSERT_SQL = 
			"INSERT INTO psm_per_peptide "
			
			+ "( psm_id, srch_rep_pept__peptide_id, scan_id, charge, linker_mass, scan_number, search_scan_filename_id ) "
			
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
	

	/**
	 * This will INSERT the given PsmPerPeptideDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmPerPeptideDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getPsmId() );
			counter++;
			pstmt.setInt( counter, item.getSrchRepPeptPeptideId() );
			
			counter++;
			if ( item.getScanId() != null ) {
				pstmt.setInt( counter, item.getScanId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			counter++;
			if ( item.getCharge()!= null ) {
				pstmt.setInt( counter, item.getCharge() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			counter++;
			pstmt.setBigDecimal( counter, item.getLinkerMass() );

			counter++;
			if ( item.getScanNumber() != null ) {
				pstmt.setInt( counter, item.getScanNumber() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			counter++;
			if ( item.getSearchScanFilenameId() != null ) {
				pstmt.setInt( counter, item.getSearchScanFilenameId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			pstmt.executeUpdate();
			
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert PsmPerPeptideDTO for psmId: " + item.getPsmId() );
			
			
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

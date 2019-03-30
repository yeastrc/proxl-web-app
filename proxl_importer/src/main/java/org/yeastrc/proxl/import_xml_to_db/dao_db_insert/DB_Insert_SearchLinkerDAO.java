package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLinkerDTO;

/**
 * Table search_linker_tbl
 *
 */
public class DB_Insert_SearchLinkerDAO {
	
	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchLinkerDAO.class);

	private DB_Insert_SearchLinkerDAO() { }
	public static DB_Insert_SearchLinkerDAO getInstance() { return new DB_Insert_SearchLinkerDAO(); }
	

	
	/**
	 * This will INSERT the given SearchDTO into the database... 
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveToDatabase( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		
		
	/**
	 * This will INSERT the given SearchLinkerDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rsGenKeys = null;

		final String sql = "INSERT INTO search_linker_tbl (search_id, linker_abbr, spacer_arm_length, spacer_arm_length_string ) VALUES ( ?, ?, ?, ? )";
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getLinkerAbbr() );
			counter++;
			if ( item.getSpacerArmLength() != null ) {
				pstmt.setDouble( counter, item.getSpacerArmLength() );
			} else {
				pstmt.setNull( counter, java.sql.Types.DOUBLE );
			}
			counter++;
			pstmt.setString( counter, item.getSpacerArmLengthString() );
			
			pstmt.executeUpdate();

			rsGenKeys = pstmt.getGeneratedKeys();
			if ( rsGenKeys.next() ) {
				item.setId( rsGenKeys.getInt( 1 ) );
			}

			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rsGenKeys != null ) {
				try { rsGenKeys.close(); } catch( Throwable t ) { ; }
				rsGenKeys = null;
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

package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLinkerPerSideLinkableProteinTerminiDTO;

/**
 * Table search_linker_per_side_linkable_protein_termini_tbl
 *
 */
public class DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO {
	
	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO.class);

	private DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO() { }
	public static DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO getInstance() { return new DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO(); }

	/**
	 * This will INSERT the given SearchLinkerPerSideLinkableProteinTerminiDTO into the database... 
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerPerSideLinkableProteinTerminiDTO item ) throws Exception {
		
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
	 * This will INSERT the given SearchLinkerPerSideLinkableProteinTerminiDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerPerSideLinkableProteinTerminiDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rsGenKeys = null;

		final String sql = "INSERT INTO search_linker_per_side_linkable_protein_termini_tbl (search_linker_per_side_definition_id, n_terminus_c_terminus, distance_from_terminus) VALUES ( ?, ?, ? )";

		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchLinkerPerSideDefinitionId() );
			counter++;
			pstmt.setString( counter, item.getProteinTerminus_c_n().value() );
			counter++;
			pstmt.setInt( counter, item.getDistanceFromTerminus() );
			
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

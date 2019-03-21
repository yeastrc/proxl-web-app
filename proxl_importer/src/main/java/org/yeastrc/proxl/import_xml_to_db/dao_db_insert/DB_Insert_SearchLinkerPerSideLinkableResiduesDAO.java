package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLinkerPerSideLinkableResiduesDTO;

/**
 * Table search_linker_per_side_linkable_residues_tbl
 *
 */
public class DB_Insert_SearchLinkerPerSideLinkableResiduesDAO {
	
	private static final Logger log = Logger.getLogger(DB_Insert_SearchLinkerPerSideLinkableResiduesDAO.class);

	private DB_Insert_SearchLinkerPerSideLinkableResiduesDAO() { }
	public static DB_Insert_SearchLinkerPerSideLinkableResiduesDAO getInstance() { return new DB_Insert_SearchLinkerPerSideLinkableResiduesDAO(); }

	/**
	 * This will INSERT the given SearchLinkerPerSideLinkableResiduesDTO into the database... 
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerPerSideLinkableResiduesDTO item ) throws Exception {
		
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
	 * This will INSERT the given SearchLinkerPerSideLinkableResiduesDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerPerSideLinkableResiduesDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rsGenKeys = null;

		final String sql = "INSERT INTO search_linker_per_side_linkable_residues_tbl (search_linker_per_side_definition_id, residue) VALUES ( ?, ? )";

		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchLinkerPerSideDefinitionId() );
			counter++;
			pstmt.setString( counter, item.getResidue() );
			
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

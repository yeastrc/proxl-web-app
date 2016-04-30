package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;


/**
 * table linker_per_search_monolink_mass
 *
 */
public class DB_Insert_LinkerPerSearchMonolinkMassDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_LinkerPerSearchMonolinkMassDAO.class);

	private DB_Insert_LinkerPerSearchMonolinkMassDAO() { }
	public static DB_Insert_LinkerPerSearchMonolinkMassDAO getInstance() { return new DB_Insert_LinkerPerSearchMonolinkMassDAO(); }
	

	private final String INSERT_SQL = 
			"INSERT INTO linker_per_search_monolink_mass "
			+ "(linker_id, search_id, monolink_mass_double, monolink_mass_string) "
			+ "VALUES (?, ?, ?, ?)";
	
	  
	public void save( LinkerPerSearchMonolinkMassDTO linkerPerSearchMonolinkMassDTO ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, linkerPerSearchMonolinkMassDTO.getLinkerId() );
			counter++;
			pstmt.setInt( counter, linkerPerSearchMonolinkMassDTO.getSearchId() );
			counter++;
			pstmt.setDouble( counter, linkerPerSearchMonolinkMassDTO.getMonolinkMassDouble() );
			counter++;
			pstmt.setString( counter, linkerPerSearchMonolinkMassDTO.getMonolinkMassString() );

			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				linkerPerSearchMonolinkMassDTO.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert linker_per_search_monolink_mass" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting linker_per_search_monolink_mass. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n linker_per_search_monolink_mass: " + linkerPerSearchMonolinkMassDTO
					+ "\nsql: " + sql;
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
		
	}
}

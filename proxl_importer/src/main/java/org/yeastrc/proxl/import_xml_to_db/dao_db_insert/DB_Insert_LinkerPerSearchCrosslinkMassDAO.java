package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;


/**
 * table linker_per_search_crosslink_mass_tbl
 *
 */
public class DB_Insert_LinkerPerSearchCrosslinkMassDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_LinkerPerSearchCrosslinkMassDAO.class);

	private DB_Insert_LinkerPerSearchCrosslinkMassDAO() { }
	public static DB_Insert_LinkerPerSearchCrosslinkMassDAO getInstance() { return new DB_Insert_LinkerPerSearchCrosslinkMassDAO(); }
	

	private final String INSERT_SQL = 
			"INSERT INTO linker_per_search_crosslink_mass_tbl "
			+ "(search_linker_id, search_id, crosslink_mass_double, crosslink_mass_string, chemical_formula) "
			+ "VALUES (?, ?, ?, ?, ?)";
	
	  
	/**
	 * @param linkerPerSearchCrosslinkMassDTO
	 * @throws Exception
	 */
	public void save( LinkerPerSearchCrosslinkMassDTO linkerPerSearchCrosslinkMassDTO ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, linkerPerSearchCrosslinkMassDTO.getSearchLinkerId() );
			counter++;
			pstmt.setInt( counter, linkerPerSearchCrosslinkMassDTO.getSearchId() );
			counter++;
			pstmt.setDouble( counter, linkerPerSearchCrosslinkMassDTO.getCrosslinkMassDouble() );
			counter++;
			pstmt.setString( counter, linkerPerSearchCrosslinkMassDTO.getCrosslinkMassString() );
			counter++;
			pstmt.setString( counter, linkerPerSearchCrosslinkMassDTO.getChemicalFormula() );

			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				linkerPerSearchCrosslinkMassDTO.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert linker_per_search_crosslink_mass_tbl" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting linker_per_search_crosslink_mass_tbl. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n linker_per_search_crosslink_mass_tbl: " + linkerPerSearchCrosslinkMassDTO
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
